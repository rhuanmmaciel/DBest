package gui.frames.forms.importexport;

import controllers.ConstantController;
import controllers.MainController;
import database.TableUtils;
import entities.Column;
import enums.ColumnDataType;
import exceptions.InvalidCSVException;
import files.csv.CSVInfo;
import files.csv.CSVRecognizer;
import files.csv.CSVRecognizer.CSVData;
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

public class CSVRecognizerForm extends FormBase implements ActionListener {

    private final JPanel headerPanel = new JPanel();

    private final JPanel mainPanel = new JPanel();

    private final JScrollPane scrollPane = new JScrollPane();

    private JTable jTable;

    private DefaultTableModel model;

    private final JTextField stringDelimiterTextField = new JTextField();

    private final JTextField otherSeparatorTextField = new JTextField();

    private final JTextField tableNameTextField = new JTextField();

    private final JSpinner fromRowSpinner = new JSpinner();

    private final ButtonGroup separatorGroup = new ButtonGroup();

    private final JRadioButton commaRadioButton = new JRadioButton(ConstantController.getString("csvRecognizer.comma"));

    private final JRadioButton semicolonRadioButton = new JRadioButton(ConstantController.getString("csvRecognizer.semicolon"));

    private final JRadioButton spaceRadioButton = new JRadioButton(ConstantController.getString("csvRecognizer.space"));

    private final JRadioButton otherRadioButton = new JRadioButton(ConstantController.getString("csvRecognizer.other"));

    private final Map<String, JComboBox<?>> typeComboBoxes = new HashMap<>();

    private final List<String> columnNames = new ArrayList<>();

    private CSVData csvData;

    private final Path path;

    private final AtomicReference<Boolean> exitReference;

    private final StringBuilder tableName;

    private final List<Column> columns;

    private final Map<Integer, Map<String, String>> content;

    private final char defaultSeparator = ',';

    private final char defaultStringDelimiter = '"';

    private final int defaultBeginIndex = 1;

    private char separator = this.defaultSeparator;

    private char stringDelimiter = this.defaultStringDelimiter;

    private int beginIndex = this.defaultBeginIndex;

