package enums;

import java.util.Arrays;
import java.util.List;

import controllers.ConstantController;

import entities.Action.CreateOperationCellAction;

import gui.frames.forms.operations.BooleanExpressionForm;
import gui.frames.forms.operations.ConditionForm;
import gui.frames.forms.operations.IOperationForm;
import gui.frames.forms.operations.JoinForm;
import gui.frames.forms.operations.ReferenceBooleanExpressionForm;
import gui.frames.forms.operations.unary.AggregationForm;
import gui.frames.forms.operations.unary.AutoIncrementForm;
import gui.frames.forms.operations.unary.ExplodeForm;
import gui.frames.forms.operations.unary.GroupForm;
import gui.frames.forms.operations.unary.LimitForm;
import gui.frames.forms.operations.unary.ProjectionForm;
import gui.frames.forms.operations.unary.ReferenceForm;
import gui.frames.forms.operations.unary.RenameForm;
import gui.frames.forms.operations.unary.SortForm;
import operations.IOperator;
import operations.binary.And;
import operations.binary.CartesianProduct;
import operations.binary.Condition;
import operations.binary.If;
import operations.binary.Or;
import operations.binary.Xor;
import operations.binary.joins.AntiLeftInnerJoin;
import operations.binary.joins.HashFullOuterJoin;
import operations.binary.joins.HashLeftInnerJoin;
import operations.binary.joins.HashLeftAntiJoin;
import operations.binary.joins.HashLeftOuterJoin;
import operations.binary.joins.HashLeftSemiJoin;
import operations.binary.joins.HashRightAntiJoin;
import operations.binary.joins.HashRightOuterJoin;
import operations.binary.joins.HashRightSemiJoin;
import operations.binary.joins.Join;
import operations.binary.joins.LeftOuterJoin;
import operations.binary.joins.MergeFullOuterJoin;
import operations.binary.joins.MergeLeftAntiJoin;
import operations.binary.joins.MergeLeftInnerJoin;
import operations.binary.joins.MergeLeftOuterJoin;
import operations.binary.joins.MergeLeftSemiJoin;
import operations.binary.joins.MergeRightAntiJoin;
import operations.binary.joins.MergeRightOuterJoin;
import operations.binary.joins.MergeRightSemiJoin;
import operations.binary.joins.RightOuterJoin;
import operations.binary.joins.SemiLeftInnerJoin;
import operations.binary.set.Append;
import operations.binary.set.BilateralExistence;
import operations.binary.set.Difference;
import operations.binary.set.HashDifference;
import operations.binary.set.HashIntersection;
import operations.binary.set.HashUnion;
import operations.binary.set.Intersection;
import operations.binary.set.UnilateralExistence;
import operations.binary.set.Union;
import operations.unary.Aggregation;
import operations.unary.AutoIncrement;
import operations.unary.DuplicateRemoval;
import operations.unary.Explode;
import operations.unary.Group;
import operations.unary.Hash;
import operations.unary.HashDuplicateRemoval;
import operations.unary.HashGroup;
import operations.unary.Limit;
import operations.unary.Materialization;
import operations.unary.Memoize;
import operations.unary.SelectColumns;
import operations.unary.Projection;
import operations.unary.Reference;
import operations.unary.Rename;
import operations.unary.Scan;
import operations.unary.Selection;
import operations.unary.Sort;

