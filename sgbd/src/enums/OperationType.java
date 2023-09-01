package enums;

import entities.Action.CreateOperationCellAction;

import gui.frames.forms.operations.IOperationForm;
import gui.frames.forms.operations.unary.*;
import gui.frames.forms.operations.binary.JoinsForm;

import operations.IOperator;
import operations.binary.CartesianProduct;
import operations.binary.set.Intersection;
import operations.binary.joins.Join;
import operations.binary.joins.LeftJoin;
import operations.binary.joins.RightJoin;
import operations.binary.set.Union;
import operations.unary.*;

import java.util.*;

import static enums.OperationErrorType.*;

public enum OperationType {

    SELECTION         ("Seleção", "σ", "selection", "selection[args](source)", OperationArity.UNARY, SelectionForm.class, Selection.class, NO_ONE_ARGUMENT),
    PROJECTION        ("Projeção", "π", "projection", "projection[args](source)", OperationArity.UNARY, ProjectionForm.class, Projection.class, PARENT_WITHOUT_COLUMN),
    RENAME            ("Renomeação", "ρ", "rename", "rename[args](source)", OperationArity.UNARY, RenameForm.class, Rename.class),
    GROUP             ("Agrupamento", "G", "group", "group[args](relation)", OperationArity.UNARY, GroupForm.class, Group.class, NO_ONE_ARGUMENT, PARENT_WITHOUT_COLUMN, NO_PREFIX),
    AGGREGATION       ("Agregação", "G", "aggregation", "aggregation[args](relation)", OperationArity.UNARY, AggregationForm.class, Aggregation.class, NO_ONE_ARGUMENT, PARENT_WITHOUT_COLUMN, NO_PREFIX),
    SORT              ("Ordenação", "↕", "sort", "sort[args](relation)", OperationArity.UNARY, SortForm.class, Sort.class, PARENT_WITHOUT_COLUMN),
    INDEXER           ("Indexação", "❶", "indexer", "indexer[args](source)", OperationArity.UNARY, IndexerForm.class, Indexer.class),
    JOIN              ("Junção", "|X|", "join", "join[args](source1,source2)", OperationArity.BINARY, JoinsForm.class, Join.class, NO_TWO_ARGUMENTS, PARENT_WITHOUT_COLUMN),
    LEFT_JOIN         ("Junção à esquerda", "⟕", "leftJoin", "leftJoin[args](source1,source2)", OperationArity.BINARY, JoinsForm.class, LeftJoin.class, NO_TWO_ARGUMENTS, PARENT_WITHOUT_COLUMN),
    RIGHT_JOIN        ("Junção à direita", "⟖", "rightJoin", "rightJoin[args](source1,source2)", OperationArity.BINARY, JoinsForm.class, RightJoin.class, NO_TWO_ARGUMENTS, PARENT_WITHOUT_COLUMN),
    CARTESIAN_PRODUCT ("Produto cartesiano", "✕", "cartesianProduct", "cartesianProduct(source1,source2)", OperationArity.BINARY, null, CartesianProduct.class, SAME_SOURCE),
    UNION             ("União", "∪", "union", "union(source1,source2)", OperationArity.BINARY, null, Union.class),
    INTERSECTION      ("Interseção", "∩", "intersection", "intersection(source1,source2)", OperationArity.BINARY, null, Intersection.class),
    DIFFERENCE        ("Diferença", "-", "difference", "difference(source1,source2)", OperationArity.BINARY, null, null);

    public final String displayName;

    public final String symbol;

    public final String name;

    public final String dslSyntax;

    public final OperationArity arity;

    public final Class<? extends IOperationForm> form;

    public final Class<? extends IOperator> operatorClass;

    public final Set<OperationErrorType> possibleErrors;

    public static final List<OperationType> OPERATIONS_WITHOUT_FORM = Arrays
        .stream(values())
        .sequential()
        .filter(operationType -> operationType.form == null)
        .toList();

    OperationType(
        String displayName, String symbol, String name, String dslSyntax, OperationArity arity,
        Class<? extends IOperationForm> form, Class<? extends IOperator> operatorClass, OperationErrorType... errors
    ) {
        this.displayName = displayName;
        this.symbol = symbol;
        this.name = name;
        this.dslSyntax = dslSyntax;
        this.arity = arity;
        this.form = form;
        this.operatorClass = operatorClass;

        LinkedHashSet<OperationErrorType> possibleErrors = new LinkedHashSet<>(
            Arrays.asList(NO_PARENT, PARENT_ERROR, arity == OperationArity.UNARY ? NO_ONE_PARENT : NO_TWO_PARENTS)
        );

        possibleErrors.addAll(List.of(errors));

        this.possibleErrors = Collections.unmodifiableSet(possibleErrors);
    }

    public String getFormattedDisplayName() {
        return String.format("%s %s", this.symbol, this.displayName);
    }

    public static OperationType fromString(String operationTypeName) {
        for (OperationType operationType : OperationType.values()) {
            if (operationType.name.equalsIgnoreCase(operationTypeName)) {
                return operationType;
            }
        }

        throw new IllegalArgumentException(String.format("Invalid operation type: %s", operationTypeName));
    }

    public CreateOperationCellAction getAction() {
        return new CreateOperationCellAction(this);
    }
}
