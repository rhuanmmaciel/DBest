package gui.frames.forms.create;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Label;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import entities.Column;
import enums.ColumnDataType;
import gui.frames.forms.create.CustomProviders.MyCustomFaker;
import net.datafaker.Faker;

@SuppressWarnings("serial")
public class FormFrameCreateData extends JDialog implements ActionListener, ChangeListener{
	
	private List<Column> columns;
	
	private JComboBox<Object> comboBox; 
	private JLabel lblColumnType;
	
	private JButton btnReady;
	private JButton btnCancel;
	
	private JTabbedPane tabbedPane;
	
	private ButtonGroup jRadioGroup;
	
	private JRadioButton rdbtnStringName;
	private JRadioButton rdbtnStringCPF;
	private JRadioButton rdbtnStringCNPJ;
	private JRadioButton rdbtnStringFirstName;
	private JRadioButton rdbtnStringLastName;
	private JRadioButton rdbtnStringCity;
	private JRadioButton rdbtnStringCountry;
	private JRadioButton rdbtnStringState;
	private JRadioButton rdbtnStringPhone;
	private JRadioButton rdbtnStringJob;
	
	private JRadioButton rdbtnIntRandom;
	private JSpinner spinnerIntRandomMin;
	private JSpinner spinnerIntRandomMax;
	private JRadioButton rdbtnIntRandomDigits;
	private JSpinner spinnerIntRandomDigits;
	private JRadioButton rdbtnIntSequence;
	private JSpinner spinnerIntSequence;
	
	private JRadioButton rdbtnFloatRandom;
	private JSpinner spinnerFloatRandomDecimals;
	private JSpinner spinnerFloatRandomMin;
	private JSpinner spinnerFloatRandomMax;
	
	private JRadioButton rdbtnCharRandom;
	private JCheckBox checkBoxCharUppercaseLetter;
	private JCheckBox checkBoxCharLowercaseLetter;
	private JCheckBox checkBoxCharSpecial;
	private JCheckBox checkBoxCharNumbers;
	private List<JCheckBox> charCheckBoxes;
	
	private JRadioButton rdbtnBoolRandom;
	
	private DefaultTableModel model;
	private JTable table;
	private Faker faker;
	private MyCustomFaker myFaker;

	public static void main(List<Column> columns, DefaultTableModel model, JTable table) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FormFrameCreateData frame = new FormFrameCreateData(columns, model, table);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public FormFrameCreateData(List<Column> columns, DefaultTableModel model, JTable table) {
		
		super((Window)null);
		setModal(true);
		
		this.model = model;
		this.table = table;
		this.columns = new ArrayList<>(columns);
		this.faker = new Faker(new Locale("pt", "BR"));
		this.myFaker = new MyCustomFaker();
		
	    initializeGUI();
	}

