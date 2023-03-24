package gui.frames.forms.operations;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

import entities.Cell;
import entities.OperatorCell;
import sgbd.query.Operator;
import sgbd.query.binaryop.UnionOperator;

@SuppressWarnings("serial")
public class FormFrameUnion extends JFrame implements ActionListener{

	private JPanel contentPane;
	private JComboBox<?> columnsComboBox1;
	private JComboBox<?> columnsComboBox2; 
	private List<String> columnsList1;
	private List<String> columnsList2;
	
	private JButton btnAdd1;
	private JButton btnAdd2;
	private JButton btnRemove1;
	private JButton btnRemove2;

	private JButton btnReady;
	private String textColumnsPicked1;
	private String textColumnsPicked2;
	private JTextArea textArea1;
	private JTextArea textArea2;
	
	private Cell cell;
	private Cell parentCell1;
	private Cell parentCell2;
	private Object jCell;
	private mxGraph graph;

	public static void main(Object cell, List<Cell> cells, mxGraph graph) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {					
					FormFrameProjection frame = new FormFrameProjection(cell, cells,graph);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public FormFrameUnion(Object cell, List<Cell> cells, mxGraph graph) {
		
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
		
		setBounds(100, 100, 995, 301);
		setLocationRelativeTo(null);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		setContentPane(contentPane);
		
		columnsList1 = new ArrayList<>();
		columnsList2 = new ArrayList<>();
		
		columnsList1 = parentCell1.getColumnsName();
		columnsList2 = parentCell2.getColumnsName();
		
		columnsComboBox1 = new JComboBox(columnsList1.toArray(new String[0]));
		columnsComboBox2 = new JComboBox(columnsList2.toArray(new String[0]));
		
		JLabel lblColumns1 = new JLabel("Colunas");
		JLabel lblColumns2 = new JLabel("Colunas");
		lblColumns2.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JLabel lblColumnsPicked1 = new JLabel("Tabela I");
		JLabel lblColumnsPicked2 = new JLabel("Tabela II");
		lblColumnsPicked2.setHorizontalAlignment(SwingConstants.RIGHT);
		
		textArea1 = new JTextArea();
		textArea2 = new JTextArea();
		
		btnAdd1 = new JButton("Add");
		btnAdd2 = new JButton("Add");
		btnAdd1.addActionListener(this);
		btnAdd2.addActionListener(this);
		
		btnRemove1 = new JButton("Remover");
		btnRemove2 = new JButton("Remover");
		btnRemove1.addActionListener(this);
		btnRemove2.addActionListener(this);
		
		btnReady = new JButton("Pronto");
		btnReady.addActionListener(this);
		
		GroupLayout groupLayout = new GroupLayout(contentPane);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap(33, Short.MAX_VALUE)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(columnsComboBox1, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblColumns1, GroupLayout.PREFERRED_SIZE, 99, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnAdd1, GroupLayout.PREFERRED_SIZE, 66, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnRemove1))
							.addGap(18)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(textArea1, GroupLayout.PREFERRED_SIZE, 223, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblColumnsPicked1))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(lblColumnsPicked2, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 151, GroupLayout.PREFERRED_SIZE)
								.addComponent(textArea2, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 223, GroupLayout.PREFERRED_SIZE))
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
									.addGap(18)
									.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
										.addComponent(columnsComboBox2, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
										.addComponent(btnAdd2, GroupLayout.PREFERRED_SIZE, 66, GroupLayout.PREFERRED_SIZE)
										.addComponent(btnRemove2, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE)))
								.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
									.addGap(119)
									.addComponent(lblColumns2, GroupLayout.PREFERRED_SIZE, 99, GroupLayout.PREFERRED_SIZE)))
							.addGap(58))
						.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
							.addComponent(btnReady)
							.addContainerGap())))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblColumns1, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblColumnsPicked1)
						.addComponent(lblColumns2, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblColumnsPicked2))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(columnsComboBox1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(17)
							.addComponent(btnAdd1)
							.addGap(17)
							.addComponent(btnRemove1))
						.addComponent(textArea1, GroupLayout.PREFERRED_SIZE, 175, GroupLayout.PREFERRED_SIZE)
						.addComponent(textArea2, GroupLayout.PREFERRED_SIZE, 175, GroupLayout.PREFERRED_SIZE)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(columnsComboBox2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(20)
							.addComponent(btnAdd2)
							.addGap(16)
							.addComponent(btnRemove2)))
					.addPreferredGap(ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
					.addComponent(btnReady)
					.addContainerGap())
		);
		contentPane.setLayout(groupLayout);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == btnAdd1){

			if(columnsComboBox1.getItemCount() > 0) {
				
				textColumnsPicked1 = textArea1.getText() + "\n" +columnsComboBox1.getSelectedItem().toString() ;
				columnsComboBox1.removeItemAt(columnsComboBox1.getSelectedIndex());
				textArea1.setText(textColumnsPicked1);
				
			}

		}else if(e.getSource() == btnRemove1) {
			
			System.out.println("botao remove");
			
		}else if(e.getSource() == btnAdd2){

			if(columnsComboBox2.getItemCount() > 0) {
				
				textColumnsPicked2 = textArea2.getText() + "\n" +columnsComboBox2.getSelectedItem().toString() ;
				columnsComboBox2.removeItemAt(columnsComboBox2.getSelectedIndex());
				textArea2.setText(textColumnsPicked2);
				
			}

		}else if(e.getSource() == btnRemove2) {
			
			System.out.println("botao remove II");
		
		}else if(e.getSource() == btnReady) {
	        
			executeOperation();
			dispose();
	        
		}
	}
	
	public void executeOperation() {
		
		Operator table1 = parentCell1.getData();
		Operator table2 = parentCell2.getData();
		
		List<String> selectedColumns1 = new ArrayList<>(Arrays.asList(textArea1.getText().split("\n"))) ;
		List<String> selectedColumns2 = new ArrayList<>(Arrays.asList(textArea2.getText().split("\n")));

		selectedColumns1.remove(0);
		selectedColumns2.remove(0);
		
		selectedColumns1.replaceAll(s -> parentCell1.getSourceTableName(s) + "." + s);
		selectedColumns2.replaceAll(s -> parentCell2.getSourceTableName(s) + "." + s);
		
		Operator operator = new UnionOperator(table1, table2, selectedColumns1, selectedColumns2);

		selectedColumns1.replaceAll(s -> s.substring(s.indexOf(".")+1));
		selectedColumns2.replaceAll(s -> s.substring(s.indexOf(".")+1));

		((OperatorCell)cell).setColumns(List.of(parentCell1.getColumns(), parentCell2.getColumns()), Stream.of(selectedColumns1).collect(Collectors.toList()));

		((OperatorCell) cell).setOperator(operator);
		cell.setName("U   " + selectedColumns1.toString() + " U " + selectedColumns2.toString());    
		
        graph.getModel().setValue(jCell,"U   " + selectedColumns1.toString() + " U " + selectedColumns2.toString());
		
        dispose();
		
	}
}
