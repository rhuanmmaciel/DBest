/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ibd.query.lookup;

import ibd.query.binaryop.join.JoinPredicate;
import ibd.table.ComparisonTypes;
import ibd.table.prototype.query.fields.Field;
import lib.booleanexpression.entities.elements.Value;
import lib.booleanexpression.entities.elements.Variable;
import lib.booleanexpression.entities.expressions.AtomicExpression;
import lib.booleanexpression.entities.expressions.BooleanExpression;
import lib.booleanexpression.entities.expressions.LogicalExpression;

/**
 *
 * @author ferna
 */
public class ExpressionConverter {

    public static LookupFilter convert(BooleanExpression be) throws Exception {
        if (be instanceof LogicalExpression le) {
            int connector = -1;
            if (le.isAnd()) {
                connector = CompositeLookupFilter.AND;
            } else {
                connector = CompositeLookupFilter.OR;
            }
            CompositeLookupFilter clf = new CompositeLookupFilter(connector);
            for (BooleanExpression expression : le.getExpressions()) {
                LookupFilter lf = convert(expression);
                clf.addFilter(lf);
            }
            return clf;
        } else if (be instanceof AtomicExpression ae) {
            Element elem1 = getElement(ae.getFirstElement());
            Element elem2 = getElement(ae.getSecondElement());
            

            int compType = convertComparisonType(ae);
            //TwoColumnsLookupFilter tclf = new TwoColumnsLookupFilter(leftColumn, rightColumn, compType);
            SingleColumnLookupFilter sclf = new SingleColumnLookupFilter(elem1, compType, elem2);
            return sclf;

        }
        return null;
    }

    private static Element getElement(lib.booleanexpression.entities.elements.Element element) throws Exception {
        if (element instanceof Variable) {
            String leftColumn = getQualifiedColumnName((Variable) element);
            return new ColumnElement(leftColumn);
        } else if (element instanceof Value){
            Comparable value = getValue((Value) element);
            return new LiteralElement(value);
        }
        else {
            return new LiteralElement(null);
        }
    }

    public static JoinPredicate convert2JoinPredicate(BooleanExpression be) throws Exception {
        JoinPredicate joinPredicate = new JoinPredicate();
        collectJoinTerms(be, joinPredicate);
        return joinPredicate;
    }

    public static void collectJoinTerms(BooleanExpression be, JoinPredicate joinPredicate) throws Exception {
        if (be instanceof LogicalExpression le) {
            if (!le.isAnd()) {
                return;
            }
            for (BooleanExpression expression : le.getExpressions()) {
                collectJoinTerms(expression, joinPredicate);
            }
        } else if (be instanceof AtomicExpression ae) {
            if (!ae.isSecondElementAColumn()) {
                return;
            }

            String leftColumn = getQualifiedColumnName((Variable) ae.getFirstElement());
            String rightColumn = getQualifiedColumnName((Variable) ae.getSecondElement());
            joinPredicate.addTerm(leftColumn, rightColumn);

        }
    }

    private static Comparable getValue(Value value) {
        Field f = value.getField();
        if (f.getMetadata().isBoolean()) {
            return f.getBoolean();
        }
        if (f.getMetadata().isFloat()) {
            return f.getFloat();
        }
        if (f.getMetadata().isInt()) {
            return f.getInt();
        }
        if (f.getMetadata().isString()) {
            return f.getString();
        }
        return f.getString();
    }

    private static String getQualifiedColumnName(Variable var) {
        String values[] = var.getNames();
        return values[0] + "." + values[1];
    }

    private static int convertComparisonType(AtomicExpression ae) {
        return switch (ae.getRelationalOperator()) {
            case LESS_THAN ->
                ComparisonTypes.LOWER_THAN;
            case GREATER_THAN ->
                ComparisonTypes.GREATER_THAN;
            case GREATER_THAN_OR_EQUAL ->
                ComparisonTypes.GREATER_EQUAL_THAN;
            case LESS_THAN_OR_EQUAL ->
                ComparisonTypes.LOWER_EQUAL_THAN;
            case EQUAL ->
                ComparisonTypes.EQUAL;
            case NOT_EQUAL ->
                ComparisonTypes.DIFF;
            case IS ->
                ComparisonTypes.IS_NULL;
            case IS_NOT ->
                ComparisonTypes.IS_NOT_NULL;
        };
    }
}
