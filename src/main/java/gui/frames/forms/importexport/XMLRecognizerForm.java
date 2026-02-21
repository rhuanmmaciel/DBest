package gui.frames.forms.importexport;

import controllers.ConstantController;
import controllers.MainController;
import entities.Column;
import enums.ColumnDataType;
import exceptions.InvalidXMLException;
import files.xml.XMLInfo;
import gui.frames.ErrorFrame;
import gui.frames.forms.FormBase;
import sources.xml.XMLAnalysisResult;
import sources.xml.XMLRecognizer;
import database.TableCreator;
import entities.cells.XMLTableCell;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
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

/**
 * Form for configuring XML import settings and previewing data
 */
public class XMLRecognizerForm extends FormBase implements ActionListener {

    private final JPanel headerPanel = new JPanel();
    private final JPanel mainPanel = new JPanel();
    private final JScrollPane scrollPane = new JScrollPane();
    private JTable jTable;
    private DefaultTableModel model;

    private final JTextField tableNameTextField = new JTextField();
    private final JComboBox<String> rootElementComboBox = new JComboBox<>();
    private final JComboBox<String> recordElementComboBox = new JComboBox<>();
    private final JComboBox<XMLRecognizer.FlatteningStrategy> strategyComboBox = new JComboBox<>(XMLRecognizer.FlatteningStrategy.values());
    private final JCheckBox removeCommonPrefixCheckBox = new JCheckBox("Remove common prefixes from column names");
    private final JCheckBox autoCreateSeparateTablesCheckBox = new JCheckBox("Auto-create separate tables with references");
    private final JButton btnCreateSeparateTables = new JButton("Create Separate Tables");
    private JTextArea separateTablesInfo = new JTextArea(3, 50);

    private final Map<String, JComboBox<ColumnDataType>> typeComboBoxes = new HashMap<>();
    private final List<String> columnNames = new ArrayList<>();

    private XMLAnalysisResult analysisResult;
    private final Path path;
    private final AtomicReference<Boolean> exitReference;
    private final StringBuilder tableName;
    private final List<Column> columns;

