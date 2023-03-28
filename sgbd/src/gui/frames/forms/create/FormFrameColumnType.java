package gui.frames.forms.create;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import entities.Column;
import enums.ColumnDataType;

@SuppressWarnings("serial")
public class FormFrameColumnType extends JDialog implements ActionListener, DocumentListener{
	
	private JPanel contentPane;
	private JButton btnContinue;
	private JTextField txtFieldTableName;
	private List<Column> columns;
	private List<String> columnsName;	
	private AtomicReference<Boolean> exitReference;
	private StringBuilder tableName;
	private List<String> tablesName;
	private Map<Integer, Map<String, String>> data;
	private List<ColumnData> columnsData;
	
	public FormFrameColumnType(List<Column> columns, Map<Integer, Map<String, String>> data, StringBuilder tableName,
							   List<String> tablesName, AtomicReference<Boolean> exitReference) {
		
		super((Window)null);
		setModal(true);
		
		this.columns = columns; 
		this.columnsName = new ArrayList<>(data.get(0).keySet());
		this.tableName = tableName;	
		this.tablesName = tablesName;
		this.data = data;
		
		this.columnsData = new ArrayList<>();
		
		this.exitReference = exitReference;
		
		initializeGUI();
		
	}
	
	private void initializeGUI() {
	    setBounds(100, 100, 400, 600);
	    setLocationRelativeTo(null);

	    contentPane = new JPanel();
	    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

	    contentPane.setLayout(new BorderLayout());
	    getContentPane().add(contentPane);

	    JPanel panel = new JPanel(new GridBagLayout());

	    createItems(panel);
	    
	    checkTypesPossibility();
	    
	    disableComboBoxOptions();

	    getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

	    btnContinue = new JButton("Continuar");
	    btnContinue.addActionListener(this);
	    btnContinue.setPreferredSize(new Dimension(110, 30)); 

	    JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); 
	    btnPanel.add(btnContinue); 
	    contentPane.add(btnPanel, BorderLayout.SOUTH);

	    JScrollPane scrollPane = new JScrollPane(panel);

	    JLabel lblTableName = new JLabel("Nome da tabela: ");
	    txtFieldTableName = new JTextField(tableName.toString()); 
	    txtFieldTableName.getDocument().addDocumentListener(this);
	    
	    Box hBoxTableName = Box.createHorizontalBox();
	    hBoxTableName.add(lblTableName);
	    hBoxTableName.add(Box.createHorizontalStrut(3));
	    hBoxTableName.add(txtFieldTableName);
	    hBoxTableName.add(Box.createHorizontalStrut(100));
	    
	    Box topPane = Box.createVerticalBox();

	    topPane.add(hBoxTableName);
	    topPane.add(Box.createVerticalStrut(3));
	    
	    JLabel lbl = new JLabel("Selecione os tipos de cada coluna: ");
	    lbl.setAlignmentX(Component.RIGHT_ALIGNMENT);
	    topPane.add(lbl);
	    
	    contentPane.add(topPane, BorderLayout.NORTH);
	    
