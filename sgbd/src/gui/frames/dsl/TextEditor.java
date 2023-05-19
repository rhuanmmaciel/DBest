package gui.frames.dsl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.kordamp.ikonli.dashicons.Dashicons;
import org.kordamp.ikonli.swing.FontIcon;

import controller.MainController;
import dsl.AntlrController;
import dsl.DslController;
import dsl.InputErrorListener;
import dsl.antlr4.RelAlgebraLexer;
import dsl.antlr4.RelAlgebraParser;
import enums.OperationType;
import gui.frames.utils.JTextLineNumber;
import javax.swing.JMenuBar;

@SuppressWarnings("serial")
public class TextEditor extends JFrame implements ActionListener {

	private JTextPane textPane = new JTextPane();
	private JScrollPane scrollPane = new JScrollPane(textPane);

	private Box bottomPane = Box.createVerticalBox();
	private JTextPane console = new JTextPane();

	private final JToolBar toolBar = new JToolBar();
	private final JButton btnBack = new JButton();
	private final JButton btnImport = new JButton();
	private final JMenu mnOperations = new JMenu("Operações");
	private final JMenuItem menuItemSelection = new JMenuItem(OperationType.SELECTION.getName());
	private final JMenuItem menuItemProjection = new JMenuItem(OperationType.PROJECTION.getName());
	private final JMenuItem menuItemJoin = new JMenuItem(OperationType.JOIN.getName());
	private final JMenuItem menuItemLeftJoin = new JMenuItem(OperationType.LEFT_JOIN.getName());
	private final JMenuItem menuItemRightJoin = new JMenuItem(OperationType.RIGHT_JOIN.getName());
	private final JMenuItem menuItemCartesianProduct = new JMenuItem(OperationType.CARTESIAN_PRODUCT.getName());
	private final JMenuItem menuItemUnion = new JMenuItem(OperationType.UNION.getName());
	
	private JButton btnRun = new JButton("Run");
	private JButton btnRunSelection = new JButton("Run selection");

	private MainController main;
	private final JMenuBar menuBar = new JMenuBar();

	public TextEditor(MainController main) {

		this.main = main;

		toolBar.setFloatable(false);
		console.setEditable(false);

		JTextLineNumber lineNumber = new JTextLineNumber(textPane);
		scrollPane.setRowHeaderView(lineNumber);

		getContentPane().add(scrollPane, BorderLayout.CENTER);
		getContentPane().add(toolBar, BorderLayout.NORTH);
		getContentPane().add(bottomPane, BorderLayout.SOUTH);

		bottomPane.setPreferredSize(new Dimension(bottomPane.getWidth(), main.getContentPane().getHeight() / 4));

		JLabel lblConsole = new JLabel("Console");
		bottomPane.setAlignmentX(Box.LEFT_ALIGNMENT);
		bottomPane.add(lblConsole);

		Box consoleAndButtons = Box.createHorizontalBox();
		consoleAndButtons.add(console);
		consoleAndButtons.add(Box.createHorizontalStrut(3));
		consoleAndButtons.add(btnRun);
		consoleAndButtons.add(Box.createHorizontalStrut(3));
		consoleAndButtons.add(btnRunSelection);

		bottomPane.add(consoleAndButtons);

		toolBar.add(btnBack);
		toolBar.add(btnImport);
		
		toolBar.add(menuBar);
		menuBar.add(mnOperations);
		
		mnOperations.add(menuItemSelection);
		mnOperations.add(menuItemProjection);
		mnOperations.add(menuItemJoin);
		mnOperations.add(menuItemLeftJoin);
		mnOperations.add(menuItemRightJoin);
		mnOperations.add(menuItemCartesianProduct);
		mnOperations.add(menuItemUnion);
		
		btnBack.addActionListener(this);
		btnImport.addActionListener(this);
		btnRun.addActionListener(this);
		btnRunSelection.addActionListener(this);

		setIcons();

	}

	private void setIcons() {

		int iconsSize = 20;

		FontIcon iconBack = FontIcon.of(Dashicons.ARROW_LEFT_ALT);
		iconBack.setIconSize(iconsSize);
		btnBack.setIcon(iconBack);

		FontIcon iconImport = FontIcon.of(Dashicons.OPEN_FOLDER);
		iconImport.setIconSize(iconsSize);
		btnImport.setIcon(iconImport);
		//EDITOR_CUSTOMCHAR
	}

	private void run() {

		run(textPane.getText());

	}

	private void run(String text) {

		if (text == null)
			return;

		console.setText("");

		StyledDocument doc = console.getStyledDocument();
		Style style = doc.addStyle("errorStyle", null);

		RelAlgebraParser parser = new RelAlgebraParser(
				new CommonTokenStream(new RelAlgebraLexer(CharStreams.fromString(text))));

		parser.removeErrorListeners();

		InputErrorListener errorListener = new InputErrorListener();
		parser.addErrorListener(errorListener);

		ParseTreeWalker walker = new ParseTreeWalker();

		AntlrController listener = new AntlrController();

		walker.walk(listener, parser.expressions());
		
		if (!errorListener.getErrors().isEmpty()) {

			StyleConstants.setForeground(style, Color.RED);
			try {

				doc.insertString(doc.getLength(), errorListener.getErrors() + "\n", style);

			} catch (BadLocationException e) {

				e.printStackTrace();

			}
			
			DslController.reset();
			
		}else {
			
			DslController.parser();
			
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == btnBack) {

			main.getBackToMain();
			;

		}

		if (e.getSource() == btnRun) {

			run();

		} else if (e.getSource() == btnRunSelection) {

			run(textPane.getSelectedText());

		}

	}
}