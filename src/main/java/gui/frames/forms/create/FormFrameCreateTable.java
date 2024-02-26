package gui.frames.forms.create;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import controllers.ConstantController;
import database.TableCreator;
import entities.Column;
import entities.cells.TableCell;
import gui.frames.forms.FormBase;
import gui.frames.forms.IFormCondition;

import org.kordamp.ikonli.dashicons.Dashicons;
import org.kordamp.ikonli.materialdesign2.MaterialDesignA;
import org.kordamp.ikonli.materialdesign2.MaterialDesignD;
import org.kordamp.ikonli.swing.FontIcon;

public class FormFrameCreateTable extends FormBase implements ActionListener, DocumentListener, IFormCondition {

    private JTable table;

    private DefaultTableModel model;

    private JButton btnAddRow;

    private JButton btnAddColumn;

    private JButton btnGenerateDataFrame;

    private JButton btnGenerateData = new JButton();

    private JTextField txtFieldTableName;

    private TableCell tableCell;

    private final AtomicReference<Boolean> exitReference;

    private final List<Column> columns;

    {
        this.columns = new ArrayList<>();
        this.tableCell = null;
    }

    public FormFrameCreateTable(AtomicReference<Boolean> exitReference) {
        super(null);

        this.setModal(true);

        this.exitReference = exitReference;

        this.initGUI();

    }

    public void initGUI() {

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent event) {
                FormFrameCreateTable.this.exitReference.set(true);
            }
        });

        this.model = new DefaultTableModel();
        this.table = new JTable(this.model);

        this.btnReady.addActionListener(this);
        this.btnCancel.addActionListener(this);

        this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        this.setContentPane(this.contentPanel);

        JScrollPane scrollPane = new JScrollPane(this.table);

        this.btnAddColumn = new JButton(ConstantController.getString("createTable.addColumn"));
        this.btnAddColumn.addActionListener(this);

        this.btnAddRow = new JButton(ConstantController.getString("createTable.addRow"));
        this.btnAddRow.addActionListener(this);

        this.txtFieldTableName = new JTextField();
        this.txtFieldTableName.getDocument().addDocumentListener(this);
        this.txtFieldTableName.setColumns(10);

        JLabel tableNameLabel = new JLabel(ConstantController.getString("createTable.tableName")+": ");

        this.btnGenerateDataFrame = new JButton(ConstantController.getString("createTable.generateData"));
        this.btnGenerateDataFrame.addActionListener(this);

        FontIcon iconDice = FontIcon.of(MaterialDesignD.DICE_6);
        iconDice.setIconSize(15);

        this.btnGenerateData.setIcon(iconDice);
        this.btnGenerateData.addActionListener(this);

        Box boxMain = Box.createVerticalBox();

        Box boxTableName = Box.createHorizontalBox();
        boxTableName.add(tableNameLabel);
        boxTableName.add(txtFieldTableName);
        boxMain.add(boxTableName);

        boxMain.add(scrollPane);

        Box boxBottom = Box.createHorizontalBox();
        boxBottom.add(btnAddColumn);
        boxBottom.add(btnAddRow);
        boxBottom.add(btnGenerateDataFrame);
        boxBottom.add(btnGenerateData);
        boxMain.add(boxBottom);

        contentPanel.add(boxMain, BorderLayout.CENTER);

        this.pack();
        this.setLocationRelativeTo(null);
        this.updateButtons();
        this.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == this.btnCancel) {
            this.exitReference.set(true);
            this.dispose();
        } else if (event.getSource() == this.btnReady) {
            this.createTable();
        } else if (event.getSource() == this.btnAddColumn) {
            new FormFrameAddColumn(this.model, this.columns);
        } else if (event.getSource() == this.btnAddRow) {
            this.model.insertRow(this.table.getRowCount(), new Object[]{});
        } else if (event.getSource() == this.btnGenerateDataFrame) {
            new FormFrameGenerateData(this.columns, this.model, this.table);
        }

        this.updateButtons();
    }

    @Override
    public void insertUpdate(DocumentEvent event) {
        this.updateButtons();
    }

    @Override
    public void removeUpdate(DocumentEvent event) {
        this.updateButtons();
    }

    @Override
    public void changedUpdate(DocumentEvent event) {
        this.updateButtons();
    }

    private void updateButtons() {
        this.checkBtnReady();
        this.btnAddRow.setEnabled(this.table.getColumnCount() > 0);
        this.btnGenerateDataFrame.setEnabled(this.table.getRowCount() > 0);
        this.btnGenerateData.setEnabled(this.table.getRowCount() > 0);
        this.updateToolTipText();
    }

    private void createTable() {
        Map<Integer, Map<String, String>> content = new HashMap<>();

        for (int i = 0; i < this.table.getRowCount(); i++) {
            Map<String, String> line = new HashMap<>();

            for (int j = 0; j < this.table.getColumnCount(); j++) {
                if (this.table.getValueAt(i, j) == null || this.table.getValueAt(i, j).toString() == null) {
                    line.put(this.table.getColumnName(j), "");
                } else {
                    line.put(this.table.getColumnName(j), this.table.getValueAt(i, j).toString());
                }
            }

            content.put(i, line);
        }

        boolean exit = false;

        AtomicReference<Boolean> exitReference = new AtomicReference<>(exit);

        List<Column> rightSourceColumns = new ArrayList<>(columns.stream().map(column -> new Column(column.NAME, txtFieldTableName.getText(), column.DATA_TYPE, column.IS_PRIMARY_KEY)).toList());

        if (!exitReference.get()) {
             tableCell = TableCreator.createMemoryTable(txtFieldTableName.getText(), rightSourceColumns, content);
        } else {
            exitReference.set(true);
        }

        this.dispose();
    }

    public TableCell getResult() {
        return this.tableCell;
    }

    @Override
    public void checkBtnReady() {
        this.btnReady.setEnabled(this.table.getRowCount() > 0 && !this.txtFieldTableName.getText().isEmpty());
    }

    @Override
    public void updateToolTipText(boolean... conditions) {

        String createDataButtonToolTipText = "";
        String addRowButtonToolTipText = "";
        String createTableToolTipText = "";

        if (this.table.getColumnCount() <= 0) {
            createDataButtonToolTipText = addRowButtonToolTipText = createTableToolTipText = "- "+ConstantController.getString("createTable.toolTipText.noColumn");
        } else if (this.table.getRowCount() <= 0) {
            createDataButtonToolTipText = createTableToolTipText = "- " + ConstantController.getString("createTable.toolTipText.noRow");
        } else if (this.txtFieldTableName.getText().isEmpty()) {
            createTableToolTipText = "- "+ConstantController.getString("createTable.toolTipText.noName");
        }

        UIManager.put("ToolTip.foreground", Color.RED);

        this.btnAddRow.setToolTipText(addRowButtonToolTipText.isEmpty() ? null : addRowButtonToolTipText);
        this.btnGenerateDataFrame.setToolTipText(createDataButtonToolTipText.isEmpty() ? null : createDataButtonToolTipText);
        this.btnReady.setToolTipText(createTableToolTipText.isEmpty() ? null : createTableToolTipText);

    }
}
