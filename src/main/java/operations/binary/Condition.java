package operations.binary;

import booleanexpression.BooleanExpressionRecognizer;
import com.mxgraph.model.mxCell;
import entities.cells.Cell;
import entities.cells.OperationCell;
import entities.utils.cells.CellUtils;
import enums.OperationErrorType;
import exceptions.tree.TreeException;
import ibd.query.lookup.ExpressionConverter;
import ibd.query.lookup.LookupFilter;
import operations.IOperator;
import operations.Operation;
import operations.OperationErrorVerifier;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import lib.booleanexpression.entities.expressions.BooleanExpression;

public class Condition implements IOperator {

    @Override
    public void executeOperation(mxCell jCell, List<String> arguments, String alias) {
        Optional<Cell> optionalCell = CellUtils.getActiveCell(jCell);

        if (optionalCell.isEmpty()) return;

        OperationCell cell = (OperationCell) optionalCell.get();
        OperationErrorType errorType = null;

        try {

            errorType = OperationErrorType.NOT_SOURCE;
            OperationErrorVerifier.notSource(cell);

            errorType = null;
        } catch (TreeException exception) {
            cell.setError(errorType);
        }

        if (errorType != null) return;

        ibd.query.Operation readyOperator = null;
        String expression = arguments.get(0);

            
        try {
            BooleanExpression booleanExpression = new BooleanExpressionRecognizer(jCell).recognizer(expression);
            LookupFilter filter = ExpressionConverter.convert(booleanExpression);
            readyOperator = new ibd.query.unaryop.filter.Condition(filter);
            
            String formattedAlias = "";
            if (!alias.isBlank())
                formattedAlias = ":"+alias;
            
            
            String operationName = String.format("%s%s  %s", cell.getType().symbol, formattedAlias, new BooleanExpressionRecognizer(jCell).recognizer(booleanExpression));

            
            Operation.operationSetter(cell, operationName, arguments, readyOperator);
        
        } catch (Exception ex) {
            Logger.getLogger(Condition.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
}
