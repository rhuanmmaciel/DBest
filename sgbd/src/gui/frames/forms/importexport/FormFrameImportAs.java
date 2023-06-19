package gui.frames.forms.importexport;

import java.awt.event.ActionEvent;
import java.util.concurrent.atomic.AtomicReference;

import entities.cells.TableCell;
import enums.FileType;
import files.ImportFile;

import javax.swing.*;

public class FormFrameImportAs extends FormFrameImportExportAs{

	private final AtomicReference<Boolean> deleteCellReference;
	private TableCell tableCell;
	private final JButton btnHead = new JButton("Arquivo head");

	{
		this.tableCell = null;
	}
	
	public FormFrameImportAs(AtomicReference<Boolean> deleteCellReference) {

		setModal(true);
		this.deleteCellReference = deleteCellReference;

		initGUI();

	}

	private void initGUI(){

		btnHead.addActionListener(this);

		setTitle("Importar");

		centerPane.add(btnHead);

		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		revalidate();

	}

	public TableCell getResult() {
		
		return tableCell;
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == btnCsv) {
			
			dispose();
			this.tableCell = new ImportFile(FileType.CSV, deleteCellReference).getResult();

			
		}else if(e.getSource() == btnHead) {

			dispose();
			this.tableCell = new ImportFile(FileType.DAT, deleteCellReference).getResult();
			
		}
		
	}

	@Override
	protected void closeWindow() {

		deleteCellReference.set(true);
		dispose();

	}
}
