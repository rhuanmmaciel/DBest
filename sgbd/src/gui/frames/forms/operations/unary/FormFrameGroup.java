package gui.frames.forms.operations.unary;

import com.mxgraph.model.mxCell;
import entities.Column;
import entities.cells.Cell;
import gui.frames.forms.operations.FormFrameOperation;
import gui.frames.forms.operations.IFormFrameOperation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Objects;

public class FormFrameGroup extends FormFrameOperation implements ActionListener, IFormFrameOperation {

    private final JButton btnAdd = new JButton("Adicionar");
    private final JButton btnRemove = new JButton("Remover colunas");
    private final JTextArea textArea = new JTextArea();
    private final JComboBox<String> comboBoxGroupBySource = new JComboBox<>();
    private final JComboBox<String> comboBoxGroupByColumn = new JComboBox<>();
    private final JComboBox<String> comboBoxAggregation = new JComboBox<>(new String[]{"Mínimo", "Máximo", "Média"});

    public FormFrameGroup(mxCell jCell) {

        super(jCell);

        initializeGUI();

    }

    private void initializeGUI() {

        centerPanel.removeAll();

        btnReady.addActionListener(this);
        btnCancel.addActionListener(this);

        textArea.setPreferredSize(new Dimension(300,300));
        textArea.setEditable(false);

        btnAdd.addActionListener(this);
        btnRemove.addActionListener(this);

        addExtraComponent(new JLabel("Fonte:"), 0, 0, 1, 1);
        addExtraComponent(comboBoxSource, 1, 0, 1, 1);
        addExtraComponent(new JLabel("Coluna:"), 0, 1, 1, 1);
        addExtraComponent(comboBoxColumn, 1, 1, 1, 1);
        addExtraComponent(new JLabel("Agregação:"), 0, 2, 1, 1);
        addExtraComponent(comboBoxAggregation, 1, 2, 1, 1);
        addExtraComponent(btnAdd, 0, 3, 1, 1);
        addExtraComponent(btnRemove, 1, 3, 1, 1);
        addExtraComponent(new JScrollPane(textArea), 0, 4, 2, 2);
        addExtraComponent(new JLabel("Fonte:"), 0, 6, 1, 1);
        addExtraComponent(comboBoxGroupBySource, 1, 6, 1, 1);
        addExtraComponent(new JLabel("Agrupar por:"), 0, 7, 1, 1);
        addExtraComponent(comboBoxGroupByColumn, 1, 7, 1, 1);

        parent1.getAllSourceTables().stream()
                .map(Cell::getName)
                .forEach(comboBoxGroupBySource::addItem);

        comboBoxGroupBySource.addActionListener(actionEvent -> setColumns(comboBoxGroupByColumn, comboBoxGroupBySource, parent1));

        setColumns(comboBoxGroupByColumn, comboBoxGroupBySource, parent1);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

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

        String aggregation = switch (Objects.requireNonNull(comboBoxAggregation.getSelectedItem()).toString()){
            case "Máximo" -> "MAX:";
            case "Mínimo" -> "MIN:";
            case "Média" -> "AVG:";
            default ->
                    throw new IllegalStateException("Unexpected value: " + comboBoxAggregation.getSelectedItem().toString());
        };
        String column = aggregation+
                comboBoxSource.getSelectedItem()+
                "."+comboBoxColumn.getSelectedItem();
        String textColumnsPicked = textArea.getText() + "\n" + column;
        textArea.setText(textColumnsPicked);

    }

}
