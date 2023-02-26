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
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

import com.mxgraph.model.mxCell;

import entities.Cell;
import entities.OperatorCell;
import entities.util.TableFormat;
import sgbd.query.Operator;
import sgbd.query.Tuple;
import sgbd.query.unaryop.FilterOperator;


@SuppressWarnings("serial")
public class FormFrameSelecao extends JFrame implements ActionListener {

	private JPanel contentPane;
	private JTextField textField;
	private JComboBox<List<String>> comboBoxColumns;
	private JComboBox<List<String>> comboBoxOp;
	
	private List<String> columnsList;
	
	private JButton btnReady;
	private Cell cell;
	private Cell parentCell;
	
	public static void main(Object cell, List<Cell> cells) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FormFrameSelecao frame = new FormFrameSelecao(cell, cells);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public FormFrameSelecao(Object cell, List<Cell> cells) {
		
		this.setVisible(true);
		
		this.cell = cells.stream().filter(x -> x.getCell().equals(((mxCell)cell))).findFirst().orElse(null);
		parentCell = this.cell.getParents().get(0);
		
		initializeGUI();
		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void initializeGUI() {
		
		setBounds(100, 100, 502, 262);
		setLocationRelativeTo(null);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	
		setContentPane(contentPane);
		
		columnsList = new ArrayList<String>();
		columnsList = parentCell.getColumnsName();
		
		comboBoxColumns = new JComboBox(columnsList.toArray(new String[0]));
		
		String operacoes[] = {">","<","=","!=",">=", "<="};
		
		comboBoxOp = new JComboBox(operacoes);
		
		textField = new JTextField();
		textField.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Colunas");
		
		JLabel lblNewLabel_1 = new JLabel("Operacao");
		
		JLabel lblNewLabel_2 = new JLabel("Valor");
		
		btnReady = new JButton("Pronto");
		btnReady.addActionListener(this);
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(19)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(comboBoxColumns, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel))
					.addGap(26)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(comboBoxOp, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel_1))
					.addGap(27)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addComponent(btnReady))
						.addComponent(lblNewLabel_2))
					.addContainerGap(51, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(47)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel)
						.addComponent(lblNewLabel_1)
						.addComponent(lblNewLabel_2))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(comboBoxColumns, GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
						.addComponent(comboBoxOp, GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE)
						.addComponent(textField, GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
						.addComponent(btnReady))
					.addGap(261))
		);
		contentPane.setLayout(gl_contentPane);
				
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == btnReady) {
			
	        executeOperation(comboBoxColumns.getSelectedItem().toString(), comboBoxOp.getSelectedItem().toString(), textField.getText());
			
		}
		
	}
	
	public void executeOperation(String columnName, String op, String value) {
		
		Operator operator = new FilterOperator(parentCell.getData(),(Tuple t)->{

			if(op.equals(">")) return t.getContent(parentCell.getSourceTableName(columnName)).getInt(columnName) > Integer.parseInt(value);
			if(op.equals(">=")) return t.getContent(parentCell.getSourceTableName(columnName)).getInt(columnName) >= Integer.parseInt(value);
			if(op.equals("<")) return t.getContent(parentCell.getSourceTableName(columnName)).getInt(columnName) < Integer.parseInt(value);
			if(op.equals("<=")) return t.getContent(parentCell.getSourceTableName(columnName)).getInt(columnName) <= Integer.parseInt(value);
			if(op.equals("=")) return t.getContent(parentCell.getSourceTableName(columnName)).getInt(columnName) == Integer.parseInt(value);
			return t.getContent(parentCell.getSourceTableName(columnName)).getInt(columnName) != Integer.parseInt(value);
			
		});

	    operator.open();
	    
	    ((OperatorCell) cell).setOperator(operator, TableFormat.getRows(operator, parentCell.getColumns()));
		((OperatorCell)cell).setColumns();
	    
        operator.close();
		
        dispose();
        
	}	
	
}
