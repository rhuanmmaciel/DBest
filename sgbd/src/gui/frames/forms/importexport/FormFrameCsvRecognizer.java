package gui.frames.forms.importexport;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import controller.MainController;
import entities.Column;
import enums.ColumnDataType;
import exceptions.InvalidCsvException;
import files.csv.Recognizer;
import files.csv.Recognizer.CsvData;
import util.TableUtils;

@SuppressWarnings("serial")
public class FormFrameCsvRecognizer extends JDialog implements ActionListener {

	private final JPanel contentPane = new JPanel();
	private final JPanel headerPanel = new JPanel();
	private final JPanel bottomPanel = new JPanel();
	private final JPanel mainPanel = new JPanel();
	private JPanel columnsPanel = new JPanel(new GridBagLayout());
	private GridBagConstraints gbc = new GridBagConstraints();
	private final JScrollPane scrollPane = new JScrollPane();
	private JTable jTable;
	private JButton cancelButton = new JButton("Cancelar");
	private JButton doneButton = new JButton("Pronto");
	private JTextField txtFieldStringDelimiter = new JTextField();
	private JTextField otherSeparator = new JTextField();
	private JSpinner spinnerFromRow = new JSpinner();
	private ButtonGroup separatorGroup = new ButtonGroup();
	private JRadioButton radioComma = new JRadioButton("Vírgula");
	private JRadioButton radioSemiColon = new JRadioButton("Ponto e vírgula");
	private JRadioButton radioSpace = new JRadioButton("Espaço");
	private JRadioButton radioOther = new JRadioButton("Outro: ");

	private final Map<String, JCheckBox> pkCheckBoxes = new HashMap<>();
	private final Map<String, JComboBox<?>> typeComboBoxes = new HashMap<>();
	private final Map<String, JLabel> namesLabel = new HashMap<>();
	private final List<String> columnsName = new ArrayList<>();

	private CsvData csvData;

	private final String path;
	private AtomicReference<Boolean> exitReference;
	private StringBuilder pkName;
	private StringBuilder tableName;
	private List<Column> columns;
	private Map<Integer, Map<String, String>> content;

	private final int defaultBeginIndex = 1;
	private final char defaultSeparator = ',';
	private final char defaultStringDelimiter = '"';

	private int beginIndex = defaultBeginIndex;
	private char separator = defaultSeparator;
	private char stringDelimiter = defaultStringDelimiter;

	public FormFrameCsvRecognizer(String path, StringBuilder tableName, StringBuilder pkName, List<Column> columns,
			Map<Integer, Map<String, String>> content, AtomicReference<Boolean> exitReference) {

		super((Window) null, "Tabela csv");
		setModal(true);

		this.exitReference = exitReference;
		this.path = path;
		this.pkName = pkName;
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
			csvData = Recognizer.importCsv(path, defaultSeparator, defaultStringDelimiter, defaultBeginIndex);
		} catch (InvalidCsvException e) {
			e.printStackTrace();
		}

		loadJTable();
		initializeHeader();
		initializeMain();
		initializeBottom();

