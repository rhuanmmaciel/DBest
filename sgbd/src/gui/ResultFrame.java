package gui;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class ResultFrame extends JFrame {

	private JPanel contentPane;
	private JTable table;
	
	public static void main(List<List<String>> data) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ResultFrame frame = new ResultFrame(data);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public ResultFrame(List<List<String>> data) {
		
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
		
		setBounds(100, 100, 1400, 600);
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
