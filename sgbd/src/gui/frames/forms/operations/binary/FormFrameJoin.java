package gui.frames.forms.operations.binary;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import com.mxgraph.model.mxCell;

import entities.cells.Cell;
import gui.frames.forms.operations.FormFrameOperation;
import gui.frames.forms.operations.IFormFrameOperation;

public class FormFrameJoin extends FormFrameOperation implements ActionListener, IFormFrameOperation {

	private final JComboBox<String> comboBoxSource2 = new JComboBox<>();
	private final JComboBox<String> comboBoxColumn2 = new JComboBox<>();

	private final Cell parent2;

	public FormFrameJoin(mxCell jCell) {

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

		parent2.getAllSourceTables().stream()
				.map(Cell::getName)
				.forEach(comboBoxSource2::addItem);

		comboBoxSource2.addActionListener(actionEvent -> setColumns(comboBoxColumn2, comboBoxSource2, parent2));

		setColumns(comboBoxColumn2, comboBoxSource2, parent2);

		pack();
		setLocationRelativeTo(null);
		setVisible(true);

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

}