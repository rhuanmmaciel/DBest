package gui.frames.forms.importexport;

import controller.ConstantController;
import entities.cells.TableCell;
import enums.FileType;
import files.ImportFile;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicReference;

public class ImportAsForm extends ImportExportAsForm {

	private final AtomicReference<Boolean> deleteCellReference;
	private TableCell tableCell;
	private final JButton btnHead = new JButton(ConstantController.getString("importAs.headerButton"));

	{
		this.tableCell = null;
	}
	
	public ImportAsForm(AtomicReference<Boolean> deleteCellReference) {

		setModal(true);
		this.deleteCellReference = deleteCellReference;

		initGUI();

	}

	private void initGUI(){

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				closeWindow();
			}
		});

		btnHead.addActionListener(this);

		setTitle(ConstantController.getString("importAs.importTitle"));

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
			this.tableCell = new ImportFile(FileType.FYI, deleteCellReference).getResult();
			
		}
		
	}

	protected void closeWindow() {

		deleteCellReference.set(true);
		dispose();

	}
}
