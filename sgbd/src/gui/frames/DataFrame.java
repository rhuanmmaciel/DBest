package gui.frames;

import controller.ConstantController;
import database.TuplesExtractor;
import engine.info.Parameters;
import entities.Column;
import entities.cells.Cell;
import entities.cells.OperationCell;
import gui.utils.JTableUtils;
import org.kordamp.ikonli.dashicons.Dashicons;
import org.kordamp.ikonli.swing.FontIcon;
import sgbd.info.Query;
import sgbd.query.Operator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataFrame extends JDialog implements ActionListener {

	private final JLabel lblText = new JLabel();
	private final JLabel lblPages = new JLabel();
	private final JTable table = new JTable();
	private final JButton btnLeft = new JButton();
	private final JButton btnRight = new JButton();
	private final JButton btnAllLeft = new JButton();
	private final JButton btnAllRight = new JButton();
	private final JButton btnStats = new JButton();
	private final JPanel centerPane = new JPanel();
	private JScrollPane scrollPane;
	private final JTextPane textPane = new JTextPane();
	private FontIcon iconStats;

	private final long INITIAL_PK_SEARCH = Query.PK_SEARCH;
	private final long INITIAL_SORT_TUPLES = Query.SORT_TUPLES;
	private final long INITIAL_COMPARE_FILTER = Query.COMPARE_FILTER;
	private final long INITIAL_COMPARE_JOIN = Query.COMPARE_JOIN;
	private final long INITIAL_DISTINCT_TUPLE = Query.COMPARE_DISTINCT_TUPLE;
	private final long INITIAL_IO_SEEK_WRITE_TIME = Parameters.IO_SEEK_WRITE_TIME;
	private final long INITIAL_IO_WRITE_TIME = Parameters.IO_WRITE_TIME;
	private final long INITIAL_IO_SEEK_READ_TIME = Parameters.IO_SEEK_READ_TIME;
	private final long INITIAL_IO_READ_TIME = Parameters.IO_READ_TIME;
	private final long INITIAL_IO_SYNC_TIME = Parameters.IO_SYNC_TIME;
	private final long INITIAL_IO_TOTAL_TIME = Parameters.IO_SYNC_TIME+Parameters.IO_SEEK_WRITE_TIME+
			Parameters.IO_READ_TIME+Parameters.IO_SEEK_READ_TIME+Parameters.IO_WRITE_TIME;
	private final long INITIAL_BLOCK_LOADED = Parameters.BLOCK_LOADED;
	private final long INITIAL_BLOCK_SAVED = Parameters.BLOCK_SAVED;

	private final List<Map<String, String>> rows;
	private final List<String> columnsName;
	private int currentIndex;
	private final Operator operator;
	private Integer lastPage = null;
	private int currentLastPage = -1;

	public DataFrame(Cell cell) {

		super((Window) null, ConstantController.getString("dataframe"));
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

	private void setIcons() {

		int buttonsSize = 15;

		FontIcon iconLeft = FontIcon.of(Dashicons.CONTROLS_BACK);
		iconLeft.setIconSize(buttonsSize);
		btnLeft.setIcon(iconLeft);

		FontIcon iconRight = FontIcon.of(Dashicons.CONTROLS_FORWARD);
		iconRight.setIconSize(buttonsSize);
		btnRight.setIcon(iconRight);

		FontIcon iconAllLeft = FontIcon.of(Dashicons.CONTROLS_SKIPBACK);
		iconAllLeft.setIconSize(buttonsSize);
		btnAllLeft.setIcon(iconAllLeft);

		FontIcon iconAllRight = FontIcon.of(Dashicons.CONTROLS_SKIPFORWARD);
		iconAllRight.setIconSize(buttonsSize);
		btnAllRight.setIcon(iconAllRight);

		iconStats = FontIcon.of(Dashicons.BOOK);
		iconStats.setIconSize(buttonsSize);
		btnStats.setIcon(iconStats);

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

		setIcons();
		btnLeft.addActionListener(this);
		btnAllLeft.addActionListener(this);
		btnRight.addActionListener(this);
		btnAllRight.addActionListener(this);
		btnStats.addActionListener(this);

		JPanel northPane = new JPanel(new FlowLayout());
		northPane.add(lblText);
		northPane.add(lblPages);
		northPane.add(btnStats);

		JPanel tablePanel = new JPanel(new BorderLayout());
		tablePanel.add(table.getTableHeader(), BorderLayout.NORTH);
		tablePanel.add(table, BorderLayout.CENTER);
		scrollPane = new JScrollPane(tablePanel);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		centerPane.add(scrollPane);

		textPane.setEditable(false);

		JPanel southPane = new JPanel(new FlowLayout());
		southPane.add(btnAllLeft);
		southPane.add(btnLeft);
		southPane.add(btnRight);
		southPane.add(btnAllRight);

		contentPane.add(northPane, BorderLayout.NORTH);
		contentPane.add(centerPane, BorderLayout.CENTER);
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

		}else if(e.getSource() == btnStats){

			alternateScreen();

		}

		updateTable(currentIndex);
		updateStats();
		verifyButtons();

	}

	private void updateStats(){

		String stats = ConstantController.getString("dataframe.query") + ":\n" +
				ConstantController.getString("dataframe.query.pkSearch") + " = " +
				(Query.PK_SEARCH - INITIAL_PK_SEARCH) + "\n" +
				ConstantController.getString("dataframe.query.sortedTuples") + " = " +
				(Query.SORT_TUPLES - INITIAL_SORT_TUPLES) + "\n" +
				ConstantController.getString("dataframe.query.filterComparison") + " = " +
				(Query.COMPARE_FILTER - INITIAL_COMPARE_FILTER) + "\n" +
				ConstantController.getString("dataframe.query.joinComparison") + " = " +
				(Query.COMPARE_JOIN - INITIAL_COMPARE_JOIN) + "\n" +
				ConstantController.getString("dataframe.query.distinctTuplesComparison") + " = " +
				(Query.COMPARE_DISTINCT_TUPLE - INITIAL_DISTINCT_TUPLE) + "\n\n" +

				ConstantController.getString("dataframe.disk") + ":" + "\n" +
				ConstantController.getString("dataframe.disk.IOSeekWriteTime") + " = " +
				(Parameters.IO_SEEK_WRITE_TIME - INITIAL_IO_SEEK_WRITE_TIME) / 1000000f + "ms" + "\n" +
				ConstantController.getString("dataframe.disk.IOWriteTime") + " = " +
				(Parameters.IO_WRITE_TIME - INITIAL_IO_WRITE_TIME) / 1000000f + "ms" + "\n" +
				ConstantController.getString("dataframe.disk.IOSeekReadTime") + " = " +
				(Parameters.IO_SEEK_READ_TIME - INITIAL_IO_SEEK_READ_TIME) / 1000000f + "ms" + "\n" +
				ConstantController.getString("dataframe.disk.IOReadTime") + " = " +
				(Parameters.IO_READ_TIME - INITIAL_IO_READ_TIME) / 1000000f + "ms" + "\n" +
				ConstantController.getString("dataframe.disk.IOSyncTime") + " = " +
				(Parameters.IO_SYNC_TIME - INITIAL_IO_SYNC_TIME) / 1000000f + "ms" + "\n" +
				ConstantController.getString("dataframe.disk.IOTime") + " = " + (Parameters.IO_SYNC_TIME
				+ Parameters.IO_SEEK_WRITE_TIME
				+ Parameters.IO_READ_TIME
				+ Parameters.IO_SEEK_READ_TIME
				+ Parameters.IO_WRITE_TIME - INITIAL_IO_TOTAL_TIME) / 1000000f + "ms" + "\n" +
				ConstantController.getString("dataframe.block.loaded") + " = " +
				(Parameters.BLOCK_LOADED - INITIAL_BLOCK_LOADED) + "\n" +
				ConstantController.getString("dataframe.block.saved") + " = " +
				(Parameters.BLOCK_SAVED - INITIAL_BLOCK_SAVED);

		textPane.setText(stats);

		revalidate();

	}

	private void alternateScreen(){

		if(centerPane.isAncestorOf(scrollPane)) {

			centerPane.remove(scrollPane);
			centerPane.add(textPane);
			iconStats.setIkon(Dashicons.EDITOR_TABLE);

		}else{

			centerPane.remove(textPane);
			centerPane.add(scrollPane);
			iconStats.setIkon(Dashicons.BOOK);

		}

		revalidate();
		repaint();

	}

	private void closeWindow(){

		operator.close();
		operator.freeResources();
		dispose();

	}

}