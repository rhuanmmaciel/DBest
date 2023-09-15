package gui.frames.forms.operations.unary;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.mxgraph.model.mxCell;

import entities.Column;
import entities.cells.Cell;
import gui.frames.forms.operations.IOperationForm;
import gui.frames.forms.operations.OperationForm;

public class GroupForm extends OperationForm implements ActionListener, IOperationForm {

    private final JButton btnAdd = new JButton("Adicionar");

    private final JButton btnRemove = new JButton("Remover colunas");

    private final JTextArea textArea = new JTextArea();

    private final JComboBox<String> comboBoxGroupBySource = new JComboBox<>();

    private final JComboBox<String> comboBoxGroupByColumn = new JComboBox<>();

    private final JComboBox<String> comboBoxAggregation = new JComboBox<>(new String[]{"Mínimo", "Máximo", "Média", "Contagem"});

    public GroupForm(mxCell jCell) {

        super(jCell);

        this.initializeGUI();
    }

    private void initializeGUI() {

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                GroupForm.this.closeWindow();
            }
        });

        this.centerPanel.removeAll();

        this.readyButton.addActionListener(this);
        this.cancelButton.addActionListener(this);

        this.textArea.setPreferredSize(new Dimension(300, 300));
        this.textArea.setEditable(false);

        this.btnAdd.addActionListener(this);
        this.btnRemove.addActionListener(this);

        this.addExtraComponent(new JLabel("Fonte:"), 0, 0, 1, 1);
        this.addExtraComponent(this.comboBoxSource, 1, 0, 1, 1);
        this.addExtraComponent(new JLabel("Coluna:"), 0, 1, 1, 1);
        this.addExtraComponent(this.comboBoxColumn, 1, 1, 1, 1);
        this.addExtraComponent(new JLabel("Agregação:"), 0, 2, 1, 1);
        this.addExtraComponent(this.comboBoxAggregation, 1, 2, 1, 1);
        this.addExtraComponent(this.btnAdd, 0, 3, 1, 1);
        this.addExtraComponent(this.btnRemove, 1, 3, 1, 1);
        this.addExtraComponent(new JScrollPane(this.textArea), 0, 4, 2, 2);
        this.addExtraComponent(new JLabel("Fonte:"), 0, 6, 1, 1);
        this.addExtraComponent(this.comboBoxGroupBySource, 1, 6, 1, 1);
        this.addExtraComponent(new JLabel("Agrupar por:"), 0, 7, 1, 1);
        this.addExtraComponent(this.comboBoxGroupByColumn, 1, 7, 1, 1);

        this.parent1.getSources().stream()
            .map(Cell::getName)
            .forEach(this.comboBoxGroupBySource::addItem);

        this.comboBoxGroupBySource.addActionListener(actionEvent -> this.setColumns(this.comboBoxGroupByColumn, this.comboBoxGroupBySource, this.parent1));

        this.setColumns(this.comboBoxGroupByColumn, this.comboBoxGroupBySource, this.parent1);
        this.setPreviousArgs();

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    @Override
    protected void setPreviousArgs() {

        if (!this.previousArguments.isEmpty()) {

            String groupByColumn = this.previousArguments.get(0);
            String groupByColumnName = Column.removeSource(groupByColumn);
            String groupByColumnSource = Column.removeName(groupByColumn);

            this.comboBoxGroupBySource.setSelectedItem(groupByColumnSource);
            this.comboBoxGroupByColumn.setSelectedItem(groupByColumnName);

            for (String element : this.previousArguments.subList(1, this.previousArguments.size())) {

                String textColumnsPicked = this.textArea.getText() + "\n" + element;
                this.textArea.setText(textColumnsPicked);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {

        if (actionEvent.getSource() == this.btnAdd) {

            if (this.comboBoxColumn.getItemCount() > 0) {
                this.updateColumns();
            }
        } else if (actionEvent.getSource() == this.btnRemove) {

            this.textArea.setText("");
        } else if (actionEvent.getSource() == this.cancelButton) {

            this.closeWindow();
        } else if (actionEvent.getSource() == this.readyButton) {

            this.arguments.addAll(List.of(this.textArea.getText().split("\n")));
            this.arguments.remove(0);
            this.arguments.add(0, Column.composeSourceAndName(Objects.requireNonNull(this.comboBoxGroupBySource.getSelectedItem()).toString(), Objects.requireNonNull(this.comboBoxGroupByColumn.getSelectedItem()).toString()
            ));
            this.onReadyButtonClicked();
        }
    }

    private void updateColumns() {

        String aggregation = switch (Objects.requireNonNull(this.comboBoxAggregation.getSelectedItem()).toString()) {
            case "Máximo" -> "MAX:";
            case "Mínimo" -> "MIN:";
            case "Média" -> "AVG:";
            case "Contagem" -> "COUNT:";
            default ->
                throw new IllegalStateException("Unexpected value: " + this.comboBoxAggregation.getSelectedItem().toString());
        };
        String column = aggregation +
            this.comboBoxSource.getSelectedItem() +
            "." + this.comboBoxColumn.getSelectedItem();
        String textColumnsPicked = this.textArea.getText() + "\n" + column;
        this.textArea.setText(textColumnsPicked);
    }

    protected void closeWindow() {
        this.dispose();
    }
}
