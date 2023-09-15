package gui.frames.dsl;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyledDocument;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import controllers.ConstantController;

import dsl.AntlrController;
import dsl.DslController;
import dsl.DslErrorListener;
import dsl.antlr4.RelAlgebraLexer;
import dsl.antlr4.RelAlgebraParser;

import exceptions.dsl.InputException;

public class Console extends JFrame implements ActionListener, KeyListener {

    private JTextField textField;

    private JTextPane textArea;

    private JButton enterButton;

    private static final List<String> history = new ArrayList<>();

    private boolean isHistoryOn = false;

    private Integer index = null;

    public Console() {
        this.initGUI();
    }

    private void initGUI() {
        this.setSize(400, 400);
        this.setLocationRelativeTo(null);
        this.setTitle(ConstantController.getString("console"));

        JPanel mainPanel = new JPanel(new BorderLayout());
        this.setContentPane(mainPanel);

        this.textField = new JTextField();
        this.textField.addKeyListener(this);
        this.textField.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 20));
        Box textAndButton = Box.createHorizontalBox();
        textAndButton.add(this.textField);

        this.enterButton = new JButton(">");
        this.enterButton.addActionListener(this);
        textAndButton.add(this.enterButton);

        JPanel textFieldPanel = new JPanel(new BorderLayout());
        textFieldPanel.add(textAndButton, BorderLayout.CENTER);
        mainPanel.add(textFieldPanel, BorderLayout.SOUTH);

        this.textArea = new JTextPane();
        this.textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(this.textArea);
        JPanel textAreaPanel = new JPanel(new BorderLayout());
        textAreaPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(textAreaPanel, BorderLayout.CENTER);

        mainPanel.setFocusable(true);
        this.requestFocus(true);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == this.enterButton) {
            this.submitText();
        }
    }

    private void submitText() {
        this.isHistoryOn = false;

        if (!this.textField.getText().isEmpty()) {
            String input = this.textField.getText().strip();

            if (input.equalsIgnoreCase("clear")) {
                this.textArea.setText("");
            } else {
                StyledDocument document = this.textArea.getStyledDocument();
                Style style = document.addStyle("errorStyle", null);

                try {
                    document.insertString(document.getLength(), input + "\n", style);
                } catch (BadLocationException exception) {
                    exception.printStackTrace();
                }

                RelAlgebraParser parser = new RelAlgebraParser(new CommonTokenStream(new RelAlgebraLexer(CharStreams.fromString(input))));

                parser.removeErrorListeners();

                DslErrorListener errorListener = new DslErrorListener();
                parser.addErrorListener(errorListener);

                ParseTreeWalker walker = new ParseTreeWalker();

                AntlrController listener = new AntlrController();

                walker.walk(listener, parser.command());

                this.execute();
            }

            this.textField.setText("");

            history.remove("");
            history.add(input);
            history.add("");
        }
    }

    private void execute() {
        if (!DslErrorListener.getErrors().isEmpty()) {
            DslErrorListener.throwError(this.textArea);
            return;
        }

        try {
            DslController.parser();
        } catch (InputException exception) {
            DslErrorListener.throwError(this.textArea);
        }
    }

    private void historyUp() {
        if (!this.isHistoryOn) {
            this.isHistoryOn = true;
            this.index = history.size() - 2;
        } else {
            if (this.index > 0) {
                this.index--;
            }
        }

        this.setHistory();
    }

    private void historyDown() {
        if (this.isHistoryOn && (this.index < history.size() - 1)) {
            this.index++;
        }

        this.setHistory();
    }

    private void setHistory() {
        if (this.index != null && this.index >= 0 && this.index < history.size()) {
            this.textField.setText(history.get(this.index));
        }
    }

    @Override
    public void keyTyped(KeyEvent event) {

    }

    @Override
    public void keyPressed(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.VK_ENTER) {
            this.submitText();
        } else if (event.getKeyCode() == KeyEvent.VK_UP) {
            this.historyUp();
        } else if (event.getKeyCode() == KeyEvent.VK_DOWN) {
            this.historyDown();
        }
    }

    @Override
    public void keyReleased(KeyEvent event) {

    }
}
