package gui.frames.forms.operations;

import com.mxgraph.model.mxCell;
import controllers.ConstantController;
import entities.Column;
import entities.cells.Cell;
import entities.cells.OperationCell;
import entities.utils.cells.CellUtils;
import enums.ColumnDataType;
import gui.frames.forms.FormBase;
import ibd.query.Operation;
import ibd.query.OperationUtils;
import ibd.query.binaryop.BinaryOperation;
import operations.IOperator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class OperationForm extends FormBase {

    private final Class<? extends IOperator> operator;

    protected final mxCell jCell;
    protected final Cell leftChild;

    protected final JComboBox<String> comboBoxSource = new JComboBox<>();
    protected final JComboBox<String> comboBoxColumn = new JComboBox<>();
    protected final JPanel centerPanel = new JPanel(new GridBagLayout());

    protected final List<String> arguments = new ArrayList<>();
    protected final List<String> previousArguments = new ArrayList<>();
    protected final List<String> restrictedColumns = new ArrayList<>();
    
    protected boolean acceptFilters = true;
    protected boolean acceptReferenceFilters = true;
    

    public OperationForm(mxCell jCell) {

        super(null);
        setModal(true);

        OperationCell cell = (OperationCell) CellUtils.getActiveCell(jCell).get();

        this.operator = cell.getType().operatorClass;
        this.jCell = jCell;

        if (cell.getParents() != null && !cell.getParents().isEmpty()) {
            this.leftChild = cell.getParents().get(0);
        } else {
            this.leftChild = null;
        }

        if (!cell.getArguments().isEmpty()) {
            previousArguments.addAll(cell.getArguments());
        }

        setTitle(cell.getType().displayName);

        initializeGUI();

    }

    private void initializeGUI() {

        setLocationRelativeTo(null);

        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        initializeComboBoxes();

        pack();

    }

    private void initializeComboBoxes() {

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(new JLabel(ConstantController.getString("operationForm.source") + ":"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        centerPanel.add(comboBoxSource, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        centerPanel.add(new JLabel(ConstantController.getString("operationForm.column") + ":"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        centerPanel.add(comboBoxColumn, gbc);

        contentPanel.add(centerPanel, BorderLayout.CENTER);
        Cell cell = CellUtils.getActiveCell(jCell).get(); 
            
        java.util.List<Column> columns = setLeftComboBoxColumns(cell);
        setComboBoxData(columns, comboBoxSource, comboBoxColumn);

        comboBoxSource.addActionListener(actionEvent -> setColumns(comboBoxColumn, comboBoxSource, columns));

    }

    protected void setLeftComboBoxData(Cell cell) {
        Cell parentCell = cell.getParents().get(0);
        setComboBoxData(parentCell.getColumns(), comboBoxSource, comboBoxColumn);

    }

    protected java.util.List<Column> setLeftComboBoxColumns(Cell cell) {
        //OperationCell cell = (OperationCell) CellUtils.getActiveCell(jCell).get();
        if (cell.getParents().isEmpty())
            return new ArrayList();
        Cell parentCell = cell.getParents().get(0);
        return new ArrayList(parentCell.getColumns());

    }

//    protected void setSourceComboBoxData(){
//        leftChild.getColumns().stream()
//                .map(x -> x.SOURCE).distinct()
//                .forEach(comboBoxSource::addItem);
//    }
    protected void setComboBoxData(java.util.List<Column> columns, JComboBox<String> comboBoxSources, JComboBox<String> comboBoxColumns) {
        columns.stream()
                .map(x -> x.SOURCE).distinct()
                .forEach(comboBoxSources::addItem);

        setColumnsComboBox(comboBoxColumns, comboBoxSources, columns);
    }

    protected void addExtraComponent(Component component, int gridx, int gridy, int gridwidth, int gridheight) {

        GridBagConstraints gbc = ((GridBagLayout) centerPanel.getLayout()).getConstraints(centerPanel);

        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = gridwidth;
        gbc.gridheight = gridheight;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        centerPanel.add(component, gbc);

        pack();
        revalidate();
        repaint();
    }

//    protected void setColumns(JComboBox<String> comboBox, JComboBox<String> comboBoxS, Cell parent){
//
//        comboBox.removeAllItems();
//        parent.getColumns().stream().filter(x -> x.SOURCE.
//                        equals(Objects.requireNonNull(comboBoxS.getSelectedItem()).toString())).
//                        map(Column::getSourceAndName).filter(x -> !restrictedColumns.contains(x)).
//                        map(Column::removeSource).forEach(comboBox::addItem);
//
//        pack();
//
//    }
    protected void setColumns(JComboBox<String> comboBox, JComboBox<String> comboBoxS, List<Column> columns) {
        comboBox.removeAllItems();
        if (comboBoxS.getSelectedItem() == null) {
            return;
        }
        String selectedItem = Objects.requireNonNull(comboBoxS.getSelectedItem()).toString();

        for (Column column : columns) {
            if (column.SOURCE.equals(selectedItem)) {
                String sourceAndName = column.getSourceAndName();
                if (!restrictedColumns.contains(sourceAndName)) {
                    String name = column.removeSource(sourceAndName);
                    comboBox.addItem(name);
                }
            }
        }

        pack();
    }

    protected void setColumnsComboBox(JComboBox<String> comboBox, JComboBox<String> comboBoxS, List<Column> columns) {
        Object obj = comboBoxS.getSelectedItem();
        if (obj == null) {
            obj = comboBoxS.getItemAt(0);
        }
        if (obj == null) {
            return;
        }
        String selectedItem = obj.toString();
        comboBox.removeAllItems();
        for (Column column : columns) {
            if (column.SOURCE.equals(selectedItem)) {
                String sourceAndName = column.getSourceAndName();
                if (!restrictedColumns.contains(sourceAndName)) {
                    String name = column.removeSource(sourceAndName);
                    comboBox.addItem(name);
                }
            }
        }

        pack();
    }

    public List<Column> getLeftSideCorrelationColumns(Operation op) {

        List<Column> columns = new ArrayList<>();

        List<Operation> leftSideCorrelations = OperationUtils.findLeftSideCorrelations(op);
        for (Operation leftSideCorrelation : leftSideCorrelations) {
            addColumns(leftSideCorrelation, columns);
        }

        return columns;
    }

    protected List<Column> getColumnsAndReferences(Cell cell) {
        List<Column> allColumns = new ArrayList(cell.getColumns());

        //OperationCell cell = (OperationCell) CellUtils.getActiveCell(jCell).get();
        if (cell.getChild() != null) {
            OperationCell childCell = cell.getChild();
            java.util.List<entities.Column> leftSideCorrelationCols = getLeftSideCorrelationColumns(childCell.getOperator());
            allColumns.addAll(leftSideCorrelationCols);
        }
        return allColumns;
    }
    
    

    protected List<Column> getReferences(Cell cell) {
        List<Column> allColumns = new ArrayList();

        java.util.List<entities.Column> leftSideCorrelationCols = getLeftSideCorrelationColumns(cell.getOperator());
        allColumns.addAll(leftSideCorrelationCols);
        return allColumns;
    }

    public static List<Operation> getLeftSideCorrelationOperations(OperationCell cell) {
        //return OperationUtils.findLeftSideCorrelations(cell.getOperator());

        List<Operation> operations = new ArrayList();
        while (cell != null) {
            Operation op1 = cell.getOperator();
            if (op1 instanceof BinaryOperation) {
                if (OperationUtils.isleftSideCorrelated(op1)) {
                    operations.add(((BinaryOperation) op1).getLeftOperation());
                }
            }
            cell = cell.getChild();
        }
        return operations;

    }

    public static List<Column> getLeftSideCorrelationColumns(OperationCell cell) {

        List<Operation> leftSideCorrelations = getLeftSideCorrelationOperations(cell);
        List<Column> columns = new ArrayList<>();

        for (Operation leftSideCorrelation : leftSideCorrelations) {
            addColumns(leftSideCorrelation, columns);
        }

        return columns;

    }

    private static void addColumns(Operation op, List<Column> columns) {
        for (Map.Entry<String, List<String>> contentInfo : op.getContentInfo().entrySet()) {
            for (String columnName : contentInfo.getValue()) {
                Column column = new Column(columnName, contentInfo.getKey(), ColumnDataType.NONE, false);
                columns.add(column);
            }
        }
    }

    protected void btnReady() {

        dispose();

        try {

            OperationCell cell = (OperationCell) CellUtils.getActiveCell(jCell).get();
            
            Constructor<? extends IOperator> constructor = operator.getDeclaredConstructor();
            IOperator operation = constructor.newInstance();
            operation.executeOperation(jCell, arguments, cell.getAlias());

        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException
                | InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    protected abstract void setPreviousArgs();

}
