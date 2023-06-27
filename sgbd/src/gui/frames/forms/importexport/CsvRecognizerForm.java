package gui.frames.forms.importexport;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import controller.MainController;
import database.TableUtils;
import entities.Column;
import enums.ColumnDataType;
import exceptions.InvalidCsvException;
import files.csv.Recognizer;
import files.csv.Recognizer.CsvData;
import gui.frames.ErrorFrame;
import gui.utils.JTableUtils;
import gui.utils.JTableUtils.CustomTableModel;

public class CsvRecognizerForm extends JDialog implements ActionListener {

	private final JPanel contentPane = new JPanel();
	private final JPanel headerPanel = new JPanel();
	private final JPanel bottomPanel = new JPanel();
	private final JPanel mainPanel = new JPanel();
	private final JScrollPane scrollPane = new JScrollPane();
	private JTable jTable;
	private DefaultTableModel model;
	private final JButton btnCancel = new JButton("Cancelar");
	private final JButton btnDone = new JButton("Pronto");
	private final JTextField txtFieldStringDelimiter = new JTextField();
	private final JTextField txtFieldOtherSeparator = new JTextField();
	private final JTextField txtFieldTableName = new JTextField();
	private final JSpinner spinnerFromRow = new JSpinner();
	private final ButtonGroup separatorGroup = new ButtonGroup();
	private final JRadioButton radioComma = new JRadioButton("Vírgula");
	private final JRadioButton radioSemiColon = new JRadioButton("Ponto e vírgula");
	private final JRadioButton radioSpace = new JRadioButton("Espaço");
	private final JRadioButton radioOther = new JRadioButton("Outro: ");

	private final Map<String, JCheckBox> pkCheckBoxes = new HashMap<>();
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

	private char separator = defaultSeparator;
	private char stringDelimiter = defaultStringDelimiter;

	public CsvRecognizerForm(String path, StringBuilder tableName, List<Column> columns,
							 Map<Integer, Map<String, String>> content, AtomicReference<Boolean> exitReference) {

		super((Window) null, "Tabela csv");
		setModal(true);

		this.exitReference = exitReference;
		this.path = Path.of(path);
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

		setBounds(0, 0, MainController.WIDTH, MainController.HEIGHT);
		setLocationRelativeTo(null);
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		try {

			csvData = Recognizer.importCsv(path, defaultSeparator, defaultStringDelimiter, 1);

		} catch (InvalidCsvException e) {

			new ErrorFrame(e.getMessage());
			csvData = new CsvData(List.of(), List.of(), new Vector<>(), new Vector<>());

		}

		loadJTable();
		initializeHeader();
		initializeMain();
		initializeBottom();

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

		itemTableName.add(new JLabel("Nome: "));
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

		itemFromRow.add(new JLabel("Começa na linha: "));
		SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, jTable.getRowCount()-2, 1);
		spinnerFromRow.setModel(spinnerModel);
		spinnerFromRow.setValue(1);
		spinnerFromRow.setMaximumSize(dim);
		spinnerFromRow.addChangeListener(e -> updateTable());
		itemFromRow.add(spinnerFromRow);
		itemFromRow.add(Box.createHorizontalGlue());

		itemSeparator.add(new JLabel("Separador de coluna: "));
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

		itemStringDelimiter.add(new JLabel("Delimitador de String: "));
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

	private void initializeBottom() {
		contentPane.add(bottomPanel, BorderLayout.SOUTH);
		bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

		bottomPanel.add(btnCancel);
		bottomPanel.add(btnDone);

		btnCancel.addActionListener(this);
		btnDone.addActionListener(this);

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

		if (e.getSource() == btnDone) {

			dispose();

			setItems();

		}

	}

