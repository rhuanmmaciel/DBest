package gui.frames.forms.operations;

import java.awt.Color;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
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

import controller.ActionClass;
import entities.Cell;
import entities.OperationCell;
import sgbd.query.Operator;
import sgbd.query.unaryop.FilterColumnsOperator;
import sgbd.table.Table;

@SuppressWarnings("serial")
public class FormFrameProjection extends JDialog implements ActionListener, IOperator {

	private JPanel contentPane;
	private JComboBox<String> columnsComboBox;
	private List<String> columnsList;

	private JButton btnAdd;
	private JButton btnRemove;
	private JButton btnReady;
	private String textColumnsPicked;
	private JTextArea textArea;

	private List<String> columnsResult;
	private OperationCell cell;
	private Cell parentCell;
	private mxCell jCell;

	private AtomicReference<Boolean> exitReference;
	private JButton btnCancel;
	private JButton btnAddAll;

	public FormFrameProjection() {
		
	}
	
	public FormFrameProjection(mxCell jCell, AtomicReference<Boolean> exitReference) {

		super((Window) null);
		setModal(true);
		setTitle("Projeção");

		this.cell = (OperationCell) ActionClass.getCells().get(jCell);
		parentCell = this.cell.getParents().get(0);
		this.jCell = jCell;
		this.exitReference = exitReference;

		initializeGUI();

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initializeGUI() {

		setBounds(100, 100, 500, 470);
		setLocationRelativeTo(null);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);

		columnsList = new ArrayList<String>();

		columnsList = parentCell.getColumnsName();

		columnsComboBox = new JComboBox(columnsList.toArray(new String[0]));
		columnsComboBox.addActionListener(this);

		JLabel lblColumns = new JLabel("Colunas");

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

		btnAdd = new JButton("Adicionar");
		btnAdd.addActionListener(this);

		btnRemove = new JButton("Remover colunas");
		btnRemove.addActionListener(this);

		btnReady = new JButton("Pronto");
		btnReady.addActionListener(this);

		btnCancel = new JButton("Cancelar");
		btnCancel.addActionListener(this);

		btnAddAll = new JButton("Adicionar todas");
		btnAddAll.addActionListener(this);

		GroupLayout groupLayout = new GroupLayout(contentPane);
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.TRAILING).addGroup(groupLayout
				.createSequentialGroup()
				.addGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout
						.createSequentialGroup().addGap(23)
						.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
								.addGroup(groupLayout.createSequentialGroup().addComponent(btnCancel)
										.addPreferredGap(ComponentPlacement.RELATED).addComponent(btnReady))
								.addGroup(groupLayout.createSequentialGroup().addGroup(groupLayout
										.createParallelGroup(Alignment.TRAILING)
										.addComponent(columnsComboBox, Alignment.LEADING, 0, 444, Short.MAX_VALUE)
										.addGroup(groupLayout.createSequentialGroup()
												.addComponent(btnAdd, GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
												.addPreferredGap(ComponentPlacement.UNRELATED).addComponent(btnAddAll)
												.addPreferredGap(ComponentPlacement.UNRELATED).addComponent(btnRemove,
														GroupLayout.PREFERRED_SIZE, 143, GroupLayout.PREFERRED_SIZE))
										.addComponent(textArea, GroupLayout.DEFAULT_SIZE, 444, Short.MAX_VALUE))
										.addGap(11))))
						.addGroup(groupLayout.createSequentialGroup().addGap(205).addComponent(lblTermos)))
				.addContainerGap())
				.addGroup(groupLayout.createSequentialGroup().addContainerGap(211, Short.MAX_VALUE)
						.addComponent(lblColumns, GroupLayout.PREFERRED_SIZE, 99, GroupLayout.PREFERRED_SIZE)
						.addGap(180)));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
						.addComponent(lblColumns, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE).addGap(17)
						.addComponent(columnsComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addGap(18)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(btnAdd)
								.addComponent(btnRemove).addComponent(btnAddAll))
						.addGap(37).addComponent(lblTermos).addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(textArea, GroupLayout.PREFERRED_SIZE, 175, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED, 55, Short.MAX_VALUE).addGroup(groupLayout
								.createParallelGroup(Alignment.BASELINE).addComponent(btnReady).addComponent(btnCancel))
						.addContainerGap()));
		contentPane.setLayout(groupLayout);
		verifyConditions();

		addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {

				exitReference.set(true);
				dispose();

			}

		});

		this.setVisible(true);

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
			executeOperation(jCell, columnsResult);

		} else if (e.getSource() == btnCancel) {

			exitReference.set(true);
			dispose();

		} else if (e.getSource() == btnAddAll) {

			while (columnsComboBox.getItemCount() != 0) {

				textColumnsPicked = textArea.getText() + "\n" + columnsComboBox.getSelectedItem().toString();
				columnsComboBox.removeItemAt(columnsComboBox.getSelectedIndex());
				textArea.setText(textColumnsPicked);

			}

		}
	}

	public void executeOperation(mxCell jCell, List<String> data) {

		OperationCell cell = (OperationCell) ActionClass.getCells().get(jCell);

		try {
		
			if (data == null || !cell.hasParents() || cell.getParents().size() != 1 || cell.hasParentErrors()) {
	
				throw new Exception();
	
			}
	
			Cell parentCell = cell.getParents().get(0);
			
			List<String> aux = parentCell.getColumnsName();
			aux.removeAll(data);
	
			Operator operator = parentCell.getOperator();
	
			for (Table table : parentCell.getOperator().getSources()) {
	
				operator = new FilterColumnsOperator(operator, table.getTableName(), aux);
	
			}
	
			cell.setColumns(List.of(parentCell.getColumns()), operator.getContentInfo().values());
	
			cell.setOperator(operator);
	
			cell.setName("π  " + data.toString());
	
			cell.setData(data);
			
			ActionClass.getGraph().getModel().setValue(jCell, "π  " + data.toString());
			
			cell.removeError();

		}catch(Exception e) {
			
			cell.setError();
			
		}
		
		dispose();

	}

}
