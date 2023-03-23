package gui.frames.forms.importexport;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;

import entities.Cell;
import enums.FileType;
import util.ExportTable;

@SuppressWarnings("serial")
public class FormFrameExportAs extends FormFrameImportExportAs implements ActionListener{

	private mxGraphComponent graph;
	private mxCell jCell;
	private List<Cell> cells;
	
	public FormFrameExportAs(mxCell cell, mxGraphComponent graph, List<Cell> cells) {

		super((Window)null);
		setModal(true);
		
		this.cells = cells;
		this.jCell = cell;
		this.graph = graph;
		
		this.setVisible(true);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == btnCancel) {
			
			dispose();
			
		}else if(e.getSource() == btnCsv) {
			
			dispose();
			
			AtomicReference<Cell> cell = new AtomicReference<>();
			
			new FormFrameSelectCell(jCell, graph, cells, cell);
			new ExportTable(cell, FileType.CSV);
			
		}else if(e.getSource() == btnXlsXlsxOdt) {
			
			dispose();
			
		}else if(e.getSource() == btnSql) {
			
			dispose();
			
		}else if(e.getSource() == btnHead) {
			
			dispose();
			
			AtomicReference<Cell> cell = new AtomicReference<>();
			
			new FormFrameSelectCell(jCell, graph, cells, cell);
			new ExportTable(cell, FileType.DAT);
			
		}
		
	}		
	
}
