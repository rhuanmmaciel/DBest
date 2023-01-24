package gui.forms;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class FormFramePrimaryKey extends JDialog implements ActionListener{

	private JPanel contentPane;
	private JTable table;
	private JButton btnPickColumn;
	private JButton btnCreatePK;
	private static Integer[] values;
	private static String name;
	
	public static void main(List<List<String>> data) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FormFramePrimaryKey frame = new FormFramePrimaryKey(data);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public FormFramePrimaryKey(List<List<String>> data) {
		
		super((Window)null);
		setModal(true);
		
		List<String> columnsName = new ArrayList<>();
		
		if(data != null && !data.isEmpty()) {
			
			columnsName = data.get(0);
			List<String> firstLine = new ArrayList<>(data.remove(0));
		
			String[][] dataArray = data.stream()
	                .map(l -> l.stream().toArray(String[]::new))
	                .toArray(String[][]::new);;
	                
	        data.add(0, firstLine);        
	                
	        String[] columnsNameArray = columnsName.stream().toArray(String[]::new); 
			
			table = new JTable(dataArray, columnsNameArray);
		
		}
		
		btnPickColumn = new JButton("Escolher coluna");
		btnPickColumn.addActionListener(this);
		
		btnCreatePK= new JButton("Criar coluna com a Chave");	
		btnCreatePK.addActionListener(this);
		
		setBounds(100, 100, 1400, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		table.setShowHorizontalLines(true);
	    table.setGridColor(Color.blue);
	    table.setColumnSelectionAllowed(true);
	    table.setRowSelectionAllowed(false);
	    table.setColumnSelectionInterval(2,2);
	    
		JScrollPane scrollPane = new JScrollPane(table);
		
		values = new Integer[table.getRowCount()];
		
		JLabel lblNewLabel = new JLabel("Selecione a Chave Primária da tabela: ");
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(27)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(lblNewLabel)
						.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 1300, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(63, Short.MAX_VALUE))
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap(1110, Short.MAX_VALUE)
					.addComponent(btnCreatePK)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnPickColumn)
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(lblNewLabel)
					.addGap(19)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 82, Short.MAX_VALUE)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnPickColumn)
						.addComponent(btnCreatePK))
					.addContainerGap())
		);
		
		contentPane.setLayout(gl_contentPane);
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == btnPickColumn) {
			
			getVerification();
			
		}
		if(e.getSource() == btnCreatePK) {
			
			returnColumn(true);
			
		}
		
	}	
	
	private void getVerification() {
		
		List<String> columnData = new ArrayList<>();
		for(int i = 0; i < table.getRowCount(); i++) {
			
			columnData.add(String.valueOf(table.getValueAt(i, table.getSelectedColumn())));			
			
		}
		
		TreeSet<String> unique = new TreeSet<>();
		unique.addAll(columnData);
		
		List<String> auxList = new ArrayList<>(unique);
		columnData.sort(null);
		
		boolean repeatedElements = !auxList.equals(columnData);
		boolean emptyCell = columnData.contains("") || columnData.contains(null);
		
		if(repeatedElements) {
			JOptionPane.showMessageDialog(null, "A coluna possui valores repetidos!", "Erro", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		if(emptyCell) {
			JOptionPane.showMessageDialog(null, "A coluna possui valor em branco ou vazio!", "Erro", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		returnColumn(false);
		
	}
	
	private void returnColumn(boolean createColumn) {
		
		if(createColumn) {
			
			name = "Chave";
			for(int i = 0; i < table.getRowCount(); i++) {
				
				values[i] = i;
				
			}
			
		}
		if(!createColumn) {
			
			name = table.getColumnName(table.getSelectedColumn());
			for(int i = 0; i < table.getRowCount(); i++) {
				
				values[i] = null;
				
			}
			
		}
		
        dispose();
		
	}

	public static Integer[] getValues() {
		
		return values;
		
	}
	
	public static String getColumnName() {
		
		return name;
		
	}
	
	
}
