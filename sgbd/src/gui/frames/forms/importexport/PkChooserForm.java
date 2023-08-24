package gui.frames.forms.importexport;

import controller.ConstantController;
import database.TableUtils;
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
import java.util.*;
import java.util.List;

public class PkChooserForm extends FormBase implements ActionListener, IFormCondition{

    private final List<Column> selectedColumns = new ArrayList<>();
    private final Vector<Vector<Object>> rows = new Vector<>();

    private final JScrollPane scrollPane = new JScrollPane();
    private DefaultTableModel model;

    private final Map<String, JCheckBox> pkCheckBoxes = new HashMap<>();
    private final List<String> columnNames = new ArrayList<>();

    public PkChooserForm(Cell cell){

        super(null);
        setModal(true);

        List<Map<String, String>> rowsAux = TuplesExtractor.getAllRows(cell.getOperator(), true);

        for(Map<String, String> row : rowsAux) {

            Vector<Object> v = new Vector<>();

            for(Map.Entry<String, String> r : row.entrySet())
                v.add(r.getValue());

            this.rows.add(v);

        }

        columnNames.addAll(rowsAux.get(0).keySet());

        initGUI();

    }

    private void initGUI(){

        setBounds(0, 0, ConstantController.UI_WIDTH, ConstantController.UI_HEIGHT);

        btnReady.addActionListener(this);
        btnCancel.addActionListener(this);

        model = new JTableUtils.CustomTableModel(rows, new Vector<>(columnNames));
        model.insertRow(0, new Object[] {});

        List<JCheckBox> checkboxes = new ArrayList<>();

        for (String s : columnNames) {

            JCheckBox checkBox = new JCheckBox();
            checkBox.addActionListener(this);

            checkboxes.add(checkBox);
            pkCheckBoxes.put(s, checkBox);

        }

        addFirstColumn();

        JTable jTable = new JTable(model) {

            @Override
            public TableCellRenderer getCellRenderer(int row, int column) {

                if (row == 0 && column != 0) {
                    return new DefaultTableCellRenderer() {

                        @Override
                        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                                       boolean hasFocus, int row, int column) {

                            return checkboxes.get(column - 1);
                        }
                    };
                }

                return super.getCellRenderer(row, column);
            }

            @Override
            public TableCellEditor getCellEditor(int row, int column) {

                if (row == 0 && column != 0)
                    return new DefaultCellEditor(checkboxes.get(column - 1));

                return super.getCellEditor(row, column);
            }
        };

        jTable.getColumnModel().moveColumn(model.getColumnCount() - 1, 0);
        jTable.getColumnModel().getColumn(0).setResizable(false);
        JTableUtils.setColumnBold(jTable, 0);
        JTableUtils.preferredColumnWidthByValues(jTable, 0);
        jTable.setShowHorizontalLines(true);
        jTable.setGridColor(Color.blue);
        jTable.setColumnSelectionAllowed(false);
        jTable.setRowSelectionAllowed(false);
        jTable.setCellSelectionEnabled(false);
        JTableUtils.setNullInRed(jTable);
        scrollPane.setViewportView(jTable);
        ((JTableUtils.CustomTableModel) model).setRowEnabled(0, true);

        contentPane.add(scrollPane, BorderLayout.CENTER);

        checkBtnReady();

        setLocationRelativeTo(null);
        setVisible(true);

    }

    private void addFirstColumn() {

        model.addColumn("Nome:");

        model.setValueAt("Chave Primária:", 0, model.getColumnCount() - 1);

        for (int row = 1; row < model.getRowCount(); row++) {
            model.setValueAt(row - 1, row, model.getColumnCount() - 1);
        }

    }

    private List<List<String>> getColumnsSelected(){

        List<List<String>> columnsSelected = new ArrayList<>();
        for(Map.Entry<String, JCheckBox> checkBox : pkCheckBoxes.entrySet())
            if(checkBox.getValue().isSelected()) {

                List<String> columnData = new ArrayList<>();
                int index = columnNames.indexOf(checkBox.getKey());

                for (Vector<Object> column : rows)
                    columnData.add(String.valueOf(column.get(index)));

                columnsSelected.add(columnData);

            }

        return columnsSelected;

    }

    public List<Column> getSelectedColumns(){

        return selectedColumns;

    }

    @Override
    public void checkBtnReady() {

        boolean hasPK = pkCheckBoxes.values().stream().anyMatch(JCheckBox::isSelected);

        boolean canBePK = TableUtils.canBePrimaryKey(getColumnsSelected());

        btnReady.setEnabled(hasPK && canBePK);

        updateToolTipTxt(hasPK, canBePK);

    }

    @Override
    public void updateToolTipTxt(boolean... conditions) {

        String btnReadyToolTipText = "";

        if(conditions[0]) btnReadyToolTipText = "- Selecione pelo menos uma coluna válida";
        if(conditions[1]) btnReadyToolTipText = "- As colunas selecionadas não podem ser chave primária";

        UIManager.put("ToolTip.foreground", Color.RED);

        btnReady.setToolTipText(btnReadyToolTipText.isEmpty() ? null : btnReadyToolTipText);

    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {

        checkBtnReady();

        if(actionEvent.getSource() == btnReady){

            for(Map.Entry<String, JCheckBox> checkBox : pkCheckBoxes.entrySet())
                if(checkBox.getValue().isSelected()) {

                    String columnName = Column.removeSource(checkBox.getKey());
                    String sourceName = Column.removeName(checkBox.getKey());

                    selectedColumns.add(new Column(columnName, sourceName, true));

                }

            dispose();

        }

        if(actionEvent.getSource() == btnCancel){

            dispose();

        }

    }
}
