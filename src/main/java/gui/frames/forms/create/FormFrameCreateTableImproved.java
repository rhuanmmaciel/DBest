package gui.frames.forms.create;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import controllers.ConstantController;
import database.TableCreator;
import engine.exceptions.DataBaseException;
import entities.Column;
import entities.cells.TableCell;
import enums.ColumnDataType;
import gui.frames.ErrorFrame;
import gui.frames.forms.FormBase;
import gui.frames.forms.IFormCondition;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kordamp.ikonli.materialdesign2.MaterialDesignA;
import org.kordamp.ikonli.materialdesign2.MaterialDesignD;
import org.kordamp.ikonli.materialdesign2.MaterialDesignP;
import org.kordamp.ikonli.materialdesign2.MaterialDesignM;
import org.kordamp.ikonli.swing.FontIcon;

public class FormFrameCreateTableImproved extends FormBase implements ActionListener, DocumentListener, IFormCondition {

    // Componentes da aba de Colunas
    private JList<Column> columnList;
    private DefaultListModel<Column> columnListModel;
    private JTextField columnNameField;
    private JComboBox<ColumnDataType> columnTypeCombo;
    private JCheckBox primaryKeyCheckbox;
    private JButton btnAddColumn;
    private JButton btnEditColumn;
    private JButton btnRemoveColumn;

    // Componentes da aba de Dados
    private JTable dataTable;
    private DefaultTableModel dataTableModel;
    private JButton btnAddRow;
    private JButton btnRemoveRow;
    private JButton btnGenerateData;
    private JButton btnPopulateColumn;

    // Componentes gerais
    private JTextField txtFieldTableName;
    private JTabbedPane tabbedPane;
    private TableCell tableCell;
    private final AtomicReference<Boolean> exitReference;
    private final List<Column> columns;

    public FormFrameCreateTableImproved(AtomicReference<Boolean> exitReference) {
        super(null);
        this.setModal(true);
        this.exitReference = exitReference;
        this.columns = new ArrayList<>();
        this.tableCell = null;
        this.initGUI();
    }