		this.setVisible(true);

	}

	private void initializeHeader() {

		contentPane.add(headerPanel, BorderLayout.NORTH);
		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
		Dimension dim = new Dimension(1000, 50);

		JPanel columnsWrapperPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JScrollPane columnsScrollPane = new JScrollPane(columnsWrapperPanel);
		columnsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		Box mainHeaderBox = Box.createHorizontalBox();
		Box items = Box.createVerticalBox();
		Box itemsPadding = Box.createHorizontalBox();
		Box itemFromRow = Box.createHorizontalBox();
		Box itemSeparator = Box.createHorizontalBox();
		Box itemStringDelimiter = Box.createHorizontalBox();

		headerPanel.add(mainHeaderBox);
		mainHeaderBox.add(itemsPadding);
		mainHeaderBox.add(items);
		items.add(Box.createVerticalStrut(5));
		items.add(itemFromRow);
		items.add(itemSeparator);
		items.add(itemStringDelimiter);
		items.add(columnsScrollPane);
		items.add(Box.createVerticalStrut(5));

		itemsPadding.add(Box.createHorizontalStrut(10));

		itemFromRow.add(new JLabel("Começa na linha: "));
		SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, jTable.getRowCount(), 1);
		spinnerFromRow.setModel(spinnerModel);
		spinnerFromRow.setValue(1);
		spinnerFromRow.setMaximumSize(dim);
		spinnerFromRow.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				updateTable();
			}
		});
		itemFromRow.add(spinnerFromRow);
		itemFromRow.add(Box.createHorizontalGlue());

		itemSeparator.add(new JLabel("Separador de coluna: "));
		radioComma.setSelected(true);
		separatorGroup.add(radioComma);
		separatorGroup.add(radioSemiColon);
		separatorGroup.add(radioSpace);
		separatorGroup.add(radioOther);
		separatorGroup.getElements().asIterator().forEachRemaining(x -> x.addActionListener(this));
		otherSeparator.getDocument().addDocumentListener(new DocumentListener() {
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
		otherSeparator.setMaximumSize(dim);
		itemSeparator.add(Box.createHorizontalStrut(3));
		itemSeparator.add(radioComma);
		itemSeparator.add(Box.createHorizontalStrut(3));
		itemSeparator.add(radioSemiColon);
		itemSeparator.add(Box.createHorizontalStrut(3));
		itemSeparator.add(radioSpace);
		itemSeparator.add(Box.createHorizontalStrut(3));
		itemSeparator.add(radioOther);
		itemSeparator.add(otherSeparator);
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
		itemStringDelimiter.add(txtFieldStringDelimiter);
		itemStringDelimiter.add(Box.createHorizontalGlue());

		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(5, 10, 5, 10);

		JLabel columnNameLabel = new JLabel("Nome");
		gbc.gridx = 0;
		gbc.gridy = 0;
		columnsPanel.add(columnNameLabel, gbc);

		JLabel primaryKeyLabel = new JLabel("Chave Primária");
		gbc.gridy = 1;
		columnsPanel.add(primaryKeyLabel, gbc);

		JLabel typeLabel = new JLabel("Tipo");
		gbc.gridy = 2;
		columnsPanel.add(typeLabel, gbc);

		loadColumnsInfo();

		columnsWrapperPanel.add(columnsPanel);

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

		bottomPanel.add(cancelButton);
		bottomPanel.add(doneButton);

		cancelButton.addActionListener(this);
		doneButton.addActionListener(this);

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		separatorGroup.getElements().asIterator().forEachRemaining(x -> {
			if (e.getSource() == x)
				updateTable();
		});

		if (e.getSource() == cancelButton) {

			dispose();
			exitReference.set(true);

		}

		if (e.getSource() == doneButton) {

			dispose();

			tableName.append(path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf(".")));

			for (String columnName : columnsName)
				if (pkCheckBoxes.get(columnName).isSelected()) {
					pkName.append(columnName);
					break;
				}

			for (String columnName : columnsName) {

				ColumnDataType type = List.of(ColumnDataType.values()).stream()
						.filter(x -> x.toString().equals(typeComboBoxes.get(columnName).getSelectedItem().toString()))
						.findFirst().orElseThrow();

				switch (type) {
				case NONE -> columns.add(new Column(columnName, ColumnDataType.NONE));

				case INTEGER -> columns.add(new Column(columnName, ColumnDataType.INTEGER));

				case FLOAT -> columns.add(new Column(columnName, ColumnDataType.FLOAT));

				case STRING -> columns.add(new Column(columnName, ColumnDataType.STRING));

				case CHARACTER -> columns.add(new Column(columnName, ColumnDataType.CHARACTER));

				default -> columns.add(new Column(columnName, ColumnDataType.BOOLEAN));

				}

			}

			for (int i = 0; i < jTable.getRowCount(); i++) {

				Map<String, String> column = new HashMap<>();
				for (int j = 0; j < jTable.getColumnCount(); j++)
					column.put(jTable.getColumnName(j), jTable.getValueAt(i, j).toString());

				content.put(i, column);

			}

		}

	}

	private void updateTable() {

		if (radioComma.isSelected())
			separator = ',';
		if (radioSemiColon.isSelected())
			separator = ';';
		if (radioSpace.isSelected())
			separator = ' ';
		if (radioOther.isSelected())
			separator = otherSeparator.getText().isEmpty() ? ' ' : otherSeparator.getText().charAt(0);

		beginIndex = (int) spinnerFromRow.getValue();

		stringDelimiter = txtFieldStringDelimiter.getText().isEmpty() ? ' '
				: txtFieldStringDelimiter.getText().charAt(0);

		try {
			csvData = Recognizer.importCsv(path, separator, stringDelimiter, beginIndex);
		} catch (InvalidCsvException e) {
			e.printStackTrace();
		}
		loadJTable();
		loadColumnsInfo();
		revalidate();

	}

	private void loadColumnsInfo() {

		pkCheckBoxes.values().forEach(x -> columnsPanel.remove(x));
		typeComboBoxes.values().forEach(x -> columnsPanel.remove(x));
		namesLabel.values().forEach(x -> columnsPanel.remove(x));

		revalidate();

		pkCheckBoxes.clear();
		typeComboBoxes.clear();
		namesLabel.clear();
		columnsName.clear();

		String[] columns = csvData.columnsNameArray();
		for (int i = 1; i < columns.length + 1; i++) {
			String columnName = columns[i - 1];

			JLabel columnLabel = new JLabel(columnName);
			gbc.gridx = i;
			gbc.gridy = 0;
			columnsPanel.add(columnLabel, gbc);

			JCheckBox checkBox = new JCheckBox();
			gbc.gridy = 1;
			columnsPanel.add(checkBox, gbc);

			List<String> columnData = new ArrayList<>();
			for (int j = 0; j < jTable.getRowCount(); j++)
				columnData.add(jTable.getValueAt(j, i - 1).toString());

			String[] types = TableUtils.getPossiblesDataType(columnData, stringDelimiter).stream()
					.map(x -> x.toString()).toArray(String[]::new);

			JComboBox<String> comboBox = new JComboBox<>(types);
			gbc.gridy = 2;
			columnsPanel.add(comboBox, gbc);

			pkCheckBoxes.put(columnName, checkBox);
			typeComboBoxes.put(columnName, comboBox);
			namesLabel.put(columnName, columnLabel);
			columnsName.add(columnName);

			checkBox.setEnabled(TableUtils.canBePrimaryKey(columnData));

		}

	}

	private void loadJTable() {

		jTable = new JTable(csvData.dataArray(), csvData.columnsNameArray());

		jTable.setShowHorizontalLines(true);
		jTable.setGridColor(Color.blue);
		jTable.setColumnSelectionAllowed(false);
		jTable.setRowSelectionAllowed(false);

		scrollPane.setViewportView(jTable);

	}

}
