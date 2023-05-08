package gui.frames.forms.importexport;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicReference;

import com.mxgraph.model.mxCell;

import entities.cells.Cell;
import enums.FileType;
import util.ExportTable;

@SuppressWarnings("serial")
public class FormFrameExportAs extends FormFrameImportExportAs implements ActionListener{

	private mxCell jCell;
	private AtomicReference<Boolean> cancelService;
	
	public FormFrameExportAs(mxCell cell, AtomicReference<Boolean> cancelService) {

		super((Window)null);
		setModal(true);
		
		this.jCell = cell;
		this.cancelService = cancelService;
		
		this.setVisible(true);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == btnCancel) {
			
			cancelService.set(true);
			dispose();
			
		}else if(e.getSource() == btnCsv) {
			
			dispose();
			
			AtomicReference<Cell> cell = new AtomicReference<>();
			
			new FormFrameSelectCell(jCell, cell, cancelService);
			
			if(!cancelService.get())
				new ExportTable(cell, FileType.CSV, cancelService);
			
		}else if(e.getSource() == btnXlsXlsxOdt) {
			
			dispose();
			
		}else if(e.getSource() == btnSql) {
			
			dispose();
			
		}else if(e.getSource() == btnHead) {
			
			dispose();
			
			AtomicReference<Cell> cell = new AtomicReference<>();
			
			new FormFrameSelectCell(jCell, cell, cancelService);
			
			if(!cancelService.get())
				new ExportTable(cell, FileType.DAT, cancelService);
			
		}
		
	}		
	
}
