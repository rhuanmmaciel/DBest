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
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

@SuppressWarnings("serial")
public class FormFramePrimaryKey extends JDialog implements ActionListener{

	private JPanel contentPane;
	private JTable table;
	private JButton btnPickColumn;
	private JButton btnCreatePK;
	private AtomicReference<Boolean> exitReference;
	private StringBuilder pkName;
	
	public static void main(List<List<String>> data, StringBuilder pkName, AtomicReference<Boolean> exitReference) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FormFramePrimaryKey frame = new FormFramePrimaryKey(data, pkName, exitReference);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public FormFramePrimaryKey(List<List<String>> data, StringBuilder pkName, AtomicReference<Boolean> exitReference) {
		
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
		
		this.exitReference = exitReference;
		this.pkName = pkName;
		
		initializeGUI();
		
	}

	private void initializeGUI() {
		

		btnPickColumn = new JButton("Escolher coluna");
		btnPickColumn.addActionListener(this);
		
		btnCreatePK= new JButton("Criar coluna com a Chave");	
		btnCreatePK.addActionListener(this);
		
		setBounds(100, 100, 1400, 600);
		setLocationRelativeTo(null);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		
		table.setShowHorizontalLines(true);
	    table.setGridColor(Color.blue);
	    table.setColumnSelectionAllowed(true);
	    table.setRowSelectionAllowed(false);
	    
	    table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
	        @Override
	        public void valueChanged(ListSelectionEvent event) {
	        	
	        	getVerification();
	        	
	        }
	    });
	    
		JScrollPane scrollPane = new JScrollPane(table);
		
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
		
		addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent e) {
				  
				exitReference.set(true);
				dispose();
				
			}
			
		 });
		
		getVerification();
		
		contentPane.setLayout(gl_contentPane);
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == btnPickColumn) {
			
			pkName.append(table.getColumnName(table.getSelectedColumn()));
			dispose();
			
		}
		if(e.getSource() == btnCreatePK) {
			
			pkName.append("Id_PK");
			dispose();
			
		}
		
		getVerification();
		
	}	
	
	private void getVerification() {
		
		boolean noColumnSelected = table.getSelectedColumn() == -1;
		boolean repeatedElements = false;
		boolean emptyCell = false;
		
		if(!noColumnSelected) {
		
			List<String> columnData = new ArrayList<>();
			for(int i = 0; i < table.getRowCount(); i++) {
				
				columnData.add(String.valueOf(table.getValueAt(i, table.getSelectedColumn())));			
				
			}
			
			TreeSet<String> unique = new TreeSet<>();
			unique.addAll(columnData);
			
			List<String> auxList = new ArrayList<>(unique);
			columnData.sort(null);
			
			repeatedElements = !auxList.equals(columnData);
			emptyCell = columnData.contains("") || columnData.contains(null);
		
		}
		
		updateToolTipText(repeatedElements, emptyCell, noColumnSelected);
		
		btnPickColumn.setEnabled(!repeatedElements && !emptyCell && !noColumnSelected);
		
	}
	
	private void updateToolTipText(boolean repeatedElements, boolean emptyCell, boolean noColumnSelected) {
		
		String btnPickColumnToolTipText = new String();
		
		if(noColumnSelected) {
			
			btnPickColumnToolTipText = "- Não foi selecionada nenhuma coluna";
			
		}else if(repeatedElements) {
			
			btnPickColumnToolTipText = "- Não podem existir elementos repetidos na PK";
			
		}else if(emptyCell) {
			
			btnPickColumnToolTipText = "- Não podem existir valores nulos na PK";
			
		}		
		
		UIManager.put("ToolTip.foreground", Color.RED);
		
		btnPickColumn.setToolTipText(btnPickColumnToolTipText.isEmpty() ? null : btnPickColumnToolTipText);
		
	}
	
}