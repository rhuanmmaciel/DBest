package dsl.antlr4;

// Generated from RelAlgebra.g4 by ANTLR 4.7.2
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link RelAlgebraParser}.
 */
public interface RelAlgebraListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link RelAlgebraParser#expressions}.
	 * @param ctx the parse tree
	 */
	void enterExpressions(RelAlgebraParser.ExpressionsContext ctx);
	/**
	 * Exit a parse tree produced by {@link RelAlgebraParser#expressions}.
	 * @param ctx the parse tree
	 */
	void exitExpressions(RelAlgebraParser.ExpressionsContext ctx);
	/**
	 * Enter a parse tree produced by {@link RelAlgebraParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(RelAlgebraParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link RelAlgebraParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(RelAlgebraParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link RelAlgebraParser#selection}.
	 * @param ctx the parse tree
	 */
	void enterSelection(RelAlgebraParser.SelectionContext ctx);
	/**
	 * Exit a parse tree produced by {@link RelAlgebraParser#selection}.
	 * @param ctx the parse tree
	 */
	void exitSelection(RelAlgebraParser.SelectionContext ctx);
	/**
	 * Enter a parse tree produced by {@link RelAlgebraParser#projection}.
	 * @param ctx the parse tree
	 */
	void enterProjection(RelAlgebraParser.ProjectionContext ctx);
	/**
	 * Exit a parse tree produced by {@link RelAlgebraParser#projection}.
	 * @param ctx the parse tree
	 */
	void exitProjection(RelAlgebraParser.ProjectionContext ctx);
	/**
	 * Enter a parse tree produced by {@link RelAlgebraParser#join}.
	 * @param ctx the parse tree
	 */
	void enterJoin(RelAlgebraParser.JoinContext ctx);
	/**
	 * Exit a parse tree produced by {@link RelAlgebraParser#join}.
	 * @param ctx the parse tree
	 */
	void exitJoin(RelAlgebraParser.JoinContext ctx);
	/**
	 * Enter a parse tree produced by {@link RelAlgebraParser#cartesian}.
	 * @param ctx the parse tree
	 */
	void enterCartesian(RelAlgebraParser.CartesianContext ctx);
	/**
	 * Exit a parse tree produced by {@link RelAlgebraParser#cartesian}.
	 * @param ctx the parse tree
	 */
	void exitCartesian(RelAlgebraParser.CartesianContext ctx);
	/**
	 * Enter a parse tree produced by the {@code simple}
	 * labeled alternative in {@link RelAlgebraParser#relation}.
	 * @param ctx the parse tree
	 */
	void enterSimple(RelAlgebraParser.SimpleContext ctx);
	/**
	 * Exit a parse tree produced by the {@code simple}
	 * labeled alternative in {@link RelAlgebraParser#relation}.
	 * @param ctx the parse tree
	 */
	void exitSimple(RelAlgebraParser.SimpleContext ctx);
	/**
	 * Enter a parse tree produced by the {@code nested}
	 * labeled alternative in {@link RelAlgebraParser#relation}.
	 * @param ctx the parse tree
	 */
	void enterNested(RelAlgebraParser.NestedContext ctx);
	/**
	 * Exit a parse tree produced by the {@code nested}
	 * labeled alternative in {@link RelAlgebraParser#relation}.
	 * @param ctx the parse tree
	 */
	void exitNested(RelAlgebraParser.NestedContext ctx);
}