    public XMLRecognizerForm(Path path, AtomicReference<Boolean> exitReference) {
        super(null);

        this.setModal(true);
        this.exitReference = exitReference;
        this.path = path;
        this.columns = new ArrayList<>();
        this.tableName = new StringBuilder();

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
        this.setTitle("XML Import Configuration");

        try {
            XMLRecognizer recognizer = new XMLRecognizer(this.path.toString());
            this.analysisResult = recognizer.analyzeStructure();
        } catch (InvalidXMLException exception) {
            new ErrorFrame(exception.getMessage());
            this.exitReference.set(true);
            return;
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

        JPanel itemsPadding = new JPanel();
        itemsPadding.setLayout(new BoxLayout(itemsPadding, BoxLayout.X_AXIS));

        JPanel items = new JPanel();
        items.setLayout(new BoxLayout(items, BoxLayout.Y_AXIS));

        JPanel itemTableName = new JPanel();
        itemTableName.setLayout(new BoxLayout(itemTableName, BoxLayout.X_AXIS));

        JPanel itemRootElement = new JPanel();
        itemRootElement.setLayout(new BoxLayout(itemRootElement, BoxLayout.X_AXIS));

        JPanel itemRecordElement = new JPanel();
        itemRecordElement.setLayout(new BoxLayout(itemRecordElement, BoxLayout.X_AXIS));

        JPanel itemStrategy = new JPanel();
        itemStrategy.setLayout(new BoxLayout(itemStrategy, BoxLayout.X_AXIS));

        JPanel itemOptions = new JPanel();
        itemOptions.setLayout(new BoxLayout(itemOptions, BoxLayout.X_AXIS));

        JPanel itemSeparateTables = new JPanel();
        itemSeparateTables.setLayout(new BoxLayout(itemSeparateTables, BoxLayout.X_AXIS));

        this.headerPanel.add(itemsPadding);
        itemsPadding.add(items);

        items.add(Box.createVerticalStrut(5));
        items.add(itemTableName);
        items.add(itemRootElement);
        items.add(itemRecordElement);
        items.add(itemStrategy);
        items.add(itemOptions);
        items.add(itemSeparateTables);
        items.add(Box.createVerticalStrut(5));

        itemsPadding.add(Box.createHorizontalStrut(10));

        // Table name
        itemTableName.add(new JLabel("Table Name:"));
        itemTableName.add(Box.createHorizontalStrut(10));
        itemTableName.add(this.tableNameTextField);
        itemTableName.add(Box.createHorizontalGlue());

        // Set default table name from file
        String fileName = this.path.getFileName().toString();
        String defaultTableName = fileName.substring(0, fileName.lastIndexOf('.'));
        this.tableNameTextField.setText(defaultTableName);

        // Root element
        itemRootElement.add(new JLabel("Root Element:"));
        itemRootElement.add(Box.createHorizontalStrut(10));
        itemRootElement.add(this.rootElementComboBox);
        itemRootElement.add(Box.createHorizontalGlue());

        this.populateElementComboBoxes();
        this.rootElementComboBox.setSelectedItem(this.analysisResult.getRootElement());
        this.rootElementComboBox.setToolTipText("Select root element or leave empty for auto-detection");

        // Record element
        itemRecordElement.add(new JLabel("Record Element:"));
        itemRecordElement.add(Box.createHorizontalStrut(10));
        itemRecordElement.add(this.recordElementComboBox);
        itemRecordElement.add(Box.createHorizontalGlue());

        this.recordElementComboBox.setSelectedItem(this.analysisResult.getRecordElement());
        this.recordElementComboBox.setToolTipText("Select record element or leave empty for auto-detection");

        // Flattening strategy
        itemStrategy.add(new JLabel("Flattening Strategy:"));
        itemStrategy.add(Box.createHorizontalStrut(10));
        itemStrategy.add(this.strategyComboBox);
        itemStrategy.add(Box.createHorizontalGlue());

        this.strategyComboBox.setSelectedItem(this.analysisResult.getStrategy());
        this.strategyComboBox.addActionListener(this);

        // Options
        itemOptions.add(this.removeCommonPrefixCheckBox);
        itemOptions.add(Box.createHorizontalGlue());
        this.removeCommonPrefixCheckBox.setSelected(true); // Default to enabled
        this.removeCommonPrefixCheckBox.addActionListener(this);

        // Separate Tables section
        itemSeparateTables.add(new JLabel("Separate Tables:"));
        itemSeparateTables.add(Box.createHorizontalStrut(10));
        itemSeparateTables.add(this.autoCreateSeparateTablesCheckBox);
        itemSeparateTables.add(Box.createHorizontalStrut(10));
        this.separateTablesInfo.setEditable(false);
        this.separateTablesInfo.setBackground(this.getBackground());
        this.separateTablesInfo.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        itemSeparateTables.add(new JScrollPane(this.separateTablesInfo));
        itemSeparateTables.add(Box.createHorizontalStrut(10));
        itemSeparateTables.add(this.btnCreateSeparateTables);
        itemSeparateTables.add(Box.createHorizontalGlue());

        this.autoCreateSeparateTablesCheckBox.setSelected(true); // Default to enabled
        this.autoCreateSeparateTablesCheckBox.addActionListener(this);
        this.btnCreateSeparateTables.addActionListener(this);
        this.updateSeparateTablesVisibility();

        // Add action listeners for combo boxes and text field
        this.rootElementComboBox.addActionListener(this);
        this.recordElementComboBox.addActionListener(this);
        this.tableNameTextField.addActionListener(this);
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

        if (event.getSource() == this.strategyComboBox ||
            event.getSource() == this.rootElementComboBox ||
            event.getSource() == this.recordElementComboBox ||
            event.getSource() == this.removeCommonPrefixCheckBox) {
            this.updateTable();
            this.updateSeparateTablesVisibility();
        }

        if (event.getSource() == this.btnCreateSeparateTables) {
            this.createSeparateTables();
        }

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
        this.btnReady.setEnabled(!tableNameAlreadyExists && !this.tableNameTextField.getText().strip().isEmpty());
    }

    private void setItems() {
        this.tableName.append(this.tableNameTextField.getText().strip());

        for (String columnName : this.columnNames) {
            ColumnDataType type;

            if (this.typeComboBoxes.containsKey(columnName)) {
                type = (ColumnDataType) this.typeComboBoxes.get(columnName).getSelectedItem();
            } else {
                type = ColumnDataType.STRING;
            }

            Column column = new Column(columnName, "xml", type, false, false);
            this.columns.add(column);
        }
    }

    private void updateTable() {
        String rootElement = (String) this.rootElementComboBox.getSelectedItem();
        String recordElement = (String) this.recordElementComboBox.getSelectedItem();
        XMLRecognizer.FlatteningStrategy strategy = (XMLRecognizer.FlatteningStrategy) this.strategyComboBox.getSelectedItem();

        if (rootElement == null || rootElement.trim().isEmpty()) rootElement = null;
        if (recordElement == null || recordElement.trim().isEmpty()) recordElement = null;

        try {
            XMLRecognizer recognizer = new XMLRecognizer(this.path.toString(), rootElement, recordElement, strategy);
            this.analysisResult = recognizer.analyzeStructure();
        } catch (InvalidXMLException exception) {
            new ErrorFrame(exception.getMessage());
            return;
        }

        this.loadJTable();
        this.verifyReadyButton();
        this.revalidate();
    }

    private void loadJTable() {
        this.columnNames.clear();
        this.typeComboBoxes.clear();

        if (this.analysisResult == null || this.analysisResult.getColumns().isEmpty()) {
            this.model = new DefaultTableModel();
            this.jTable = new JTable(this.model);
            this.scrollPane.setViewportView(this.jTable);
            return;
        }

        // Create column names
        Vector<String> columnNames = new Vector<>();
        columnNames.add("Row");

        // Get original column names
        List<String> originalColumnNames = new ArrayList<>();
        for (entities.Column column : this.analysisResult.getColumns()) {
            originalColumnNames.add(column.NAME);
        }

        // Apply common prefix removal if enabled
        List<String> processedColumnNames = this.removeCommonPrefixCheckBox.isSelected()
            ? removeCommonPrefixes(originalColumnNames)
            : originalColumnNames;

        for (int i = 0; i < processedColumnNames.size(); i++) {
            String columnName = processedColumnNames.get(i);
            columnNames.add(columnName);
            this.columnNames.add(columnName);
        }

        // Create data rows
        Vector<Vector<Object>> data = new Vector<>();

        // Add header row with type selectors
        Vector<Object> headerRow = new Vector<>();
        headerRow.add("Type:");

        for (entities.Column column : this.analysisResult.getColumns()) {
            JComboBox<ColumnDataType> typeComboBox = new JComboBox<>(ColumnDataType.values());
            typeComboBox.setSelectedItem(column.DATA_TYPE);
            this.typeComboBoxes.put(column.NAME, typeComboBox);
            headerRow.add(typeComboBox);
        }
        data.add(headerRow);

        // Add sample data rows
        List<Map<String, String>> sampleData = this.analysisResult.getSampleData();
        for (int i = 0; i < Math.min(10, sampleData.size()); i++) {
            Vector<Object> row = new Vector<>();
            row.add(i + 1);

            Map<String, String> rowData = sampleData.get(i);
            for (entities.Column column : this.analysisResult.getColumns()) {
                String value = rowData.get(column.NAME);
                row.add(value != null ? value : "");
            }
            data.add(row);
        }

        this.model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return row == 0 && column > 0; // Only type selectors are editable
            }
        };

