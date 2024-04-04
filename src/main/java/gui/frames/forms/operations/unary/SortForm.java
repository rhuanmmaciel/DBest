package gui.frames.forms.operations.unary;

import com.mxgraph.model.mxCell;
import controllers.ConstantController;
import entities.Column;
import gui.frames.forms.IFormCondition;
import gui.frames.forms.operations.IOperationForm;
import gui.frames.forms.operations.OperationForm;
import utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class SortForm extends OperationForm implements ActionListener, IOperationForm, IFormCondition {

    private final ButtonGroup buttonGroup = new ButtonGroup();
    private final JRadioButton ascendingRadioButton = new JRadioButton(ConstantController.getString("operationForm.ascending"));
    private final JRadioButton descendingRadioButton = new JRadioButton(ConstantController.getString("operationForm.descending"));

    public SortForm(mxCell jCell) {

        super(jCell);

        initGUI();

    }

    public void initGUI() {

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeWindow();
            }
        });

        addExtraComponent(ascendingRadioButton, 0, 2, 1, 1);
        addExtraComponent(descendingRadioButton, 1, 2, 1, 1);
        ascendingRadioButton.addActionListener(this);
        descendingRadioButton.addActionListener(this);
        buttonGroup.add(ascendingRadioButton);
        buttonGroup.add(descendingRadioButton);

        btnCancel.addActionListener(this);
        btnReady.addActionListener(this);

        setPreviousArgs();
        checkBtnReady();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

    }

    @Override
    protected void setPreviousArgs() {

        if(!previousArguments.isEmpty()){

            String column = previousArguments.getFirst();
            String columnName = Column.removeSource(column);
            String columnSource = Column.removeName(column);

            ascendingRadioButton.setSelected(true);
            if(Utils.startsWithIgnoreCase(column, "DESC:"))
               descendingRadioButton.setSelected(true);

            comboBoxSource.setSelectedItem(columnSource);
            comboBoxColumn.setSelectedItem(columnName);

        }

    }

    @Override
    public void checkBtnReady() {

        AtomicBoolean anySelected = new AtomicBoolean(false);

        buttonGroup.getElements().asIterator().forEachRemaining(x -> {
            if(x.isSelected()) anySelected.set(true);
        });

        boolean noneSelection = anySelected.get();

        btnReady.setEnabled(noneSelection);

        updateToolTipText(noneSelection);

    }

    @Override
    public void updateToolTipText(boolean ...conditions) {

        String btnReadyToolTipText = "";

        boolean noneSelection = conditions[0];

        if (!noneSelection)
            btnReadyToolTipText = "- "+ ConstantController.getString("operationForm.toolTip.sort.selectAtLeastOne");

        UIManager.put("ToolTip.foreground", Color.RED);

        btnReady.setToolTipText(btnReadyToolTipText.isEmpty() ? null : btnReadyToolTipText);

    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {

        checkBtnReady();

        if (actionEvent.getSource() == btnReady) {

            String order = ascendingRadioButton.isSelected() ? "ASC:" : "DESC:";
            arguments.add(order+Column.composeSourceAndName(Objects.requireNonNull(comboBoxSource.getSelectedItem()).toString(),
                    Objects.requireNonNull(comboBoxColumn.getSelectedItem()).toString()));
            btnReady();

        } else if (actionEvent.getSource() == btnCancel) {

            closeWindow();

        }
    }

    protected void closeWindow() {
        dispose();
    }
}