	private void verifyReadyButton() {

		boolean tableNameAlreadyExist = MainController.getTables().containsKey(txtFieldTableName.getText().strip());
		boolean hasPK = pkCheckBoxes.values().stream().anyMatch(JCheckBox::isSelected);

		List<List<String>> columnsSelected = new ArrayList<>();
		for(Map.Entry<String, JCheckBox> checkBox : pkCheckBoxes.entrySet())
			if(checkBox.getValue().isSelected()) {

				List<String> columnData = new ArrayList<>();
				int index = csvData.columnsNameList().indexOf(checkBox.getKey());

				for (List<String> column : csvData.dataList())
					columnData.add(column.get(index));

				columnsSelected.add(columnData);

			}

		boolean canBePK = TableUtils.canBePrimaryKey(columnsSelected);

		btnDone.setEnabled(!tableNameAlreadyExist && hasPK && canBePK);

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

			columns.add(new Column(columnName, txtFieldTableName.getText().strip(), type,
					pkCheckBoxes.get(columnName).isSelected()));

		}

		for (int i = 2; i < jTable.getRowCount(); i++) {

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

		int beginIndex = (int) spinnerFromRow.getValue();

		stringDelimiter = txtFieldStringDelimiter.getText().isEmpty() ? '\0'
				: txtFieldStringDelimiter.getText().charAt(0);

		try {

			csvData = Recognizer.importCsv(path, separator, stringDelimiter, beginIndex);

		} catch (InvalidCsvException e) {

			new ErrorFrame(e.getMessage());
			csvData = new CsvData(List.of(), List.of(), new Vector<>(), new Vector<>());

		}

		loadJTable();
		verifyReadyButton();
		revalidate();

	}

	private void loadJTable() {

		pkCheckBoxes.clear();
		typeComboBoxes.clear();
		columnsName.clear();

		columnsName.addAll(csvData.columnsNameList());

		model = new CustomTableModel(csvData.dataArray(), csvData.columnsNameArray());
		model.insertRow(0, new Object[] {});
		model.insertRow(0, new Object[] {});

		List<JComboBox<?>> comboboxes = new ArrayList<>();
		List<JCheckBox> checkboxes = new ArrayList<>();

		for (int i = 0; i < columnsName.size(); i++) {

			List<String> columnData = new ArrayList<>();
			for (int j = 0; j < csvData.dataArray().size() - 2; j++)
				columnData.add(csvData.dataList().get(j).get(i));

			String[] types = TableUtils.getPossiblesDataType(columnData, stringDelimiter).stream()
					.map(ColumnDataType::toString).toArray(String[]::new);

			JComboBox<String> comboBox = new JComboBox<>(types);
			JCheckBox checkBox = new JCheckBox();
			comboBox.addActionListener(this);
			checkBox.addActionListener(this);

			comboboxes.add(comboBox);
			checkboxes.add(checkBox);
			pkCheckBoxes.put(columnsName.get(i), checkBox);
			typeComboBoxes.put(columnsName.get(i), comboBox);

		}

		addFirstColumn();

		jTable = new JTable(model) {

			@Override
			public TableCellRenderer getCellRenderer(int row, int column) {

				if (row == 0 && column != 0) {
					return new DefaultTableCellRenderer() {

						@Override
						public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
								boolean hasFocus, int row, int column) {

							return checkboxes.get(column - 1);
						}
					};
				}

				if (row == 1 && column != 0) {
					return new DefaultTableCellRenderer() {

						@Override
						public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
								boolean hasFocus, int row, int column) {

							return comboboxes.get(column - 1);
						}
					};
				}

				return super.getCellRenderer(row, column);
			}

			@Override
			public TableCellEditor getCellEditor(int row, int column) {

				if (row == 0 && column != 0)
					return new DefaultCellEditor(checkboxes.get(column - 1));

				if (row == 1 && column != 0)
					return new DefaultCellEditor(comboboxes.get(column - 1));
				else
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
		((CustomTableModel) model).setRowEnabled(1, true);

	}

	private void addFirstColumn() {

		model.addColumn("Nome:");

		model.setValueAt("Chave Primária:", 0, model.getColumnCount() - 1);
		model.setValueAt("Tipo:", 1, model.getColumnCount() - 1);

		for (int row = 2; row < model.getRowCount(); row++) {
			model.setValueAt(row - 1, row, model.getColumnCount() - 1);
		}

	}

}
