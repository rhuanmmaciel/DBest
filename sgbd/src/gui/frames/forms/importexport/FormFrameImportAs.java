package gui.frames.forms.importexport;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicReference;

import entities.cells.TableCell;
import enums.FileType;
import util.ImportFile;

@SuppressWarnings("serial")
public class FormFrameImportAs extends FormFrameImportExportAs{

	private AtomicReference<Boolean> deleteCellReference;
	private TableCell tableCell;
	
	{
		this.tableCell = null;
	}
	
	public FormFrameImportAs(AtomicReference<Boolean> deleteCellReference) {
		
		super((Window)null);
		setModal(true);
		
		this.deleteCellReference = deleteCellReference;
		
		addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent e) {
				  
				deleteCellReference.set(true);
    
			}
			
		 });
		this.setVisible(true);

		
	}

	public TableCell getResult() {
		
		return tableCell;
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == btnCancel) {
			
			deleteCellReference.set(true);
			dispose();
			
		}else if(e.getSource() == btnCsv) {
			
			dispose();
			this.tableCell = new ImportFile(FileType.CSV, deleteCellReference).getResult();

			
		}else if(e.getSource() == btnXlsXlsxOdt) {
			
			dispose();
			this.tableCell = new ImportFile(FileType.EXCEL, deleteCellReference).getResult();
			
		}else if(e.getSource() == btnSql) {
			
			dispose();
			
		}else if(e.getSource() == btnHead) {
			
			dispose();
			this.tableCell = new ImportFile(FileType.DAT, deleteCellReference).getResult();
			
		}
		
	}	
}
