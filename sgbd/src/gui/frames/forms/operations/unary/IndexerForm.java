package gui.frames.forms.operations.unary;

import com.mxgraph.model.mxCell;
import controller.ConstantController;
import gui.frames.forms.IFormCondition;
import gui.frames.forms.operations.IOperationForm;
import gui.frames.forms.operations.OperationForm;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class IndexerForm extends OperationForm implements IOperationForm, ActionListener, IFormCondition {

    private final JTextField txtField = new JTextField();

    public IndexerForm(mxCell jCell) {

        super(jCell);

        initGUI();

        checkBtnReady();

    }

    private void initGUI(){

        centerPanel.removeAll();
        addExtraComponent(new JLabel(ConstantController.getString("operationForm.columnName")+": "), 0, 0, 1, 1);
        addExtraComponent(txtField, 1, 0, 2, 1);
        txtField.setMinimumSize(new Dimension(50, txtField.getHeight()));
        txtField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                checkBtnReady();
            }
            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                checkBtnReady();
            }
            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                checkBtnReady();
            }
        });
        btnCancel.addActionListener(this);
        btnReady.addActionListener(this);

        setPreviousArgs();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

    }

    @Override
    protected void setPreviousArgs() {

        if(!previousArguments.isEmpty()) txtField.setText(previousArguments.get(0));

    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {

        checkBtnReady();

        if(actionEvent.getSource() == btnReady){
            arguments.add(txtField.getText());
            btnReady();

        }

        if(btnCancel == actionEvent.getSource()){
            closeWindow();
        }

    }

    protected void closeWindow() {
        dispose();
    }

    @Override
    public void checkBtnReady() {

        boolean hasName = !txtField.getText().isBlank();
        btnReady.setEnabled(hasName);
        updateToolTipTxt(hasName);

    }

    @Override
    public void updateToolTipTxt(boolean ...conditions) {

        String btnReadyToolTipText = "";

        if (conditions[0])
            btnReadyToolTipText = "- "+ConstantController.getString("operationForm.toolTip.indexer.typeName");

        UIManager.put("ToolTip.foreground", Color.RED);

        btnReady.setToolTipText(btnReadyToolTipText.isEmpty() ? null : btnReadyToolTipText);

    }

}
