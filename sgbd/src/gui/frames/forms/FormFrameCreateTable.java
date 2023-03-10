package gui.frames.forms;

import java.awt.EventQueue;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import entities.Column;
import entities.TableCell;
import util.TableCreator;

@SuppressWarnings("serial")
public class FormFrameCreateTable extends JDialog implements ActionListener{

	private JPanel contentPane;
	private JTable table;
	private DefaultTableModel model;
	private JButton btnCreateTable;
	private JButton btnCancel;
	private JButton btnAddRow;
	private JButton btnAddColumn;
	private JButton btnRandomData;
	private JTextField textFieldTableName;
	private TableCell tableCell;
	private List<Column> columns;
	
	public static void main(TableCell tableCell) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FormFrameCreateTable frame = new FormFrameCreateTable(tableCell);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public FormFrameCreateTable(TableCell tableCell) {
		
		super((Window)null);
		setModal(true);
		
		columns = new ArrayList<>();
		this.tableCell = tableCell;
				
		initializeGUI();
		
	}

	private void initializeGUI() {
		
		model = new DefaultTableModel(); 
		table = new JTable(model);
		
		btnCreateTable = new JButton("Criar tabela");
		btnCreateTable.addActionListener(this);
		
		btnCancel= new JButton("Cancelar");	
		btnCancel.addActionListener(this);
		
		setBounds(100, 100, 1250, 600);
		setLocationRelativeTo(null);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
	    
		JScrollPane scrollPane = new JScrollPane(table);
		
		JLabel lblNewLabel = new JLabel("Crie sua tabela: ");
		
		btnAddColumn = new JButton("Adicionar coluna");
		btnAddColumn.addActionListener(this);
		
		btnAddRow = new JButton("Adicionar linha");
		btnAddRow.addActionListener(this);
		btnAddRow.setEnabled(false);
		
		textFieldTableName = new JTextField();
		textFieldTableName.setColumns(10);
		
		JLabel lblTableName = new JLabel("Nome da tabela:");
		
		btnRandomData = new JButton("Gerar dados aleatórios");
		btnRandomData.addActionListener(this);
		btnRandomData.setEnabled(false);
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(37)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblNewLabel)
							.addGap(303)
							.addComponent(lblTableName)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(textFieldTableName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addContainerGap())
						.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
							.addGroup(gl_contentPane.createSequentialGroup()
								.addComponent(btnAddColumn, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(btnAddRow)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(btnRandomData)
								.addGap(981))
							.addGroup(gl_contentPane.createSequentialGroup()
								.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
									.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 1171, GroupLayout.PREFERRED_SIZE)
									.addGroup(gl_contentPane.createSequentialGroup()
										.addComponent(btnCancel)
										.addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(btnCreateTable)))
								.addContainerGap()))))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblNewLabel)
								.addComponent(lblTableName))
							.addGap(18))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(textFieldTableName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)))
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(btnAddColumn)
								.addComponent(btnAddRow)
								.addComponent(btnRandomData))
							.addGap(55))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(71)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(btnCreateTable)
								.addComponent(btnCancel))
							.addContainerGap())))
		);
		
		contentPane.setLayout(gl_contentPane);
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == btnCancel) {
			
			dispose();
			
		}
		if(e.getSource() == btnCreateTable) {
			
			getVerification();
			
		}
		if(e.getSource() == btnAddColumn) {
			
			new FormFrameAddColumn(this.model, columns);
			
		}
		if(e.getSource() == btnAddRow) {
			
			model.insertRow(table.getRowCount(),new Object[]{});
			
		}
		if(e.getSource() == btnRandomData) {
			
			new FormFrameRandomData(columns, model, table);
			
		}
		btnAddRow.setEnabled(table.getColumnCount() > 0);
		btnRandomData.setEnabled(table.getRowCount() > 0);
		
	}	
	
	private void getVerification() {
		
		boolean isThereAnyNullValue = false;
		for(int i = 0; i < table.getColumnCount(); i++) {
			
			for(int j = 0; j < table.getRowCount(); j++) {
				
				if(table.getValueAt(j, i) == null) isThereAnyNullValue = true;
				
			}
			
		}
		
		if(table.getRowCount() <= 0) {
		
			JOptionPane.showMessageDialog(null, "Não é possível criar tabela sem linhas!", "Erro", JOptionPane.ERROR_MESSAGE);
			return;
		
		}
		
		if(isThereAnyNullValue) {
			
			JOptionPane.showMessageDialog(null, "Não é possível criar tabela com campo em branco!", "Erro", JOptionPane.ERROR_MESSAGE);
			return;
		
		}
		
		if(textFieldTableName.getText().isEmpty()) {
			
			JOptionPane.showMessageDialog(null, "Não é possível criar tabela sem nome!", "Erro", JOptionPane.ERROR_MESSAGE);
			return;
			
		}
		
		List<String> columnsName = new ArrayList<>();
		
		for(int i = 0; i < table.getColumnCount(); i++)
			columnsName.add(table.getColumnName(i));
		
		List<List<String>> lines = new ArrayList<>();
		lines.add(columnsName);
		
		for(int i = 0; i < table.getRowCount(); i++) {
				
			List<String> line = new ArrayList<>();
			for(int j = 0; j < table.getColumnCount(); j++) {
				
				line.add(table.getValueAt(i, j).toString());
				
			}
			lines.add(line);
			
		}
		
		new FormFramePrimaryKey(lines);
		lines.remove(0);
		
		TableCreator.createTable(tableCell, textFieldTableName.getText(), columns, lines);
		dispose();
		
	}
}
