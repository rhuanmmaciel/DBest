package gui.frames.forms.operations.unary;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.UIManager;

import com.mxgraph.model.mxCell;

import controllers.ConstantController;

import entities.Column;

import gui.frames.forms.IFormCondition;
import gui.frames.forms.operations.IOperationForm;
import gui.frames.forms.operations.OperationForm;

import utils.Utils;

public class SortForm extends OperationForm implements ActionListener, IOperationForm, IFormCondition {

    private final ButtonGroup buttonGroup = new ButtonGroup();

    private final JRadioButton ascendingRadioButton = new JRadioButton(
        ConstantController.getString("operationForm.ascending")
    );

    private final JRadioButton descendingRadioButton = new JRadioButton(
        ConstantController.getString("operationForm.descending")
    );

    public SortForm(mxCell jCell) {
        super(jCell);

        this.initializeGUI();
    }

    private void initializeGUI() {
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent exception) {
                SortForm.this.closeWindow();
            }
        });

        this.addExtraComponent(this.ascendingRadioButton, 0, 2, 1, 1);
        this.addExtraComponent(this.descendingRadioButton, 1, 2, 1, 1);
        this.ascendingRadioButton.addActionListener(this);
        this.descendingRadioButton.addActionListener(this);
        this.buttonGroup.add(this.ascendingRadioButton);
        this.buttonGroup.add(this.descendingRadioButton);

        this.cancelButton.addActionListener(this);
        this.readyButton.addActionListener(this);

        this.setPreviousArgs();
        this.checkReadyButton();

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    @Override
    protected void setPreviousArgs() {
        if (!this.previousArguments.isEmpty()) {
            String column = this.previousArguments.get(0);
            String columnName = Column.removeSource(column);
            String columnSource = Column.removeName(column);

            this.ascendingRadioButton.setSelected(true);

            if (Utils.startsWithIgnoreCase(column, "DESC:")) {
                this.descendingRadioButton.setSelected(true);
            }

            this.comboBoxSource.setSelectedItem(columnSource);
            this.comboBoxColumn.setSelectedItem(columnName);
        }
    }

    @Override
    public void checkReadyButton() {
        AtomicBoolean isAnyButtonSelectedReference = new AtomicBoolean(false);

        this.buttonGroup
            .getElements()
            .asIterator()
            .forEachRemaining(button -> {
                if (button.isSelected()) isAnyButtonSelectedReference.set(true);
            });

        boolean isAnyButtonSelected = isAnyButtonSelectedReference.get();

        this.readyButton.setEnabled(isAnyButtonSelected);
        this.updateToolTipText(isAnyButtonSelected);
    }

    @Override
    public void updateToolTipText(boolean... conditions) {
        String readyButtonToolTipText = "";

        boolean noneSelection = conditions[0];

        if (!noneSelection) {
            readyButtonToolTipText = String.format(
                "- %s",
                ConstantController.getString("operationForm.toolTip.sort.selectAtLeastOne")
            );
        }

        UIManager.put("ToolTip.foreground", Color.RED);

        this.readyButton.setToolTipText(readyButtonToolTipText.isEmpty() ? null : readyButtonToolTipText);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        this.checkReadyButton();

        if (actionEvent.getSource() == this.readyButton) {
            String order = this.ascendingRadioButton.isSelected() ? "ASC:" : "DESC:";
            String source = Objects.requireNonNull(this.comboBoxSource.getSelectedItem()).toString();
            String column = Objects.requireNonNull(this.comboBoxColumn.getSelectedItem()).toString();

            this.arguments.add(String.format("%s%s.%s", order, source, column));

            this.onReadyButtonClicked();
        } else if (actionEvent.getSource() == this.cancelButton) {
            this.closeWindow();
        }
    }

    protected void closeWindow() {
        this.dispose();
    }
}
