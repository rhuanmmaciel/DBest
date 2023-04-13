package gui.frames.forms.importexport;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;

import controller.ActionClass;
import entities.Cell;
import util.ExportTable;

@SuppressWarnings("serial")
public class FormFrameExportTable extends JDialog implements ActionListener{

	private JPanel contentPane;
	
	private JButton btnCancel;
	private JButton btnPNG; 
	private JButton btnTable;
	private JButton btnGraph;
	
	private JLabel lblPickFileExtension;
	private Map<mxCell, Cell> cells;
	private mxGraphComponent graph;
	private AtomicReference<Boolean> cancelService;
	private AtomicReference<File> lastDirectoryRef;
	
	public FormFrameExportTable(AtomicReference<Boolean> cancelService,
								AtomicReference<File> lastDirectoryRef) {

		super((Window)null);
		setModal(true);
		
		this.cancelService = cancelService;
		this.graph = ActionClass.getGraphComponent();
		this.cells = ActionClass.getCells();
		this.lastDirectoryRef = lastDirectoryRef;
		
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
		
		btnPNG = new JButton("Imagem da árvore");
		btnPNG.addActionListener(this);
		
		btnTable= new JButton("Tabela");
		btnTable.addActionListener(this);
		
		btnGraph = new JButton("Árvore");
		btnGraph.setEnabled(false);
		btnGraph.addActionListener(this);
		
		lblPickFileExtension = new JLabel("Salvar como:");
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap(206, Short.MAX_VALUE)
					.addComponent(btnCancel))
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(46)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(btnPNG, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnGraph, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnTable, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(56, Short.MAX_VALUE))
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(104)
					.addComponent(lblPickFileExtension)
					.addContainerGap(108, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblPickFileExtension)
					.addGap(35)
					.addComponent(btnGraph)
					.addGap(8)
					.addComponent(btnPNG)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnTable)
					.addPreferredGap(ComponentPlacement.RELATED, 63, Short.MAX_VALUE)
					.addComponent(btnCancel))
		);

		addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent e) {
				  
    
			}
			
		 });
		
		contentPane.setLayout(gl_contentPane);
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == btnCancel) {
			
			dispose();
			
		}else if(e.getSource() == btnGraph) {
			
			new ExportTable(cells, "", 1);
			dispose();
			
		}else if(e.getSource() == btnPNG) {
			
			dispose();
			new ExportTable(graph);
			
		}else if(e.getSource() == btnTable) {
			
			dispose();
	
			mxCell cell = null;
			
			new FormFrameExportAs(cell, graph, cells, cancelService, lastDirectoryRef);
			
		}
		
	}	
}
