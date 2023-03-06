package gui.frames.forms;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

import entities.Column;
import enums.ColumnDataType;

@SuppressWarnings("serial")
public class FormFrameColumnType extends JDialog implements ActionListener {
	
	private JPanel contentPane;
	private JButton btnContinue;
	private List<Column> columns;
	private List<JLabel> labels;
	private List<JComboBox<String>> comboBoxes;
	private List<String> columnsName;	
	
	public static void main(List<Column> columns, List<String> columnsName) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FormFrameColumnType frame = new FormFrameColumnType(columns, columnsName);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public FormFrameColumnType(List<Column> columns, List<String> columnsName) {
		
		super((Window)null);
		setModal(true);
		
		this.columns = columns; 
		this.columnsName = columnsName;
			
		initializeGUI();
		
	}
	
	private void initializeGUI() {
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		labels = new ArrayList<>();
		comboBoxes = new ArrayList<>();
		
		Dimension dim = new Dimension(100, 10);

		JPanel panel = new JPanel(new GridLayout(0, 2));
		for (String elemento : columnsName) {
		    
			JLabel label = new JLabel(elemento);
		    JComboBox<String> comboBox = new JComboBox<>(new String[]{"None", "Integer", "Float", "Character", "String", "Boolean"});
		    labels.add(label);
		    comboBoxes.add(comboBox);
		    
		    label.setMaximumSize(dim);
		    comboBox.setMaximumSize(dim);

		    panel.add(label);
		    panel.add(comboBox);
		}
		
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		btnContinue = new JButton("Continuar");
		btnContinue.addActionListener(this);
		
		setBounds(100, 100, 1400, 600);
		setLocationRelativeTo(null);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
	    
		JScrollPane scrollPane = new JScrollPane(panel);
		
		JLabel lblNewLabel = new JLabel("Selecione os tipos de dados de cada coluna: ");
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(27)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(lblNewLabel)
						.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 1300, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(63, Short.MAX_VALUE))
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap(1110, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnContinue)
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(lblNewLabel)
					.addGap(19)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 82, Short.MAX_VALUE)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnContinue))
					.addContainerGap())
		);
		
		contentPane.setLayout(gl_contentPane);
		this.setVisible(true);

		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == btnContinue) {
			
			int i = 0;
			for(JLabel column : labels) {
				
				if(comboBoxes.get(i).getSelectedItem().toString() == "None") {
					
					columns.add(new Column(column.getText(), ColumnDataType.NONE));
					
				}else if(comboBoxes.get(i).getSelectedItem().toString() == "Integer") {
				
					columns.add(new Column(column.getText(), ColumnDataType.INTEGER));
				
				}else if(comboBoxes.get(i).getSelectedItem().toString() == "Float") {
				
					columns.add(new Column(column.getText(), ColumnDataType.FLOAT));
				
				}else if(comboBoxes.get(i).getSelectedItem().toString() == "String") {
				
					columns.add(new Column(column.getText(), ColumnDataType.STRING));
				
				}else if(comboBoxes.get(i).getSelectedItem().toString() == "Character") {
				
					columns.add(new Column(column.getText(), ColumnDataType.CHARACTER));
				
				}else {
						
					columns.add(new Column(column.getText(), ColumnDataType.BOOLEAN));
					
				}
				i++;
			}
			
			dispose();
			
		}
		
	}

}
