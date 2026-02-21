package gui.frames.forms.create;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import entities.Column;
import enums.ColumnDataType;
import gui.frames.forms.FormBase;
import gui.frames.forms.IFormCondition;

import org.kordamp.ikonli.materialdesign2.MaterialDesignP;
import org.kordamp.ikonli.materialdesign2.MaterialDesignM;
import org.kordamp.ikonli.swing.FontIcon;

public class FormFramePopulateColumn extends FormBase implements ActionListener, DocumentListener, IFormCondition {

    private final List<Column> columns;
    private final DefaultTableModel dataTableModel;
    private final JTable dataTable;

    private JComboBox<Object> columnComboBox;
    private JLabel lblColumnType;
    private JTextField valueField;
    private JSpinner quantitySpinner;
    private JCheckBox preserveExistingCheckbox;
    private JButton btnAddValue;
    private JButton btnRemoveValue;
    private JTable valuesTable;
    private DefaultTableModel valuesTableModel;

    private final List<ValueQuantityPair> valueQuantityPairs;

    public FormFramePopulateColumn(List<Column> columns, DefaultTableModel dataTableModel, JTable dataTable) {
        super((Window) null);
        this.setModal(true);
        this.setTitle("Populate Column");
        this.columns = new ArrayList<>(columns);
        this.dataTableModel = dataTableModel;
        this.dataTable = dataTable;
        this.valueQuantityPairs = new ArrayList<>();
        this.initGUI();
    }

    private void initGUI() {
        this.setSize(500, 450);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Monta os painéis principais
        JPanel topPanel = createColumnSelectionPanel();
        JPanel centerPanel = createValuesConfigurationPanel();
        JPanel bottomPanel = createButtonPanel();

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        this.setContentPane(mainPanel);
        this.updateColumnType();
        this.updateButtons();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    /**
     * Cria o painel de seleção de coluna
     */
    private JPanel createColumnSelectionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Label da coluna
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Column:"), gbc);

        // ComboBox de seleção de coluna
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        this.columnComboBox = new JComboBox<>();
        this.columns.forEach(column -> this.columnComboBox.addItem(column.NAME));
        this.columnComboBox.addActionListener(this);
        panel.add(this.columnComboBox, gbc);

        // Label do tipo da coluna
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        this.lblColumnType = new JLabel();
        panel.add(this.lblColumnType, gbc);

        return panel;
    }

    // Cria o painel de configuração de valores
    private JPanel createValuesConfigurationPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel inputPanel = createValueInputPanel();
        JPanel tablePanel = createValuesTablePanel();

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(tablePanel, BorderLayout.CENTER);

