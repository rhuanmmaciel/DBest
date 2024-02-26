package gui.utils;

import java.awt.Color;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import enums.OperationType;

public class CustomDocumentFilter extends DocumentFilter {

    private final StyledDocument styledDocument;

    private final String[] tokens = {"import", "as", "this"};

    private final String[] operations = Arrays
        .stream(OperationType.values())
        .map(operationType -> operationType.name)
        .toArray(String[]::new);

    private final JTextPane textPane;

    private final StyleContext styleContext = StyleContext.getDefaultStyleContext();

    private final AttributeSet blueAttributeSet = this.styleContext.addAttribute(this.styleContext.getEmptySet(), StyleConstants.Foreground, Color.BLUE);

    private final AttributeSet orangeAttributeSet = this.styleContext.addAttribute(this.styleContext.getEmptySet(), StyleConstants.Foreground, Color.MAGENTA);

    private final AttributeSet blackAttributeSet = this.styleContext.addAttribute(this.styleContext.getEmptySet(), StyleConstants.Foreground, Color.BLACK);

    private final Pattern pattern = this.buildPattern();

    private final Pattern operationPattern = this.buildOperationPattern();

    public CustomDocumentFilter(JTextPane textPane) {
        this.textPane = textPane;
        this.styledDocument = textPane.getStyledDocument();
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String text, AttributeSet attributeSet) throws BadLocationException {
        super.insertString(fb, offset, text, attributeSet);

        this.handleTextChanged();
    }

    @Override
    public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
        super.remove(fb, offset, length);

        this.handleTextChanged();
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attributeSet) throws BadLocationException {
        super.replace(fb, offset, length, text, attributeSet);

        this.handleTextChanged();
    }

    private void handleTextChanged() {
        SwingUtilities.invokeLater(this::updateTextStyles);
    }

    private Pattern buildPattern(String[] tokens) {
        StringBuilder pattern = new StringBuilder();

        for (String token : tokens) {
            pattern.append("\\b");
            pattern.append(token);
            pattern.append("\\b|");
        }

        if (!pattern.isEmpty()) {
            pattern.deleteCharAt(pattern.length() - 1);
        }

        return Pattern.compile(pattern.toString());
    }

    private Pattern buildPattern() {
        return this.buildPattern(this.tokens);
    }

    private Pattern buildOperationPattern() {
        return this.buildPattern(this.operations);
    }

    private void updateTextStyles() {
        this.styledDocument.setCharacterAttributes(
            0, this.textPane.getText().length(),
            this.blackAttributeSet, true
        );

        Matcher matcher = this.pattern.matcher(this.textPane.getText());

        while (matcher.find()) {
            this.styledDocument.setCharacterAttributes(
                matcher.start(), matcher.end() - matcher.start(),
                this.blueAttributeSet, false
            );
        }

        matcher = this.operationPattern.matcher(this.textPane.getText());

        while (matcher.find()) {
            this.styledDocument.setCharacterAttributes(
                matcher.start(), matcher.end() - matcher.start(), this.orangeAttributeSet, false
            );
        }
    }
}
