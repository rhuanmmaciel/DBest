package gui.frames.forms.importexport;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class FormFrameImportFromDsl extends JDialog implements ActionListener{
	
	private final JButton btnReady = new JButton("Pronto");
	private final JButton btnCancel = new JButton("Cancelar");
	private final JPanel panel = new JPanel();
	private final JPanel bottomPane = new JPanel();
	private final Box searchFile = Box.createHorizontalBox();
	private final JTextField textFieldFile = new JTextField();
	private final JButton btnSearchFile = new JButton("Procurar");
	
	public FormFrameImportFromDsl() {
		
		super((Window)null);
		initGUI();
	
	}

	private void initGUI() {
		
		setModal(true);
		getContentPane().setLayout(new BorderLayout(0, 0));
		getContentPane().add(panel, BorderLayout.NORTH);
		
		Box content = Box.createVerticalBox();
		panel.add(content);
		
		content.add(searchFile);
		textFieldFile.setColumns(10);
		
		searchFile.add(textFieldFile);
		
		searchFile.add(btnSearchFile);
		
		FlowLayout flowLayout = (FlowLayout) bottomPane.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		getContentPane().add(bottomPane, BorderLayout.SOUTH);
		
		btnReady.addActionListener(this);
		btnCancel.addActionListener(this);
		
		bottomPane.add(btnCancel);
		bottomPane.add(btnReady);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		
		
	}
	
	
}
