package gui.frames.forms.operations;

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

public class AtomicExpressionForm extends OperationForm implements ActionListener, IOperationForm, IFormCondition {

	protected final BooleanExpressionForm root;

	private AtomicExpression atomicExpression = null;
	private ValueType valueType1 = ValueType.NONE;
	private ValueType valueType2 = ValueType.NONE;
	private Cell parent2;
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
	private final JButton btnColumnSet2 = new JButton(ConstantController.getString("operationForm.atomicExpression.insert"));
	private final JButton btnNumberSet2 = new JButton(ConstantController.getString("operationForm.atomicExpression.insert"));
	private final JButton btnStringSet2 = new JButton(ConstantController.getString("operationForm.atomicExpression.insert"));
	private final JButton btnNullSet2 = new JButton(ConstantController.getString("operationForm.atomicExpression.insert"));

	private final DecimalFormat decimalFormat = new DecimalFormat("#.###");
	private final NumberFormatter numberFormatter = new NumberFormatter(decimalFormat);

	private final JFormattedTextField textFieldNumber1 = new JFormattedTextField(numberFormatter);
	private final JTextField textFieldString1 = new JTextField();
	private final JLabel labelNull1 = new JLabel("NULL");
	private final JFormattedTextField textFieldNumber2 = new JFormattedTextField(numberFormatter);
	private final JTextField textFieldString2 = new JTextField();
	private final JLabel labelNull2 = new JLabel("  NULL");

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

		if (isTxtField1Empty)
			btnReadyToolTipText = "- " + ConstantController.getString("operationForm.atomicExpression.toolTip.firstElement");
		else if (isTxtField2Empty)
			btnReadyToolTipText = "- " + ConstantController.getString("operationForm.atomicExpression.toolTip.secondElement");

		UIManager.put("ToolTip.foreground", Color.RED);

