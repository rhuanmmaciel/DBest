package gui.frames.forms.operations;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;

import javax.swing.*;
import javax.swing.text.NumberFormatter;

import com.mxgraph.model.mxCell;

import controller.ConstantController;
import controller.MainController;
import entities.Column;
import entities.cells.CsvTableCell;
import entities.cells.OperationCell;
import enums.OperationType;
import lib.booleanexpression.entities.elements.Element;
import lib.booleanexpression.entities.elements.Value;
import lib.booleanexpression.entities.elements.Variable;
import lib.booleanexpression.entities.expressions.AtomicExpression;
import lib.booleanexpression.enums.RelationalOperator;
import sgbd.table.Table;

public class AtomicExpressionForm extends OperationForm implements ActionListener, IOperationForm {

	private AtomicExpression atomicExpression = null;
	private ValueType valueType1 = ValueType.NONE;
	private ValueType valueType2 = ValueType.NONE;

	private final JTextField txtFieldValue1 = new JTextField();
	private final JComboBox<String> comboBoxOperator = new JComboBox<>(new String[]{"=", "≠", ">", "<", "≥", "≤", "is", "is not"});
	private final JTextField txtFieldValue2 = new JTextField();

	private final JComboBox<String> comboBoxSource2 = new JComboBox<>();
	private final JComboBox<String> comboBoxColumn2 = new JComboBox<>();

	private final JButton btnColumnSet1 = new JButton("Inserir");
	private final JButton btnNumberSet1 = new JButton("Inserir");
	private final JButton btnStringSet1 = new JButton("Inserir");
	private final JButton btnNullSet1 = new JButton("Inserir");
	private final JButton btnColumnSet2 = new JButton("Inserir");
	private final JButton btnNumberSet2 = new JButton("Inserir");
	private final JButton btnStringSet2 = new JButton("Inserir");
	private final JButton btnNullSet2 = new JButton("Inserir");

	private final DecimalFormat decimalFormat = new DecimalFormat("#.###");
	private final NumberFormatter numberFormatter = new NumberFormatter(decimalFormat);

	private final JFormattedTextField textFieldNumber1 = new JFormattedTextField(numberFormatter);
	private final JTextField textFieldString1 = new JTextField();
	private final JLabel labelNull1 = new JLabel("NULL");
	private final JFormattedTextField textFieldNumber2 = new JFormattedTextField(numberFormatter);
	private final JTextField textFieldString2 = new JTextField();
	private final JLabel labelNull2 = new JLabel("  NULL");

	private enum ValueType{
		COLUMN, NUMBER, STRING, NULL, NONE
	}

	public AtomicExpressionForm(mxCell jCell) {

		super(jCell);

		parent1.getColumns().stream()
				.map(Column::getSource).distinct()
				.forEach(comboBoxSource2::addItem);

		comboBoxSource2.addActionListener(actionEvent -> setColumns(comboBoxColumn2, comboBoxSource2, parent1));

		setColumns(comboBoxColumn2, comboBoxSource2, parent1);

		initializeGUI();

	}

