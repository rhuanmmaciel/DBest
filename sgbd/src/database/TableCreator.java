package database;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import com.mxgraph.model.mxCell;

import entities.cells.TableCell;
import files.FileUtils;
import gui.frames.main.MainFrame;
import sgbd.prototype.Prototype;
import sgbd.prototype.RowData;
import sgbd.prototype.column.Column;
import sgbd.table.SimpleTable;
import sgbd.table.Table;
import sgbd.table.components.Header;

import javax.swing.*;

public class TableCreator extends JDialog{

	private final JProgressBar progressBar = new JProgressBar(0, 100);
	private final JButton loadTuples = new JButton("Carregar tuplas");

	private TableCell tableCell = null;

	private boolean stopProgress = false;

	private final boolean mustExport;

	public TableCreator(String tableName, List<entities.Column> columns,
						Map<Integer, Map<String, String>> data, AtomicReference<Boolean> exitReference,
						boolean mustExport){
		super((Window) null, "Progresso");

		this.mustExport = mustExport;

		setModal(true);
		setLayout(new FlowLayout());
		progressBar.setStringPainted(true);
		add(progressBar);
		add(loadTuples);

		loadTuples.addActionListener(e -> {
			loadTuples.setEnabled(false);
			SwingWorker<TableCell, Void> worker = new SwingWorker<>() {
				@Override
				protected TableCell doInBackground() {
					return createTable(tableName, columns, data);
				}

				@Override
				protected void done() {
					try {
						tableCell = get();
						dispose();
					} catch (InterruptedException | ExecutionException ex) {
						ex.printStackTrace();
					}
				}
			};

			worker.execute();
		});

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				stopProgress = true;
				exitReference.set(true);
				dispose();
			}
		});

		pack();
		setLocationRelativeTo(null);
		setVisible(true);

	}

	private TableCell createTable(String tableName, List<entities.Column> columns,
			Map<Integer, Map<String, String>> data) {

		List<RowData> rows = new ArrayList<>(getRowData(columns, data));

		Prototype prototype = new Prototype();

		for (entities.Column column : columns) {

			short size;
			short flags;

			switch(column.getType()) {
			case INTEGER -> {
				size = 4;
				flags = Column.SIGNED_INTEGER_COLUMN;
			}case LONG ->{
				size = 8;
				flags = Column.SIGNED_INTEGER_COLUMN;
			}case FLOAT -> {
				size = 4;
				flags = Column.FLOATING_POINT;
			}case DOUBLE -> {
				size = 8;
				flags = Column.FLOATING_POINT;
			}case STRING -> {
				size = Short.MAX_VALUE;
				flags = Column.STRING;
			}case CHARACTER ->{
				size = 1;
				flags = Column.STRING;
			}default ->{
				size = 100;
				flags = Column.NONE;
			}
			}

			flags |= column.isPK() ? Column.PRIMARY_KEY : Column.CAN_NULL_COLUMN;

			prototype.addColumn(column.getName(), size, flags);

		}

		Table table = SimpleTable.openTable(new Header(prototype, tableName));
		table.open();

		insertRows(table, rows);

		if(stopProgress){
			FileUtils.deleteFile(Path.of(tableName+".dat"));
			return null;
		}

		table.saveHeader(tableName+".head");

		if(mustExport) return new TableCell(null, tableName, "table", columns, table, prototype);

		mxCell jCell = (mxCell) MainFrame.getGraph().insertVertex(MainFrame.getGraph().getDefaultParent(), null,
				tableName, 0, 0, 80, 30, "table");

		return new TableCell(jCell, tableName, "table", columns, table, prototype);

	}

	private List<RowData> getRowData(List<entities.Column> columns, Map<Integer, Map<String, String>> content) {

		List<RowData> rows = new ArrayList<>();

		for (Map<String, String> line : content.values()) {

			RowData rowData = new RowData();

			for (String data : line.keySet()) {

				entities.Column column = columns.stream().filter(x -> x.getName().equals(data)).findFirst()
						.orElseThrow();

				if (!line.get(data).equals("null") && !line.get(data).equals(""))
					switch (column.getType()) {
						case INTEGER -> rowData.setInt(column.getName(), (int) (Double.parseDouble(line.get(data).strip())));
						case LONG -> rowData.setLong(column.getName(), (long) (Double.parseDouble(line.get(data).strip())));
						case FLOAT -> rowData.setFloat(column.getName(), Float.parseFloat(line.get(data).strip()));
						case DOUBLE -> rowData.setDouble(column.getName(), Double.parseDouble(line.get(data).strip()));
						default -> rowData.setString(column.getName(), line.get(data).strip());
					}

			}

			rows.add(rowData);

		}

		return rows;

	}

	public void insertRows(Table table, List<RowData> rows) {
		int totalRows = rows.size();
		int progress = 0;
		long startTime = System.currentTimeMillis();

		while (progress < totalRows && !stopProgress) {
			RowData row = rows.get(progress);
			table.insert(row);
			progress++;
			int percentage = 100 * progress / totalRows;

			int finalProgress = progress;
			SwingUtilities.invokeLater(() -> {
				progressBar.setValue(percentage);
				progressBar.setString(finalProgress + "/" + totalRows);
			});
		}

		System.out.println("Tempo gasto: " + (System.currentTimeMillis() - startTime) + " milissegundos.");
	}

	public TableCell getTableCell(){
		return tableCell;
	}

}
