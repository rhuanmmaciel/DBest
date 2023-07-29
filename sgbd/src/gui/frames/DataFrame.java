package gui.frames;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import controller.ConstantController;
import entities.Column;
import entities.cells.Cell;
import entities.cells.OperationCell;
import database.TuplesExtractor;
import gui.utils.JTableUtils;
import sgbd.query.Operator;

public class DataFrame extends JDialog implements ActionListener {

	private final JLabel lblText = new JLabel();
	private final JLabel lblPages = new JLabel();
	private final JTable table = new JTable();
	private final JButton btnLeft = new JButton("<");
	private final JButton btnRight = new JButton(">");
	private final JButton btnAllLeft = new JButton("<<");
	private final JButton btnAllRight = new JButton(">>");

	private final List<Map<String, String>> rows;
	private final List<String> columnsName;
	private int currentIndex;
	private final Operator operator;
	private Integer lastPage = null;
	private int currentLastPage = -1;

	public DataFrame(Cell cell) {

		super((Window) null, "DataFrame");
		setModal(true);
		
		if(cell instanceof OperationCell operationCell) lblText.setText(operationCell.getType().DISPLAY_NAME+":");
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

		if(page > currentLastPage) {

			Map<String, String> row = TuplesExtractor.getRow(operator, true);

			while (row != null && currentElement < lastElement) {

				rows.add(row);
				row = TuplesExtractor.getRow(operator, true);
				if (row != null) currentElement++;
				if (currentElement >= lastElement) {
					rows.add(row);
				}

			}

			if (row == null && lastPage == null)
				lastPage = currentElement / 15;

		}

		currentLastPage = Math.max(currentLastPage, page);

		if (!rows.isEmpty()) {

			for (Map.Entry<String, List<String>> columns : operator.getContentInfo().entrySet())
				for(String columnName : columns.getValue())
					model.addColumn(Column.putSource(columnName, columns.getKey()));

			int i = page * 15 + 1;
			int endOfList = Math.min(lastElement+1, rows.size());
			for (Map<String, String> currentRow : rows.subList(firstElement, endOfList)) {

				Object[] line = new Object[rows.get(firstElement).size() + 1];
				line[0] = i++;

				for (int j = 0; j < currentRow.size(); j++)
					line[j+1] = currentRow.get(model.getColumnName(j+1));

				model.addRow(line);

			}

		} else {

			model.setColumnIdentifiers(columnsName.toArray());

		}

		table.setModel(model);

		JTableUtils.preferredColumnWidthByValues(table, 0);

		for(int i = 1; i < table.getColumnCount(); i++)
			JTableUtils.preferredColumnWidthByColumnName(table, i);

		table.getColumnModel().getColumn(0).setResizable(false);
		
		JTableUtils.setColumnBold(table, 0);
		JTableUtils.setNullInRed(table);

		table.setEnabled(false);
		table.setFillsViewportHeight(true);
		table.repaint();

	}

	private void initializeGUI() {

		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));

		setContentPane(contentPane);

		btnLeft.addActionListener(this);
		btnAllLeft.addActionListener(this);
		btnRight.addActionListener(this);
		btnAllRight.addActionListener(this);

		JPanel northPane = new JPanel(new FlowLayout());
		northPane.add(lblText);
		northPane.add(lblPages);

		JPanel tablePanel = new JPanel(new BorderLayout());
		tablePanel.add(table.getTableHeader(), BorderLayout.NORTH);
		tablePanel.add(table, BorderLayout.CENTER);
		JScrollPane scrollPane = new JScrollPane(tablePanel);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		JPanel southPane = new JPanel(new FlowLayout());
		southPane.add(btnAllLeft);
		southPane.add(btnLeft);
		southPane.add(btnRight);
		southPane.add(btnAllRight);

		contentPane.add(northPane, BorderLayout.NORTH);
		contentPane.add(scrollPane, BorderLayout.CENTER);
		contentPane.add(southPane, BorderLayout.SOUTH);

		verifyButtons();

		if(table.getRowCount() == 0) lblPages.setText("0/0");

		resize();
		setLocationRelativeTo(null);
		this.setVisible(true);
	}

	private void resize(){
		pack();
		if (getWidth() > ConstantController.UI_WIDTH) {
			int height = getHeight();
			setSize((int) (ConstantController.UI_WIDTH *0.95), height);
		}
	}

	private void verifyButtons() {

		btnLeft.setEnabled(currentIndex != 0);
		btnAllLeft.setEnabled(currentIndex != 0);
		btnRight.setEnabled(lastPage == null || lastPage != currentIndex);
		btnAllRight.setEnabled(lastPage == null || lastPage != currentIndex);

		lblPages.setText(currentIndex + 1 + "/" + (lastPage == null ? " ???" : lastPage + 1));

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == btnRight) {

			currentIndex++;

		} else if (e.getSource() == btnLeft) {

			currentIndex--;

		}if(e.getSource() == btnAllLeft){

			currentIndex = Math.max(currentIndex - 100, 0);

		}else if(e.getSource() == btnAllRight){

			if(lastPage == null)
				for(int i = 0; lastPage == null && i < 100; i++) updateTable(++currentIndex);
			else currentIndex = lastPage;

		}

		updateTable(currentIndex);
		verifyButtons();

	}

	private void closeWindow(){

		operator.close();
		operator.freeResources();
		dispose();

	}

}