package gui.utils;

import enums.OperationType;

import java.awt.Color;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class CustomDocumentFilter extends DocumentFilter {

	private final StyledDocument styledDocument;
	private final String[] tokens = { "import", "as", "this" };
	private final String[] operations = Arrays.stream(OperationType.values()).map(x -> x.NAME).toArray(String[]::new);
	private final JTextPane textPane;

	private final StyleContext styleContext = StyleContext.getDefaultStyleContext();
	private final javax.swing.text.AttributeSet blueAttributeSet = styleContext.addAttribute(styleContext.getEmptySet(),
			StyleConstants.Foreground, Color.BLUE);
	private final javax.swing.text.AttributeSet orangeAttributeSet = styleContext
			.addAttribute(styleContext.getEmptySet(), StyleConstants.Foreground, Color.MAGENTA);
	private final javax.swing.text.AttributeSet blackAttributeSet = styleContext
			.addAttribute(styleContext.getEmptySet(), StyleConstants.Foreground, Color.BLACK);

	private final Pattern pattern = buildPattern();
	private final Pattern operationPattern = buildOperationPattern();

	public CustomDocumentFilter(JTextPane textPane) {

		this.textPane = textPane;
		styledDocument = textPane.getStyledDocument();

	}

	public void insertString(FilterBypass fb, int offset, String text, javax.swing.text.AttributeSet attributeSet)
			throws BadLocationException {
		super.insertString(fb, offset, text, attributeSet);

		handleTextChanged();
	}

	@Override
	public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
		super.remove(fb, offset, length);

		handleTextChanged();
	}

	public void replace(FilterBypass fb, int offset, int length, String text,
			javax.swing.text.AttributeSet attributeSet) throws BadLocationException {
		super.replace(fb, offset, length, text, attributeSet);

		handleTextChanged();
	}

	private void handleTextChanged() {
		SwingUtilities.invokeLater(this::updateTextStyles);
	}

	private Pattern buildPattern() {
		StringBuilder sb = new StringBuilder();
		for (String token : tokens) {
			sb.append("\\b"); // Start of word boundary
			sb.append(token);
			sb.append("\\b|"); // End of word boundary and an or for the next word
		}
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1); // Remove the trailing "|"
		}

		return Pattern.compile(sb.toString());

	}

	private Pattern buildOperationPattern() {
		StringBuilder sb = new StringBuilder();
		for (String token : operations) {
			sb.append("\\b"); // Início do limite da palavra
			sb.append(token);
			sb.append("\\b|"); // Fim do limite da palavra e "ou" para a próxima palavra
		}
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1); // Remover o "|" final
		}

		return Pattern.compile(sb.toString());
	}

	private void updateTextStyles() {

		styledDocument.setCharacterAttributes(0, textPane.getText().length(), blackAttributeSet, true);

		Matcher matcher = pattern.matcher(textPane.getText());
		while (matcher.find())
			styledDocument.setCharacterAttributes(matcher.start(), matcher.end() - matcher.start(), blueAttributeSet,
					false);

		matcher = operationPattern.matcher(textPane.getText());
		while (matcher.find())
			styledDocument.setCharacterAttributes(matcher.start(), matcher.end() - matcher.start(), orangeAttributeSet,
					false);

	}
}
