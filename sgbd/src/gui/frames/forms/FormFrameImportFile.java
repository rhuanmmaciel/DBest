package gui.frames.forms;

import java.awt.EventQueue;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

import entities.TableCell;
import enums.FileType;
import util.ImportFile;

@SuppressWarnings("serial")
public class FormFrameImportFile extends JDialog implements ActionListener{

	private JPanel contentPane;
	private JButton btnCancel;
	private JButton btnXlsXlsxOdt; 
	private JButton btnSql;
	private JButton btnCsv;
	private TableCell tableCell;
	private JLabel lblPickFileExtension;
	
	public static void main(TableCell tableCell) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FormFrameImportFile frame = new FormFrameImportFile(tableCell);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public FormFrameImportFile(TableCell tableCell) {

		super((Window)null);
		setModal(true);
		
		this.tableCell = tableCell;
		
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
		
		btnXlsXlsxOdt = new JButton(".xls  .xlsx  .odt");
		btnXlsXlsxOdt.addActionListener(this);
		
		btnSql= new JButton(".sql");
		btnSql.addActionListener(this);
		
		btnCsv = new JButton(".csv");
		btnCsv.addActionListener(this);
		
		lblPickFileExtension = new JLabel("Escolha a extensão do arquivo:");
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap(194, Short.MAX_VALUE)
					.addComponent(btnCancel)
					.addContainerGap())
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(34)
					.addComponent(lblPickFileExtension)
					.addContainerGap(47, Short.MAX_VALUE))
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(116)
					.addComponent(btnSql, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(116, Short.MAX_VALUE))
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(69)
					.addComponent(btnXlsXlsxOdt)
					.addContainerGap(99, Short.MAX_VALUE))
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(110)
					.addComponent(btnCsv, GroupLayout.PREFERRED_SIZE, 71, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(121, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblPickFileExtension)
					.addGap(23)
					.addComponent(btnCsv)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnXlsXlsxOdt)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnSql)
					.addPreferredGap(ComponentPlacement.RELATED, 42, Short.MAX_VALUE)
					.addComponent(btnCancel))
		);

		contentPane.setLayout(gl_contentPane);
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == btnCancel) {
			
			dispose();
			
		}else if(e.getSource() == btnCsv) {
			
			new ImportFile(tableCell, FileType.CSV);
			dispose();
			
		}else if(e.getSource() == btnXlsXlsxOdt) {
			
			new ImportFile(tableCell, FileType.EXCEL);
			dispose();
			
		}else if(e.getSource() == btnSql) {
			
			dispose();
			
		}
		
	}	
}
