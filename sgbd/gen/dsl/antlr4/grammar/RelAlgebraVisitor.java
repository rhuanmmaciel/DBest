// Generated from /home/rhuan/Documents/rep/dbest/sgbd/src/dsl/antlr4/grammar/RelAlgebra.g4 by ANTLR 4.12.0
package dsl.antlr4.grammar;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link RelAlgebraParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface RelAlgebraVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link RelAlgebraParser#command}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCommand(RelAlgebraParser.CommandContext ctx);
	/**
	 * Visit a parse tree produced by {@link RelAlgebraParser#importStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImportStatement(RelAlgebraParser.ImportStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link RelAlgebraParser#nameDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNameDeclaration(RelAlgebraParser.NameDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link RelAlgebraParser#pathStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPathStatement(RelAlgebraParser.PathStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link RelAlgebraParser#variableDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableDeclaration(RelAlgebraParser.VariableDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link RelAlgebraParser#createTable}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCreateTable(RelAlgebraParser.CreateTableContext ctx);
	/**
	 * Visit a parse tree produced by {@link RelAlgebraParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(RelAlgebraParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link RelAlgebraParser#position}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPosition(RelAlgebraParser.PositionContext ctx);
	/**
	 * Visit a parse tree produced by {@link RelAlgebraParser#number}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumber(RelAlgebraParser.NumberContext ctx);
	/**
	 * Visit a parse tree produced by {@link RelAlgebraParser#selection}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelection(RelAlgebraParser.SelectionContext ctx);
	/**
	 * Visit a parse tree produced by {@link RelAlgebraParser#projection}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProjection(RelAlgebraParser.ProjectionContext ctx);
	/**
	 * Visit a parse tree produced by {@link RelAlgebraParser#join}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJoin(RelAlgebraParser.JoinContext ctx);
	/**
	 * Visit a parse tree produced by {@link RelAlgebraParser#leftJoin}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLeftJoin(RelAlgebraParser.LeftJoinContext ctx);
	/**
	 * Visit a parse tree produced by {@link RelAlgebraParser#rightJoin}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRightJoin(RelAlgebraParser.RightJoinContext ctx);
	/**
	 * Visit a parse tree produced by {@link RelAlgebraParser#cartesianProduct}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCartesianProduct(RelAlgebraParser.CartesianProductContext ctx);
	/**
	 * Visit a parse tree produced by {@link RelAlgebraParser#union}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnion(RelAlgebraParser.UnionContext ctx);
	/**
	 * Visit a parse tree produced by {@link RelAlgebraParser#intersection}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntersection(RelAlgebraParser.IntersectionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code simple}
	 * labeled alternative in {@link RelAlgebraParser#relation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimple(RelAlgebraParser.SimpleContext ctx);
	/**
	 * Visit a parse tree produced by the {@code nested}
	 * labeled alternative in {@link RelAlgebraParser#relation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNested(RelAlgebraParser.NestedContext ctx);
}