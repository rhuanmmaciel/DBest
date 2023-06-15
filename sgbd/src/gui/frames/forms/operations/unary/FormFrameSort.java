package gui.frames.forms.operations.unary;

import com.mxgraph.model.mxCell;
import entities.Column;
import gui.frames.forms.operations.FormFrameOperation;
import gui.frames.forms.operations.IFormFrameOperation;
import util.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicBoolean;

public class FormFrameSort extends FormFrameOperation implements ActionListener, IFormFrameOperation {

    private final ButtonGroup buttonGroup = new ButtonGroup();
    private final JRadioButton ascendingRadioButton = new JRadioButton("Ascendente");
    private final JRadioButton descendingRadioButton = new JRadioButton("Descendente");

    public FormFrameSort(mxCell jCell) {

        super(jCell);

        initializeGUI();

    }

    private void initializeGUI() {

        addExtraComponent(ascendingRadioButton, 0, 2, 1, 1);
        addExtraComponent(descendingRadioButton, 1, 2, 1, 1);
        ascendingRadioButton.addActionListener(this);
        descendingRadioButton.addActionListener(this);
        buttonGroup.add(ascendingRadioButton);
        buttonGroup.add(descendingRadioButton);

        btnCancel.addActionListener(this);
        btnReady.addActionListener(this);

        setPreviousArgs();
        verifyConditions();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

    }

    @Override
    protected void setPreviousArgs() {

        if(!previousArguments.isEmpty()){

            String column = previousArguments.get(0);
            String columnName = Column.removeSource(column);
            String columnSource = Column.removeName(column);

            ascendingRadioButton.setSelected(true);
            if(Utils.startsWithIgnoreCase(column, "DESC:"))
               descendingRadioButton.setSelected(true);

            comboBoxSource.setSelectedItem(columnSource);
            comboBoxColumn.setSelectedItem(columnName);

        }

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

            String order = ascendingRadioButton.isSelected() ? "ASC:" : "DESC:";
            arguments.add(order+Column.putSource(comboBoxColumn.getSelectedItem().toString(),
                     comboBoxSource.getSelectedItem().toString()));
            btnReady();

        } else if (actionEvent.getSource() == btnCancel) {

            closeWindow();

        }
    }

}
