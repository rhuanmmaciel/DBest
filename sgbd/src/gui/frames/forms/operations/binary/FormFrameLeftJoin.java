package gui.frames.forms.operations.binary;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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

import entities.cells.Cell;
import entities.cells.OperationCell;
import exceptions.TreeException;
import gui.frames.forms.operations.IOperator;
import gui.frames.forms.operations.Operation;
import sgbd.query.Operator;
import sgbd.query.binaryop.joins.LeftNestedLoopJoin;

@SuppressWarnings("serial")
public class FormFrameLeftJoin extends JDialog implements ActionListener, IOperator {

	private JPanel contentPane;
	private JComboBox<?> comboBoxColumns1;
	private JComboBox<?> comboBoxColumns2;
	private JButton btnPronto;
	private List<String> columnsList_1;
	private List<String> columnsList_2;

	private OperationCell cell;
	private Cell parentCell1;
	private Cell parentCell2;
	private mxCell jCell;

	private JButton btnCancel;

	public FormFrameLeftJoin() {

	}

	public FormFrameLeftJoin(mxCell jCell) {

		super((Window) null);
		setModal(true);
		setTitle("Junção à esquerda");

		this.cell = (OperationCell) Cell.getCells().get(jCell);
		this.parentCell1 = this.cell.getParents().get(0);
		this.parentCell2 = this.cell.getParents().get(1);
		this.jCell = jCell;

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

		comboBoxColumns1 = new JComboBox(columnsList_1.toArray(new String[0]));

		columnsList_2 = new ArrayList<String>();
		columnsList_2 = parentCell2.getColumnsName();

		comboBoxColumns2 = new JComboBox(columnsList_2.toArray(new String[0]));

		JLabel lblNewLabel = new JLabel("Tabela I");

		JLabel lblNewLabel_1 = new JLabel("Tabela II");

		JLabel lblNewLabel_2 = new JLabel("=");

		btnPronto = new JButton("Pronto");
		btnPronto.addActionListener(this);

		btnCancel = new JButton("Cancelar");
		btnCancel.addActionListener(this);

		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING).addGroup(gl_contentPane
				.createSequentialGroup().addGap(40)
				.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING).addComponent(lblNewLabel)
						.addGroup(gl_contentPane.createSequentialGroup()
								.addComponent(comboBoxColumns1, GroupLayout.PREFERRED_SIZE, 146,
										GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(lblNewLabel_2, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE)
								.addGap(6)
								.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
										.addGroup(gl_contentPane.createSequentialGroup().addComponent(lblNewLabel_1)
												.addContainerGap(140, Short.MAX_VALUE))
										.addGroup(gl_contentPane.createSequentialGroup()
												.addComponent(comboBoxColumns2, 0, 152, Short.MAX_VALUE).addGap(66))))))
				.addGroup(gl_contentPane.createSequentialGroup().addContainerGap(217, Short.MAX_VALUE)
						.addComponent(btnCancel).addPreferredGap(ComponentPlacement.UNRELATED).addComponent(btnPronto)
						.addContainerGap()));
		gl_contentPane
				.setVerticalGroup(gl_contentPane
						.createParallelGroup(
								Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup().addContainerGap()
								.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
										.addComponent(lblNewLabel).addComponent(lblNewLabel_1))
								.addGap(5)
								.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
										.addComponent(comboBoxColumns1, GroupLayout.PREFERRED_SIZE,
												GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(comboBoxColumns2, GroupLayout.PREFERRED_SIZE,
												GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
								.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE).addComponent(btnPronto)
										.addComponent(btnCancel)))
						.addGroup(gl_contentPane.createSequentialGroup().addGap(30)
								.addComponent(lblNewLabel_2, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE)
								.addContainerGap(52, Short.MAX_VALUE)));
		contentPane.setLayout(gl_contentPane);

		addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {

				dispose();

			}

		});

		this.setVisible(true);

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == btnPronto) {
			executeOperation(jCell, List.of(comboBoxColumns1.getSelectedItem().toString(),
					comboBoxColumns2.getSelectedItem().toString()));

		} else if (e.getSource() == btnCancel) {

			dispose();

		}

	}

	public void executeOperation(mxCell jCell, List<String> data) {

		OperationCell cell = (OperationCell) Cell.getCells().get(jCell);

		try {

			if (data == null || data.size() != 2 || !cell.hasParents() || cell.getParents().size() != 2
					|| cell.hasParentErrors() || (!cell.getParents().get(0).getColumnsName().contains(data.get(0))
							&& !cell.getParents().get(1).getColumnsName().contains(data.get(1)))) {

				throw new TreeException();

			}

			Cell parentCell1 = cell.getParents().get(0);
			Cell parentCell2 = cell.getParents().get(1);

			Operator table_1 = parentCell1.getOperator();
			Operator table_2 = parentCell2.getOperator();
			String item1 = data.get(0);
			String item2 = data.get(1);

			Operator operator = new LeftNestedLoopJoin(table_1, table_2, (t1, t2) -> {

				return t1.getContent(parentCell1.getSourceTableName(item1)).getInt(item1) == t2
						.getContent(parentCell2.getSourceTableName(item2)).getInt(item2);

			});

			Operation.operationSetter(cell, "⟕   " + item1 + " = " + item2, data, operator);
			
		} catch (TreeException e) {

			cell.setError();

		}

		dispose();

	}

}
