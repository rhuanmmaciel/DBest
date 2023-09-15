package gui.frames.forms.operations;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.mxgraph.model.mxCell;

import entities.Column;
import entities.cells.Cell;
import entities.cells.OperationCell;
import entities.utils.cells.CellUtils;

import gui.frames.forms.FormBase;

import operations.IOperator;

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

    protected OperationForm(mxCell jCell) {
        super(null);

        this.setModal(true);

        Optional<Cell> optionalCell = CellUtils.getActiveCell(jCell);

        if (optionalCell.isEmpty()) {
            this.operator = null;
            this.jCell = null;
            this.parent1 = null;
            return;
        }

        OperationCell cell = (OperationCell) optionalCell.get();

        this.operator = cell.getType().operatorClass;
        this.jCell = jCell;
        this.parent1 = cell.getParents().get(0);

        if (!cell.getArguments().isEmpty()) this.previousArguments.addAll(cell.getArguments());

        this.setTitle(cell.getType().displayName);

        this.initializeGUI();
    }

    private void initializeGUI() {
        this.setLocationRelativeTo(null);

        this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        this.initializeComboBoxes();

        this.pack();
    }

    private void initializeComboBoxes() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        this.centerPanel.add(new JLabel("Fonte:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.centerPanel.add(this.comboBoxSource, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        this.centerPanel.add(new JLabel("Coluna:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.centerPanel.add(this.comboBoxColumn, gbc);

        this.contentPanel.add(this.centerPanel, BorderLayout.CENTER);

        this.parent1.getColumns().stream().map(Column::getSource).distinct().forEach(this.comboBoxSource::addItem);

        this.comboBoxSource.addActionListener(actionEvent -> this.setColumns(this.comboBoxColumn, this.comboBoxSource, this.parent1));

        this.setColumns(this.comboBoxColumn, this.comboBoxSource, this.parent1);
    }

    protected void addExtraComponent(Component component, int gridx, int gridy, int gridwidth, int gridheight) {
        GridBagConstraints gbc = ((GridBagLayout) this.centerPanel.getLayout()).getConstraints(this.centerPanel);

        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = gridwidth;
        gbc.gridheight = gridheight;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.centerPanel.add(component, gbc);

        this.pack();
        this.revalidate();
        this.repaint();
    }

    protected void setColumns(JComboBox<String> comboBox, JComboBox<String> comboBoxS, Cell parent) {
        comboBox.removeAllItems();

        parent
            .getColumns()
            .stream()
            .filter(column -> column
                .getSource()
                .equals(Objects.requireNonNull(comboBoxS.getSelectedItem()).toString())
            )
            .map(Column::getSourceAndName)
            .filter(column -> !this.restrictedColumns.contains(column))
            .map(Column::removeSource)
            .forEach(comboBox::addItem);

        this.pack();
    }

    protected void onReadyButtonClicked() {
        this.dispose();

        try {
            Constructor<? extends IOperator> constructor = this.operator.getDeclaredConstructor();
            IOperator operation = constructor.newInstance();
            operation.executeOperation(this.jCell, this.arguments);
        } catch (
            InstantiationException | IllegalAccessException |
            NoSuchMethodException | InvocationTargetException exception
        ) {
            exception.printStackTrace();
        }
    }

    protected abstract void setPreviousArgs();
}
