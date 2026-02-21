package gui.frames.forms.operations;

import booleanexpression.BooleanExpressionRecognizer;
import com.mxgraph.model.mxCell;
import controllers.ConstantController;
import entities.cells.Cell;
import entities.utils.cells.CellUtils;
import gui.frames.forms.IFormCondition;
import lib.booleanexpression.entities.elements.Element;
import lib.booleanexpression.entities.elements.Null;
import lib.booleanexpression.entities.expressions.AtomicExpression;
import lib.booleanexpression.enums.RelationalOperator;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Objects;

import static booleanexpression.Utils.*;
import entities.Column;
import ibd.table.prototype.query.fields.BooleanField;
import ibd.table.prototype.query.fields.DoubleField;
import ibd.table.prototype.query.fields.FloatField;
import ibd.table.prototype.query.fields.IntegerField;
import ibd.table.prototype.query.fields.LongField;
import ibd.table.prototype.query.fields.StringField;
import java.util.ArrayList;
import lib.booleanexpression.entities.elements.Value;
import lib.booleanexpression.entities.elements.Variable;

public class AtomicExpressionForm extends OperationForm implements ActionListener, IOperationForm, IFormCondition {

    protected final BooleanExpressionForm root;

    private AtomicExpression atomicExpression = null;
    private ValueType valueType1 = ValueType.NONE;
    private ValueType valueType2 = ValueType.NONE;
    //private Cell rightChild;
    private final JTextField txtFieldValue1 = new JTextField();
    private final JComboBox<String> comboBoxOperator = new JComboBox<>(Arrays.stream(RelationalOperator
            .values()).map(x -> x.symbols[0]).toArray(String[]::new));
    private final JTextField txtFieldValue2 = new JTextField();

    private final JComboBox<String> comboBoxSource2 = new JComboBox<>();
    private final JComboBox<String> comboBoxColumn2 = new JComboBox<>();

    private final JButton btnColumnSet1 = new JButton(ConstantController.getString("operationForm.atomicExpression.insert"));
    private final JButton btnNumberSet1 = new JButton(ConstantController.getString("operationForm.atomicExpression.insert"));
    private final JButton btnStringSet1 = new JButton(ConstantController.getString("operationForm.atomicExpression.insert"));
    private final JButton btnNullSet1 = new JButton(ConstantController.getString("operationForm.atomicExpression.insert"));
    private final JButton btnTrueSet1 = new JButton(ConstantController.getString("operationForm.atomicExpression.insert"));
    private final JButton btnColumnSet2 = new JButton(ConstantController.getString("operationForm.atomicExpression.insert"));
    private final JButton btnNumberSet2 = new JButton(ConstantController.getString("operationForm.atomicExpression.insert"));
    private final JButton btnStringSet2 = new JButton(ConstantController.getString("operationForm.atomicExpression.insert"));
    private final JButton btnNullSet2 = new JButton(ConstantController.getString("operationForm.atomicExpression.insert"));
    private final JButton btnTrueSet2 = new JButton(ConstantController.getString("operationForm.atomicExpression.insert"));
    
    private final DecimalFormat decimalFormat = new DecimalFormat("#.###");
    private final NumberFormatter numberFormatter = new NumberFormatter(decimalFormat);

    private final JFormattedTextField textFieldNumber1 = new JFormattedTextField(numberFormatter);
    private final JTextField textFieldString1 = new JTextField();
    private final JLabel labelNull1 = new JLabel("  NULL");
    private final JLabel labelTrue1 = new JLabel("  TRUE");
    private final JFormattedTextField textFieldNumber2 = new JFormattedTextField(numberFormatter);
    private final JTextField textFieldString2 = new JTextField();
    private final JLabel labelNull2 = new JLabel("  NULL");
    private final JLabel labelTrue2 = new JLabel("  TRUE");

    @Override
    public void checkBtnReady() {

        boolean isTxtField1Empty = txtFieldValue1.getText().isEmpty() || txtFieldValue1.getText().isBlank();
        boolean isTxtField2Empty = txtFieldValue2.getText().isEmpty() || txtFieldValue2.getText().isBlank();

        btnReady.setEnabled(!isTxtField1Empty && !isTxtField2Empty);

        updateToolTipText(isTxtField1Empty, isTxtField2Empty);

    }

