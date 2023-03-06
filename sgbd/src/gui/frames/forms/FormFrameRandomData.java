package gui.frames.forms;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import entities.Column;
import enums.ColumnDataType;
import net.datafaker.Faker;

@SuppressWarnings("serial")
public class FormFrameRandomData extends JDialog implements ActionListener{
	
	private JPanel contentPane;
	
	private List<Column> columns;
	
	private JComboBox<Object> comboBox; 
	private JButton btnReady;
	private JButton btnCancel;
	
	private List<Component> everyTypeComponents;
	private List<Component> stringComponents;
	private List<Component> intComponents;
	private List<Component> floatComponents;
	private List<Component>charComponents;
	private List<Component> boolComponents;
	
	private ButtonGroup jRadioGroup;
	
	private JRadioButton rdbtnName;
	private JRadioButton rdbtnCPF;
	private JRadioButton rdbtnCNPJ;
	private JRadioButton rdbtnFirstName;
	private JRadioButton rdbtnLastName;
	private JRadioButton rdbtnCity;
	private JRadioButton rdbtnCountry;
	private JRadioButton rdbtnState;
	private JRadioButton rdbtnRandomInteger;
	private JRadioButton rdbtnRandomIntegerDigits;
	private JRadioButton rdbtnRandomFloat;
	
	private JSpinner spinnerRandomIntegerMin;
	private JSpinner spinnerRandomIntegerMax;
	
	private JSpinner spinnerRandomIntegerNDigits;
	
	private JSpinner spinnerRandomFloatDecimals;
	private JSpinner spinnerRandomFloatMin;
	private JSpinner spinnerRandomFloatMax;
	
	private DefaultTableModel model;
	private JTable table;
	private Faker faker;

	public static void main(List<Column> columns, DefaultTableModel model, JTable table) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FormFrameRandomData frame = new FormFrameRandomData(columns, model, table);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public FormFrameRandomData(List<Column> columns, DefaultTableModel model, JTable table) {
		
		super((Window)null);
		setModal(true);
		
		this.model = model;
		this.table = table;
		this.columns = new ArrayList<>(columns);
		this.faker = new Faker(new Locale("pt", "BR"));
				
		initializeGUI();
		
	}

