package gui.frames.forms.operations.unary;

import com.mxgraph.model.mxCell;
import controllers.ConstantController;
import gui.frames.forms.operations.IOperationForm;
import gui.frames.forms.operations.OperationForm;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.DocumentFilter.FilterBypass;

public class AutoIncrementForm extends OperationForm implements IOperationForm, ActionListener {

    private final JTextField txtFieldCol = new JTextField();
    private final JTextField txtFieldInc = new JTextField();

    public AutoIncrementForm(mxCell jCell) {

        super(jCell);

        initGUI();

    }

    public void initGUI() {

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeWindow();
            }
        });

        centerPanel.removeAll();

        btnReady.addActionListener(this);
        btnCancel.addActionListener(this);

        txtFieldCol.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                verifyConditions();
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                verifyConditions();
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                verifyConditions();
            }
        });
        
        txtFieldInc.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                verifyConditions();
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                verifyConditions();
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                verifyConditions();
            }
        });
        
        ((AbstractDocument) txtFieldInc.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string.matches("\\d+")) { // Allow only digits
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text.matches("\\d+")) { // Allow only digits
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });

        addExtraComponent(new JLabel(ConstantController.getString("operationForm.colName")), 0, 0, 1, 1);
        addExtraComponent(txtFieldCol, 1, 0, 2, 1);
        
        addExtraComponent(new JLabel(ConstantController.getString("operationForm.incValue")), 0, 1, 1, 1);
        addExtraComponent(txtFieldInc, 1, 1, 2, 1);

        setPreviousArgs();
        verifyConditions();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

    }

    private void verifyConditions() {
        boolean enabled = (!txtFieldCol.getText().isBlank() && !txtFieldCol.getText().isEmpty() &&
                           !txtFieldInc.getText().isBlank() && !txtFieldInc.getText().isEmpty());
        btnReady.setEnabled(enabled);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == btnCancel) {

            closeWindow();

        } else if (e.getSource() == btnReady) {

            arguments.add(txtFieldCol.getText());
            arguments.add(txtFieldInc.getText());
            btnReady();

        }

        verifyConditions();

    }

    protected void closeWindow() {
        dispose();
    }

    @Override
    protected void setPreviousArgs() {
        if (!previousArguments.isEmpty()) {
            txtFieldCol.setText(previousArguments.get(0));
            txtFieldInc.setText(previousArguments.get(1));
        }
        else {
            txtFieldCol.setText("id");
            txtFieldInc.setText("1");
        
        }
    }

}
