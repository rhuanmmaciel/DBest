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

import java.util.Arrays;
import java.util.List;

public enum OperationType {

	SELECTION("Seleção", "σ", "selection", "selection[args](source)", OperationArity.UNARY, FormFrameSelection.class, Selection.class),
	PROJECTION("Projeção", "π", "projection", "projection[args](source)",OperationArity.UNARY, FormFrameProjection.class, Projection.class),
	RENAME("Renomeação", "ρ", "rename", "rename[args](source)", OperationArity.UNARY, FormFrameRename.class, Rename.class),
	GROUP("Agrupamento", "G", "group", "group[args](relation)",OperationArity.UNARY, FormFrameGroup.class, Group.class),
	AGGREGATION("Agregação", "G", "aggregation", "aggregation[args](relation)",OperationArity.UNARY, FormFrameAggregation.class, Aggregation.class),
	SORT("Ordenação", "↕", "sort", "sort[args](relation)", OperationArity.UNARY, FormFrameSort.class, Sort.class),

	JOIN("Junção", "|X|", "join", "join[args](source1,source2)", OperationArity.BINARY, FormFrameJoins.class, Join.class),
	LEFT_JOIN("Junção à esquerda", "⟕", "leftJoin", "leftJoin[args](source1,source2)", OperationArity.BINARY, FormFrameJoins.class, LeftJoin.class),
	RIGHT_JOIN("Junção à direita", "⟖", "rightJoin", "rightJoin[args](source1,source2)", OperationArity.BINARY, FormFrameJoins.class, RightJoin.class),
	CARTESIAN_PRODUCT("Produto Cartesiano", "✕", "cartesianProduct", "cartesianProduct(source1,source2)", OperationArity.BINARY, null, CartesianProduct.class),
	UNION("União", "∪", "union", "union(source1,source2)", OperationArity.BINARY, null, Union.class),
	INTERSECTION("Interseção", "∩", "intersection", "intersection(source1,source2)", OperationArity.BINARY, null, Intersection.class),
	DIFFERENCE("Diferença", "-", "difference", "difference(source1,source2)", OperationArity.BINARY, null, null);

	private final String displayName;
	private final String symbol;
	private final String operationName;
	private final String dslOperation;
	private final OperationArity arity;
	private final Class<? extends IFormFrameOperation> form;
	private final Class<? extends IOperator> operator;

	OperationType(String displayName, String symbol, String operationName, String dslOperation, OperationArity arity,
				  Class<? extends IFormFrameOperation> form, Class<? extends IOperator> operator) {
		this.displayName = displayName;
		this.symbol = symbol;
		this.operationName = operationName;
		this.dslOperation = dslOperation;
		this.arity = arity;
		this.form = form;
		this.operator = operator;
	}

	public final static List<OperationType> OPERATIONS_WITHOUT_FORM = Arrays.stream(values()).sequential().filter(x -> x.getForm() == null).toList();

	public static OperationType fromString(String operationType) {

		for (OperationType operation : OperationType.values())
			if (operation.getOperationName().equalsIgnoreCase(operationType))
				return operation;

		throw new IllegalArgumentException("Invalid operation type: " + operationType);

	}

	public String getDisplayName() {
		return displayName;
	}

	public String getSymbol() {
		return symbol;
	}

	public String getDisplayNameAndSymbol() {
		return getSymbol() + " " + getDisplayName();
	}

	public String getOperationName() {
		return operationName;
	}

	public String getDslOperation() {
		return dslOperation;
	}

	public OperationArity getArity() {
		return arity;
	}

	public Class<? extends IFormFrameOperation> getForm() {
		return form;
	}

	public Class<? extends IOperator> getOperator() {
		return operator;
	}

	public CreateOperationAction getAction() {
		return new CreateOperationAction(this);
	}
}
