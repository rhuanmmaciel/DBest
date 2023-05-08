package dsl;

import dsl.antlr4.RelAlgebraBaseListener;
import dsl.antlr4.RelAlgebraParser;

public class RelAlgebraDSLController extends RelAlgebraBaseListener{

	@Override public void enterSelection(RelAlgebraParser.SelectionContext ctx) { 
		
		System.out.println(ctx.getText());		
	}
	
	@Override public void enterProjection(RelAlgebraParser.ProjectionContext ctx) { 
		
		System.out.println(ctx.PREDICATE());
		
	}

	@Override public void exitProjection(RelAlgebraParser.ProjectionContext ctx) { 
		
		
	}
	
}
