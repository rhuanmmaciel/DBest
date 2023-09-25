package dsl.antlr4;// Generated from RelAlgebra.g4 by ANTLR 4.7.2
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link dsl.antlr4.RelAlgebraParser}.
 */
public interface RelAlgebraListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link dsl.antlr4.RelAlgebraParser#command}.
	 * @param ctx the parse tree
	 */
	void enterCommand(dsl.antlr4.RelAlgebraParser.CommandContext ctx);
	/**
	 * Exit a parse tree produced by {@link dsl.antlr4.RelAlgebraParser#command}.
	 * @param ctx the parse tree
	 */
	void exitCommand(dsl.antlr4.RelAlgebraParser.CommandContext ctx);
	/**
	 * Enter a parse tree produced by {@link dsl.antlr4.RelAlgebraParser#importStatement}.
	 * @param ctx the parse tree
	 */
	void enterImportStatement(dsl.antlr4.RelAlgebraParser.ImportStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link dsl.antlr4.RelAlgebraParser#importStatement}.
	 * @param ctx the parse tree
	 */
	void exitImportStatement(dsl.antlr4.RelAlgebraParser.ImportStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link dsl.antlr4.RelAlgebraParser#nameDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterNameDeclaration(dsl.antlr4.RelAlgebraParser.NameDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link dsl.antlr4.RelAlgebraParser#nameDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitNameDeclaration(dsl.antlr4.RelAlgebraParser.NameDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link dsl.antlr4.RelAlgebraParser#pathStatement}.
	 * @param ctx the parse tree
	 */
	void enterPathStatement(dsl.antlr4.RelAlgebraParser.PathStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link dsl.antlr4.RelAlgebraParser#pathStatement}.
	 * @param ctx the parse tree
	 */
	void exitPathStatement(dsl.antlr4.RelAlgebraParser.PathStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link dsl.antlr4.RelAlgebraParser#variableDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclaration(dsl.antlr4.RelAlgebraParser.VariableDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link dsl.antlr4.RelAlgebraParser#variableDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclaration(dsl.antlr4.RelAlgebraParser.VariableDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link dsl.antlr4.RelAlgebraParser#createTable}.
	 * @param ctx the parse tree
	 */
	void enterCreateTable(dsl.antlr4.RelAlgebraParser.CreateTableContext ctx);
	/**
	 * Exit a parse tree produced by {@link dsl.antlr4.RelAlgebraParser#createTable}.
	 * @param ctx the parse tree
	 */
	void exitCreateTable(dsl.antlr4.RelAlgebraParser.CreateTableContext ctx);
	/**
	 * Enter a parse tree produced by {@link dsl.antlr4.RelAlgebraParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(dsl.antlr4.RelAlgebraParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link dsl.antlr4.RelAlgebraParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(dsl.antlr4.RelAlgebraParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link dsl.antlr4.RelAlgebraParser#position}.
	 * @param ctx the parse tree
	 */
	void enterPosition(dsl.antlr4.RelAlgebraParser.PositionContext ctx);
	/**
	 * Exit a parse tree produced by {@link dsl.antlr4.RelAlgebraParser#position}.
	 * @param ctx the parse tree
	 */
	void exitPosition(dsl.antlr4.RelAlgebraParser.PositionContext ctx);
	/**
	 * Enter a parse tree produced by {@link dsl.antlr4.RelAlgebraParser#number}.
	 * @param ctx the parse tree
	 */
	void enterNumber(dsl.antlr4.RelAlgebraParser.NumberContext ctx);
	/**
	 * Exit a parse tree produced by {@link dsl.antlr4.RelAlgebraParser#number}.
	 * @param ctx the parse tree
	 */
	void exitNumber(dsl.antlr4.RelAlgebraParser.NumberContext ctx);
	/**
	 * Enter a parse tree produced by {@link dsl.antlr4.RelAlgebraParser#selection}.
	 * @param ctx the parse tree
	 */
	void enterSelection(dsl.antlr4.RelAlgebraParser.SelectionContext ctx);
	/**
	 * Exit a parse tree produced by {@link dsl.antlr4.RelAlgebraParser#selection}.
	 * @param ctx the parse tree
	 */
	void exitSelection(dsl.antlr4.RelAlgebraParser.SelectionContext ctx);
	/**
	 * Enter a parse tree produced by {@link dsl.antlr4.RelAlgebraParser#projection}.
	 * @param ctx the parse tree
	 */
	void enterProjection(dsl.antlr4.RelAlgebraParser.ProjectionContext ctx);
	/**
	 * Exit a parse tree produced by {@link dsl.antlr4.RelAlgebraParser#projection}.
	 * @param ctx the parse tree
	 */
	void exitProjection(dsl.antlr4.RelAlgebraParser.ProjectionContext ctx);
	/**
	 * Enter a parse tree produced by {@link dsl.antlr4.RelAlgebraParser#sort}.
	 * @param ctx the parse tree
	 */
	void enterSort(dsl.antlr4.RelAlgebraParser.SortContext ctx);
	/**
	 * Exit a parse tree produced by {@link dsl.antlr4.RelAlgebraParser#sort}.
	 * @param ctx the parse tree
	 */
	void exitSort(dsl.antlr4.RelAlgebraParser.SortContext ctx);
	/**
	 * Enter a parse tree produced by {@link dsl.antlr4.RelAlgebraParser#group}.
	 * @param ctx the parse tree
	 */
	void enterGroup(dsl.antlr4.RelAlgebraParser.GroupContext ctx);
	/**
	 * Exit a parse tree produced by {@link dsl.antlr4.RelAlgebraParser#group}.
	 * @param ctx the parse tree
	 */
	void exitGroup(dsl.antlr4.RelAlgebraParser.GroupContext ctx);
	/**
	 * Enter a parse tree produced by {@link dsl.antlr4.RelAlgebraParser#rename}.
	 * @param ctx the parse tree
	 */
	void enterRename(dsl.antlr4.RelAlgebraParser.RenameContext ctx);
	/**
	 * Exit a parse tree produced by {@link dsl.antlr4.RelAlgebraParser#rename}.
	 * @param ctx the parse tree
	 */
	void exitRename(dsl.antlr4.RelAlgebraParser.RenameContext ctx);
	/**
	 * Enter a parse tree produced by {@link dsl.antlr4.RelAlgebraParser#aggregation}.
	 * @param ctx the parse tree
	 */
	void enterAggregation(dsl.antlr4.RelAlgebraParser.AggregationContext ctx);
	/**
	 * Exit a parse tree produced by {@link dsl.antlr4.RelAlgebraParser#aggregation}.
	 * @param ctx the parse tree
	 */
	void exitAggregation(dsl.antlr4.RelAlgebraParser.AggregationContext ctx);
	/**
	 * Enter a parse tree produced by {@link dsl.antlr4.RelAlgebraParser#indexer}.
	 * @param ctx the parse tree
	 */
	void enterIndexer(dsl.antlr4.RelAlgebraParser.IndexerContext ctx);
	/**
	 * Exit a parse tree produced by {@link dsl.antlr4.RelAlgebraParser#indexer}.
	 * @param ctx the parse tree
	 */
	void exitIndexer(dsl.antlr4.RelAlgebraParser.IndexerContext ctx);
	/**
	 * Enter a parse tree produced by {@link dsl.antlr4.RelAlgebraParser#join}.
	 * @param ctx the parse tree
	 */
	void enterJoin(dsl.antlr4.RelAlgebraParser.JoinContext ctx);
	/**
	 * Exit a parse tree produced by {@link dsl.antlr4.RelAlgebraParser#join}.
	 * @param ctx the parse tree
	 */
	void exitJoin(dsl.antlr4.RelAlgebraParser.JoinContext ctx);
	/**
	 * Enter a parse tree produced by {@link dsl.antlr4.RelAlgebraParser#leftJoin}.
	 * @param ctx the parse tree
	 */
	void enterLeftJoin(dsl.antlr4.RelAlgebraParser.LeftJoinContext ctx);
	/**
	 * Exit a parse tree produced by {@link dsl.antlr4.RelAlgebraParser#leftJoin}.
	 * @param ctx the parse tree
	 */
	void exitLeftJoin(dsl.antlr4.RelAlgebraParser.LeftJoinContext ctx);
	/**
	 * Enter a parse tree produced by {@link dsl.antlr4.RelAlgebraParser#rightJoin}.
	 * @param ctx the parse tree
	 */
	void enterRightJoin(dsl.antlr4.RelAlgebraParser.RightJoinContext ctx);
	/**
	 * Exit a parse tree produced by {@link dsl.antlr4.RelAlgebraParser#rightJoin}.
	 * @param ctx the parse tree
	 */
	void exitRightJoin(dsl.antlr4.RelAlgebraParser.RightJoinContext ctx);
	/**
	 * Enter a parse tree produced by {@link dsl.antlr4.RelAlgebraParser#cartesianProduct}.
	 * @param ctx the parse tree
	 */
	void enterCartesianProduct(dsl.antlr4.RelAlgebraParser.CartesianProductContext ctx);
	/**
	 * Exit a parse tree produced by {@link dsl.antlr4.RelAlgebraParser#cartesianProduct}.
	 * @param ctx the parse tree
	 */
	void exitCartesianProduct(dsl.antlr4.RelAlgebraParser.CartesianProductContext ctx);
	/**
	 * Enter a parse tree produced by {@link dsl.antlr4.RelAlgebraParser#union}.
	 * @param ctx the parse tree
	 */
	void enterUnion(dsl.antlr4.RelAlgebraParser.UnionContext ctx);
	/**
	 * Exit a parse tree produced by {@link dsl.antlr4.RelAlgebraParser#union}.
	 * @param ctx the parse tree
	 */
	void exitUnion(dsl.antlr4.RelAlgebraParser.UnionContext ctx);
	/**
	 * Enter a parse tree produced by {@link dsl.antlr4.RelAlgebraParser#intersection}.
	 * @param ctx the parse tree
	 */
	void enterIntersection(dsl.antlr4.RelAlgebraParser.IntersectionContext ctx);
	/**
	 * Exit a parse tree produced by {@link dsl.antlr4.RelAlgebraParser#intersection}.
	 * @param ctx the parse tree
	 */
	void exitIntersection(dsl.antlr4.RelAlgebraParser.IntersectionContext ctx);
	/**
	 * Enter a parse tree produced by {@link dsl.antlr4.RelAlgebraParser#unary}.
	 * @param ctx the parse tree
	 */
	void enterUnary(dsl.antlr4.RelAlgebraParser.UnaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link dsl.antlr4.RelAlgebraParser#unary}.
	 * @param ctx the parse tree
	 */
	void exitUnary(dsl.antlr4.RelAlgebraParser.UnaryContext ctx);
	/**
	 * Enter a parse tree produced by {@link dsl.antlr4.RelAlgebraParser#binary}.
	 * @param ctx the parse tree
	 */
	void enterBinary(dsl.antlr4.RelAlgebraParser.BinaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link dsl.antlr4.RelAlgebraParser#binary}.
	 * @param ctx the parse tree
	 */
	void exitBinary(dsl.antlr4.RelAlgebraParser.BinaryContext ctx);
	/**
	 * Enter a parse tree produced by the {@code simple}
	 * labeled alternative in {@link dsl.antlr4.RelAlgebraParser#relation}.
	 * @param ctx the parse tree
	 */
	void enterSimple(dsl.antlr4.RelAlgebraParser.SimpleContext ctx);
	/**
	 * Exit a parse tree produced by the {@code simple}
	 * labeled alternative in {@link dsl.antlr4.RelAlgebraParser#relation}.
	 * @param ctx the parse tree
	 */
	void exitSimple(dsl.antlr4.RelAlgebraParser.SimpleContext ctx);
	/**
	 * Enter a parse tree produced by the {@code nested}
	 * labeled alternative in {@link dsl.antlr4.RelAlgebraParser#relation}.
	 * @param ctx the parse tree
	 */
	void enterNested(dsl.antlr4.RelAlgebraParser.NestedContext ctx);
	/**
	 * Exit a parse tree produced by the {@code nested}
	 * labeled alternative in {@link dsl.antlr4.RelAlgebraParser#relation}.
	 * @param ctx the parse tree
	 */
	void exitNested(RelAlgebraParser.NestedContext ctx);
}