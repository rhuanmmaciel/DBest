package gui.frames.forms.operations.unary;

import com.mxgraph.model.mxCell;
import entities.cells.Cell;
import entities.cells.OperationCell;
import gui.frames.forms.operations.IFormFrameOperation;
import operations.unary.Sort;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class FormFrameSort extends JDialog implements ActionListener, IFormFrameOperation {

    private final mxCell jCell;
    private final Cell parentCell;
    private final JButton btnCancel = new JButton("Cancelar");
    private final JButton btnReady = new JButton("Continuar");
    private final ButtonGroup buttonGroup = new ButtonGroup();
    private final JRadioButton ascendingRadioButton = new JRadioButton("Ascendente");
    private final JRadioButton descendingRadioButton = new JRadioButton("Descendente");
    private JComboBox<String> comboBox;
    private final Sort sort = new Sort();

    public FormFrameSort(mxCell jCell) {

        super((Window) null);
        setModal(true);
        setTitle("Ordenação");

        OperationCell cell = (OperationCell) Cell.getCells().get(jCell);
        parentCell = cell.getParents().get(0);
        this.jCell = jCell;

        initializeGUI();

    }

    private void initializeGUI() {

        setBounds(100, 100, 500, 470);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);

        String[] options = parentCell.getColumnsName().toArray(new String[0]);
        comboBox = new JComboBox<>(options);
        panel.add(comboBox, gbc);

        gbc.gridy++;
        ascendingRadioButton.addActionListener(this);
        descendingRadioButton.addActionListener(this);
        buttonGroup.add(ascendingRadioButton);
        buttonGroup.add(descendingRadioButton);
        panel.add(ascendingRadioButton, gbc);
        gbc.gridy++;
        panel.add(descendingRadioButton, gbc);

        gbc.gridy++;
        btnCancel.addActionListener(this);
        btnReady.addActionListener(this);
        panel.add(btnCancel, gbc);
        gbc.gridx++;
        panel.add(btnReady, gbc);

        add(panel, BorderLayout.CENTER);

        verifyConditions();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void verifyConditions() {

        AtomicBoolean anySelected = new AtomicBoolean(false);

        buttonGroup.getElements().asIterator().forEachRemaining(x -> {
            if(x.isSelected()) anySelected.set(true);
        });

        btnReady.setEnabled(anySelected.get());

        updateToolTipText(anySelected.get());

    }

    private void updateToolTipText(boolean noneSelection) {

        String btnReadyToolTipText = "";

        if (!noneSelection)
            btnReadyToolTipText = "- Selecione pelo menos uma opção";

        UIManager.put("ToolTip.foreground", Color.RED);

        btnReady.setToolTipText(btnReadyToolTipText.isEmpty() ? null : btnReadyToolTipText);

    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {

        verifyConditions();

        if (actionEvent.getSource() == btnReady) {

            dispose();
            String order = ascendingRadioButton.isSelected() ? "ASC:" : "DESC:";
            sort.executeOperation(jCell, List.of(order+comboBox.getSelectedItem().toString()));

        } else if (actionEvent.getSource() == btnCancel) {

            dispose();

        }
    }

}
