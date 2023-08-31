package gui.frames.forms.operations;

import com.mxgraph.model.mxCell;
import controller.ConstantController;
import entities.Column;
import entities.cells.Cell;
import entities.cells.OperationCell;
import gui.frames.forms.FormBase;
import operations.IOperator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class OperationForm extends FormBase {

    private final Class<? extends IOperator> operator;

    protected final mxCell jCell;
    protected final Cell parent1;

    protected final JComboBox<String> comboBoxSource = new JComboBox<>();
    protected final JComboBox<String> comboBoxColumn = new JComboBox<>();
    protected final JPanel centerPanel = new JPanel(new GridBagLayout());

    protected final List<String> arguments = new ArrayList<>();
    protected final List<String> previousArguments = new ArrayList<>();
    protected final List<String> restrictedColumns = new ArrayList<>();

    public OperationForm(mxCell jCell) {

        super(null);
        setModal(true);

        OperationCell cell = (OperationCell) Cell.getCells().get(jCell);

        this.operator = cell.getType().OPERATOR_CLASS;
        this.jCell = jCell;
        this.parent1 = cell.getParents().get(0);

        if(!cell.getArguments().isEmpty()) previousArguments.addAll(cell.getArguments());

        setTitle(cell.getType().DISPLAY_NAME);

        initializeGUI();

    }

    private void initializeGUI(){

        setLocationRelativeTo(null);

        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        initializeComboBoxes();

        pack();

    }

    private void initializeComboBoxes() {

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(new JLabel(ConstantController.getString("operationForm.source") +":"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        centerPanel.add(comboBoxSource, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        centerPanel.add(new JLabel(ConstantController.getString("operationForm.column") +":"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        centerPanel.add(comboBoxColumn, gbc);

        contentPane.add(centerPanel, BorderLayout.CENTER);

        parent1.getColumns().stream()
                .map(Column::getSource).distinct()
                .forEach(comboBoxSource::addItem);

        comboBoxSource.addActionListener(actionEvent -> setColumns(comboBoxColumn, comboBoxSource, parent1));

        setColumns(comboBoxColumn, comboBoxSource, parent1);

    }

    protected void addExtraComponent(Component component, int gridx, int gridy, int gridwidth, int gridheight) {

        GridBagConstraints gbc = ((GridBagLayout) centerPanel.getLayout()).getConstraints(centerPanel);

        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = gridwidth;
        gbc.gridheight = gridheight;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        centerPanel.add(component, gbc);

        pack();
        revalidate();
        repaint();
    }

    protected void setColumns(JComboBox<String> comboBox, JComboBox<String> comboBoxS, Cell parent){

        comboBox.removeAllItems();
        parent.getColumns().stream().filter(x -> x.getSource().
                        equals(Objects.requireNonNull(comboBoxS.getSelectedItem()).toString())).
                        map(Column::getSourceAndName).filter(x -> !restrictedColumns.contains(x)).
                        map(Column::removeSource).forEach(comboBox::addItem);

        pack();

    }

    protected void btnReady(){

        dispose();

        try {

            Constructor<? extends IOperator> constructor = operator.getDeclaredConstructor();
            IOperator operation = constructor.newInstance();
            operation.executeOperation(jCell, arguments);

        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException
                 | InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    protected abstract void setPreviousArgs();

}
