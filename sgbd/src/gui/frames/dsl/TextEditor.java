package gui.frames.dsl;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.BufferedReader;
import java.io.File;
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

import controllers.ConstantController;
import controllers.MainController;

import dsl.AntlrController;
import dsl.DslController;
import dsl.DslErrorListener;
import dsl.antlr4.RelAlgebraLexer;
import dsl.antlr4.RelAlgebraParser;

import enums.OperationType;

import exceptions.dsl.InputException;

import gui.utils.CustomDocumentFilter;
import gui.utils.JTextLineNumber;

public class TextEditor extends JFrame implements ActionListener {

    private final JTextPane textPane = new JTextPane();

    private static String lastPath = null;

    private final JTextPane console = new JTextPane();

    private final JButton backButton = new JButton();

    private final JButton importButton = new JButton();

    private final JMenuItem selectionMenuItem = new JMenuItem(OperationType.SELECTION.displayName);

    private final JMenuItem projectionMenuItem = new JMenuItem(OperationType.PROJECTION.displayName);

    private final JMenuItem joinMenuItem = new JMenuItem(OperationType.JOIN.displayName);

    private final JMenuItem leftJoinMenuItem = new JMenuItem(OperationType.LEFT_JOIN.displayName);

    private final JMenuItem rightJoinMenuItem = new JMenuItem(OperationType.RIGHT_JOIN.displayName);

    private final JMenuItem cartesianProductMenuItem = new JMenuItem(OperationType.CARTESIAN_PRODUCT.displayName);

    private final JMenuItem unionMenuItem = new JMenuItem(OperationType.UNION.displayName);

    private final JMenuItem intersectionMenuItem = new JMenuItem(OperationType.INTERSECTION.displayName);

    private final JMenuItem groupMenuItem = new JMenuItem(OperationType.GROUP.displayName);

    private final JButton runButton = new JButton(ConstantController.getString("textEditor.execute"));

    private final JButton runSelection = new JButton(ConstantController.getString("textEditor.executeSelectedText"));

    private final MainController mainController;

    public TextEditor(MainController mainController) {
        ((AbstractDocument) this.textPane.getDocument()).setDocumentFilter(new CustomDocumentFilter(this.textPane));

        this.mainController = mainController;

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        this.console.setEditable(false);

        JTextLineNumber lineNumber = new JTextLineNumber(this.textPane);

        JScrollPane scrollPane = new JScrollPane(this.textPane);
        scrollPane.setRowHeaderView(lineNumber);

        scrollPane.getVerticalScrollBar().addAdjustmentListener(e -> lineNumber.repaint());

        this.getContentPane().add(scrollPane, BorderLayout.CENTER);
        this.getContentPane().add(toolBar, BorderLayout.NORTH);

        Box bottomPane = Box.createVerticalBox();
        this.getContentPane().add(bottomPane, BorderLayout.SOUTH);

        bottomPane.setPreferredSize(new Dimension(bottomPane.getWidth(), mainController.getContentPane().getHeight() / 4));

        JLabel lblConsole = new JLabel(ConstantController.getString("console"));
        bottomPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        bottomPane.add(lblConsole);

        Box consoleAndButtons = Box.createHorizontalBox();
        consoleAndButtons.add(this.console);
        consoleAndButtons.add(Box.createHorizontalStrut(3));

        Box buttons = Box.createVerticalBox();
        buttons.add(this.runButton);
        buttons.add(Box.createVerticalStrut(3));
        buttons.add(this.runSelection);

        consoleAndButtons.add(buttons);

        bottomPane.add(consoleAndButtons);

        toolBar.add(this.backButton);
        toolBar.add(this.importButton);

        JMenuBar menuBar = new JMenuBar();
        toolBar.add(menuBar);
        JMenu mnOperations = new JMenu(ConstantController.getString("textEditor.operations"));
        menuBar.add(mnOperations);

        mnOperations.add(this.selectionMenuItem);
        this.selectionMenuItem.addActionListener(this);
        mnOperations.add(this.projectionMenuItem);
        this.projectionMenuItem.addActionListener(this);
        mnOperations.add(this.joinMenuItem);
        this.joinMenuItem.addActionListener(this);
        mnOperations.add(this.leftJoinMenuItem);
        this.leftJoinMenuItem.addActionListener(this);
        mnOperations.add(this.rightJoinMenuItem);
        this.rightJoinMenuItem.addActionListener(this);
        mnOperations.add(this.cartesianProductMenuItem);
        this.cartesianProductMenuItem.addActionListener(this);
        mnOperations.add(this.unionMenuItem);
        this.unionMenuItem.addActionListener(this);
        mnOperations.add(this.intersectionMenuItem);
        this.intersectionMenuItem.addActionListener(this);
        mnOperations.add(this.groupMenuItem);
        this.groupMenuItem.addActionListener(this);

        this.backButton.addActionListener(this);
        this.importButton.addActionListener(this);
        this.runButton.addActionListener(this);
        this.runSelection.addActionListener(this);

        this.setIcons();
    }

