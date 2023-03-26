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
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

import entities.Cell;
import entities.OperatorCell;
import sgbd.query.Operator;
import sgbd.query.unaryop.FilterColumnsOperator;
import sgbd.table.Table;

@SuppressWarnings("serial")
public class FormFrameProjection extends JDialog implements ActionListener {

	private JPanel contentPane;
	private JComboBox<?> columnsComboBox;
	private List<String> columnsList;

	private JButton btnAdd;
	private JButton btnRemove;
	private JButton btnReady;
	private String textColumnsPicked;
	private JTextArea textArea;

	private List<String> columnsResult;
	private Cell cell;
	private Cell parentCell;
	private Object jCell;
	private mxGraph graph;

	public FormFrameProjection(Object cell, List<Cell> cells, mxGraph graph) {

		super((Window)null);
		setModal(true);
		setTitle("Projeção");
		
		this.cell = cells.stream().filter(x -> x.getCell().equals(((mxCell) cell))).findFirst().orElse(null);
		parentCell = this.cell.getParents().get(0);
		this.jCell = cell;
		this.graph = graph;
		initializeGUI();

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initializeGUI() {

		setBounds(100, 100, 500, 301);
		setLocationRelativeTo(null);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);

		columnsList = new ArrayList<String>();

		columnsList = parentCell.getColumnsName();

		columnsComboBox = new JComboBox(columnsList.toArray(new String[0]));

		JLabel lblColumns = new JLabel("Columns");

		JLabel lblTermos = new JLabel("Termos");

		textArea = new JTextArea();

		columnsResult = new ArrayList<String>();

		btnAdd = new JButton("Add");
		btnAdd.addActionListener(this);

		btnRemove = new JButton("Remover");
		btnRemove.addActionListener(this);

		btnReady = new JButton("Pronto");
		btnReady.addActionListener(this);

		GroupLayout groupLayout = new GroupLayout(contentPane);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(38)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(columnsComboBox, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblColumns, GroupLayout.PREFERRED_SIZE, 99, GroupLayout.PREFERRED_SIZE)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(btnAdd, GroupLayout.PREFERRED_SIZE, 66, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(btnRemove)))
							.addPreferredGap(ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
								.addComponent(textArea, GroupLayout.PREFERRED_SIZE, 223, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblTermos)))
						.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
							.addContainerGap(396, Short.MAX_VALUE)
							.addComponent(btnReady)))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblColumns, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblTermos))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(columnsComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(17)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(btnAdd)
								.addComponent(btnRemove)))
						.addComponent(textArea, GroupLayout.PREFERRED_SIZE, 175, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
					.addComponent(btnReady)
					.addContainerGap())
		);
		contentPane.setLayout(groupLayout);
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == btnAdd) {

			if (columnsComboBox.getItemCount() > 0) {
				textColumnsPicked = textArea.getText() + "\n" + columnsComboBox.getSelectedItem().toString();
				columnsComboBox.removeItemAt(columnsComboBox.getSelectedIndex());
				textArea.setText(textColumnsPicked);
			}

		} else if (e.getSource() == btnRemove) {
			
			textArea.setText(textArea.getText().replace(textArea.getSelectedText(),""));

		} else if (e.getSource() == btnReady) {

			columnsResult = new ArrayList<>(List.of(textArea.getText().split("\n")));
			columnsResult.remove(0);
			graph.getModel().setValue(jCell, "π  " + columnsResult.toString());
			executeOperation();

		}
	}

	public void executeOperation() {

		List<String> aux = parentCell.getColumnsName();
		aux.removeAll(columnsResult);

		Operator operator = parentCell.getData();

		for (Table table : parentCell.getData().getSources()) {

			operator = new FilterColumnsOperator(operator, table.getTableName(), aux);

		}

		((OperatorCell) cell).setColumns(List.of(parentCell.getColumns()), operator.getContentInfo().values());

		((OperatorCell) cell).setOperator(operator);

		cell.setName("π  " + columnsResult.toString());

		dispose();

	}

}
