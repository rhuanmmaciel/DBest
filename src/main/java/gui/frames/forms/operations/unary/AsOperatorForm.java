package gui.frames.forms.operations.unary;

import controllers.ConstantController;
import gui.frames.forms.FormBase;
import gui.frames.forms.IFormCondition;

import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicReference;

public class AsOperatorForm extends FormBase implements ActionListener, IFormCondition {

    private final JTextField textField = new JTextField();

    private final AtomicReference<Boolean> cancelService;

    public AsOperatorForm(AtomicReference<Boolean> cancelService) {

        super(null);
        setModal(true);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeWindow();
            }
        });

        this.cancelService = cancelService;

        btnCancel.addActionListener(this);
        btnReady.addActionListener(this);

        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkBtnReady();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkBtnReady();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkBtnReady();
            }
        });

        contentPanel.add(textField, BorderLayout.CENTER);

        checkBtnReady();

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.revalidate();
    }

    public String getNewName(){
        return textField.getText();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource() == this.btnCancel){
            cancelService.set(true);
            dispose();
        }

        if(e.getSource() == this.btnReady){
            dispose();
        }

    }

    private void closeWindow() {
        dispose();
    }

    @Override
    public void checkBtnReady() {

        boolean isTxtFieldBlank = textField.getText().isBlank();

        btnReady.setEnabled(!isTxtFieldBlank);

        updateToolTipText(isTxtFieldBlank);

    }

    @Override
    public void updateToolTipText(boolean... conditions) {

        String btnReadyToolTipText = "";

        if (conditions[0]) {
            btnReadyToolTipText = "- "+ConstantController.getString("asOperatorForm.toolTipText.blank");
        }

        UIManager.put("ToolTip.foreground", Color.RED);

        this.btnReady.setToolTipText(btnReadyToolTipText.isEmpty() ? null : btnReadyToolTipText);
    }
}
