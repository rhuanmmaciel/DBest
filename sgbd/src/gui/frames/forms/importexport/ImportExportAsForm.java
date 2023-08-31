package gui.frames.forms.importexport;

import controller.ConstantController;
import gui.frames.forms.FormBase;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public abstract class ImportExportAsForm extends FormBase implements ActionListener{

	protected JButton btnCsv = new JButton(ConstantController.getString("importExportAs.csvButton"));
	protected JLabel lblText = new JLabel();
	protected JPanel centerPane = new JPanel();


	public ImportExportAsForm() {

		super(null);

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