public enum OperationType {
REFERENCE         (ConstantController.getString("operation.reference"), "R", "reference", "reference[args]", OperationArity.NULLARY, ReferenceForm.class, Reference.class, false, true),
    SCAN         (ConstantController.getString("operation.scan"), "→", "scan", "scan(source)", OperationArity.UNARY, null, Scan.class, false, false),
    FILTER         (ConstantController.getString("operation.filter"), "σ", "filter", "filter[args](source)", OperationArity.UNARY, BooleanExpressionForm.class, Selection.class, false, false),
    PROJECTION        (ConstantController.getString("operation.projection"), "π", "projection", "projection[args](source)", OperationArity.UNARY, ProjectionForm.class, Projection.class, false, false),
    SELECT_COLUMNS(ConstantController.getString("operation.selectColumns"), "S", "selectColumns", "selectColumns[args](source)", OperationArity.UNARY, ProjectionForm.class, SelectColumns.class, false, false),
    LIMIT(ConstantController.getString("operation.limit"), "L", "limit", "limit[args](source)", OperationArity.UNARY, LimitForm.class, Limit.class, false, false),
DUPLICATE_REMOVAL(ConstantController.getString("operation.duplicateRemoval"), "\u0394", "duplicateRemoval", "duplicateRemoval(source)", OperationArity.UNARY, null, DuplicateRemoval.class, false, false),
  HASH_DUPLICATE_REMOVAL(ConstantController.getString("operation.hashDuplicateRemoval"), "#\u0394", "hashDuplicateRemoval", "hashDuplicateRemoval(source)", OperationArity.UNARY, null, HashDuplicateRemoval.class, true, false),  
RENAME            (ConstantController.getString("operation.rename"), "ρ", "rename", "rename[args](source)", OperationArity.UNARY, RenameForm.class, Rename.class, false, false),
EXPLODE            (ConstantController.getString("operation.explode"), "E", "explode", "explode[args](source)", OperationArity.UNARY, ExplodeForm.class, Explode.class, false, false),
AUTO_INCREMENT            (ConstantController.getString("operation.autoInc"), "A", "autoInc", "autoInc[args](source)", OperationArity.UNARY, AutoIncrementForm.class, AutoIncrement.class, false, false),

//    GROUP             (ConstantController.getString("operation.group"), "G", "group", "group[args](source)", OperationArity.UNARY, GroupForm.class, Group.class, NO_ONE_ARGUMENT, PARENT_WITHOUT_COLUMN, NO_PREFIX),
    GROUP             (ConstantController.getString("operation.group"), "{}", "group", "group[args](source)", OperationArity.UNARY, GroupForm.class, Group.class, false, false),
    HASH_GROUP             (ConstantController.getString("operation.hashGroup"), "#{}", "hashGroup", "hashGroup[args](source)", OperationArity.UNARY, GroupForm.class, HashGroup.class, false, false),
//    AGGREGATION       (ConstantController.getString("operation.aggregation"), "\u22C8", "aggregation", "aggregation[args](source)", OperationArity.UNARY, AggregationForm.class, Aggregation.class, NO_ONE_ARGUMENT, PARENT_WITHOUT_COLUMN, NO_PREFIX),
        AGGREGATION       (ConstantController.getString("operation.aggregation"), "\u2211", "aggregation", "aggregation[args](source)", OperationArity.UNARY, AggregationForm.class, Aggregation.class, false, false),
    SORT              (ConstantController.getString("operation.sort"), "↕", "sort", "sort[args](source)", OperationArity.UNARY, SortForm.class, Sort.class, true, false),
//    INDEXER           (ConstantController.getString("operation.indexer"), "❶", "indexer", "indexer[args](source)", OperationArity.UNARY, IndexerForm.class, Indexer.class),
  
    AND              (ConstantController.getString("operation.and"), "&&", "and", "and(source1,source2)", OperationArity.BINARY, null, And.class, false, true),
    OR              (ConstantController.getString("operation.or"), "||", "or", "or(source1,source2)", OperationArity.BINARY, null, Or.class, false, true),
    XOR              (ConstantController.getString("operation.xor"), "^", "xor", "xor(source1,source2)", OperationArity.BINARY, null, Xor.class, false, true),
    CONDITION              (ConstantController.getString("operation.condition"), "?", "condition", "condition[args]", OperationArity.NULLARY, ConditionForm.class, Condition.class, false, true),
    IF              (ConstantController.getString("operation.if"), "IF", "if", "if[args](source1,source2)", OperationArity.BINARY, ReferenceBooleanExpressionForm.class, If.class, false, true),
    
    NESTED_LOOP_JOIN              (ConstantController.getString("operation.join"), "|X|", "join", "join[args](source1,source2)", OperationArity.BINARY, JoinForm.class, Join.class, false, false),
    MERGE_JOIN        (ConstantController.getString("operation.mergeJoin"), "↕|X|", "mergeJoin", "mergeJoin[args](source1,source2)", OperationArity.BINARY, JoinForm.class, MergeLeftInnerJoin.class, false, false),
    HASH_JOIN              (ConstantController.getString("operation.hashJoin"), "#|X|", "hashJoin", "hashJoin[args](source1,source2)", OperationArity.BINARY, JoinForm.class, HashLeftInnerJoin.class, true, false),
    
