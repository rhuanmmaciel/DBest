package gui.frames.forms.importexport;

import gui.frames.forms.FormBase;

import java.awt.*;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public abstract class ImportExportAsForm extends FormBase implements ActionListener{

	protected JButton btnCsv = new JButton("Arquivo csv");
	protected JLabel lblText = new JLabel();
	protected JPanel centerPane = new JPanel();


	public ImportExportAsForm() {

		super((Window) null);

		initializeGUI();
		
	}

	private void initializeGUI() {
		
		contentPane.removeAll();

		contentPane.add(centerPane, BorderLayout.CENTER);

		btnCsv.addActionListener(this);
		
		centerPane.add(lblText);
		centerPane.add(btnCsv);

	}

}
