package gui.frames.forms.operations.unary;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.mxgraph.model.mxCell;

import controllers.ConstantController;

import gui.frames.forms.IFormCondition;
import gui.frames.forms.operations.IOperationForm;
import gui.frames.forms.operations.OperationForm;

public class IndexerForm extends OperationForm implements IOperationForm, ActionListener, IFormCondition {

    private final JTextField txtField = new JTextField();

    public IndexerForm(mxCell jCell) {
        super(jCell);

        this.initGUI();
        this.checkReadyButton();
    }

    private void initGUI() {
        this.centerPanel.removeAll();

        this.addExtraComponent(
            new JLabel(String.format("%s: ", ConstantController.getString("operationForm.columnName"))),
            0, 0, 1, 1
        );

        this.addExtraComponent(this.txtField, 1, 0, 2, 1);
        this.txtField.setMinimumSize(new Dimension(50, this.txtField.getHeight()));
        this.txtField.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                IndexerForm.this.checkReadyButton();
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                IndexerForm.this.checkReadyButton();
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                IndexerForm.this.checkReadyButton();
            }
        });

        this.cancelButton.addActionListener(this);
        this.readyButton.addActionListener(this);

        this.setPreviousArgs();

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    @Override
    protected void setPreviousArgs() {
        if (!this.previousArguments.isEmpty()) this.txtField.setText(this.previousArguments.get(0));
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        this.checkReadyButton();

        if (actionEvent.getSource() == this.readyButton) {
            this.arguments.add(this.txtField.getText());
            this.onReadyButtonClicked();
        }

        if (this.cancelButton == actionEvent.getSource()) {
            this.closeWindow();
        }
    }

    protected void closeWindow() {
        this.dispose();
    }

    @Override
    public void checkReadyButton() {
        this.readyButton.setEnabled(!this.txtField.getText().isBlank());
        this.updateToolTipText();
    }

    @Override
    public void updateToolTipText(boolean... conditions) {
        String readyButtonToolTipText = "";

        if (conditions[0]) {
            readyButtonToolTipText = String.format(
                "- %s",
                ConstantController.getString("operationForm.toolTip.indexer.typeName")
            );
        }

        UIManager.put("ToolTip.foreground", Color.RED);

        this.readyButton.setToolTipText(readyButtonToolTipText.isEmpty() ? null : readyButtonToolTipText);
    }
}
