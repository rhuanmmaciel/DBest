package gui.frames.dsl;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.kordamp.ikonli.dashicons.Dashicons;
import org.kordamp.ikonli.swing.FontIcon;

import controller.MainController;
import dsl.AntlrController;
import dsl.DslController;
import dsl.DslErrorListener;
import dsl.antlr4.RelAlgebraLexer;
import dsl.antlr4.RelAlgebraParser;
import enums.OperationType;
import gui.utils.CustomDocumentFilter;
import gui.utils.JTextLineNumber;

@SuppressWarnings("serial")
public class TextEditor extends JFrame implements ActionListener {

	private JTextPane textPane = new JTextPane();
	private JScrollPane scrollPane = new JScrollPane(textPane);
	
	private static String lastPath = null;

	private Box bottomPane = Box.createVerticalBox();
	private JTextPane console = new JTextPane();

	private final JToolBar toolBar = new JToolBar();
	private final JButton btnBack = new JButton();
	private final JButton btnImport = new JButton();
	private final JMenu mnOperations = new JMenu("Operações");
	private final JMenuItem menuItemSelection = new JMenuItem(OperationType.SELECTION.getDisplayName());
	private final JMenuItem menuItemProjection = new JMenuItem(OperationType.PROJECTION.getDisplayName());
	private final JMenuItem menuItemJoin = new JMenuItem(OperationType.JOIN.getDisplayName());
	private final JMenuItem menuItemLeftJoin = new JMenuItem(OperationType.LEFT_JOIN.getDisplayName());
	private final JMenuItem menuItemRightJoin = new JMenuItem(OperationType.RIGHT_JOIN.getDisplayName());
	private final JMenuItem menuItemCartesianProduct = new JMenuItem(OperationType.CARTESIAN_PRODUCT.getDisplayName());
	private final JMenuItem menuItemUnion = new JMenuItem(OperationType.UNION.getDisplayName());

	private JButton btnRun = new JButton("Executar");
	private JButton btnRunSelection = new JButton("Executar texto selecionado");

	private MainController main;
	private final JMenuBar menuBar = new JMenuBar();

	public TextEditor(MainController main) {
		
		((AbstractDocument) textPane.getDocument()).setDocumentFilter(new CustomDocumentFilter(textPane));
		
		this.main = main;

		toolBar.setFloatable(false);
		console.setEditable(false);

        JTextLineNumber lineNumber = new JTextLineNumber(textPane);
        scrollPane.setRowHeaderView(lineNumber);

        scrollPane.getVerticalScrollBar().addAdjustmentListener((AdjustmentListener) new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                lineNumber.repaint();
            }
        });

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
		
		Box buttons = Box.createVerticalBox();
		buttons.add(btnRun);
		buttons.add(Box.createVerticalStrut(3));
		buttons.add(btnRunSelection);

		consoleAndButtons.add(buttons);
		
		bottomPane.add(consoleAndButtons);

		toolBar.add(btnBack);
		toolBar.add(btnImport);

		toolBar.add(menuBar);
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

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == btnBack)
			main.getBackToMain();

		else if (e.getSource() == btnRun)
			run();
		else if (e.getSource() == btnRunSelection)
			run(textPane.getSelectedText());

		else if (e.getSource() == menuItemSelection)
			insertOperation("selection[predicado](tabela);");
		
		else if (e.getSource() == menuItemProjection)
			insertOperation("projection[colunas](tabela);");
		
		else if (e.getSource() == menuItemJoin)
			insertOperation("join[coluna1,coluna2](tabela1,tabela2);");
		
		else if (e.getSource() == menuItemLeftJoin)
			insertOperation("leftJoin[coluna1,coluna2](tabela1,tabela2);");
		
		else if (e.getSource() == menuItemRightJoin)
			insertOperation("rightJoin[coluna1,coluna2](tabela1,tabela2);");
		
		else if (e.getSource() == menuItemCartesianProduct)
			insertOperation("cartesianProduct(tabela1,tabela2);");
		
		else if (e.getSource() == menuItemUnion)
			insertOperation("union[colunas1,colunas2](tabela1,tabela2);");

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
	            
	            
	        } catch (FileNotFoundException e) {
			
	        	e.printStackTrace();
			
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