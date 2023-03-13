package gui.frames.forms;

import java.awt.EventQueue;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicReference;

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
	private AtomicReference<Boolean> deleteCellReference;
	private TableCell tableCell;
	private JLabel lblPickFileExtension;
	
	public static void main(TableCell tableCell, AtomicReference<Boolean> deleteCellReference) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FormFrameImportFile frame = new FormFrameImportFile(tableCell, deleteCellReference);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public FormFrameImportFile(TableCell tableCell, AtomicReference<Boolean> deleteCellReference) {

		super((Window)null);
		setModal(true);
		
		this.tableCell = tableCell;
		this.deleteCellReference = deleteCellReference;
		
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
		
		btnXlsXlsxOdt = new JButton(".xls  .xlsx");
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
					.addGap(34)
					.addComponent(lblPickFileExtension)
					.addContainerGap(47, Short.MAX_VALUE))
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(100)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(btnXlsXlsxOdt, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnCsv, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnSql, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))
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
					.addComponent(btnCsv)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnXlsXlsxOdt)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnSql)
					.addPreferredGap(ComponentPlacement.RELATED, 57, Short.MAX_VALUE)
					.addComponent(btnCancel))
		);

		addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent e) {
				  
				deleteCellReference.set(true);
    
			}
			
		 });
		
		contentPane.setLayout(gl_contentPane);
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == btnCancel) {
			
			deleteCellReference.set(true);
			dispose();
			
		}else if(e.getSource() == btnCsv) {
			
			new ImportFile(tableCell, FileType.CSV, deleteCellReference);
			dispose();
			
		}else if(e.getSource() == btnXlsXlsxOdt) {
			
			new ImportFile(tableCell, FileType.EXCEL, deleteCellReference);
			dispose();
			
		}else if(e.getSource() == btnSql) {
			
			dispose();
			
		}
		
	}	
}
