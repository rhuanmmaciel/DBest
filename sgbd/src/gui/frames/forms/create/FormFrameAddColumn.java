package gui.frames.forms.create;

import java.awt.Color;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Objects;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import entities.Column;
import enums.ColumnDataType;
import gui.frames.forms.FormBase;

public class FormFrameAddColumn extends FormBase implements ActionListener, DocumentListener{

	private JTextField txtColumnName;
	private JComboBox<Object> comboBox;
	private final JCheckBox pkCheckBox = new JCheckBox();
	private DefaultTableModel table;
	private JButton btnReady;
	private JButton btnCancel;
	private JLabel lblColumnName;
	private List<Column> columns;
	
	public FormFrameAddColumn(DefaultTableModel table, List<Column> columns) {
		
		super((Window)null);
		setModal(true);
		
		this.columns = columns;
		this.table = table;
		
		initializeGUI();
		
	}
	
	private void initializeGUI() {

		setLocationRelativeTo(null);
		
		setContentPane(contentPane);

		lblColumnName = new JLabel("Nome da coluna");
		
		txtColumnName = new JTextField();
		txtColumnName.getDocument().addDocumentListener(this);
		txtColumnName.setColumns(10);
		
		JLabel lblTypeq = new JLabel("Tipo de dado");
		
		String[] options = {"None", "Integer", "Float", "Character", "String", "Boolean"};
		comboBox = new JComboBox<Object>(options);
		

		
		updateButton();
		pack();
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == btnReady) {
			
			ColumnDataType type;

			boolean isPK = pkCheckBox.isSelected();

			String itemSelected = comboBox.getSelectedItem().toString();
			
			if(Objects.equals(itemSelected, "None")) {
				
				type = ColumnDataType.NONE;
				
			} else if(Objects.equals(itemSelected, "Integer")) {
				
				type = ColumnDataType.INTEGER;
				
			}else if(Objects.equals(itemSelected, "Float")) {
				
				type = ColumnDataType.FLOAT;
				
			}else if(Objects.equals(itemSelected, "String")) {
				
				type = ColumnDataType.STRING;
				
			}else if(Objects.equals(itemSelected, "Character")) {
				
				type = ColumnDataType.CHARACTER;
				
			}else if(Objects.equals(itemSelected, "Boolean")) {
				
				type = ColumnDataType.BOOLEAN;
				
			}else {
				
				type = ColumnDataType.NONE;
				
			}
			
			table.addColumn(txtColumnName.getText().replaceAll("[^\\p{Alnum}]", ""));
//			columns.add(new Column(txtColumnName, , type));
			closeWindow();

		}
		if(e.getSource() == btnCancel) {
			
			closeWindow();

		}
		
	}

	@Override
	public void insertUpdate(DocumentEvent e) {

		updateButton();
		
	}

	@Override
	public void removeUpdate(DocumentEvent e) {

		updateButton();
		
	}

	@Override
	public void changedUpdate(DocumentEvent e) {

		updateButton();
		
	}	
	
	private void updateButton() {
		
		btnReady.setEnabled(!txtColumnName.getText().isEmpty() &&
							table.findColumn(txtColumnName.getText()) == -1
							&& txtColumnName.getText().matches("^[\\p{Alnum}]*$"));
		updateToolTipText();
		
	}
	
	private void updateToolTipText() {
		
		String btnCreateColumnToolTipText = "";
		
		if(txtColumnName.getText().isEmpty()) {
			
			btnCreateColumnToolTipText = "- Não existe nome para a coluna";
			
		}else if(table.findColumn(txtColumnName.getText()) != -1) {
			
			btnCreateColumnToolTipText = "- Não é possível 2 colunas terem o mesmo nome";
			
		}else if(!txtColumnName.getText().matches("^[\\p{Alnum}]*$")) {
			
			btnCreateColumnToolTipText = "- Apenas letras e números podem ser utilizados para o nome";
			
		}
		
		UIManager.put("ToolTip.foreground", Color.RED);
		
		btnReady.setToolTipText(btnCreateColumnToolTipText.isEmpty() ? null : btnCreateColumnToolTipText);
		
	}

	@Override
	protected void closeWindow() {
		dispose();
	}
}
