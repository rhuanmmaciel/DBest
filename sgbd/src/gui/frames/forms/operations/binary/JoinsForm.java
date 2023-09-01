package gui.frames.forms.operations.binary;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Optional;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import com.mxgraph.model.mxCell;

import entities.Column;
import entities.cells.Cell;
import entities.utils.cells.CellUtils;
import gui.frames.forms.operations.IOperationForm;
import gui.frames.forms.operations.OperationForm;

public class JoinsForm extends OperationForm implements ActionListener, IOperationForm {

    private final JComboBox<String> comboBoxSource2 = new JComboBox<>();

    private final JComboBox<String> comboBoxColumn2 = new JComboBox<>();

    private final Cell parent2;

    public JoinsForm(mxCell jCell) {
        super(jCell);

		Optional<Cell> optionalCell = CellUtils.getActiveCell(jCell);

		this.parent2 = optionalCell.map(cell -> cell.getParents().get(1)).orElse(null);

        this.initializeGUI();
    }

    private void initializeGUI() {

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                JoinsForm.this.closeWindow();
            }
        });

        this.centerPanel.removeAll();

        this.readyButton.addActionListener(this);
        this.cancelButton.addActionListener(this);

        this.addExtraComponent(new JLabel("Fonte 1"), 0, 0, 1, 1);
        this.addExtraComponent(new JLabel("Coluna 1"), 1, 0, 1, 1);
        this.addExtraComponent(this.comboBoxSource, 0, 1, 1, 1);
        this.addExtraComponent(this.comboBoxColumn, 1, 1, 1, 1);
        this.addExtraComponent(new JLabel(" "), 0, 2, 1, 1);
        this.addExtraComponent(new JLabel("Fonte 2"), 0, 3, 1, 1);
        this.addExtraComponent(new JLabel("Coluna 2"), 1, 3, 1, 1);
        this.addExtraComponent(this.comboBoxSource2, 0, 4, 1, 1);
        this.addExtraComponent(this.comboBoxColumn2, 1, 4, 1, 1);
        this.addExtraComponent(new JLabel(" "), 0, 5, 1, 1);

        this.parent2.getColumns().stream()
            .map(Column::getSource).distinct()
            .forEach(this.comboBoxSource2::addItem);

        this.comboBoxSource2.addActionListener(actionEvent -> this.setColumns(this.comboBoxColumn2, this.comboBoxSource2, this.parent2));

        this.setColumns(this.comboBoxColumn2, this.comboBoxSource2, this.parent2);
        this.setPreviousArgs();

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);

    }

    @Override
    protected void setPreviousArgs() {

        if (!this.previousArguments.isEmpty()) {

            String column1 = this.previousArguments.get(0);
            String column2 = this.previousArguments.get(1);

            String column1Name = Column.removeSource(column1);
            String column1Source = Column.removeName(column1);

            String column2Name = Column.removeSource(column2);
            String column2Source = Column.removeName(column2);

            this.comboBoxSource.setSelectedItem(column1Source);
            this.comboBoxColumn.setSelectedItem(column1Name);
            this.comboBoxSource2.setSelectedItem(column2Source);
            this.comboBoxColumn2.setSelectedItem(column2Name);

        }

    }

    @Override
    public void actionPerformed(ActionEvent event) {

        if (event.getSource() == this.readyButton) {

            this.arguments.add(this.comboBoxSource.getSelectedItem() + "." + this.comboBoxColumn.getSelectedItem());
            this.arguments.add(this.comboBoxSource2.getSelectedItem() + "." + this.comboBoxColumn2.getSelectedItem());
            this.onReadyButtonClicked();

        } else if (event.getSource() == this.cancelButton) {
            this.closeWindow();
		}

    }

    protected void closeWindow() {
        this.dispose();
    }
}
