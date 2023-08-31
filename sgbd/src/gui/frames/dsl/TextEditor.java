package gui.frames.dsl;

import controller.ConstantController;
import controller.MainController;
import dsl.AntlrController;
import dsl.DslController;
import dsl.DslErrorListener;
import dsl.antlr4.RelAlgebraLexer;
import dsl.antlr4.RelAlgebraParser;
import enums.OperationType;
import gui.utils.CustomDocumentFilter;
import gui.utils.JTextLineNumber;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.kordamp.ikonli.dashicons.Dashicons;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class TextEditor extends JFrame implements ActionListener {

	private final JTextPane textPane = new JTextPane();

	private static String lastPath = null;

	private final JTextPane console = new JTextPane();

	private final JButton btnBack = new JButton();
	private final JButton btnImport = new JButton();
	private final JMenuItem menuItemSelection = new JMenuItem(OperationType.SELECTION.DISPLAY_NAME);
	private final JMenuItem menuItemProjection = new JMenuItem(OperationType.PROJECTION.DISPLAY_NAME);
	private final JMenuItem menuItemJoin = new JMenuItem(OperationType.JOIN.DISPLAY_NAME);
	private final JMenuItem menuItemLeftJoin = new JMenuItem(OperationType.LEFT_JOIN.DISPLAY_NAME);
	private final JMenuItem menuItemRightJoin = new JMenuItem(OperationType.RIGHT_JOIN.DISPLAY_NAME);
	private final JMenuItem menuItemCartesianProduct = new JMenuItem(OperationType.CARTESIAN_PRODUCT.DISPLAY_NAME);
	private final JMenuItem menuItemUnion = new JMenuItem(OperationType.UNION.DISPLAY_NAME);
	private final JMenuItem menuItemIntersection = new JMenuItem(OperationType.INTERSECTION.DISPLAY_NAME);
	private final JMenuItem menuItemGroup = new JMenuItem(OperationType.GROUP.DISPLAY_NAME);

	private final JButton btnRun = new JButton(ConstantController.getString("textEditor.execute"));
	private final JButton btnRunSelection = new JButton(ConstantController.getString("textEditor.executeSelectedText"));

	private final MainController main;

	public TextEditor(MainController main) {
		
		((AbstractDocument) textPane.getDocument()).setDocumentFilter(new CustomDocumentFilter(textPane));
		
		this.main = main;

		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		console.setEditable(false);

        JTextLineNumber lineNumber = new JTextLineNumber(textPane);
		JScrollPane scrollPane = new JScrollPane(textPane);
		scrollPane.setRowHeaderView(lineNumber);

        scrollPane.getVerticalScrollBar().addAdjustmentListener(e -> lineNumber.repaint());

		getContentPane().add(scrollPane, BorderLayout.CENTER);
		getContentPane().add(toolBar, BorderLayout.NORTH);
		Box bottomPane = Box.createVerticalBox();
		getContentPane().add(bottomPane, BorderLayout.SOUTH);

		bottomPane.setPreferredSize(new Dimension(bottomPane.getWidth(), main.getContentPane().getHeight() / 4));

		JLabel lblConsole = new JLabel(ConstantController.getString("console"));
		bottomPane.setAlignmentX(Box.LEFT_ALIGNMENT);
		bottomPane.add(lblConsole);

		Box consoleAndButtons = Box.createHorizontalBox();
		consoleAndButtons.add(console);
		consoleAndButtons.add(Box.createHorizontalStrut(3));
		
		Box buttons = Box.createVerticalBox();
		buttons.add(btnRun);
		buttons.add(Box.createVerticalStrut(3));
		buttons.add(btnRunSelection);

		consoleAndButtons.add(buttons);
		
		bottomPane.add(consoleAndButtons);

		toolBar.add(btnBack);
		toolBar.add(btnImport);

		JMenuBar menuBar = new JMenuBar();
		toolBar.add(menuBar);
		JMenu mnOperations = new JMenu(ConstantController.getString("textEditor.operations"));
		menuBar.add(mnOperations);

		mnOperations.add(menuItemSelection);
		menuItemSelection.addActionListener(this);
		mnOperations.add(menuItemProjection);
		menuItemProjection.addActionListener(this);
		mnOperations.add(menuItemJoin);
		menuItemJoin.addActionListener(this);
		mnOperations.add(menuItemLeftJoin);
		menuItemLeftJoin.addActionListener(this);
		mnOperations.add(menuItemRightJoin);
		menuItemRightJoin.addActionListener(this);
		mnOperations.add(menuItemCartesianProduct);
		menuItemCartesianProduct.addActionListener(this);
		mnOperations.add(menuItemUnion);
		menuItemUnion.addActionListener(this);
		mnOperations.add(menuItemIntersection);
		menuItemIntersection.addActionListener(this);
		mnOperations.add(menuItemGroup);
		menuItemGroup.addActionListener(this);

		btnBack.addActionListener(this);
		btnImport.addActionListener(this);
		btnRun.addActionListener(this);
		btnRunSelection.addActionListener(this);

		setIcons();

	}

	private void run() {

		run(textPane.getText());

	}

	private void run(String text) {

		if (text == null)
			return;

		console.setText("");

		RelAlgebraParser parser = new RelAlgebraParser(
				new CommonTokenStream(new RelAlgebraLexer(CharStreams.fromString(text))));

		parser.removeErrorListeners();

		DslErrorListener errorListener = new DslErrorListener();
		parser.addErrorListener(errorListener);

		ParseTreeWalker walker = new ParseTreeWalker();

		AntlrController listener = new AntlrController();

		walker.walk(listener, parser.command());

		if (!DslErrorListener.getErrors().isEmpty())
			DslErrorListener.throwError(console);

		else {

			DslController.parser();

			if (!DslErrorListener.getErrors().isEmpty())
				DslErrorListener.throwError(console);

		}

		DslErrorListener.clearErrors();

	}

	private void setIcons() {

		int iconsSize = 20;

		FontIcon iconBack = FontIcon.of(Dashicons.ARROW_LEFT_ALT);
		iconBack.setIconSize(iconsSize);
		btnBack.setIcon(iconBack);

		FontIcon iconImport = FontIcon.of(Dashicons.OPEN_FOLDER);
		iconImport.setIconSize(iconsSize);
		btnImport.setIcon(iconImport);

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == btnBack)
			main.getBackToMain();

		else if (e.getSource() == btnRun)
			run();
		else if (e.getSource() == btnRunSelection)
			run(textPane.getSelectedText());

		else if (e.getSource() == menuItemSelection)
			insertOperation(ConstantController.getString("textEditor.operations.example.selection"));
		
		else if (e.getSource() == menuItemProjection)
			insertOperation(ConstantController.getString("textEditor.operations.example.projection"));
		
		else if (e.getSource() == menuItemJoin)
			insertOperation(ConstantController.getString("textEditor.operations.example.join"));
		
		else if (e.getSource() == menuItemLeftJoin)
			insertOperation(ConstantController.getString("textEditor.operations.example.leftJoin"));
		
		else if (e.getSource() == menuItemRightJoin)
			insertOperation(ConstantController.getString("textEditor.operations.example.rightJoin"));
		
		else if (e.getSource() == menuItemCartesianProduct)
			insertOperation(ConstantController.getString("textEditor.operations.example.cartesianProduct"));
		
		else if (e.getSource() == menuItemUnion)
			insertOperation(ConstantController.getString("textEditor.operations.example.union"));

		else if (e.getSource() == menuItemIntersection)
			insertOperation(ConstantController.getString("textEditor.operations.example.intersection"));

		else if (e.getSource() == menuItemGroup)
			insertOperation(ConstantController.getString("textEditor.operations.example.group"));

		else if(e.getSource() == btnImport)
			importText();
		
	}

	private void importText() {
		
		JFileChooser fileUpload = new JFileChooser();

		fileUpload.setCurrentDirectory(MainController.getLastDirectory());
		
		if (fileUpload.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			
			lastPath = fileUpload.getCurrentDirectory().getAbsolutePath();
			
			MainController.setLastDirectory(new File(fileUpload.getCurrentDirectory().getAbsolutePath()));
			
	        StringBuilder stringBuilder = new StringBuilder();
	        
	        try (BufferedReader reader = new BufferedReader(new FileReader(fileUpload.getSelectedFile()))) {
	        	
	            String line;
	            while ((line = reader.readLine()) != null) 
	                stringBuilder.append(line).append("\n");
	            
	            
	        } catch (IOException e) {

				e.printStackTrace();

			}
			
	        textPane.setText(stringBuilder.toString());
	        
		}
		
	}
	
	private void insertOperation(String text) {

		try {
			textPane.getDocument().insertString(textPane.getCaretPosition(), text, null);
		} catch (BadLocationException error) {
			error.printStackTrace();
		}

	}

	public static String getLastPath() {
		return lastPath;
	}
	
}