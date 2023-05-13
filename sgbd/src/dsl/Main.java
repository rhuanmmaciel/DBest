package dsl;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import dsl.antlr4.RelAlgebraLexer;
import dsl.antlr4.RelAlgebraParser;

public class Main{

	public static void main(String[] args){

		String test = """
		projection[biostats_id](selection[biostats_id<10](biostats))
				 		 """;
		
		RelAlgebraParser parser = new RelAlgebraParser(
		        new CommonTokenStream(new RelAlgebraLexer(CharStreams.fromString(test))));
		
		ParseTreeWalker walker = new ParseTreeWalker();
		
		AntlrController listener = new AntlrController();
		
	    walker.walk(listener, parser.expression());
		
	    DslController.parser();
	    
	}

}