    NESTED_LOOP_LEFT_SEMI_JOIN              (ConstantController.getString("operation.semiJoin"), "\u22C9", "semiJoin", "semiJoin[args](source1,source2)", OperationArity.BINARY, JoinForm.class, SemiLeftInnerJoin.class, false, false),
    MERGE_LEFT_SEMI_JOIN              (ConstantController.getString("operation.mergeLeftSemiJoin"), "↕\u22C9", "mergeLeftSemiJoin", "mergeLeftSemiJoin[args](source1,source2)", OperationArity.BINARY, JoinForm.class, MergeLeftSemiJoin.class, false, false),
    MERGE_RIGHT_SEMI_JOIN              (ConstantController.getString("operation.mergeRightSemiJoin"), "↕\u22CA", "mergeRightSemiJoin", "mergeRightSemiJoin[args](source1,source2)", OperationArity.BINARY, JoinForm.class, MergeRightSemiJoin.class, false, false),
    HASH_LEFT_SEMI_JOIN              (ConstantController.getString("operation.hashLeftSemiJoin"), "#\u22C9", "hashLeftSemiJoin", "hashLeftSemiJoin[args](source1,source2)", OperationArity.BINARY, JoinForm.class, HashLeftSemiJoin.class, true, false),
    HASH_RIGHT_SEMI_JOIN              (ConstantController.getString("operation.hashRightSemiJoin"), "#\u22CA", "hashRightSemiJoin", "hashRightSemiJoin[args](source1,source2)", OperationArity.BINARY, JoinForm.class, HashRightSemiJoin.class, true, false),
    
    NESTED_LOOP_LEFT_ANTI_JOIN              (ConstantController.getString("operation.antiJoin"), "\u25B7", "antiJoin", "antiJoin[args](source1,source2)", OperationArity.BINARY, JoinForm.class, AntiLeftInnerJoin.class, false, false),
    MERGE_LEFT_ANTI_JOIN              (ConstantController.getString("operation.mergeLeftAntiJoin"), "↕\u25B7", "mergeLeftAntiJoin", "mergeLeftAntiJoin[args](source1,source2)", OperationArity.BINARY, JoinForm.class, MergeLeftAntiJoin.class, false, false),
    MERGE_RIGHT_ANTI_JOIN              (ConstantController.getString("operation.mergeRightAntiJoin"), "↕\u25C1", "mergeRightAntiJoin", "mergeRightAntiJoin[args](source1,source2)", OperationArity.BINARY, JoinForm.class, MergeRightAntiJoin.class, false, false),
    HASH_LEFT_ANTI_JOIN              (ConstantController.getString("operation.hashLeftAntiJoin"), "#\u25B7", "hashLeftAntiJoin", "hashLeftAntiJoin[args](source1,source2)", OperationArity.BINARY, JoinForm.class, HashLeftAntiJoin.class, true, false),
    HASH_RIGHT_ANTI_JOIN              (ConstantController.getString("operation.hashRightAntiJoin"), "#\u25C1", "hashRightAntiJoin", "hashRightAntiJoin[args](source1,source2)", OperationArity.BINARY, JoinForm.class, HashRightAntiJoin.class, true, false),
    
    NESTED_LOOP_LEFT_OUTER_JOIN         (ConstantController.getString("operation.leftOuterJoin"), "⟕", "leftOuterJoin", "leftOuterJoin[args](source1,source2)", OperationArity.BINARY, JoinForm.class, LeftOuterJoin.class, false, false),
    NESTED_LOOP_RIGHT_OUTER_JOIN        (ConstantController.getString("operation.rightOuterJoin"), "⟖", "rightOuterJoin", "rightOuterJoin[args](source1,source2)", OperationArity.BINARY, JoinForm.class, RightOuterJoin.class, false, false),
    MERGE_LEFT_OUTER_JOIN              (ConstantController.getString("operation.mergeLeftOuterJoin"), "↕⟕", "mergeLeftOuterJoin", "mergeLeftOuterJoin[args](source1,source2)", OperationArity.BINARY, JoinForm.class, MergeLeftOuterJoin.class, false, false),
    MERGE_RIGHT_OUTER_JOIN              (ConstantController.getString("operation.mergeRightOuterJoin"), "↕⟖", "mergeRightOuterJoin", "mergeRightOuterJoin[args](source1,source2)", OperationArity.BINARY, JoinForm.class, MergeRightOuterJoin.class, false, false),
    MERGE_FULL_OUTER_JOIN              (ConstantController.getString("operation.mergeFullOuterJoin"), "↕⟗", "mergeFullOuterJoin", "mergeFullOuterJoin[args](source1,source2)", OperationArity.BINARY, JoinForm.class, MergeFullOuterJoin.class, false, false),
    HASH_LEFT_OUTER_JOIN              (ConstantController.getString("operation.hashLeftOuterJoin"), "#⟕", "hashLeftOuterJoin", "hashLeftOuterJoin[args](source1,source2)", OperationArity.BINARY, JoinForm.class, HashLeftOuterJoin.class, true, false),  
    HASH_RIGHT_OUTER_JOIN        (ConstantController.getString("operation.hashRightOuterJoin"), "#⟖", "hashRightOuterJoin", "hashRightOuterJoin[args](source1,source2)", OperationArity.BINARY, JoinForm.class, HashRightOuterJoin.class, true, false),
    HASH_FULL_OUTER_JOIN        (ConstantController.getString("operation.hashFullOuterJoin"), "#⟗", "hashFullOuterJoin", "hashFullOuterJoin[args](source1,source2)", OperationArity.BINARY, JoinForm.class, HashFullOuterJoin.class, true, false),
    