	public void initializeGUI() {

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				closeWindow();
			}
		});

		centerPanel.removeAll();

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

		addExtraComponent(txtFieldValue1, 0, 0, 3, 1);
		addExtraComponent(comboBoxOperator, 3, 0, 1, 1);
		addExtraComponent(txtFieldValue2, 4, 0, 3, 1);

		addExtraComponent(new JLabel("  Fonte: "), 0, 1, 1, 1);
		addExtraComponent(comboBoxSource, 1, 1, 1, 1);
		addExtraComponent(new JLabel("  Coluna: "), 0, 2, 1, 1);
		addExtraComponent(comboBoxColumn, 1, 2, 1, 1);
		addExtraComponent(btnColumnSet1, 2, 1, 1, 2);

		addExtraComponent(new JLabel("  Fonte: "), 5, 1, 1, 1);
		addExtraComponent(comboBoxSource2, 6, 1, 1, 1);
		addExtraComponent(new JLabel("  Coluna: "), 5, 2, 1, 1);
		addExtraComponent(comboBoxColumn2, 6, 2, 1, 1);
		addExtraComponent(btnColumnSet2, 4, 1, 1, 2);

		addExtraComponent(new JLabel("  Número:  "), 0, 3, 1, 1);
		addExtraComponent(textFieldNumber1, 1, 3, 1, 1);
		addExtraComponent(btnNumberSet1, 2, 3, 1, 1);

		addExtraComponent(new JLabel("  Número:  "), 5, 3, 1, 1);
		addExtraComponent(textFieldNumber2, 6, 3, 1, 1);
		addExtraComponent(btnNumberSet2, 4, 3, 1, 1);

		addExtraComponent(new JLabel("  String:  "), 0, 4, 1, 1);
		addExtraComponent(textFieldString1, 1, 4, 1, 1);
		addExtraComponent(btnStringSet1, 2, 4, 1, 1);

		addExtraComponent(new JLabel("  String:  "), 5, 4, 1, 1);
		addExtraComponent(textFieldString2, 6, 4, 1, 1);
		addExtraComponent(btnStringSet2, 4, 4, 1, 1);

		addExtraComponent(labelNull1, 1, 5, 1, 1);
		addExtraComponent(btnNullSet1, 2, 5, 1, 1);

		addExtraComponent(labelNull2, 5, 5, 1, 1);
		addExtraComponent(btnNullSet2, 4, 5, 1, 1);

		setPreviousArgs();

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

		if(e.getSource() == btnColumnSet1) {

			txtFieldValue1.setText(comboBoxSource.getSelectedItem() + "." + comboBoxColumn.getSelectedItem());
			valueType1 = ValueType.COLUMN;

		}else if(e.getSource() == btnNumberSet1) {

			txtFieldValue1.setText(textFieldNumber1.getText());
			valueType1 = ValueType.NUMBER;

		}else if(e.getSource() == btnStringSet1) {

			txtFieldValue1.setText("\"" + textFieldString1.getText() + "\"");
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

			txtFieldValue2.setText("\""+textFieldString2.getText()+"\"");
			valueType2 = ValueType.STRING;

		}else if(e.getSource() == btnNullSet2){

			txtFieldValue2.setText("NULL");
			valueType2 = ValueType.NULL;

		}else if (e.getSource() == btnReady) {

			Element firstElement = switch (valueType1){
				case COLUMN -> new Variable(Column.removeName(txtFieldValue1.getText()), Column.removeSource(txtFieldValue1.getText()));
				case NUMBER -> new Value(Float.parseFloat(txtFieldValue1.getText()));
				case STRING -> new Value(txtFieldValue1.getText());
				case NULL, NONE -> null;
			};

			Element secondElement = switch (valueType2){
				case COLUMN -> new Variable(Column.removeName(txtFieldValue2.getText()), Column.removeSource(txtFieldValue2.getText()));
				case NUMBER -> new Value(Float.parseFloat(txtFieldValue2.getText()));
				case STRING -> new Value(txtFieldValue2.getText());
				case NULL, NONE -> null;
			};

			RelationalOperator relationalOperator = switch ((String)comboBoxOperator.getSelectedItem()){
				case "=" -> RelationalOperator.EQUAL;
				case ConstantController.NOT_EQUAL_SYMBOL -> RelationalOperator.NOT_EQUAL;
				case ConstantController.GREATER_THAN_OR_EQUAL_SYMBOL -> RelationalOperator.GREATER_THAN_OR_EQUAL;
				case ConstantController.LESS_THAN_OR_EQUAL_SYMBOL -> RelationalOperator.LESS_THAN_OR_EQUAL;
				case ">" -> RelationalOperator.GREATER_THAN;
				case "<" -> RelationalOperator.LESS_THAN;
				case "is" -> RelationalOperator.IS;
				case "is not" -> RelationalOperator.IS_NOT;
				default ->
						throw new IllegalStateException("Unexpected value: " + (String) comboBoxOperator.getSelectedItem());
			};

			atomicExpression = new AtomicExpression(firstElement, secondElement, relationalOperator);
			btnReady();

		}else if (e.getSource() == btnCancel)
			closeWindow();

	}

	public AtomicExpression getResult(){

		return atomicExpression;

	}

	protected void closeWindow() {
		dispose();
	}
}
