package gui.frames.forms.importexport;

import controller.MainController;
import database.TableUtils;
import database.TuplesExtractor;
import entities.Column;
import entities.cells.Cell;
import gui.frames.forms.FormBase;
import gui.frames.forms.IFormCondition;
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

public class ExportSQLScriptForm extends FormBase implements ActionListener, IFormCondition {

    private final JPanel formPane = new JPanel(new GridBagLayout());
    private final JPanel tablePane = new JPanel();
    private final JScrollPane scrollPane = new JScrollPane();

    private final JTextArea textArea = new JTextArea();
    private final JButton btnChangeScreen = new JButton("Ver tabela");
    private final JTextField txtFieldDatabaseName = new JTextField();
    private final JTextField txtFieldTableName = new JTextField();
    private final JCheckBox checkBoxCreateDatabase;

    private final StringBuilder databaseName;
    private final StringBuilder tableName;
    private final StringBuilder additionalCommand;
    private final Map<String, JCheckBox> pkCheckBoxes;
    private final Map<String, JCheckBox> nullCheckBoxes;
    private final Map<String, JTextField> newColumnNameTxtFields;
    private final Vector<String> columnNames;
    private final Vector<Vector<Object>> content;
    private final AtomicReference<Boolean> exitReference;
    private final Cell cell;
    private boolean isForm = true;

    public ExportSQLScriptForm(Cell cell, SQLScriptInf inf, AtomicReference<Boolean> exitReference){

        super(null);
        setModal(true);

        this.tableName = inf.tableName;
        this.newColumnNameTxtFields = inf.newColumnNameTxtFields;
        this.columnNames = inf.columnNames;
        this.content = inf.content;
        this.databaseName = inf.databaseName;
        this.pkCheckBoxes = inf.pkCheckBoxes;
        this.nullCheckBoxes = inf.nullCheckBoxes;
        this.exitReference = exitReference;
        this.cell = cell;
        this.checkBoxCreateDatabase = inf.checkBoxCreateDatabase;
        this.additionalCommand = inf.additionalCommand;

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

        tablePane.setLayout(new BorderLayout());
        tablePane.add(scrollPane);

        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        btnChangeScreen.addActionListener(this);

        initForm();
        initBottom();

        checkBtnReady();

        setLocationRelativeTo(null);
        this.setVisible(true);

    }

    private void initForm(){

        contentPane.add(btnChangeScreen, BorderLayout.NORTH);
        contentPane.add(formPane, BorderLayout.CENTER);

        addComponent(new JLabel("Nome do Database: "), 0, 0, 1, 1);
        addComponent(txtFieldDatabaseName, 1, 0, 1, 1);
        txtFieldDatabaseName.setMaximumSize(new Dimension(3000, 50));
        txtFieldDatabaseName.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                checkBtnReady();
            }
            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                checkBtnReady();
            }
            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                checkBtnReady();
            }
        });

        addComponent(new JLabel("Nome da tabela: "), 0, 1, 1, 1);
        addComponent(txtFieldTableName, 1, 1, 1, 1);
        txtFieldTableName.setMaximumSize(new Dimension(3000, 50));
        txtFieldTableName.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                checkBtnReady();
            }
            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                checkBtnReady();
            }
            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                checkBtnReady();
            }
        });

        addComponent(checkBoxCreateDatabase, 0, 2, 1, 1);

        addComponent(new JLabel("  Coluna  "), 0, 3, 1, 1);
        addComponent(new JLabel("  Nome da coluna  "), 1, 3, 1, 1);
        addComponent(new JLabel("  Chave primária  "), 2, 3, 1, 1);
        addComponent(new JLabel("  Pode possuir valor null  "), 3, 3, 1, 1);

        int i = 4;
        for(String columnName : columnNames.subList(0, columnNames.size()-1)){

            addComponent(new JLabel(columnName), 0, i, 1, 1);
            addComponent(newColumnNameTxtFields.get(columnName), 1, i, 1, 1);
            addComponent(pkCheckBoxes.get(columnName), 2, i, 1, 1);
            addComponent(nullCheckBoxes.get(columnName), 3, i, 1, 1);
            i++;

        }

        addComponent(new JScrollPane(textArea), 0, i++, 4, 1);

    }

    protected void addComponent(Component component, int gridx, int gridy, int gridwidth, int gridheight) {

        GridBagConstraints gbc = ((GridBagLayout) formPane.getLayout()).getConstraints(formPane);

        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = gridwidth;
        gbc.gridheight = gridheight;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPane.add(component, gbc);

        pack();
        revalidate();
        repaint();
    }

    private void loadTable() {

        contentPane.remove(formPane);
        contentPane.add(tablePane, BorderLayout.CENTER);

        pack();
        revalidate();
        repaint();

    }

    private void loadForm(){

        contentPane.remove(tablePane);
        contentPane.add(formPane, BorderLayout.CENTER);

        pack();
        revalidate();
        repaint();

    }

    private void initBottom(){

        btnCancel.addActionListener(this);
        btnReady.addActionListener(this);

    }

    private void loadJTable(){

        boolean columnsPut = false;

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
                            checkBtnReady();
                        }
                        @Override
                        public void removeUpdate(DocumentEvent documentEvent) {
                            checkBtnReady();
                        }
                        @Override
                        public void changedUpdate(DocumentEvent documentEvent) {
                            checkBtnReady();
                        }
                    });

                    pkCheckBoxes.put(inf, pkCheckBox);
                    nullCheckBoxes.put(inf, nullCheckBox);
                    newColumnNameTxtFields.put(inf, newColumnNameTextField);

                }
                columnsPut = true;

            }

            Vector<Object> line = new Vector<>(row.values());

            content.add(line);

        }

        JTableUtils.CustomTableModel model = new JTableUtils.CustomTableModel(content, columnNames);

        addFirstColumn(model);

        JTable table = new JTable(model);

        setCheckBoxesEnabled();

        JTableUtils.preferredColumnWidthByValues(table, 0);
        for(int i = 1; i < table.getColumnCount(); i++)
            JTableUtils.preferredColumnWidthByColumnName(table, i);

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

        for (int row = 0; row < model.getRowCount(); row++) {
            model.setValueAt(row + 1, row, model.getColumnCount() - 1);
        }

    }

    protected void closeWindow() {
        dispose();
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        checkBtnReady();

        if(btnCancel == actionEvent.getSource()){
            exitReference.set(true);
            closeWindow();
        }

        if(btnReady == actionEvent.getSource()){

            databaseName.append(txtFieldDatabaseName.getText());
            tableName.append(txtFieldTableName.getText());
            additionalCommand.append(textArea.getText());
            closeWindow();

        }

        if(btnChangeScreen == actionEvent.getSource()){

            if(isForm){

                isForm = false;
                btnChangeScreen.setText("Voltar");
                loadTable();
                return;

            }

            isForm = true;
            btnChangeScreen.setText("Ver tabela");
            loadForm();

        }

    }

    @Override
    public void checkBtnReady() {

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

    @Override
    public void updateToolTipTxt() {

    }

    public record SQLScriptInf(StringBuilder databaseName, StringBuilder tableName,
                               Map<String, JTextField> newColumnNameTxtFields, Map<String, JCheckBox> pkCheckBoxes,
                               Map<String, JCheckBox> nullCheckBoxes, JCheckBox checkBoxCreateDatabase,
                               StringBuilder additionalCommand, Vector<String> columnNames,
                               Vector<Vector<Object>> content){

    }

}
