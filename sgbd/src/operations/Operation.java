package operations;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;

import entities.cells.OperationCell;

import gui.frames.main.MainFrame;

import sgbd.query.Operator;

import javax.swing.*;

import java.awt.*;

import java.util.List;

public class Operation {

	private Operation() {

	}

	public static void operationSetter(OperationCell cell, String name, List<String> arguments, Operator operator) {
		mxCell jCell = cell.getJCell();

		cell.setOperator(operator);
		cell.setName(name);
		cell.setArguments(arguments);
		cell.removeError();

		MainFrame.getGraph().getModel().setValue(jCell, name);

		JLabel label = new JLabel();
		Font font = new Font("Arial", Font.PLAIN, 12);
		label.setFont(font);

		String text = (String) jCell.getValue();
		FontMetrics fontMetrics = label.getFontMetrics(font);
		int textWidth = fontMetrics.stringWidth(text);

		mxGeometry oldGeometry = jCell.getGeometry();

		mxGeometry newGeometry = new mxGeometry(
			oldGeometry.getX(), oldGeometry.getY(), textWidth, oldGeometry.getHeight()
		);

		MainFrame.getGraph().getModel().setGeometry(jCell, newGeometry);
	}
}
