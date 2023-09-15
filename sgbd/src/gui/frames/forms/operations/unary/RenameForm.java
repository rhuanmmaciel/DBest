package gui.frames.forms.operations.unary;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.mxgraph.model.mxCell;

import entities.Column;
import gui.frames.forms.operations.IOperationForm;
import gui.frames.forms.operations.OperationForm;

public class RenameForm extends OperationForm implements IOperationForm, ActionListener {

    private final JTextField txtFieldNewName = new JTextField();

    private final JButton btnAdd = new JButton("Adicionar");

    private final JButton btnRemove = new JButton("Remover sources");

    private final JTextArea textArea = new JTextArea();

    public RenameForm(mxCell jCell) {

        super(jCell);

        this.initializeGUI();
    }

    private void initializeGUI() {

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent event) {
                RenameForm.this.closeWindow();
            }
        });

        this.centerPanel.removeAll();

        this.readyButton.addActionListener(this);
        this.cancelButton.addActionListener(this);

        this.textArea.setPreferredSize(new Dimension(300, 300));
        this.textArea.setEditable(false);

        this.txtFieldNewName.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                RenameForm.this.verifyConditions();
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                RenameForm.this.verifyConditions();
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                RenameForm.this.verifyConditions();
            }
        });

        this.btnAdd.addActionListener(this);
        this.btnRemove.addActionListener(this);

        this.addExtraComponent(new JLabel("Source:"), 0, 0, 1, 1);
        this.addExtraComponent(this.comboBoxSource, 1, 0, 2, 1);
        this.addExtraComponent(new JLabel("Novo nome"), 0, 1, 1, 1);
        this.addExtraComponent(this.txtFieldNewName, 1, 1, 2, 1);
        this.addExtraComponent(this.btnAdd, 0, 2, 1, 1);
        this.addExtraComponent(this.btnRemove, 2, 2, 1, 1);
        this.addExtraComponent(new JScrollPane(this.textArea), 0, 3, 3, 3);

        this.setPreviousArgs();
        this.verifyConditions();

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void verifyConditions() {
        this.btnAdd.setEnabled(!this.txtFieldNewName.getText().isBlank() && !this.txtFieldNewName.getText().isEmpty());
        this.readyButton.setEnabled(!this.textArea.getText().isEmpty());
    }

    @Override
    protected void setPreviousArgs() {

        if (!this.previousArguments.isEmpty()) {

            for (String element : this.previousArguments) {

                String groupByColumnName = Column.removeSource(element);
                String groupByColumnSource = Column.removeName(element);

                this.comboBoxSource.setSelectedItem(groupByColumnSource);
                this.comboBoxColumn.setSelectedItem(groupByColumnName);

                if (this.comboBoxColumn.getItemCount() > 0) {
                    this.updateColumns();
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == this.btnAdd) {

            if (this.comboBoxSource.getItemCount() > 0) {
                this.updateColumns();
            }
        } else if (e.getSource() == this.btnRemove) {

            this.textArea.setText("");
        } else if (e.getSource() == this.cancelButton) {

            this.closeWindow();
        } else if (e.getSource() == this.readyButton) {

            this.arguments.addAll(List.of(this.textArea.getText().split("\n")));
            this.arguments.remove(0);
            this.onReadyButtonClicked();
        }

        this.verifyConditions();
    }

    private void updateColumns() {

        String column = Objects.requireNonNull(this.comboBoxSource.getSelectedItem()) + ":" + this.txtFieldNewName.getText();
        String textColumnsPicked = this.textArea.getText() + "\n" + column;
        this.textArea.setText(textColumnsPicked);
    }

    protected void closeWindow() {
        this.dispose();
    }
}
