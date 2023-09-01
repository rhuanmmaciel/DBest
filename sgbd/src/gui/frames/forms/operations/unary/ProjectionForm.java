package gui.frames.forms.operations.unary;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.mxgraph.model.mxCell;

import entities.Column;

import gui.frames.forms.operations.IOperationForm;
import gui.frames.forms.operations.OperationForm;

public class ProjectionForm extends OperationForm implements ActionListener, IOperationForm {

    private final JButton addButton = new JButton("Adicionar");

    private final JButton removeButton = new JButton("Remover colunas");

    private final JButton addAllButton = new JButton("Adicionar todas");

    private final JTextArea textArea = new JTextArea();

    public ProjectionForm(mxCell jCell) {
        super(jCell);

        this.initializeComponents();
    }

    private void initializeComponents() {
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent event) {
                ProjectionForm.this.closeWindow();
            }
        });

        this.readyButton.addActionListener(this);
        this.cancelButton.addActionListener(this);

        this.textArea.setPreferredSize(new Dimension(300, 300));
        this.textArea.setEditable(false);

        this.addButton.addActionListener(this);
        this.removeButton.addActionListener(this);
        this.addAllButton.addActionListener(this);

        this.addExtraComponent(this.addButton, 0, 2, 1, 1);
        this.addExtraComponent(this.addAllButton, 1, 2, 1, 1);
        this.addExtraComponent(this.removeButton, 2, 2, 1, 1);
        this.addExtraComponent(new JScrollPane(this.textArea), 0, 3, 3, 3);

        this.setPreviousArgs();

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    @Override
    protected void setPreviousArgs() {
        if (this.previousArguments.isEmpty()) return;

        for (String element : this.previousArguments) {
            String columnName = Column.removeSource(element);
            String sourceName = Column.removeName(element);

            this.comboBoxSource.setSelectedItem(sourceName);
            this.comboBoxColumn.setSelectedItem(columnName);

            if (this.comboBoxColumn.getItemCount() > 0) {
                this.updateColumns();
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == this.addButton) {
            if (this.comboBoxColumn.getItemCount() > 0) {
                this.updateColumns();
            }
        } else if (event.getSource() == this.removeButton) {
            this.textArea.setText("");
            this.restrictedColumns.clear();
            this.comboBoxColumn.removeAllItems();
            this.parent1.getColumnNames().forEach(this.comboBoxColumn::addItem);
        } else if (event.getSource() == this.cancelButton) {
            this.closeWindow();
        } else if (event.getSource() == this.readyButton) {
            this.arguments.addAll(List.of(this.textArea.getText().split("\n")));
            this.onReadyButtonClicked();
        } else if (event.getSource() == this.addAllButton) {
            while (this.comboBoxColumn.getItemCount() != 0) {
                this.updateColumns();
            }
        }
    }

    private void updateColumns() {
        Object selectedSource = Objects.requireNonNull(this.comboBoxSource.getSelectedItem());
        Object selectedColumn = Objects.requireNonNull(this.comboBoxColumn.getSelectedItem());

        String sourceAndColumn = String.format("%s.%s", selectedSource, selectedColumn);

        String oldText = this.textArea.getText();
        String newText;

        if (oldText == null || oldText.isBlank()) {
            newText = sourceAndColumn;
        } else {
            newText = String.format("%s\n%s", oldText, sourceAndColumn);
        }

        this.restrictedColumns.add(sourceAndColumn);
        this.comboBoxColumn.removeItemAt(this.comboBoxColumn.getSelectedIndex());
        this.textArea.setText(newText);
    }

    protected void closeWindow() {
        this.dispose();
    }
}
