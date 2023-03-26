package gui.frames.forms.operations;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

import entities.Cell;
import entities.OperatorCell;
import sgbd.prototype.Column;
import sgbd.query.Operator;
import sgbd.query.Tuple;
import sgbd.query.unaryop.AsOperator;
import sgbd.util.Conversor;

public class FormFrameRename extends JDialog implements ActionListener {

	private JPanel contentPane;
	private JTextField textField;
	private JComboBox comboBox;
	private JButton btnPronto;
	private List<String> columnsList;

	
	private Cell cell;
	private Cell parentCell;
	private Object jCell;
	private mxGraph graph;

	public FormFrameRename(Object cell, List<Cell> cells, mxGraph graph) {
		
		super((Window)null);
		setModal(true);
		setTitle("Renomeação");
		
		this.cell = cells.stream().filter(x -> x.getCell().equals(((mxCell)cell))).findFirst().orElse(null);
		parentCell = this.cell.getParents().get(0);
		this.jCell = cell;
		this.graph = graph;
		initializeGUI();

	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initializeGUI() {
		
		setBounds(100, 100, 450, 172);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		
		columnsList = new ArrayList<String>();
		
		columnsList = parentCell.getColumnsName();
		
		comboBox = new JComboBox(columnsList.toArray(new String[0]));
		
		textField = new JTextField();
		textField.setColumns(10);
		
		
		JLabel lblNewLabel = new JLabel("Coluna");
		
		JLabel lblNewLabel_1 = new JLabel("as");
		
		btnPronto = new JButton("Pronto");
		btnPronto.addActionListener(this);

		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(19)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(lblNewLabel)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 118, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addComponent(lblNewLabel_1)
							.addGap(30)
							.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(53)
							.addComponent(btnPronto)))
					.addGap(24))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(30)
					.addComponent(lblNewLabel)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel_1)
						.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnPronto))
					.addContainerGap(79, Short.MAX_VALUE))
		);
		contentPane.setLayout(gl_contentPane);
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == btnPronto) {
			executeOperation(comboBox.getSelectedItem().toString(),textField.getText());
		}
	}
	
	public void executeOperation(String column,String formatedString) {
		
		//List<String> aux = parentCell.getColumnsName();
		//aux.removeAll(columnsResult);
		
		Operator operator = parentCell.getData();
		
		operator = new AsOperator(operator,(Conversor) new Conversor() {
          @Override
          public Column metaInfo(Tuple t) {
              return t.getContent(parentCell.getSourceTableName(column)).getMeta(column);
          }

          @Override
          public byte[] process(Tuple t) {
        	  //tenho que ver se quer q coloque o nome da tabela igual tava salvando anteriormente
              String formated = t.getContent(parentCell.getSourceTableName(column)).getString(column)+ formatedString;
              return formated.getBytes(StandardCharsets.UTF_8);
          }

      }, formatedString);
		System.out.println("old " +column+" new : "+formatedString);
	    operator.open();
	    
	    ((OperatorCell)cell).setColumns(List.of(parentCell.getColumns()), operator.getContentInfo().values());
        ((OperatorCell) cell).setOperator(operator);
		cell.setName("ρ  "+ formatedString);
	    
        operator.close();
		
        dispose();
		
	}
}
