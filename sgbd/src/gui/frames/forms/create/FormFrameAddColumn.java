package gui.frames.forms.create;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.List;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import controllers.ConstantController;
import entities.Column;

import enums.ColumnDataType;

import gui.frames.forms.FormBase;
import gui.frames.forms.IFormCondition;

public class FormFrameAddColumn extends FormBase implements ActionListener, DocumentListener, IFormCondition {

    private JTextField textFieldColumnName;

    private JComboBox<Object> comboBox;

    private final JCheckBox checkBoxIsPrimaryKey = new JCheckBox();

    private final DefaultTableModel table;

    private JLabel lblColumnName;

    private final List<Column> columns;

    public FormFrameAddColumn(DefaultTableModel table, List<Column> columns) {
        super(null);

        this.setModal(true);

        this.columns = columns;
        this.table = table;

        this.initGUI();
    }

    public void initGUI() {
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent event) {
                FormFrameAddColumn.this.closeWindow();
            }
        });

        btnReady.addActionListener(this);
        btnCancel.addActionListener(this);

        this.lblColumnName = new JLabel(ConstantController.getString("createTable.addColumn.columnName"));
        this.textFieldColumnName = new JTextField();
        this.textFieldColumnName.getDocument().addDocumentListener(this);
        this.textFieldColumnName.setColumns(10);

        String[] dataTypeOptions = {"None", "Integer", "Float", "Character", "String", "Boolean"};

        this.comboBox = new JComboBox<>(dataTypeOptions);

        Box boxMain = Box.createVerticalBox();

        Box boxColumnName = Box.createHorizontalBox();
        boxColumnName.add(lblColumnName);
        boxColumnName.add(textFieldColumnName);
        boxMain.add(boxColumnName);

        boxMain.add(comboBox);

        Box boxPrimaryKey = Box.createHorizontalBox();
        boxPrimaryKey.add(new JLabel(ConstantController.getString("createTable.addColumn.isPK")));
        boxPrimaryKey.add(checkBoxIsPrimaryKey);

        boxMain.add(boxPrimaryKey);

        contentPanel.add(boxMain);

        this.checkBtnReady();
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == this.btnReady) {

            boolean isPrimaryKey = this.checkBoxIsPrimaryKey.isSelected();

            ColumnDataType columnDataType = switch (comboBox.getSelectedItem().toString()) {
                case "Integer" -> ColumnDataType.INTEGER;
                case "Float" -> ColumnDataType.FLOAT;
                case "String" -> ColumnDataType.STRING;
                case "Character" -> ColumnDataType.CHARACTER;
                case "Boolean" -> ColumnDataType.BOOLEAN;
                case null, default -> ColumnDataType.NONE;
            };

            this.table.addColumn(this.textFieldColumnName.getText().replaceAll("[^\\p{Alnum}]", ""));
            this.columns.add(new Column(textFieldColumnName.getText(), "any", columnDataType, isPrimaryKey));
            this.closeWindow();
        }

        if (event.getSource() == this.btnCancel) {
            this.closeWindow();
        }
    }

    @Override
    public void insertUpdate(DocumentEvent event) {
        this.checkBtnReady();
    }

    @Override
    public void removeUpdate(DocumentEvent event) {
        this.checkBtnReady();
    }

    @Override
    public void changedUpdate(DocumentEvent event) {
        this.checkBtnReady();
    }

    protected void closeWindow() {
        this.dispose();
    }

    @Override
    public void checkBtnReady() {

        this.btnReady.setEnabled(!this.textFieldColumnName.getText().isEmpty() && this.table.findColumn(this.textFieldColumnName.getText()) == -1 && this.textFieldColumnName.getText().matches("^[\\p{Alnum}]*$"));
        this.updateToolTipText();

    }

    @Override
    public void updateToolTipText(boolean... conditions) {

        String createColumnButtonToolTipText = "";

        if (this.textFieldColumnName.getText().isEmpty()) {
            createColumnButtonToolTipText = "- A coluna não possui nome";
        } else if (this.table.findColumn(this.textFieldColumnName.getText()) != -1) {
            createColumnButtonToolTipText = "- Duas colunas não podem ter o mesmo nome";
        } else if (!this.textFieldColumnName.getText().matches("^[\\p{Alnum}]*$")) {
            createColumnButtonToolTipText = "- Apenas letras e números podem ser utilizados para o nome da coluna";
        }

        UIManager.put("ToolTip.foreground", Color.RED);

        this.btnReady.setToolTipText(createColumnButtonToolTipText.isEmpty() ? null : createColumnButtonToolTipText);

    }
}
