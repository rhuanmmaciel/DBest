package gui.frames.forms.importexport;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import entities.Cell;

@SuppressWarnings("serial")
public class FormFrameSelectCell extends JDialog implements ActionListener, MouseListener{

	private mxGraphComponent graph;
	private mxCell jCell;
	private AtomicReference<Cell> cell;
	private Map<mxCell, Cell> cells;
	private JButton btnCancel;
	private AtomicReference<Boolean> cancelService;
	
	public FormFrameSelectCell(mxCell jCell, mxGraphComponent graph, Map<mxCell, Cell> cells, AtomicReference<Cell> cell, AtomicReference<Boolean> cancelService) {
		
		super((Window)null);
		setModal(true);
		
		mxIGraphModel model = graph.getGraph().getModel();

		mxGraph newGraph = new mxGraph();

		newGraph.setModel(model);

		mxGraphComponent newGraphComponent = new mxGraphComponent(newGraph);

		getContentPane().add(newGraphComponent, BorderLayout.CENTER);
		
		this.graph = new mxGraphComponent(newGraph);
		this.jCell = jCell;
		this.cells = cells;
		this.cell = cell;
		this.cancelService = cancelService;
		
		initializeGUI();
		
	}
	
	private void initializeGUI() {
	    setBounds(100, 100, 700, 500);
	    setLocationRelativeTo(null);

	    getContentPane().add(graph);

	    graph.getGraphControl().addMouseListener(this);

	    JPanel buttonPanel = new JPanel();
	    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

	    btnCancel = new JButton("Cancelar");
	    btnCancel.addActionListener(this);
	    
	    buttonPanel.add(Box.createHorizontalGlue());
	    buttonPanel.add(btnCancel);

	    getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {

				cancelService.set(true);
				dispose();
			}

		});
	    
	    this.setVisible(true);
	}


	@Override
	public void actionPerformed(ActionEvent e) {

		if(e.getSource() == btnCancel) {
			
			cancelService.set(true);
			dispose();
			
		}
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
		jCell = (mxCell) graph.getCellAt(e.getX(), e.getY());
		
		if(jCell != null) {
			
			cell.set(cells.get(jCell));
	
			dispose();
		
		}
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}