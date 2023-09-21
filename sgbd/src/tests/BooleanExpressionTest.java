package tests;

import lib.booleanexpression.entities.elements.Value;
import lib.booleanexpression.entities.elements.Variable;
import lib.booleanexpression.entities.expressions.AtomicExpression;
import lib.booleanexpression.entities.expressions.BooleanExpression;
import lib.booleanexpression.entities.expressions.LogicalExpression;
import lib.booleanexpression.enums.LogicalOperator;
import lib.booleanexpression.enums.RelationalOperator;
import sgbd.prototype.query.fields.IntegerField;
import sgbd.query.Operator;
import sgbd.query.TestOperators;
import sgbd.query.sourceop.TableScan;
import sgbd.query.unaryop.FilterOperator;
import sgbd.source.table.Table;

public class BooleanExpressionTest {

    public static void main(String[] args){

        Table movie = Table.loadFromHeader("/home/rhuan/Documents/repositories/dbest/tabelas-fbd/ator.head");
        movie.open();

        Operator movie1 = new TableScan(movie);
        Value v = new Value(new IntegerField(5));
        Value v2 = new Value(new IntegerField(2));

        AtomicExpression a1 = new AtomicExpression(new Variable("ator.idAtor"), v, RelationalOperator.GREATER_THAN);
        AtomicExpression a2 = new AtomicExpression(new Variable("ator.idAtor"), v2, RelationalOperator.LESS_THAN);

        BooleanExpression b = new LogicalExpression(LogicalOperator.OR, a1, a2);

        TestOperators.testOperator(new FilterOperator(movie1, b));

    }

}
