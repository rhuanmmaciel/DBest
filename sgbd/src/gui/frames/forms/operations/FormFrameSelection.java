package gui.frames.forms.operations;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.NumberFormatter;

import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

import entities.Cell;
import entities.OperatorCell;
import enums.ColumnDataType;
import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;
import sgbd.query.Operator;
import sgbd.query.Tuple;
import sgbd.query.unaryop.FilterOperator;


@SuppressWarnings("serial")
public class FormFrameSelection extends JFrame implements ActionListener, DocumentListener{

    private List<String> columnsList;

    private JTextArea textArea;

    private JButton btnColumnAdd;
    private JButton btnOperatorAdd;
    private JButton btnLogicalOperatorAdd;
    private JButton btnNumberAdd;
    private JButton btnStringAdd;
    private JButton btnRemoveLastOne;
    private JButton btnRemoveAll;

    private JComboBox<List<String>> comboBoxColumns;
    private JComboBox<List<String>> comboBoxOperator;
    private JComboBox<List<String>> comboBoxLogicalOperator;
    private JFormattedTextField formattedTextFieldNumber;
    private JTextField textFieldString;

    private JButton btnReady;
    private JButton btnCancel;

    private Cell cell;
    private Cell parentCell;

    private Evaluator evaluator;

    private Object jCell;
    private mxGraph graph;
    private JPanel panel;

