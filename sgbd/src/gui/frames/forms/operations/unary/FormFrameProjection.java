package gui.frames.forms.operations.unary;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Objects;

import javax.swing.*;

import com.mxgraph.model.mxCell;

import gui.frames.forms.operations.FormFrameOperation;
import gui.frames.forms.operations.IFormFrameOperation;

public class FormFrameProjection extends FormFrameOperation implements ActionListener, IFormFrameOperation {

	private JButton btnAdd;
	private JButton btnRemove;
	private JTextArea textArea;

	private JButton btnAddAll;

	public FormFrameProjection(mxCell jCell) {

		super(jCell);

		initializeGUI();

	}

	private void initializeGUI() {

		btnReady.addActionListener(this);
		btnCancel.addActionListener(this);

		textArea = new JTextArea();
		textArea.setPreferredSize(new Dimension(300,300));

		btnAdd = new JButton("Adicionar");
		btnAdd.addActionListener(this);

		btnRemove = new JButton("Remover colunas");
		btnRemove.addActionListener(this);

		btnAddAll = new JButton("Adicionar todas");
		btnAddAll.addActionListener(this);

		addExtraComponent(btnAdd, 0, 2, 1, 1);
		addExtraComponent(btnAddAll, 1, 2, 1, 1);
		addExtraComponent(btnRemove, 2, 2, 1, 1);

		addExtraComponent(new JScrollPane(textArea), 0, 3, 3, 3);

		pack();
		setLocationRelativeTo(null);
		setVisible(true);

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

		String column = Objects.requireNonNull(comboBoxSource.getSelectedItem()).toString()+
				"."+
				Objects.requireNonNull(comboBoxColumn.getSelectedItem()).toString();
		String textColumnsPicked = textArea.getText() + "\n" + column;
		restrictedColumns.add(column);
		comboBoxColumn.removeItemAt(comboBoxColumn.getSelectedIndex());
		textArea.setText(textColumnsPicked);

	}

}
