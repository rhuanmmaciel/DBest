package gui.frames.forms.importexport;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
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
	private List<Cell> cells;
	private JButton btnCancel;
	
	public static void main(mxCell jCell, mxGraphComponent graph, List<Cell> cells, AtomicReference<Cell> cell) {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FormFrameSelectCell frame = new FormFrameSelectCell(jCell, graph, cells, cell);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}
	
	public FormFrameSelectCell(mxCell jCell, mxGraphComponent graph, List<Cell> cells, AtomicReference<Cell> cell) {
		
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
		
		initializeGUI();
		
	}
	
	private void initializeGUI() {
	    setBounds(100, 100, 700, 500);
	    setLocationRelativeTo(null);

	    getContentPane().add(graph);

	    graph.getGraphControl().addMouseListener(this);

	    // Criar o novo JPanel para o botão
	    JPanel buttonPanel = new JPanel();
	    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

	    btnCancel = new JButton("Cancelar");
	    
	    // Adicionar o Box.Filler para posicionar o botão no canto direito
	    buttonPanel.add(Box.createHorizontalGlue());
	    buttonPanel.add(btnCancel);

	    // Adicionar o JPanel do botão ao JDialog na posição sul (BOTTOM)
	    getContentPane().add(buttonPanel, BorderLayout.SOUTH);

	    this.setVisible(true);
	}


	@Override
	public void actionPerformed(ActionEvent e) {

		if(e.getSource() == btnCancel) {
			
			dispose();
			
		}
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
		jCell = (mxCell) graph.getCellAt(e.getX(), e.getY());
		
		for(Cell i : cells) {
			
			mxCell j = (mxCell) i.getCell();
			
			if(j.equals(jCell)) {
				
				cell.set(i);
				
			}
			
		}

		dispose();
		
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