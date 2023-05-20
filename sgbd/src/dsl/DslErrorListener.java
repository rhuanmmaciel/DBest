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

public class DslErrorListener extends BaseErrorListener {

	private static List<String> errors = new ArrayList<>();

	@Override
	public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
			String msg, RecognitionException e) {
		
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

	public static void throwError(JTextPane component) {
		
		StyledDocument doc = component.getStyledDocument();
		Style style = doc.addStyle("errorStyle", null);
		StyleConstants.setForeground(style, Color.RED);
		try {

			doc.insertString(doc.getLength(), DslErrorListener.getErrors() + "\n", style);

		} catch (BadLocationException e) {

			e.printStackTrace();

		}
		
		DslController.reset();
		
	}
	
}
