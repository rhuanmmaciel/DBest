package operations.binary;

import com.mxgraph.model.mxCell;
import entities.cells.Cell;
import entities.cells.OperationCell;
import entities.utils.cells.CellUtils;
import enums.OperationErrorType;
import exceptions.tree.TreeException;
import operations.IOperator;
import operations.Operation;
import operations.OperationErrorVerifier;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class And implements IOperator {

    @Override
    public void executeOperation(mxCell jCell, List<String> arguments, String alias) {
        Optional<Cell> optionalCell = CellUtils.getActiveCell(jCell);

        if (optionalCell.isEmpty()) return;

        OperationCell cell = (OperationCell) optionalCell.get();
        OperationErrorType errorType = null;

        try {
            errorType = OperationErrorType.NO_PARENT;
            OperationErrorVerifier.hasParent(cell);

            errorType = OperationErrorType.NO_TWO_PARENTS;
            OperationErrorVerifier.twoParents(cell);

            errorType = OperationErrorType.PARENT_ERROR;
            OperationErrorVerifier.noParentError(cell);

//            errorType = OperationErrorType.SAME_SOURCE;
//            OperationErrorVerifier.haveDifferentSources(cell.getParents().get(0), cell.getParents().get(1));

            errorType = null;
        } catch (TreeException exception) {
            cell.setError(errorType);
        }

        if (errorType != null) return;

        Cell parentCell1 = cell.getParents().get(0);
        Cell parentCell2 = cell.getParents().get(1);

        ibd.query.Operation operator1 = parentCell1.getOperator();
        ibd.query.Operation operator2 = parentCell2.getOperator();

        ibd.query.Operation readyOperator = null;
        try {
            readyOperator = new ibd.query.binaryop.conditional.LogicalAnd(operator1, operator2, alias);
        } catch (Exception ex) {
            Logger.getLogger(And.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String formattedAlias = "";
            if (!alias.isBlank())
                formattedAlias = ":"+alias;

        String operationName = String.format("%s%s", cell.getType().symbol,formattedAlias);
        Operation.operationSetter(cell, operationName, List.of(), readyOperator);
    }
}
