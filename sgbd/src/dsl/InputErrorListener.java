package dsl;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class InputErrorListener extends BaseErrorListener {

	private List<String> errors = new ArrayList<>();

	@Override
	public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
			String msg, RecognitionException e) {
		
		String error = String.format("line %d:%d %s", line, charPositionInLine, msg);
		errors.add(error);
	
	}

	public List<String> getErrors() {
		return errors;
	}

}