		btnReady.setToolTipText(btnReadyToolTipText.isEmpty() ? null : btnReadyToolTipText);

	}

	private enum ValueType{
		COLUMN, NUMBER, STRING, NULL, NONE
	}

	public AtomicExpressionForm(BooleanExpressionForm root, mxCell jCell) {

		super(jCell);

		this.root = root;

		parent1.getColumns().stream()
				.map(Column::getSource).distinct()
				.forEach(comboBoxSource2::addItem);

		setColumns(comboBoxColumn2, comboBoxSource2, parent1);

		parent2 = null;

		if(CellUtils.getActiveCell(jCell).get().getParents().size() == 2){

			this.parent2 = CellUtils.getActiveCell(jCell).get().getParents().get(1);
			parent2.getColumns().stream()
					.map(Column::getSource).distinct()
					.forEach(comboBoxSource::addItem);

			parent2.getColumns().stream()
					.map(Column::getSource).distinct()
					.forEach(comboBoxSource2::addItem);

		}

		for(ActionListener actionListener : comboBoxSource.getActionListeners())
			comboBoxSource.removeActionListener(actionListener);

		for(ActionListener actionListener : comboBoxSource2.getActionListeners())
			comboBoxSource2.removeActionListener(actionListener);

		comboBoxSource.addActionListener(this);
		comboBoxSource2.addActionListener(this);

		initGUI();

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
		btnColumnSet2.addActionListener(this);
		btnNumberSet2.addActionListener(this);
		btnStringSet2.addActionListener(this);
		btnNullSet2.addActionListener(this);

		decimalFormat.setMaximumFractionDigits(5);

		txtFieldValue1.setEditable(false);
		txtFieldValue2.setEditable(false);

		addExtraComponent(txtFieldValue1, 0, 0, 3, 1);
		addExtraComponent(comboBoxOperator, 3, 0, 1, 1);
		addExtraComponent(txtFieldValue2, 4, 0, 3, 1);

		addExtraComponent(new JLabel("  "+ConstantController.getString("operationForm.atomicExpression.labels.source")+": "), 0, 1, 1, 1);
		addExtraComponent(comboBoxSource, 1, 1, 1, 1);
		addExtraComponent(new JLabel("  "+ConstantController.getString("operationForm.atomicExpression.labels.column")+": "), 0, 2, 1, 1);
		addExtraComponent(comboBoxColumn, 1, 2, 1, 1);
		addExtraComponent(btnColumnSet1, 2, 1, 1, 2);

		addExtraComponent(new JLabel("  "+ConstantController.getString("operationForm.atomicExpression.labels.source")+": "), 5, 1, 1, 1);
		addExtraComponent(comboBoxSource2, 6, 1, 1, 1);
		addExtraComponent(new JLabel("  "+ConstantController.getString("operationForm.atomicExpression.labels.column")+": "), 5, 2, 1, 1);
		addExtraComponent(comboBoxColumn2, 6, 2, 1, 1);
		addExtraComponent(btnColumnSet2, 4, 1, 1, 2);

		addExtraComponent(new JLabel("  "+ConstantController.getString("operationForm.atomicExpression.labels.number")+": "), 0, 3, 1, 1);
		addExtraComponent(textFieldNumber1, 1, 3, 1, 1);
		addExtraComponent(btnNumberSet1, 2, 3, 1, 1);

		addExtraComponent(new JLabel("  "+ConstantController.getString("operationForm.atomicExpression.labels.number")+": "), 5, 3, 1, 1);
		addExtraComponent(textFieldNumber2, 6, 3, 1, 1);
		addExtraComponent(btnNumberSet2, 4, 3, 1, 1);

		addExtraComponent(new JLabel("  "+ConstantController.getString("operationForm.atomicExpression.labels.string")+": "), 0, 4, 1, 1);
		addExtraComponent(textFieldString1, 1, 4, 1, 1);
		addExtraComponent(btnStringSet1, 2, 4, 1, 1);

		addExtraComponent(new JLabel("  "+ConstantController.getString("operationForm.atomicExpression.labels.string")+": "), 5, 4, 1, 1);
		addExtraComponent(textFieldString2, 6, 4, 1, 1);
		addExtraComponent(btnStringSet2, 4, 4, 1, 1);

		addExtraComponent(labelNull1, 1, 5, 1, 1);
		addExtraComponent(btnNullSet1, 2, 5, 1, 1);

		addExtraComponent(labelNull2, 5, 5, 1, 1);
		addExtraComponent(btnNullSet2, 4, 5, 1, 1);

		setPreviousArgs();

		checkBtnReady();

		pack();
		setLocationRelativeTo(null);

		setVisible(true);

	}

	@Override
	protected void setPreviousArgs() {

//		if(!previousArguments.isEmpty())
//			insertString(previousArguments.get(0));

	}


	@Override
	public void actionPerformed(ActionEvent e) {

		checkBtnReady();

		if(e.getSource() == comboBoxSource){
			if(parent1.getColumns().stream().anyMatch(x -> x.getSource().
					equals(Objects.requireNonNull(comboBoxSource.getSelectedItem()).toString()))){

				setColumns(comboBoxColumn, comboBoxSource, parent1);

			}else if(parent2 != null && parent2.getColumns().stream().anyMatch(x -> x.getSource().
					equals(Objects.requireNonNull(comboBoxSource.getSelectedItem()).toString()))){

				setColumns(comboBoxColumn, comboBoxSource, parent2);

			}
		}
		if(e.getSource() == comboBoxSource2){
			if(parent1.getColumns().stream().anyMatch(x -> x.getSource().
							equals(Objects.requireNonNull(comboBoxSource2.getSelectedItem()).toString()))){

				setColumns(comboBoxColumn2, comboBoxSource2, parent1);

			}else if(parent2 != null && parent2.getColumns().stream().anyMatch(x -> x.getSource().
					equals(Objects.requireNonNull(comboBoxSource2.getSelectedItem()).toString()))){

				setColumns(comboBoxColumn2, comboBoxSource2, parent2);

			}
		}

		if(e.getSource() == btnColumnSet1) {

			txtFieldValue1.setText(comboBoxSource.getSelectedItem() + "." + comboBoxColumn.getSelectedItem());
			valueType1 = ValueType.COLUMN;

		}else if(e.getSource() == btnNumberSet1) {

			txtFieldValue1.setText(textFieldNumber1.getText());
			valueType1 = ValueType.NUMBER;

		}else if(e.getSource() == btnStringSet1) {

			txtFieldValue1.setText("'" + textFieldString1.getText() + "'");
			valueType1 = ValueType.STRING;

		}else if(e.getSource() == btnNullSet1) {

			txtFieldValue1.setText("NULL");
			valueType1 = ValueType.NULL;

		}else if(e.getSource() == btnColumnSet2) {

			txtFieldValue2.setText(comboBoxSource2.getSelectedItem() + "." + comboBoxColumn2.getSelectedItem());
			valueType2 = ValueType.COLUMN;

		}else if(e.getSource() == btnNumberSet2) {

			txtFieldValue2.setText(textFieldNumber2.getText());
			valueType2 = ValueType.NUMBER;

		}else if(e.getSource() == btnStringSet2){

			txtFieldValue2.setText("'"+textFieldString2.getText()+"'");
			valueType2 = ValueType.STRING;

		}else if(e.getSource() == btnNullSet2){

			txtFieldValue2.setText("NULL");
			valueType2 = ValueType.NULL;

		}else if (e.getSource() == btnReady) {

			Element firstElement = switch (valueType1){
				case COLUMN -> getVariable(txtFieldValue1.getText());
				case NUMBER -> getValueAsNumber((txtFieldValue1.getText()));
				case STRING -> getValueAsString(txtFieldValue1.getText());
				case NULL -> new Null();
				case NONE -> null;
			};

			Element secondElement = switch (valueType2){
				case COLUMN -> getVariable(txtFieldValue2.getText());
				case NUMBER -> getValueAsNumber(txtFieldValue2.getText());
				case STRING -> getValueAsString(txtFieldValue2.getText());
				case NULL -> new Null();
				case NONE -> null;
			};

			RelationalOperator relationalOperator = RelationalOperator.getOperator((String) Objects.requireNonNull(comboBoxOperator.getSelectedItem()));

			atomicExpression = new AtomicExpression(firstElement, secondElement, relationalOperator);
			btnReady();

		}else if (e.getSource() == btnCancel)
			closeWindow();

		checkBtnReady();

	}

	public AtomicExpression getResult(){

		return atomicExpression;

	}

	protected void closeWindow() {
		dispose();
	}
}
