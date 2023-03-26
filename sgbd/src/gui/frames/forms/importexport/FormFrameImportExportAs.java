package gui.frames.forms.importexport;

import java.awt.Window;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public abstract class FormFrameImportExportAs extends JDialog implements ActionListener{

	protected JPanel contentPane;
	
	protected JButton btnHead;
	protected JButton btnCancel;
	protected JButton btnXlsXlsxOdt; 
	protected JButton btnSql;
	protected JButton btnCsv;
	protected JLabel lblPickFileExtension;
	
	public FormFrameImportExportAs(Window window) {

		super(window);
		
		initializeGUI();
		
	}

	private void initializeGUI() {
		
		setBounds(100, 100, 312, 263);
		setLocationRelativeTo(null);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		
		btnCancel = new JButton("Cancelar");
		btnCancel.addActionListener(this);
		
		btnXlsXlsxOdt = new JButton(".xlsx");
		btnXlsXlsxOdt.addActionListener(this);
		btnXlsXlsxOdt.setEnabled(false);
		
		btnSql= new JButton(".sql");
		btnSql.addActionListener(this);
		btnSql.setEnabled(false);
		
		btnCsv = new JButton(".csv");
		btnCsv.addActionListener(this);
		
		btnHead = new JButton(".head");
		btnHead.addActionListener(this);
		
		lblPickFileExtension = new JLabel("Escolha a extensão do arquivo:");
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(34)
					.addComponent(lblPickFileExtension)
					.addContainerGap(47, Short.MAX_VALUE))
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(100)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
							.addComponent(btnXlsXlsxOdt, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
							.addComponent(btnCsv, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
							.addComponent(btnSql, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
							.addComponent(btnHead, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(102, Short.MAX_VALUE))
				.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
					.addContainerGap(206, Short.MAX_VALUE)
					.addComponent(btnCancel))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblPickFileExtension)
					.addGap(20)
					.addComponent(btnHead)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnCsv)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnXlsXlsxOdt)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnSql)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnCancel))
		);
		
		contentPane.setLayout(gl_contentPane);
		
	}

}
