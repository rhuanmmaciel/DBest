package gui.frames.forms.operations.unary;

import com.mxgraph.model.mxCell;
import controllers.ConstantController;
import entities.Column;
import entities.cells.Cell;
import entities.utils.cells.CellUtils;
import gui.frames.forms.operations.IOperationForm;
import gui.frames.forms.operations.OperationForm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GroupForm extends OperationForm implements ActionListener, IOperationForm {

    private final JButton btnClear = new JButton(ConstantController.getString("operationForm.clear"));
    private final JButton btnRemove = new JButton(ConstantController.getString("operationForm.remove"));
    private final JButton btnAdd = new JButton(ConstantController.getString("operationForm.add"));

    private final JPanel checkBoxPanel = new JPanel();
    private final ArrayList<JCheckBox> aggregationCheckBoxes = new ArrayList<>();

    private final JComboBox<String> comboBoxGroupBySource = new JComboBox<>();
    private final JComboBox<String> comboBoxGroupByColumn = new JComboBox<>();
    private final JComboBox<String> comboBoxAggregation = new JComboBox<>(new String[]{
        ConstantController.getString("operationForm.minimum"),
        ConstantController.getString("operationForm.maximum"),
        ConstantController.getString("operationForm.average"),
        ConstantController.getString("operationForm.sum"),
        ConstantController.getString("operationForm.first"),
        ConstantController.getString("operationForm.last"),
        ConstantController.getString("operationForm.count"),
        ConstantController.getString("operationForm.countAll"),
        ConstantController.getString("operationForm.countNull")});

    public GroupForm(mxCell jCell) {
        super(jCell);
        initGUI();
    }

    public void initGUI() {
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeWindow();
            }
        });

        centerPanel.removeAll();

        btnReady.addActionListener(this);
        btnCancel.addActionListener(this);

        checkBoxPanel.setLayout(new BoxLayout(checkBoxPanel, BoxLayout.Y_AXIS));
        checkBoxPanel.setPreferredSize(new Dimension(300, 300));

        btnAdd.addActionListener(this);
        btnRemove.addActionListener(this);
        btnClear.addActionListener(this);

        addExtraComponent(new JLabel(ConstantController.getString("operationForm.source") + ":"), 0, 0, 1, 1);
        addExtraComponent(comboBoxSource, 1, 0, 2, 1);
        addExtraComponent(new JLabel(ConstantController.getString("operationForm.column") + ":"), 0, 1, 1, 1);
        addExtraComponent(comboBoxColumn, 1, 1, 2, 1);
        addExtraComponent(new JLabel(ConstantController.getString("operation.aggregation") + ":"), 0, 2, 1, 1);
        addExtraComponent(comboBoxAggregation, 1, 2, 2, 1);

        addExtraComponent(btnAdd, 0, 3, 1, 1);
        addExtraComponent(btnRemove, 1, 3, 1, 1);
        addExtraComponent(btnClear, 2, 3, 1, 1);

        addExtraComponent(new JScrollPane(checkBoxPanel), 0, 4, 3, 2);

        addExtraComponent(new JLabel(ConstantController.getString("operationForm.source") + ":"), 0, 6, 1, 1);
        addExtraComponent(comboBoxGroupBySource, 1, 6, 2, 1);
        addExtraComponent(new JLabel(ConstantController.getString("operationForm.groupBy") + ":"), 0, 7, 1, 1);
        addExtraComponent(comboBoxGroupByColumn, 1, 7, 2, 1);

        comboBoxGroupBySource.addActionListener(actionEvent -> setColumns(comboBoxGroupByColumn, comboBoxGroupBySource, leftChild.getColumns()));

        Cell cell = CellUtils.getActiveCell(jCell).get();
        java.util.List<Column> columns = setLeftComboBoxColumns(cell);
        setComboBoxData(columns, comboBoxGroupBySource, comboBoxGroupByColumn);

        setPreviousArgs();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    protected void setPreviousArgs() {
        if (previousArguments.isEmpty()) return;

        String groupByColumn = previousArguments.get(0);
        String groupByColumnName = Column.removeSource(groupByColumn);
        String groupByColumnSource = Column.removeName(groupByColumn);

        comboBoxGroupBySource.setSelectedItem(groupByColumnSource);
        comboBoxGroupByColumn.setSelectedItem(groupByColumnName);

        for (String element : previousArguments.subList(1, previousArguments.size())) {
            aggregationCheckBoxes.add(new JCheckBox(element));
        }
        refreshCheckBoxPanel();
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == btnAdd) {
            if (comboBoxColumn.getItemCount() > 0) {
                String aggregation = createAggregationString();
                if (aggregationCheckBoxes.stream().noneMatch(cb -> cb.getText().equals(aggregation))) {
                    aggregationCheckBoxes.add(new JCheckBox(aggregation));
                    refreshCheckBoxPanel();
                }
            }
        } else if (actionEvent.getSource() == btnRemove) {
            aggregationCheckBoxes.removeIf(AbstractButton::isSelected);
            refreshCheckBoxPanel();
        } else if (actionEvent.getSource() == btnClear) {
            aggregationCheckBoxes.clear();
            refreshCheckBoxPanel();
        } else if (actionEvent.getSource() == btnCancel) {
            closeWindow();
        } else if (actionEvent.getSource() == btnReady) {
            arguments.clear();
            arguments.add(Column.composeSourceAndName(
                Objects.requireNonNull(comboBoxGroupBySource.getSelectedItem()).toString(),
                Objects.requireNonNull(comboBoxGroupByColumn.getSelectedItem()).toString())
            );
            arguments.addAll(aggregationCheckBoxes.stream().map(JCheckBox::getText).collect(Collectors.toList()));
            btnReady();
        }
    }

    private String createAggregationString() {
        String selected = Objects.requireNonNull(comboBoxAggregation.getSelectedItem()).toString();
        String suffix;

        if (selected.equals(ConstantController.getString("operationForm.maximum")))
            suffix = "MAX:";
        else if (selected.equals(ConstantController.getString("operationForm.minimum")))
            suffix = "MIN:";
        else if (selected.equals(ConstantController.getString("operationForm.average")))
            suffix = "AVG:";
        else if (selected.equals(ConstantController.getString("operationForm.sum")))
            suffix = "SUM:";
        else if (selected.equals(ConstantController.getString("operationForm.first")))
            suffix = "FIRST:";
        else if (selected.equals(ConstantController.getString("operationForm.last")))
            suffix = "LAST:";
        else if (selected.equals(ConstantController.getString("operationForm.count")))
            suffix = "COUNT:";
        else if (selected.equals(ConstantController.getString("operationForm.countAll")))
            suffix = "COUNT_ALL:";
        else if (selected.equals(ConstantController.getString("operationForm.countNull")))
            suffix = "COUNT_NULL:";
        else
            throw new IllegalStateException("Unexpected value: " + selected);

        return suffix + comboBoxSource.getSelectedItem() + "." + comboBoxColumn.getSelectedItem();
    }

    private void refreshCheckBoxPanel() {
        checkBoxPanel.removeAll();
        for (JCheckBox checkBox : aggregationCheckBoxes) {
            checkBoxPanel.add(checkBox);
        }
        checkBoxPanel.revalidate();
        checkBoxPanel.repaint();
    }

    protected void closeWindow() {
        dispose();
    }
}
