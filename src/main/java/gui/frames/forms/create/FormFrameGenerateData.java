package gui.frames.forms.create;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Label;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import controllers.ConstantController;
import entities.Column;

import enums.ColumnDataType;

import gui.frames.forms.FormBase;
import gui.frames.forms.IFormCondition;
import gui.frames.forms.create.CustomProviders.DBestFaker;

import net.datafaker.Faker;

public class FormFrameGenerateData extends FormBase implements ActionListener, ChangeListener, IFormCondition {

    private final List<Column> columns;

    private final JComboBox<Object> comboBox = new JComboBox<>();

    private JLabel lblColumnType;

    private JTabbedPane tabbedPane;

    private ButtonGroup jRadioGroup;

    private JRadioButton nameStringRadioButton;

    private JRadioButton cpfStringRadioButton;

    private JRadioButton cnpjStringRadioButton;

    private JRadioButton firstNameStringRadioButton;

    private JRadioButton lastNameStringRadioButton;

    private JRadioButton cityStringRadioButton;

    private JRadioButton countryStringRadioButton;

    private JRadioButton stateStringRadioButton;

    private JRadioButton phoneStringRadioButton;

    private JRadioButton jobStringRadioButton;

    private JRadioButton randomIntegerRadioButton;

    private JSpinner randomIntegerMinimumSpinner;

    private JSpinner randomIntegerMaximumSpinner;

    private JRadioButton randomIntegerDigitsRadioButton;

    private JSpinner randomIntegerDigitsSpinner;

    private JRadioButton integerSequenceRadioButton;

    private JSpinner integerSequenceSpinner;

    private JRadioButton randomFloatRadioButton;

    private JSpinner randomFloatDecimalsSpinner;

    private JSpinner randomFloatMinimumSpinner;

    private JSpinner randomFloatMaximumSpinner;

    private JRadioButton randomCharacterRadioButton;

    private JCheckBox upperCaseCharacterCheckBox;

    private JCheckBox lowerCaseCharacterCheckBox;

    private JCheckBox specialCharacterCheckBox;

    private JCheckBox numberCharacterCheckBox;

    private List<JCheckBox> characterCheckBoxes;

    private JRadioButton randomBoolRadioButton;

    private final DefaultTableModel model;

    private final JTable table;

    private final Faker faker;

    private final DBestFaker dbestFaker;

    public FormFrameGenerateData(List<Column> columns, DefaultTableModel model, JTable table) {
        super((Window) null);

        this.setModal(true);

        this.model = model;
        this.table = table;
        this.columns = new ArrayList<>(columns);
        this.faker = new Faker(Locale.of("pt", "BR"));
        this.dbestFaker = new DBestFaker();

        this.initGUI();
    }

