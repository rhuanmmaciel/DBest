package dsl;

import java.awt.Color;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import controllers.ConstantController;

public class DslErrorListener extends BaseErrorListener {

    private static final List<String> ERRORS = new ArrayList<>();

    @Override
    public void syntaxError(
        Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
        int charPositionInLine, String msg, RecognitionException e
    ) {
        String lineString = ConstantController.getString("dsl.error.line");
        String error = String.format("%s %d:%d %s", lineString, line, charPositionInLine, msg);
        ERRORS.add(error);
    }

    public static void addErrors(String error) {
        ERRORS.add(error);
    }

    public static List<String> getErrors() {
        return ERRORS;
    }

    public static void clearErrors() {
        ERRORS.clear();
    }

    public static void throwError(JTextPane component) {
        StyledDocument document = component.getStyledDocument();
        Style style = document.addStyle("errorStyle", null);
        StyleConstants.setForeground(style, Color.RED);

        DslErrorListener.getErrors().forEach(error -> {
            try {
                document.insertString(document.getLength(), String.format("%s%n", error), style);
            } catch (BadLocationException exception) {
                exception.printStackTrace();
            }
        });

        DslController.reset();
    }
}