    @Override
    public void updateToolTipText(boolean... conditions) {

        String btnReadyToolTipText = "";

        boolean isTxtField1Empty = conditions[0];
        boolean isTxtField2Empty = conditions[1];

        if (isTxtField1Empty) {
            btnReadyToolTipText = "- " + ConstantController.getString("operationForm.atomicExpression.toolTip.firstElement");
        } else if (isTxtField2Empty) {
            btnReadyToolTipText = "- " + ConstantController.getString("operationForm.atomicExpression.toolTip.secondElement");
        }

        UIManager.put("ToolTip.foreground", Color.RED);

        btnReady.setToolTipText(btnReadyToolTipText.isEmpty() ? null : btnReadyToolTipText);

    }

    private enum ValueType {
        COLUMN, NUMBER, STRING, NULL, TRUE, NONE
    }

    public AtomicExpressionForm(BooleanExpressionForm root, mxCell jCell, AtomicExpression atomicExpression, boolean acceptFilters, boolean acceptReferenceFilters) {

        super(jCell);
        this.atomicExpression = atomicExpression;
        this.acceptFilters = acceptFilters;
        this.acceptReferenceFilters = acceptReferenceFilters;
        this.root = root;

//		parent1.getColumns().stream()
//				.map(column -> column.SOURCE).distinct()
//				.forEach(comboBoxSource2::addItem);
//
//		setColumns(comboBoxColumn2, comboBoxSource2, parent1);
        //rightChild = null;
        Cell cell = CellUtils.getActiveCell(jCell).get();

        java.util.List<Column> allColumns = new ArrayList();

        if (acceptFilters) {
            if (!cell.getParents().isEmpty()) {
                Cell parentCell = cell.getParents().get(0);
                allColumns.addAll(parentCell.getColumns());
            }
        }

        if (acceptReferenceFilters) {
            allColumns.addAll(getReferences(cell));
        }

        allColumns.stream()
                .map(column -> column.SOURCE).distinct()
                .forEach(comboBoxSource2::addItem);

        setColumnsComboBox(comboBoxColumn2, comboBoxSource2, allColumns);

        for (ActionListener actionListener : comboBoxSource.getActionListeners()) {
            comboBoxSource.removeActionListener(actionListener);
        }

        for (ActionListener actionListener : comboBoxSource2.getActionListeners()) {
            comboBoxSource2.removeActionListener(actionListener);
        }

        comboBoxSource.addActionListener(this);
        comboBoxSource2.addActionListener(this);

        initGUI();

    }

    protected java.util.List<Column> setLeftComboBoxColumns(Cell cell) {
        java.util.List<Column> allColumns = new ArrayList();
        if (acceptFilters) {
            if (!cell.getParents().isEmpty()) {
                Cell parentCell = cell.getParents().get(0);
                allColumns.addAll(parentCell.getColumns());
            }
        }

        if (acceptReferenceFilters) {
            allColumns.addAll(getReferences(cell));
        }

        return allColumns;

    }