    public void initGUI() {

        // componentes do topPane
        List<String> columnsName = new ArrayList<>();
        this.columns.forEach(x -> columnsName.add(x.NAME));

        Arrays.stream(columnsName.toArray()).forEach(this.comboBox::addItem);
        this.comboBox.addActionListener(this);

        Label lblComboBox = new Label(ConstantController.getString("createTable.generateData.columnName"));

        this.lblColumnType = new JLabel();

        JPanel topPane = new JPanel();
        topPane.add(lblComboBox);
        topPane.add(this.comboBox);
        topPane.add(this.lblColumnType);

        // VBox que vai conter o topPane e o jtabbedPane
        Box mainVBox = Box.createVerticalBox();
        mainVBox.setSize(this.getMaximumSize());
        this.getContentPane().add(mainVBox, BorderLayout.NORTH);

        mainVBox.add(topPane);

        this.tabbedPane = new JTabbedPane(SwingConstants.TOP);
        mainVBox.add(this.tabbedPane);

        // criação das telas de cada tipo
        JPanel stringPane = new JPanel();
        this.tabbedPane.addTab("String", null, stringPane, null);

        JPanel intPane = new JPanel();
        this.tabbedPane.addTab("Integer", null, intPane, null);

        JPanel floatPane = new JPanel();
        this.tabbedPane.addTab("Float", null, floatPane, null);

        JPanel charPane = new JPanel();
        this.tabbedPane.addTab("Character", null, charPane, null);

        JPanel boolPane = new JPanel();
        this.tabbedPane.addTab("Boolean", null, boolPane, null);

        this.jRadioGroup = new ButtonGroup();

        intPane.setLayout(new BoxLayout(intPane, BoxLayout.Y_AXIS));
        stringPane.setLayout(new BoxLayout(stringPane, BoxLayout.Y_AXIS));
        floatPane.setLayout(new BoxLayout(floatPane, BoxLayout.Y_AXIS));
        charPane.setLayout(new BoxLayout(charPane, BoxLayout.Y_AXIS));
        boolPane.setLayout(new BoxLayout(boolPane, BoxLayout.Y_AXIS));

        // VBox's que armazenarão cada radioButton para ficarem um abaixo do outro
        Box stringBox = Box.createVerticalBox();
        stringBox.setAlignmentX(LEFT_ALIGNMENT);
        stringPane.add(stringBox);

        Box intBox = Box.createVerticalBox();
        intBox.setAlignmentX(LEFT_ALIGNMENT);
        intPane.add(intBox);

        Box floatBox = Box.createVerticalBox();
        floatBox.setAlignmentX(LEFT_ALIGNMENT);
        floatPane.add(floatBox);

        Box charBox = Box.createVerticalBox();
        charBox.setAlignmentX(LEFT_ALIGNMENT);
        charPane.add(charBox);

        Box boolBox = Box.createVerticalBox();
        boolBox.setAlignmentX(LEFT_ALIGNMENT);
        boolPane.add(boolBox);

        // Strings:
        this.nameStringRadioButton = new JRadioButton(ConstantController.getString("createTable.generateData.fullName"));
        this.createButton(this.nameStringRadioButton);
        stringBox.add(this.nameStringRadioButton);

        this.firstNameStringRadioButton = new JRadioButton(ConstantController.getString("createTable.generateData.name"));
        this.createButton(this.firstNameStringRadioButton);
        stringBox.add(this.firstNameStringRadioButton);

        this.lastNameStringRadioButton = new JRadioButton(ConstantController.getString("createTable.generateData.lastName"));
        this.createButton(this.lastNameStringRadioButton);
        stringBox.add(this.lastNameStringRadioButton);

        this.cpfStringRadioButton = new JRadioButton(ConstantController.getString("createTable.generateData.cpf"));
        this.createButton(this.cpfStringRadioButton);
        stringBox.add(this.cpfStringRadioButton);

        this.cnpjStringRadioButton = new JRadioButton(ConstantController.getString("createTable.generateData.cnpj"));
        this.createButton(this.cnpjStringRadioButton);
        stringBox.add(this.cnpjStringRadioButton);

        this.cityStringRadioButton = new JRadioButton(ConstantController.getString("createTable.generateData.city"));
        this.createButton(this.cityStringRadioButton);
        stringBox.add(this.cityStringRadioButton);

        this.countryStringRadioButton = new JRadioButton(ConstantController.getString("createTable.generateData.country"));
        this.createButton(this.countryStringRadioButton);
        stringBox.add(this.countryStringRadioButton);

        this.stateStringRadioButton = new JRadioButton(ConstantController.getString("createTable.generateData.state"));
        this.createButton(this.stateStringRadioButton);
        stringBox.add(this.stateStringRadioButton);

        this.phoneStringRadioButton = new JRadioButton(ConstantController.getString("createTable.generateData.telephoneNumber"));
        this.createButton(this.phoneStringRadioButton);
        stringBox.add(this.phoneStringRadioButton);

        this.jobStringRadioButton = new JRadioButton(ConstantController.getString("createTable.generateData.workArea"));
        this.createButton(this.jobStringRadioButton);
        stringBox.add(this.jobStringRadioButton);

        // Inteiros:
        Box intItem = Box.createHorizontalBox();
        intItem.setAlignmentX(LEFT_ALIGNMENT);

        this.randomIntegerRadioButton = new JRadioButton(ConstantController.getString("createTable.generateData.numberBetween")+" ");
        this.createButton(this.randomIntegerRadioButton);
        this.randomIntegerMinimumSpinner = new JSpinner();
        this.createSpinner(this.randomIntegerMinimumSpinner);

        JLabel randomIntegerLabel = new JLabel(" "+ConstantController.getString("createTable.generateData.and")+" ");
        this.randomIntegerMaximumSpinner = new JSpinner();
        this.createSpinner(this.randomIntegerMaximumSpinner);

        intItem.add(this.randomIntegerRadioButton);
        intItem.add(this.randomIntegerMinimumSpinner);
        intItem.add(randomIntegerLabel);
        intItem.add(this.randomIntegerMaximumSpinner);

        intBox.add(intItem);

        intItem = Box.createHorizontalBox();
        intItem.setAlignmentX(LEFT_ALIGNMENT);

        this.randomIntegerDigitsRadioButton = new JRadioButton(ConstantController.getString("createTable.generateData.numberWith")+" ");
        this.createButton(this.randomIntegerDigitsRadioButton);
        this.randomIntegerDigitsSpinner = new JSpinner();
        this.createSpinner(this.randomIntegerDigitsSpinner);
        this.randomIntegerDigitsSpinner.setModel(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
        this.randomIntegerDigitsSpinner.setMaximumSize(new Dimension(180, 20));
        JLabel randomIntegerDigitsLabel = new JLabel(" "+ConstantController.getString("createTable.generateData.digits"));

        intItem.add(this.randomIntegerDigitsRadioButton);
        intItem.add(this.randomIntegerDigitsSpinner);
        intItem.add(randomIntegerDigitsLabel);

        intBox.add(intItem);

        intItem = Box.createHorizontalBox();
        intItem.setAlignmentX(LEFT_ALIGNMENT);

        this.integerSequenceRadioButton = new JRadioButton(ConstantController.getString("createTable.generateData.sequenceBeginningWith")+" ");
        this.createButton(this.integerSequenceRadioButton);
        this.integerSequenceSpinner = new JSpinner();
        this.createSpinner(this.integerSequenceSpinner);

        intItem.add(this.integerSequenceRadioButton);
        intItem.add(this.integerSequenceSpinner);

        intBox.add(intItem);

        intBox.add(Box.createVerticalStrut(15));

        // Floats:
        Box floatItem = Box.createHorizontalBox();
        floatItem.setAlignmentX(LEFT_ALIGNMENT);

        this.randomFloatRadioButton = new JRadioButton(ConstantController.getString("createTable.generateData.numberWith")+" ");
        this.createButton(this.randomFloatRadioButton);
        this.randomFloatDecimalsSpinner = new JSpinner();
        this.createSpinner(this.randomFloatDecimalsSpinner);
        JLabel randomFloatLabel1 = new JLabel(" "+ConstantController.getString("createTable.generateData.decimalPlacesBetween")+" ");
        this.randomFloatMinimumSpinner = new JSpinner();
        this.createSpinner(this.randomFloatMinimumSpinner);
        JLabel randomFloatLabel2 = new JLabel(" "+ConstantController.getString("createTable.generateData.and")+" ");
        this.randomFloatMaximumSpinner = new JSpinner();
        this.createSpinner(this.randomFloatMaximumSpinner);

        floatItem.add(this.randomFloatRadioButton);
        floatItem.add(this.randomFloatDecimalsSpinner);
        floatItem.add(randomFloatLabel1);
        floatItem.add(this.randomFloatMinimumSpinner);
        floatItem.add(randomFloatLabel2);
        floatItem.add(this.randomFloatMaximumSpinner);

        floatBox.add(floatItem);
        floatBox.add(Box.createVerticalStrut(15));

        // Caracteres

        Box charItem = Box.createHorizontalBox();
        charItem.setAlignmentX(LEFT_ALIGNMENT);

        this.characterCheckBoxes = new ArrayList<>();

        this.randomCharacterRadioButton = new JRadioButton(ConstantController.getString("createTable.generateData.randomCharacter")+": ");
        this.createButton(this.randomCharacterRadioButton);
        this.upperCaseCharacterCheckBox = new JCheckBox(ConstantController.getString("createTable.generateData.uppercaseLetters")+" ");
        this.createCheckBox(this.upperCaseCharacterCheckBox);
        this.lowerCaseCharacterCheckBox = new JCheckBox(ConstantController.getString("createTable.generateData.lowercaseLetters")+" ");
        this.createCheckBox(this.lowerCaseCharacterCheckBox);
        this.specialCharacterCheckBox = new JCheckBox(ConstantController.getString("createTable.generateData.special")+" ");
        this.createCheckBox(this.specialCharacterCheckBox);
        this.numberCharacterCheckBox = new JCheckBox(ConstantController.getString("createTable.generateData.numbers")+" ");
        this.createCheckBox(this.numberCharacterCheckBox);

        charItem.add(this.randomCharacterRadioButton);
        charItem.add(this.lowerCaseCharacterCheckBox);
        charItem.add(this.upperCaseCharacterCheckBox);
        charItem.add(this.specialCharacterCheckBox);
        charItem.add(this.numberCharacterCheckBox);

        charBox.add(charItem);
        charBox.add(Box.createVerticalStrut(15));

        // Booleanos

        Box boolItem = Box.createHorizontalBox();
        boolItem.setAlignmentX(LEFT_ALIGNMENT);

        this.randomBoolRadioButton = new JRadioButton(ConstantController.getString("createTable.generateData.randomBoolean"));
        this.createButton(this.randomBoolRadioButton);

        boolItem.add(this.randomBoolRadioButton);

        boolBox.add(boolItem);
        boolBox.add(Box.createVerticalStrut(15));

        JPanel bottomPane = new JPanel();
        FlowLayout flowLayout = (FlowLayout) bottomPane.getLayout();
        flowLayout.setAlignment(FlowLayout.RIGHT);
        this.getContentPane().add(bottomPane, BorderLayout.SOUTH);

        bottomPane.add(this.btnCancel);

        bottomPane.add(this.btnReady);

        this.btnCancel.addActionListener(this);
        this.btnReady.addActionListener(this);

        int columnIndex = this.table.getColumnModel().getColumnIndex(Objects.requireNonNull(this.comboBox.getSelectedItem()).toString());

        ColumnDataType selectedColumnType = this.columns.get(columnIndex).DATA_TYPE;

        this.lblColumnType.setText(String.format("%s: %s", ConstantController.getString("createTable.generateData.type"), selectedColumnType.toString()));
        this.setAllowedTabs(selectedColumnType);
        this.checkBtnReady();
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void createCheckBox(JCheckBox check) {
        check.addActionListener(this);
        this.characterCheckBoxes.add(check);
    }

    private void createButton(JRadioButton button) {
        button.addActionListener(this);
        this.jRadioGroup.add(button);
    }

    private void createSpinner(JSpinner spinner) {
        spinner.addChangeListener(this);
        spinner.setMaximumSize(new Dimension(100, 20));
    }

    private void setAllowedTabs(ColumnDataType selectedColumnType) {
        this.tabbedPane.setEnabledAt(0, false);
        this.tabbedPane.setEnabledAt(1, false);
        this.tabbedPane.setEnabledAt(2, false);
        this.tabbedPane.setEnabledAt(3, false);
        this.tabbedPane.setEnabledAt(4, false);

        if (selectedColumnType == ColumnDataType.STRING || selectedColumnType == ColumnDataType.NONE) {
            this.tabbedPane.setSelectedIndex(this.tabbedPane.indexOfTab("String"));
            this.tabbedPane.setEnabledAt(0, true);
            this.tabbedPane.setEnabledAt(1, true);
            this.tabbedPane.setEnabledAt(2, true);
            this.tabbedPane.setEnabledAt(3, true);
            this.tabbedPane.setEnabledAt(4, true);
        } else if (selectedColumnType == ColumnDataType.INTEGER) {
            this.tabbedPane.setSelectedIndex(this.tabbedPane.indexOfTab("Integer"));
            this.tabbedPane.setEnabledAt(1, true);
        } else if (selectedColumnType == ColumnDataType.FLOAT) {
            this.tabbedPane.setSelectedIndex(this.tabbedPane.indexOfTab("Float"));
            this.tabbedPane.setEnabledAt(1, true);
            this.tabbedPane.setEnabledAt(2, true);
        } else if (selectedColumnType == ColumnDataType.CHARACTER) {
            this.tabbedPane.setSelectedIndex(this.tabbedPane.indexOfTab("Character"));
            this.tabbedPane.setEnabledAt(3, true);
        } else if (selectedColumnType == ColumnDataType.BOOLEAN) {
            this.tabbedPane.setSelectedIndex(this.tabbedPane.indexOfTab("Boolean"));
            this.tabbedPane.setEnabledAt(4, true);
        }
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        int columnIndex = this.table
            .getColumnModel()
            .getColumnIndex(Objects.requireNonNull(this.comboBox.getSelectedItem()).toString());

        ColumnDataType selectedColumnType = this.columns.get(columnIndex).DATA_TYPE;

        if (event.getSource() == this.btnCancel) {
            this.dispose();
        }

        if (event.getSource() == this.btnReady) {
            this.insertItems(columnIndex, selectedColumnType);
            this.dispose();
        }

        if (event.getSource() == this.comboBox) {
            this.setAllowedTabs(selectedColumnType);
            this.lblColumnType.setText(String.format("%s: %s", ConstantController.getString("createTable.generateData.type"), selectedColumnType.toString()));
            this.jRadioGroup.clearSelection();
            this.characterCheckBoxes.forEach(x -> x.setSelected(false));
        }

        this.checkBtnReady();
    }

    @Override
    public void stateChanged(ChangeEvent event) {
        this.checkBtnReady();
    }

    private void insertItems(int columnIndex, ColumnDataType type) {
        List<Object> inf = this.getItems(type);

        int rowsCount = this.model.getRowCount();

        for (int i = 0; i < rowsCount; i++) {
            this.model.setValueAt(inf.get(i), i, columnIndex);
        }
    }

    private List<Object> getItems(ColumnDataType type) {
        List<Object> randomItems = null;

        int rowsCount = this.model.getRowCount();

        if (type == ColumnDataType.INTEGER || type == ColumnDataType.NONE || type == ColumnDataType.STRING || type == ColumnDataType.FLOAT) {
            if (this.randomIntegerRadioButton.isSelected()) {
                randomItems = this.faker
                    .collection(() -> this.faker
                        .number()
                        .numberBetween(
                            (int) this.randomIntegerMinimumSpinner.getValue(),
                            (int) this.randomIntegerMaximumSpinner.getValue()
                        )
                    )
                    .len(rowsCount)
                    .generate();
            } else if (this.randomIntegerDigitsRadioButton.isSelected()) {
                randomItems = this.faker
                    .collection(() -> this.faker
                        .number()
                        .randomNumber((int) this.randomIntegerDigitsSpinner.getValue(), true)
                    )
                    .len(rowsCount)
                    .generate();
            } else if (this.integerSequenceRadioButton.isSelected()) {
                randomItems = IntStream
                    .rangeClosed(
                        (int) this.integerSequenceSpinner.getValue(),
                        (int) this.integerSequenceSpinner.getValue() + rowsCount
                    )
                    .boxed()
                    .collect(Collectors.toList());
            }
        }

        if (type == ColumnDataType.STRING || (type == ColumnDataType.NONE && randomItems == null)) {
            if (this.nameStringRadioButton.isSelected()) {
                randomItems = this.faker.collection(() -> this.faker.name().fullName()).len(rowsCount).generate();
            } else if (this.cpfStringRadioButton.isSelected()) {
                randomItems = this.faker.collection(() -> this.faker.cpf().invalid()).len(rowsCount).generate();
            } else if (this.cnpjStringRadioButton.isSelected()) {
                randomItems = this.faker.collection(() -> this.faker.cnpj().invalid()).len(rowsCount).generate();
            } else if (this.firstNameStringRadioButton.isSelected()) {
                randomItems = this.faker.collection(() -> this.faker.name().firstName()).len(rowsCount).generate();
            } else if (this.lastNameStringRadioButton.isSelected()) {
                randomItems = this.faker.collection(() -> this.faker.name().lastName()).len(rowsCount).generate();
            } else if (this.cityStringRadioButton.isSelected()) {
                randomItems = this.faker.collection(() -> this.faker.address().city()).len(rowsCount).generate();
            } else if (this.stateStringRadioButton.isSelected()) {
                randomItems = this.faker.collection(() -> this.faker.address().state()).len(rowsCount).generate();
            } else if (this.countryStringRadioButton.isSelected()) {
                randomItems = this.faker.collection(() -> this.faker.address().country()).len(rowsCount).generate();
            } else if (this.phoneStringRadioButton.isSelected()) {
                randomItems = this.faker.collection(() -> this.faker.phoneNumber().phoneNumber()).len(rowsCount).generate();
            } else if (this.jobStringRadioButton.isSelected()) {
                randomItems = this.faker.collection(() -> this.faker.job().field()).len(rowsCount).generate();
            }
        }

        if (
            type == ColumnDataType.FLOAT ||
            ((type == ColumnDataType.NONE || type == ColumnDataType.STRING) && randomItems == null) &&
            this.randomFloatRadioButton.isSelected()
        ) {
            randomItems = this.faker
                .collection(() -> this.faker
                    .number()
                    .randomDouble(
                        (int) this.randomFloatDecimalsSpinner.getValue(),
                        (int) this.randomFloatMinimumSpinner.getValue(),
                        (int) this.randomFloatMaximumSpinner.getValue()
                    )
                )
                .len(rowsCount)
                .generate();

        }

        if (
            type == ColumnDataType.CHARACTER ||
            ((type == ColumnDataType.NONE || type == ColumnDataType.STRING) && randomItems == null) &&
            this.randomCharacterRadioButton.isSelected()
        ) {
            randomItems = this.dbestFaker
                .collection(() -> this.dbestFaker
                    .character()
                    .anyChar(
                        this.upperCaseCharacterCheckBox.isSelected(),
                        this.lowerCaseCharacterCheckBox.isSelected(),
                        this.specialCharacterCheckBox.isSelected(),
                        this.numberCharacterCheckBox.isSelected()
                    )
                )
                .len(rowsCount)
                .generate();

        }

        if (
            type == ColumnDataType.BOOLEAN ||
            ((type == ColumnDataType.NONE || type == ColumnDataType.STRING) && randomItems == null) &&
            this.randomBoolRadioButton.isSelected()
        ) {
            randomItems = this.faker.collection(() -> this.faker.bool().bool()).len(rowsCount).generate();
        }

        return randomItems;
    }

    @Override
    public void checkBtnReady() {

        boolean isAnyCharCheckBoxSelected = this.randomCharacterRadioButton.isSelected() && this.characterCheckBoxes.stream().anyMatch(AbstractButton::isSelected);
        boolean isAnySelected = this.jRadioGroup.getSelection() != null && !this.randomCharacterRadioButton.isSelected();

        this.btnReady.setEnabled(isAnySelected || isAnyCharCheckBoxSelected);

        this.updateToolTipText(isAnySelected, isAnyCharCheckBoxSelected);

    }

    @Override
    public void updateToolTipText(boolean... conditions) {
        String btnReadyToolTipText = "";

        if (!conditions[0]) {
            btnReadyToolTipText = "- "+ConstantController.getString("createTable.generateData.toolTipText.selectSomeOption");
        }

        if (!conditions[1] && !this.randomCharacterRadioButton.isSelected()) {
            btnReadyToolTipText = "- "+ConstantController.getString("createTable.generateData.toolTipText.noneButtonSelected");
        }

        UIManager.put("ToolTip.foreground", Color.RED);

        this.btnReady.setToolTipText(btnReadyToolTipText.isEmpty() ? null : btnReadyToolTipText);
    }
}
