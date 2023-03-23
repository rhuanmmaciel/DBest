package gui.frames;

import java.awt.EventQueue;
import java.awt.Window;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
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
import sgbd.prototype.ComplexRowData;
import sgbd.query.Operator;
import sgbd.query.Tuple;

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
		
		List<List<String>> data = cell.getContent();
        String[] columnsNameArray = cell.getColumnsName().stream().toArray(String[]::new); 
		
        System.out.println(data.toString().toString());
        
		if(data != null && !data.isEmpty() && !data.get(0).isEmpty()) {
			
		
			String[][] dataArray = data.stream()
	                .map(l -> l.stream().toArray(String[]::new))
	                .toArray(String[][]::new);;
	                
			sortColumns(cell.getData(), columnsNameArray);
			
			table = new JTable(dataArray, columnsNameArray);
		
		}else {
			
			table = new JTable(new String[0][], columnsNameArray);
			
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

	private void sortColumns(Operator op, String[] columnsName) {
		
	    Operator aux = op;
	    aux.open();

	    Tuple tuple = aux.next();
	    List<String> keyOrder = new ArrayList<String>();

        for (Map.Entry<String, ComplexRowData> line : tuple) {
            for (Map.Entry<String, byte[]> data : line.getValue()) {
                keyOrder.add(data.getKey());
            }
        }

	    Arrays.sort(columnsName, Comparator.comparingInt(keyOrder::indexOf));
	    aux.close();
	}

}