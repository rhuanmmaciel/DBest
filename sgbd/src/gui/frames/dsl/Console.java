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

import dsl.AntlrController;
import dsl.DslController;
import dsl.DslErrorListener;
import dsl.antlr4.RelAlgebraLexer;
import dsl.antlr4.RelAlgebraParser;

@SuppressWarnings("serial")
public class Console extends JFrame implements ActionListener, KeyListener {

	private JTextField textField;
	private JTextPane textArea;
	private JButton enterButton;
	private static List<String> history = new ArrayList<>();
	private boolean historyOn = false;
	private Integer indice = null;

	public Console() {

		initGUI();

	}

	private void initGUI() {

		setSize(400, 400);
		setLocationRelativeTo(null);
		setTitle("Console");

		JPanel mainPanel = new JPanel(new BorderLayout());
		setContentPane(mainPanel);

		textField = new JTextField();
		textField.addKeyListener(this);
		textField.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 20));
		Box textAndButton = Box.createHorizontalBox();
		textAndButton.add(textField);

		enterButton = new JButton(">");
		enterButton.addActionListener(this);
		textAndButton.add(enterButton);

		JPanel textFieldPanel = new JPanel(new BorderLayout());
		textFieldPanel.add(textAndButton, BorderLayout.CENTER);
		mainPanel.add(textFieldPanel, BorderLayout.SOUTH);

		textArea = new JTextPane();
		textArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textArea);
		JPanel textAreaPanel = new JPanel(new BorderLayout());
		textAreaPanel.add(scrollPane, BorderLayout.CENTER);
		mainPanel.add(textAreaPanel, BorderLayout.CENTER);

		mainPanel.setFocusable(true);
		requestFocus(true);
		setVisible(true);

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == enterButton) {

			submitText();

		}

	}

	private void submitText() {

		historyOn = false;

		if (!textField.getText().isEmpty()) {

			String input = textField.getText().strip();

			if (input.toLowerCase().equals("clear")) {

				textArea.setText("");

			} else {

				StyledDocument doc = textArea.getStyledDocument();
				Style style = doc.addStyle("errorStyle", null);
				try {
					
					doc.insertString(doc.getLength(), input + "\n", style);
					
				} catch (BadLocationException e) {
					
					e.printStackTrace();
					
				}
				
				RelAlgebraParser parser = new RelAlgebraParser(
						new CommonTokenStream(new RelAlgebraLexer(CharStreams.fromString(input))));

				parser.removeErrorListeners();
				
				DslErrorListener errorListener = new DslErrorListener();
				parser.addErrorListener(errorListener);

				ParseTreeWalker walker = new ParseTreeWalker();
				
				AntlrController listener = new AntlrController();

				walker.walk(listener, parser.expressions());

				if (!DslErrorListener.getErrors().isEmpty())					
					DslErrorListener.throwError(textArea);

				DslController.parser();
				
			}

			textField.setText("");
			history.remove("");
			history.add(input);
			history.add("");

		}

	}

	private void historyUp() {

		if (!historyOn) {

			historyOn = true;
			indice = history.size() - 2;

		} else {

			if (indice > 0)
				indice--;

		}

		setHistory();

	}

	private void historyDown() {

		if (historyOn) {

			if (indice < history.size() - 1)
				indice++;

		}

		setHistory();

	}

	private void setHistory() {
		
		if (indice != null && indice >= 0 && indice < history.size()) {

			textField.setText(history.get(indice));

		}

	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {

		if (e.getKeyCode() == KeyEvent.VK_ENTER) {

			submitText();

		} else if (e.getKeyCode() == KeyEvent.VK_UP) {

			historyUp();

		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {

			historyDown();

		}

	}

	@Override
	public void keyReleased(KeyEvent e) {

	}
}
