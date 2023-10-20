package gui.frames.forms.importexport;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import controllers.ConstantController;

import database.TableUtils;
import database.TuplesExtractor;

import entities.Column;
import entities.cells.Cell;

import gui.frames.forms.FormBase;
import gui.frames.forms.IFormCondition;

import gui.utils.JTableUtils;

public class ExportSQLScriptForm extends FormBase implements ActionListener, IFormCondition {

    private final JPanel formPane = new JPanel(new GridBagLayout());

    private final JPanel tablePane = new JPanel();

    private final JScrollPane scrollPane = new JScrollPane();

    private final JTextArea textArea = new JTextArea();

    private final JButton btnChangeScreen = new JButton(ConstantController.getString("exportSQLScript.showTableButton"));

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

    public ExportSQLScriptForm(Cell cell, SQLScriptInf inf, AtomicReference<Boolean> exitReference) {
        super(null);

        this.setModal(true);

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

        this.loadJTable();

        this.initGUI();
    }

    @Override
    public void initGUI() {
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent event) {
                ExportSQLScriptForm.this.exitReference.set(true);
                ExportSQLScriptForm.this.closeWindow();
            }
        });

        this.tablePane.setLayout(new BorderLayout());
        this.tablePane.add(this.scrollPane);

        this.scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        this.scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        this.btnChangeScreen.addActionListener(this);

        this.initForm();
        this.initBottom();

        this.checkBtnReady();

        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void initForm() {

        this.contentPanel.add(this.btnChangeScreen, BorderLayout.NORTH);
        this.contentPanel.add(this.formPane, BorderLayout.CENTER);

        this.addComponent(new JLabel(String.format("%s:", ConstantController.getString("exportSQLScript.databaseName"))), 0, 0, 1, 1);
        this.addComponent(this.txtFieldDatabaseName, 1, 0, 1, 1);
        this.txtFieldDatabaseName.setMaximumSize(new Dimension(3000, 50));
        this.txtFieldDatabaseName.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                ExportSQLScriptForm.this.checkBtnReady();
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                ExportSQLScriptForm.this.checkBtnReady();
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                ExportSQLScriptForm.this.checkBtnReady();
            }
        });

        this.addComponent(new JLabel(String.format("%s:", ConstantController.getString("exportSQLScript.tableName"))), 0, 1, 1, 1);
        this.addComponent(this.txtFieldTableName, 1, 1, 1, 1);
        this.txtFieldTableName.setMaximumSize(new Dimension(3000, 50));
        this.txtFieldTableName.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                ExportSQLScriptForm.this.checkBtnReady();
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                ExportSQLScriptForm.this.checkBtnReady();
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                ExportSQLScriptForm.this.checkBtnReady();
            }
        });

        this.addComponent(this.checkBoxCreateDatabase, 0, 2, 1, 1);

        this.addComponent(new JLabel(String.format(" %s ", ConstantController.getString("exportSQLScript.column"))), 0, 3, 1, 1);
        this.addComponent(new JLabel(String.format(" %s ", ConstantController.getString("exportSQLScript.columnName"))), 1, 3, 1, 1);
        this.addComponent(new JLabel(String.format(" %s ", ConstantController.getString("exportSQLScript.primaryKey"))), 2, 3, 1, 1);
        this.addComponent(new JLabel(String.format(" %s ", ConstantController.getString("exportSQLScript.canBeNull"))), 3, 3, 1, 1);

        int i = 4;

        for (String columnName : this.columnNames.subList(0, this.columnNames.size() - 1)) {
            this.addComponent(new JLabel(columnName), 0, i, 1, 1);
            this.addComponent(this.newColumnNameTxtFields.get(columnName), 1, i, 1, 1);
            this.addComponent(this.pkCheckBoxes.get(columnName), 2, i, 1, 1);
            this.addComponent(this.nullCheckBoxes.get(columnName), 3, i, 1, 1);
            i++;
        }

        this.addComponent(new JScrollPane(this.textArea), 0, i, 4, 1);
    }

    protected void addComponent(Component component, int gridX, int gridY, int gridWidth, int gridHeight) {
        GridBagConstraints gbc = ((GridBagLayout) this.formPane.getLayout()).getConstraints(this.formPane);

        gbc.gridx = gridX;
        gbc.gridy = gridY;
        gbc.gridwidth = gridWidth;
        gbc.gridheight = gridHeight;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        this.formPane.add(component, gbc);
        this.pack();
        this.revalidate();
        this.repaint();
    }

    private void loadTable() {
        this.contentPanel.remove(this.formPane);
        this.contentPanel.add(this.tablePane, BorderLayout.CENTER);

        this.pack();
        this.revalidate();
        this.repaint();
    }

    private void loadForm() {
        this.contentPanel.remove(this.tablePane);
        this.contentPanel.add(this.formPane, BorderLayout.CENTER);

        this.pack();
        this.revalidate();
        this.repaint();
    }

    private void initBottom() {
        this.btnCancel.addActionListener(this);
        this.btnReady.addActionListener(this);
    }

    private void loadJTable() {
        boolean columnsPut = false;

        for (Map<String, String> row : TuplesExtractor.getAllRows(this.cell.getOperator(), true)) {
            if (!columnsPut) {
                for (String inf : row.keySet()) {
                    this.columnNames.add(inf);

                    JCheckBox pkCheckBox = new JCheckBox();
                    JCheckBox nullCheckBox = new JCheckBox();
                    JTextField newColumnNameTextField = new JTextField(Column.removeSource(inf));

                    pkCheckBox.addActionListener(this);

                    newColumnNameTextField.getDocument().addDocumentListener(new DocumentListener() {

                        @Override
                        public void insertUpdate(DocumentEvent documentEvent) {
                            ExportSQLScriptForm.this.checkBtnReady();
                        }

                        @Override
                        public void removeUpdate(DocumentEvent documentEvent) {
                            ExportSQLScriptForm.this.checkBtnReady();
                        }

                        @Override
                        public void changedUpdate(DocumentEvent documentEvent) {
                            ExportSQLScriptForm.this.checkBtnReady();
                        }
                    });

                    this.pkCheckBoxes.put(inf, pkCheckBox);
                    this.nullCheckBoxes.put(inf, nullCheckBox);
                    this.newColumnNameTxtFields.put(inf, newColumnNameTextField);
                }

                columnsPut = true;
            }

            Vector<Object> line = new Vector<>(row.values());

            this.content.add(line);
        }

        JTableUtils.CustomTableModel model = new JTableUtils.CustomTableModel(this.content, this.columnNames);

        this.addFirstColumn(model);

        JTable table = new JTable(model);

        this.setCheckBoxesEnabled();

        JTableUtils.preferredColumnWidthByValues(table, 0);

        for (int i = 1; i < table.getColumnCount(); i++) {
            JTableUtils.preferredColumnWidthByColumnName(table, i);
        }

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

        this.scrollPane.setViewportView(table);
        table.setFillsViewportHeight(true);
    }

    private void setCheckBoxesEnabled() {
        for (Map.Entry<String, JCheckBox> checkBox : this.nullCheckBoxes.entrySet()) {
            List<String> columnData = new ArrayList<>();

            int index = this.columnNames.indexOf(checkBox.getKey());

            for (Vector<Object> column : this.content) {
                columnData.add(Objects.toString(column.get(index)));
            }

            boolean canBeNotNull = !TableUtils.hasNull(columnData.subList(3, columnData.size()));

            checkBox.getValue().setEnabled(canBeNotNull);
            checkBox.getValue().setSelected(!canBeNotNull);
        }
    }

    private void addFirstColumn(JTableUtils.CustomTableModel model) {
        model.addColumn(String.format("%s", ConstantController.getString("exportSQLScript.firstColumnName")));

        for (int row = 0; row < model.getRowCount(); row++) {
            model.setValueAt(row + 1, row, model.getColumnCount() - 1);
        }
    }

    protected void closeWindow() {
        this.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        this.checkBtnReady();

        if (this.btnCancel == actionEvent.getSource()) {
            this.exitReference.set(true);
            this.closeWindow();
        }

        if (this.btnReady == actionEvent.getSource()) {
            this.databaseName.append(this.txtFieldDatabaseName.getText());
            this.tableName.append(this.txtFieldTableName.getText());
            this.additionalCommand.append(this.textArea.getText());
            this.closeWindow();
        }

        if (this.btnChangeScreen == actionEvent.getSource()) {
            if (this.isForm) {
                this.isForm = false;
                this.btnChangeScreen.setText(String.format("%s", ConstantController.getString("exportSQLScript.backButton")));
                this.loadTable();
            } else {
                this.isForm = true;
                this.btnChangeScreen.setText(String.format("%s", ConstantController.getString("exportSQLScript.showTableButton")));
                this.loadForm();
            }
        }
    }

    @Override
    public void checkBtnReady() {
        List<List<String>> columnsSelected = new ArrayList<>();

        for (Map.Entry<String, JCheckBox> checkBox : this.pkCheckBoxes.entrySet()) {
            if (checkBox.getValue().isSelected()) {
                List<String> columnData = new ArrayList<>();
                int index = this.columnNames.indexOf(checkBox.getKey());

                for (Vector<Object> column : this.content) {
                    columnData.add(Objects.toString(column.get(index)));
                }

                columnsSelected.add(columnData.subList(3, columnData.size()));
            }
        }

        boolean everyColumnHasName = this.newColumnNameTxtFields.values().stream().noneMatch(x -> x.getText().isBlank());
        boolean canBePK = columnsSelected.isEmpty() || TableUtils.canBePrimaryKey(columnsSelected);

        this.btnReady.setEnabled(canBePK && !this.txtFieldDatabaseName.getText().isBlank() && !this.txtFieldTableName.getText().isBlank() && everyColumnHasName);
    }

    @Override
    public void updateToolTipText(boolean... conditions) {

    }

    public record SQLScriptInf(
        StringBuilder databaseName, StringBuilder tableName, Map<String, JTextField> newColumnNameTxtFields,
        Map<String, JCheckBox> pkCheckBoxes, Map<String, JCheckBox> nullCheckBoxes, JCheckBox checkBoxCreateDatabase,
        StringBuilder additionalCommand, Vector<String> columnNames, Vector<Vector<Object>> content
    ) {

    }
}
