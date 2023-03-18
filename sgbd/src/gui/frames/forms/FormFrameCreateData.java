package gui.frames.forms;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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
import net.datafaker.Faker;

@SuppressWarnings("serial")
public class FormFrameCreateData extends JDialog implements ActionListener, ChangeListener{
	
	private List<Column> columns;
	
	private JComboBox<Object> comboBox; 
	private JLabel lblColumnType;
	
	private JButton btnReady;
	private JButton btnCancel;
	
	private JTabbedPane tabbedPane;
	
	private List<Component> everyTypeComponents;
	private List<Component> stringComponents;
	private List<Component> intComponents;
	private List<Component> floatComponents;
	private List<Component>charComponents;
	private List<Component> boolComponents;
	
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
	private JRadioButton rdbtnIntRandomDigits;
	private JRadioButton rdbtnIntSequence;
	private JRadioButton rdbtnFloatRandom;
	
	private JSpinner spinnerIntRandomMin;
	private JSpinner spinnerIntRandomMax;
	
	private JSpinner spinnerIntRandomDigits;
	
	private JSpinner spinnerIntSequence;
	
	private JSpinner spinnerFloatRandomDecimals;
	private JSpinner spinnerFloatRandomMin;
	private JSpinner spinnerFloatRandomMax;
	
	private DefaultTableModel model;
	private JTable table;
	private Faker faker;

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

	    // listas que armazenarão os componentes de cada tipo para poder desabilitá-los todos juntos
	    everyTypeComponents = new ArrayList<>();
	    stringComponents = new ArrayList<>();
	    intComponents = new ArrayList<>();
	    floatComponents = new ArrayList<>();
	    charComponents = new ArrayList<>();
	    boolComponents = new ArrayList<>();

	    jRadioGroup = new ButtonGroup();
	    
	    intPane.setLayout(new BoxLayout(intPane, BoxLayout.Y_AXIS));
		stringPane.setLayout(new BoxLayout(stringPane, BoxLayout.Y_AXIS));
		floatPane.setLayout(new BoxLayout(floatPane, BoxLayout.Y_AXIS));
						
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
		
		// Strings:
		rdbtnStringName = new JRadioButton("Nome Completo");
		createButton(rdbtnStringName, ColumnDataType.STRING);
		stringBox.add(rdbtnStringName);
		
		rdbtnStringFirstName = new JRadioButton("Nome");
		createButton(rdbtnStringFirstName, ColumnDataType.STRING);
		stringBox.add(rdbtnStringFirstName);
		
		rdbtnStringLastName = new JRadioButton("Sobrenome");
		createButton(rdbtnStringLastName, ColumnDataType.STRING);
		stringBox.add(rdbtnStringLastName);
		
		rdbtnStringCPF = new JRadioButton("CPF");
		createButton(rdbtnStringCPF, ColumnDataType.STRING);
		stringBox.add(rdbtnStringCPF);
		
		rdbtnStringCNPJ = new JRadioButton("CNPJ");
		createButton(rdbtnStringCNPJ, ColumnDataType.STRING);
		stringBox.add(rdbtnStringCNPJ);
		
		rdbtnStringCity = new JRadioButton("Cidade");
		createButton(rdbtnStringCity, ColumnDataType.STRING);
		stringBox.add(rdbtnStringCity);
		
		rdbtnStringCountry = new JRadioButton("País");
		createButton(rdbtnStringCountry, ColumnDataType.STRING);
		stringBox.add(rdbtnStringCountry);
		
		rdbtnStringState = new JRadioButton("Estado");
		createButton(rdbtnStringState, ColumnDataType.STRING);	
		stringBox.add(rdbtnStringState);
		
		rdbtnStringPhone = new JRadioButton("Número de telefone");
		createButton(rdbtnStringPhone, ColumnDataType.STRING);	
		stringBox.add(rdbtnStringPhone);
		
		rdbtnStringJob = new JRadioButton("Área de atuação");
		createButton(rdbtnStringJob, ColumnDataType.STRING);
		stringBox.add(rdbtnStringJob);

	    // Inteiros:
		Box intItem = Box.createHorizontalBox();
		intItem.setAlignmentX(LEFT_ALIGNMENT);
		
	    rdbtnIntRandom = new JRadioButton("Número entre ");
	    createButton(rdbtnIntRandom, ColumnDataType.INTEGER);
	    spinnerIntRandomMin = new JSpinner();
		createSpinner(spinnerIntRandomMin, ColumnDataType.INTEGER);
	    JLabel lblRandomIntegerText_1 = new JLabel(" e ");
	    spinnerIntRandomMax = new JSpinner();
		createSpinner(spinnerIntRandomMax, ColumnDataType.INTEGER);
	    intComponents.add(lblRandomIntegerText_1);
	    
	    intItem.add(rdbtnIntRandom);
	    intItem.add(spinnerIntRandomMin);
	    intItem.add(lblRandomIntegerText_1);
	    intItem.add(spinnerIntRandomMax);
	    
	    intBox.add(intItem);
	    
	    
	    intItem = Box.createHorizontalBox();
	    intItem.setAlignmentX(LEFT_ALIGNMENT);
	    
