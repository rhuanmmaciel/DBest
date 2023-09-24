package gui.frames.forms.operations;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.text.DecimalFormat;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.text.NumberFormatter;

import com.mxgraph.model.mxCell;

import controllers.ConstantController;

import entities.Column;
import entities.cells.Cell;
import entities.utils.cells.CellUtils;

import gui.frames.forms.IFormCondition;

import lib.booleanexpression.entities.elements.Element;
import lib.booleanexpression.entities.elements.Null;
import lib.booleanexpression.entities.expressions.AtomicExpression;
import lib.booleanexpression.enums.RelationalOperator;

import static booleanexpression.Utils.getValueAsNumber;
import static booleanexpression.Utils.getValueAsString;
import static booleanexpression.Utils.getVariable;

public class AtomicExpressionForm extends OperationForm implements ActionListener, IOperationForm, IFormCondition {

    protected final BooleanExpressionForm root;

    private AtomicExpression atomicExpression = null;

    private ValueType valueType1 = ValueType.NONE;

    private ValueType valueType2 = ValueType.NONE;

    private Cell parent2;

    private final JTextField txtFieldValue1 = new JTextField();

    private final JComboBox<String> comboBoxOperator = new JComboBox<>(Arrays.stream(RelationalOperator.values()).map(x -> x.symbols[0]).toArray(String[]::new));

    private final JTextField txtFieldValue2 = new JTextField();

    private final JComboBox<String> comboBoxSource2 = new JComboBox<>();

    private final JComboBox<String> comboBoxColumn2 = new JComboBox<>();

    private final JButton btnColumnSet1 = new JButton(ConstantController.getString("operationForm.atomicExpression.insert"));

    private final JButton btnNumberSet1 = new JButton(ConstantController.getString("operationForm.atomicExpression.insert"));

    private final JButton btnStringSet1 = new JButton(ConstantController.getString("operationForm.atomicExpression.insert"));

    private final JButton btnNullSet1 = new JButton(ConstantController.getString("operationForm.atomicExpression.insert"));

    private final JButton btnColumnSet2 = new JButton(ConstantController.getString("operationForm.atomicExpression.insert"));

    private final JButton btnNumberSet2 = new JButton(ConstantController.getString("operationForm.atomicExpression.insert"));

    private final JButton btnStringSet2 = new JButton(ConstantController.getString("operationForm.atomicExpression.insert"));

    private final JButton btnNullSet2 = new JButton(ConstantController.getString("operationForm.atomicExpression.insert"));

    private final DecimalFormat decimalFormat = new DecimalFormat("#.###");

    private final NumberFormatter numberFormatter = new NumberFormatter(this.decimalFormat);

    private final JFormattedTextField textFieldNumber1 = new JFormattedTextField(this.numberFormatter);

    private final JTextField textFieldString1 = new JTextField();

    private final JLabel labelNull1 = new JLabel("NULL");

    private final JFormattedTextField textFieldNumber2 = new JFormattedTextField(this.numberFormatter);

    private final JTextField textFieldString2 = new JTextField();

    private final JLabel labelNull2 = new JLabel("  NULL");

    @Override
    public void checkReadyButton() {
        boolean isTxtField1Empty = this.txtFieldValue1.getText().isEmpty() || this.txtFieldValue1.getText().isBlank();
        boolean isTxtField2Empty = this.txtFieldValue2.getText().isEmpty() || this.txtFieldValue2.getText().isBlank();

        this.readyButton.setEnabled(!isTxtField1Empty && !isTxtField2Empty);

        this.updateToolTipText(isTxtField1Empty, isTxtField2Empty);
    }

    @Override
    public void updateToolTipText(boolean... conditions) {
        String readyButtonToolTipText = "";

        boolean isTxtField1Empty = conditions[0];
        boolean isTxtField2Empty = conditions[1];

        if (isTxtField1Empty) {
            readyButtonToolTipText = "- " + ConstantController.getString("operationForm.atomicExpression.toolTip.firstElement");
        } else if (isTxtField2Empty) {
            readyButtonToolTipText = "- " + ConstantController.getString("operationForm.atomicExpression.toolTip.secondElement");
        }

        UIManager.put("ToolTip.foreground", Color.RED);

        this.readyButton.setToolTipText(readyButtonToolTipText.isEmpty() ? null : readyButtonToolTipText);
    }

    private enum ValueType {
        COLUMN, NUMBER, STRING, NULL, NONE
    }

