package gui.frames.forms.operations.unary;

import com.mxgraph.model.mxCell;
import entities.Column;
import gui.frames.forms.IFormCondition;
import gui.frames.forms.operations.OperationForm;
import gui.frames.forms.operations.IOperationForm;
import util.Utils;

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
    private final JRadioButton ascendingRadioButton = new JRadioButton("Ascendente");
    private final JRadioButton descendingRadioButton = new JRadioButton("Descendente");

    public SortForm(mxCell jCell) {

        super(jCell);

        initializeGUI();

    }

    private void initializeGUI() {

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

    private boolean noneSelection;

    @Override
    public void checkBtnReady() {

        AtomicBoolean anySelected = new AtomicBoolean(false);

        buttonGroup.getElements().asIterator().forEachRemaining(x -> {
            if(x.isSelected()) anySelected.set(true);
        });

        btnReady.setEnabled(anySelected.get());

        noneSelection = anySelected.get();

        updateToolTipTxt();

    }

    @Override
    public void updateToolTipTxt() {

        String btnReadyToolTipText = "";

        if (!noneSelection)
            btnReadyToolTipText = "- Selecione pelo menos uma opção";

        UIManager.put("ToolTip.foreground", Color.RED);

        btnReady.setToolTipText(btnReadyToolTipText.isEmpty() ? null : btnReadyToolTipText);

    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {

        checkBtnReady();

        if (actionEvent.getSource() == btnReady) {

            String order = ascendingRadioButton.isSelected() ? "ASC:" : "DESC:";
            arguments.add(order+Column.putSource(Objects.requireNonNull(comboBoxColumn.getSelectedItem()).toString(),
                     Objects.requireNonNull(comboBoxSource.getSelectedItem()).toString()));
            btnReady();

        } else if (actionEvent.getSource() == btnCancel) {

            closeWindow();

        }
    }

    protected void closeWindow() {
        dispose();
    }
}
