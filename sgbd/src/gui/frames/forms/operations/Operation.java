package gui.frames.forms.operations;

import java.awt.Font;
import java.awt.FontMetrics;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;

import controller.MainController;
import entities.Column;
import entities.cells.Cell;
import entities.cells.OperationCell;
import sgbd.query.Operator;

public class Operation {

	public static void operationSetter(OperationCell cell, String name, List<String> arguments, Operator operator) {
		
		mxCell jCell = cell.getJGraphCell();
		
		List<List<Column>> parentsColumns = new ArrayList<>();
		
		for(Cell parent : cell.getParents())
			parentsColumns.addAll(List.of(parent.getColumns()));
			
		cell.setColumns(parentsColumns, operator.getContentInfo().values());

		cell.setOperator(operator);

		cell.setName(name);

		cell.setArguments(arguments);

		cell.removeError();

		MainController.getGraph().getModel().setValue(jCell, name);
		
		JLabel label = new JLabel();
		Font font = new Font("Arial", Font.PLAIN, 12); 
		label.setFont(font);

		String texto = (String)jCell.getValue(); 
		FontMetrics metrics = label.getFontMetrics(font);
		int largura = metrics.stringWidth(texto);
		
		mxGeometry newGeometry = new mxGeometry(jCell.getGeometry().getX(), jCell.getGeometry().getY(), largura,
				jCell.getGeometry().getHeight());
		MainController.getGraph().getModel().setGeometry(jCell, newGeometry);
		
	}
	
}