    public void initGUI() {
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeWindow();
            }
        });

        centerPanel.removeAll();

        btnReady.addActionListener(root);
        btnCancel.addActionListener(root);

        btnReady.addActionListener(this);
        btnCancel.addActionListener(this);

        btnColumnSet1.addActionListener(this);
        btnNumberSet1.addActionListener(this);
        btnStringSet1.addActionListener(this);
        btnNullSet1.addActionListener(this);
        btnTrueSet1.addActionListener(this);
        btnColumnSet2.addActionListener(this);
        btnNumberSet2.addActionListener(this);
        btnStringSet2.addActionListener(this);
        btnNullSet2.addActionListener(this);
        btnTrueSet2.addActionListener(this);

        decimalFormat.setMaximumFractionDigits(5);

        txtFieldValue1.setEditable(false);
        txtFieldValue2.setEditable(false);

        addExtraComponent(txtFieldValue1, 0, 0, 3, 1);
        addExtraComponent(comboBoxOperator, 3, 0, 1, 1);
        addExtraComponent(txtFieldValue2, 4, 0, 3, 1);

        addExtraComponent(new JLabel("  " + ConstantController.getString("operationForm.atomicExpression.labels.source") + ": "), 0, 1, 1, 1);
        addExtraComponent(comboBoxSource, 1, 1, 1, 1);
        addExtraComponent(new JLabel("  " + ConstantController.getString("operationForm.atomicExpression.labels.column") + ": "), 0, 2, 1, 1);
        addExtraComponent(comboBoxColumn, 1, 2, 1, 1);
        addExtraComponent(btnColumnSet1, 2, 1, 1, 2);

        addExtraComponent(new JLabel("  " + ConstantController.getString("operationForm.atomicExpression.labels.source") + ": "), 5, 1, 1, 1);
        addExtraComponent(comboBoxSource2, 6, 1, 1, 1);
        addExtraComponent(new JLabel("  " + ConstantController.getString("operationForm.atomicExpression.labels.column") + ": "), 5, 2, 1, 1);
        addExtraComponent(comboBoxColumn2, 6, 2, 1, 1);
        addExtraComponent(btnColumnSet2, 4, 1, 1, 2);

        addExtraComponent(new JLabel("  " + ConstantController.getString("operationForm.atomicExpression.labels.number") + ": "), 0, 3, 1, 1);
        addExtraComponent(textFieldNumber1, 1, 3, 1, 1);
        addExtraComponent(btnNumberSet1, 2, 3, 1, 1);

        addExtraComponent(new JLabel("  " + ConstantController.getString("operationForm.atomicExpression.labels.number") + ": "), 5, 3, 1, 1);
        addExtraComponent(textFieldNumber2, 6, 3, 1, 1);
        addExtraComponent(btnNumberSet2, 4, 3, 1, 1);

        addExtraComponent(new JLabel("  " + ConstantController.getString("operationForm.atomicExpression.labels.string") + ": "), 0, 4, 1, 1);
        addExtraComponent(textFieldString1, 1, 4, 1, 1);
        addExtraComponent(btnStringSet1, 2, 4, 1, 1);

        addExtraComponent(new JLabel("  " + ConstantController.getString("operationForm.atomicExpression.labels.string") + ": "), 5, 4, 1, 1);
        addExtraComponent(textFieldString2, 6, 4, 1, 1);
        addExtraComponent(btnStringSet2, 4, 4, 1, 1);

        addExtraComponent(labelNull1, 0, 5, 1, 1);
        addExtraComponent(btnNullSet1, 2, 5, 1, 1);

        addExtraComponent(labelNull2, 5, 5, 1, 1);
        addExtraComponent(btnNullSet2, 4, 5, 1, 1);
        
        addExtraComponent(labelTrue1, 0, 6, 1, 1);
        addExtraComponent(btnTrueSet1, 2, 6, 1, 1);
        
        addExtraComponent(labelTrue2, 5, 6, 1, 1);
        addExtraComponent(btnTrueSet2, 4, 6, 1, 1);

        setPreviousArgs();

        checkBtnReady();

        pack();
        setLocationRelativeTo(null);

        setVisible(true);

    }

    @Override
    protected void setPreviousArgs() {

//        if (previousArguments.isEmpty()) {
//            return;
//        }
        if (atomicExpression == null) {
            return;
        }
        //try {

//            System.out.println((previousArguments.get(0)));
//            this.atomicExpression
//                    = (AtomicExpression) new BooleanExpressionRecognizer(jCell).recognizer(previousArguments.get(0));
            Element elem1 = atomicExpression.getFirstElement();

            Element elem2 = atomicExpression.getSecondElement();

            comboBoxOperator.setSelectedIndex(Arrays.stream(RelationalOperator
                    .values()).toList().indexOf(atomicExpression.getRelationalOperator()));

            if (elem1 instanceof Variable var1) {
                comboBoxSource.setSelectedItem(var1.getNames()[0]);
                comboBoxColumn.setSelectedItem(var1.getNames()[1]);
                txtFieldValue1.setText(var1.getNames()[0] + "." + var1.getNames()[1]);
                valueType1 = AtomicExpressionForm.ValueType.COLUMN;
            } else if (elem1 instanceof Value val1) {
                if (val1.getField() instanceof StringField) {
                    txtFieldValue1.setText(getText(val1, true));
                    textFieldString1.setText(getText(val1, false));
                    valueType1 = AtomicExpressionForm.ValueType.STRING;
                } else if (val1.getField() instanceof BooleanField) {
                    txtFieldValue1.setText(getText(val1, true));
                    valueType1 = AtomicExpressionForm.ValueType.TRUE;
                }else {
                    txtFieldValue1.setText(getText(val1, true));
                    textFieldNumber1.setText(getText(val1, false));
                    valueType1 = AtomicExpressionForm.ValueType.NUMBER;
                }
            } else {
                valueType1 = AtomicExpressionForm.ValueType.NULL;
                txtFieldValue1.setText("NULL");
                valueType1 = AtomicExpressionForm.ValueType.NULL;
            }
            if (elem2 instanceof Variable var2) {
                comboBoxSource2.setSelectedItem(var2.getNames()[0]);
                comboBoxColumn2.setSelectedItem(var2.getNames()[1]);
                txtFieldValue2.setText(var2.getNames()[0] + "." + var2.getNames()[1]);
                valueType2 = AtomicExpressionForm.ValueType.COLUMN;
            } else if (elem2 instanceof Value val2) {
                if (val2.getField() instanceof StringField) {
                    txtFieldValue2.setText(getText(val2, true));
                    textFieldString2.setText(getText(val2, false));
                    valueType2 = AtomicExpressionForm.ValueType.STRING;
                } else if (val2.getField() instanceof BooleanField) {
                    txtFieldValue2.setText(getText(val2, true));
                    valueType2 = AtomicExpressionForm.ValueType.TRUE;
                }
                else{txtFieldValue2.setText(getText(val2, true));
                    textFieldNumber2.setText(getText(val2, false));
                    valueType2 = AtomicExpressionForm.ValueType.NUMBER;
                }
            } else {
                txtFieldValue2.setText("NULL");
                valueType2 = AtomicExpressionForm.ValueType.NULL;
            }

//        } catch (BooleanExpressionException e) {
//            new ErrorFrame(e.getMessage());
//        }
    }

    protected void setPrevioussArgs() {

//        if(previousArguments.isEmpty()) return;
//
//        try {
//
//            System.out.println((previousArguments.get(0)));
//            this.atomicExpression =
//                (AtomicExpression) new BooleanExpressionRecognizer(jCell).recognizer(previousArguments.get(0));
//
//            txtFieldValue1.setText(getText(atomicExpression.getFirstElement()));
//            txtFieldValue2.setText(getText(atomicExpression.getSecondElement()));
//
//            comboBoxOperator.setSelectedIndex(Arrays.stream(RelationalOperator
//                .values()).toList().indexOf(atomicExpression.getRelationalOperator()));
//
//        } catch (BooleanExpressionException e) {
//            new ErrorFrame(e.getMessage());
//        }
    }

