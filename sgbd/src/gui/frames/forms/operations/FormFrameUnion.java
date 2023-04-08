package gui.frames.forms.operations;

import java.awt.Color;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
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
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

import entities.Cell;
import entities.OperationCell;
import sgbd.query.Operator;
import sgbd.query.binaryop.UnionOperator;

@SuppressWarnings("serial")
public class FormFrameUnion extends JDialog implements ActionListener, DocumentListener{

	private JPanel contentPane;
	private JComboBox<String> columnsComboBox1;
	private JComboBox<String> columnsComboBox2; 
	private List<String> columnsList1;
	private List<String> columnsList2;
	
	private JButton btnAdd1;
	private JButton btnAdd2;
	private JButton btnRemove1;
	private JButton btnRemove2;

	private JButton btnCancel;
	private JButton btnReady;
	private String textColumnsPicked1;
	private String textColumnsPicked2;
	private JTextArea textArea1;
	private JTextArea textArea2;
	
	private OperationCell cell;
	private Cell parentCell1;
	private Cell parentCell2;
	private mxCell jCell;
	private mxGraph graph;
	
	private AtomicReference<Boolean> exitRef;

	public FormFrameUnion(mxCell jCell, Map<mxCell, Cell> cells, mxGraph graph, AtomicReference<Boolean> exitRef) {
		
		super((Window)null);
		setModal(true);
		setTitle("União");
		
		this.cell = (OperationCell) cells.get(jCell);
		this.parentCell1 = this.cell.getParents().get(0);
		this.parentCell2 = this.cell.getParents().get(1);
		this.jCell = jCell;
		this.graph = graph;
		this.exitRef = exitRef;
		
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
		columnsComboBox1.addActionListener(this);
		columnsComboBox2.addActionListener(this);
		
		JLabel lblColumns1 = new JLabel("Colunas");
		JLabel lblColumns2 = new JLabel("Colunas");
		lblColumns2.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JLabel lblColumnsPicked1 = new JLabel("Tabela I");
		JLabel lblColumnsPicked2 = new JLabel("Tabela II");
		lblColumnsPicked2.setHorizontalAlignment(SwingConstants.RIGHT);
		
		textArea1 = new JTextArea();
		textArea1.getDocument().addDocumentListener(this);
		textArea2 = new JTextArea();
		textArea2.getDocument().addDocumentListener(this);
		
		textArea1.setEditable(false);
		textArea2.setEditable(false);
		
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
		
		btnCancel = new JButton("Cancelar");
		btnCancel.addActionListener(this);
		
		GroupLayout groupLayout = new GroupLayout(contentPane);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap(39, Short.MAX_VALUE)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
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
							.addGap(18)
							.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
								.addComponent(columnsComboBox2, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnAdd2, GroupLayout.PREFERRED_SIZE, 66, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnRemove2, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblColumns2, GroupLayout.PREFERRED_SIZE, 99, GroupLayout.PREFERRED_SIZE))
							.addGap(58))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(btnCancel)
							.addPreferredGap(ComponentPlacement.RELATED)
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
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnReady)
						.addComponent(btnCancel))
					.addContainerGap())
		);
		contentPane.setLayout(groupLayout);
		verifyConditions();
		
		addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {

				exitRef.set(true);
				dispose();
				
			}

		});
		
		this.setVisible(true);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		verifyConditions();
		if(e.getSource() == btnAdd1){

			if(columnsComboBox1.getItemCount() > 0) {
				
				textColumnsPicked1 = textArea1.getText() + "\n" +columnsComboBox1.getSelectedItem().toString() ;
				columnsComboBox1.removeItemAt(columnsComboBox1.getSelectedIndex());
				textArea1.setText(textColumnsPicked1);
				
			}

		}else if(e.getSource() == btnRemove1) {
			
			textArea1.setText("");
			
			columnsComboBox1.removeAllItems();
			
			for(String item : columnsList1)
				columnsComboBox1.addItem(item);
			
		}else if(e.getSource() == btnAdd2){

			if(columnsComboBox2.getItemCount() > 0) {
				
				textColumnsPicked2 = textArea2.getText() + "\n" +columnsComboBox2.getSelectedItem().toString() ;
				columnsComboBox2.removeItemAt(columnsComboBox2.getSelectedIndex());
				textArea2.setText(textColumnsPicked2);
				
			}

		}else if(e.getSource() == btnRemove2) {
			
			textArea2.setText("");
			
			columnsComboBox2.removeAllItems();
			
			for(String item : columnsList2)
				columnsComboBox2.addItem(item);
		
		}else if(e.getSource() == btnReady) {
	        
			executeOperation();
			dispose();
	        
		}else if(e.getSource() == btnCancel) {
			
			exitRef.set(true);
			dispose();
			
		}
		
	}
	
	private void verifyConditions() {
		
		boolean noneSelection1 = textArea1.getText().isEmpty();
		boolean noneSelection2 = textArea2.getText().isEmpty();
		boolean differentAmountOfColumns = textArea2.getLineCount() != textArea1.getLineCount(); 
		
		btnReady.setEnabled(!noneSelection1 && !noneSelection2 && !differentAmountOfColumns);
		
		updateToolTipText(noneSelection1, noneSelection2 , differentAmountOfColumns);
		
	}
	
	private void updateToolTipText(boolean noneSelection1, boolean noneSelection2 , boolean differentAmountOfColumns) {
		
		String btnReadyToolTipText = new String();
		
		 if(noneSelection1) {
				
			btnReadyToolTipText = "- Selecione pelo menos uma coluna da primeira tabela";
				
		}else if(noneSelection2) {
		
			btnReadyToolTipText = "- Selecione pelo menos uma coluna da segunda tabela";
		
		}else if(differentAmountOfColumns) {
			
			btnReadyToolTipText = "- É necessário que seja selecionado a mesma quantidade de colunas de cada tabela";
			
		}
		UIManager.put("ToolTip.foreground", Color.RED);
		
		btnReady.setToolTipText(btnReadyToolTipText.isEmpty() ? null : btnReadyToolTipText);
		
	}
	
	private void executeOperation() {
		
		Operator table1 = parentCell1.getOperator();
		Operator table2 = parentCell2.getOperator();
		
		List<String> selectedColumns1 = new ArrayList<>(Arrays.asList(textArea1.getText().split("\n"))) ;
		List<String> selectedColumns2 = new ArrayList<>(Arrays.asList(textArea2.getText().split("\n")));

		selectedColumns1.remove(0);
		selectedColumns2.remove(0);
		
		selectedColumns1.replaceAll(s -> parentCell1.getSourceTableName(s) + "." + s);
		selectedColumns2.replaceAll(s -> parentCell2.getSourceTableName(s) + "." + s);
		
		Operator operator = new UnionOperator(table1, table2, selectedColumns1, selectedColumns2);

		((OperationCell) cell).setOperator(operator);

		selectedColumns1.replaceAll(s -> s.substring(s.indexOf(".")+1));
		selectedColumns2.replaceAll(s -> s.substring(s.indexOf(".")+1));
		
		((OperationCell)cell).setColumns(List.of(parentCell1.getColumns(), parentCell2.getColumns()), operator.getContentInfo().values());
		
		cell.setName("U   " + selectedColumns1.toString() + " U " + selectedColumns2.toString());    
		
        graph.getModel().setValue(jCell,"U   " + selectedColumns1.toString() + " U " + selectedColumns2.toString());
		
        dispose();
		
	}

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
}
