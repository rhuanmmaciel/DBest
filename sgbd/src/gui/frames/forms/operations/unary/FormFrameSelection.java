package gui.frames.forms.operations.unary;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.NumberFormatter;

import com.mxgraph.model.mxCell;

import gui.frames.forms.operations.FormFrameOperation;
import gui.frames.forms.operations.IFormFrameOperation;

public class FormFrameSelection extends FormFrameOperation implements ActionListener, IFormFrameOperation {

	private JTextArea textArea;

	private JButton btnColumnAdd;
	private JButton btnOperatorAdd;
	private JButton btnLogicalOperatorAdd;
	private JButton btnNumberAdd;
	private JButton btnStringAdd;
	private JButton btnRemoveLastOne;
	private JButton btnRemoveAll;

	private JComboBox<List<String>> comboBoxOperator;
	private JComboBox<List<String>> comboBoxLogicalOperator;
	private JFormattedTextField formattedTextFieldNumber;
	private JTextField textFieldString;

	public FormFrameSelection(mxCell jCell) {

		super(jCell);

		initializeGUI();

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void initializeGUI() {

		centerPanel.removeAll();

		btnReady.addActionListener(this);
		btnCancel.addActionListener(this);

		textArea = new JTextArea();
		textArea.setPreferredSize(new Dimension(300, 75));
		textArea.setEditable(false);

		btnColumnAdd = new JButton("Add");
		btnColumnAdd.addActionListener(this);

		comboBoxOperator = new JComboBox(new String[]{ ">", "<", "=", "≠", "≥", "≤", "(", ")" });

		btnOperatorAdd = new JButton("Add");
		btnOperatorAdd.addActionListener(this);

		comboBoxLogicalOperator = new JComboBox(new String[]{ "AND", "OR" });

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
		addExtraComponent(btnRemoveLastOne, 0, 6, 1, 1);
		addExtraComponent(btnRemoveAll, 1, 6, 1, 1);
		addExtraComponent(textArea, 0, 7, 3, 3);

		pack();
		setLocationRelativeTo(null);

		setVisible(true);

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == btnColumnAdd) {

			textArea.append(" ");
			textArea.append(comboBoxSource.getSelectedItem()+"."+comboBoxColumn.getSelectedItem());

		}

		if (e.getSource() == btnOperatorAdd) {

			textArea.append(" ");
			textArea.append(comboBoxOperator.getSelectedItem().toString());

		}

		if (e.getSource() == btnLogicalOperatorAdd) {

			textArea.append(" ");
			textArea.append(comboBoxLogicalOperator.getSelectedItem().toString());

		}

		if (e.getSource() == btnNumberAdd && !formattedTextFieldNumber.getText().isEmpty()) {

			textArea.append(" ");
			textArea.append(formattedTextFieldNumber.getText());

		}

		if (e.getSource() == btnStringAdd && !textFieldString.getText().isEmpty()) {

			textArea.append(" ");
			textArea.append("'" + textFieldString.getText() + "'");

		}

		if (e.getSource() == btnRemoveLastOne && !textArea.getText().isEmpty()) {

			String text = textArea.getText();
			Pattern pattern = Pattern.compile("[^']*\\s");
			Matcher matcher = pattern.matcher(text);

			if (matcher.find()) {
				int lastIndex = matcher.end() - 1;
				textArea.replaceRange("", lastIndex, text.length());
			}

		}

		if (e.getSource() == btnRemoveAll && !textArea.getText().isEmpty())
			textArea.setText("");

		if (e.getSource() == btnReady) {

			arguments.add(textArea.getText());
			btnReady();

		}

		if (e.getSource() == btnCancel)
			closeWindow();

	}

}
