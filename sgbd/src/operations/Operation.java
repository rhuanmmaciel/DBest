package operations;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import controller.MainController;
import entities.cells.OperationCell;
import sgbd.query.Operator;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Operation {

	public static void operationSetter(OperationCell cell, String name, List<String> arguments, Operator operator) {

		mxCell jCell = cell.getJGraphCell();

		cell.setOperator(operator);

		cell.setName(name);

		cell.setArguments(arguments);

		cell.removeError();

		MainController.getGraph().getModel().setValue(jCell, name);

		JLabel label = new JLabel();
		Font font = new Font("Arial", Font.PLAIN, 12);
		label.setFont(font);

		String text = (String) jCell.getValue();
		FontMetrics metrics = label.getFontMetrics(font);
		int textWidth = metrics.stringWidth(text);

		mxGeometry newGeometry = new mxGeometry(jCell.getGeometry().getX(), jCell.getGeometry().getY(), textWidth,
				jCell.getGeometry().getHeight());
		MainController.getGraph().getModel().setGeometry(jCell, newGeometry);

	}

}
