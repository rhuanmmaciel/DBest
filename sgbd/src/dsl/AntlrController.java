package dsl;

import dsl.antlr4.RelAlgebraBaseListener;
import dsl.antlr4.RelAlgebraParser.CommandContext;

public class AntlrController extends RelAlgebraBaseListener{
	
	@Override
	public void exitCommand(CommandContext ctx) {
			
		for(String command : ctx.getText().split(";"))
			DslController.addCommand(command);
		
	}
	
}
