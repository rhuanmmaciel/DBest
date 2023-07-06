package gui.frames.forms.operations.unary;

import com.mxgraph.model.mxCell;
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
        addExtraComponent(new JLabel("Nome da coluna: "), 0, 0, 1, 1);
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

        btnReady.setEnabled(!txtField.getText().isBlank());
        updateToolTipTxt();

    }

    @Override
    public void updateToolTipTxt() {

        String btnReadyToolTipText = "";

        if (txtField.getText().isBlank())
            btnReadyToolTipText = "- Digite algum nome para a coluna";

        UIManager.put("ToolTip.foreground", Color.RED);

        btnReady.setToolTipText(btnReadyToolTipText.isEmpty() ? null : btnReadyToolTipText);

    }

}