		rdbtnIntRandomDigits = new JRadioButton("Número com ");
		createButton(rdbtnIntRandomDigits, ColumnDataType.INTEGER);
		spinnerIntRandomDigits = new JSpinner();
		createSpinner(spinnerIntRandomDigits, ColumnDataType.INTEGER);
		JLabel lblRandomIntegerDigitsText_1 = new JLabel(" digítos");
		intComponents.add(lblRandomIntegerDigitsText_1);
		
		intItem.add(rdbtnIntRandomDigits);
		intItem.add(spinnerIntRandomDigits);
		intItem.add(lblRandomIntegerDigitsText_1);
		
	    intBox.add(intItem);
	    
	    
	    intItem = Box.createHorizontalBox();
	    intItem.setAlignmentX(LEFT_ALIGNMENT);
	    
		rdbtnIntSequence = new JRadioButton("Sequência com início em ");
		createButton(rdbtnIntSequence, ColumnDataType.INTEGER);
		spinnerIntSequence = new JSpinner();
		createSpinner(spinnerIntSequence, ColumnDataType.INTEGER);
		
		intItem.add(rdbtnIntSequence);
		intItem.add(spinnerIntSequence);
		
	    intBox.add(intItem);
		
	    
		// Floats:
		Box floatItem = Box.createHorizontalBox();
		floatItem.setAlignmentX(LEFT_ALIGNMENT);
	    
		rdbtnFloatRandom = new JRadioButton("Número com ");
		createButton(rdbtnFloatRandom, ColumnDataType.FLOAT);
		spinnerFloatRandomDecimals = new JSpinner();
		createSpinner(spinnerFloatRandomDecimals, ColumnDataType.FLOAT);
		JLabel lblRandomFloatText_1 = new JLabel(" casa decimais entre ");
		floatComponents.add(lblRandomFloatText_1);
		spinnerFloatRandomMin = new JSpinner();
		createSpinner(spinnerFloatRandomMin, ColumnDataType.FLOAT);
		JLabel lblRandomFloatText_2 = new JLabel(" e ");
		floatComponents.add(lblRandomFloatText_2);
		spinnerFloatRandomMax = new JSpinner();
		createSpinner(spinnerFloatRandomMax, ColumnDataType.FLOAT);
		
		floatItem.add(rdbtnFloatRandom);
		floatItem.add(spinnerFloatRandomDecimals);
		floatItem.add(lblRandomFloatText_1);
		floatItem.add(spinnerFloatRandomMin);
		floatItem.add(lblRandomFloatText_2);
		floatItem.add(spinnerFloatRandomMax);
		
		floatBox.add(floatItem);
		
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
	    
		everyTypeComponents.addAll(boolComponents);
		everyTypeComponents.addAll(intComponents);
		everyTypeComponents.addAll(floatComponents);
		everyTypeComponents.addAll(stringComponents);
		everyTypeComponents.addAll(charComponents);
		
		int columnIndex = table.getColumnModel().getColumnIndex(comboBox.getSelectedItem().toString().toUpperCase());
		
		ColumnDataType selectedColumnType = columns.get(columnIndex).getType();
		
		lblColumnType.setText("Tipo: " + selectedColumnType.toString());
		setAllowedTabs(selectedColumnType);
		verifyReady(); 
		this.setVisible(true);
	}
	
	private void createButton(JRadioButton button, ColumnDataType type) {
		
		button.addActionListener(this);
		jRadioGroup.add(button);
		
		if(type == ColumnDataType.STRING) {
		
			stringComponents.add(button);
		
		}else if(type == ColumnDataType.INTEGER) {
			
			intComponents.add(button);
			
		}else if(type == ColumnDataType.FLOAT) {
			
			floatComponents.add(button);
			
		}else if(type == ColumnDataType.CHARACTER) {
			
			charComponents.add(button);
			
		}else if(type == ColumnDataType.BOOLEAN) {
			
			boolComponents.add(button);
			
		}
		
	}
	
	private void createSpinner(JSpinner spinner, ColumnDataType type) {
		
		spinner.addChangeListener(this);
		spinner.setMaximumSize(new Dimension(50, 20));
		
		if(type == ColumnDataType.STRING) {
		
			stringComponents.add(spinner);
		
		}else if(type == ColumnDataType.INTEGER) {
			
			intComponents.add(spinner);
			
		}else if(type == ColumnDataType.FLOAT) {
			
			floatComponents.add(spinner);
			
		}else if(type == ColumnDataType.CHARACTER) {
			
			charComponents.add(spinner);
			
		}else if(type == ColumnDataType.BOOLEAN) {
			
			boolComponents.add(spinner);
			
		}	
		
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
		
		int columnIndex = table.getColumnModel().getColumnIndex(comboBox.getSelectedItem().toString().toUpperCase());
		
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
			
		}
		
		verifyReady();
		
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {

		verifyReady();
		
	}
	
	private void verifyReady() {
		
		boolean isAnySelected = jRadioGroup.getSelection() != null;
		
		btnReady.setEnabled(isAnySelected);
			
		updateToolTipText(isAnySelected);
		
	}
	
	private void updateToolTipText(boolean isAnySelected) {
		
		String btnReadyToolTipText = new String();
		
		if(!isAnySelected) {
			
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
		
		return randomItems;
		
	}

}
