package gui.frames.forms.operations;

import com.mxgraph.model.mxCell;
import entities.Column;
import entities.cells.Cell;
import entities.cells.OperationCell;
import enums.OperationArity;
import enums.OperationType;
import operations.IOperator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FormFrameOperation extends JDialog  {

    private final Class<? extends IOperator> operator;

    private final mxCell jCell;
    private final OperationType type;
    private final OperationCell cell;
    private final OperationArity arity;
    protected final Cell parent1;

    protected final JPanel contentPane = new JPanel(new BorderLayout());
    protected final JButton btnCancel = new JButton("Cancelar");
    protected final JButton btnReady = new JButton("Pronto");
    protected final JComboBox<String> comboBoxSource = new JComboBox<>();
    protected final JComboBox<String> comboBoxColumn = new JComboBox<>();
    protected final JPanel centerPanel = new JPanel(new GridBagLayout());

    protected final List<String> arguments = new ArrayList<>();
    protected final List<String> restrictedColumns = new ArrayList<>();

    public FormFrameOperation(mxCell jCell) {

        super((Window) null);
        setModal(true);

        this.jCell = jCell;
        this.cell = (OperationCell) Cell.getCells().get(jCell);
        this.type = cell.getType();
        this.arity = cell.getArity();
        this.operator = type.getOperator();

        setTitle(type.getDisplayName());

        this.parent1 = this.cell.getParents().get(0);

        initializeGUI();

    }

    private void initializeGUI(){

        setLocationRelativeTo(null);
        setContentPane(contentPane);

        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        initializeComboBoxes();
        initializeBottomButtons();

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeWindow();
            }
        });

        pack();

    }

    private void initializeComboBoxes() {

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(new JLabel("Fonte:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        centerPanel.add(comboBoxSource, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        centerPanel.add(new JLabel("Coluna:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        centerPanel.add(comboBoxColumn, gbc);

        contentPane.add(centerPanel, BorderLayout.CENTER);

        parent1.getAllSourceTables().stream()
                .map(Cell::getName)
                .forEach(comboBoxSource::addItem);

        comboBoxSource.addActionListener(actionEvent -> setColumns());

        setColumns();

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

    private void setColumns(){

        comboBoxColumn.removeAllItems();

        parent1.getColumns().stream().filter(x -> x.getSource().
                        equals(Objects.requireNonNull(comboBoxSource.getSelectedItem()).toString())).
                        map(Column::getSourceAndName).filter(x -> !restrictedColumns.contains(x)).
                        map(Column::removeSource).forEach(comboBoxColumn::addItem);

        pack();

    }


    private void initializeBottomButtons() {
        JPanel bottomPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPane.add(btnCancel);
        bottomPane.add(btnReady);
        contentPane.add(bottomPane, BorderLayout.SOUTH);
    }

    protected void btnReady(){

        dispose();

        try {

            Constructor<? extends IOperator> constructor = operator.getDeclaredConstructor();
            IOperator operation = constructor.newInstance();
            operation.executeOperation(jCell, arguments);
            System.out.println(operation);

        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException
                 | InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    protected void closeWindow(){
        dispose();
    }

}
