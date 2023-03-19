package gui.frames.forms;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Objects;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import entities.Column;
import enums.ColumnDataType;

@SuppressWarnings("serial")
public class FormFrameAddColumn extends JDialog implements ActionListener, DocumentListener{

	private JPanel contentPane;
	private JTextField txtColumnName;
	private JComboBox<Object> comboBox;
	private DefaultTableModel table;
	private JButton btnReady;
	private JButton btnCancel;
	private JLabel lblColumnName;
	private List<Column> columns;

	public static void main(DefaultTableModel table, List<Column> columns) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FormFrameAddColumn frame = new FormFrameAddColumn(table, columns);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public FormFrameAddColumn(DefaultTableModel table, List<Column> columns) {
		
		super((Window)null);
		setModal(true);
		
		this.columns = columns;
		this.table = table;
		
		initializeGUI();
		
	}
	
	private void initializeGUI() {

		
		setBounds(100, 100, 312, 263);
		setLocationRelativeTo(null);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		
		btnReady = new JButton("Pronto");
		btnReady.addActionListener(this);
		
		btnCancel = new JButton("Cancelar");
		btnCancel.addActionListener(this);
		
		lblColumnName = new JLabel("Nome da coluna");
		
		txtColumnName = new JTextField();
		txtColumnName.getDocument().addDocumentListener(this);
		txtColumnName.setColumns(10);
		
		JLabel lblTypeq = new JLabel("Tipo de dado");
		
		String[] options = {"None", "Integer", "Float", "Character", "String", "Boolean"};
		comboBox = new JComboBox<Object>(options);
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap(582, Short.MAX_VALUE)
					.addComponent(btnCancel)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnReady)
					.addGap(6))
				.addGroup(Alignment.LEADING, gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(lblColumnName)
						.addComponent(lblTypeq))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(txtColumnName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(comboBox, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblColumnName)
						.addComponent(txtColumnName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(15)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(lblTypeq)
						.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED, 109, Short.MAX_VALUE)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnCancel)
						.addComponent(btnReady))
					.addContainerGap())
		);
		
		updateButton();
		contentPane.setLayout(gl_contentPane);
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == btnReady) {
			
			ColumnDataType type;
			
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
			
			table.addColumn(txtColumnName.getText().toUpperCase().replaceAll("[^\\p{Alnum}_-]", ""));
			columns.add(new Column(txtColumnName.getText().replaceAll("[^\\p{Alnum}_-]", ""), type));
			dispose();
			
		}
		if(e.getSource() == btnCancel) {
			
			dispose();
			
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
							table.findColumn(txtColumnName.getText().toUpperCase().replaceAll("[^\\p{Alnum}_-]", "")) == -1);
		updateToolTipText();
		
	}
	
	private void updateToolTipText() {
		
		String btnCreateColumnToolTipText = "";
		
		if(txtColumnName.getText().isEmpty()) {
			
			btnCreateColumnToolTipText = "- Não existe nome para a coluna";
			
		}else if(table.findColumn(txtColumnName.getText().toUpperCase().replaceAll("[^\\p{Alnum}_-]", "")) != -1) {
			
			btnCreateColumnToolTipText = "- Não é possível 2 colunas terem o mesmo nome";
			
		}
		
		UIManager.put("ToolTip.foreground", Color.RED);
		
		btnReady.setToolTipText(btnCreateColumnToolTipText.isEmpty() ? null : btnCreateColumnToolTipText);
		
	}
}
