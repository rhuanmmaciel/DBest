package dsl;

import dsl.antlr4.RelAlgebraBaseListener;

import dsl.antlr4.RelAlgebraParser;

public class AntlrController extends RelAlgebraBaseListener {

    @Override
    public void exitCommand(RelAlgebraParser.CommandContext ctx) {

        for (String command : ctx.getText().split(";")) {
            DslController.addCommand(command.trim());
        }
    }

}

