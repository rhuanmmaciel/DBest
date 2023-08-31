package gui.frames.forms.importexport;

import controller.ConstantController;
import controller.MainController;
import database.TableUtils;
import entities.Column;
import enums.ColumnDataType;
import exceptions.InvalidCsvException;
import files.csv.CsvInfo;
import files.csv.CsvRecognizer;
import files.csv.CsvRecognizer.CsvData;
import gui.frames.ErrorFrame;
import gui.frames.forms.FormBase;
import gui.utils.JTableUtils;
import gui.utils.JTableUtils.CustomTableModel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Path;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class CsvRecognizerForm extends FormBase implements ActionListener {

	private final JPanel headerPanel = new JPanel();
	private final JPanel mainPanel = new JPanel();
	private final JScrollPane scrollPane = new JScrollPane();
	private JTable jTable;
	private DefaultTableModel model;
	private final JTextField txtFieldStringDelimiter = new JTextField();
	private final JTextField txtFieldOtherSeparator = new JTextField();
	private final JTextField txtFieldTableName = new JTextField();
	private final JSpinner spinnerFromRow = new JSpinner();
	private final ButtonGroup separatorGroup = new ButtonGroup();
	private final JRadioButton radioComma = new JRadioButton(ConstantController.getString("csvRecognizer.comma"));
	private final JRadioButton radioSemiColon = new JRadioButton(ConstantController.getString("csvRecognizer.semicolon"));
	private final JRadioButton radioSpace = new JRadioButton(ConstantController.getString("csvRecognizer.space"));
	private final JRadioButton radioOther = new JRadioButton(ConstantController.getString("csvRecognizer.other")+": ");

	private final Map<String, JComboBox<?>> typeComboBoxes = new HashMap<>();
	private final List<String> columnsName = new ArrayList<>();

	private CsvData csvData;

	private final Path path;
	private final AtomicReference<Boolean> exitReference;
	private final StringBuilder tableName;
	private final List<Column> columns;
	private final Map<Integer, Map<String, String>> content;

	private final char defaultSeparator = ',';
	private final char defaultStringDelimiter = '"';
	private final int defaultBeginIndex = 1;

	private char separator = defaultSeparator;
	private char stringDelimiter = defaultStringDelimiter;
	private int beginIndex = defaultBeginIndex;

	public CsvRecognizerForm(Path path, StringBuilder tableName, List<Column> columns,
							 Map<Integer, Map<String, String>> content, AtomicReference<Boolean> exitReference) {

		super(null);
		setModal(true);

		this.exitReference = exitReference;
		this.path = path;
		this.columns = columns;
		this.content = content;
		this.tableName = tableName;

		addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {

				exitReference.set(true);

			}

		});

		initializeGUI();

	}

	private void initializeGUI() {

		setBounds(0, 0, ConstantController.UI_WIDTH, ConstantController.UI_HEIGHT);
		setLocationRelativeTo(null);

		try {

			csvData = CsvRecognizer.importCsv(path, defaultSeparator, defaultStringDelimiter, 1);

		} catch (InvalidCsvException e) {

			new ErrorFrame(e.getMessage());
			csvData = new CsvData(List.of(), List.of(), new Vector<>(), new Vector<>());

		}

		loadJTable();
		initializeHeader();
		initializeMain();

		btnCancel.addActionListener(this);
		btnReady.addActionListener(this);

		verifyReadyButton();

		this.setVisible(true);

	}

	private void initializeHeader() {

		contentPane.add(headerPanel, BorderLayout.NORTH);
		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
		Dimension dim = new Dimension(1000, 50);

		Box mainHeaderBox = Box.createHorizontalBox();
		Box items = Box.createVerticalBox();
		Box itemsPadding = Box.createHorizontalBox();
		Box itemTableName = Box.createHorizontalBox();
		Box itemFromRow = Box.createHorizontalBox();
		Box itemSeparator = Box.createHorizontalBox();
		Box itemStringDelimiter = Box.createHorizontalBox();

		headerPanel.add(mainHeaderBox);
		mainHeaderBox.add(itemsPadding);
		mainHeaderBox.add(items);
		items.add(Box.createVerticalStrut(5));
		items.add(itemTableName);
		items.add(itemFromRow);
		items.add(itemSeparator);
		items.add(itemStringDelimiter);
		items.add(Box.createVerticalStrut(5));

		itemsPadding.add(Box.createHorizontalStrut(10));

		itemTableName.add(new JLabel(ConstantController.getString("csvRecognizer.name")+": "));
		itemTableName.add(txtFieldTableName);
		txtFieldTableName.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				verifyReadyButton();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				verifyReadyButton();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				verifyReadyButton();
			}
		});
		txtFieldTableName.setMaximumSize(new Dimension(3000, 50));
		String fileName = String.valueOf(path.getFileName());
		fileName = fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf(".")) : fileName;
		txtFieldTableName.setText(fileName);
		itemTableName.add(Box.createHorizontalGlue());

		itemFromRow.add(new JLabel(ConstantController.getString("csvRecognizer.beginRow")+": "));
		SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, jTable.getRowCount()-2, 1);
		spinnerFromRow.setModel(spinnerModel);
		spinnerFromRow.setValue(1);
		spinnerFromRow.setMaximumSize(dim);
		spinnerFromRow.addChangeListener(e -> updateTable());
		itemFromRow.add(spinnerFromRow);
		itemFromRow.add(Box.createHorizontalGlue());

		itemSeparator.add(new JLabel(ConstantController.getString("csvRecognizer.columnSeparator")+": "));
		radioComma.setSelected(true);
		separatorGroup.add(radioComma);
		separatorGroup.add(radioSemiColon);
		separatorGroup.add(radioSpace);
		separatorGroup.add(radioOther);
		separatorGroup.getElements().asIterator().forEachRemaining(x -> x.addActionListener(this));
		txtFieldOtherSeparator.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				if (radioOther.isSelected())
					updateTable();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				if (radioOther.isSelected())
					updateTable();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				if (radioOther.isSelected())
					updateTable();
			}
		});
		txtFieldOtherSeparator.setMaximumSize(dim);
		itemSeparator.add(Box.createHorizontalStrut(3));
		itemSeparator.add(radioComma);
		itemSeparator.add(Box.createHorizontalStrut(3));
		itemSeparator.add(radioSemiColon);
		itemSeparator.add(Box.createHorizontalStrut(3));
		itemSeparator.add(radioSpace);
		itemSeparator.add(Box.createHorizontalStrut(3));
		itemSeparator.add(radioOther);
		itemSeparator.add(txtFieldOtherSeparator);
		itemSeparator.add(Box.createHorizontalGlue());

		itemStringDelimiter.add(new JLabel(ConstantController.getString("csvRecognizer.stringDelimiter")+": "));
		txtFieldStringDelimiter.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				updateTable();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				updateTable();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				updateTable();
			}
		});
		txtFieldStringDelimiter.setMaximumSize(dim);
		txtFieldStringDelimiter.setText(String.valueOf(defaultStringDelimiter));
		itemStringDelimiter.add(txtFieldStringDelimiter);
		itemStringDelimiter.add(Box.createHorizontalGlue());

	}

	private void initializeMain() {

		contentPane.add(mainPanel, BorderLayout.CENTER);
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(scrollPane);

		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.getViewport().setBackground(Color.WHITE);
		scrollPane.getViewport().setPreferredSize(scrollPane.getPreferredSize());

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		verifyReadyButton();

		separatorGroup.getElements().asIterator().forEachRemaining(x -> {
			if (e.getSource() == x)
				updateTable();
		});

		if (e.getSource() == btnCancel) {

			dispose();
			exitReference.set(true);

		}

		if (e.getSource() == btnReady) {

			dispose();

			setItems();

		}

	}

	private void verifyReadyButton() {

		boolean tableNameAlreadyExist = MainController.getTables().containsKey(txtFieldTableName.getText().strip());

		btnReady.setEnabled(!tableNameAlreadyExist);

	}

	private void setItems() {

		tableName.append(txtFieldTableName.getText().strip());

		for (String columnName : columnsName) {

			ColumnDataType type;

			if (typeComboBoxes.get(columnName).getSelectedItem() != null)
				type = Stream.of(ColumnDataType.values())
						.filter(x -> x.toString().equals(Objects.requireNonNull(typeComboBoxes.get(columnName).getSelectedItem()).toString()))
						.findFirst().orElseThrow();

			else
				type = ColumnDataType.NONE;

			columns.add(new Column(columnName, txtFieldTableName.getText().strip(), type));

		}

		for (int i = 1; i < jTable.getRowCount(); i++) {

			Map<String, String> column = new HashMap<>();
			for (int j = 1; j < jTable.getColumnCount(); j++)
				column.put(jTable.getColumnName(j), jTable.getValueAt(i, j).toString());

			content.put(i, column);

		}

	}

	private void updateTable() {

		if (radioComma.isSelected())
			separator = ',';
		else if (radioSemiColon.isSelected())
			separator = ';';
		else if (radioSpace.isSelected())
			separator = ' ';
		else if (radioOther.isSelected())
			separator = txtFieldOtherSeparator.getText().isEmpty() ? ' ' : txtFieldOtherSeparator.getText().charAt(0);

		beginIndex = (int) spinnerFromRow.getValue();

		stringDelimiter = txtFieldStringDelimiter.getText().isEmpty() ? '\0'
				: txtFieldStringDelimiter.getText().charAt(0);

		try {

			csvData = CsvRecognizer.importCsv(path, separator, stringDelimiter, beginIndex);

		} catch (InvalidCsvException e) {

			new ErrorFrame(e.getMessage());
			csvData = new CsvData(List.of(), List.of(), new Vector<>(), new Vector<>());

		}

		loadJTable();
		verifyReadyButton();
		revalidate();

	}

	private void loadJTable() {

		typeComboBoxes.clear();
		columnsName.clear();

		columnsName.addAll(csvData.columnsNameList());

		model = new CustomTableModel(csvData.dataArray(), csvData.columnsNameArray());
		model.insertRow(0, new Object[] {});

		List<JComboBox<?>> comboBoxes = new ArrayList<>();

		for (int i = 0; i < columnsName.size(); i++) {

			List<String> columnData = new ArrayList<>();
			for (int j = 0; j < csvData.dataArray().size() - 2; j++)
				columnData.add(csvData.dataList().get(j).get(i));

			String[] types = TableUtils.getPossiblesDataType(columnData, stringDelimiter).stream()
					.map(ColumnDataType::toString).toArray(String[]::new);

			JComboBox<String> comboBox = new JComboBox<>(types);
			comboBox.addActionListener(this);

			comboBoxes.add(comboBox);
			typeComboBoxes.put(columnsName.get(i), comboBox);

		}

		addFirstColumn();

		jTable = new JTable(model) {

			@Override
			public TableCellRenderer getCellRenderer(int row, int column) {

				if (row == 0 && column != 0)
					return new DefaultTableCellRenderer() {

						@Override
						public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
								boolean hasFocus, int row, int column) {

							return comboBoxes.get(column - 1);
						}
					};

				return super.getCellRenderer(row, column);
			}

			@Override
			public TableCellEditor getCellEditor(int row, int column) {

				if (row == 0 && column != 0)
					return new DefaultCellEditor(comboBoxes.get(column - 1));

				return super.getCellEditor(row, column);
			}
		};

		jTable.getColumnModel().moveColumn(model.getColumnCount() - 1, 0);
		jTable.getColumnModel().getColumn(0).setResizable(false);
		JTableUtils.setColumnBold(jTable, 0);
		JTableUtils.preferredColumnWidthByValues(jTable, 0);
		jTable.setShowHorizontalLines(true);
		jTable.setGridColor(Color.blue);
		jTable.setColumnSelectionAllowed(false);
		jTable.setRowSelectionAllowed(false);
		jTable.setCellSelectionEnabled(false);
		JTableUtils.setNullInRed(jTable);
		scrollPane.setViewportView(jTable);
		((CustomTableModel) model).setRowEnabled(0, true);

	}

	private void addFirstColumn() {

		model.addColumn(ConstantController.getString("csvRecognizer.firstColumn.name")+":");

		model.setValueAt(ConstantController.getString("csvRecognizer.firstColumn.type")+":", 0, model.getColumnCount() - 1);

		for (int row = 1; row < model.getRowCount(); row++) {
			model.setValueAt(row , row, model.getColumnCount() - 1);
		}

	}

	public CsvInfo getCsvInfo(){

		return new CsvInfo(separator, stringDelimiter, beginIndex, path);

	}

}