    CARTESIAN_PRODUCT (ConstantController.getString("operation.cartesianProduct"), "✕", "cartesianProduct", "cartesianProduct(source1,source2)", OperationArity.BINARY, null, CartesianProduct.class, false, false),
    HASH             (ConstantController.getString("operation.hash"), "#", "hash", "hash(source)", OperationArity.UNARY, null, Hash.class,true, false),
    MEMOIZE             (ConstantController.getString("operation.memoize"), "\u2133", "memoize", "memoize(source)", OperationArity.UNARY, null, Memoize.class,false, false),
    MATERIALIZATION             (ConstantController.getString("operation.materialization"), "\u29C9", "materialization", "materialization(source)", OperationArity.UNARY, null, Materialization.class,true, false),
    
    UNILATERAL_EXISTENCE  (ConstantController.getString("operation.unilateralExistence"), "∃⟗", "unilateralExistence", "unilateralExistence(source)", OperationArity.BINARY, null, UnilateralExistence.class,false, false),
    BILATERAL_EXISTENCE  (ConstantController.getString("operation.bilateralExistence"), "∃⨝", "bilateralExistence", "bilateralExistence(source)", OperationArity.BINARY, null, BilateralExistence.class,false, false),
    
    APPEND             (ConstantController.getString("operation.append"), "+", "append", "append(source1,source2)", OperationArity.BINARY, null, Append.class,false, false),
    UNION             (ConstantController.getString("operation.union"), "∪", "union", "union(source1,source2)", OperationArity.BINARY, null, Union.class,false, false),
    HASH_UNION             (ConstantController.getString("operation.hashUnion"), "#∪", "hashUnion", "hashUnion(source1,source2)", OperationArity.BINARY, null, HashUnion.class,true, false),
    INTERSECTION      (ConstantController.getString("operation.intersection"), "∩", "intersection", "intersection(source1,source2)", OperationArity.BINARY, null, Intersection.class, false, false),
    HASH_INTERSECTION      (ConstantController.getString("operation.hashIntersection"), "#∩", "hashIntersection", "hashIntersection(source1,source2)", OperationArity.BINARY, null, HashIntersection.class, true, false),
    DIFFERENCE        (ConstantController.getString("operation.difference"), "-", "difference", "difference(source1,source2)", OperationArity.BINARY, null, Difference.class, false, false),
    HASH_DIFFERENCE        (ConstantController.getString("operation.hashDifference"), "#-", "hashDifference", "hashDifference(source1,source2)", OperationArity.BINARY, null, HashDifference.class, true, false);

    public final String displayName;

    public final String symbol;

    public final String name;

    public final String dslSyntax;

    public final OperationArity arity;

    public final Class<? extends IOperationForm> form;

    public final Class<? extends IOperator> operatorClass;

    public final boolean isSetBasedProcessing;
    
    public final boolean allwaysAllowConnections;

    public static final List<OperationType> OPERATIONS_WITHOUT_FORM = Arrays
        .stream(values())
        .sequential()
        .filter(operationType -> operationType.form == null)
        .toList();

    OperationType(
        String displayName, String symbol, String name, String dslSyntax, OperationArity arity,
        Class<? extends IOperationForm> form, Class<? extends IOperator> operatorClass, 
        boolean isSetBasedProcessing, boolean allwaysAllowConnections
    ) {
        this.displayName = displayName;
        this.symbol = symbol;
        this.name = name;
        this.dslSyntax = dslSyntax;
        this.arity = arity;
        this.form = form;
        this.operatorClass = operatorClass;
        this.isSetBasedProcessing = isSetBasedProcessing;
        this.allwaysAllowConnections = allwaysAllowConnections;
    }

    public String getFormattedDisplayName() {
        return String.format("%s %s", this.symbol, this.displayName);
    }

    public static OperationType fromString(String operationTypeName) {
        
        int index = operationTypeName.indexOf(":");
        if (index!=-1){
            operationTypeName = operationTypeName.substring(0, index);
        }
        
        for (OperationType operationType : OperationType.values()) {
            if (operationType.name.equalsIgnoreCase(operationTypeName)) {
                return operationType;
            }
        }
        return null;
        //throw new IllegalArgumentException(String.format("Invalid operation type: %s", operationTypeName));
    }

    public CreateOperationCellAction getAction() {
        return new CreateOperationCellAction(this);
    }
}
