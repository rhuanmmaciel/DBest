package gui.frames.forms.operations.unary;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.NumberFormatter;
import javax.swing.text.StyledDocument;

import com.mxgraph.model.mxCell;

import gui.frames.forms.operations.OperationForm;
import gui.frames.forms.operations.IOperationForm;

public class SelectionForm extends OperationForm implements ActionListener, IOperationForm {

	private JTextPane textPane;

	private JButton btnColumnAdd;
	private JButton btnOperatorAdd;
	private JButton btnLogicalOperatorAdd;
	private JButton btnNumberAdd;
	private JButton btnStringAdd;
	private JButton btnRemoveLastOne;
	private JButton btnRemoveAll;
	private final JButton btnNull = new JButton("null");
	private final JButton btnIs = new JButton("is");
	private final JButton btnIsNot = new JButton("is not");

	private JComboBox<String> comboBoxOperator;
	private JComboBox<String> comboBoxLogicalOperator;
	private JFormattedTextField formattedTextFieldNumber;
	private JTextField textFieldString;

	public SelectionForm(mxCell jCell) {

		super(jCell);

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

		textPane = new JTextPane();
		textPane.setPreferredSize(new Dimension(300, 75));
		textPane.setEditable(false);

		btnColumnAdd = new JButton("Add");
		btnColumnAdd.addActionListener(this);

		comboBoxOperator = new JComboBox<>(new String[]{ ">", "<", "=", "≠", "≥", "≤", "(", ")" });

		btnOperatorAdd = new JButton("Add");
		btnOperatorAdd.addActionListener(this);

		comboBoxLogicalOperator = new JComboBox<>(new String[]{ "AND", "OR" });

		btnLogicalOperatorAdd = new JButton("Add");
		btnLogicalOperatorAdd.addActionListener(this);

		DecimalFormat decimalFormat = new DecimalFormat("#.###");
		decimalFormat.setMaximumFractionDigits(5);
		NumberFormatter numberFormatter = new NumberFormatter(decimalFormat);
		formattedTextFieldNumber = new JFormattedTextField(numberFormatter);

		btnNumberAdd = new JButton("Add");
		btnNumberAdd.addActionListener(this);

		textFieldString = new JTextField();

		btnStringAdd = new JButton("Add");
		btnStringAdd.addActionListener(this);

		btnNull.setForeground(Color.RED);
		btnNull.addActionListener(this);

		btnIs.addActionListener(this);
		btnIsNot.addActionListener(this);

		btnRemoveLastOne = new JButton("Apagar último inserido");
		btnRemoveLastOne.addActionListener(this);

		btnRemoveAll = new JButton("Apagar tudo");
		btnRemoveAll.addActionListener(this);

		addExtraComponent(new JLabel("Fonte:"), 0, 0, 1, 1);
		addExtraComponent(comboBoxSource, 1, 0, 1, 1);
		addExtraComponent(new JLabel("Coluna:"), 0, 1, 1, 1);
		addExtraComponent(comboBoxColumn, 1, 1, 1, 1);
		addExtraComponent(btnColumnAdd, 2, 1, 1, 1);
		addExtraComponent( new JLabel("Operações:"), 0, 2, 1, 1);
		addExtraComponent(comboBoxOperator, 1, 2, 1, 1);
		addExtraComponent(btnOperatorAdd, 2, 2, 1, 1);
		addExtraComponent(new JLabel("Operadores Lógicos: "), 0, 3, 1, 1);
		addExtraComponent(comboBoxLogicalOperator, 1, 3, 1, 1);
		addExtraComponent(btnLogicalOperatorAdd, 2, 3, 1, 1);
		addExtraComponent(new JLabel("Números: "), 0, 4, 1, 1);
		addExtraComponent(formattedTextFieldNumber, 1, 4, 1, 1);
		addExtraComponent(btnNumberAdd, 2, 4, 1, 1);
		addExtraComponent(new JLabel("Strings: "), 0, 5, 1, 1);
		addExtraComponent(textFieldString, 1, 5, 1, 1);
		addExtraComponent(btnStringAdd, 2, 5, 1, 1);
		addExtraComponent(btnNull, 0, 6, 1, 1);
		addExtraComponent(btnIs, 1, 6, 1, 1);
		addExtraComponent(btnIsNot, 2, 6, 1, 1);
		addExtraComponent(btnRemoveLastOne, 0, 7, 1, 1);
		addExtraComponent(btnRemoveAll, 1, 7, 1, 1);
		addExtraComponent(new JScrollPane(textPane), 0, 8, 3, 3);

		setPreviousArgs();

		pack();
		setLocationRelativeTo(null);

		setVisible(true);

	}

	@Override
	protected void setPreviousArgs() {

		if(!previousArguments.isEmpty())
			insertString(previousArguments.get(0));

	}

	private void insertString(String txt){

		try {
			textPane.getDocument().insertString(textPane.getCaretPosition(), txt, null);
		} catch (BadLocationException error) {
			error.printStackTrace();
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == btnColumnAdd) {

			insertString(" ");
			insertString(comboBoxSource.getSelectedItem()+"."+comboBoxColumn.getSelectedItem());

		}

		if (e.getSource() == btnOperatorAdd) {

			insertString(" ");
			insertString((String) comboBoxOperator.getSelectedItem());

		}

		if (e.getSource() == btnLogicalOperatorAdd) {

			insertString(" ");
			insertString(Objects.requireNonNull(comboBoxLogicalOperator.getSelectedItem()).toString());

		}

		if (e.getSource() == btnNumberAdd && !formattedTextFieldNumber.getText().isEmpty()) {

			insertString(" ");
			insertString(formattedTextFieldNumber.getText());

		}

		if (e.getSource() == btnStringAdd && !textFieldString.getText().isEmpty()) {

			insertString(" ");
			insertString("'" + textFieldString.getText() + "'");

		}

		if (e.getSource() == btnRemoveLastOne && !textPane.getText().isEmpty()) {

			String text = textPane.getText();
			Pattern pattern = Pattern.compile("[^']*\\s");
			Matcher matcher = pattern.matcher(text);

			if (matcher.find()) {

				int lastIndex = matcher.end() - 1;
				StyledDocument doc = textPane.getStyledDocument();
				int end = text.length();
				String replacement = "";

				try {
					doc.remove(lastIndex, end - lastIndex);
					doc.insertString(lastIndex, replacement, null);
				} catch (BadLocationException exception) {
					exception.printStackTrace();
				}
			}

		}

		if(btnNull == e.getSource()) {
			insertString(" ");
			insertString("null");
		}

		if(btnIs == e.getSource()){
			insertString(" ");
			insertString("is");
		}

		if(btnIsNot == e.getSource()){
			insertString(" ");
			insertString("is not");
		}

		if (e.getSource() == btnRemoveAll && !textPane.getText().isEmpty())
			textPane.setText("");

		if (e.getSource() == btnReady) {

			arguments.add(textPane.getText());
			btnReady();

		}

		if (e.getSource() == btnCancel)
			closeWindow();

	}

	protected void closeWindow() {
		dispose();
	}
}
