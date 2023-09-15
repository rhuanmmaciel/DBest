package gui.frames.forms.create;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.List;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import entities.Column;

import enums.ColumnDataType;

import gui.frames.forms.FormBase;

public class FormFrameAddColumn extends FormBase implements ActionListener, DocumentListener {

    private JTextField columnNameTextField;

    private JComboBox<Object> comboBox;

    private final JCheckBox primaryKeyCheckBox = new JCheckBox();

    private final DefaultTableModel table;

    private JButton readyButton1;

    private JButton cancelButton1;

    private JLabel columnNameLabel;

    private final List<Column> columns;

    public FormFrameAddColumn(DefaultTableModel table, List<Column> columns) {
        super(null);

        this.setModal(true);

        this.columns = columns;
        this.table = table;

        this.initializeGUI();
    }

    private void initializeGUI() {
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent event) {
                FormFrameAddColumn.this.closeWindow();
            }
        });

        this.setLocationRelativeTo(null);
        this.setContentPane(this.contentPanel);

        this.columnNameLabel = new JLabel("Nome da coluna");
        this.columnNameTextField = new JTextField();
        this.columnNameTextField.getDocument().addDocumentListener(this);
        this.columnNameTextField.setColumns(10);

        String[] dataTypeOptions = {"None", "Integer", "Float", "Character", "String", "Boolean"};

        this.comboBox = new JComboBox<>(dataTypeOptions);

        this.updateButton();
        this.pack();
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == this.readyButton1) {
            ColumnDataType columnDataType;

            boolean isPrimaryKey = this.primaryKeyCheckBox.isSelected();

            String itemSelected = this.comboBox.getSelectedItem().toString();

            if (Objects.equals(itemSelected, "None")) {
                columnDataType = ColumnDataType.NONE;
            } else if (Objects.equals(itemSelected, "Integer")) {
                columnDataType = ColumnDataType.INTEGER;
            } else if (Objects.equals(itemSelected, "Float")) {
                columnDataType = ColumnDataType.FLOAT;
            } else if (Objects.equals(itemSelected, "String")) {
                columnDataType = ColumnDataType.STRING;
            } else if (Objects.equals(itemSelected, "Character")) {
                columnDataType = ColumnDataType.CHARACTER;
            } else if (Objects.equals(itemSelected, "Boolean")) {
                columnDataType = ColumnDataType.BOOLEAN;
            } else {
                columnDataType = ColumnDataType.NONE;
            }

            this.table.addColumn(this.columnNameTextField.getText().replaceAll("[^\\p{Alnum}]", ""));
            this.closeWindow();
        }

        if (event.getSource() == this.cancelButton1) {
            this.closeWindow();
        }
    }

    @Override
    public void insertUpdate(DocumentEvent event) {
        this.updateButton();
    }

    @Override
    public void removeUpdate(DocumentEvent event) {
        this.updateButton();
    }

    @Override
    public void changedUpdate(DocumentEvent event) {
        this.updateButton();
    }

    private void updateButton() {
        this.readyButton1.setEnabled(!this.columnNameTextField.getText().isEmpty() && this.table.findColumn(this.columnNameTextField.getText()) == -1 && this.columnNameTextField.getText().matches("^[\\p{Alnum}]*$"));
        this.updateToolTipText();
    }

    private void updateToolTipText() {
        String createColumnButtonToolTipText = "";

        if (this.columnNameTextField.getText().isEmpty()) {
            createColumnButtonToolTipText = "- A coluna não possui nome";
        } else if (this.table.findColumn(this.columnNameTextField.getText()) != -1) {
            createColumnButtonToolTipText = "- Duas colunas não podem ter o mesmo nome";
        } else if (!this.columnNameTextField.getText().matches("^[\\p{Alnum}]*$")) {
            createColumnButtonToolTipText = "- Apenas letras e números podem ser utilizados para o nome da coluna";
        }

        UIManager.put("ToolTip.foreground", Color.RED);

        this.readyButton1.setToolTipText(createColumnButtonToolTipText.isEmpty() ? null : createColumnButtonToolTipText);
    }

    protected void closeWindow() {
        this.dispose();
    }
}
