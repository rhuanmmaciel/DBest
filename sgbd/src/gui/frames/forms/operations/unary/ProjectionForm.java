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
import java.util.List;
import java.util.Objects;

public class ProjectionForm extends OperationForm implements ActionListener, IOperationForm {

	private final JButton btnAdd = new JButton(ConstantController.getString("operationForm.add"));
	private final JButton btnRemove = new JButton(ConstantController.getString("operationForm.removeColumns"));
	private final JButton btnAddAll = new JButton(ConstantController.getString("operationForm.addAllColumns"));
	private final JTextArea textArea = new JTextArea();

	public ProjectionForm(mxCell jCell) {

		super(jCell);

		initGUI();

	}

	@Override
	public void initGUI() {

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				closeWindow();
			}
		});

		btnReady.addActionListener(this);
		btnCancel.addActionListener(this);

		textArea.setPreferredSize(new Dimension(300,300));
		textArea.setEditable(false);

		btnAdd.addActionListener(this);
		btnRemove.addActionListener(this);
		btnAddAll.addActionListener(this);

		addExtraComponent(btnAdd, 0, 2, 1, 1);
		addExtraComponent(btnAddAll, 1, 2, 1, 1);
		addExtraComponent(btnRemove, 2, 2, 1, 1);
		addExtraComponent(new JScrollPane(textArea), 0, 3, 3, 3);

		setPreviousArgs();

		pack();
		setLocationRelativeTo(null);
		setVisible(true);

	}

	@Override
	protected void setPreviousArgs() {

		if(!previousArguments.isEmpty()){

			for(String element : previousArguments){

				String columnName = Column.removeSource(element);
				String sourceName = Column.removeName(element);

				comboBoxSource.setSelectedItem(sourceName);
				comboBoxColumn.setSelectedItem(columnName);

				if (comboBoxColumn.getItemCount() > 0)
					updateColumns();

			}

		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == btnAdd) {

			if (comboBoxColumn.getItemCount() > 0)
				updateColumns();

		} else if (e.getSource() == btnRemove) {

			textArea.setText("");

			restrictedColumns.clear();
			comboBoxColumn.removeAllItems();

			parent1.getColumnNames().forEach(comboBoxColumn::addItem);

		} else if(e.getSource() == btnCancel){

			closeWindow();

		}else if (e.getSource() == btnReady) {
			
			arguments.addAll(List.of(textArea.getText().split("\n")));
			arguments.remove(0);
			btnReady();

		}  else if (e.getSource() == btnAddAll) {

			while (comboBoxColumn.getItemCount() != 0) {

				updateColumns();

			}

		}
	}

	private void updateColumns(){

		String column = Objects.requireNonNull(comboBoxSource.getSelectedItem())+
				"."+
				Objects.requireNonNull(comboBoxColumn.getSelectedItem());
		String textColumnsPicked = textArea.getText() + "\n" + column;
		restrictedColumns.add(column);
		comboBoxColumn.removeItemAt(comboBoxColumn.getSelectedIndex());
		textArea.setText(textColumnsPicked);

	}

	protected void closeWindow() {
		dispose();
	}
}
