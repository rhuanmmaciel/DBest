package gui.frames;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.kordamp.ikonli.dashicons.Dashicons;
import org.kordamp.ikonli.swing.FontIcon;

import controllers.ConstantController;

import database.TuplesExtractor;

import engine.info.Parameters;

import entities.Column;
import entities.cells.Cell;
import entities.cells.OperationCell;

import gui.utils.JTableUtils;

import sgbd.info.Query;
import sgbd.query.Operator;

public class DataFrame extends JDialog implements ActionListener {

    private final JLabel lblText = new JLabel();

    private final JLabel lblPages = new JLabel();

    private final JTable table = new JTable();

    private final JButton btnLeft = new JButton();

    private final JButton btnRight = new JButton();

    private final JButton btnAllLeft = new JButton();

    private final JButton btnAllRight = new JButton();

    private final JButton btnStats = new JButton();

    private final JPanel tablePanel = new JPanel(new BorderLayout());

    private JScrollPane scrollPanel;

    private final JTextPane textPane = new JTextPane();

    private FontIcon iconStats;

    private final long INITIAL_PK_SEARCH = Query.PK_SEARCH;

    private final long INITIAL_SORT_TUPLES = Query.SORT_TUPLES;

    private final long INITIAL_COMPARE_FILTER = Query.COMPARE_FILTER;

    private final long INITIAL_COMPARE_JOIN = Query.COMPARE_JOIN;

    private final long INITIAL_DISTINCT_TUPLE = Query.COMPARE_DISTINCT_TUPLE;

    private final long INITIAL_IO_SEEK_WRITE_TIME = Parameters.IO_SEEK_WRITE_TIME;

    private final long INITIAL_IO_WRITE_TIME = Parameters.IO_WRITE_TIME;

    private final long INITIAL_IO_SEEK_READ_TIME = Parameters.IO_SEEK_READ_TIME;

    private final long INITIAL_IO_READ_TIME = Parameters.IO_READ_TIME;

    private final long INITIAL_IO_SYNC_TIME = Parameters.IO_SYNC_TIME;

    private final long INITIAL_IO_TOTAL_TIME = Parameters.IO_SYNC_TIME + Parameters.IO_SEEK_WRITE_TIME + Parameters.IO_READ_TIME + Parameters.IO_SEEK_READ_TIME + Parameters.IO_WRITE_TIME;

    private final long INITIAL_BLOCK_LOADED = Parameters.BLOCK_LOADED;

    private final long INITIAL_BLOCK_SAVED = Parameters.BLOCK_SAVED;

    private final List<Map<String, String>> rows;

    private final List<String> columnsName;

    private int currentIndex;

    private final Operator operator;

    private Integer lastPage = null;

    private int currentLastPage = -1;

