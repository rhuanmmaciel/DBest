package enums;

import controller.ConstantController;
import entities.Action.CreateOperationAction;
import gui.frames.forms.operations.BooleanExpressionForm;
import gui.frames.forms.operations.IOperationForm;
import gui.frames.forms.operations.unary.*;
import operations.IOperator;
import operations.binary.CartesianProduct;
import operations.binary.joins.Join;
import operations.binary.joins.LeftJoin;
import operations.binary.joins.RightJoin;
import operations.binary.set.Intersection;
import operations.binary.set.Union;
import operations.unary.*;

import java.util.*;

import static enums.OperationErrorType.*;

public enum OperationType {

	SELECTION(ConstantController.getString("operation.selection"), "σ", "selection", "selection[args](source)", OperationArity.UNARY, BooleanExpressionForm.class, Selection.class,
			   NO_ONE_ARGUMENT),
	PROJECTION(ConstantController.getString("operation.projection"), "π", "projection", "projection[args](source)",OperationArity.UNARY, ProjectionForm.class, Projection.class,
			   PARENT_WITHOUT_COLUMN),
	RENAME(ConstantController.getString("operation.rename"), "ρ", "rename", "rename[args](source)", OperationArity.UNARY, RenameForm.class, Rename.class),
	GROUP(ConstantController.getString("operation.group"), "G", "group", "group[args](relation)",OperationArity.UNARY, GroupForm.class, Group.class,
			   NO_ONE_ARGUMENT, PARENT_WITHOUT_COLUMN, NO_PREFIX),
	AGGREGATION(ConstantController.getString("operation.aggregation"), "G", "aggregation", "aggregation[args](relation)",OperationArity.UNARY,
			AggregationForm.class, Aggregation.class,
			    NO_ONE_ARGUMENT, PARENT_WITHOUT_COLUMN, NO_PREFIX),
	SORT(ConstantController.getString("operation.sort"), "↕", "sort", "sort[args](relation)", OperationArity.UNARY, SortForm.class, Sort.class,
			    PARENT_WITHOUT_COLUMN),
	INDEXER(ConstantController.getString("operation.indexer"), "❶", "indexer", "indexer[args](source)", OperationArity.UNARY, IndexerForm.class, Indexer.class),

	JOIN(ConstantController.getString("operation.join"), "|X|", "join", "join[args](source1,source2)", OperationArity.BINARY, BooleanExpressionForm.class, Join.class,
			 NO_TWO_ARGUMENTS,  PARENT_WITHOUT_COLUMN),
	LEFT_JOIN(ConstantController.getString("operation.leftJoin"), "⟕", "leftJoin", "leftJoin[args](source1,source2)", OperationArity.BINARY, BooleanExpressionForm.class, LeftJoin.class,
			 NO_TWO_ARGUMENTS,  PARENT_WITHOUT_COLUMN),
	RIGHT_JOIN(ConstantController.getString("operation.rightJoin"), "⟖", "rightJoin", "rightJoin[args](source1,source2)", OperationArity.BINARY, BooleanExpressionForm.class, RightJoin.class,
			 NO_TWO_ARGUMENTS, PARENT_WITHOUT_COLUMN),
	CARTESIAN_PRODUCT(ConstantController.getString("operation.cartesianProduct"), "✕", "cartesianProduct", "cartesianProduct(source1,source2)", OperationArity.BINARY, null, CartesianProduct.class,
			SAME_SOURCE),
	UNION(ConstantController.getString("operation.union"), "∪", "union", "union(source1,source2)", OperationArity.BINARY, null, Union.class),
	INTERSECTION(ConstantController.getString("operation.intersection"), "∩", "intersection", "intersection(source1,source2)", OperationArity.BINARY, null, Intersection.class),
	DIFFERENCE(ConstantController.getString("operation.difference"), "-", "difference", "difference(source1,source2)", OperationArity.BINARY, null, null);

	public final String DISPLAY_NAME;
	public final String SYMBOL;
	public final String NAME;
	public final String DSL_SYNTAX;
	public final OperationArity ARITY;
	public final Class<? extends IOperationForm> FORM;
	public final Class<? extends IOperator> OPERATOR_CLASS;
	public final Set<OperationErrorType> POSSIBLE_ERRORS;


	OperationType(String DISPLAY_NAME, String SYMBOL, String NAME, String DSL_SYNTAX, OperationArity ARITY,
				  Class<? extends IOperationForm> FORM, Class<? extends IOperator> OPERATOR_CLASS,
				  OperationErrorType... errors) {
		this.DISPLAY_NAME = DISPLAY_NAME;
		this.SYMBOL = SYMBOL;
		this.NAME = NAME;
		this.DSL_SYNTAX = DSL_SYNTAX;
		this.ARITY = ARITY;
		this.FORM = FORM;
		this.OPERATOR_CLASS = OPERATOR_CLASS;

		LinkedHashSet<OperationErrorType> aux = new LinkedHashSet<>();

		aux.add(NO_PARENT);
		aux.add(PARENT_ERROR);

		aux.add(ARITY == OperationArity.UNARY ? NO_ONE_PARENT : NO_TWO_PARENTS);
//		if((FLAGS & NO_ARGUMENT_NEEDED) == 0) aux.addAll(List.of(NULL_ARGUMENT, EMPTY_ARGUMENT));
		aux.addAll(List.of(errors));

		this.POSSIBLE_ERRORS = Collections.unmodifiableSet(aux);

	}

	public final static List<OperationType> OPERATIONS_WITHOUT_FORM = Arrays.stream(values()).sequential().filter(x -> x.FORM == null).toList();

	public String getDisplayNameAndSymbol(){
		return SYMBOL+" "+DISPLAY_NAME;
	}

	public static OperationType fromString(String operationType) {

		for (OperationType operation : OperationType.values())
			if (operation.NAME.equalsIgnoreCase(operationType))
				return operation;

		throw new IllegalArgumentException("Invalid operation type: " + operationType);

	}

	public CreateOperationAction getAction() {
		return new CreateOperationAction(this);
	}
}
