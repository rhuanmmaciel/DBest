package operations;

import java.util.List;

import entities.cells.OperationCell;
import exceptions.ArgumentsException;
import exceptions.ParentsAmountException;
import exceptions.ParentsErrorException;

public class OperationErrorVerifier {

	public enum ErrorMessage {
		NO_PARENT, NO_ONE_PARENT, NULL_ARGUMENT, NO_ONE_ARGUMENT, PARENT_ERROR, PARENT_WITHOUT_COLUMN,
		NO_TWO_PARENTS, NO_TWO_ARGUMENTS
	}

	public static void hasParent(OperationCell cell) throws ParentsAmountException {

		if (!cell.hasParents())
			throw new ParentsAmountException("");

	}

	public static void oneParent(OperationCell cell) throws ParentsAmountException {

		if (cell.getParents().size() != 1)
			throw new ParentsAmountException("");

	}

	public static void noNullArgument(List<String> arguments) throws ArgumentsException {

		if (arguments == null)
			throw new ArgumentsException("");

	}

	public static void oneArgument(List<String> arguments) throws ArgumentsException {

		if (arguments.size() != 1)
			throw new ArgumentsException("");

	}
	
	public static void twoArguments(List<String> arguments) throws ArgumentsException{
		
		if(arguments.size() != 2)
			throw new ArgumentsException("");
		
	}

	public static void noParentError(OperationCell cell) throws ParentsErrorException {

		if (cell.hasParentErrors())
			throw new ParentsErrorException("");

	}

	public static void parentContainsColumns(List<String> parent, List<String> columns) throws ArgumentsException {

		if (!parent.containsAll(columns))
			throw new ArgumentsException("");

	}
	
	public static void twoParents(OperationCell cell) throws ParentsAmountException {
		
		if(cell.getParents().size() != 2)
			throw new ParentsAmountException("");
		
	}
	
}
