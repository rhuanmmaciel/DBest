package enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import controllers.ConstantController;

import entities.Action.CreateOperationCellAction;

import gui.frames.forms.operations.BooleanExpressionForm;
import gui.frames.forms.operations.IOperationForm;
import gui.frames.forms.operations.unary.AggregationForm;
import gui.frames.forms.operations.unary.GroupForm;
import gui.frames.forms.operations.unary.IndexerForm;
import gui.frames.forms.operations.unary.ProjectionForm;
import gui.frames.forms.operations.unary.RenameForm;
import gui.frames.forms.operations.unary.SortForm;

import operations.IOperator;
import operations.binary.CartesianProduct;
import operations.binary.joins.Join;
import operations.binary.joins.LeftJoin;
import operations.binary.joins.RightJoin;
import operations.binary.set.Intersection;
import operations.binary.set.Union;
import operations.unary.Aggregation;
import operations.unary.Group;
import operations.unary.Indexer;
import operations.unary.Projection;
import operations.unary.Rename;
import operations.unary.Selection;
import operations.unary.Sort;

import static enums.OperationErrorType.NO_ONE_ARGUMENT;
import static enums.OperationErrorType.NO_ONE_PARENT;
import static enums.OperationErrorType.NO_PARENT;
import static enums.OperationErrorType.NO_PREFIX;
import static enums.OperationErrorType.NO_TWO_ARGUMENTS;
import static enums.OperationErrorType.NO_TWO_PARENTS;
import static enums.OperationErrorType.PARENT_ERROR;
import static enums.OperationErrorType.PARENT_WITHOUT_COLUMN;
import static enums.OperationErrorType.SAME_SOURCE;

public enum OperationType {

    SELECTION         (ConstantController.getString("operation.selection"), "σ", "selection", "selection[args](source)", OperationArity.UNARY, BooleanExpressionForm.class, Selection.class, NO_ONE_ARGUMENT),
    PROJECTION        (ConstantController.getString("operation.projection"), "π", "projection", "projection[args](source)", OperationArity.UNARY, ProjectionForm.class, Projection.class, PARENT_WITHOUT_COLUMN),
    RENAME            (ConstantController.getString("operation.rename"), "ρ", "rename", "rename[args](source)", OperationArity.UNARY, RenameForm.class, Rename.class),
//    GROUP             (ConstantController.getString("operation.group"), "G", "group", "group[args](relation)", OperationArity.UNARY, GroupForm.class, Group.class, NO_ONE_ARGUMENT, PARENT_WITHOUT_COLUMN, NO_PREFIX),
//    AGGREGATION       (ConstantController.getString("operation.aggregation"), "G", "aggregation", "aggregation[args](relation)", OperationArity.UNARY, AggregationForm.class, Aggregation.class, NO_ONE_ARGUMENT, PARENT_WITHOUT_COLUMN, NO_PREFIX),
    SORT              (ConstantController.getString("operation.sort"), "↕", "sort", "sort[args](relation)", OperationArity.UNARY, SortForm.class, Sort.class, PARENT_WITHOUT_COLUMN),
//    INDEXER           (ConstantController.getString("operation.indexer"), "❶", "indexer", "indexer[args](source)", OperationArity.UNARY, IndexerForm.class, Indexer.class),
    JOIN              (ConstantController.getString("operation.join"), "|X|", "join", "join[args](source1,source2)", OperationArity.BINARY, BooleanExpressionForm.class, Join.class, NO_TWO_ARGUMENTS, PARENT_WITHOUT_COLUMN),
    LEFT_JOIN         (ConstantController.getString("operation.leftJoin"), "⟕", "leftJoin", "leftJoin[args](source1,source2)", OperationArity.BINARY, BooleanExpressionForm.class, LeftJoin.class, NO_TWO_ARGUMENTS, PARENT_WITHOUT_COLUMN),
    RIGHT_JOIN        (ConstantController.getString("operation.rightJoin"), "⟖", "rightJoin", "rightJoin[args](source1,source2)", OperationArity.BINARY, BooleanExpressionForm.class, RightJoin.class, NO_TWO_ARGUMENTS, PARENT_WITHOUT_COLUMN),
    CARTESIAN_PRODUCT (ConstantController.getString("operation.cartesianProduct"), "✕", "cartesianProduct", "cartesianProduct(source1,source2)", OperationArity.BINARY, null, CartesianProduct.class, SAME_SOURCE),
    UNION             (ConstantController.getString("operation.union"), "∪", "union", "union(source1,source2)", OperationArity.BINARY, null, Union.class),
    INTERSECTION      (ConstantController.getString("operation.intersection"), "∩", "intersection", "intersection(source1,source2)", OperationArity.BINARY, null, Intersection.class),
    DIFFERENCE        (ConstantController.getString("operation.difference"), "-", "difference", "difference(source1,source2)", OperationArity.BINARY, null, null);

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
