package gui.frames.forms.operations;

import com.mxgraph.model.mxCell;
import controller.ConstantController;
import controller.MainController;
import entities.cells.CsvTableCell;
import entities.cells.OperationCell;
import enums.OperationType;
import gui.frames.forms.operations.panelstruct.AtomicPane;
import gui.frames.forms.operations.panelstruct.ExpressionPane;
import gui.frames.forms.operations.panelstruct.LogicalPane;
import sgbd.table.Table;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

public class BooleanExpressionForm extends OperationForm implements ActionListener, IOperationForm{

    private final JButton btnAnd = new JButton("AND");
    private final JButton btnOr = new JButton("OR");
    private final JButton btnAtomic = new JButton("Atomic Expression");

    private ExpressionPane root = null;

    public static void main(String [] args){

        mxCell op = (mxCell) MainController.getGraph().insertVertex(
                MainController.getGraph().getDefaultParent(), null, "a", 0, 0, 80, 30,
                "op");

        mxCell table = (mxCell) MainController.getGraph().insertVertex(
                MainController.getGraph().getDefaultParent(), null, "t", 0, 0, 80, 30,
                "table");

        OperationCell cell = new OperationCell(op, OperationType.SELECTION);

        cell.addParent(new CsvTableCell(table, "a", "b", Table.loadFromHeader("/home/rhuan/Documents/rep/dbest/tabelas-fbd/ator.head")));

        new BooleanExpressionForm(op);

    }

    public BooleanExpressionForm(mxCell jCell) {
        super(jCell);
        initGUI();
    }

    @Override
    protected void setPreviousArgs() {
    }

    private void initGUI() {

        centerPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeWindow();
            }
        });

        setMinimumSize(new Dimension((int)(ConstantController.UI_WIDTH * 0.8), (int)(ConstantController.UI_HEIGHT * 0.8)));
        centerPanel.removeAll();

        Container parent = centerPanel.getParent();

        parent.remove(centerPanel);
        parent.add(centerPanel, BorderLayout.NORTH);

        btnAnd.addActionListener(this);
        btnOr.addActionListener(this);
        btnAtomic.addActionListener(this);

        addExtraComponent(btnAnd, 0, 0, 1, 1);
        addExtraComponent(new JLabel(" "), 1, 0, 1, 1);
        addExtraComponent(btnOr, 2, 0, 1, 1);
        addExtraComponent(new JLabel(" "), 3, 0, 1, 1);
        addExtraComponent(btnAtomic, 4, 0, 1, 1);

        addExtraComponent(new JLabel(" "), 0, 1, 1, 1);

        setPreviousArgs();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void removeLayer(ExpressionPane pane) {
        if (root != null) {
            if (root == pane) {
                centerPanel.remove(pane);
                root = null;
                setButtonsEnabled(true);

            } else {
                removeExpressionPane(root, pane);
            }

            revalidate();
            pack();
        }
    }

    private boolean removeExpressionPane(ExpressionPane parent, ExpressionPane target) {

        if (parent instanceof LogicalPane logicalPane) {
            if (logicalPane.getChildren().contains(target)) {
                logicalPane.removeChild(target);
                return true;
            } else {
                for (ExpressionPane child : logicalPane.getChildren()) {
                    if (removeExpressionPane(child, target)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void setButtonsEnabled(boolean c){

        btnAnd.setEnabled(c);
        btnOr.setEnabled(c);
        btnAtomic.setEnabled(c);

    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {

        if(root == null && (actionEvent.getSource() == btnAnd || actionEvent.getSource() == btnOr
                || actionEvent.getSource() == btnAtomic)) {

            ExpressionPane expressionPane;

            if (actionEvent.getSource() == btnAnd)
                expressionPane = new LogicalPane(this, jCell, LogicalPane.LogicalOperator.AND);
            else if (actionEvent.getSource() == btnOr)
                expressionPane = new LogicalPane(this, jCell, LogicalPane.LogicalOperator.OR);
            else
                expressionPane = new AtomicPane(this, jCell);

            root = expressionPane;
            addExtraComponent(new JScrollPane(expressionPane), 0, 2, 1, 1);

            setButtonsEnabled(false);

        }

        revalidate();
        pack();

    }

    protected void closeWindow() {
        dispose();
    }

}