        return panel;
    }

    // Cria o painel de entrada de valores
    private JPanel createValueInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Campo de valor
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Value:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        this.valueField = new JTextField(15);
        this.valueField.getDocument().addDocumentListener(this);
        panel.add(this.valueField, gbc);

        // Campo de quantidade
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Quantity:"), gbc);

        gbc.gridx = 3;
        this.quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
        this.quantitySpinner.setPreferredSize(new Dimension(80, 25));
        panel.add(this.quantitySpinner, gbc);

        // Botões de adicionar/remover
        gbc.gridx = 4;
        this.btnAddValue = new JButton("Add");
        this.btnAddValue.setIcon(FontIcon.of(MaterialDesignP.PLUS));
        this.btnAddValue.addActionListener(this);
        panel.add(this.btnAddValue, gbc);

        gbc.gridx = 5;
        this.btnRemoveValue = new JButton("Remove");
        this.btnRemoveValue.setIcon(FontIcon.of(MaterialDesignM.MINUS));
        this.btnRemoveValue.addActionListener(this);
        panel.add(this.btnRemoveValue, gbc);

        return panel;
    }

    // Cria o painel da tabela de valores
    private JPanel createValuesTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Modelo da tabela com colunas: Value, Quantity, Percentage
        this.valuesTableModel = new DefaultTableModel(new String[]{"Value", "Quantity", "Percentage"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 2; // Percentage não é editável
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return switch (columnIndex) {
                    case 1 -> Integer.class;
                    case 2 -> String.class;
                    default -> String.class;
                };
            }
        };

        this.valuesTable = new JTable(this.valuesTableModel);
        this.valuesTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        this.valuesTable.getSelectionModel().addListSelectionListener(e -> updateButtons());

        JScrollPane scrollPane = new JScrollPane(this.valuesTable);
        scrollPane.setPreferredSize(new Dimension(400, 200));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // Cria o painel de botões e opções
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Painel de opções (checkbox)
        JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        this.preserveExistingCheckbox = new JCheckBox("Preserve existing values", true);
        this.preserveExistingCheckbox.setToolTipText("When checked, new values will be added without overwriting existing ones");
        optionsPanel.add(preserveExistingCheckbox);

        // Painel de botões de ação
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        this.btnCancel.addActionListener(this);
        this.btnReady.addActionListener(this);
        actionPanel.add(this.btnCancel);
        actionPanel.add(this.btnReady);

        panel.add(optionsPanel, BorderLayout.WEST);
        panel.add(actionPanel, BorderLayout.EAST);

        return panel;
    }

    // Atualiza o tipo da coluna selecionada
    private void updateColumnType() {
        if (this.columnComboBox.getSelectedItem() != null) {
            int columnIndex = this.dataTable.getColumnModel()
                .getColumnIndex(Objects.requireNonNull(this.columnComboBox.getSelectedItem()).toString());
            ColumnDataType selectedColumnType = this.columns.get(columnIndex).DATA_TYPE;
            this.lblColumnType.setText(String.format("Type: %s", selectedColumnType.toString()));
        }
    }

    // Adiciona um novo valor à lista
    private void addValue() {
        String value = this.valueField.getText().trim();
        int quantity = (Integer) this.quantitySpinner.getValue();

        if (value.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a value.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validar o tipo do valor
        if (!isValidValue(value)) {
            return;
        }

        // Verificar se o valor já existe
        for (ValueQuantityPair pair : this.valueQuantityPairs) {
            if (pair.getValue().equals(value)) {
                JOptionPane.showMessageDialog(this, "Value already exists. Please enter a different value.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Adicionar o valor
        ValueQuantityPair newPair = new ValueQuantityPair(value, quantity);
        this.valueQuantityPairs.add(newPair);

        // Atualizar interface
        updateValuesTable();
        this.valueField.setText("");
        this.quantitySpinner.setValue(1);
        updateButtons();
    }

    // Remove o valor selecionado da lista
    private void removeValue() {
        int selectedRow = this.valuesTable.getSelectedRow();
        if (selectedRow >= 0) {
            this.valueQuantityPairs.remove(selectedRow);
            updateValuesTable();
            updateButtons();
        }
    }

    // Valida se o valor é compatível com o tipo da coluna
    private boolean isValidValue(String value) {
        int columnIndex = this.dataTable.getColumnModel()
            .getColumnIndex(Objects.requireNonNull(this.columnComboBox.getSelectedItem()).toString());
        ColumnDataType columnType = this.columns.get(columnIndex).DATA_TYPE;

        try {
            switch (columnType) {
                case INTEGER:
                    Integer.parseInt(value);
                    break;
                case LONG:
                    Long.parseLong(value);
                    break;
                case FLOAT:
                    Float.parseFloat(value);
                    break;
                case DOUBLE:
                    Double.parseDouble(value);
                    break;
                case BOOLEAN:
                    if (!value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")) {
                        throw new IllegalArgumentException("Invalid boolean value");
                    }
                    break;
                case CHARACTER:
                    if (value.length() != 1) {
                        throw new IllegalArgumentException("Character must be exactly one character");
                    }
                    break;
                case STRING:
                case NONE:
                    // Qualquer string é válida
                    break;
            }
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                String.format("Invalid value for type %s: %s", columnType.toString(), e.getMessage()),
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // Atualiza a tabela de valores com percentuais
    private void updateValuesTable() {
        this.valuesTableModel.setRowCount(0);

        int totalQuantity = this.valueQuantityPairs.stream().mapToInt(ValueQuantityPair::getQuantity).sum();

        for (ValueQuantityPair pair : this.valueQuantityPairs) {
            double percentage = totalQuantity > 0 ? (double) pair.getQuantity() / totalQuantity * 100.0 : 0.0;
            this.valuesTableModel.addRow(new Object[]{
                pair.getValue(),
                pair.getQuantity(),
                String.format("%.1f%%", percentage)
            });
        }
    }

    // MÉTODO PRINCIPAL - Popular a coluna com os valores configurados
    private void populateColumn() {
        if (this.valueQuantityPairs.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please add at least one value.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int columnIndex = this.dataTable.getColumnModel()
            .getColumnIndex(Objects.requireNonNull(this.columnComboBox.getSelectedItem()).toString());

        // Criar lista distribuída de valores
        List<Object> distributedValues = createDistributedValues();

        // Embaralhar para distribuição aleatória
        Collections.shuffle(distributedValues, new Random());

        if (preserveExistingCheckbox.isSelected()) {
            // Modo preservar valores existentes
            preserveExistingValues(columnIndex, distributedValues);
        } else {
            // Modo sobrepor - substituir valores existentes
            overwriteValues(columnIndex, distributedValues);
        }

        this.dispose();
    }

    // MODO PRESERVAR - Adiciona valores apenas em células vazias
    private void preserveExistingValues(int columnIndex, List<Object> distributedValues) {
        // Identifica linhas com valores nulos/vazios na coluna
        List<Integer> emptyRows = new ArrayList<>();

        for (int row = 0; row < dataTableModel.getRowCount(); row++) {
            Object currentValue = dataTableModel.getValueAt(row, columnIndex);
            if (isValueEmpty(currentValue)) {
                emptyRows.add(row);
            }
        }

        // Adiciona novas linhas se necessário
        int valuesToAdd = distributedValues.size();
        int availableEmptyRows = emptyRows.size();

        if (availableEmptyRows < valuesToAdd) {
            int rowsToAdd = valuesToAdd - availableEmptyRows;
            for (int i = 0; i < rowsToAdd; i++) {
                dataTableModel.addRow(new Object[dataTableModel.getColumnCount()]);
                emptyRows.add(dataTableModel.getRowCount() - 1);
            }
        }

        // Preenche apenas as linhas vazias
        for (int i = 0; i < Math.min(valuesToAdd, emptyRows.size()); i++) {
            int rowIndex = emptyRows.get(i);
            dataTableModel.setValueAt(distributedValues.get(i), rowIndex, columnIndex);
        }
    }

    // MODO SOBREPOR - Substitui valores existentes
    private void overwriteValues(int columnIndex, List<Object> distributedValues) {
        int totalRows = distributedValues.size();

        // Ajusta o número de linhas
        adjustRowCount(totalRows);

        // Preenche todas as linhas com os novos valores
        for (int i = 0; i < totalRows; i++) {
            dataTableModel.setValueAt(distributedValues.get(i), i, columnIndex);
        }

        // Limpa valores da coluna nas linhas extras
        for (int i = totalRows; i < dataTableModel.getRowCount(); i++) {
            dataTableModel.setValueAt(null, i, columnIndex);
        }

        // Remove linhas completamente vazias
        removeEmptyRows();
    }

    // Verifica se um valor é considerado vazio
    private boolean isValueEmpty(Object value) {
        return value == null || value.toString().trim().isEmpty();
    }

    // Remove linhas completamente vazias
    private void removeEmptyRows() {
        for (int row = dataTableModel.getRowCount() - 1; row >= 0; row--) {
            if (isRowEmpty(row)) {
                dataTableModel.removeRow(row);
            }
        }
    }

    // Verifica se uma linha está completamente vazia
    private boolean isRowEmpty(int rowIndex) {
        for (int col = 0; col < dataTableModel.getColumnCount(); col++) {
            Object value = dataTableModel.getValueAt(rowIndex, col);
            if (!isValueEmpty(value)) {
                return false;
            }
        }
        return true;
    }

    // Ajusta o número de linhas da tabela
    private void adjustRowCount(int desiredRowCount) {
        while (dataTableModel.getRowCount() < desiredRowCount) {
            dataTableModel.addRow(new Object[dataTableModel.getColumnCount()]);
        }
    }

    // Cria a lista distribuída de valores baseada nas quantidades
    private List<Object> createDistributedValues() {
        List<Object> distributedValues = new ArrayList<>();

        for (ValueQuantityPair pair : this.valueQuantityPairs) {
            Object convertedValue = convertValueToColumnType(pair.getValue());
            for (int i = 0; i < pair.getQuantity(); i++) {
                distributedValues.add(convertedValue);
            }
        }

        return distributedValues;
    }

    // Converte string para o tipo apropriado da coluna
    private Object convertValueToColumnType(String value) {
        int columnIndex = this.dataTable.getColumnModel()
            .getColumnIndex(Objects.requireNonNull(this.columnComboBox.getSelectedItem()).toString());
        ColumnDataType columnType = this.columns.get(columnIndex).DATA_TYPE;

        try {
            return switch (columnType) {
                case INTEGER -> Integer.parseInt(value);
                case LONG -> Long.parseLong(value);
                case FLOAT -> Float.parseFloat(value);
                case DOUBLE -> Double.parseDouble(value);
                case BOOLEAN -> Boolean.parseBoolean(value);
                case CHARACTER -> value.charAt(0);
                default -> value; // STRING, NONE
            };
        } catch (Exception e) {
            return value; // Fallback para string
        }
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == this.btnCancel) {
            this.dispose();
        } else if (event.getSource() == this.btnReady) {
            this.populateColumn();
        } else if (event.getSource() == this.columnComboBox) {
            this.updateColumnType();
            this.valueQuantityPairs.clear();
            this.updateValuesTable();
            this.updateButtons();
        } else if (event.getSource() == this.btnAddValue) {
            this.addValue();
        } else if (event.getSource() == this.btnRemoveValue) {
            this.removeValue();
        }
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        this.updateButtons();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        this.updateButtons();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        this.updateButtons();
    }

    private void updateButtons() {
        boolean hasValue = !this.valueField.getText().trim().isEmpty();
        boolean hasValues = !this.valueQuantityPairs.isEmpty();
        boolean hasSelection = this.valuesTable.getSelectedRow() >= 0;

        this.btnAddValue.setEnabled(hasValue);
        this.btnRemoveValue.setEnabled(hasSelection);
        this.btnReady.setEnabled(hasValues);

        this.checkBtnReady();
    }

    @Override
    public void checkBtnReady() {
        boolean hasValues = !this.valueQuantityPairs.isEmpty();
        this.btnReady.setEnabled(hasValues);
        this.updateToolTipText(hasValues);
    }

    @Override
    public void updateToolTipText(boolean... conditions) {
        String tooltip = "";
        if (!conditions[0]) {
            tooltip = "Please add at least one value with quantity.";
        }

        UIManager.put("ToolTip.foreground", Color.RED);
        this.btnReady.setToolTipText(tooltip.isEmpty() ? null : tooltip);
    }

    // Classe auxiliar para armazenar par valor-quantidade
    private static class ValueQuantityPair {
        private final String value;
        private final int quantity;

        public ValueQuantityPair(String value, int quantity) {
            this.value = value;
            this.quantity = quantity;
        }

        public String getValue() {
            return value;
        }

        public int getQuantity() {
            return quantity;
        }
    }
}
