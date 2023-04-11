package gui.frames.forms.operations;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.mxgraph.model.mxCell;

import controller.ActionClass;
import entities.Cell;
import entities.OperationCell;
import sgbd.query.Operator;
import sgbd.query.binaryop.joins.BlockNestedLoopJoin;

public class CartesianProduct implements IOperator {

	public CartesianProduct(mxCell jCell, AtomicReference<Boolean> exitReference) {

		executeOperation(jCell, null);

	}

	public void executeOperation(mxCell jCell, List<String> data) {
		
		OperationCell cell = (OperationCell) ActionClass.getCells().get(jCell);
		Cell parentCell1 = cell.getParents().get(0);
		Cell parentCell2 = cell.getParents().get(1);
		
		Operator table1 = parentCell1.getOperator();
		Operator table2 = parentCell2.getOperator();

		Operator operator = new BlockNestedLoopJoin(table1, table2, (t1, t2) -> {
			return true;
		});

		cell.setColumns(List.of(parentCell1.getColumns(), parentCell2.getColumns()),
				operator.getContentInfo().values());
		cell.setOperator(operator);
		cell.setName(parentCell1.getName() + " X " + parentCell2.getName());
		cell.setData(data);
		
		ActionClass.getGraph().getModel().setValue(jCell, "X");

	}
}
