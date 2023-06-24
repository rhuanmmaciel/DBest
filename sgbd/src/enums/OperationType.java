package enums;

import entities.Action.CreateOperationAction;
import gui.frames.forms.operations.IFormFrameOperation;
import gui.frames.forms.operations.unary.*;
import operations.IOperator;
import gui.frames.forms.operations.binary.FormFrameJoins;
import operations.binary.CartesianProduct;
import operations.binary.set.Intersection;
import operations.binary.joins.Join;
import operations.binary.joins.LeftJoin;
import operations.binary.joins.RightJoin;
import operations.binary.set.Union;
import operations.unary.*;

import java.util.*;

import static enums.OperationErrorType.*;
import static enums.OperationFlags.*;

public enum OperationType {

	SELECTION("Seleção", "σ", "selection", "selection[args](source)", OperationArity.UNARY, FormFrameSelection.class, Selection.class,
			   NO_ONE_ARGUMENT),
	PROJECTION("Projeção", "π", "projection", "projection[args](source)",OperationArity.UNARY, FormFrameProjection.class, Projection.class,
			   PARENT_WITHOUT_COLUMN),
	RENAME("Renomeação", "ρ", "rename", "rename[args](source)", OperationArity.UNARY, FormFrameRename.class, Rename.class),
	GROUP("Agrupamento", "G", "group", "group[args](relation)",OperationArity.UNARY, FormFrameGroup.class, Group.class,
			   NO_ONE_ARGUMENT, PARENT_WITHOUT_COLUMN, NO_PREFIX),
	AGGREGATION("Agregação", "G", "aggregation", "aggregation[args](relation)",OperationArity.UNARY,
			FormFrameAggregation.class, Aggregation.class,
			    NO_ONE_ARGUMENT, PARENT_WITHOUT_COLUMN, NO_PREFIX),
	SORT("Ordenação", "↕", "sort", "sort[args](relation)", OperationArity.UNARY, FormFrameSort.class, Sort.class,
			    PARENT_WITHOUT_COLUMN),

	JOIN("Junção", "|X|", "join", "join[args](source1,source2)", OperationArity.BINARY, FormFrameJoins.class, Join.class,
			 NO_TWO_ARGUMENTS,  PARENT_WITHOUT_COLUMN),
	LEFT_JOIN("Junção à esquerda", "⟕", "leftJoin", "leftJoin[args](source1,source2)", OperationArity.BINARY, FormFrameJoins.class, LeftJoin.class,
			 NO_TWO_ARGUMENTS,  PARENT_WITHOUT_COLUMN),
	RIGHT_JOIN("Junção à direita", "⟖", "rightJoin", "rightJoin[args](source1,source2)", OperationArity.BINARY, FormFrameJoins.class, RightJoin.class,
			 NO_TWO_ARGUMENTS, PARENT_WITHOUT_COLUMN),
	CARTESIAN_PRODUCT("Produto Cartesiano", "✕", "cartesianProduct", "cartesianProduct(source1,source2)", OperationArity.BINARY, null, CartesianProduct.class,
			SAME_SOURCE),
	UNION("União", "∪", "union", "union(source1,source2)", OperationArity.BINARY, null, Union.class),
	INTERSECTION("Interseção", "∩", "intersection", "intersection(source1,source2)", OperationArity.BINARY, null, Intersection.class),
	DIFFERENCE("Diferença", "-", "difference", "difference(source1,source2)", OperationArity.BINARY, null, null);

	public final String DISPLAY_NAME;
	public final String SYMBOL;
	public final String NAME;
	public final String DSL_SYNTAX;
	public final OperationArity ARITY;
	public final Class<? extends IFormFrameOperation> FORM;
	public final Class<? extends IOperator> OPERATOR_CLASS;
	public final Set<OperationErrorType> POSSIBLE_ERRORS;


	OperationType(String DISPLAY_NAME, String SYMBOL, String NAME, String DSL_SYNTAX, OperationArity ARITY,
				  Class<? extends IFormFrameOperation> FORM, Class<? extends IOperator> OPERATOR_CLASS,
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
