package gui.frames.forms;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import entities.Column;
import entities.TableCell;
import util.TableCreator;

@SuppressWarnings("serial")
public class FormFrameCreateTable extends JDialog implements ActionListener, DocumentListener{

	private JPanel contentPane;
	private JTable table;
	private DefaultTableModel model;
	private JButton btnCreateTable;
	private JButton btnCancel;
	private JButton btnAddRow;
	private JButton btnAddColumn;
	private JButton btnCreateData;
	private JTextField textFieldTableName;
	private TableCell tableCell;
	private AtomicReference<TableCell> tableCellReference;
	private List<Column> columns;
	
	public static void main(AtomicReference<TableCell> tableCell) {
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
	
	public FormFrameCreateTable(AtomicReference<TableCell> tableCell) {
		
		super((Window)null);
		setModal(true);
		
		columns = new ArrayList<>();
		this.tableCellReference = tableCell;
		this.tableCell = tableCell.get();
				
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
		
		textFieldTableName = new JTextField();
		textFieldTableName.getDocument().addDocumentListener(this);
		textFieldTableName.setColumns(10);
		
		JLabel lblTableName = new JLabel("Nome da tabela:");
		
		btnCreateData = new JButton("Gerador de dados");
		btnCreateData.addActionListener(this);
		
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
								.addComponent(btnCreateData)
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
								.addComponent(btnCreateData))
							.addGap(55))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(71)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(btnCreateTable)
								.addComponent(btnCancel))
							.addContainerGap())))
		);
		
		addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent e) {
				  
				tableCellReference.set(null);
    
			}
			
		 });
		 
		updateButtons();
		contentPane.setLayout(gl_contentPane);
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == btnCancel) {
			
			tableCellReference.set(null);
			dispose();
			
		}
		if(e.getSource() == btnCreateTable) {
			
			createTable();
			
		}
		if(e.getSource() == btnAddColumn) {
			
			new FormFrameAddColumn(this.model, columns);
			
		}
		if(e.getSource() == btnAddRow) {
			
			model.insertRow(table.getRowCount(),new Object[]{});
			
		}
		if(e.getSource() == btnCreateData) {
			
			new FormFrameRandomData(columns, model, table);
			
		}
		
		updateButtons();
		
	}	
	
	@Override
	public void insertUpdate(DocumentEvent e) {

		updateButtons();
		
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		
		updateButtons();
		
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		
		updateButtons();
		
	}
	
	private void updateButtons() {
		
		btnCreateTable.setEnabled(table.getRowCount() > 0 && !textFieldTableName.getText().isEmpty());
		btnAddRow.setEnabled(table.getColumnCount() > 0);
		btnCreateData.setEnabled(table.getRowCount() > 0);
		updateToolTipText();	
		
	}
	
	private void updateToolTipText() {
		
		String btnCreateDataToolTipText = new String();
		String btnAddRowToolTipText = new String();
		String btnCreateTableToolTipText = new String();
		
		if(table.getColumnCount() <= 0) {
			
			btnCreateDataToolTipText = btnAddRowToolTipText = btnCreateTableToolTipText = "- Não existem colunas na tabela";
			
		}else if(table.getRowCount() <= 0) {
			
			btnCreateDataToolTipText = btnCreateTableToolTipText = "- Não existem linhas na tabela";
			
		}else if(textFieldTableName.getText().isEmpty()) {
			
			btnCreateTableToolTipText = "- A table não possui nome";
			
		}
		
		UIManager.put("ToolTip.foreground", Color.RED);
		
		btnAddRow.setToolTipText(btnAddRowToolTipText.isEmpty() ? null : btnAddRowToolTipText);
		btnCreateData.setToolTipText(btnCreateDataToolTipText.isEmpty() ? null : btnCreateDataToolTipText);	
		btnCreateTable.setToolTipText(btnCreateTableToolTipText.isEmpty() ? null : btnCreateTableToolTipText);
		
	}
	
	private void createTable() {
		
		List<String> columnsName = new ArrayList<>();
		
		for(int i = 0; i < table.getColumnCount(); i++)
			columnsName.add(table.getColumnName(i));
		
		List<List<String>> lines = new ArrayList<>();
		lines.add(columnsName);
		
		for(int i = 0; i < table.getRowCount(); i++) {
				
			List<String> line = new ArrayList<>();
			for(int j = 0; j < table.getColumnCount(); j++) {
				
				if(table.getValueAt(i, j) == null || table.getValueAt(i, j).toString() == null)
					line.add("");
				
				else
					line.add(table.getValueAt(i, j).toString());
				
			}
			lines.add(line);
			
		}
		
		boolean exit = false;		
		AtomicReference<Boolean> exitReference = new AtomicReference<>(exit);
		
		new FormFramePrimaryKey(lines, exitReference);
		
		if(!exitReference.get()) {
		
			lines.remove(0);
			TableCreator.createTable(tableCell, textFieldTableName.getText(), columns, lines);
		
		}else {
			
			tableCellReference.set(null);
			
		}
				
		dispose();		
		
	}

}
