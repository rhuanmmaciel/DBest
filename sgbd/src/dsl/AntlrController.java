package dsl;

import dsl.antlr4.RelAlgebraBaseListener;
import dsl.antlr4.RelAlgebraParser.CreateTableContext;
import dsl.antlr4.RelAlgebraParser.ExpressionsContext;
import dsl.antlr4.RelAlgebraParser.ImportStatementContext;
import dsl.antlr4.RelAlgebraParser.SimpleContext;
import dsl.antlr4.RelAlgebraParser.VariableDeclarationContext;

public class AntlrController extends RelAlgebraBaseListener{
	
	@Override
	public void exitImportStatement(ImportStatementContext ctx) {
		
		for(String command : ctx.getText().split(";")) {
			
			DslController.addImport(command);
			
		}

	}
	
	@Override
	public void exitExpressions(ExpressionsContext ctx) {
			
		for(String command : ctx.getText().split(";")) {
			
			DslController.addExpression(command);
			
		}
		
	}
	
	@Override
	public void exitSimple(SimpleContext ctx) {
		
		DslController.addTable(ctx.getText());
		
	}
	
	@Override
	public void exitCreateTable(CreateTableContext ctx) {
		
		DslController.addAloneTable(ctx.getText());
		
	}
	
	@Override
	public void exitVariableDeclaration(VariableDeclarationContext ctx) {
		
		DslController.addExpression(ctx.getText());
		
	}

	
}
