package booleanexpression;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import java.util.ArrayList;
import java.util.List;

public class ErrorListener extends BaseErrorListener {

    private static final List<String> errors = new ArrayList<>();

    @Override
    public void syntaxError(
        Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
        int charPositionInLine, String msg, RecognitionException e
    ) {
        String error = String.format("line %d:%d %s", line, charPositionInLine, msg);
        errors.add(error);
    }

    public static List<String> getErrors() {
        return errors;
    }

}
