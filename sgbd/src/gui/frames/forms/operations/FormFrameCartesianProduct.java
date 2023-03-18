package gui.frames.forms.operations;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

import entities.Cell;
import entities.Column;
import entities.OperatorCell;
import entities.util.TableFormat;
import sgbd.query.Operator;
import sgbd.query.binaryop.BlockNestedLoopJoin;

@SuppressWarnings("serial")
public class FormFrameCartesianProduct extends JFrame implements ActionListener {

	private JPanel contentPane;
	private JButton btnReady;
	private String tabelName1;
	private String tableName2;
	
	private Cell cell;
	private Cell parentCell1;
	private Cell parentCell2;
	private Object jCell;
	private mxGraph graph;
	private JTextField table1;
	private JTextField table2;
	
	public static void main(Object cell, List<Cell> cells,mxGraph graph) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FormFrameCartesianProduct frame = new FormFrameCartesianProduct(cell, cells,graph);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public FormFrameCartesianProduct(Object cell, List<Cell> cells, mxGraph graph) {
		
		this.setVisible(true);
		
		this.cell = cells.stream().filter(x -> x.getCell().equals(((mxCell)cell))).findFirst().orElse(null);
		this.parentCell1 = this.cell.getParents().get(0);
		this.parentCell2 = this.cell.getParents().get(1);
		this.jCell = cell;
		this.graph = graph;
		initializeGUI();
		
	}
	
	private void initializeGUI() {
		
		setBounds(100, 100, 450, 148);
		setLocationRelativeTo(null);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		
		tabelName1 = parentCell1.getName();
	
		tableName2 = parentCell2.getName();
		
		JLabel lblNewLabel = new JLabel("Tabela");
		
		JLabel lblNewLabel_1 = new JLabel("Tabela");
		
		JLabel lblNewLabel_2 = new JLabel("X");
		
		btnReady = new JButton("Pronto");
		btnReady.addActionListener(this);
		
		table1 = new JTextField();
		table1.setColumns(10);
		
		table2 = new JTextField();
		table2.setColumns(10);
		
		table1.setText(tabelName1);
		table2.setText(tableName2);

		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(40)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(lblNewLabel)
						.addComponent(table1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(26)
					.addComponent(lblNewLabel_2, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(table2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(30)
							.addComponent(btnReady))
						.addComponent(lblNewLabel_1))
					.addContainerGap(107, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel)
						.addComponent(lblNewLabel_1))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addComponent(lblNewLabel_2, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
						.addComponent(table1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnReady)
						.addComponent(table2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(43, Short.MAX_VALUE))
		);
		contentPane.setLayout(gl_contentPane);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if(e.getSource() == btnReady) {
			executeOperation();
	        graph.getModel().setValue(jCell,tabelName1 + " X " + tableName2);
	        
		}
	}

	public void executeOperation() {
		
		Operator table_1 = parentCell1.getData();
		Operator table_2 = parentCell2.getData();
		
		Operator operator = new BlockNestedLoopJoin(table_1,table_2,(t1, t2) -> {
			return true;
        });
		
		
		
		operator.open();
		    
		List<Column> columns = new ArrayList<>(parentCell1.getColumns());
		columns.addAll(parentCell2.getColumns());
		
		((OperatorCell) cell).setOperator(operator, TableFormat.getRows(operator, columns));
		((OperatorCell)cell).setColumns();
		cell.setName(tabelName1 + " X " + tableName2);
		    
	    operator.close();
			
	    dispose();
		
	}
}