    private void run() {
        this.run(this.textPane.getText());
    }

    private void run(String text) {
        if (text == null) return;

        this.console.setText("");

        RelAlgebraParser parser = new RelAlgebraParser(new CommonTokenStream(new RelAlgebraLexer(CharStreams.fromString(text))));

        parser.removeErrorListeners();

        DslErrorListener errorListener = new DslErrorListener();
        parser.addErrorListener(errorListener);

        ParseTreeWalker walker = new ParseTreeWalker();

        AntlrController listener = new AntlrController();

        walker.walk(listener, parser.command());

        this.execute();
    }

    private void execute() {
        if (DslErrorListener.getErrors().isEmpty()) {
            try {
                DslController.parser();
            } catch (InputException exception) {
                DslErrorListener.throwError(this.console);
            }
            return;
        }

        DslErrorListener.throwError(this.console);
    }

    private void setIcons() {
        int iconsSize = 20;

        FontIcon iconBack = FontIcon.of(Dashicons.ARROW_LEFT_ALT);
        iconBack.setIconSize(iconsSize);
        this.backButton.setIcon(iconBack);

        FontIcon iconImport = FontIcon.of(Dashicons.OPEN_FOLDER);
        iconImport.setIconSize(iconsSize);
        this.importButton.setIcon(iconImport);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == this.backButton) {
            this.mainController.goBackToMain();
        } else if (event.getSource() == this.runButton) {
            this.run();
        } else if (event.getSource() == this.runSelection) {
            this.run(this.textPane.getSelectedText());
        } else if (event.getSource() == this.selectionMenuItem) {
            this.insertOperation(ConstantController.getString("textEditor.operations.example.selection"));
        } else if (event.getSource() == this.projectionMenuItem) {
            this.insertOperation(ConstantController.getString("textEditor.operations.example.projection"));
        } else if (event.getSource() == this.joinMenuItem) {
            this.insertOperation(ConstantController.getString("textEditor.operations.example.join"));
        } else if (event.getSource() == this.leftJoinMenuItem) {
            this.insertOperation(ConstantController.getString("textEditor.operations.example.leftJoin"));
        } else if (event.getSource() == this.rightJoinMenuItem) {
            this.insertOperation(ConstantController.getString("textEditor.operations.example.rightJoin"));
        } else if (event.getSource() == this.cartesianProductMenuItem) {
            this.insertOperation(ConstantController.getString("textEditor.operations.example.cartesianProduct"));
        } else if (event.getSource() == this.unionMenuItem) {
            this.insertOperation(ConstantController.getString("textEditor.operations.example.union"));
        } else if (event.getSource() == this.intersectionMenuItem) {
            this.insertOperation(ConstantController.getString("textEditor.operations.example.intersection"));
        } else if (event.getSource() == this.groupMenuItem) {
            this.insertOperation(ConstantController.getString("textEditor.operations.example.group"));
        } else if (event.getSource() == this.importButton) {
            this.importText();
        }
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

                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }

            this.textPane.setText(stringBuilder.toString());
        }
    }

    private void insertOperation(String text) {
        try {
            this.textPane.getDocument().insertString(this.textPane.getCaretPosition(), text, null);
        } catch (BadLocationException exception) {
            exception.printStackTrace();
        }
    }

    public static String getLastPath() {
        return lastPath;
    }
}
