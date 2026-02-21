package gui.frames.forms.operations.unary;

import com.mxgraph.model.mxCell;
import controllers.ConstantController;
import entities.Column;
import gui.frames.forms.operations.IOperationForm;
import gui.frames.forms.operations.OperationForm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ProjectionForm extends OperationForm implements ActionListener, IOperationForm {

    private final JButton btnAdd = new JButton(ConstantController.getString("operationForm.add"));
    private final JButton btnRemove = new JButton(ConstantController.getString("operationForm.remove"));
    private final JButton btnClear = new JButton(ConstantController.getString("operationForm.clear"));
    private final JButton btnAddAll = new JButton(ConstantController.getString("operationForm.addAllColumns"));

    private final JPanel checkBoxPanel = new JPanel();
    private final ArrayList<JCheckBox> projectionCheckBoxes = new ArrayList<>();

    public ProjectionForm(mxCell jCell) {
        super(jCell);
        initGUI();
    }

    public void initGUI() {
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeWindow();
            }
        });

        btnReady.addActionListener(this);
        btnCancel.addActionListener(this);

        checkBoxPanel.setLayout(new BoxLayout(checkBoxPanel, BoxLayout.Y_AXIS));
        checkBoxPanel.setPreferredSize(new Dimension(300, 300));

        btnAdd.addActionListener(this);
        btnRemove.addActionListener(this);
        btnClear.addActionListener(this);
        btnAddAll.addActionListener(this);

        addExtraComponent(btnAdd, 0, 2, 1, 1);
        addExtraComponent(btnAddAll, 1, 2, 1, 1);
        addExtraComponent(btnRemove, 2, 2, 1, 1);
        addExtraComponent(btnClear, 3, 2, 1, 1);
        addExtraComponent(new JScrollPane(checkBoxPanel), 0, 3, 4, 3);

        setPreviousArgs();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    protected void setPreviousArgs() {
        if (!previousArguments.isEmpty()) {
            for (String element : previousArguments) {
                if (projectionCheckBoxes.stream().noneMatch(cb -> cb.getText().equals(element))) {
                    projectionCheckBoxes.add(new JCheckBox(element));

                    String columnName = Column.removeSource(element);
                    comboBoxColumn.removeItem(columnName);
                }
            }
            refreshCheckBoxPanel();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAdd) {
            if (comboBoxColumn.getItemCount() > 0) {
                addColumn();
            }
        } else if (e.getSource() == btnRemove) {
            List<JCheckBox> selectedBoxes = projectionCheckBoxes.stream()
                .filter(AbstractButton::isSelected)
                .collect(Collectors.toList());

            for (JCheckBox checkBox : selectedBoxes) {
                String columnName = Column.removeSource(checkBox.getText());
                comboBoxColumn.addItem(columnName);
            }

            projectionCheckBoxes.removeIf(AbstractButton::isSelected);
            refreshCheckBoxPanel();

        } else if (e.getSource() == btnClear) {
            projectionCheckBoxes.clear();
            restrictedColumns.clear();
            comboBoxColumn.removeAllItems();
            leftChild.getColumnNames().forEach(comboBoxColumn::addItem);
            refreshCheckBoxPanel();

        } else if (e.getSource() == btnCancel) {
            closeWindow();
        } else if (e.getSource() == btnReady) {
            arguments.addAll(projectionCheckBoxes.stream().map(JCheckBox::getText).toList());
            btnReady();
        } else if (e.getSource() == btnAddAll) {
            while (comboBoxColumn.getItemCount() != 0) {
                comboBoxColumn.setSelectedIndex(0);
                addColumn();
            }
        }
    }

    private void addColumn() {
        String column = Objects.requireNonNull(comboBoxSource.getSelectedItem()) +
            "." +
            Objects.requireNonNull(comboBoxColumn.getSelectedItem());

        projectionCheckBoxes.add(new JCheckBox(column));
        refreshCheckBoxPanel();

        restrictedColumns.add(column);
        comboBoxColumn.removeItemAt(comboBoxColumn.getSelectedIndex());
    }

    private void refreshCheckBoxPanel() {
        checkBoxPanel.removeAll();
        for (JCheckBox checkBox : projectionCheckBoxes) {
            checkBoxPanel.add(checkBox);
        }
        checkBoxPanel.revalidate();
        checkBoxPanel.repaint();
    }

    protected void closeWindow() {
        dispose();
    }
}