    public AtomicExpressionForm(BooleanExpressionForm root, mxCell jCell) {
        super(jCell);

        this.root = root;

        this.parent1.getColumns().stream().map(Column::getSource).distinct().forEach(this.comboBoxSource2::addItem);

        this.setColumns(this.comboBoxColumn2, this.comboBoxSource2, this.parent1);

        this.parent2 = null;

        CellUtils.getActiveCell(jCell).ifPresent(cell -> {
            List<Cell> cellParents = cell.getParents();

            if (cellParents.size() == 2) {
                this.parent2 = cellParents.get(1);
                this.parent2.getColumns().stream().map(Column::getSource).distinct().forEach(this.comboBoxSource::addItem);
                this.parent2.getColumns().stream().map(Column::getSource).distinct().forEach(this.comboBoxSource2::addItem);
            }
        });

        for (ActionListener actionListener : this.comboBoxSource.getActionListeners()) {
            this.comboBoxSource.removeActionListener(actionListener);
        }

        for (ActionListener actionListener : this.comboBoxSource2.getActionListeners()) {
            this.comboBoxSource2.removeActionListener(actionListener);
        }

        this.comboBoxSource.addActionListener(this);
        this.comboBoxSource2.addActionListener(this);

        this.initializeGUI();
    }

    public void initializeGUI() {
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent event) {
                AtomicExpressionForm.this.closeWindow();
            }
        });

        this.centerPanel.removeAll();

        this.readyButton.addActionListener(this.root);
        this.cancelButton.addActionListener(this.root);

        this.readyButton.addActionListener(this);
        this.cancelButton.addActionListener(this);

        this.btnColumnSet1.addActionListener(this);
        this.btnNumberSet1.addActionListener(this);
        this.btnStringSet1.addActionListener(this);
        this.btnNullSet1.addActionListener(this);
        this.btnColumnSet2.addActionListener(this);
        this.btnNumberSet2.addActionListener(this);
        this.btnStringSet2.addActionListener(this);
        this.btnNullSet2.addActionListener(this);

        this.decimalFormat.setMaximumFractionDigits(5);

        this.txtFieldValue1.setEditable(false);
        this.txtFieldValue2.setEditable(false);

        this.addExtraComponent(this.txtFieldValue1, 0, 0, 3, 1);
        this.addExtraComponent(this.comboBoxOperator, 3, 0, 1, 1);
        this.addExtraComponent(this.txtFieldValue2, 4, 0, 3, 1);

        this.addExtraComponent(new JLabel("  " + ConstantController.getString("operationForm.atomicExpression.labels.source") + ": "), 0, 1, 1, 1);
        this.addExtraComponent(this.comboBoxSource, 1, 1, 1, 1);
        this.addExtraComponent(new JLabel("  " + ConstantController.getString("operationForm.atomicExpression.labels.column") + ": "), 0, 2, 1, 1);
        this.addExtraComponent(this.comboBoxColumn, 1, 2, 1, 1);
        this.addExtraComponent(this.btnColumnSet1, 2, 1, 1, 2);

        this.addExtraComponent(new JLabel("  " + ConstantController.getString("operationForm.atomicExpression.labels.source") + ": "), 5, 1, 1, 1);
        this.addExtraComponent(this.comboBoxSource2, 6, 1, 1, 1);
        this.addExtraComponent(new JLabel("  " + ConstantController.getString("operationForm.atomicExpression.labels.column") + ": "), 5, 2, 1, 1);
        this.addExtraComponent(this.comboBoxColumn2, 6, 2, 1, 1);
        this.addExtraComponent(this.btnColumnSet2, 4, 1, 1, 2);

        this.addExtraComponent(new JLabel("  " + ConstantController.getString("operationForm.atomicExpression.labels.number") + ": "), 0, 3, 1, 1);
        this.addExtraComponent(this.textFieldNumber1, 1, 3, 1, 1);
        this.addExtraComponent(this.btnNumberSet1, 2, 3, 1, 1);

        this.addExtraComponent(new JLabel("  " + ConstantController.getString("operationForm.atomicExpression.labels.number") + ": "), 5, 3, 1, 1);
        this.addExtraComponent(this.textFieldNumber2, 6, 3, 1, 1);
        this.addExtraComponent(this.btnNumberSet2, 4, 3, 1, 1);

        this.addExtraComponent(new JLabel("  " + ConstantController.getString("operationForm.atomicExpression.labels.string") + ": "), 0, 4, 1, 1);
        this.addExtraComponent(this.textFieldString1, 1, 4, 1, 1);
        this.addExtraComponent(this.btnStringSet1, 2, 4, 1, 1);

        this.addExtraComponent(new JLabel("  " + ConstantController.getString("operationForm.atomicExpression.labels.string") + ": "), 5, 4, 1, 1);
        this.addExtraComponent(this.textFieldString2, 6, 4, 1, 1);
        this.addExtraComponent(this.btnStringSet2, 4, 4, 1, 1);

        this.addExtraComponent(this.labelNull1, 1, 5, 1, 1);
        this.addExtraComponent(this.btnNullSet1, 2, 5, 1, 1);

        this.addExtraComponent(this.labelNull2, 5, 5, 1, 1);
        this.addExtraComponent(this.btnNullSet2, 4, 5, 1, 1);

        this.setPreviousArgs();

        this.checkReadyButton();

        this.pack();
        this.setLocationRelativeTo(null);

        this.setVisible(true);
    }

    @Override
    protected void setPreviousArgs() {
        // if(!previousArguments.isEmpty())
        //     insertString(previousArguments.get(0));
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        this.checkReadyButton();

        if (event.getSource() == this.comboBoxSource) {
            if (
                this.parent1
                    .getColumns()
                    .stream()
                    .anyMatch(column -> column
                        .getSource().equals(Objects.requireNonNull(this.comboBoxSource.getSelectedItem()).toString())
                    )
            ) {
                this.setColumns(this.comboBoxColumn, this.comboBoxSource, this.parent1);
            } else if (
                this.parent2 != null &&
                this.parent2
                    .getColumns()
                    .stream()
                    .anyMatch(column -> column
                        .getSource().equals(Objects.requireNonNull(this.comboBoxSource.getSelectedItem()).toString())
                    )
            ) {
                this.setColumns(this.comboBoxColumn, this.comboBoxSource, this.parent2);
            }
        }

        if (event.getSource() == this.comboBoxSource2) {
            if (
                this.parent1
                    .getColumns()
                    .stream()
                    .anyMatch(column -> column
                        .getSource()
                        .equals(Objects.requireNonNull(this.comboBoxSource2.getSelectedItem()).toString())
                    )
            ) {
                this.setColumns(this.comboBoxColumn2, this.comboBoxSource2, this.parent1);
            } else if (
                this.parent2 != null &&
                this.parent2
                    .getColumns()
                    .stream()
                    .anyMatch(column -> column
                        .getSource()
                        .equals(Objects.requireNonNull(this.comboBoxSource2.getSelectedItem()).toString())
                    )
            ) {
                this.setColumns(this.comboBoxColumn2, this.comboBoxSource2, this.parent2);
            }
        }

        if (event.getSource() == this.btnColumnSet1) {
            this.txtFieldValue1.setText(this.comboBoxSource.getSelectedItem() + "." + this.comboBoxColumn.getSelectedItem());
            this.valueType1 = ValueType.COLUMN;
        } else if (event.getSource() == this.btnNumberSet1) {
            this.txtFieldValue1.setText(this.textFieldNumber1.getText());
            this.valueType1 = ValueType.NUMBER;
        } else if (event.getSource() == this.btnStringSet1) {
            this.txtFieldValue1.setText("'" + this.textFieldString1.getText() + "'");
            this.valueType1 = ValueType.STRING;
        } else if (event.getSource() == this.btnNullSet1) {
            this.txtFieldValue1.setText("NULL");
            this.valueType1 = ValueType.NULL;
        } else if (event.getSource() == this.btnColumnSet2) {
            this.txtFieldValue2.setText(this.comboBoxSource2.getSelectedItem() + "." + this.comboBoxColumn2.getSelectedItem());
            this.valueType2 = ValueType.COLUMN;
        } else if (event.getSource() == this.btnNumberSet2) {
            this.txtFieldValue2.setText(this.textFieldNumber2.getText());
            this.valueType2 = ValueType.NUMBER;
        } else if (event.getSource() == this.btnStringSet2) {
            this.txtFieldValue2.setText("'" + this.textFieldString2.getText() + "'");
            this.valueType2 = ValueType.STRING;
        } else if (event.getSource() == this.btnNullSet2) {
            this.txtFieldValue2.setText("NULL");
            this.valueType2 = ValueType.NULL;
        } else if (event.getSource() == this.readyButton) {
            Element firstElement = switch (this.valueType1) {
                case COLUMN -> getVariable(this.txtFieldValue1.getText());
                case NUMBER -> getValueAsNumber((this.txtFieldValue1.getText()));
                case STRING -> getValueAsString(this.txtFieldValue1.getText());
                case NULL -> new Null();
                case NONE -> null;
            };

            Element secondElement = switch (this.valueType2) {
                case COLUMN -> getVariable(this.txtFieldValue2.getText());
                case NUMBER -> getValueAsNumber(this.txtFieldValue2.getText());
                case STRING -> getValueAsString(this.txtFieldValue2.getText());
                case NULL -> new Null();
                case NONE -> null;
            };

            RelationalOperator relationalOperator = RelationalOperator.getOperator((String) this.comboBoxOperator.getSelectedItem());

            this.atomicExpression = new AtomicExpression(firstElement, secondElement, relationalOperator);
            this.onReadyButtonClicked();
        } else if (event.getSource() == this.cancelButton) {
            this.closeWindow();
        }

        this.checkReadyButton();
    }

    public AtomicExpression getResult() {
        return this.atomicExpression;
    }

    protected void closeWindow() {
        this.dispose();
    }
}