    public static void main(Object cell, List<Cell> cells,mxGraph graph) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    FormFrameSelection frame = new FormFrameSelection(cell, cells,graph);
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public FormFrameSelection(Object cell, List<Cell> cells, mxGraph graph) {

        this.setVisible(true);

        this.cell = cells.stream().filter(x -> x.getCell().equals(((mxCell)cell))).findFirst().orElse(null);
        parentCell = this.cell.getParents().get(0);
        this.jCell = cell;
        this.graph = graph;

        this.evaluator = new Evaluator();

        initializeGUI();

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void initializeGUI() {

        setBounds(100, 100, 800, 400);
        setLocationRelativeTo(null);

        JPanel centerPane = new JPanel();
        getContentPane().add(centerPane, BorderLayout.CENTER);

        textArea = new JTextArea();
        textArea.getDocument().addDocumentListener(this);;
        textArea.setMaximumSize(new Dimension(750, 50));
        textArea.setEditable(false);
        centerPane.add(textArea);

        Dimension buttonsDimension = new Dimension(350, 25);

        Box operatorsHBox = Box.createHorizontalBox();
        operatorsHBox.setMaximumSize(buttonsDimension);
        operatorsHBox.setAlignmentX(LEFT_ALIGNMENT);

        Box columnsHBox = Box.createHorizontalBox();
        columnsHBox.setMaximumSize(buttonsDimension);
        columnsHBox.setAlignmentX(LEFT_ALIGNMENT);

        Box logicalOperatorsHBox = Box.createHorizontalBox();
        logicalOperatorsHBox.setMaximumSize(buttonsDimension);
        logicalOperatorsHBox.setAlignmentX(LEFT_ALIGNMENT);

        Box numberHBox = Box.createHorizontalBox();
        numberHBox.setMaximumSize(buttonsDimension);
        numberHBox.setAlignmentX(LEFT_ALIGNMENT);

        Box stringHBox = Box.createHorizontalBox();
        stringHBox.setMaximumSize(buttonsDimension);
        stringHBox.setAlignmentX(LEFT_ALIGNMENT);

        Box removeHBox = Box.createHorizontalBox();
        removeHBox.setMaximumSize(buttonsDimension);
        removeHBox.setAlignmentX(LEFT_ALIGNMENT);

        // HBox das operações

        JLabel lblOperators = new JLabel("Operações: ");
        operatorsHBox.add(lblOperators);

        String operators[] = {">","<","=","≠","≥", "≤", "(", ")"};
        comboBoxOperator = new JComboBox(operators);
        operatorsHBox.add(comboBoxOperator);

        btnOperatorAdd = new JButton("Add");
        btnOperatorAdd.addActionListener(this);
        operatorsHBox.add(Box.createHorizontalStrut(5));
        operatorsHBox.add(btnOperatorAdd);

        // HBox das colunas

        JLabel lblColumns = new JLabel("Colunas: ");
        columnsHBox.add(lblColumns);

        columnsList = new ArrayList<String>();
        columnsList = parentCell.getColumnsName();
        comboBoxColumns = new JComboBox(columnsList.toArray(new String[0]));
        columnsHBox.add(comboBoxColumns);

        btnColumnAdd = new JButton("Add");
        btnColumnAdd.addActionListener(this);
        columnsHBox.add(Box.createHorizontalStrut(5));
        columnsHBox.add(btnColumnAdd);

        // HBox dos operadores lógicos

        JLabel lblLogicalOperators = new JLabel("Operadores Lógicos: ");
        logicalOperatorsHBox.add(lblLogicalOperators);

        String logicalOperators[] = {"AND", "OR"};
        comboBoxLogicalOperator = new JComboBox(logicalOperators);
        logicalOperatorsHBox.add(comboBoxLogicalOperator);

        btnLogicalOperatorAdd = new JButton("Add");
        btnLogicalOperatorAdd.addActionListener(this);
        logicalOperatorsHBox.add(Box.createHorizontalStrut(5));
        logicalOperatorsHBox.add(btnLogicalOperatorAdd);

        // HBox dos números

        JLabel lblNumber = new JLabel("Números: ");
        numberHBox.add(lblNumber);

        DecimalFormat decimalFormat = new DecimalFormat("#.###");
        decimalFormat.setMaximumFractionDigits(5);
        NumberFormatter numberFormatter = new NumberFormatter(decimalFormat);
        formattedTextFieldNumber = new JFormattedTextField(numberFormatter);
        numberHBox.add(formattedTextFieldNumber);

        btnNumberAdd = new JButton("Add");
        btnNumberAdd.addActionListener(this);
        numberHBox.add(Box.createHorizontalStrut(5));
        numberHBox.add(btnNumberAdd);

        // HBox das strings

        JLabel lblString = new JLabel("Strings: ");
        stringHBox.add(lblString);

        textFieldString = new JTextField();
        stringHBox.add(textFieldString);

        btnStringAdd = new JButton("Add");
        btnStringAdd.addActionListener(this);
        stringHBox.add(Box.createHorizontalStrut(5));
        stringHBox.add(btnStringAdd);

        // HBox para remover

        btnRemoveLastOne = new JButton("Apagar último inserido");
        btnRemoveLastOne.addActionListener(this);
        removeHBox.add(btnRemoveLastOne);

        btnRemoveAll = new JButton("Apagar tudo");
        btnRemoveAll.addActionListener(this);
        removeHBox.add(Box.createHorizontalStrut(5));
        removeHBox.add(btnRemoveAll);

        // BottomPane

        JPanel bottomPane = new JPanel();
        getContentPane().add(bottomPane, BorderLayout.SOUTH);
        bottomPane.setLayout(new BoxLayout(bottomPane, BoxLayout.X_AXIS));
        
        bottomPane.add(Box.createHorizontalStrut(5));

        Box contents = Box.createVerticalBox();

        contents.add(columnsHBox);
        contents.add(Box.createVerticalStrut(3));
        contents.add(operatorsHBox);
        contents.add(Box.createVerticalStrut(5));
        contents.add(logicalOperatorsHBox);
        contents.add(Box.createVerticalStrut(5));
        contents.add(numberHBox);
        contents.add(Box.createVerticalStrut(5));
        contents.add(stringHBox);
        contents.add(Box.createVerticalStrut(5));
        contents.add(removeHBox);
        contents.add(Box.createVerticalStrut(10));

        bottomPane.add(contents);

        columnsList = new ArrayList<String>();
        columnsList = parentCell.getColumnsName();

        bottomPane.add(Box.createHorizontalStrut(5));
        
        panel = new JPanel();
        bottomPane.add(panel);
        panel.setLayout(null);

        btnCancel = new JButton("Cancelar");
        btnCancel.setBounds(33, 141, 96, 25);
        panel.add(btnCancel);
        Component horizontalStrut = Box.createHorizontalStrut(3);
        horizontalStrut.setBounds(139, 17, 3, 12);
        panel.add(horizontalStrut);
        panel.add(Box.createHorizontalStrut(5));
        
        btnReady = new JButton("Pronto");
        btnReady.setBounds(141, 141, 82, 25);
        panel.add(btnReady);
        btnReady.addActionListener(this);
        
        btnReady.setEnabled(isExpressionValid());
        btnCancel.addActionListener(this);

        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource() == btnColumnAdd) {

            textArea.append(" ");
            
            textArea.append(comboBoxColumns.getSelectedItem().toString());

        }

        if(e.getSource() == btnOperatorAdd) {

            textArea.append(" ");
            textArea.append(comboBoxOperator.getSelectedItem().toString());

        }

        if(e.getSource() == btnLogicalOperatorAdd) {

            textArea.append(" ");
            textArea.append(comboBoxLogicalOperator.getSelectedItem().toString());

        }

        if(e.getSource() == btnNumberAdd && !formattedTextFieldNumber.getText().isEmpty()) {

            textArea.append(" ");
            textArea.append(formattedTextFieldNumber.getText());

        }

        if(e.getSource() == btnStringAdd && !textFieldString.getText().isEmpty()) {

            textArea.append(" ");
            textArea.append("'"+textFieldString.getText()+"'");

        }

        if(e.getSource() == btnRemoveLastOne && !textArea.getText().isEmpty()) {

            String text = textArea.getText();
            Pattern pattern = Pattern.compile("[^']*\\s");
            Matcher matcher = pattern.matcher(text);

            if (matcher.find()) {
                int lastIndex = matcher.end() - 1;
                textArea.replaceRange("", lastIndex, text.length());
            }

        }

        if(e.getSource() == btnRemoveAll && !textArea.getText().isEmpty()) {

            textArea.setText("");;

        }

        if(e.getSource() == btnReady) {

            executeOperation();

            graph.getModel().setValue(jCell,"σ  " + textArea.getText());
        }

    }

