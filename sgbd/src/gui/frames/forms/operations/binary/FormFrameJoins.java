package gui.frames.forms.operations.binary;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import com.mxgraph.model.mxCell;

import entities.Column;
import entities.cells.Cell;
import gui.frames.forms.operations.FormFrameOperation;
import gui.frames.forms.operations.IFormFrameOperation;

public class FormFrameJoins extends FormFrameOperation implements ActionListener, IFormFrameOperation {

	private final JComboBox<String> comboBoxSource2 = new JComboBox<>();
	private final JComboBox<String> comboBoxColumn2 = new JComboBox<>();

	private final Cell parent2;

	public FormFrameJoins(mxCell jCell) {

		super(jCell);

		this.parent2 = Cell.getCells().get(jCell).getParents().get(1);

		initializeGUI();

	}

	private void initializeGUI() {

		centerPanel.removeAll();

		btnReady.addActionListener(this);
		btnCancel.addActionListener(this);

		addExtraComponent(new JLabel("Fonte 1"), 0, 0, 1, 1);
		addExtraComponent(new JLabel("Coluna 1"), 1, 0, 1, 1);
		addExtraComponent(comboBoxSource, 0, 1, 1, 1);
		addExtraComponent(comboBoxColumn, 1, 1, 1, 1);
		addExtraComponent(new JLabel(" "), 0, 2, 1, 1);
		addExtraComponent(new JLabel("Fonte 2"), 0, 3, 1, 1);
		addExtraComponent(new JLabel("Coluna 2"), 1, 3, 1, 1);
		addExtraComponent(comboBoxSource2, 0, 4, 1, 1);
		addExtraComponent(comboBoxColumn2, 1, 4, 1, 1);
		addExtraComponent(new JLabel(" "), 0, 5, 1, 1);

		parent2.getColumns().stream()
				.map(Column::getSource).distinct()
				.forEach(comboBoxSource2::addItem);

		comboBoxSource2.addActionListener(actionEvent -> setColumns(comboBoxColumn2, comboBoxSource2, parent2));

		setColumns(comboBoxColumn2, comboBoxSource2, parent2);
		setPreviousArgs();

		pack();
		setLocationRelativeTo(null);
		setVisible(true);

	}

	@Override
	protected void setPreviousArgs() {

		if(!previousArguments.isEmpty()){

			String column1 = previousArguments.get(0);
			String column2 = previousArguments.get(1);

			String column1Name = Column.removeSource(column1);
			String column1Source = Column.removeName(column1);

			String column2Name = Column.removeSource(column2);
			String column2Source = Column.removeName(column2);

			comboBoxSource.setSelectedItem(column1Source);
			comboBoxColumn.setSelectedItem(column1Name);
			comboBoxSource2.setSelectedItem(column2Source);
			comboBoxColumn2.setSelectedItem(column2Name);

		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == btnReady) {

			arguments.add(comboBoxSource.getSelectedItem()+"."+comboBoxColumn.getSelectedItem());
			arguments.add(comboBoxSource2.getSelectedItem()+"."+comboBoxColumn2.getSelectedItem());
			btnReady();

		} else if (e.getSource() == btnCancel)
			closeWindow();

	}

	@Override
	protected void closeWindow() {
		dispose();
	}
}