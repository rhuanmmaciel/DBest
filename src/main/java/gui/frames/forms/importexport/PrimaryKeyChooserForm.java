package gui.frames.forms.importexport;

import controllers.ConstantController;
import database.TuplesExtractor;
import entities.Column;
import entities.cells.Cell;
import gui.frames.forms.FormBase;
import gui.frames.forms.IFormCondition;
import gui.utils.JTableUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.*;

public class PrimaryKeyChooserForm extends FormBase implements ActionListener, IFormCondition {

    private final List<Column> selectedColumns = new ArrayList<>();

    private final Vector<Vector<Object>> rows = new Vector<>();

    private final JScrollPane scrollPane = new JScrollPane();

    private DefaultTableModel model;

    private final Map<String, JCheckBox> pkCheckBoxes = new HashMap<>();

    private final List<String> columnNames = new ArrayList<>();

    public PrimaryKeyChooserForm(Cell cell) {
        super(null);

        this.setModal(true);

        List<Map<String, String>> rowsAux = TuplesExtractor.getAllRowsList(cell.getOperator(), true);

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

    private Vector<Vector<Object>> limitData(Vector<Vector<Object>> csvData, int limit){

        return csvData.stream().limit(limit).collect(Vector::new, Vector::add, Vector::addAll);

    }

    public void initGUI() {
        this.setBounds(0, 0, ConstantController.UI_SCREEN_WIDTH, ConstantController.UI_SCREEN_HEIGHT);

        this.btnReady.addActionListener(this);
        this.btnCancel.addActionListener(this);

        this.model = new JTableUtils.CustomTableModel(limitData(rows, 100), new Vector<>(this.columnNames));
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

        this.checkBtnReady();

        this.setLocationRelativeTo(null);
        this.setVisible(true);
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
        boolean hasPrimaryKey = this.pkCheckBoxes.values().stream().anyMatch(JCheckBox::isSelected);

        this.btnReady.setEnabled(hasPrimaryKey);

        this.updateToolTipText(hasPrimaryKey);
    }

    @Override
    public void updateToolTipText(boolean... conditions) {
        String btnReadyToolTipText = "";

        if (conditions[0]) {
            btnReadyToolTipText = String.format("- %s", ConstantController.getString("pkChooser.toolTip.validColumn"));
        }

        UIManager.put("ToolTip.foreground", Color.RED);

        this.btnReady.setToolTipText(btnReadyToolTipText.isEmpty() ? null : btnReadyToolTipText);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {

        this.checkBtnReady();

        if (actionEvent.getSource() == this.btnReady) {
            for (Map.Entry<String, JCheckBox> checkBox : this.pkCheckBoxes.entrySet()) {
                if (checkBox.getValue().isSelected()) {
                    String columnName = Column.removeSource(checkBox.getKey());
                    String sourceName = Column.removeName(checkBox.getKey());

                    this.selectedColumns.add(new Column(columnName, sourceName, true));
                }
            }

            this.dispose();
        }

        if (actionEvent.getSource() == this.btnCancel) {
            this.dispose();
        }
    }
}