        this.jTable = new JTable(this.model);
        this.jTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        this.jTable.setFillsViewportHeight(true);

        // Configure custom cell renderer and editor for the first row (type selectors)
        for (int col = 1; col < columnNames.size(); col++) {
            String columnName = this.analysisResult.getColumns().get(col - 1).NAME;
            JComboBox<ColumnDataType> comboBox = this.typeComboBoxes.get(columnName);

            // Set custom renderer
            this.jTable.getColumnModel().getColumn(col).setCellRenderer(new TableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                        boolean isSelected, boolean hasFocus, int row, int column) {
                    if (row == 0) {
                        return comboBox;
                    } else {
                        // Use default renderer for data rows
                        DefaultTableCellRenderer defaultRenderer = new DefaultTableCellRenderer();
                        return defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    }
                }
            });

            // Set custom editor
            this.jTable.getColumnModel().getColumn(col).setCellEditor(new DefaultCellEditor(comboBox));
        }

        // Configure column widths to use available space efficiently
        this.configureColumnWidths();

        this.scrollPane.setViewportView(this.jTable);
    }

    /**
     * Configures column widths to distribute available space efficiently
     */
    private void configureColumnWidths() {
        if (this.jTable == null || this.jTable.getColumnCount() == 0) {
            return;
        }

        // Set minimum width for the row number column
        this.jTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        this.jTable.getColumnModel().getColumn(0).setMinWidth(50);
        this.jTable.getColumnModel().getColumn(0).setMaxWidth(80);

        // Calculate available width for data columns
        int totalColumns = this.jTable.getColumnCount();
        if (totalColumns > 1) {
            // Get the scroll pane width or use a default
            int availableWidth = this.scrollPane.getWidth();
            if (availableWidth <= 0) {
                availableWidth = 800; // Default width
            }

            // Reserve space for row number column and scrollbar
            int dataColumnsWidth = availableWidth - 80 - 20; // 80 for row column, 20 for scrollbar
            int columnWidth = Math.max(100, dataColumnsWidth / (totalColumns - 1)); // Minimum 100px per column

            // Set preferred width for data columns
            for (int i = 1; i < totalColumns; i++) {
                this.jTable.getColumnModel().getColumn(i).setPreferredWidth(columnWidth);
                this.jTable.getColumnModel().getColumn(i).setMinWidth(80);
            }
        }
    }

    public XMLInfo getXMLInfo() {
        String rootElement = (String) this.rootElementComboBox.getSelectedItem();
        String recordElement = (String) this.recordElementComboBox.getSelectedItem();
        XMLRecognizer.FlatteningStrategy strategy = (XMLRecognizer.FlatteningStrategy) this.strategyComboBox.getSelectedItem();

        if (rootElement == null || rootElement.trim().isEmpty()) rootElement = null;
        if (recordElement == null || recordElement.trim().isEmpty()) recordElement = null;

        return new XMLInfo(rootElement, recordElement, strategy, this.path, this.tableName.toString(), this.columns);
    }

    /**
     * Populates the element combo boxes with available XML elements
     */
    private void populateElementComboBoxes() {
        try {
            XMLRecognizer recognizer = new XMLRecognizer(this.path.toString());

            // Get all elements for root element combo box
            List<String> allElements = recognizer.getAllElementNames();
            this.rootElementComboBox.removeAllItems();
            this.rootElementComboBox.addItem(""); // Empty option for auto-detection
            for (String element : allElements) {
                this.rootElementComboBox.addItem(element);
            }

            // Get potential record elements (elements that appear multiple times)
            List<String> recordElements = recognizer.getPotentialRecordElements();
            this.recordElementComboBox.removeAllItems();
            this.recordElementComboBox.addItem(""); // Empty option for auto-detection
            for (String element : recordElements) {
                this.recordElementComboBox.addItem(element);
            }

            // If no potential record elements found, add all elements as options
            if (recordElements.isEmpty()) {
                for (String element : allElements) {
                    this.recordElementComboBox.addItem(element);
                }
            }

        } catch (InvalidXMLException e) {
            // If there's an error, just add empty options
            this.rootElementComboBox.removeAllItems();
            this.rootElementComboBox.addItem("");
            this.recordElementComboBox.removeAllItems();
            this.recordElementComboBox.addItem("");
        }
    }

    /**
     * Removes common prefixes from column names to make them cleaner
     * This method now handles both global prefixes and group-specific prefixes
     */
    private List<String> removeCommonPrefixes(List<String> columnNames) {
        if (columnNames.size() <= 1) {
            return new ArrayList<>(columnNames);
        }

        List<String> result = new ArrayList<>(columnNames);

        // First, try to remove global common prefix
        String globalPrefix = findLongestCommonPrefix(columnNames);
        if (globalPrefix.length() > 1) {
            result = removeGlobalPrefix(result, globalPrefix);
        }

        // Then, remove group-specific prefixes (e.g., "address." from address.street, address.city, etc.)
        result = removeGroupPrefixes(result);

        return result;
    }

    /**
     * Removes a global prefix that is common to all column names
     */
    private List<String> removeGlobalPrefix(List<String> columnNames, String commonPrefix) {
        List<String> cleanedNames = new ArrayList<>();
        for (String name : columnNames) {
            String cleaned = name.substring(commonPrefix.length());
            // Ensure we don't create empty names
            if (cleaned.isEmpty()) {
                cleaned = name; // Keep original if removal would result in empty string
            } else {
                // Remove leading separators
                while (!cleaned.isEmpty() && (cleaned.charAt(0) == '_' || cleaned.charAt(0) == '.' || cleaned.charAt(0) == '-')) {
                    cleaned = cleaned.substring(1);
                }
                // If nothing left after removing separators, keep original
                if (cleaned.isEmpty()) {
                    cleaned = name;
                } else {
                    // Capitalize first letter if it starts with lowercase after prefix removal
                    if (Character.isLowerCase(cleaned.charAt(0))) {
                        cleaned = Character.toUpperCase(cleaned.charAt(0)) + cleaned.substring(1);
                    }
                }
            }
            cleanedNames.add(cleaned);
        }
        return cleanedNames;
    }

    /**
     * Removes group-specific prefixes (e.g., "address." from nested elements)
     */
    private List<String> removeGroupPrefixes(List<String> columnNames) {
        // Group columns by their prefix (part before the first dot)
        Map<String, List<Integer>> prefixGroups = new HashMap<>();

        for (int i = 0; i < columnNames.size(); i++) {
            String columnName = columnNames.get(i);
            int dotIndex = columnName.indexOf('.');
            if (dotIndex > 0) {
                String prefix = columnName.substring(0, dotIndex);
                prefixGroups.computeIfAbsent(prefix, k -> new ArrayList<>()).add(i);
            }
        }

        List<String> result = new ArrayList<>(columnNames);

        // For each prefix group with multiple columns, remove the prefix
        for (Map.Entry<String, List<Integer>> entry : prefixGroups.entrySet()) {
            String prefix = entry.getKey();
            List<Integer> indices = entry.getValue();

            // Only remove prefix if there are multiple columns with the same prefix
            if (indices.size() > 1) {
                for (Integer index : indices) {
                    String originalName = columnNames.get(index);
                    String withoutPrefix = originalName.substring(prefix.length() + 1); // +1 to remove the dot

                    // Capitalize first letter
                    if (!withoutPrefix.isEmpty() && Character.isLowerCase(withoutPrefix.charAt(0))) {
                        withoutPrefix = Character.toUpperCase(withoutPrefix.charAt(0)) + withoutPrefix.substring(1);
                    }

                    result.set(index, withoutPrefix);
                }
            }
        }

        return result;
    }

    /**
     * Finds the longest common prefix among all column names
     */
    private String findLongestCommonPrefix(List<String> columnNames) {
        if (columnNames.isEmpty()) {
            return "";
        }

        String first = columnNames.get(0);
        int prefixLength = first.length();

        for (int i = 1; i < columnNames.size(); i++) {
            String current = columnNames.get(i);
            prefixLength = Math.min(prefixLength, current.length());

            for (int j = 0; j < prefixLength; j++) {
                if (first.charAt(j) != current.charAt(j)) {
                    prefixLength = j;
                    break;
                }
            }
        }

        if (prefixLength == 0) {
            return "";
        }

        String prefix = first.substring(0, prefixLength);

        // Find the last meaningful separator within the prefix
        int lastSeparator = -1;

        // Look for underscore, dot, or dash
        for (int i = prefix.length() - 1; i >= 0; i--) {
            char c = prefix.charAt(i);
            if (c == '_' || c == '.' || c == '-') {
                lastSeparator = i;
                break;
            }
        }

        // If no separator found, look for camelCase boundary
        if (lastSeparator == -1) {
            lastSeparator = findLastCamelCaseBoundary(prefix);
        }

        // Return prefix up to and including the separator
        if (lastSeparator >= 0) {
            return prefix.substring(0, lastSeparator + 1);
        }

        // If prefix is more than 3 characters and no separator, use it anyway
        if (prefix.length() > 3) {
            return prefix;
        }
        return "";
    }

    /**
     * Finds the last camelCase boundary in a string
     */
    private int findLastCamelCaseBoundary(String str) {
        for (int i = str.length() - 1; i > 0; i--) {
            if (Character.isUpperCase(str.charAt(i)) && Character.isLowerCase(str.charAt(i - 1))) {
                return i - 1;
            }
        }
        return -1;
    }

    /**
     * Updates the visibility and content of separate tables section
     */
    private void updateSeparateTablesVisibility() {
        XMLRecognizer.FlatteningStrategy strategy = (XMLRecognizer.FlatteningStrategy) this.strategyComboBox.getSelectedItem();
        boolean showSeparateTables = strategy == XMLRecognizer.FlatteningStrategy.SEPARATE_TABLES;

        this.autoCreateSeparateTablesCheckBox.setVisible(showSeparateTables);
        this.separateTablesInfo.setVisible(showSeparateTables);
        this.btnCreateSeparateTables.setVisible(showSeparateTables);

        if (showSeparateTables) {
            updateSeparateTablesInfo();
        }

        this.revalidate();
        this.repaint();
    }

    /**
     * Updates the information about potential separate tables
     */
    private void updateSeparateTablesInfo() {
        try {
            String rootElement = (String) this.rootElementComboBox.getSelectedItem();
            String recordElement = (String) this.recordElementComboBox.getSelectedItem();

            if (rootElement == null || rootElement.trim().isEmpty()) rootElement = null;
            if (recordElement == null || recordElement.trim().isEmpty()) recordElement = null;

            XMLRecognizer recognizer = new XMLRecognizer(this.path.toString(), rootElement, recordElement, XMLRecognizer.FlatteningStrategy.SEPARATE_TABLES);
            List<XMLRecognizer.SeparateTableInfo> separateTables = recognizer.getPotentialSeparateTables();

            if (separateTables.isEmpty()) {
                this.separateTablesInfo.setText("No separate tables detected.");
                this.btnCreateSeparateTables.setEnabled(false);
            } else {
                StringBuilder info = new StringBuilder();
                for (XMLRecognizer.SeparateTableInfo tableInfo : separateTables) {
                    info.append(tableInfo.toString()).append("\n");
                }
                this.separateTablesInfo.setText(info.toString().trim());
                this.btnCreateSeparateTables.setEnabled(true);
            }
        } catch (Exception e) {
            this.separateTablesInfo.setText("Error analyzing separate tables: " + e.getMessage());
            this.btnCreateSeparateTables.setEnabled(false);
        }
    }

    /**
     * Creates separate tables for detected nested structures
     */
    private void createSeparateTables() {
        try {
            String rootElement = (String) this.rootElementComboBox.getSelectedItem();
            String recordElement = (String) this.recordElementComboBox.getSelectedItem();

            if (rootElement == null || rootElement.trim().isEmpty()) rootElement = null;
            if (recordElement == null || recordElement.trim().isEmpty()) recordElement = null;

            XMLRecognizer recognizer = new XMLRecognizer(this.path.toString(), rootElement, recordElement, XMLRecognizer.FlatteningStrategy.SEPARATE_TABLES);
            List<XMLRecognizer.SeparateTableInfo> separateTables = recognizer.getPotentialSeparateTables();

            if (separateTables.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No separate tables to create.", "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            if (this.autoCreateSeparateTablesCheckBox.isSelected()) {
                // Auto-create separate tables
                createSeparateTablesAutomatically(separateTables, rootElement, recordElement);
            } else {
                // Show manual instructions
                showManualCreationInstructions(separateTables);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error creating separate tables: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Automatically creates separate tables with proper references
     */
    private void createSeparateTablesAutomatically(List<XMLRecognizer.SeparateTableInfo> separateTables, String rootElement, String recordElement) {
        try {
            StringBuilder successMessage = new StringBuilder("Successfully created the following tables:\n\n");
            int createdCount = 0;

            for (XMLRecognizer.SeparateTableInfo tableInfo : separateTables) {
                String tableName = this.tableNameTextField.getText().trim() + "_" + tableInfo.getElementName();

                // Check if table name already exists
                if (MainController.getTables().containsKey(tableName)) {
                    tableName = tableName + "_" + System.currentTimeMillis();
                }

                // Create XMLInfo for the separate table with special handling
                XMLInfo separateTableInfo = createSeparateTableXMLInfo(
                    rootElement,
                    recordElement,
                    tableInfo.getElementName(),
                    tableName,
                    tableInfo
                );

                // Create the table (skip adding to main graph since it's a separate table)
                XMLTableCell tableCell = TableCreator.createXMLTable(
                    separateTableInfo.tableName(),
                    separateTableInfo.columns(),
                    separateTableInfo,
                    false,
                    true  // skipMainGraph = true for separate tables
                );

                // Add table to tables panel only (not to main graph)
                MainController.saveTable(tableCell);

                successMessage.append("• ").append(tableName)
                             .append(" (").append(tableInfo.getOccurrenceCount()).append(" records)\n");
                createdCount++;
            }

            if (createdCount > 0) {
                successMessage.append("\nAll tables include reference IDs for linking with the main table.");
                JOptionPane.showMessageDialog(this, successMessage.toString(), "Tables Created Successfully", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error auto-creating tables: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Shows manual creation instructions
     */
    private void showManualCreationInstructions(List<XMLRecognizer.SeparateTableInfo> separateTables) {
        StringBuilder message = new StringBuilder("The following separate tables can be created:\n\n");
        for (XMLRecognizer.SeparateTableInfo tableInfo : separateTables) {
            message.append("• ").append(tableInfo.getElementName())
                   .append(" (").append(tableInfo.getOccurrenceCount()).append(" records)\n");
        }
        message.append("\nTo create these tables manually:\n");
        message.append("1. Import this XML file again\n");
        message.append("2. Change the table name for each separate table\n");
        message.append("3. Select the corresponding element as the record element\n");
        message.append("4. Use NESTED_COLUMNS strategy for better structure\n");

        JOptionPane.showMessageDialog(this, message.toString(), "Separate Tables Guide", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Creates XMLInfo for separate tables with special data extraction
     */
    private XMLInfo createSeparateTableXMLInfo(String rootElement, String parentRecordElement,
                                               String childElementName, String tableName,
                                               XMLRecognizer.SeparateTableInfo tableInfo) {
        // Create XMLInfo with special marker in recordElement to indicate separate table
        // Format: "SEPARATE_TABLE:parentElement:childElement"
        String specialRecordElement = "SEPARATE_TABLE:" + parentRecordElement + ":" + childElementName;

        return new XMLInfo(
            rootElement,
            specialRecordElement,
            XMLRecognizer.FlatteningStrategy.NESTED_COLUMNS,
            this.path,
            tableName,
            createColumnsForSeparateTable(tableInfo)
        );
    }

    /**
     * Creates columns for a separate table based on its structure
     */
    private List<entities.Column> createColumnsForSeparateTable(XMLRecognizer.SeparateTableInfo tableInfo) {
        List<entities.Column> columns = new ArrayList<>();

        // Add a reference ID column to link back to the main table
        columns.add(new entities.Column("parent_id", "xml", ColumnDataType.STRING));

        // Add columns based on the element structure
        for (String columnName : tableInfo.getColumns()) {
            columns.add(new entities.Column(columnName, "xml", ColumnDataType.STRING));
        }

        return columns;
    }
}