    public void executeOperation() {

        String[] formattedInput = formatString(textArea.getText()).split(" ");

        Operator operator = new FilterOperator(parentCell.getData(),(Tuple t)->{

            for(String element : formattedInput) {

                if (isColumn(element)) {

                    String columnName = element.substring(2, element.length() - 1);

                    ColumnDataType type = parentCell.getColumns().stream()
                            .filter(col -> col.getName().equals(columnName))
                            .findFirst().get().getType();

                    String data;

                    if(type == ColumnDataType.INTEGER) {

                        data = String.valueOf(t.getContent(parentCell.getSourceTableName(columnName)).getInt(columnName));

                    }else if(type == ColumnDataType.FLOAT) {

                        data = String.valueOf(t.getContent(parentCell.getSourceTableName(columnName)).getFloat(columnName));

                    }else {

                        data = "'" + t.getContent(parentCell.getSourceTableName(columnName)).getString(columnName) + "'";

                    }

                    evaluator.putVariable(columnName, data);

                }
            }
            
            try {
            	
                return evaluator.evaluate(formatString(textArea.getText())).equals("1.0");

            } catch (EvaluationException e) {
                e.printStackTrace();
                return false;
            }

        });


        ((OperatorCell)cell).setColumns(List.of(parentCell.getColumns()), operator.getContentInfo().values());
        ((OperatorCell) cell).setOperator(operator);

        cell.setName("σ  " + textArea.getText());

        dispose();

    }

    private boolean isExpressionValid() {

        String[] formattedInput = formatString(textArea.getText()).split(" ");

        if(formattedInput.length <= 2) return false;

        for(String element : formattedInput) {

            if (isColumn(element)) {

                String columnName = element.substring(2, element.length() - 1);
                
                
                ColumnDataType type = parentCell.getColumns().stream()
                        .filter(col -> col.getName().equals(columnName))
                        .findFirst().get().getType();


                String data;

                if(type == ColumnDataType.INTEGER) {

                    data = String.valueOf(1);

                }else if(type == ColumnDataType.FLOAT) {

                    data = String.valueOf(1.0);

                }else {

                    data = "'" + "a" + "'";

                }

                evaluator.putVariable(columnName, data);

            }
        }

        try {
        	
            evaluator.evaluate(formatString(textArea.getText()));
            return true;

        } catch (EvaluationException e) {

            return false;

        }
    }

    private boolean isColumn(String input) {

        Pattern pattern = Pattern.compile("#\\{.*?\\}");
        Matcher matcher = pattern.matcher(input);

        return matcher.matches();

    }
    private String formatString(String input) {

    	input = input.replaceAll("(?<=\\s*|^)([\\w.()-<>]+\\_[\\w.()-<>]+)(?=\\s*|$)", "#{$1}");

        input = input.replaceAll("\\bAND\\b", "&&");

        input = input.replaceAll("\\bOR\\b", "||");

        input = input.replaceAll("=", "==");

        input = input.replaceAll("≠", "!=");

        input = input.replaceAll("≥", ">=");

        input = input.replaceAll("≤", "<=");

        return input;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {

        btnReady.setEnabled(isExpressionValid());

    }

    @Override
    public void removeUpdate(DocumentEvent e) {

        btnReady.setEnabled(isExpressionValid());

    }

    @Override
    public void changedUpdate(DocumentEvent e) {

        btnReady.setEnabled(isExpressionValid());

    }

}
