package dsl;

import dsl.antlr4.RelAlgebraBaseListener;
import dsl.antlr4.RelAlgebraParser.ExpressionContext;
import dsl.antlr4.RelAlgebraParser.SimpleContext;

public class AntlrController extends RelAlgebraBaseListener{
	
	@Override
	public void exitExpression(ExpressionContext ctx) {

		DslController.addCommand(ctx.getText());
		
	}

	@Override
	public void exitSimple(SimpleContext ctx) {
		
		DslController.addTable(ctx.getText());
		
	}

	
}
