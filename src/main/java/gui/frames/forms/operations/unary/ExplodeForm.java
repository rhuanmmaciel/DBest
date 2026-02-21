package gui.frames.forms.operations.unary;

import com.mxgraph.model.mxCell;
import controllers.ConstantController;
import entities.Column;
import gui.frames.forms.operations.IOperationForm;
import gui.frames.forms.operations.OperationForm;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.DocumentFilter.FilterBypass;

public class ExplodeForm extends OperationForm implements ActionListener, IOperationForm {

    private final JTextField txtFieldDelimiter = new JTextField();

    public ExplodeForm(mxCell jCell) {

        super(jCell);

        initGUI();

    }

    public void initGUI() {

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeWindow();
            }
        });

        addExtraComponent(new JLabel(ConstantController.getString("operationForm.delimiter")), 0, 3, 1, 1);
        addExtraComponent(txtFieldDelimiter, 1, 3, 2, 1);

        txtFieldDelimiter.getDocument().addDocumentListener(new DocumentListener() {
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

        // Create a DocumentFilter to limit the number of characters to 1
        ((AbstractDocument) txtFieldDelimiter.getDocument()).setDocumentFilter(new DocumentFilter() {

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                int currentLength = fb.getDocument().getLength();
                int overLimit = (currentLength + text.length()) - 1 - length;
                if (overLimit > 0) {
                    text = text.substring(0, text.length() - overLimit);
                }
                if (text.length() > 0) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });

        btnCancel.addActionListener(this);
        btnReady.addActionListener(this);

        setPreviousArgs();
        verifyConditions();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

    }

    private void verifyConditions() {
        btnReady.setEnabled(
                !txtFieldDelimiter.getText().isBlank() && !txtFieldDelimiter.getText().isEmpty()
        );
    }

    @Override
    protected void setPreviousArgs() {

        if (!previousArguments.isEmpty()) {

            String column = previousArguments.get(0);
            String columnName = Column.removeSource(column);
            String columnSource = Column.removeName(column);

            comboBoxSource.setSelectedItem(columnSource);
            comboBoxColumn.setSelectedItem(columnName);
        }
        if (previousArguments.size() > 1) {
            txtFieldDelimiter.setText(previousArguments.get(1));
        }

    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {

        if (actionEvent.getSource() == btnReady) {

            arguments.add(Column.composeSourceAndName(Objects.requireNonNull(comboBoxSource.getSelectedItem()).toString(),
                    Objects.requireNonNull(comboBoxColumn.getSelectedItem()).toString()));
            arguments.add(txtFieldDelimiter.getText());
            btnReady();

        } else if (actionEvent.getSource() == btnCancel) {

            closeWindow();

        }
    }

    protected void closeWindow() {
        dispose();
    }
}