	    GroupLayout gl_contentPane = new GroupLayout(contentPane);
	    gl_contentPane.setHorizontalGroup(
	            gl_contentPane.createParallelGroup(Alignment.TRAILING)
	                    .addGroup(gl_contentPane.createSequentialGroup()
	                            .addGap(27)
	                            .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
	                                    .addComponent(topPane)
	                                    .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE))
	                            .addContainerGap(63, Short.MAX_VALUE))
	    );
	    gl_contentPane.setVerticalGroup(
	            gl_contentPane.createParallelGroup(Alignment.LEADING)
	                    .addGroup(gl_contentPane.createSequentialGroup()
	                            .addComponent(topPane)
	                            .addGap(19)
	                            .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE)
	                            .addPreferredGap(ComponentPlacement.RELATED, 82, Short.MAX_VALUE)
	                            .addContainerGap())
	                    .addGroup(gl_contentPane.createSequentialGroup()
	                            .addContainerGap()
	                            .addComponent(btnPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                            .addContainerGap())
	    );

	    addWindowListener(new WindowAdapter() {

	        public void windowClosing(WindowEvent e) {

	            exitReference.set(true);

	        }

	    });

	    contentPane.add(scrollPane, BorderLayout.CENTER);
	    
	    verifyContinue();
	    
	    this.setVisible(true);
	}
	
	private void updateToolTipText(Boolean repeatedName, Boolean blankName) {
		
		String btnContinueToolTipText = new String();
		
		 if(blankName) {
				
			btnContinueToolTipText = "- Não é permitido uma tabela sem nome";
				
		}else if(repeatedName) {
		
			btnContinueToolTipText = "- Não pode existir duas tabelas com o mesmo nome";
		
		}		
		UIManager.put("ToolTip.foreground", Color.RED);
		
		btnContinue.setToolTipText(btnContinueToolTipText.isEmpty() ? null : btnContinueToolTipText);
		
	}

	private void createItems(JPanel panel) {
		
	    GridBagConstraints gbc = new GridBagConstraints();
	    gbc.insets = new Insets(5, 5, 5, 5);
	    
	    int i = 2;
	    for (String element : columnsName) {
	    	
	        JLabel label = new JLabel(element + ":");
	        JComboBox<String> comboBox = new JComboBox<>(new String[]{"None", "Integer", "Float", "Character", "String", "Boolean"});
	        comboBox.addActionListener(this);
	        
	        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
	        JSeparator separator2 = new JSeparator(SwingConstants.HORIZONTAL);
	        
	        separator.setPreferredSize(new Dimension(150, 10)); 
	        separator2.setPreferredSize(new Dimension(150, 10)); 
	        
	        label.setPreferredSize(new Dimension(120, 30)); 
	        comboBox.setPreferredSize(new Dimension(120, 30)); 
	        
	        gbc.gridx = 0;
	        gbc.gridy = i;
	        panel.add(label, gbc);

	        gbc.gridx = 1;
	        gbc.gridy = i;
	        panel.add(comboBox, gbc);

	        gbc.gridx = 0;
	        gbc.gridy = i+1;
	        gbc.gridwidth = 1;
	        panel.add(separator, gbc);
	        
	        gbc.gridx = 1;
	        gbc.gridy = i+1;
	        gbc.gridwidth = 1;
	        panel.add(separator2, gbc);
	        
	        i+= 2;
	        
	        List<String> dataColumn = new ArrayList<>();
	        
	        for(Map<String, String> line : data.values()) {
	        	dataColumn.add(line.get(element));
	        }
	        
	        columnsData.add(new ColumnData(dataColumn, comboBox, label.getText()));
	        
	    }		
		
	}
	
	private void checkTypesPossibility() {
		
		for(ColumnData column : columnsData) {
			
			for(String inf : column.getData()) {
				
				if(inf.length() > 1) {
					
					column.removeType(ColumnDataType.CHARACTER);
					
				}
				
				try{
					
					Integer.parseInt(inf);
					
				}catch(NumberFormatException e) {
					
					column.removeType(ColumnDataType.INTEGER);
					
				}
				
				try{
					
					Float.parseFloat(inf);
					
				}catch(NumberFormatException e) {
					
					column.removeType(ColumnDataType.FLOAT);
					
				}
				
			}
			
		}
		
	}
	
	private void disableComboBoxOptions(){
		
		List<String> order = new ArrayList<>(List.of("None", "Boolean", "String", "Character", "Float", "Integer"));
		
		for(ColumnData column : columnsData) {
			
			JComboBox<String> comboBox = column.getComboBox();
			comboBox.removeActionListener(this);
			
			if(!column.getPossiblesTypes().contains(ColumnDataType.INTEGER)) comboBox.removeItem("Integer");
			if(!column.getPossiblesTypes().contains(ColumnDataType.FLOAT)) comboBox.removeItem("Float");
			if(!column.getPossiblesTypes().contains(ColumnDataType.BOOLEAN)) comboBox.removeItem("Boolean");
			if(!column.getPossiblesTypes().contains(ColumnDataType.CHARACTER)) comboBox.removeItem("Character");
			
			for(String type : order) {
				
				for(int i = 0; i < comboBox.getItemCount(); i++) {
					
					if(comboBox.getItemAt(i).equals(type)) comboBox.setSelectedIndex(i);
					
				}
				
			}
			
			comboBox.addActionListener(this);
			
		}
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		verifyContinue();
		
		if(e.getSource() == btnContinue) {
			
			for(ColumnData column : columnsData) {
				
				if(column.getComboBox().getSelectedItem().toString() == "None") {
					
					columns.add(new Column(column.getName(), ColumnDataType.NONE));
					
				}else if(column.getComboBox().getSelectedItem().toString() == "Integer") {
				
					columns.add(new Column(column.getName(), ColumnDataType.INTEGER));
				
				}else if(column.getComboBox().getSelectedItem().toString() == "Float") {
				
					columns.add(new Column(column.getName(), ColumnDataType.FLOAT));
				
				}else if(column.getComboBox().getSelectedItem().toString() == "String") {
				
					columns.add(new Column(column.getName(), ColumnDataType.STRING));
				
				}else if(column.getComboBox().getSelectedItem().toString() == "Character") {
				
					columns.add(new Column(column.getName(), ColumnDataType.CHARACTER));
				
				}else {
						
					columns.add(new Column(column.getName(), ColumnDataType.BOOLEAN));
					
				}
			}
			
			tableName.replace(0, tableName.length(), txtFieldTableName.getText().replaceAll(" ", ""));
			
			dispose();
			
		}
		
	}
	
	private void verifyContinue() {
		
		String txtFieldName = txtFieldTableName.getText().replaceAll(" ", "");
		
		Boolean blankName = txtFieldName.isEmpty();
		Boolean repeatedName = tablesName.contains(txtFieldName.toUpperCase());
		
		btnContinue.setEnabled(!blankName && !repeatedName);
		
		updateToolTipText(repeatedName, blankName);
		
	}

	@Override
	public void insertUpdate(DocumentEvent e) {

		verifyContinue();
		
	}

	@Override
	public void removeUpdate(DocumentEvent e) {

		verifyContinue();
		
	}

	@Override
	public void changedUpdate(DocumentEvent e) {

		verifyContinue();
		
	}
	
	private class ColumnData{
		
		private List<String> data;
		private JComboBox<String> comboBox;
		private String name;
		private List<ColumnDataType> possiblesTypes;
		
		public ColumnData(List<String> data, JComboBox<String> comboBox, String name) {
			
			this.data = data;
			this.comboBox = comboBox;
			this.name = name.replace(":", "");
			possiblesTypes = new ArrayList<>(Set.of(ColumnDataType.values()));
			
		}

		public List<String> getData() {
			return data;
		}

		public JComboBox<String> getComboBox() {
			return comboBox;
		}

		public String getName() {
			return name;
		}
		
		public void removeType(ColumnDataType type) {
			
			possiblesTypes.remove(type);
			
		}
		
		public List<ColumnDataType> getPossiblesTypes(){
			
			return possiblesTypes;
			
		}
		
	}
	
}
