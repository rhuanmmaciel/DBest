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
import sgbd.query.unaryop.SortOperator;
import sgbd.util.statics.Util;

import java.util.*;

public class Sort implements IOperator {

    public Sort() {

    }

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
            OperationErrorVerifier.parentContainsColumns(cell.getParents().get(0).getColumnsName(),
                    Collections.singletonList(arguments.get(0).replace("ASC:", "").replace("DESC:", "")));
            error = null;

        } catch (TreeException e) {

            cell.setError(error);

        }

        if(error != null) return;

        Cell parentCell = cell.getParents().get(0);

        Operator operator = parentCell.getOperator();

        operator = new SortOperator(operator, new Comparator<Tuple>() {
            @Override
            public int compare(Tuple entries, Tuple t1) {

                String column = arguments.get(0);

                boolean ascendingOrder = !column.startsWith("DESC:");

                column = column.replace("ASC:", "").replace("DESC:","");

                String item1 = "";
                for (Map.Entry<String, ComplexRowData> line : entries)
                    for (Map.Entry<String, byte[]> data : line.getValue())
                        if(Objects.equals(data.getKey(), column))
                            switch (Util.typeOfColumn(line.getValue().getMeta(data.getKey()))) {
                                case "int" -> item1 = line.getValue().getInt(data.getKey()).toString();
                                case "float" -> item1 = line.getValue().getFloat(data.getKey()).toString();
                                default -> item1 = line.getValue().getString(data.getKey());
                            }

                String item2 = "";
                for (Map.Entry<String, ComplexRowData> line : t1)
                    for (Map.Entry<String, byte[]> data : line.getValue())
                        if(Objects.equals(data.getKey(), column))
                            switch (Util.typeOfColumn(line.getValue().getMeta(data.getKey()))) {
                                case "int" -> item2 = line.getValue().getInt(data.getKey()).toString();
                                case "float" -> item2 = line.getValue().getFloat(data.getKey()).toString();
                                default -> item2 = line.getValue().getString(data.getKey());
                            }

               switch (Util.typeOfColumn(t1.getContent(cell.getSourceTableName(column)).getMeta(column))){
                    case "int" -> {

                        Integer n1 =Integer.parseInt(item1);
                        Integer n2 =Integer.parseInt(item2);

                        if(ascendingOrder) return n1.compareTo(n2);

                        return n2.compareTo(n1);

                    }
                    case "float" -> {

                        Float n1 = Float.parseFloat(item1);
                        Float n2 = Float.parseFloat(item2);

                        if(ascendingOrder) return n1.compareTo(n2);

                        return n2.compareTo(n1);

                    }
                    default ->{

                        if(ascendingOrder) return item1.compareTo(item2);

                        return item2.compareTo(item1);

                    }
                }

            }
        });

        Operation.operationSetter(cell, cell.getType().getSymbol() + arguments.toString(), arguments, operator);

    }

}