	private void initializeGUI() {

	    setBounds(100, 100, 1000, 830);
	    setLocationRelativeTo(null);
	    
	    // componentes do topPane
	    List<String> columnsName = new ArrayList<>();
	    columns.forEach(x -> columnsName.add(x.getName()));

	    comboBox = new JComboBox<>(columnsName.toArray());
	    comboBox.addActionListener(this);
	    
	    Label lblComboBox = new Label("Nome da coluna: ");
		
	    lblColumnType = new JLabel();
	    
		JPanel topPane = new JPanel();
		topPane.add(lblComboBox);
		topPane.add(comboBox);
		topPane.add(lblColumnType);
	    
	    // VBox que vai conter o topPane e o jtabbedPane
		Box mainVBox = Box.createVerticalBox();
		mainVBox.setSize(getMaximumSize());
		getContentPane().add(mainVBox, BorderLayout.NORTH);
		
		mainVBox.add(topPane);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		mainVBox.add(tabbedPane);
		
		// criação das telas de cada tipo
		JPanel stringPane = new JPanel();
		tabbedPane.addTab("String", null, stringPane, null);
		
		JPanel intPane = new JPanel();
		tabbedPane.addTab("Inteiro", null, intPane, null);
		
		JPanel floatPane = new JPanel();
		tabbedPane.addTab("Ponto Flutuante", null, floatPane, null);
		
		JPanel charPane = new JPanel();
		tabbedPane.addTab("Caractere", null, charPane, null);
		
		JPanel boolPane = new JPanel();
		tabbedPane.addTab("Booleano", null, boolPane, null);

	    jRadioGroup = new ButtonGroup();
	    
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
		rdbtnStringName = new JRadioButton("Nome Completo");
		createButton(rdbtnStringName);
		stringBox.add(rdbtnStringName);
		
		rdbtnStringFirstName = new JRadioButton("Nome");
		createButton(rdbtnStringFirstName);
		stringBox.add(rdbtnStringFirstName);
		
		rdbtnStringLastName = new JRadioButton("Sobrenome");
		createButton(rdbtnStringLastName);
		stringBox.add(rdbtnStringLastName);
		
		rdbtnStringCPF = new JRadioButton("CPF");
		createButton(rdbtnStringCPF);
		stringBox.add(rdbtnStringCPF);
		
		rdbtnStringCNPJ = new JRadioButton("CNPJ");
		createButton(rdbtnStringCNPJ);
		stringBox.add(rdbtnStringCNPJ);
		
		rdbtnStringCity = new JRadioButton("Cidade");
		createButton(rdbtnStringCity);
		stringBox.add(rdbtnStringCity);
		
		rdbtnStringCountry = new JRadioButton("País");
		createButton(rdbtnStringCountry);
		stringBox.add(rdbtnStringCountry);
		
		rdbtnStringState = new JRadioButton("Estado");
		createButton(rdbtnStringState);	
		stringBox.add(rdbtnStringState);
		
		rdbtnStringPhone = new JRadioButton("Número de telefone");
		createButton(rdbtnStringPhone);	
		stringBox.add(rdbtnStringPhone);
		
		rdbtnStringJob = new JRadioButton("Área de atuação");
		createButton(rdbtnStringJob);
		stringBox.add(rdbtnStringJob);

	    // Inteiros:
		Box intItem = Box.createHorizontalBox();
		intItem.setAlignmentX(LEFT_ALIGNMENT);
		
	    rdbtnIntRandom = new JRadioButton("Número entre ");
	    createButton(rdbtnIntRandom);
	    spinnerIntRandomMin = new JSpinner();
		createSpinner(spinnerIntRandomMin);
	    JLabel lblRandomIntegerText_1 = new JLabel(" e ");
	    spinnerIntRandomMax = new JSpinner();
		createSpinner(spinnerIntRandomMax);
	    
	    intItem.add(rdbtnIntRandom);
	    intItem.add(spinnerIntRandomMin);
	    intItem.add(lblRandomIntegerText_1);
	    intItem.add(spinnerIntRandomMax);
	    
	    intBox.add(intItem);
	    
	    
	    intItem = Box.createHorizontalBox();
	    intItem.setAlignmentX(LEFT_ALIGNMENT);
	    
		rdbtnIntRandomDigits = new JRadioButton("Número com ");
		createButton(rdbtnIntRandomDigits);
		spinnerIntRandomDigits = new JSpinner();
		createSpinner(spinnerIntRandomDigits);
		JLabel lblRandomIntegerDigitsText_1 = new JLabel(" digítos");
		
		intItem.add(rdbtnIntRandomDigits);
		intItem.add(spinnerIntRandomDigits);
		intItem.add(lblRandomIntegerDigitsText_1);
		
	    intBox.add(intItem);
	    
	    
	    intItem = Box.createHorizontalBox();
	    intItem.setAlignmentX(LEFT_ALIGNMENT);
	    
		rdbtnIntSequence = new JRadioButton("Sequência com início em ");
		createButton(rdbtnIntSequence);
		spinnerIntSequence = new JSpinner();
		createSpinner(spinnerIntSequence);
		
		intItem.add(rdbtnIntSequence);
		intItem.add(spinnerIntSequence);
		
	    intBox.add(intItem);
		
	    intBox.add(Box.createVerticalStrut(15));
	    
		// Floats:
		Box floatItem = Box.createHorizontalBox();
		floatItem.setAlignmentX(LEFT_ALIGNMENT);
	    
		rdbtnFloatRandom = new JRadioButton("Número com ");
		createButton(rdbtnFloatRandom);
		spinnerFloatRandomDecimals = new JSpinner();
		createSpinner(spinnerFloatRandomDecimals);
		JLabel lblRandomFloatText_1 = new JLabel(" casa decimais entre ");
		spinnerFloatRandomMin = new JSpinner();
		createSpinner(spinnerFloatRandomMin);
		JLabel lblRandomFloatText_2 = new JLabel(" e ");
		spinnerFloatRandomMax = new JSpinner();
		createSpinner(spinnerFloatRandomMax);
		
		floatItem.add(rdbtnFloatRandom);
		floatItem.add(spinnerFloatRandomDecimals);
		floatItem.add(lblRandomFloatText_1);
		floatItem.add(spinnerFloatRandomMin);
		floatItem.add(lblRandomFloatText_2);
		floatItem.add(spinnerFloatRandomMax);
		
		floatBox.add(floatItem);
		floatBox.add(Box.createVerticalStrut(15));
		
		// Caracteres
		
		Box charItem = Box.createHorizontalBox();
		charItem.setAlignmentX(LEFT_ALIGNMENT);
		
		charCheckBoxes = new ArrayList<>();
		
		rdbtnCharRandom = new JRadioButton("Caractere aleatório: ");
		createButton(rdbtnCharRandom);
		checkBoxCharUppercaseLetter = new JCheckBox("Letras maiúsculas ");
		createCheckBox(checkBoxCharUppercaseLetter);
		checkBoxCharLowercaseLetter = new JCheckBox("Letras minúsculas ");
		createCheckBox(checkBoxCharLowercaseLetter);
		checkBoxCharSpecial= new JCheckBox("Especiais ");
		createCheckBox(checkBoxCharSpecial);
		checkBoxCharNumbers= new JCheckBox("Números ");
		createCheckBox(checkBoxCharNumbers);
		
		charItem.add(rdbtnCharRandom);
		charItem.add(checkBoxCharLowercaseLetter);
		charItem.add(checkBoxCharUppercaseLetter);
		charItem.add(checkBoxCharSpecial);
		charItem.add(checkBoxCharNumbers);
		
		charBox.add(charItem);
		charBox.add(Box.createVerticalStrut(15));
		
		
		// Booleanos
		
		Box boolItem = Box.createHorizontalBox();
		boolItem.setAlignmentX(LEFT_ALIGNMENT);
		
		rdbtnBoolRandom = new JRadioButton("Booleano aleatório");
		createButton(rdbtnBoolRandom);
		
		boolItem.add(rdbtnBoolRandom);
		
		boolBox.add(boolItem);
		boolBox.add(Box.createVerticalStrut(15));
		
		JPanel bottomPane = new JPanel();
		FlowLayout flowLayout = (FlowLayout) bottomPane.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		getContentPane().add(bottomPane, BorderLayout.SOUTH);
		
	    btnCancel = new JButton("Cancelar");
	    bottomPane.add(btnCancel);
	    
	    btnReady = new JButton("Pronto");
	    bottomPane.add(btnReady);

	    btnCancel.addActionListener(this);
	    btnReady.addActionListener(this);
		
		int columnIndex = table.getColumnModel().getColumnIndex(comboBox.getSelectedItem().toString());
		
		ColumnDataType selectedColumnType = columns.get(columnIndex).getType();
		
		lblColumnType.setText("Tipo: " + selectedColumnType.toString());
		setAllowedTabs(selectedColumnType);
		verifyReady(); 
		this.setVisible(true);
	}
	
