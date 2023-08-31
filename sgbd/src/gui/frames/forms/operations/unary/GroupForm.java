package gui.frames.forms.operations.unary;

import com.mxgraph.model.mxCell;
import controller.ConstantController;
import entities.Column;
import entities.cells.Cell;
import gui.frames.forms.operations.OperationForm;
import gui.frames.forms.operations.IOperationForm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Objects;

public class GroupForm extends OperationForm implements ActionListener, IOperationForm {

    private final JButton btnAdd = new JButton(ConstantController.getString("operationForm.add"));
    private final JButton btnRemove = new JButton(ConstantController.getString("operationForm.removeColumns"));
    private final JTextArea textArea = new JTextArea();
    private final JComboBox<String> comboBoxGroupBySource = new JComboBox<>();
    private final JComboBox<String> comboBoxGroupByColumn = new JComboBox<>();
    private final JComboBox<String> comboBoxAggregation = new JComboBox<>(new String[]{
            ConstantController.getString("operationForm.minimum"),
            ConstantController.getString("operationForm.maximum"),
            ConstantController.getString("operationForm.average"),
            ConstantController.getString("operationForm.count")});

    public GroupForm(mxCell jCell) {

        super(jCell);

        initializeGUI();

    }

    private void initializeGUI() {

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeWindow();
            }
        });

        centerPanel.removeAll();

        btnReady.addActionListener(this);
        btnCancel.addActionListener(this);

        textArea.setPreferredSize(new Dimension(300,300));
        textArea.setEditable(false);

        btnAdd.addActionListener(this);
        btnRemove.addActionListener(this);

        addExtraComponent(new JLabel(ConstantController.getString("operationForm.source")+":"), 0, 0, 1, 1);
        addExtraComponent(comboBoxSource, 1, 0, 1, 1);
        addExtraComponent(new JLabel(ConstantController.getString("operationForm.column")+":"), 0, 1, 1, 1);
        addExtraComponent(comboBoxColumn, 1, 1, 1, 1);
        addExtraComponent(new JLabel(ConstantController.getString("operation.aggregation")+":"), 0, 2, 1, 1);
        addExtraComponent(comboBoxAggregation, 1, 2, 1, 1);
        addExtraComponent(btnAdd, 0, 3, 1, 1);
        addExtraComponent(btnRemove, 1, 3, 1, 1);
        addExtraComponent(new JScrollPane(textArea), 0, 4, 2, 2);
        addExtraComponent(new JLabel(ConstantController.getString("operationForm.source")+":"), 0, 6, 1, 1);
        addExtraComponent(comboBoxGroupBySource, 1, 6, 1, 1);
        addExtraComponent(new JLabel(ConstantController.getString("operationForm.groupBy")+":"), 0, 7, 1, 1);
        addExtraComponent(comboBoxGroupByColumn, 1, 7, 1, 1);

        parent1.getAllSourceTables().stream()
                .map(Cell::getName)
                .forEach(comboBoxGroupBySource::addItem);

        comboBoxGroupBySource.addActionListener(actionEvent -> setColumns(comboBoxGroupByColumn, comboBoxGroupBySource, parent1));

        setColumns(comboBoxGroupByColumn, comboBoxGroupBySource, parent1);
        setPreviousArgs();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

    }

    @Override
    protected void setPreviousArgs() {

        if(!previousArguments.isEmpty()){

            String groupByColumn = previousArguments.get(0);
            String groupByColumnName =Column.removeSource(groupByColumn);
            String groupByColumnSource = Column.removeName(groupByColumn);

            comboBoxGroupBySource.setSelectedItem(groupByColumnSource);
            comboBoxGroupByColumn.setSelectedItem(groupByColumnName);

            for(String element : previousArguments.subList(1, previousArguments.size())){

                String textColumnsPicked = textArea.getText() + "\n" + element;
                textArea.setText(textColumnsPicked);

            }

        }

    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {

        if (actionEvent.getSource() == btnAdd) {

            if (comboBoxColumn.getItemCount() > 0)
                updateColumns();

        } else if (actionEvent.getSource() == btnRemove) {

            textArea.setText("");

        } else if(actionEvent.getSource() == btnCancel){

            closeWindow();

        }else if (actionEvent.getSource() == btnReady) {

            arguments.addAll(List.of(textArea.getText().split("\n")));
            arguments.remove(0);
            arguments.add(0, Column.putSource(Objects.requireNonNull(comboBoxGroupByColumn.getSelectedItem()).toString(),
                    Objects.requireNonNull(comboBoxGroupBySource.getSelectedItem()).toString()));
            btnReady();

        }

    }

    private void updateColumns(){

        String selected = Objects.requireNonNull(comboBoxAggregation.getSelectedItem()).toString();

        String suffix;

        if(selected.equals(ConstantController.getString("operationForm.maximum")))
            suffix = "MAX:";
        else  if(selected.equals(ConstantController.getString("operationForm.minimum")))
            suffix = "MIN:";
        else  if(selected.equals(ConstantController.getString("operationForm.average")))
            suffix = "AVG:";
        else  if(selected.equals(ConstantController.getString("operationForm.count")))
            suffix = "COUNT:";
        else
            throw new IllegalStateException("Unexpected value: " + selected);

        String column = suffix+
                comboBoxSource.getSelectedItem()+
                "."+comboBoxColumn.getSelectedItem();
        String textColumnsPicked = textArea.getText() + "\n" + column;
        textArea.setText(textColumnsPicked);

    }

    protected void closeWindow() {
        dispose();
    }
}
