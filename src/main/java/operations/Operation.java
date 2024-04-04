package operations;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;

import entities.cells.OperationCell;

import entities.utils.cells.CellUtils;
import gui.frames.main.MainFrame;

import sgbd.query.Operator;

import javax.swing.*;

import java.awt.*;

import java.util.List;

import static entities.utils.cells.CellUtils.changeCellName;

public class Operation {

	private Operation() {

	}

	public static void operationSetter(OperationCell cell, String name, List<String> arguments, Operator operator) {
		mxCell jCell = cell.getJCell();

		cell.setOperator(operator);
		cell.setName(name);
		cell.setArguments(arguments);
		cell.removeError();

		changeCellName(jCell, name);

	}
}
