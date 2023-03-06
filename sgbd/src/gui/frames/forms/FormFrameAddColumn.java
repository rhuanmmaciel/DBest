package gui.frames.forms;

import java.awt.EventQueue;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import entities.Column;
import enums.ColumnDataType;

@SuppressWarnings("serial")
public class FormFrameAddColumn extends JDialog implements ActionListener{

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
		
		contentPane.setLayout(gl_contentPane);
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == btnReady) {
			
			boolean isAllRight = true;
			
			if(txtColumnName.getText().isEmpty()) {
				
				JOptionPane.showMessageDialog(null, "A coluna não pode ficar sem nome!", "Erro", JOptionPane.ERROR_MESSAGE);
				isAllRight = false;
			
			}
			
			if(table.findColumn(txtColumnName.getText().toUpperCase()) != -1) {

				JOptionPane.showMessageDialog(null, "A tabela não pode possuir colunas com nomes iguais!", "Erro", JOptionPane.ERROR_MESSAGE);
				isAllRight = false;
				
			}
			
			if(isAllRight) {
				
				ColumnDataType type;
				
				String itemSelected = comboBox.getSelectedItem().toString();
				
				if(itemSelected == "None") {
					
					type = ColumnDataType.NONE;
					
				} else if(itemSelected == "Integer") {
					
					type = ColumnDataType.INTEGER;
					
				}else if(itemSelected == "Float") {
					
					type = ColumnDataType.FLOAT;
					
				}else if(itemSelected == "String") {
					
					type = ColumnDataType.STRING;
					
				}else if(itemSelected == "Character") {
					
					type = ColumnDataType.CHARACTER;
					
				}else if(itemSelected == "Boolean") {
					
					type = ColumnDataType.BOOLEAN;
					
				}else {
					
					type = ColumnDataType.NONE;
					
				}
				
				table.addColumn(txtColumnName.getText().toUpperCase());
				columns.add(new Column(txtColumnName.getText(), type));
				dispose();
			
			}
			
		}
		if(e.getSource() == btnCancel) {
			
			dispose();
			
		}
		
	}	
}