    public CSVRecognizerForm(
        Path path, StringBuilder tableName, List<Column> columns,
        Map<Integer, Map<String, String>> content, AtomicReference<Boolean> exitReference
    ) {
        super(null);

        this.setModal(true);

        this.exitReference = exitReference;
        this.path = path;
        this.columns = columns;
        this.content = content;
        this.tableName = tableName;

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent event) {
                exitReference.set(true);
            }
        });

        this.initGUI();
    }

    public void initGUI() {
        this.setBounds(0, 0, ConstantController.UI_SCREEN_WIDTH, ConstantController.UI_SCREEN_HEIGHT);
        this.setLocationRelativeTo(null);

        try {
            this.csvData = CSVRecognizer.importCSV(this.path, this.defaultSeparator, this.defaultStringDelimiter, 1);
        } catch (InvalidCSVException exception) {
            new ErrorFrame(exception.getMessage());
            this.csvData = new CSVData(List.of(), List.of(), new Vector<>(), new Vector<>());
        }

        this.loadJTable();
        this.initializeHeader();
        this.initializeMain();
        this.btnReady.addActionListener(this);
        this.btnCancel.addActionListener(this);
        this.verifyReadyButton();
        this.setVisible(true);
    }

    private void initializeHeader() {
        this.contentPanel.add(this.headerPanel, BorderLayout.NORTH);
        this.headerPanel.setLayout(new BoxLayout(this.headerPanel, BoxLayout.Y_AXIS));

        Dimension dimension = new Dimension(1000, 50);

        Box mainHeaderBox = Box.createHorizontalBox();
        Box items = Box.createVerticalBox();
        Box itemsPadding = Box.createHorizontalBox();
        Box itemTableName = Box.createHorizontalBox();
        Box itemFromRow = Box.createHorizontalBox();
        Box itemSeparator = Box.createHorizontalBox();
        Box itemStringDelimiter = Box.createHorizontalBox();

        this.headerPanel.add(mainHeaderBox);

        mainHeaderBox.add(itemsPadding);
        mainHeaderBox.add(items);

        items.add(Box.createVerticalStrut(5));
        items.add(itemTableName);
        items.add(itemFromRow);
        items.add(itemSeparator);
        items.add(itemStringDelimiter);
        items.add(Box.createVerticalStrut(5));

        itemsPadding.add(Box.createHorizontalStrut(10));

        itemTableName.add(new JLabel(String.format("%s:", ConstantController.getString("csvRecognizer.name"))));
        itemTableName.add(this.tableNameTextField);

        this.tableNameTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent event) {
                CSVRecognizerForm.this.verifyReadyButton();
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
                CSVRecognizerForm.this.verifyReadyButton();
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
                CSVRecognizerForm.this.verifyReadyButton();
            }
        });

        this.tableNameTextField.setMaximumSize(new Dimension(3000, 50));

        String fileName = String.valueOf(this.path.getFileName());
        fileName = fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf(".")) : fileName;

        this.tableNameTextField.setText(fileName);

        itemTableName.add(Box.createHorizontalGlue());
        itemFromRow.add(new JLabel(String.format("%s:", ConstantController.getString("csvRecognizer.beginRow"))));

        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, this.jTable.getRowCount() - 2, 1);

        this.fromRowSpinner.setModel(spinnerModel);
        this.fromRowSpinner.setValue(1);
        this.fromRowSpinner.setMaximumSize(dimension);
        this.fromRowSpinner.addChangeListener(e -> this.updateTable());

        itemFromRow.add(this.fromRowSpinner);
        itemFromRow.add(Box.createHorizontalGlue());

        itemSeparator.add(new JLabel(String.format("%s:", ConstantController.getString("csvRecognizer.columnSeparator"))));

        this.commaRadioButton.setSelected(true);
        this.separatorGroup.add(this.commaRadioButton);
        this.separatorGroup.add(this.semicolonRadioButton);
        this.separatorGroup.add(this.spaceRadioButton);
        this.separatorGroup.add(this.otherRadioButton);
        this.separatorGroup.getElements().asIterator().forEachRemaining(x -> x.addActionListener(this));

        this.otherSeparatorTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent event) {
				if (CSVRecognizerForm.this.otherRadioButton.isSelected()) {
                    CSVRecognizerForm.this.updateTable();
				}
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
				if (CSVRecognizerForm.this.otherRadioButton.isSelected()) {
                    CSVRecognizerForm.this.updateTable();
				}
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
				if (CSVRecognizerForm.this.otherRadioButton.isSelected()) {
                    CSVRecognizerForm.this.updateTable();
				}
            }
        });

        this.otherSeparatorTextField.setMaximumSize(dimension);

        itemSeparator.add(Box.createHorizontalStrut(3));
        itemSeparator.add(this.commaRadioButton);
        itemSeparator.add(Box.createHorizontalStrut(3));
        itemSeparator.add(this.semicolonRadioButton);
        itemSeparator.add(Box.createHorizontalStrut(3));
        itemSeparator.add(this.spaceRadioButton);
        itemSeparator.add(Box.createHorizontalStrut(3));
        itemSeparator.add(this.otherRadioButton);
        itemSeparator.add(this.otherSeparatorTextField);
        itemSeparator.add(Box.createHorizontalGlue());

        itemStringDelimiter.add(new JLabel(String.format("%s:", ConstantController.getString("csvRecognizer.stringDelimiter"))));

        this.stringDelimiterTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent event) {
                CSVRecognizerForm.this.updateTable();
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
                CSVRecognizerForm.this.updateTable();
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
                CSVRecognizerForm.this.updateTable();
            }
        });

        this.stringDelimiterTextField.setMaximumSize(dimension);
        this.stringDelimiterTextField.setText(String.valueOf(this.defaultStringDelimiter));

        itemStringDelimiter.add(this.stringDelimiterTextField);
        itemStringDelimiter.add(Box.createHorizontalGlue());
    }

    private void initializeMain() {
        this.contentPanel.add(this.mainPanel, BorderLayout.CENTER);

        this.mainPanel.setLayout(new BorderLayout());
        this.mainPanel.add(this.scrollPane);

        this.scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        this.scrollPane.getViewport().setBackground(Color.WHITE);
        this.scrollPane.getViewport().setPreferredSize(this.scrollPane.getPreferredSize());
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        this.verifyReadyButton();

        this.separatorGroup.getElements().asIterator().forEachRemaining(element -> {
			if (event.getSource() == element) {
                this.updateTable();
			}
        });

        if (event.getSource() == this.btnCancel) {
            this.dispose();
            this.exitReference.set(true);
        }

        if (event.getSource() == this.btnReady) {
            this.dispose();
            this.setItems();
        }
    }

    private void verifyReadyButton() {
        boolean tableNameAlreadyExists = MainController.getTables().containsKey(this.tableNameTextField.getText().strip());

        this.btnReady.setEnabled(!tableNameAlreadyExists);
    }

    private void setItems() {
        this.tableName.append(this.tableNameTextField.getText().strip());

        for (String columnName : this.columnNames) {
            ColumnDataType type;

			if (this.typeComboBoxes.get(columnName).getSelectedItem() != null) {
				type = Stream
                    .of(ColumnDataType.values())
					.filter(x -> x.toString().equals(Objects.requireNonNull(this.typeComboBoxes.get(columnName).getSelectedItem()).toString()))
					.findFirst().orElseThrow();
			} else {
				type = ColumnDataType.NONE;
			}

            this.columns.add(new Column(columnName, this.tableNameTextField.getText().strip(), type));
        }

        for (int i = 1; i < this.jTable.getRowCount(); i++) {
            Map<String, String> column = new HashMap<>();

			for (int j = 1; j < this.jTable.getColumnCount(); j++) {
				column.put(this.jTable.getColumnName(j), this.jTable.getValueAt(i, j).toString());
			}

            this.content.put(i, column);
        }
    }

    private void updateTable() {
		if (this.commaRadioButton.isSelected()) {
            this.separator = ',';
		} else if (this.semicolonRadioButton.isSelected()) {
            this.separator = ';';
		} else if (this.spaceRadioButton.isSelected()) {
            this.separator = ' ';
		} else if (this.otherRadioButton.isSelected()) {
            this.separator = this.otherSeparatorTextField.getText().isEmpty() ? ' ' : this.otherSeparatorTextField.getText().charAt(0);
		}

        this.beginIndex = (int) this.fromRowSpinner.getValue();

        this.stringDelimiter = this.stringDelimiterTextField.getText().isEmpty() ? '\0'
            : this.stringDelimiterTextField.getText().charAt(0);

        try {
            this.csvData = CSVRecognizer.importCSV(this.path, this.separator, this.stringDelimiter, this.beginIndex);
        } catch (InvalidCSVException exception) {
            new ErrorFrame(exception.getMessage());

            this.csvData = new CSVData(List.of(), List.of(), new Vector<>(), new Vector<>());
        }

        this.loadJTable();
        this.verifyReadyButton();
        this.revalidate();
    }

    private void loadJTable() {
        this.typeComboBoxes.clear();
        this.columnNames.clear();

        this.columnNames.addAll(this.csvData.columnNamesList());

        this.model = new CustomTableModel(this.csvData.dataArray(), this.csvData.columnNamesArray());
        this.model.insertRow(0, new Object[]{});

        List<JComboBox<?>> comboBoxes = new ArrayList<>();

        for (int i = 0; i < this.columnNames.size(); i++) {
            List<String> columnData = new ArrayList<>();

			for (int j = 0; j < this.csvData.dataArray().size() - 2; j++) {
				columnData.add(this.csvData.dataList().get(j).get(i));
			}

            String[] types = TableUtils
                .getPossiblesDataType(columnData, this.stringDelimiter).stream()
                .map(ColumnDataType::toString).toArray(String[]::new);

            JComboBox<String> comboBox = new JComboBox<>(types);

            comboBox.addActionListener(this);
            comboBoxes.add(comboBox);

            this.typeComboBoxes.put(this.columnNames.get(i), comboBox);
        }

        this.addFirstColumn();

        this.jTable = new JTable(this.model) {

            @Override
            public TableCellRenderer getCellRenderer(int row, int column) {
				if (row == 0 && column != 0) {
					return new DefaultTableCellRenderer() {

						@Override
						public Component getTableCellRendererComponent(
                            JTable table, Object value, boolean isSelected,
                            boolean hasFocus, int row, int column
                        ) {
							return comboBoxes.get(column - 1);
						}
					};
				}

                return super.getCellRenderer(row, column);
            }

            @Override
            public TableCellEditor getCellEditor(int row, int column) {
				if (row == 0 && column != 0) {
					return new DefaultCellEditor(comboBoxes.get(column - 1));
				}

                return super.getCellEditor(row, column);
            }
        };

        this.jTable.getColumnModel().moveColumn(this.model.getColumnCount() - 1, 0);
        this.jTable.getColumnModel().getColumn(0).setResizable(false);

        JTableUtils.setColumnBold(this.jTable, 0);
        JTableUtils.preferredColumnWidthByValues(this.jTable, 0);

        this.jTable.setShowHorizontalLines(true);
        this.jTable.setGridColor(Color.blue);
        this.jTable.setColumnSelectionAllowed(false);
        this.jTable.setRowSelectionAllowed(false);
        this.jTable.setCellSelectionEnabled(false);

        JTableUtils.setNullInRed(this.jTable);

        this.scrollPane.setViewportView(this.jTable);

        ((CustomTableModel) this.model).setRowEnabled(0, true);
    }

    private void addFirstColumn() {
        String firstColumnName = String.format("%s:", ConstantController.getString("csvRecognizer.firstColumn.name"));
        String firstColumnType = String.format("%s:", ConstantController.getString("csvRecognizer.firstColumn.type"));

        this.model.addColumn(firstColumnName);
        this.model.setValueAt(firstColumnType, 0, this.model.getColumnCount() - 1);

        for (int row = 1; row < this.model.getRowCount(); row++) {
            this.model.setValueAt(row, row, this.model.getColumnCount() - 1);
        }
    }

    public CSVInfo getCSVInfo() {
        return new CSVInfo(this.separator, this.stringDelimiter, this.beginIndex, this.path);
    }
}