    public DataFrame(Cell cell) {

        super((Window) null, ConstantController.getString("dataframe"));
        this.setModal(true);

        if (cell instanceof OperationCell operationCell) {
            this.lblText.setText(operationCell.getType().displayName + ":");
        } else {
            this.lblText.setText(cell.getName() + ":");
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

    private void setIcons() {
        int buttonsSize = 15;

        FontIcon iconLeft = FontIcon.of(Dashicons.CONTROLS_BACK);
        iconLeft.setIconSize(buttonsSize);
        this.btnLeft.setIcon(iconLeft);

        FontIcon iconRight = FontIcon.of(Dashicons.CONTROLS_FORWARD);
        iconRight.setIconSize(buttonsSize);
        this.btnRight.setIcon(iconRight);

        FontIcon iconAllLeft = FontIcon.of(Dashicons.CONTROLS_SKIPBACK);
        iconAllLeft.setIconSize(buttonsSize);
        this.btnAllLeft.setIcon(iconAllLeft);

        FontIcon iconAllRight = FontIcon.of(Dashicons.CONTROLS_SKIPFORWARD);
        iconAllRight.setIconSize(buttonsSize);
        this.btnAllRight.setIcon(iconAllRight);

        this.iconStats = FontIcon.of(Dashicons.BOOK);
        this.iconStats.setIconSize(buttonsSize);
        this.btnStats.setIcon(this.iconStats);
    }

    private void updateTable(int page) {
        int firstElement = page * 15;
        int lastElement = page * 15 + 14;

        DefaultTableModel model = new DefaultTableModel();

        model.addColumn("");

        this.getTuples(firstElement, page, lastElement);

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

    private void getTuples(int currentElement, int page, int lastElement) {
        if (page > this.currentLastPage) {
            Map<String, String> row = TuplesExtractor.getRow(this.operator, true);

            while (row != null && currentElement < lastElement) {
                this.rows.add(row);

                row = TuplesExtractor.getRow(this.operator, true);

                if (row != null) currentElement++;

                if (currentElement >= lastElement) {
                    this.rows.add(row);
                }
            }

            if (row == null && this.lastPage == null) {
                this.lastPage = currentElement / 15;
            }
        }
    }

    private void initializeGUI() {
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        this.setContentPane(contentPane);

        this.setIcons();
        this.btnLeft.addActionListener(this);
        this.btnAllLeft.addActionListener(this);
        this.btnRight.addActionListener(this);
        this.btnAllRight.addActionListener(this);
        this.btnStats.addActionListener(this);

        JPanel northPane = new JPanel(new FlowLayout());
        northPane.add(this.lblText);
        northPane.add(this.lblPages);
        northPane.add(this.btnStats);

        this.tablePanel.add(this.table.getTableHeader(), BorderLayout.NORTH);
        this.tablePanel.add(this.table, BorderLayout.CENTER);
        this.scrollPanel = new JScrollPane(this.tablePanel);
        this.scrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        this.scrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        this.textPane.setEditable(false);

        JPanel southPane = new JPanel(new FlowLayout());
        southPane.add(this.btnAllLeft);
        southPane.add(this.btnLeft);
        southPane.add(this.btnRight);
        southPane.add(this.btnAllRight);

        contentPane.add(northPane, BorderLayout.NORTH);
        contentPane.add(this.scrollPanel, BorderLayout.CENTER);
        contentPane.add(southPane, BorderLayout.SOUTH);

        this.verifyButtons();

        if (this.table.getRowCount() == 0) this.lblPages.setText("0/0");

        this.resize();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void resize() {
        this.pack();

        if (this.getWidth() > ConstantController.UI_SCREEN_WIDTH) {
            int height = this.getHeight();
            this.setSize((int) (ConstantController.UI_SCREEN_WIDTH * 0.95), height);
        }
    }

    private void verifyButtons() {
        this.btnLeft.setEnabled(this.currentIndex != 0);
        this.btnAllLeft.setEnabled(this.currentIndex != 0);
        this.btnRight.setEnabled(this.lastPage == null || this.lastPage != this.currentIndex);
        this.btnAllRight.setEnabled(this.lastPage == null || this.lastPage != this.currentIndex);
        this.lblPages.setText(this.currentIndex + 1 + "/" + (this.lastPage == null ? " ???" : this.lastPage + 1));
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == this.btnRight) {
            this.currentIndex++;
        } else if (event.getSource() == this.btnLeft) {
            this.currentIndex--;
        }

        if (event.getSource() == this.btnAllLeft) {
            this.currentIndex = 0;
        } else if (event.getSource() == this.btnAllRight) {
            if (this.lastPage == null) {
                while (this.lastPage == null) {
                    this.updateTable(++this.currentIndex);
                }
            } else {
                this.currentIndex = this.lastPage;
            }
        } else if (event.getSource() == this.btnStats) {
            this.alternateScreen();
        }

        this.updateTable(this.currentIndex);
        this.updateStats();
        this.verifyButtons();
    }

    private void updateStats() {
        String stats = ConstantController.getString("dataframe.query") + ":\n" + ConstantController.getString("dataframe.query.pkSearch") + " = " + (Query.PK_SEARCH - this.INITIAL_PK_SEARCH) + "\n" + ConstantController.getString("dataframe.query.sortedTuples") + " = " + (Query.SORT_TUPLES - this.INITIAL_SORT_TUPLES) + "\n" + ConstantController.getString("dataframe.query.filterComparison") + " = " + (Query.COMPARE_FILTER - this.INITIAL_COMPARE_FILTER) + "\n" + ConstantController.getString("dataframe.query.joinComparison") + " = " + (Query.COMPARE_JOIN - this.INITIAL_COMPARE_JOIN) + "\n" + ConstantController.getString("dataframe.query.distinctTuplesComparison") + " = " + (Query.COMPARE_DISTINCT_TUPLE - this.INITIAL_DISTINCT_TUPLE) + "\n\n" + ConstantController.getString("dataframe.disk") + ":" + "\n" + ConstantController.getString("dataframe.disk.IOSeekWriteTime") + " = " + (Parameters.IO_SEEK_WRITE_TIME - this.INITIAL_IO_SEEK_WRITE_TIME) / 1000000f + "ms" + "\n" + ConstantController.getString("dataframe.disk.IOWriteTime") + " = " + (Parameters.IO_WRITE_TIME - this.INITIAL_IO_WRITE_TIME) / 1000000f + "ms" + "\n" + ConstantController.getString("dataframe.disk.IOSeekReadTime") + " = " + (Parameters.IO_SEEK_READ_TIME - this.INITIAL_IO_SEEK_READ_TIME) / 1000000f + "ms" + "\n" + ConstantController.getString("dataframe.disk.IOReadTime") + " = " + (Parameters.IO_READ_TIME - this.INITIAL_IO_READ_TIME) / 1000000f + "ms" + "\n" + ConstantController.getString("dataframe.disk.IOSyncTime") + " = " + (Parameters.IO_SYNC_TIME - this.INITIAL_IO_SYNC_TIME) / 1000000f + "ms" + "\n" + ConstantController.getString("dataframe.disk.IOTime") + " = " + (Parameters.IO_SYNC_TIME + Parameters.IO_SEEK_WRITE_TIME + Parameters.IO_READ_TIME + Parameters.IO_SEEK_READ_TIME + Parameters.IO_WRITE_TIME - this.INITIAL_IO_TOTAL_TIME) / 1000000f + "ms" + "\n" + ConstantController.getString("dataframe.block.loaded") + " = " + (Parameters.BLOCK_LOADED - this.INITIAL_BLOCK_LOADED) + "\n" + ConstantController.getString("dataframe.block.saved") + " = " + (Parameters.BLOCK_SAVED - this.INITIAL_BLOCK_SAVED);

        this.textPane.setText(stats);

        this.revalidate();
    }

    private void alternateScreen() {
        if (this.scrollPanel.isAncestorOf(this.tablePanel)) {
            this.scrollPanel.setViewportView(null);
            this.scrollPanel.setViewportView(this.textPane);
            this.iconStats.setIkon(Dashicons.EDITOR_TABLE);

            this.revalidate();
            this.repaint();

            return;
        }

        this.scrollPanel.setViewportView(null);
        this.scrollPanel.setViewportView(this.tablePanel);
        this.iconStats.setIkon(Dashicons.BOOK);

        this.revalidate();
        this.repaint();
    }

    private void closeWindow() {
        this.operator.close();
        this.operator.freeResources();
        this.dispose();
    }
}

