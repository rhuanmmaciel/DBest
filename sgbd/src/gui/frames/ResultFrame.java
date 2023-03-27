package gui.frames;

import java.awt.EventQueue;
import java.awt.Window;
import java.util.Map;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;

import entities.Cell;

@SuppressWarnings("serial")
public class ResultFrame extends JDialog{

	private JPanel contentPane;
	private JTable table;
	
	public static void main(Cell cell) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ResultFrame frame = new ResultFrame(cell);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public ResultFrame(Cell cell) {
	
		super((Window)null);
		setModal(true);
		
		Map<Integer, Map<String, String>> data = cell.getMapContent();
		
		
		
		if(data != null && !data.isEmpty() && !data.get(0).isEmpty()) {
			
			String[] columnsNameArray = new String[data.get(0).size()];
			
			int i = 0;
			for(String columnName: data.get(0).keySet()) {
				
				columnsNameArray[i] = columnName;
				i++;
				
			}
			
			String[][] dataArray = new String[data.size()][];
			int j = 0;
		    for(Map<String, String> row : data.values()) {
		    	
		    	String[] line = new String[data.get(0).size()];
		    	int k = 0;
		    	for(String key : row.keySet()) {
		    		
		    		line[k] = row.get(key);
		    		k++;
		    		
		    	}
		    	
		    	dataArray[j] = line;
		    	j++;
		    }
			
			
			table = new JTable(dataArray, columnsNameArray);
		
		}else {
			
			table = new JTable(new String[0][], cell.getColumnsName().stream().toArray(String[]::new));
			
		}
		
		table.setEnabled(false);

		initializeGUI();
		
	}
	
	private void initializeGUI(){
		
		setBounds(100, 100, 1400, 600);
		setLocationRelativeTo(null);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		
		JScrollPane scrollPane = new JScrollPane(table);
		
		JLabel lblNewLabel = new JLabel("Resultado");
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(27)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(lblNewLabel)
						.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 1300, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(33, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(lblNewLabel)
					.addGap(19)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(20, Short.MAX_VALUE))
		);
		
		contentPane.setLayout(gl_contentPane);
		this.setVisible(true);
	}

}