package operations.unary;

import com.mxgraph.model.mxCell;
import entities.cells.Cell;
import entities.cells.OperationCell;
import exceptions.tree.TreeException;
import operations.IOperator;
import operations.Operation;
import operations.OperationErrorVerifier;
import sgbd.prototype.ComplexRowData;
import sgbd.query.Operator;
import sgbd.query.Tuple;
import sgbd.query.agregation.AvgAgregation;
import sgbd.query.agregation.MinAgregation;
import sgbd.query.sourceop.TableScan;
import sgbd.query.unaryop.GroupOperator;
import sgbd.table.Table;
import sgbd.util.statics.Util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Group implements IOperator {

    public static void main(String[] args){

        Table table = Table.loadFromHeader("/home/rhuan/Documents/input/mlbplayers.head");

        table.open();

        Operator op = new TableScan(table);

        Operator aux = new GroupOperator(op, "mlbplayers", "mlbplayers_Team", List.of(
                new MinAgregation("mlbplayers","mlbplayers_Id"),
                new MinAgregation("mlbplayers", "mlbplayers_Weightlbs")
        ));
        aux.open();

        while (aux.hasNext()) {

            Tuple t = aux.next();

            Map<String, String> row = new LinkedHashMap<>();

            for (Map.Entry<String, ComplexRowData> line : t)
                for (Map.Entry<String, byte[]> data : line.getValue())
                    switch (Util.typeOfColumn(line.getValue().getMeta(data.getKey()))) {
                        case "int" -> row.put(data.getKey(), line.getValue().getInt(data.getKey()).toString());
                        case "float" -> row.put(data.getKey(), line.getValue().getFloat(data.getKey()).toString());
                        default -> row.put(data.getKey(), line.getValue().getString(data.getKey()));
                    }

            System.out.println(row);
        }


    }

    public Group() {

    }

    @Override
    public void executeOperation(mxCell jCell, List<String> arguments) {

        OperationCell cell = (OperationCell) Cell.getCells().get(jCell);

        OperationErrorVerifier.ErrorMessage error = null;

        try {

            error = OperationErrorVerifier.ErrorMessage.NO_PARENT;
            OperationErrorVerifier.hasParent(cell);

            error = OperationErrorVerifier.ErrorMessage.NO_ONE_PARENT;
            OperationErrorVerifier.oneParent(cell);

            error = OperationErrorVerifier.ErrorMessage.PARENT_ERROR;
            OperationErrorVerifier.noParentError(cell);

            error = OperationErrorVerifier.ErrorMessage.NULL_ARGUMENT;
            OperationErrorVerifier.noNullArgument(arguments);

            error = OperationErrorVerifier.ErrorMessage.PARENT_WITHOUT_COLUMN;
            OperationErrorVerifier.parentContainsColumns(cell.getParents().get(0).getColumnsName(), arguments);

            error = null;

        } catch (TreeException e) {

            cell.setError(error);

        }

        if(error != null) return;

        Cell parentCell = cell.getParents().get(0);

        Operator operator = parentCell.getOperator();

        operator = new GroupOperator(operator, cell.getSourceTableNameByColumn(arguments.get(0)), arguments.get(0), List.of(
                new AvgAgregation(cell.getSourceTableNameByColumn(arguments.get(1)),arguments.get(1))
        ));

        Operation.operationSetter(cell, cell.getType().getSymbol() + arguments.toString(), arguments, operator);

    }

}
