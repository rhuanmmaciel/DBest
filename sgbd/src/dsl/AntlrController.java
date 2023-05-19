package dsl;

import dsl.antlr4.RelAlgebraBaseListener;
import dsl.antlr4.RelAlgebraParser.ExpressionsContext;
import dsl.antlr4.RelAlgebraParser.SimpleContext;

public class AntlrController extends RelAlgebraBaseListener{
	
	@Override
	public void exitExpressions(ExpressionsContext ctx) {
			
		for(String command : ctx.getText().split(";")) {
			
			DslController.addCommand(command);
			
		}
		
	}
	
	@Override
	public void exitSimple(SimpleContext ctx) {
		
		DslController.addTable(ctx.getText());
		
	}

	
}
