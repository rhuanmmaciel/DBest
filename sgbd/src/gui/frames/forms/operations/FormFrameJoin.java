package gui.frames.forms.operations;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
public class FormFrameJoin extends JFrame implements ActionListener {

	private JPanel contentPane;
	private JComboBox<?> colunasComboBox;
	private JComboBox<?> colunasComboBox_1;
	private JButton btnPronto;
	private List<String> columnsList_1;
	private List<String> columnsList_2;
	
	private Cell cell;
	private Cell parentCell1;
	private Cell parentCell2;
	private Object jCell;
	private mxGraph graph;
	
	public static void main(Object cell, List<Cell> cells,mxGraph graph) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FormFrameJoin frame = new FormFrameJoin(cell, cells,graph);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public FormFrameJoin(Object cell, List<Cell> cells, mxGraph graph) {
		
		this.setVisible(true);
		
		this.cell = cells.stream().filter(x -> x.getCell().equals(((mxCell)cell))).findFirst().orElse(null);
		this.parentCell1 = this.cell.getParents().get(0);
		this.parentCell2 = this.cell.getParents().get(1);
		this.jCell = cell;
		this.graph = graph;
		initializeGUI();
		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initializeGUI() {
		
		setBounds(100, 100, 450, 148);
		setLocationRelativeTo(null);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		
		columnsList_1 = new ArrayList<String>();
		columnsList_1 = parentCell1.getColumnsName();
		
		colunasComboBox = new JComboBox(columnsList_1.toArray(new String[0]));
		
		columnsList_2 = new ArrayList<String>();
		columnsList_2 = parentCell2.getColumnsName();
		
		colunasComboBox_1 = new JComboBox(columnsList_2.toArray(new String[0]));
		
		JLabel lblNewLabel = new JLabel("Coluna");
		
		JLabel lblNewLabel_1 = new JLabel("Coluna");
		
		JLabel lblNewLabel_2 = new JLabel("=");
		
		btnPronto = new JButton("Pronto");
		btnPronto.addActionListener(this);

		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(40)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(colunasComboBox, GroupLayout.PREFERRED_SIZE, 104, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel))
					.addGap(18)
					.addComponent(lblNewLabel_2, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(colunasComboBox_1, GroupLayout.PREFERRED_SIZE, 105, GroupLayout.PREFERRED_SIZE)
							.addGap(28)
							.addComponent(btnPronto))
						.addComponent(lblNewLabel_1))
					.addContainerGap(91, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel)
						.addComponent(lblNewLabel_1))
					.addGap(5)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(colunasComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel_2, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE)
						.addComponent(colunasComboBox_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnPronto))
					.addContainerGap(198, Short.MAX_VALUE))
		);
		contentPane.setLayout(gl_contentPane);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if(e.getSource() == btnPronto) {
			executeOperation(colunasComboBox.getSelectedItem().toString(),colunasComboBox_1.getSelectedItem().toString());
	        graph.getModel().setValue(jCell,"|X|   "+ colunasComboBox.getSelectedItem().toString()+" = "+colunasComboBox_1.getSelectedItem().toString());
	        
		}
	}

	public void executeOperation(String item1, String item2) {
		
		Operator table_1 = parentCell1.getData();
		Operator table_2 = parentCell2.getData();
		
		Operator operator = new BlockNestedLoopJoin(table_1,table_2,(t1, t2) -> {
			return t1.getContent(parentCell1.getSourceTableName(item1)).getInt(item1) == t2.getContent(parentCell2.getSourceTableName(item2)).getInt(item2);
        });
		
		
		
		operator.open();
		    
		List<Column> columns = new ArrayList<>(parentCell1.getColumns());
		columns.addAll(parentCell2.getColumns());
		
		((OperatorCell) cell).setOperator(operator, TableFormat.getRows(operator, columns));
		((OperatorCell)cell).setColumns();
		cell.setName("|X|   " + colunasComboBox.getSelectedItem().toString()+" = "+colunasComboBox_1.getSelectedItem().toString());    
		
	    operator.close();
			
	    dispose();
		
	}
	
}
