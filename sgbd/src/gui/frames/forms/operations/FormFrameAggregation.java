package gui.frames.forms.operations;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

import entities.Cell;
import entities.OperatorCell;
import sgbd.query.Operator;

@SuppressWarnings("serial")
public class FormFrameAggregation extends JDialog implements ActionListener {

	private JPanel contentPane;
	private JComboBox comboBoxColunas;
	private JComboBox comboBoxOp;
	private JButton btnPronto;
	private Cell cell;
	private Cell parentCell;
	private Object jCell;
	private mxGraph graph;
	private List<String> columnsList;

	public FormFrameAggregation(Object cell, List<Cell> cells, mxGraph graph) {

		super((Window)null);
		setModal(true);
		setTitle("Agregação");

		this.cell = cells.stream().filter(x -> x.getCell().equals(((mxCell) cell))).findFirst().orElse(null);
		parentCell = this.cell.getParents().get(0);
		this.jCell = cell;
		this.graph = graph;
		initializeGUI();
	}

	/**
	 * Create the frame.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void initializeGUI() {

		setBounds(100, 100, 450, 196);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);

		columnsList = new ArrayList<String>();

		columnsList = parentCell.getColumnsName();

		comboBoxColunas = new JComboBox(columnsList.toArray(new String[0]));

		String operacoes[] = { "max", "min" };

		comboBoxOp = new JComboBox(operacoes);

		JLabel lblNewLabel = new JLabel("Colunas");

		JLabel lblNewLabel_1 = new JLabel("Operacao");

		btnPronto = new JButton("Pronto");
		btnPronto.addActionListener(this);

		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING).addGroup(gl_contentPane
				.createSequentialGroup().addGap(32)
				.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING).addComponent(lblNewLabel)
						.addComponent(comboBoxColunas, GroupLayout.PREFERRED_SIZE, 101, GroupLayout.PREFERRED_SIZE))
				.addGap(49)
				.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
								.addComponent(comboBoxOp, GroupLayout.PREFERRED_SIZE, 96, GroupLayout.PREFERRED_SIZE)
								.addGap(45).addComponent(btnPronto))
						.addComponent(lblNewLabel_1))
				.addGap(36)));
		gl_contentPane
				.setVerticalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup().addGap(46)
								.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
										.addComponent(lblNewLabel).addComponent(lblNewLabel_1))
								.addPreferredGap(ComponentPlacement.RELATED)
								.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
										.addComponent(comboBoxColunas, GroupLayout.PREFERRED_SIZE,
												GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(comboBoxOp, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
												GroupLayout.PREFERRED_SIZE)
										.addComponent(btnPronto))
								.addContainerGap(85, Short.MAX_VALUE)));
		contentPane.setLayout(gl_contentPane);
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == btnPronto) {
			executeOperation(comboBoxColunas.getSelectedItem().toString(), comboBoxOp.getSelectedItem().toString());

		}

	}

	public void executeOperation(String column, String op) {

		Operator operator = parentCell.getData();

		// if(op == "max") operator = new GroupOperator(new
		// MaxOperator(parentCell.getData(),parentCell.getSourceTableName(column),column),parentCell.getSourceTableName(column),column);
		// else if(op == "min") operator = new GroupOperator(new
		// MinOperator(parentCell.getData(),parentCell.getSourceTableName(column),column),parentCell.getSourceTableName(column),column);

		operator.open();

		((OperatorCell) cell).setColumns(List.of(parentCell.getColumns()), operator.getContentInfo().values());

		((OperatorCell) cell).setOperator(operator);

		operator.close();

		dispose();

	}
}
