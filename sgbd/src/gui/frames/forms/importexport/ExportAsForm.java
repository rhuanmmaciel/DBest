package gui.frames.forms.importexport;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicReference;

import com.mxgraph.model.mxCell;

import entities.cells.Cell;
import enums.FileType;
import files.ExportFile;

import javax.swing.*;

public class ExportAsForm extends ImportExportAsForm implements ActionListener{

	private final AtomicReference<Boolean> cancelService;
	private final JButton btnFyi = new JButton("Fyi DataBase");
	private final JButton btnMySQLScript = new JButton("Script SQL");
	private final Cell cell;
	
	public ExportAsForm(mxCell jCell, AtomicReference<Boolean> cancelService) {

		setModal(true);

		this.cancelService = cancelService;
		this.cell = Cell.getCells().get(jCell);

		initGUI();

	}

	private void initGUI(){

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				closeWindow();
			}
		});

		JPanel pane = new JPanel(new FlowLayout());

		pane.add(btnFyi);
		pane.add(btnCsv);
		pane.add(btnMySQLScript);

		contentPane.add(pane, BorderLayout.CENTER);

		btnMySQLScript.addActionListener(this);
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
				new ExportFile(cell, FileType.FYI);
			
		}else if(e.getSource() == btnMySQLScript){

			closeWindow();
			if(!cancelService.get())
				new ExportFile(cell, FileType.SQL);

		}
		
	}

	protected void closeWindow() {
		dispose();
	}
}