	private void initializeGUI() {
		
		setBounds(100, 100, 1000, 821);
		setLocationRelativeTo(null);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		
		JLabel lblGeradorDeDados = new JLabel("Gerador de dados aleatórios");
		
		List<String> columnsName = new ArrayList<>();
		columns.forEach(x-> columnsName.add(x.getName()));
		
		comboBox = new JComboBox<Object>(columnsName.toArray());
		comboBox.addActionListener(this);
		
		btnReady = new JButton("Pronto");
		btnReady.addActionListener(this);
		
		btnCancel = new JButton("Cancelar");
		btnCancel.addActionListener(this);
		
		everyTypeComponents = new ArrayList<>();
		stringComponents = new ArrayList<>();
		intComponents = new ArrayList<>();
		floatComponents = new ArrayList<>();
		charComponents = new ArrayList<>();
		boolComponents = new ArrayList<>();
		
		jRadioGroup = new ButtonGroup();
		
		rdbtnName = new JRadioButton("Nome Completo");
		createButton(rdbtnName, ColumnDataType.STRING);
		
		rdbtnFirstName = new JRadioButton("Nome");
		createButton(rdbtnFirstName, ColumnDataType.STRING);
		
		rdbtnLastName = new JRadioButton("Sobrenome");
		createButton(rdbtnLastName, ColumnDataType.STRING);
		
		rdbtnCPF = new JRadioButton("CPF");
		createButton(rdbtnCPF, ColumnDataType.STRING);
		
		rdbtnCNPJ = new JRadioButton("CNPJ");
		createButton(rdbtnCNPJ, ColumnDataType.STRING);
		
		rdbtnCity = new JRadioButton("Cidade");
		createButton(rdbtnCity, ColumnDataType.STRING);
		
		rdbtnCountry = new JRadioButton("País");;
		createButton(rdbtnCountry, ColumnDataType.STRING);
		
		rdbtnState = new JRadioButton("Estado");
		createButton(rdbtnState, ColumnDataType.STRING);	
		
		rdbtnRandomInteger = new JRadioButton("Número entre");
		createButton(rdbtnRandomInteger, ColumnDataType.INTEGER);
		spinnerRandomIntegerMax = new JSpinner();
		intComponents.add(spinnerRandomIntegerMax);
		JLabel lblRandomIntegerText_1 = new JLabel("e");
		intComponents.add(lblRandomIntegerText_1);
		spinnerRandomIntegerMin = new JSpinner();
		intComponents.add(spinnerRandomIntegerMin);
		
		rdbtnRandomIntegerDigits = new JRadioButton("Número com");
		createButton(rdbtnRandomIntegerDigits, ColumnDataType.INTEGER);
		spinnerRandomIntegerNDigits = new JSpinner();
		intComponents.add(spinnerRandomIntegerNDigits);
		JLabel lblRandomIntegerDigitsText_1 = new JLabel("digítos");
		intComponents.add(lblRandomIntegerDigitsText_1);
		
		rdbtnRandomFloat = new JRadioButton("Número com");
		createButton(rdbtnRandomFloat, ColumnDataType.FLOAT);
		spinnerRandomFloatDecimals = new JSpinner();
		floatComponents.add(spinnerRandomFloatDecimals);
		JLabel lblRandomFloatText_1 = new JLabel("casa decimais");
		floatComponents.add(lblRandomFloatText_1);
		JLabel lblRandomFloatText_2 = new JLabel("entre");
		floatComponents.add(lblRandomFloatText_2);
		spinnerRandomFloatMin = new JSpinner();
		floatComponents.add(spinnerRandomFloatMin);
		JLabel lblRandomFloatText_3 = new JLabel("e");
		floatComponents.add(lblRandomFloatText_3);
		spinnerRandomFloatMax = new JSpinner();
		floatComponents.add(spinnerRandomFloatMax);
		
		everyTypeComponents.addAll(boolComponents);
		everyTypeComponents.addAll(intComponents);
		everyTypeComponents.addAll(floatComponents);
		everyTypeComponents.addAll(stringComponents);
		everyTypeComponents.addAll(charComponents);
		
		int columnIndex = table.getColumnModel().getColumnIndex(comboBox.getSelectedItem().toString().toUpperCase());
		
		Column selectedColumn = columns.get(columnIndex);
		
		disableButtons(selectedColumn);
		
		JLabel lblStrings = new JLabel("Strings");
		JLabel lblInteiro = new JLabel("Inteiros");
		JLabel lblFloat = new JLabel("Ponto Flutuante");
		
		JSeparator separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setOrientation(SwingConstants.VERTICAL);
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(274)
							.addComponent(lblGeradorDeDados))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(365)
							.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(50)
							.addComponent(lblStrings)
							.addGap(216)
							.addComponent(lblInteiro)
							.addGap(250)
							.addComponent(lblFloat))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addGap(8)
									.addComponent(rdbtnName))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addContainerGap()
									.addComponent(rdbtnFirstName))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addContainerGap()
									.addComponent(rdbtnLastName))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addContainerGap()
									.addComponent(rdbtnCPF))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addContainerGap()
									.addComponent(rdbtnCNPJ))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addContainerGap()
									.addComponent(rdbtnCity))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addContainerGap()
									.addComponent(rdbtnState))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addContainerGap()
									.addComponent(rdbtnCountry)))
							.addGap(26)
							.addComponent(separator, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(rdbtnRandomInteger)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(spinnerRandomIntegerMin, GroupLayout.PREFERRED_SIZE, 81, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(lblRandomIntegerText_1)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(spinnerRandomIntegerMax, GroupLayout.PREFERRED_SIZE, 79, GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(rdbtnRandomIntegerDigits)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(spinnerRandomIntegerNDigits, GroupLayout.PREFERRED_SIZE, 62, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(lblRandomIntegerDigitsText_1)))
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addPreferredGap(ComponentPlacement.RELATED, 274, Short.MAX_VALUE)
									.addComponent(btnCancel)
									.addGap(18)
									.addComponent(btnReady))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addGap(18)
									.addComponent(separator_1, GroupLayout.PREFERRED_SIZE, 2, GroupLayout.PREFERRED_SIZE)
									.addGap(18)
									.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
										.addGroup(gl_contentPane.createSequentialGroup()
											.addComponent(rdbtnRandomFloat)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(spinnerRandomFloatDecimals, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(lblRandomFloatText_1))
										.addGroup(gl_contentPane.createSequentialGroup()
											.addGap(21)
											.addComponent(lblRandomFloatText_2)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(spinnerRandomFloatMin, GroupLayout.PREFERRED_SIZE, 77, GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(lblRandomFloatText_3)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(spinnerRandomFloatMax, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE)))
									.addGap(225)))))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(lblGeradorDeDados)
									.addGap(18)
									.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addGap(12)
									.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
										.addComponent(lblStrings)
										.addComponent(lblInteiro))
									.addGap(18))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(lblFloat)
									.addGap(18)))
							.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
								.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
									.addComponent(btnCancel)
									.addComponent(btnReady))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
										.addGroup(gl_contentPane.createSequentialGroup()
											.addGap(7)
											.addComponent(rdbtnName)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(rdbtnFirstName)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(rdbtnLastName)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(rdbtnCPF)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(rdbtnCNPJ)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(rdbtnCity)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(rdbtnState)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(rdbtnCountry))
										.addGroup(gl_contentPane.createSequentialGroup()
											.addGap(6)
											.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
												.addComponent(separator_1, GroupLayout.PREFERRED_SIZE, 451, GroupLayout.PREFERRED_SIZE)
												.addGroup(gl_contentPane.createSequentialGroup()
													.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
														.addComponent(spinnerRandomIntegerMin, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
														.addComponent(lblRandomIntegerText_1, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
														.addComponent(spinnerRandomIntegerMax, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
														.addComponent(rdbtnRandomInteger))
													.addPreferredGap(ComponentPlacement.RELATED)
													.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
														.addComponent(rdbtnRandomIntegerDigits)
														.addComponent(spinnerRandomIntegerNDigits, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
														.addComponent(lblRandomIntegerDigitsText_1)))
												.addGroup(gl_contentPane.createSequentialGroup()
													.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
														.addComponent(rdbtnRandomFloat)
														.addComponent(spinnerRandomFloatDecimals, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
														.addComponent(lblRandomFloatText_1))
													.addPreferredGap(ComponentPlacement.RELATED)
													.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
														.addComponent(lblRandomFloatText_2)
														.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
															.addComponent(spinnerRandomFloatMin, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
															.addComponent(lblRandomFloatText_3)
															.addComponent(spinnerRandomFloatMax, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))))))
									.addGap(140))))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(102)
							.addComponent(separator, GroupLayout.PREFERRED_SIZE, 451, GroupLayout.PREFERRED_SIZE)))
					.addGap(75))
		);
		contentPane.setLayout(gl_contentPane);
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

	@Override
	public void actionPerformed(ActionEvent e) {
		
		int columnIndex = table.getColumnModel().getColumnIndex(comboBox.getSelectedItem().toString().toUpperCase());
		
		Column selectedColumn = columns.get(columnIndex);
		
		disableButtons(selectedColumn);
		
		if(e.getSource() == btnCancel) {
			
			dispose();
			
		}
		if(e.getSource() == btnReady) {
			
			insertItems(columnIndex, selectedColumn.getType());

			dispose();
			
		}
		
	}
	
	private void disableButtons(Column selectedColumn) {
		
		everyTypeComponents.forEach(c -> c.setEnabled(false));
		
		List<Component> group = null;
		
		if(selectedColumn.getType() == ColumnDataType.NONE) {
			
			group = everyTypeComponents;
			
		}else if(selectedColumn.getType() == ColumnDataType.STRING) {
			
			group = stringComponents;
			
		}else if(selectedColumn.getType() == ColumnDataType.INTEGER) {
			
			group = intComponents;
			
		}else if(selectedColumn.getType() == ColumnDataType.FLOAT) {
			
			group = floatComponents;
			
		}else if(selectedColumn.getType() == ColumnDataType.CHARACTER) {
			
			group = charComponents;
			
		}else if(selectedColumn.getType() == ColumnDataType.BOOLEAN) {
			
			group = boolComponents;
			
		}
		
		if(group != null) {
			
			group.forEach(c -> c.setEnabled(true));
			
		}
			
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
			
			if(rdbtnRandomInteger.isSelected()) {
				
				randomItems = faker.collection(() -> faker.number().numberBetween((int)spinnerRandomIntegerMin.getValue(),
																				  (int)spinnerRandomIntegerMax.getValue())).len(rowsCount).generate();
				
			}else if(rdbtnRandomIntegerDigits.isSelected()) {
				
				randomItems = faker.collection(() -> faker.number().randomNumber((int)spinnerRandomIntegerNDigits.getValue(), true)).len(rowsCount).generate();
				
			}
			
		}
		
		if(type == ColumnDataType.STRING || (type == ColumnDataType.NONE && randomItems == null)) {
			
			if(rdbtnName.isSelected()) {
				
				randomItems = faker.collection(() -> faker.name().fullName()).len(rowsCount).generate();
				
			}else if(rdbtnCPF.isSelected()) {
				
				randomItems = faker.collection(() -> faker.cpf().invalid()).len(rowsCount).generate();	
				
			}else if(rdbtnCNPJ.isSelected()) {

				randomItems = faker.collection(() -> faker.cnpj().invalid()).len(rowsCount).generate();

			}else if(rdbtnFirstName.isSelected()) {
					
				randomItems = faker.collection(() -> faker.name().firstName()).len(rowsCount).generate();
				
			}else if(rdbtnLastName.isSelected()) {
					
				randomItems = faker.collection(() -> faker.name().lastName()).len(rowsCount).generate();
				
			}else if(rdbtnCity.isSelected()) {
				
				randomItems = faker.collection(() -> faker.address().city()).len(rowsCount).generate();
				
			}else if(rdbtnState.isSelected()) {
				
				randomItems = faker.collection(() -> faker.address().state()).len(rowsCount).generate();
				
			}else if(rdbtnCountry.isSelected()) {

				randomItems = faker.collection(() -> faker.address().country()).len(rowsCount).generate();
				
			}			
			
		}
		
		if(type == ColumnDataType.FLOAT || (type == ColumnDataType.NONE && randomItems == null)) {
			
			if(rdbtnRandomFloat.isSelected()) {
				
				randomItems = faker.collection(() -> faker.number().randomDouble((int)spinnerRandomFloatDecimals.getValue(),
																				 (int)spinnerRandomFloatMin.getValue(),
																				 (int)spinnerRandomFloatMax.getValue())).len(rowsCount).generate();
				
			}
			
		}
		
		return randomItems;
		
	}
}