	private void createCheckBox(JCheckBox check) {
		
		check.addActionListener(this);
		charCheckBoxes.add(check);
		
	}
	
	private void createButton(JRadioButton button) {
		
		button.addActionListener(this);
		jRadioGroup.add(button);
		
	}
	
	private void createSpinner(JSpinner spinner) {
		
		spinner.addChangeListener(this);
		spinner.setMaximumSize(new Dimension(100, 20));
		
	}
	
	private void setAllowedTabs(ColumnDataType selectedColumnType) {
			
		tabbedPane.setEnabledAt(0, false);
		tabbedPane.setEnabledAt(1, false);
		tabbedPane.setEnabledAt(2, false);
		tabbedPane.setEnabledAt(3, false);
		tabbedPane.setEnabledAt(4, false);
		
		if(selectedColumnType == ColumnDataType.STRING || selectedColumnType == ColumnDataType.NONE) {
			
			tabbedPane.setSelectedIndex(tabbedPane.indexOfTab("String"));			
			tabbedPane.setEnabledAt(0, true);
			tabbedPane.setEnabledAt(1, true);
			tabbedPane.setEnabledAt(2, true);
			tabbedPane.setEnabledAt(3, true);
			tabbedPane.setEnabledAt(4, true);
			
		}else if(selectedColumnType == ColumnDataType.INTEGER) {
			
			tabbedPane.setSelectedIndex(tabbedPane.indexOfTab("Inteiro"));
			tabbedPane.setEnabledAt(1, true);
			
		}else if(selectedColumnType == ColumnDataType.FLOAT) {
			
			tabbedPane.setSelectedIndex(tabbedPane.indexOfTab("Ponto Flutuante"));
			tabbedPane.setEnabledAt(1, true);
			tabbedPane.setEnabledAt(2, true);
			
		}else if(selectedColumnType == ColumnDataType.CHARACTER) {
			
			tabbedPane.setSelectedIndex(tabbedPane.indexOfTab("Caractere"));
			tabbedPane.setEnabledAt(3, true);
			
		}else if(selectedColumnType == ColumnDataType.BOOLEAN) {
			
			tabbedPane.setSelectedIndex(tabbedPane.indexOfTab("Booleano"));
			tabbedPane.setEnabledAt(4, true);
			
		}
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		int columnIndex = table.getColumnModel().getColumnIndex(comboBox.getSelectedItem().toString());
		
		ColumnDataType selectedColumnType = columns.get(columnIndex).getType();
		
		if(e.getSource() == btnCancel) {
			
			dispose();
			
		}
		if(e.getSource() == btnReady) {
			
			insertItems(columnIndex, selectedColumnType);

			dispose();
			
		}
		if(e.getSource() == comboBox) {
			
			setAllowedTabs(selectedColumnType);
			lblColumnType.setText("Tipo: " + selectedColumnType.toString());
			jRadioGroup.clearSelection();
			charCheckBoxes.forEach(x -> x.setSelected(false));
			
		}
		
		verifyReady();
		
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {

		verifyReady();
		
	}
	
	private void verifyReady() {
		
		boolean isAnyCharCheckBoxSelected = rdbtnCharRandom.isSelected() && charCheckBoxes.stream().anyMatch(x -> x.isSelected());
		boolean isAnySelected = jRadioGroup.getSelection() != null && !rdbtnCharRandom.isSelected();

		btnReady.setEnabled(isAnySelected || isAnyCharCheckBoxSelected);
			
		updateToolTipText(isAnySelected, isAnyCharCheckBoxSelected);
		
	}
	
	private void updateToolTipText(boolean isAnySelected, boolean isAnyCharCheckBoxSelected) {
		
		String btnReadyToolTipText = new String();
		
		if(!isAnyCharCheckBoxSelected) {
			
			btnReadyToolTipText = "- Selecione alguma check box";
			
		}
		if(!isAnySelected && !rdbtnCharRandom.isSelected()) {
			
			btnReadyToolTipText = "- Nenhum botão selecionado";
			
		}

		UIManager.put("ToolTip.foreground", Color.RED);
		
		btnReady.setToolTipText(btnReadyToolTipText.isEmpty() ? null : btnReadyToolTipText);
		
	}
	
	private void insertItems(int columnIndex, ColumnDataType type) {
		
		List<Object> inf = getItems(type);
		int rowsCount = model.getRowCount();
		
		for(int i = 0; i < rowsCount; i++) {
			
			model.setValueAt(inf.get(i), i, columnIndex);
		
		}
		
	}
	
	private List<Object> getItems(ColumnDataType type){
		
		List<Object> randomItems = null;
		int rowsCount = model.getRowCount();
		
		if(type == ColumnDataType.INTEGER || type == ColumnDataType.NONE) {
			
			if(rdbtnIntRandom.isSelected()) {
				
				randomItems = faker.collection(() -> faker.number().numberBetween((int)spinnerIntRandomMin.getValue(),
																				  (int)spinnerIntRandomMax.getValue())).len(rowsCount).generate();
				
			}else if(rdbtnIntRandomDigits.isSelected()) {
				
				randomItems = faker.collection(() -> faker.number().randomNumber((int)spinnerIntRandomDigits.getValue(), true)).len(rowsCount).generate();
				
			}else if(rdbtnIntSequence.isSelected()) {
				
				randomItems = IntStream.rangeClosed((int)spinnerIntSequence.getValue(), (int)spinnerIntSequence.getValue() + rowsCount).boxed().collect(Collectors.toList());
				
			}
			
		}
		
		if(type == ColumnDataType.STRING || (type == ColumnDataType.NONE && randomItems == null)) {
			
			if(rdbtnStringName.isSelected()) {
				
				randomItems = faker.collection(() -> faker.name().fullName()).len(rowsCount).generate();
				
			}else if(rdbtnStringCPF.isSelected()) {
				
				randomItems = faker.collection(() -> faker.cpf().invalid()).len(rowsCount).generate();	
				
			}else if(rdbtnStringCNPJ.isSelected()) {

				randomItems = faker.collection(() -> faker.cnpj().invalid()).len(rowsCount).generate();

			}else if(rdbtnStringFirstName.isSelected()) {
					
				randomItems = faker.collection(() -> faker.name().firstName()).len(rowsCount).generate();
				
			}else if(rdbtnStringLastName.isSelected()) {
					
				randomItems = faker.collection(() -> faker.name().lastName()).len(rowsCount).generate();
				
			}else if(rdbtnStringCity.isSelected()) {
				
				randomItems = faker.collection(() -> faker.address().city()).len(rowsCount).generate();
				
			}else if(rdbtnStringState.isSelected()) {
				
				randomItems = faker.collection(() -> faker.address().state()).len(rowsCount).generate();
				
			}else if(rdbtnStringCountry.isSelected()) {

				randomItems = faker.collection(() -> faker.address().country()).len(rowsCount).generate();
				
			}else if(rdbtnStringPhone.isSelected()) {
				
				randomItems = faker.collection(() -> faker.phoneNumber().phoneNumber()).len(rowsCount).generate();
				
			}else if(rdbtnStringJob.isSelected()) {
				
				randomItems = faker.collection(() -> faker.job().field()).len(rowsCount).generate();;
				
			}
			
		}
		
		if(type == ColumnDataType.FLOAT || (type == ColumnDataType.NONE && randomItems == null)) {
			
			if(rdbtnFloatRandom.isSelected()) {
				
				randomItems = faker.collection(() -> faker.number().randomDouble((int)spinnerFloatRandomDecimals.getValue(),
																				 (int)spinnerFloatRandomMin.getValue(),
																				 (int)spinnerFloatRandomMax.getValue())).len(rowsCount).generate();
				
			}
			
		}
		
		if(type == ColumnDataType.CHARACTER || (type == ColumnDataType.NONE && randomItems == null)) {
			
			if(rdbtnCharRandom.isSelected()) {
				
				randomItems = myFaker.collection(() -> myFaker.character().anyChar(checkBoxCharUppercaseLetter.isSelected(),
																				   checkBoxCharLowercaseLetter.isSelected(),
																				   checkBoxCharSpecial.isSelected(),
																				   checkBoxCharNumbers.isSelected())).len(rowsCount).generate();
				
			}
			
		}
		
		if(type == ColumnDataType.BOOLEAN || (type == ColumnDataType.NONE && randomItems == null)) {
		
			if(rdbtnBoolRandom.isSelected()) {
				
				randomItems = faker.collection(() -> faker.bool().bool()).len(rowsCount).generate();
				
			}
			
		}
		
		return randomItems;
		
	}

}
