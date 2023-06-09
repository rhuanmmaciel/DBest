package operations;

import entities.Column;
import entities.cells.Cell;
import entities.cells.OperationCell;
import enums.OperationErrorType;
import exceptions.tree.ArgumentsException;
import exceptions.tree.ParentsAmountException;
import exceptions.tree.ParentsErrorException;
import util.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class OperationErrorVerifier {

//	public static void verifyError(List<OperationErrorType> possibleErrors, OperationCell cell, List<String> arguments) throws ParentsAmountException, ParentsErrorException, ArgumentsException {
//
//		List<OperationErrorType> loopableList = List.copyOf(possibleErrors);
//		for(OperationErrorType currentError : loopableList) {
//
//			switch (currentError){
//
//				case NO_PARENT -> hasParent(cell);
//				case NO_ONE_PARENT -> oneParent(cell);
//				case NULL_ARGUMENT -> noNullArgument(arguments);
//				case NO_ONE_ARGUMENT -> oneArgument(arguments);
//				case PARENT_ERROR -> noParentError(cell);
//				case PARENT_WITHOUT_COLUMN -> {
//				}
//				case NO_TWO_PARENTS -> {
//				}
//				case NO_TWO_ARGUMENTS -> {
//				}
//				case EMPTY_ARGUMENT -> noEmptyArgument(arguments);
//				case NO_PREFIX -> {
//				}
//				case SAME_SOURCE -> {
//				}
//			}
//
//			possibleErrors.remove(currentError);
//
//		}
//
//	}

	public static void everyoneHavePrefix(List<String> arguments, List<String> prefix) throws ArgumentsException{

		if(!arguments.stream().allMatch(x-> Utils.startsWithIgnoreCase(x, prefix)))
			throw new ArgumentsException("");

	}

	public static void haveDifferentSources(Cell parent1, Cell parent2) throws ArgumentsException{

		if(parent1.getColumns().stream().map(Column::getSource)
				.anyMatch(x -> parent2.getColumns().stream().map(Column::getSource).anyMatch(x::equals)))
			throw new ArgumentsException("");

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

	public static void noEmptyArgument(List<String> arguments) throws ArgumentsException{
		if(arguments.isEmpty()) throw new ArgumentsException("");
		if(arguments.get(0).isEmpty()) throw new ArgumentsException("");
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

	public static void parentContainsColumns(List<String> parent, List<String> columns, List<String> columnExceptions) throws ArgumentsException{

		List<String> filteredColumns = new ArrayList<>(columns);
		filteredColumns.removeIf(x ->
			 columnExceptions.stream().anyMatch(c -> c.equals(Column.hasSource(x) ? Column.removeSource(x) : x))
		);

		parentContainsColumns(parent, filteredColumns);

	}

	public static void parentContainsColumns(List<String> parent, List<String> columns) throws ArgumentsException {

		List<String> withSource = columns.stream().filter(Column::hasSource).toList();

		List<String> withoutSource = columns.stream().filter(x -> !Column.hasSource(x)).toList();

		if (!new HashSet<>(parent).containsAll(withSource))
			throw new ArgumentsException("");

		if(!new HashSet<>(parent.stream().map(x -> x.substring(x.indexOf(".") + 1)).toList()).containsAll(withoutSource))
			throw new ArgumentsException("");
	}
	
	public static void twoParents(OperationCell cell) throws ParentsAmountException {
		
		if(cell.getParents().size() != 2)
			throw new ParentsAmountException("");
		
	}
	
}
