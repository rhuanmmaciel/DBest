package gui.frames.forms.importexport;

import controllers.ConstantController;
import controllers.MainController;
import database.TuplesExtractor;
import entities.Column;
import entities.cells.Cell;
import gui.frames.forms.FormBase;
import gui.frames.forms.IFormCondition;
import gui.utils.JTableUtils;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.*;

public class PKAndNameChooserForm extends FormBase implements ActionListener, IFormCondition {

    private final JTextField tableNameTextField = new JTextField();

    private final List<Column> selectedColumns = new ArrayList<>();

    private final Vector<Vector<Object>> rows = new Vector<>();

    private final JScrollPane scrollPane = new JScrollPane();

    private DefaultTableModel model;

    private final Map<String, JCheckBox> pkCheckBoxes = new HashMap<>();

    private final Cell cell;

    private final List<JCheckBox> pkOrder = new LinkedList<>();

    private final List<String> columnNames = new ArrayList<>();

    public PKAndNameChooserForm(Cell cell) {
        super(null);

        this.setModal(true);

        this.cell = cell;

        List<Map<String, String>> rowsAux = TuplesExtractor.getRows(cell.getOperator(), 100, true);

        for (Map<String, String> row : rowsAux) {

            Vector<Object> v = new Vector<>();

            for (Map.Entry<String, String> r : row.entrySet()) {
                v.add(r.getValue());
            }

            this.rows.add(v);
        }

        this.columnNames.addAll(rowsAux.getFirst().keySet());

        this.initGUI();
    }


    public void initGUI() {
        this.setBounds(0, 0, ConstantController.UI_SCREEN_WIDTH, ConstantController.UI_SCREEN_HEIGHT);

        this.btnReady.addActionListener(this);
        this.btnCancel.addActionListener(this);

        this.model = new JTableUtils.CustomTableModel(rows, new Vector<>(this.columnNames));
        this.model.insertRow(0, new Object[]{});

        List<JCheckBox> checkboxes = new ArrayList<>();

        for (String s : this.columnNames) {

            JCheckBox checkBox = new JCheckBox();
            checkBox.addActionListener(this);

            checkboxes.add(checkBox);
            this.pkCheckBoxes.put(s, checkBox);
        }

        this.addFirstColumn();

        JTable jTable = new JTable(this.model) {

            @Override
            public TableCellRenderer getCellRenderer(int row, int column) {
                if (row == 0 && column != 0) {
                    return new DefaultTableCellRenderer() {

                        @Override
                        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

                            return checkboxes.get(column - 1);
                        }
                    };
                }

                return super.getCellRenderer(row, column);
            }

            @Override
            public TableCellEditor getCellEditor(int row, int column) {
                if (row == 0 && column != 0) {
                    return new DefaultCellEditor(checkboxes.get(column - 1));
                }

                return super.getCellEditor(row, column);
            }
        };

        jTable.getColumnModel().moveColumn(this.model.getColumnCount() - 1, 0);
        jTable.getColumnModel().getColumn(0).setResizable(false);

        JTableUtils.setColumnBold(jTable, 0);
        JTableUtils.preferredColumnWidthByValues(jTable, 0);

        jTable.setShowHorizontalLines(true);
        jTable.setGridColor(Color.blue);
        jTable.setColumnSelectionAllowed(false);
        jTable.setRowSelectionAllowed(false);
        jTable.setCellSelectionEnabled(false);

        JTableUtils.setNullInRed(jTable);

        this.scrollPane.setViewportView(jTable);

        ((JTableUtils.CustomTableModel) this.model).setRowEnabled(0, true);

        this.contentPanel.add(this.scrollPane, BorderLayout.CENTER);

        addTextField();

