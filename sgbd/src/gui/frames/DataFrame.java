package gui.frames;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import controller.ConstantController;

import entities.Column;
import entities.cells.Cell;
import entities.cells.OperationCell;

import database.TuplesExtractor;

import gui.utils.JTableUtils;

import sgbd.query.Operator;

public class DataFrame extends JDialog implements ActionListener {

    private final JLabel lblText;

    private final JLabel lblPages;

    private final JTable table;

    private final JButton btnLeft;

    private final JButton btnRight;

    private final JButton btnAllLeft;

    private final JButton btnAllRight;

    private final List<Map<String, String>> rows;

    private final List<String> columnsName;

    private int currentIndex;

    private final Operator operator;

    private Integer lastPage;

    private int currentLastPage;

    public DataFrame(Cell cell) {
        super((Window) null, "DataFrame");

        this.lblText = new JLabel();
        this.lblPages = new JLabel();
        this.table = new JTable();
        this.btnLeft = new JButton("<");
        this.btnRight = new JButton(">");
        this.btnAllLeft = new JButton("<<");
        this.btnAllRight = new JButton(">>");
        this.currentIndex = 0;
        this.currentLastPage = -1;

        this.setModal(true);

        if (cell instanceof OperationCell operationCell) {
            this.lblText.setText(String.format("%s:", operationCell.getType().displayName));
        } else {
            this.lblText.setText(String.format("%s:", cell.getName()));
        }

        this.operator = cell.getOperator();
        this.operator.open();
        this.columnsName = cell.getColumnSourcesAndNames();
        this.rows = new ArrayList<>();
        this.updateTable(0);
        this.currentIndex = 0;

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent event) {
                DataFrame.this.closeWindow();
            }
        });

        this.initializeGUI();
    }

    private void updateTable(int page) {
        int firstElement = page * 15;
        int lastElement = page * 15 + 14;
        int currentElement = firstElement;

        DefaultTableModel model = new DefaultTableModel();

        model.addColumn("");

        if (page > this.currentLastPage) {
            Map<String, String> row = TuplesExtractor.getRow(this.operator, true);

            while (row != null && currentElement < lastElement) {
                this.rows.add(row);
                row = TuplesExtractor.getRow(this.operator, true);

                if (row != null) {
                    currentElement++;
                }

                if (currentElement >= lastElement) {
                    this.rows.add(row);
                }
            }

            if (row == null && this.lastPage == null) {
                this.lastPage = currentElement / 15;
            }
        }

        this.currentLastPage = Math.max(this.currentLastPage, page);

        if (!this.rows.isEmpty()) {
            for (Map.Entry<String, List<String>> columns : this.operator.getContentInfo().entrySet()) {
                for (String columnName : columns.getValue()) {
                    model.addColumn(Column.composeSourceAndName(columns.getKey(), columnName));
                }
            }

            int i = page * 15 + 1;
            int endOfList = Math.min(lastElement + 1, this.rows.size());

            for (Map<String, String> currentRow : this.rows.subList(firstElement, endOfList)) {
                Object[] line = new Object[this.rows.get(firstElement).size() + 1];
                line[0] = i++;

                for (int j = 0; j < currentRow.size(); j++) {
                    line[j + 1] = currentRow.get(model.getColumnName(j + 1));
                }

                model.addRow(line);
            }
        } else {
            model.setColumnIdentifiers(this.columnsName.toArray());
        }

        this.table.setModel(model);

        JTableUtils.preferredColumnWidthByValues(this.table, 0);

        for (int i = 1; i < this.table.getColumnCount(); i++) {
            JTableUtils.preferredColumnWidthByColumnName(this.table, i);
        }

        this.table.getColumnModel().getColumn(0).setResizable(false);

        JTableUtils.setColumnBold(this.table, 0);
        JTableUtils.setNullInRed(this.table);

        this.table.setEnabled(false);
        this.table.setFillsViewportHeight(true);
        this.table.repaint();
    }

    private void initializeGUI() {
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        this.setContentPane(contentPane);

        this.btnLeft.addActionListener(this);
        this.btnAllLeft.addActionListener(this);
        this.btnRight.addActionListener(this);
        this.btnAllRight.addActionListener(this);

        JPanel northPane = new JPanel(new FlowLayout());
        northPane.add(this.lblText);
        northPane.add(this.lblPages);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(this.table.getTableHeader(), BorderLayout.NORTH);
        tablePanel.add(this.table, BorderLayout.CENTER);
        JScrollPane scrollPane = new JScrollPane(tablePanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        JPanel southPane = new JPanel(new FlowLayout());
        southPane.add(this.btnAllLeft);
        southPane.add(this.btnLeft);
        southPane.add(this.btnRight);
        southPane.add(this.btnAllRight);

        contentPane.add(northPane, BorderLayout.NORTH);
        contentPane.add(scrollPane, BorderLayout.CENTER);
        contentPane.add(southPane, BorderLayout.SOUTH);

        this.verifyButtons();

        if (this.table.getRowCount() == 0) {
            this.lblPages.setText("0/0");
        }

        this.resize();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void resize() {
        this.pack();

        if (this.getWidth() > ConstantController.UI_SCREEN_WIDTH) {
            this.setSize((int) (ConstantController.UI_SCREEN_WIDTH * 0.95), this.getHeight());
        }
    }

    private void verifyButtons() {
        this.btnLeft.setEnabled(this.currentIndex != 0);
        this.btnAllLeft.setEnabled(this.currentIndex != 0);
        this.btnRight.setEnabled(this.lastPage == null || this.lastPage != this.currentIndex);
        this.btnAllRight.setEnabled(this.lastPage == null || this.lastPage != this.currentIndex);
        this.lblPages.setText(String.format("%s/%s", this.currentIndex + 1, this.lastPage == null ? "???" : this.lastPage + 1));
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == this.btnRight) {
            this.currentIndex++;
        } else if (event.getSource() == this.btnLeft) {
            this.currentIndex--;
        }

        if (event.getSource() == this.btnAllLeft) {
            this.currentIndex = Math.max(this.currentIndex - 100, 0);
        } else if (event.getSource() == this.btnAllRight) {
            if (this.lastPage == null) {
                for (int i = 0; this.lastPage == null && i < 100; i++) {
                    this.updateTable(++this.currentIndex);
                }
            } else {
                this.currentIndex = this.lastPage;
            }
        }

        this.updateTable(this.currentIndex);
        this.verifyButtons();
    }

    private void closeWindow() {
        this.operator.close();
        this.operator.freeResources();
        this.dispose();
    }
}
