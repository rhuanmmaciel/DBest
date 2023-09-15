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

        this.initializeGUI();

	}

	public void initializeGUI() {

        this.addWindowListener(new WindowAdapter() {

            @Override
			public void windowClosing(WindowEvent exception) {
                SelectionForm.this.closeWindow();
			}
		});

        this.centerPanel.removeAll();

        this.readyButton.addActionListener(this);
        this.cancelButton.addActionListener(this);

        this.textPane = new JTextPane();
        this.textPane.setPreferredSize(new Dimension(300, 75));
        this.textPane.setEditable(false);

        this.btnColumnAdd = new JButton("Add");
        this.btnColumnAdd.addActionListener(this);

        this.comboBoxOperator = new JComboBox<>(new String[]{ ">", "<", "=", "≠", "≥", "≤", "(", ")" });

        this.btnOperatorAdd = new JButton("Add");
        this.btnOperatorAdd.addActionListener(this);

        this.comboBoxLogicalOperator = new JComboBox<>(new String[]{ "AND", "OR" });

        this.btnLogicalOperatorAdd = new JButton("Add");
        this.btnLogicalOperatorAdd.addActionListener(this);

		DecimalFormat decimalFormat = new DecimalFormat("#.###");
		decimalFormat.setMaximumFractionDigits(5);
		NumberFormatter numberFormatter = new NumberFormatter(decimalFormat);
        this.formattedTextFieldNumber = new JFormattedTextField(numberFormatter);

        this.btnNumberAdd = new JButton("Add");
        this.btnNumberAdd.addActionListener(this);

        this.textFieldString = new JTextField();

        this.btnStringAdd = new JButton("Add");
        this.btnStringAdd.addActionListener(this);

        this.btnNull.setForeground(Color.RED);
        this.btnNull.addActionListener(this);

        this.btnIs.addActionListener(this);
        this.btnIsNot.addActionListener(this);

        this.btnRemoveLastOne = new JButton("Apagar último inserido");
        this.btnRemoveLastOne.addActionListener(this);

        this.btnRemoveAll = new JButton("Apagar tudo");
        this.btnRemoveAll.addActionListener(this);

        this.addExtraComponent(new JLabel("Fonte:"), 0, 0, 1, 1);
        this.addExtraComponent(this.comboBoxSource, 1, 0, 1, 1);
        this.addExtraComponent(new JLabel("Coluna:"), 0, 1, 1, 1);
        this.addExtraComponent(this.comboBoxColumn, 1, 1, 1, 1);
        this.addExtraComponent(this.btnColumnAdd, 2, 1, 1, 1);
        this.addExtraComponent( new JLabel("Operações:"), 0, 2, 1, 1);
        this.addExtraComponent(this.comboBoxOperator, 1, 2, 1, 1);
        this.addExtraComponent(this.btnOperatorAdd, 2, 2, 1, 1);
        this.addExtraComponent(new JLabel("Operadores Lógicos: "), 0, 3, 1, 1);
        this.addExtraComponent(this.comboBoxLogicalOperator, 1, 3, 1, 1);
        this.addExtraComponent(this.btnLogicalOperatorAdd, 2, 3, 1, 1);
        this.addExtraComponent(new JLabel("Números: "), 0, 4, 1, 1);
        this.addExtraComponent(this.formattedTextFieldNumber, 1, 4, 1, 1);
        this.addExtraComponent(this.btnNumberAdd, 2, 4, 1, 1);
        this.addExtraComponent(new JLabel("Strings: "), 0, 5, 1, 1);
        this.addExtraComponent(this.textFieldString, 1, 5, 1, 1);
        this.addExtraComponent(this.btnStringAdd, 2, 5, 1, 1);
        this.addExtraComponent(this.btnNull, 0, 6, 1, 1);
        this.addExtraComponent(this.btnIs, 1, 6, 1, 1);
        this.addExtraComponent(this.btnIsNot, 2, 6, 1, 1);
        this.addExtraComponent(this.btnRemoveLastOne, 0, 7, 1, 1);
        this.addExtraComponent(this.btnRemoveAll, 1, 7, 1, 1);
        this.addExtraComponent(new JScrollPane(this.textPane), 0, 8, 3, 3);

        this.setPreviousArgs();

        this.pack();
        this.setLocationRelativeTo(null);

        this.setVisible(true);

	}

	@Override
	protected void setPreviousArgs() {

		if(!this.previousArguments.isEmpty())
            this.insertString(this.previousArguments.get(0));

	}

	private void insertString(String txt){

		try {
            this.textPane.getDocument().insertString(this.textPane.getCaretPosition(), txt, null);
		} catch (BadLocationException error) {
			error.printStackTrace();
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == this.btnColumnAdd) {

            this.insertString(" ");
            this.insertString(this.comboBoxSource.getSelectedItem()+"."+ this.comboBoxColumn.getSelectedItem());

		}

		if (e.getSource() == this.btnOperatorAdd) {

            this.insertString(" ");
            this.insertString((String) this.comboBoxOperator.getSelectedItem());

		}

		if (e.getSource() == this.btnLogicalOperatorAdd) {

            this.insertString(" ");
            this.insertString(Objects.requireNonNull(this.comboBoxLogicalOperator.getSelectedItem()).toString());

		}

		if (e.getSource() == this.btnNumberAdd && !this.formattedTextFieldNumber.getText().isEmpty()) {

            this.insertString(" ");
            this.insertString(this.formattedTextFieldNumber.getText());

		}

		if (e.getSource() == this.btnStringAdd && !this.textFieldString.getText().isEmpty()) {

            this.insertString(" ");
            this.insertString("'" + this.textFieldString.getText() + "'");

		}

		if (e.getSource() == this.btnRemoveLastOne && !this.textPane.getText().isEmpty()) {

			String text = this.textPane.getText();
			Pattern pattern = Pattern.compile("[^']*\\s");
			Matcher matcher = pattern.matcher(text);

			if (matcher.find()) {

				int lastIndex = matcher.end() - 1;
				StyledDocument doc = this.textPane.getStyledDocument();
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

		if(this.btnNull == e.getSource()) {
            this.insertString(" ");
            this.insertString("null");
		}

		if(this.btnIs == e.getSource()){
            this.insertString(" ");
            this.insertString("is");
		}

		if(this.btnIsNot == e.getSource()){
            this.insertString(" ");
            this.insertString("is not");
		}

		if (e.getSource() == this.btnRemoveAll && !this.textPane.getText().isEmpty())
            this.textPane.setText("");

		if (e.getSource() == this.readyButton) {

            this.arguments.add(this.textPane.getText());
            this.onReadyButtonClicked();

		}

		if (e.getSource() == this.cancelButton)
            this.closeWindow();

	}

	protected void closeWindow() {
        this.dispose();
	}
}
