package booleanexpression;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class ErrorListener extends BaseErrorListener {

    private static final List<String> errors = new ArrayList<>();

    @Override
    public void syntaxError(
        Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
        int charPositionInLine, String msg, RecognitionException e
    ) {
        System.out.println("ERROOOOOOOOOOOOOOOO");
        String error = String.format("line %d:%d %s", line, charPositionInLine, msg);
        errors.add(error);
    }

    public static void addErrors(String error) {
        errors.add(error);
    }

    public static List<String> getErrors() {
        return errors;
    }

    public static void clearErrors() {
        errors.clear();
    }
}
