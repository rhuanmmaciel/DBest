package booleanexpression.antlr;// Generated from BooleanExpressionDSL.g4 by ANTLR 4.7.2
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link BooleanExpressionDSLParser}.
 */
public interface BooleanExpressionDSLListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link BooleanExpressionDSLParser#command}.
	 * @param ctx the parse tree
	 */
	void enterCommand(BooleanExpressionDSLParser.CommandContext ctx);
	/**
	 * Exit a parse tree produced by {@link BooleanExpressionDSLParser#command}.
	 * @param ctx the parse tree
	 */
	void exitCommand(BooleanExpressionDSLParser.CommandContext ctx);
	/**
	 * Enter a parse tree produced by {@link BooleanExpressionDSLParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(BooleanExpressionDSLParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link BooleanExpressionDSLParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(BooleanExpressionDSLParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link BooleanExpressionDSLParser#atomic}.
	 * @param ctx the parse tree
	 */
	void enterAtomic(BooleanExpressionDSLParser.AtomicContext ctx);
	/**
	 * Exit a parse tree produced by {@link BooleanExpressionDSLParser#atomic}.
	 * @param ctx the parse tree
	 */
	void exitAtomic(BooleanExpressionDSLParser.AtomicContext ctx);
	/**
	 * Enter a parse tree produced by {@link BooleanExpressionDSLParser#value}.
	 * @param ctx the parse tree
	 */
	void enterValue(BooleanExpressionDSLParser.ValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link BooleanExpressionDSLParser#value}.
	 * @param ctx the parse tree
	 */
	void exitValue(BooleanExpressionDSLParser.ValueContext ctx);
}