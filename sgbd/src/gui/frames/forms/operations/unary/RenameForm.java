package gui.frames.forms.operations.unary;

import com.mxgraph.model.mxCell;
import controller.ConstantController;
import entities.Column;
import gui.frames.forms.operations.OperationForm;
import gui.frames.forms.operations.IOperationForm;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Objects;

public class RenameForm extends OperationForm implements IOperationForm, ActionListener{

    private final JTextField txtFieldNewName = new JTextField();
    private final JButton btnAdd = new JButton(ConstantController.getString("operationForm.add"));
    private final JButton btnRemove = new JButton(ConstantController.getString("operationForm.removeSources"));
    private final JTextArea textArea = new JTextArea();

    public RenameForm(mxCell jCell) {

        super(jCell);

        initializeGUI();

    }

    private void initializeGUI() {

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeWindow();
            }
        });

        centerPanel.removeAll();

        btnReady.addActionListener(this);
        btnCancel.addActionListener(this);

        textArea.setPreferredSize(new Dimension(300,300));
        textArea.setEditable(false);

        txtFieldNewName.getDocument().addDocumentListener(new DocumentListener() {
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

        btnAdd.addActionListener(this);
        btnRemove.addActionListener(this);

        addExtraComponent(new JLabel(ConstantController.getString("operationForm.source")+":"), 0, 0, 1, 1);
        addExtraComponent(comboBoxSource, 1, 0, 2, 1);
        addExtraComponent(new JLabel(ConstantController.getString("operationForm.newName")), 0, 1, 1, 1);
        addExtraComponent(txtFieldNewName, 1, 1, 2, 1);
        addExtraComponent(btnAdd, 0, 2, 1, 1);
        addExtraComponent(btnRemove, 2, 2, 1, 1);
        addExtraComponent(new JScrollPane(textArea), 0, 3, 3, 3);

        setPreviousArgs();
        verifyConditions();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

    }

    private void verifyConditions(){
        btnAdd.setEnabled(!txtFieldNewName.getText().isBlank() && !txtFieldNewName.getText().isEmpty());
        btnReady.setEnabled(!textArea.getText().isEmpty());
    }

    @Override
    protected void setPreviousArgs() {

        if(!previousArguments.isEmpty()){

            for(String element : previousArguments){

                String groupByColumnName = Column.removeSource(element);
                String groupByColumnSource = Column.removeName(element);

                comboBoxSource.setSelectedItem(groupByColumnSource);
                comboBoxColumn.setSelectedItem(groupByColumnName);

                if (comboBoxColumn.getItemCount() > 0)
                    updateColumns();

            }

        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == btnAdd) {

            if (comboBoxSource.getItemCount() > 0)
                updateColumns();

        } else if (e.getSource() == btnRemove) {

            textArea.setText("");

        } else if(e.getSource() == btnCancel){

            closeWindow();

        }else if (e.getSource() == btnReady) {

            arguments.addAll(List.of(textArea.getText().split("\n")));
            arguments.remove(0);
            btnReady();

        }

        verifyConditions();

    }

    private void updateColumns(){

        String column = Objects.requireNonNull(comboBoxSource.getSelectedItem()).toString()+
                ":"+txtFieldNewName.getText();
        String textColumnsPicked = textArea.getText() + "\n" + column;
        textArea.setText(textColumnsPicked);

    }

    protected void closeWindow() {
        dispose();
    }

}
