package gui.frames.forms.operations.unary;

import com.mxgraph.model.mxCell;
import entities.Column;
import gui.frames.forms.operations.OperationForm;
import gui.frames.forms.operations.IOperationForm;
import operations.unary.Aggregation;
import utils.Utils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Objects;

public class AggregationForm extends OperationForm implements ActionListener, IOperationForm {

    protected final JComboBox<String> comboBoxAggregation = new JComboBox<>(Arrays.stream(Aggregation.Function.values())
            .map(Aggregation.Function::getDisplayName)
            .toArray(String[]::new));

    public AggregationForm(mxCell jCell) {

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

        readyButton.addActionListener(this);
        cancelButton.addActionListener(this);

        addExtraComponent(new JLabel("Fonte:"), 0, 0, 1, 1);
        addExtraComponent(comboBoxSource, 1, 0, 1, 1);
        addExtraComponent(new JLabel("Coluna:"), 0, 1, 1, 1);
        addExtraComponent(comboBoxColumn, 1, 1, 1, 1);
        addExtraComponent(new JLabel("Agregação:"), 0, 2, 1, 1);
        addExtraComponent(comboBoxAggregation, 1, 2, 1, 1);

        setPreviousArgs();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

    }

    @Override
    protected void setPreviousArgs() {

        if(!previousArguments.isEmpty()){

            String column = previousArguments.get(0);

            if(Utils.startsWithIgnoreCase(column, Aggregation.PREFIXES)){

                String prefix = Utils.getFirstMatchingPrefixIgnoreCase(column, Aggregation.PREFIXES);
                column = column.substring(prefix.length());
                comboBoxAggregation.setSelectedItem(switch (prefix){
                    case "MAX:" -> "Máximo";
                    case "MIN:" -> "Mínimo";
                    case "AVG:" -> "Média";
                    case "COUNT:" -> "Contagem";
                    default -> throw new IllegalStateException("Unexpected value: " + prefix);
                });

            }

            String columnName = Column.removeSource(column);
            String columnSource = Column.removeName(column);

            comboBoxSource.setSelectedItem(columnSource);
            comboBoxColumn.setSelectedItem(columnName);

        }

    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {

       if(actionEvent.getSource() == cancelButton){

            closeWindow();

        }else if (actionEvent.getSource() == readyButton) {

           String aggregation = switch (Objects.requireNonNull(comboBoxAggregation.getSelectedItem()).toString()){
               case "Máximo" -> "MAX:";
               case "Mínimo" -> "MIN:";
               case "Média" -> "AVG:";
               case "Contagem" -> "COUNT:";
               default ->
                       throw new IllegalStateException("Unexpected value: " + comboBoxAggregation.getSelectedItem().toString());
           };
            arguments.add(aggregation+(comboBoxSource.getSelectedItem()+"."+comboBoxColumn.getSelectedItem()));
            onReadyButtonClicked();

        }

    }

    protected void closeWindow() {
        dispose();
    }
}