//    private String getText(Element element){
//
//        if(element instanceof Value value) {
//
//            if(value.getField() instanceof StringField)
//                return ("'"+value.getField().getString()+"'");
//            else if(value.getField() instanceof IntegerField)
//                return (String.valueOf(value.getField().getInt()));
//            else if(value.getField() instanceof LongField)
//                return (String.valueOf(value.getField().getLong()));
//            else if(value.getField() instanceof FloatField)
//                return (String.valueOf(value.getField().getFloat()));
//            else if(value.getField() instanceof DoubleField)
//                return (String.valueOf(value.getField().getDouble()));
//            else if(value.getField() instanceof BooleanField)
//                return (String.valueOf(value.getField().getBoolean()));
//
//        }else if(element instanceof Variable variable) {
//
//            return (variable.getNames()[0] + "." + variable.getNames()[1]);
//        }
//
//        return "NULL";
//
//    }
    private String getText(Value value, boolean withQuotes) {

        if (value.getField() instanceof StringField) {
            if (withQuotes) {
                return ("'" + value.getField().getString() + "'");
            } else {
                return (value.getField().getString());
            }
        } else if (value.getField() instanceof IntegerField) {
            return (String.valueOf(value.getField().getInt()));
        } else if (value.getField() instanceof LongField) {
            return (String.valueOf(value.getField().getLong()));
        } else if (value.getField() instanceof FloatField) {
            return (String.valueOf(value.getField().getFloat()));
        } else if (value.getField() instanceof DoubleField) {
            return (String.valueOf(value.getField().getDouble()));
        } else if (value.getField() instanceof BooleanField) {
            return (String.valueOf(value.getField().getBoolean()));
        } else {
            return "";
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        checkBtnReady();

        if (e.getSource() == comboBoxSource) {
            sourceBox1Selected(comboBoxSource, comboBoxColumn, leftChild);
        }
        if (e.getSource() == comboBoxSource2) {
            sourceBox2Selected(comboBoxSource2, comboBoxColumn2, leftChild);
        }

        if (e.getSource() == btnColumnSet1) {

            txtFieldValue1.setText(comboBoxSource.getSelectedItem() + "." + comboBoxColumn.getSelectedItem());
            valueType1 = ValueType.COLUMN;

        } else if (e.getSource() == btnNumberSet1) {

            txtFieldValue1.setText(textFieldNumber1.getText());
            valueType1 = ValueType.NUMBER;

        } else if (e.getSource() == btnStringSet1) {

            txtFieldValue1.setText("'" + textFieldString1.getText() + "'");
            valueType1 = ValueType.STRING;

        } else if (e.getSource() == btnNullSet1) {

            txtFieldValue1.setText("NULL");
            valueType1 = ValueType.NULL;

        }else if (e.getSource() == btnTrueSet1) {

            txtFieldValue1.setText("TRUE");
            valueType1 = ValueType.TRUE;

        }else if (e.getSource() == btnColumnSet2) {

            txtFieldValue2.setText(comboBoxSource2.getSelectedItem() + "." + comboBoxColumn2.getSelectedItem());
            valueType2 = ValueType.COLUMN;

        } else if (e.getSource() == btnNumberSet2) {

            txtFieldValue2.setText(textFieldNumber2.getText());
            valueType2 = ValueType.NUMBER;

        } else if (e.getSource() == btnStringSet2) {

            txtFieldValue2.setText("'" + textFieldString2.getText() + "'");
            valueType2 = ValueType.STRING;

        } else if (e.getSource() == btnNullSet2) {

            txtFieldValue2.setText("NULL");
            valueType2 = ValueType.NULL;

        } else if (e.getSource() == btnTrueSet2) {

            txtFieldValue2.setText("TRUE");
            valueType2 = ValueType.TRUE;

        }else if (e.getSource() == btnReady) {

            Element firstElement = switch (valueType1) {
                case COLUMN ->
                    getVariable(txtFieldValue1.getText());
                case NUMBER ->
                    getValueAsNumber((txtFieldValue1.getText()));
                case STRING ->
                    getValueAsString(txtFieldValue1.getText());
                case NULL ->
                    new Null();
                case TRUE ->
                    getValueAsBoolean(true);
                case NONE ->
                    null;
            };

            Element secondElement = switch (valueType2) {
                case COLUMN ->
                    getVariable(txtFieldValue2.getText());
                case NUMBER ->
                    getValueAsNumber(txtFieldValue2.getText());
                case STRING ->
                    getValueAsString(txtFieldValue2.getText());
                case NULL ->
                    new Null();
                case TRUE ->
                    getValueAsBoolean(true);
                case NONE ->
                    null;
            };

            RelationalOperator relationalOperator = RelationalOperator.getOperator((String) Objects.requireNonNull(comboBoxOperator.getSelectedItem()));

            atomicExpression = new AtomicExpression(firstElement, secondElement, relationalOperator);
            arguments.add(new BooleanExpressionRecognizer(jCell).recognizer(atomicExpression));
            btnReady();

        } else if (e.getSource() == btnCancel) {
            closeWindow();
        }

        checkBtnReady();

    }

    protected void sourceBox1Selected(JComboBox<String> comboBoxSources, JComboBox<String> comboBoxColumns, Cell cell) {

        java.util.List<Column> allColumns = new ArrayList();
        allColumns.addAll(cell.getColumns());

        allColumns.addAll(getReferences(cell));

        if (hasMatch(allColumns, comboBoxSources)) {
            setColumnsComboBox(comboBoxColumns, comboBoxSources, allColumns);

        }
    }
    
    protected void sourceBox2Selected(JComboBox<String> comboBoxSources, JComboBox<String> comboBoxColumns, Cell cell) {

        java.util.List<Column> allColumns = new ArrayList();
        if (acceptFilters) {
            allColumns.addAll(cell.getColumns());
        }

        if (acceptReferenceFilters) {
            allColumns.addAll(getReferences(cell));
        }

        if (hasMatch(allColumns, comboBoxSources)) {
            setColumnsComboBox(comboBoxColumns, comboBoxSources, allColumns);

        }
    }

//    protected void sourceBox2Selected() {
//        if (leftChild != null && hasMatch(leftChild.getColumns(), comboBoxSource2)) {
//            setColumnsComboBox(comboBoxColumn2, comboBoxSource2, leftChild.getColumns());
//        } else {
//            OperationCell cell = CellUtils.getActiveCell(jCell).get().getChild();
//            java.util.List<entities.Column> leftSideCorrelationCols = this.getLeftSideCorrelationColumns(cell.getOperator());
//
//            if (hasMatch(leftSideCorrelationCols, comboBoxSource2)) {
//                setColumnsComboBox(comboBoxColumn2, comboBoxSource2, leftSideCorrelationCols);
//            }
//
//        }
//    }
    private boolean hasMatch(java.util.List<Column> columns, JComboBox<String> comboBox) {
        return (leftChild != null && columns.stream().anyMatch(column -> column.SOURCE.
                equals(Objects.requireNonNull(comboBox.getSelectedItem()).toString())));
    }

    public AtomicExpression getResult() {

        return atomicExpression;

    }

    protected void closeWindow() {
        dispose();
    }
}