    public void initGUI() {
        this.setTitle(ConstantController.getString("createTable.enhanced.title"));
        this.setSize(800, 600);
        
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                FormFrameCreateTableImproved.this.exitReference.set(true);
            }
        });

        this.btnReady.addActionListener(this);
        this.btnCancel.addActionListener(this);

        // Layout principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Painel superior - Nome da tabela
        JPanel tableNamePanel = createTableNamePanel();
        mainPanel.add(tableNamePanel, BorderLayout.NORTH);

        // Painel central - Abas
        this.tabbedPane = new JTabbedPane();
        this.tabbedPane.addTab(ConstantController.getString("createTable.enhanced.columns.tab"), FontIcon.of(MaterialDesignA.APPS), createColumnsPanel());
        this.tabbedPane.addTab(ConstantController.getString("createTable.enhanced.data.tab"), FontIcon.of(MaterialDesignD.DATABASE), createDataPanel());
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // Painel inferior - Botões
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        this.setContentPane(mainPanel);
        this.updateButtons();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private JPanel createTableNamePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(new TitledBorder(ConstantController.getString("createTable.enhanced.table.configuration")));
        
        JLabel tableNameLabel = new JLabel(ConstantController.getString("createTable.tableName") + ":");
        this.txtFieldTableName = new JTextField(20);
        this.txtFieldTableName.getDocument().addDocumentListener(this);
        
        panel.add(tableNameLabel);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(txtFieldTableName);
        
        return panel;
    }

    private JPanel createColumnsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Painel esquerdo - Lista de colunas
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(new TitledBorder(ConstantController.getString("createTable.enhanced.columns.tab")));
        leftPanel.setPreferredSize(new Dimension(300, 0));
        
        this.columnListModel = new DefaultListModel<>();
        this.columnList = new JList<>(columnListModel);
        this.columnList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.columnList.setCellRenderer(new ColumnListCellRenderer());
        this.columnList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateColumnFields();
                updateButtons();
            }
        });
        
        JScrollPane columnScrollPane = new JScrollPane(columnList);
        leftPanel.add(columnScrollPane, BorderLayout.CENTER);
        
        // Botões de gerenciamento de colunas
        JPanel columnButtonPanel = new JPanel(new FlowLayout());
        this.btnRemoveColumn = new JButton(ConstantController.getString("createTable.enhanced.remove.column"));
        this.btnRemoveColumn.setIcon(FontIcon.of(MaterialDesignM.MINUS));
        this.btnRemoveColumn.addActionListener(this);
        columnButtonPanel.add(btnRemoveColumn);
        
        leftPanel.add(columnButtonPanel, BorderLayout.SOUTH);
        
        // Painel direito - Edição de coluna
        JPanel rightPanel = createColumnEditPanel();
        
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createColumnEditPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder(ConstantController.getString("createTable.enhanced.column.details")));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Nome da coluna
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel(ConstantController.getString("createTable.enhanced.column.name")), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        this.columnNameField = new JTextField(20);
        this.columnNameField.getDocument().addDocumentListener(this);
        panel.add(columnNameField, gbc);
        
        // Tipo da coluna
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel(ConstantController.getString("createTable.enhanced.data.type")), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        this.columnTypeCombo = new JComboBox<>(ColumnDataType.values());
        this.columnTypeCombo.setRenderer(new ColumnDataTypeRenderer());
        this.columnTypeCombo.addActionListener(this);
        panel.add(columnTypeCombo, gbc);
        
        // Primary Key
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        this.primaryKeyCheckbox = new JCheckBox(ConstantController.getString("createTable.enhanced.primary.key"));
        this.primaryKeyCheckbox.addActionListener(this);
        panel.add(primaryKeyCheckbox, gbc);
        
        // Botões de ação
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE;
        JPanel actionPanel = new JPanel(new FlowLayout());
        
        this.btnAddColumn = new JButton(ConstantController.getString("createTable.enhanced.add.column"));
        this.btnAddColumn.setIcon(FontIcon.of(MaterialDesignP.PLUS));
        this.btnAddColumn.addActionListener(this);
        actionPanel.add(btnAddColumn);
        
        this.btnEditColumn = new JButton(ConstantController.getString("createTable.enhanced.update.column"));
        this.btnEditColumn.setIcon(FontIcon.of(MaterialDesignP.PENCIL));
        this.btnEditColumn.addActionListener(this);
        actionPanel.add(btnEditColumn);
        
        panel.add(actionPanel, gbc);
        
        // Espaço vazio para empurrar tudo para cima
        gbc.gridy = 4; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        panel.add(Box.createVerticalGlue(), gbc);
        
        return panel;
    }

    private JPanel createDataPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Tabela de dados
        this.dataTableModel = new DefaultTableModel();
        this.dataTable = new JTable(dataTableModel);
        this.dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        JScrollPane dataScrollPane = new JScrollPane(dataTable);
        panel.add(dataScrollPane, BorderLayout.CENTER);
        
        // Botões de gerenciamento de dados
        JPanel dataButtonPanel = new JPanel(new FlowLayout());
        
        this.btnAddRow = new JButton(ConstantController.getString("createTable.enhanced.add.row"));
        this.btnAddRow.setIcon(FontIcon.of(MaterialDesignP.PLUS));
        this.btnAddRow.addActionListener(this);
        dataButtonPanel.add(btnAddRow);
        
        this.btnRemoveRow = new JButton(ConstantController.getString("createTable.enhanced.remove.row"));
        this.btnRemoveRow.setIcon(FontIcon.of(MaterialDesignM.MINUS));
        this.btnRemoveRow.addActionListener(this);
        dataButtonPanel.add(btnRemoveRow);
        
        this.btnGenerateData = new JButton(ConstantController.getString("createTable.enhanced.generate.data"));
        this.btnGenerateData.setIcon(FontIcon.of(MaterialDesignD.DICE_6));
        this.btnGenerateData.addActionListener(this);
        dataButtonPanel.add(btnGenerateData);

        this.btnPopulateColumn = new JButton(ConstantController.getString("populateColumn.enhanced.populate.data"));
        this.btnPopulateColumn.setIcon(FontIcon.of(MaterialDesignP.PERCENT));
        this.btnPopulateColumn.setIcon(FontIcon.of(MaterialDesignD.DICE_6));
        this.btnPopulateColumn.addActionListener(this);
        dataButtonPanel.add(btnPopulateColumn);

        panel.add(dataButtonPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.add(btnCancel);
        panel.add(btnReady);
        return panel;
    }

    private void updateColumnFields() {
        Column selectedColumn = columnList.getSelectedValue();
        if (selectedColumn != null) {
            columnNameField.setText(selectedColumn.NAME);
            columnTypeCombo.setSelectedItem(selectedColumn.DATA_TYPE);
            primaryKeyCheckbox.setSelected(selectedColumn.IS_PRIMARY_KEY);
            btnEditColumn.setEnabled(true);
        } else {
            clearColumnFields();
            btnEditColumn.setEnabled(false);
        }
    }

    private void clearColumnFields() {
        columnNameField.setText("");
        columnTypeCombo.setSelectedIndex(0);
        primaryKeyCheckbox.setSelected(false);
    }

    private void updateDataTable() {
        // Limpar modelo atual
        dataTableModel.setRowCount(0);
        dataTableModel.setColumnCount(0);
        
        // Adicionar colunas baseadas na definição
        for (Column column : columns) {
            dataTableModel.addColumn(column.NAME);
        }
        
        // Revalidar tabela
        dataTable.revalidate();
        dataTable.repaint();
    }

    private void addColumn() {
        String name = columnNameField.getText().trim();
        ColumnDataType type = (ColumnDataType) columnTypeCombo.getSelectedItem();
        boolean isPrimaryKey = primaryKeyCheckbox.isSelected();
        
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, ConstantController.getString("createTable.enhanced.error.empty.name"), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Verificar se já existe coluna com este nome
        for (Column column : columns) {
            if (column.NAME.equals(name)) {
                JOptionPane.showMessageDialog(this, ConstantController.getString("createTable.enhanced.error.name.exists"), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        // Verificar se já existe primary key
        if (isPrimaryKey) {
            for (Column column : columns) {
                if (column.IS_PRIMARY_KEY) {
                    JOptionPane.showMessageDialog(this, ConstantController.getString("createTable.enhanced.error.pk.exists"), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }
        
        Column newColumn = new Column(name, "", type, isPrimaryKey);
        columns.add(newColumn);
        columnListModel.addElement(newColumn);
        
        updateDataTable();
        clearColumnFields();
        updateButtons();
    }

    private void editColumn() {
        int selectedIndex = columnList.getSelectedIndex();
        if (selectedIndex >= 0) {
            String name = columnNameField.getText().trim();
            ColumnDataType type = (ColumnDataType) columnTypeCombo.getSelectedItem();
            boolean isPrimaryKey = primaryKeyCheckbox.isSelected();
            
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, ConstantController.getString("createTable.enhanced.error.empty.name"), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Verificar se já existe coluna com este nome (exceto a atual)
            for (int i = 0; i < columns.size(); i++) {
                if (i != selectedIndex && columns.get(i).NAME.equals(name)) {
                    JOptionPane.showMessageDialog(this, ConstantController.getString("createTable.enhanced.error.name.exists"), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            // Verificar se já existe primary key (exceto a atual)
            if (isPrimaryKey) {
                for (int i = 0; i < columns.size(); i++) {
                    if (i != selectedIndex && columns.get(i).IS_PRIMARY_KEY) {
                        JOptionPane.showMessageDialog(this, ConstantController.getString("createTable.enhanced.error.pk.exists"), "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }
            
            Column updatedColumn = new Column(name, "", type, isPrimaryKey);
            columns.set(selectedIndex, updatedColumn);
            columnListModel.setElementAt(updatedColumn, selectedIndex);
            
            updateDataTable();
            updateButtons();
        }
    }

    private void removeColumn() {
        int selectedIndex = columnList.getSelectedIndex();
        if (selectedIndex >= 0) {
            int result = JOptionPane.showConfirmDialog(this, 
                ConstantController.getString("createTable.enhanced.confirm.remove"), 
                "Confirm Removal", 
                JOptionPane.YES_NO_OPTION);
            
            if (result == JOptionPane.YES_OPTION) {
                columns.remove(selectedIndex);
                columnListModel.remove(selectedIndex);
                updateDataTable();
                clearColumnFields();
                updateButtons();
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == this.btnCancel) {
            this.exitReference.set(true);
            this.dispose();
        } else if (event.getSource() == this.btnReady) {
            try {
                this.createTable();
            } catch (Exception ex) {
                Logger.getLogger(FormFrameCreateTableImproved.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (event.getSource() == this.btnAddColumn) {
            addColumn();
        } else if (event.getSource() == this.btnEditColumn) {
            editColumn();
        } else if (event.getSource() == this.btnRemoveColumn) {
            removeColumn();
        } else if (event.getSource() == this.btnAddRow) {
            if (dataTableModel.getColumnCount() > 0) {
                dataTableModel.addRow(new Object[dataTableModel.getColumnCount()]);
            }
        } else if (event.getSource() == this.btnRemoveRow) {
            int selectedRow = dataTable.getSelectedRow();
            if (selectedRow >= 0) {
                dataTableModel.removeRow(selectedRow);
            }
        } else if (event.getSource() == this.btnGenerateData) {
            if (!columns.isEmpty() && dataTableModel.getRowCount() > 0) {
                new FormFrameGenerateData(columns, dataTableModel, dataTable);
            }
        } else if (event.getSource() == this.btnPopulateColumn) {
            if (dataTableModel.getColumnCount() > 0) {
                new FormFramePopulateColumn(columns, dataTableModel, dataTable);
            }
        }
        
        updateButtons();
    }

    @Override
    public void insertUpdate(DocumentEvent event) {
        this.updateButtons();
    }

    @Override
    public void removeUpdate(DocumentEvent event) {
        this.updateButtons();
    }

    @Override
    public void changedUpdate(DocumentEvent event) {
        this.updateButtons();
    }

    private void updateButtons() {
        boolean hasColumns = !columns.isEmpty();
        boolean hasTableName = !txtFieldTableName.getText().trim().isEmpty();
        boolean hasData = dataTableModel.getRowCount() > 0;
        boolean hasColumnName = !columnNameField.getText().trim().isEmpty();
        boolean hasSelection = columnList.getSelectedIndex() >= 0;
        
        // Botões da aba de colunas
        btnAddColumn.setEnabled(hasColumnName);
        btnEditColumn.setEnabled(hasSelection && hasColumnName);
        btnRemoveColumn.setEnabled(hasSelection);
        
        // Botões da aba de dados
        btnAddRow.setEnabled(hasColumns);
        btnRemoveRow.setEnabled(hasData && dataTable.getSelectedRow() >= 0);
        btnGenerateData.setEnabled(hasColumns && hasData);
        btnPopulateColumn.setEnabled(hasColumns);
        
        // Botão final
        btnReady.setEnabled(hasColumns && hasTableName && hasData);
        
        this.checkBtnReady();
    }

    private void createTable() throws Exception {
        Map<Integer, Map<String, String>> content = new HashMap<>();

        for (int i = 0; i < dataTableModel.getRowCount(); i++) {
            Map<String, String> line = new HashMap<>();

            for (int j = 0; j < dataTableModel.getColumnCount(); j++) {
                Object value = dataTableModel.getValueAt(i, j);
                String stringValue = (value == null) ? "" : value.toString();
                line.put(dataTableModel.getColumnName(j), stringValue);
            }

            content.put(i, line);
        }

        boolean exit = false;
        AtomicReference<Boolean> exitReference = new AtomicReference<>(exit);

        List<Column> rightSourceColumns = new ArrayList<>(columns.stream()
            .map(column -> new Column(column.NAME, txtFieldTableName.getText(), column.DATA_TYPE, column.IS_PRIMARY_KEY))
            .toList());

        if (!exitReference.get()) {
            try {
                tableCell = TableCreator.createMemoryTable(txtFieldTableName.getText(), rightSourceColumns, content);
            } catch (DataBaseException e) {
                exitReference.set(true);
                new ErrorFrame(e.getMessage());
            }
        } else {
            exitReference.set(true);
        }

        this.dispose();
    }

    public TableCell getResult() {
        return this.tableCell;
    }

    @Override
    public void checkBtnReady() {
        boolean hasColumns = !columns.isEmpty();
        boolean hasTableName = !txtFieldTableName.getText().trim().isEmpty();
        boolean hasData = dataTableModel.getRowCount() > 0;
        
        this.btnReady.setEnabled(hasColumns && hasTableName && hasData);
    }

    @Override
    public void updateToolTipText(boolean... conditions) {
        String tooltip = "";
        if (columns.isEmpty()) {
            tooltip = ConstantController.getString("createTable.enhanced.tooltip.add.columns");
        } else if (txtFieldTableName.getText().trim().isEmpty()) {
            tooltip = ConstantController.getString("createTable.enhanced.tooltip.enter.name");
        } else if (dataTableModel.getRowCount() == 0) {
            tooltip = ConstantController.getString("createTable.enhanced.tooltip.add.data");
        }
        
        btnReady.setToolTipText(tooltip.isEmpty() ? null : tooltip);
    }

    // Renderer customizado para a lista de colunas
    private static class ColumnListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof Column column) {
                String text = String.format("%s (%s)%s", 
                    column.NAME, 
                    column.DATA_TYPE.toString(),
                    column.IS_PRIMARY_KEY ? " [PK]" : "");
                setText(text);
                
                // Ícone baseado no tipo
                FontIcon icon = switch (column.DATA_TYPE) {
                    case INTEGER, LONG -> FontIcon.of(MaterialDesignM.MATH_INTEGRAL);
                    case FLOAT, DOUBLE -> FontIcon.of(MaterialDesignM.MATH_INTEGRAL);
                    case STRING, CHARACTER -> FontIcon.of(MaterialDesignA.ALPHA);
                    case BOOLEAN -> FontIcon.of(MaterialDesignA.ALPHA_B);
                    default -> FontIcon.of(MaterialDesignA.ALPHA);
                };
                
                if (column.IS_PRIMARY_KEY) {
                    icon.setIconColor(Color.ORANGE);
                }
                
                setIcon(icon);
            }
            
            return this;
        }
    }

    // Renderer customizado para o ComboBox de tipos de dados
    private static class ColumnDataTypeRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof ColumnDataType type) {
                setText(type.toString());
                
                FontIcon icon = switch (type) {
                    case INTEGER, LONG -> FontIcon.of(MaterialDesignM.MATH_INTEGRAL);
                    case FLOAT, DOUBLE -> FontIcon.of(MaterialDesignM.MATH_INTEGRAL);
                    case STRING, CHARACTER -> FontIcon.of(MaterialDesignA.ALPHA);
                    case BOOLEAN -> FontIcon.of(MaterialDesignA.ALPHA_B);
                    default -> FontIcon.of(MaterialDesignA.ALPHA);
                };
                
                setIcon(icon);
            }
            
            return this;
        }
    }
}
