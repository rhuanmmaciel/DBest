package gui.frames.forms.operations.unary;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Objects;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import com.mxgraph.model.mxCell;

import entities.Column;
import gui.frames.forms.operations.IOperationForm;
import gui.frames.forms.operations.OperationForm;
import operations.unary.Aggregation;
import utils.Utils;

public class AggregationForm extends OperationForm implements ActionListener, IOperationForm {

    protected final JComboBox<String> comboBoxAggregation = new JComboBox<>(Arrays.stream(Aggregation.Function.values())
            .map(Aggregation.Function::getDisplayName)
            .toArray(String[]::new));

    public AggregationForm(mxCell jCell) {

        super(jCell);

        this.initializeGUI();

    }

    private void initializeGUI() {

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                AggregationForm.this.closeWindow();
            }
        });

        this.centerPanel.removeAll();

        this.readyButton.addActionListener(this);
        this.cancelButton.addActionListener(this);

        this.addExtraComponent(new JLabel("Fonte:"), 0, 0, 1, 1);
        this.addExtraComponent(this.comboBoxSource, 1, 0, 1, 1);
        this.addExtraComponent(new JLabel("Coluna:"), 0, 1, 1, 1);
        this.addExtraComponent(this.comboBoxColumn, 1, 1, 1, 1);
        this.addExtraComponent(new JLabel("Agregação:"), 0, 2, 1, 1);
        this.addExtraComponent(this.comboBoxAggregation, 1, 2, 1, 1);

        this.setPreviousArgs();

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);

    }

    @Override
    protected void setPreviousArgs() {

        if(!this.previousArguments.isEmpty()){

            String column = this.previousArguments.get(0);

            if(Utils.startsWithIgnoreCase(column, Aggregation.PREFIXES)){

                String prefix = Utils.getFirstMatchingPrefixIgnoreCase(column, Aggregation.PREFIXES);
                column = column.substring(prefix.length());
                this.comboBoxAggregation.setSelectedItem(switch (prefix){
                    case "MAX:" -> "Máximo";
                    case "MIN:" -> "Mínimo";
                    case "AVG:" -> "Média";
                    case "COUNT:" -> "Contagem";
                    default -> throw new IllegalStateException("Unexpected value: " + prefix);
                });

            }

            String columnName = Column.removeSource(column);
            String columnSource = Column.removeName(column);

            this.comboBoxSource.setSelectedItem(columnSource);
            this.comboBoxColumn.setSelectedItem(columnName);

        }

    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {

       if(actionEvent.getSource() == this.cancelButton){

           this.closeWindow();

        }else if (actionEvent.getSource() == this.readyButton) {

           String aggregation = switch (Objects.requireNonNull(this.comboBoxAggregation.getSelectedItem()).toString()){
               case "Máximo" -> "MAX:";
               case "Mínimo" -> "MIN:";
               case "Média" -> "AVG:";
               case "Contagem" -> "COUNT:";
               default ->
                       throw new IllegalStateException("Unexpected value: " + this.comboBoxAggregation.getSelectedItem().toString());
           };
           this.arguments.add(aggregation+(this.comboBoxSource.getSelectedItem()+"."+ this.comboBoxColumn.getSelectedItem()));
           this.onReadyButtonClicked();

        }

    }

    protected void closeWindow() {
        this.dispose();
    }
}
