package gui.frames.forms.operations.unary;

import com.mxgraph.model.mxCell;
import controller.ConstantController;
import entities.Column;
import gui.frames.forms.operations.OperationForm;
import gui.frames.forms.operations.IOperationForm;
import operations.unary.Aggregation;
import util.Utils;

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

        btnReady.addActionListener(this);
        btnCancel.addActionListener(this);

        addExtraComponent(new JLabel(ConstantController.getString("operationForm.source") +":"), 0, 0, 1, 1);
        addExtraComponent(comboBoxSource, 1, 0, 1, 1);
        addExtraComponent(new JLabel(ConstantController.getString("operationForm.column")+":"), 0, 1, 1, 1);
        addExtraComponent(comboBoxColumn, 1, 1, 1, 1);
        addExtraComponent(new JLabel(ConstantController.getString("operation.aggregation")+":"), 0, 2, 1, 1);
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

                String prefix = Utils.getStartPrefixIgnoreCase(column, Aggregation.PREFIXES);
                column = column.substring(Utils.getLastIndexOfPrefix(prefix));
                comboBoxAggregation.setSelectedItem(switch (prefix){
                    case "MAX:" -> ConstantController.getString("operationForm.maximum");
                    case "MIN:" -> ConstantController.getString("operationForm.minimum");
                    case "AVG:" -> ConstantController.getString("operationForm.average");
                    case "COUNT:" -> ConstantController.getString("operationForm.count");
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

       if(actionEvent.getSource() == btnCancel){

            closeWindow();

        }else if (actionEvent.getSource() == btnReady) {

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

            arguments.add(suffix+(comboBoxSource.getSelectedItem()+"."+comboBoxColumn.getSelectedItem()));
            btnReady();

        }

    }

    protected void closeWindow() {
        dispose();
    }
}
