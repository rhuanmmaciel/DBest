package gui.frames.forms.importexport;

import controller.MainController;
import database.TableUtils;
import database.TuplesExtractor;
import entities.Column;
import entities.cells.Cell;
import gui.frames.forms.FormBase;
import gui.utils.JTableUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ExportMySQLScriptForm extends FormBase implements ActionListener {

    private final JPanel headerPanel = new JPanel();
    private final JPanel mainPanel = new JPanel();
    private final JScrollPane scrollPane = new JScrollPane();

    private final JTextField txtFieldDatabaseName = new JTextField();
    private final JTextField txtFieldTableName = new JTextField();
    private final JCheckBox checkBoxCreateDatabase;

    private final StringBuilder databaseName;
    private final StringBuilder tableName;
    private final Map<String, JCheckBox> pkCheckBoxes;
    private final Map<String, JCheckBox> nullCheckBoxes;
    private final Map<String, JTextField> newColumnNameTxtFields;
    private final Vector<String> columnNames;
    private final Vector<Vector<Object>> content;
    private final AtomicReference<Boolean> exitReference;
    private final Cell cell;

    public ExportMySQLScriptForm(Cell cell, StringBuilder databaseName, StringBuilder tableName,
                                 Map<String, JTextField> newColumnNameTxtFields, Map<String, JCheckBox> pkCheckBoxes,
                                 Map<String, JCheckBox> nullCheckBoxes, JCheckBox checkBoxCreateDatabase,
                                 Vector<String> columnNames, Vector<Vector<Object>> content,
                                 AtomicReference<Boolean> exitReference){

        super(null);
        setModal(true);

        this.tableName = tableName;
        this.newColumnNameTxtFields = newColumnNameTxtFields;
        this.columnNames = columnNames;
        this.content = content;
        this.databaseName = databaseName;
        this.pkCheckBoxes = pkCheckBoxes;
        this.nullCheckBoxes = nullCheckBoxes;
        this.exitReference = exitReference;
        this.cell = cell;
        this.checkBoxCreateDatabase = checkBoxCreateDatabase;

        loadJTable();

        initGUI();

    }

    private void initGUI(){

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                exitReference.set(true);
                closeWindow();
            }
        });

        setBounds(0, 0, MainController.WIDTH, MainController.HEIGHT);
        setLocationRelativeTo(null);

        initHeader();
        initCenter();
        initBottom();

        verifyReadyButton();

        this.setVisible(true);

    }

    private void initHeader(){

        contentPane.add(headerPanel, BorderLayout.NORTH);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        Box mainHeaderBox = Box.createHorizontalBox();
        Box items = Box.createVerticalBox();
        Box itemsPadding = Box.createHorizontalBox();
        Box itemDatabaseName = Box.createHorizontalBox();
        Box itemTableName = Box.createHorizontalBox();
        Box itemCreateDatabase = Box.createHorizontalBox();

        headerPanel.add(mainHeaderBox);
        mainHeaderBox.add(itemsPadding);
        mainHeaderBox.add(items);
        items.add(Box.createVerticalStrut(5));
        items.add(itemDatabaseName);
        items.add(itemTableName);
        items.add(itemCreateDatabase);
        items.add(Box.createVerticalStrut(5));

        itemsPadding.add(Box.createVerticalStrut(10));

        itemDatabaseName.add(new JLabel("Nome do Database: "));
        itemDatabaseName.add(txtFieldDatabaseName);
        txtFieldDatabaseName.setMaximumSize(new Dimension(3000, 50));
        txtFieldDatabaseName.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                verifyReadyButton();
            }
            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                verifyReadyButton();
            }
            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                verifyReadyButton();
            }
        });
        itemDatabaseName.add(Box.createHorizontalGlue());

        itemTableName.add(new JLabel("Nome da tabela: "));
        itemTableName.add(txtFieldTableName);
        txtFieldTableName.setMaximumSize(new Dimension(3000, 50));
        txtFieldTableName.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                verifyReadyButton();
            }
            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                verifyReadyButton();
            }
            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                verifyReadyButton();
            }
        });
        itemTableName.add(Box.createHorizontalGlue());

        itemCreateDatabase.add(checkBoxCreateDatabase);
        itemCreateDatabase.add(Box.createHorizontalGlue());

    }

    private void initCenter() {

        contentPane.add(mainPanel, BorderLayout.CENTER);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(scrollPane);

        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

    }

    private void initBottom(){

        btnCancel.addActionListener(this);
        btnReady.addActionListener(this);

    }

    private void loadJTable(){

        boolean columnsPut = false;

        List<JCheckBox> pkCheckBoxesList = new ArrayList<>();
        List<JCheckBox> nullCheckBoxesList = new ArrayList<>();
        List<JTextField> newColumnNamesList = new ArrayList<>();

        for(Map<String, String> row : TuplesExtractor.getAllRows(cell.getOperator(), true)){

            if(!columnsPut){

                for (String inf : row.keySet()) {

                    columnNames.add(inf);

                    JCheckBox pkCheckBox = new JCheckBox();
                    JCheckBox nullCheckBox = new JCheckBox();
                    JTextField newColumnNameTextField = new JTextField(Column.removeSource(inf));
                    pkCheckBox.addActionListener(this);
                    newColumnNameTextField.getDocument().addDocumentListener(new DocumentListener() {
                        @Override
                        public void insertUpdate(DocumentEvent documentEvent) {
                            verifyReadyButton();
                        }
                        @Override
                        public void removeUpdate(DocumentEvent documentEvent) {
                            verifyReadyButton();
                        }
                        @Override
                        public void changedUpdate(DocumentEvent documentEvent) {
                            verifyReadyButton();
                        }
                    });

                    pkCheckBoxes.put(inf, pkCheckBox);
                    nullCheckBoxes.put(inf, nullCheckBox);
                    newColumnNameTxtFields.put(inf, newColumnNameTextField);
                    pkCheckBoxesList.add(pkCheckBox);
                    nullCheckBoxesList.add(nullCheckBox);
                    newColumnNamesList.add(newColumnNameTextField);

                }
                columnsPut = true;

            }

            Vector<Object> line = new Vector<>(row.values());

            content.add(line);

        }

        JTableUtils.CustomTableModel model = new JTableUtils.CustomTableModel(content, columnNames);

        model.insertRow(0, new Object[] {});
        model.insertRow(0, new Object[] {});
        model.insertRow(0, new Object[]{});

        addFirstColumn(model);

        JTable table = new JTable(model) {

            @Override
            public TableCellRenderer getCellRenderer(int row, int column) {

                if (row == 0 && column != 0) {
                    return new DefaultTableCellRenderer() {

                        @Override
                        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                                       boolean hasFocus, int row, int column) {

                            return newColumnNamesList.get(column - 1);
                        }
                    };
                }

                if (row == 1 && column != 0) {
                    return new DefaultTableCellRenderer() {

                        @Override
                        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                                       boolean hasFocus, int row, int column) {

                            return pkCheckBoxesList.get(column - 1);
                        }
                    };
                }

                if (row == 2 && column != 0) {
                    return new DefaultTableCellRenderer() {

                        @Override
                        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                                       boolean hasFocus, int row, int column) {

                            return nullCheckBoxesList.get(column - 1);
                        }
                    };
                }

                return super.getCellRenderer(row, column);
            }

            @Override
            public TableCellEditor getCellEditor(int row, int column) {

                if (row == 0 && column != 0)
                    return new DefaultCellEditor(newColumnNamesList.get(column - 1));

                if (row == 1 && column != 0)
                    return new DefaultCellEditor(pkCheckBoxesList.get(column - 1));

                if (row == 2 && column != 0)
                    return new DefaultCellEditor(nullCheckBoxesList.get(column - 1));
                else
                    return super.getCellEditor(row, column);
            }
        };

        setCheckBoxesEnabled();

        JTableUtils.preferredColumnWidthByValues(table, 0);
        for(int i = 1; i < table.getColumnCount(); i++)
            JTableUtils.preferredColumnWidthByColumnName(table, i);

        table.getColumnModel().getColumn(0).setResizable(false);
        table.setRowHeight(30);
        table.getColumnModel().moveColumn(model.getColumnCount() - 1, 0);
        table.getColumnModel().getColumn(0).setResizable(false);
        JTableUtils.setColumnBold(table, 0);
        JTableUtils.preferredColumnWidthByValues(table, 0);
        table.setShowHorizontalLines(true);
        table.setGridColor(Color.blue);
        table.setColumnSelectionAllowed(false);
        table.setRowSelectionAllowed(false);
        table.setCellSelectionEnabled(false);
        JTableUtils.setNullInRed(table);
        scrollPane.setViewportView(table);
        table.setFillsViewportHeight(true);
        model.setRowEnabled(0, true);
        model.setRowEnabled(1, true);
        model.setRowEnabled(2, true);

    }

    private void setCheckBoxesEnabled(){

        for(Map.Entry<String, JCheckBox> checkBox : nullCheckBoxes.entrySet()){

                List<String> columnData = new ArrayList<>();
                int index = columnNames.indexOf(checkBox.getKey());

                for (Vector<Object> column : content)
                    columnData.add(Objects.toString(column.get(index)));

                boolean canBeNotNull = !TableUtils.hasNull(columnData.subList(3, columnData.size()));
                checkBox.getValue().setEnabled(canBeNotNull);
                checkBox.getValue().setSelected(!canBeNotNull);

        }

    }

    private void addFirstColumn(JTableUtils.CustomTableModel model) {

        model.addColumn("Nome:");

        model.setValueAt("Nome da coluna:", 0, model.getColumnCount() - 1);
        model.setValueAt("Chave Primária:", 1, model.getColumnCount() - 1);
        model.setValueAt("Pode possuir o valor null:", 2, model.getColumnCount() - 1);

        for (int row = 3; row < model.getRowCount(); row++) {
            model.setValueAt(row - 1, row, model.getColumnCount() - 1);
        }

    }

    private void verifyReadyButton(){

        List<List<String>> columnsSelected = new ArrayList<>();
        for(Map.Entry<String, JCheckBox> checkBox : pkCheckBoxes.entrySet())
            if(checkBox.getValue().isSelected()) {

                List<String> columnData = new ArrayList<>();
                int index = columnNames.indexOf(checkBox.getKey());

                for (Vector<Object> column : content)
                    columnData.add(Objects.toString(column.get(index)));

                columnsSelected.add(columnData.subList(3, columnData.size()));

            }

        boolean everyColumnHasName = newColumnNameTxtFields.values().stream().noneMatch(x -> x.getText().isBlank());
        boolean canBePK = columnsSelected.isEmpty() || TableUtils.canBePrimaryKey(columnsSelected);

        btnReady.setEnabled(canBePK && !txtFieldDatabaseName.getText().isBlank() &&
                !txtFieldTableName.getText().isBlank() && everyColumnHasName);

    }

    protected void closeWindow() {
        dispose();
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        verifyReadyButton();

        if(btnCancel == actionEvent.getSource()){
            exitReference.set(true);
            closeWindow();
        }

        if(btnReady == actionEvent.getSource()){

            databaseName.append(txtFieldDatabaseName.getText());
            tableName.append(txtFieldTableName.getText());
            closeWindow();

        }

    }
}