        this.checkBtnReady();

        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void addTextField(){

        Box box = Box.createHorizontalBox();

        box.add(new JLabel(ConstantController.getString("createTable.tableName")+": "));
        box.add(tableNameTextField);

        this.contentPanel.add(box, BorderLayout.NORTH);

        tableNameTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkBtnReady();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkBtnReady();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkBtnReady();
            }
        });

    }

    private void addFirstColumn() {
        String firstColumnName = String.format("%s:", ConstantController.getString("pkChooser.firstColumn.name"));
        String firstColumnPrimaryKey = String.format("%s:", ConstantController.getString("pkChooser.firstColumn.primaryKey"));

        this.model.addColumn(firstColumnName);
        this.model.setValueAt(firstColumnPrimaryKey, 0, this.model.getColumnCount() - 1);

        for (int row = 1; row < this.model.getRowCount(); row++) {
            this.model.setValueAt(row - 1, row, this.model.getColumnCount() - 1);
        }
    }

    private List<List<String>> getColumnsSelected() {
        List<List<String>> columnsSelected = new ArrayList<>();

        for (Map.Entry<String, JCheckBox> checkBox : this.pkCheckBoxes.entrySet()) {
            int i = 0;
            if (checkBox.getValue().isSelected()) {
                List<String> columnData = new ArrayList<>();

                int index = this.columnNames.indexOf(checkBox.getKey());

                for (Vector<Object> column : this.rows) {
                    if(i++ != 0)
                        columnData.add(String.valueOf(column.get(index)));
                }

                columnsSelected.add(columnData);
            }
        }

        return columnsSelected;
    }

    public List<Column> getSelectedColumns() {
        return this.selectedColumns;
    }

    @Override
    public void checkBtnReady() {

        boolean tableNameAlreadyExists = MainController.getTables().containsKey(this.tableNameTextField.getText().strip());
        boolean hasPrimaryKey = this.pkCheckBoxes.values().stream().anyMatch(JCheckBox::isSelected);
        boolean blankTableName = tableNameTextField.getText().isBlank();

        this.btnReady.setEnabled(hasPrimaryKey && !tableNameAlreadyExists && !blankTableName);

        this.updateToolTipText(hasPrimaryKey, tableNameAlreadyExists, blankTableName);
    }

    @Override
    public void updateToolTipText(boolean... conditions) {
        String btnReadyToolTipText = "";

        if (conditions[0]) {
            btnReadyToolTipText = String.format("- %s", ConstantController.getString("pkChooser.toolTip.validColumn"));
        }

        if (conditions[1]) {
            btnReadyToolTipText = String.format("- %s", ConstantController.getString("pkChooser.toolTip.tableNameAlreadyExist"));
        }

        if (conditions[2]) {
            btnReadyToolTipText = String.format("- %s", ConstantController.getString("pkChooser.toolTip.blankTableName"));
        }

        UIManager.put("ToolTip.foreground", Color.RED);

        this.btnReady.setToolTipText(btnReadyToolTipText.isEmpty() ? null : btnReadyToolTipText);
    }

    private void updateOrder(){
        for (Map.Entry<String, JCheckBox> checkBox : this.pkCheckBoxes.entrySet()) {

            if(!checkBox.getValue().isSelected()) {
                pkOrder.remove(checkBox.getValue());
                continue;
            }

            if(!pkOrder.contains(checkBox.getValue()))
                pkOrder.add(checkBox.getValue());

        }
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {

        updateOrder();
        this.checkBtnReady();

        if (actionEvent.getSource() == this.btnReady) {
            for (JCheckBox checkBox : pkOrder) {
                if (checkBox.isSelected()) {

                    String sourceColumnName = pkCheckBoxes.entrySet().stream()
                        .filter(x -> x.getValue().equals(checkBox)).findFirst().orElseThrow().getKey();
                    String columnName = Column.removeSource(sourceColumnName);
                    String sourceName = Column.removeName(sourceColumnName);

                    Column c = cell.getColumns().stream().filter(x -> x.getSourceAndName()
                        .equals(sourceColumnName)).findFirst().orElseThrow();
                    this.selectedColumns.add(new Column(columnName, sourceName, c.DATA_TYPE,  true, c.IS_IGNORED_COLUMN));
                }
            }
            System.out.println(selectedColumns);
            this.dispose();
        }

        if (actionEvent.getSource() == this.btnCancel) {
            this.dispose();
        }
    }
    public String getTableName(){
        return tableNameTextField.getText();
    }

}
