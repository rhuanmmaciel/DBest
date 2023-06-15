package gui.frames;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import entities.cells.Cell;
import entities.cells.OperationCell;
import entities.utils.TableFormat;
import gui.frames.main.MainFrame;
import gui.utils.JTableUtils;
import sgbd.query.Operator;

public class DataFrame extends JDialog implements ActionListener {

	private final int WIDTH = (int)(MainFrame.WIDTH * 1.1);
	private final int HEIGHT = (int)(MainFrame.HEIGHT * 0.6);

	private final JLabel lblText = new JLabel();
	private final JTable table = new JTable();
	private JButton btnLeft;
	private JButton btnRight;

	private Map<String, String> row;
	private final List<Map<String, String>> rows;
	private final List<String> columnsName;
	private int currentIndex;
	private final Operator operator;
	private Integer lastPage = null;

	public DataFrame(Cell cell) {

		super((Window) null, "DataFrame");
		setModal(true);
		
		if(cell instanceof OperationCell operationCell) lblText.setText(operationCell.getType().getDisplayName()+":");
		else lblText.setText(cell.getName()+":");

		this.operator = cell.getOperator();
		operator.open();

		columnsName = cell.getColumnSourceNames();

		rows = new ArrayList<>();

		updateTable(0);
		currentIndex = 0;

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				closeWindow();
			}
		});

		initializeGUI();

	}
	
	private void updateTable(int page) {

		int firstElement = page * 15;
		int lastElement = page * 15 + 14;
		int currentElement = firstElement;

		DefaultTableModel model = new DefaultTableModel();

		model.addColumn("");

		row = TableFormat.getRow(operator);

		while (row != null && currentElement < lastElement){

			rows.add(row);
			row = TableFormat.getRow(operator);
			if(row != null) currentElement++;
			if(currentElement >= lastElement) rows.add(row);

		}

		if(row == null && lastPage == null)
			lastPage = currentElement / 15;

		if (!rows.isEmpty()) {

			for (String columnName : rows.get(firstElement).keySet())
				model.addColumn(columnName);

			int i = page * 15 + 1;
			int endOfList = Math.min(lastElement+1, rows.size());
			for (Map<String, String> currentRow : rows.subList(firstElement, endOfList)) {

				Object[] line = new Object[rows.get(firstElement).size() + 1];
				line[0] = i++;

				int k = 1;
				for (String key : currentRow.keySet()) {
					line[k] = currentRow.get(key);
					k++;
				}
				model.addRow(line);

			}

		} else {

			model.setColumnIdentifiers(columnsName.toArray());

		}

		table.setModel(model);

		JTableUtils.minColumnWidth(table, 0);

		table.getColumnModel().getColumn(0).setResizable(false);
		
		JTableUtils.setColumnBold(table, 0);
		JTableUtils.setNullInRed(table);

		table.setEnabled(false);
		table.setFillsViewportHeight(true);
		table.repaint();

	}

	

	private void initializeGUI() {

		setBounds(0, 0, WIDTH, HEIGHT);
		setLocationRelativeTo(null);

		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);

		JScrollPane scrollPane = new JScrollPane(table);

		btnLeft = new JButton("<");
		btnLeft.addActionListener(this);

		btnRight = new JButton(">");
		btnRight.addActionListener(this);

		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
		        .addGroup(Alignment.TRAILING,
		                gl_contentPane.createSequentialGroup()
		                        .addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
		                                .addGroup(gl_contentPane.createSequentialGroup()
		                                        .addContainerGap()
		                                        .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, WIDTH * 92 / 100, GroupLayout.PREFERRED_SIZE))
		                                .addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
		                                        .addGroup(gl_contentPane.createSequentialGroup()
		                                                .addGap(WIDTH / 100)
		                                                .addComponent(lblText)
		                                                .addPreferredGap(ComponentPlacement.RELATED, WIDTH * 85 / 100, Short.MAX_VALUE))
		                                        .addGroup(gl_contentPane.createSequentialGroup()
		                                                .addContainerGap()
		                                                .addComponent(btnLeft)
		                                                .addPreferredGap(ComponentPlacement.UNRELATED)
		                                                .addComponent(btnRight)
		                                                .addPreferredGap(ComponentPlacement.RELATED)
		                                              )))
		                                .addGap(WIDTH * 5 / 100)));



		gl_contentPane.setVerticalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
		        .addGroup(gl_contentPane.createSequentialGroup()
		                .addContainerGap()
		                .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
		                        .addComponent(lblText))
		                .addGap(HEIGHT * 5 / 100)
		                .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, HEIGHT * 60 / 100, GroupLayout.PREFERRED_SIZE)
		                .addGap(HEIGHT * 5 / 100)
		                .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
		                        .addComponent(btnRight)
		                        .addComponent(btnLeft))
		                .addContainerGap(HEIGHT * 5 / 100, Short.MAX_VALUE)));


		contentPane.setLayout(gl_contentPane);

		verifyButtons();
		
		this.setVisible(true);
	}

	private void verifyButtons() {

		btnLeft.setEnabled(currentIndex != 0);
		btnRight.setEnabled(lastPage == null || lastPage != currentIndex);

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == btnRight) {

			currentIndex++;

		} else if (e.getSource() == btnLeft) {

			currentIndex--;

		}

		updateTable(currentIndex);
		verifyButtons();

	}

	private void closeWindow(){

		operator.close();
		dispose();

	}

}