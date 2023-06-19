package gui.frames.forms.importexport;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicReference;

import com.mxgraph.model.mxCell;

import entities.cells.Cell;
import enums.FileType;
import files.ExportFile;

import javax.swing.*;

public class FormFrameExportAs extends FormFrameImportExportAs implements ActionListener{

	private final AtomicReference<Boolean> cancelService;
	private final JButton btnFyi = new JButton("Fyi DataBase");
	private final Cell cell;
	
	public FormFrameExportAs(mxCell jCell, AtomicReference<Boolean> cancelService) {

		setModal(true);

		this.cancelService = cancelService;
		this.cell = Cell.getCells().get(jCell);

		initGUI();

	}

	private void initGUI(){

		contentPane.add(btnFyi);
		btnFyi.addActionListener(this);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == btnCancel) {
			
			cancelService.set(true);
			closeWindow();

		}else if(e.getSource() == btnCsv) {
			
			closeWindow();

			if(!cancelService.get())
				new ExportFile(cell, FileType.CSV);
			
		}else if(e.getSource() == btnFyi) {
			
			closeWindow();
			if(!cancelService.get())
				new ExportFile(cell, FileType.DAT);
			
		}
		
	}

	@Override
	protected void closeWindow() {
		dispose();
	}
}
