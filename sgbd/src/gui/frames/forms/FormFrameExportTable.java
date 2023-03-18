package gui.frames.forms;

import java.awt.EventQueue;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

import entities.Cell;
import util.ExportTable;

@SuppressWarnings("serial")
public class FormFrameExportTable extends JDialog implements ActionListener{

	private JPanel contentPane;
	private JButton btnCancel;
	private JButton btnPNG; 
	private JButton btnTableCSV;
	private JButton btnGraph;
	private AtomicReference<Boolean> deleteCellReference;
	private JLabel lblPickFileExtension;
	private List<Cell> cells;
	private JFrame exportframe;
	//private List<List<String>> data;
	private JTextField textField;
	private JLabel lblNewLabel;
	
	public static void main(AtomicReference<Boolean> deleteCellReference,List<Cell> cells,JFrame exportframe,List<List<String>> data) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FormFrameExportTable frame = new FormFrameExportTable(deleteCellReference,cells,exportframe,data);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public FormFrameExportTable(AtomicReference<Boolean> deleteCellReference,List<Cell> cells,JFrame exportframe,List<List<String>> data) {

		super((Window)null);
		setModal(true);
		
		this.deleteCellReference = deleteCellReference;
		this.exportframe = exportframe;
		this.cells = cells;
		//this.data = data;
		
		initializeGUI();
		
	}

	private void initializeGUI() {
		
		setBounds(100, 100, 312, 263);
		setLocationRelativeTo(null);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		
		btnCancel = new JButton("Cancelar");
		btnCancel.addActionListener(this);
		
		btnPNG = new JButton("Imagem");
		btnPNG.addActionListener(this);
		
		btnTableCSV= new JButton("Tabela");
		btnTableCSV.addActionListener(this);
		
		btnGraph = new JButton("Graph");
		btnGraph.addActionListener(this);
		
		lblPickFileExtension = new JLabel("Salvar como:");
		
		textField = new JTextField();
		textField.setColumns(10);
		
		lblNewLabel = new JLabel("Nome do arquivo:");
		
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(100)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(btnPNG, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnGraph, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnTableCSV, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(86, Short.MAX_VALUE))
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap(211, Short.MAX_VALUE)
					.addComponent(btnCancel))
				.addGroup(Alignment.LEADING, gl_contentPane.createSequentialGroup()
					.addGap(34)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(lblNewLabel)
						.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblPickFileExtension))
					.addContainerGap(166, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblPickFileExtension)
					.addGap(20)
					.addComponent(btnGraph)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnPNG)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnTableCSV)
					.addPreferredGap(ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
					.addComponent(lblNewLabel)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(7)
					.addComponent(btnCancel))
		);

		addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent e) {
				  
				deleteCellReference.set(true);
    
			}
			
		 });
		
		contentPane.setLayout(gl_contentPane);
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == btnCancel) {
			
			deleteCellReference.set(true);
			dispose();
			
		}else if(e.getSource() == btnGraph) {
			
			ExportTable.saveGraph(cells, textField.getText().toString()+".csv");
			dispose();
			
		}else if(e.getSource() == btnPNG) {
			
			ExportTable.exportToImage(exportframe,textField.getText().toString()+".png");
			dispose();
			
		}else if(e.getSource() == btnTableCSV) {
			
			ExportTable.exportToCsv(cells.get(cells.size()-1).getContent(),textField.getText().toString()+".csv");
			dispose();
			
		}
		
	}	
}
