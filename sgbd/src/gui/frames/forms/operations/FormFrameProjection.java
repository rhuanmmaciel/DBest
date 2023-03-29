package gui.frames.forms.operations;

import java.awt.Color;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

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
	private JComboBox<String> columnsComboBox;
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

	private AtomicReference<Boolean> exitReference;
	private JButton btnCancel;

	public FormFrameProjection(Object jCell, Map<mxCell, Cell> cells, mxGraph graph,
			AtomicReference<Boolean> exitReference) {

		super((Window) null);
		setModal(true);
		setTitle("Projeção");

		this.cell = cells.get(jCell);
		parentCell = this.cell.getParents().get(0);
		this.jCell = jCell;
		this.graph = graph;
		this.exitReference = exitReference;

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
		columnsComboBox.addActionListener(this);

		JLabel lblColumns = new JLabel("Columns");

		JLabel lblTermos = new JLabel("Termos");

		textArea = new JTextArea();
		textArea.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {

				verifyConditions();
				
			}

			@Override
			public void removeUpdate(DocumentEvent e) {

				verifyConditions();
				
			}

			@Override
			public void changedUpdate(DocumentEvent e) {

				verifyConditions();
				
			}
			
		});

		columnsResult = new ArrayList<String>();

		btnAdd = new JButton("Add");
		btnAdd.addActionListener(this);

		btnRemove = new JButton("Remover");
		btnRemove.addActionListener(this);

		btnReady = new JButton("Pronto");
		btnReady.addActionListener(this);

		btnCancel = new JButton("Cancelar");
		btnCancel.addActionListener(this);

		GroupLayout groupLayout = new GroupLayout(contentPane);
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout
				.createSequentialGroup()
				.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING).addGroup(groupLayout
						.createSequentialGroup().addGap(38)
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(columnsComboBox, GroupLayout.PREFERRED_SIZE, 200,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(lblColumns, GroupLayout.PREFERRED_SIZE, 99, GroupLayout.PREFERRED_SIZE)
								.addGroup(groupLayout.createSequentialGroup()
										.addComponent(btnAdd, GroupLayout.PREFERRED_SIZE, 66,
												GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(ComponentPlacement.UNRELATED).addComponent(btnRemove)))
						.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(
								groupLayout.createParallelGroup(Alignment.LEADING, false)
										.addComponent(textArea, GroupLayout.PREFERRED_SIZE, 223,
												GroupLayout.PREFERRED_SIZE)
										.addComponent(lblTermos)))
						.addGroup(groupLayout.createSequentialGroup().addContainerGap(268, Short.MAX_VALUE)
								.addComponent(btnCancel).addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(btnReady)))
				.addContainerGap()));
		groupLayout
				.setVerticalGroup(
						groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(
										groupLayout.createSequentialGroup().addContainerGap()
												.addGroup(groupLayout
														.createParallelGroup(Alignment.BASELINE)
														.addComponent(
																lblColumns, GroupLayout.PREFERRED_SIZE, 14,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(lblTermos))
												.addPreferredGap(ComponentPlacement.RELATED)
												.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
														.addGroup(groupLayout.createSequentialGroup().addComponent(
																columnsComboBox, GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
																.addGap(17)
																.addGroup(groupLayout
																		.createParallelGroup(Alignment.BASELINE)
																		.addComponent(btnAdd).addComponent(btnRemove)))
														.addComponent(textArea, GroupLayout.PREFERRED_SIZE, 175,
																GroupLayout.PREFERRED_SIZE))
												.addPreferredGap(ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
												.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
														.addComponent(btnReady).addComponent(btnCancel))
												.addContainerGap()));
		contentPane.setLayout(groupLayout);
		verifyConditions();
		this.setVisible(true);

		addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {

				exitReference.set(true);
				dispose();

			}

		});

	}

	private void verifyConditions() {

		boolean noneSelection = textArea.getText().isEmpty();

		btnReady.setEnabled(!noneSelection);

		updateToolTipText(noneSelection);

	}

	private void updateToolTipText(boolean noneSelection) {

		String btnReadyToolTipText = new String();

		if (noneSelection) {

			btnReadyToolTipText = "- Selecione pelo menos uma coluna";

		} 
		
		UIManager.put("ToolTip.foreground", Color.RED);

		btnReady.setToolTipText(btnReadyToolTipText.isEmpty() ? null : btnReadyToolTipText);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		verifyConditions();
		
		if (e.getSource() == btnAdd) {

			if (columnsComboBox.getItemCount() > 0) {
				textColumnsPicked = textArea.getText() + "\n" + columnsComboBox.getSelectedItem().toString();
				columnsComboBox.removeItemAt(columnsComboBox.getSelectedIndex());
				textArea.setText(textColumnsPicked);
			}

		} else if (e.getSource() == btnRemove) {

			textArea.setText("");

			columnsComboBox.removeAllItems();

			for (String item : columnsList)
				columnsComboBox.addItem(item);

		} else if (e.getSource() == btnReady) {

			columnsResult = new ArrayList<>(List.of(textArea.getText().split("\n")));
			columnsResult.remove(0);
			graph.getModel().setValue(jCell, "π  " + columnsResult.toString());
			executeOperation();

		} else if (e.getSource() == btnCancel) {

			exitReference.set(true);
			dispose();

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
