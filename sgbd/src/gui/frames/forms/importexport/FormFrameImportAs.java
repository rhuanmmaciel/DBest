package gui.frames.forms.importexport;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import entities.TableCell;
import enums.FileType;
import util.ImportFile;

@SuppressWarnings("serial")
public class FormFrameImportAs extends FormFrameImportExportAs{

	private AtomicReference<Boolean> deleteCellReference;
	private TableCell tableCell;
	private List<String> tablesName;
	private AtomicReference<File> lastDirectoryRef;
	
	public FormFrameImportAs(TableCell tableCell, List<String> tablesName, AtomicReference<Boolean> deleteCellReference,
							 AtomicReference<File> lastDirectoryRef) {
		
		super((Window)null);
		setModal(true);
		
		this.tablesName = tablesName;
		this.tableCell = tableCell;
		this.deleteCellReference = deleteCellReference;
		this.lastDirectoryRef = lastDirectoryRef;
		
		addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent e) {
				  
				deleteCellReference.set(true);
    
			}
			
		 });
		this.setVisible(true);

		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == btnCancel) {
			
			deleteCellReference.set(true);
			dispose();
			
		}else if(e.getSource() == btnCsv) {
			
			dispose();
			new ImportFile(tableCell, FileType.CSV, tablesName, deleteCellReference, lastDirectoryRef);

			
		}else if(e.getSource() == btnXlsXlsxOdt) {
			
			dispose();
			new ImportFile(tableCell, FileType.EXCEL, tablesName, deleteCellReference, lastDirectoryRef);
			
		}else if(e.getSource() == btnSql) {
			
			dispose();
			
		}else if(e.getSource() == btnHead) {
			
			dispose();
			new ImportFile(tableCell, FileType.DAT, tablesName, deleteCellReference, lastDirectoryRef);
			
		}
		
	}	
}
