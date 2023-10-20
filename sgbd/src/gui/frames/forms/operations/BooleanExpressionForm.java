package gui.frames.forms.operations;

import booleanexpression.BooleanExpressionRecognizer;
import com.mxgraph.model.mxCell;
import controllers.ConstantController;
import gui.frames.forms.IFormCondition;
import gui.frames.forms.operations.panelstruct.AtomicPane;
import gui.frames.forms.operations.panelstruct.ExpressionPane;
import gui.frames.forms.operations.panelstruct.LogicalPane;
import lib.booleanexpression.entities.expressions.BooleanExpression;
import lib.booleanexpression.entities.expressions.LogicalExpression;
import lib.booleanexpression.enums.LogicalOperator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class BooleanExpressionForm extends OperationForm implements ActionListener, IOperationForm, IFormCondition {

    private final JButton btnAnd = new JButton("AND");
    private final JButton btnOr = new JButton("OR");
    private final JButton btnAtomic = new JButton(ConstantController.getString("operationForm.booleanExpression.atomicExpressionButton"));
    private final mxCell jCell;
    private ExpressionPane root = null;

    public BooleanExpressionForm(mxCell jCell) {
        super(jCell);
        this.jCell = jCell;
        initGUI();
    }

    @Override
    protected void setPreviousArgs() {
    }

    @Override
    public void initGUI() {

        centerPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeWindow();
            }
        });

        setMinimumSize(new Dimension((int)(ConstantController.UI_SCREEN_WIDTH * 0.8), (int)(ConstantController.UI_SCREEN_HEIGHT * 0.8)));
        centerPanel.removeAll();

        Container parent = centerPanel.getParent();

        parent.remove(centerPanel);
        parent.add(centerPanel, BorderLayout.NORTH);

        btnReady.addActionListener(this);
        btnAnd.addActionListener(this);
        btnOr.addActionListener(this);
        btnAtomic.addActionListener(this);

        initButtons();

        addExtraComponent(new JLabel(" "), 0, 1, 1, 1);

        setPreviousArgs();
        checkBtnReady();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initButtons(){

        addExtraComponent(btnAnd, 0, 0, 1, 1);
        addExtraComponent(new JLabel(" "), 1, 0, 1, 1);
        addExtraComponent(btnOr, 2, 0, 1, 1);
        addExtraComponent(new JLabel(" "), 3, 0, 1, 1);
        addExtraComponent(btnAtomic, 4, 0, 1, 1);

    }

    public void removeLayer(ExpressionPane pane) {

        if (root != null) {

            if (root == pane) {

                centerPanel.removeAll();
                initButtons();
                root = null;
                setButtonsEnabled(true);

            } else
                removeExpressionPane(root, pane);

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
                expressionPane = new LogicalPane(this, jCell, LogicalOperator.AND);
            else if (actionEvent.getSource() == btnOr)
                expressionPane = new LogicalPane(this, jCell, LogicalOperator.OR);
            else
                expressionPane = new AtomicPane(this, jCell);

            root = expressionPane;
            addExtraComponent(new JScrollPane(expressionPane), 0, 2, 1, 1);

            setButtonsEnabled(false);

        }

        if(root != null && actionEvent.getSource() == btnReady){

            if(root instanceof AtomicPane atomicPane){

                arguments.add(atomicPane.getAtomicExpression());

            }

            else {

                arguments.add(new BooleanExpressionRecognizer(jCell).recognizer(getBooleanExpression(root)));

            }

            btnReady();

        }
        checkBtnReady();

        revalidate();
        pack();

    }

    private BooleanExpression getBooleanExpression(ExpressionPane expressionPane){

        if(expressionPane instanceof AtomicPane atomicPane) return atomicPane.booleanExpression;

        List<BooleanExpression> arguments = new ArrayList<>();
        for (ExpressionPane exp : ((LogicalPane) expressionPane).getChildren())
            arguments.add(getBooleanExpression(exp));

        LogicalOperator logicalOperator = ((LogicalPane) expressionPane).logicalOperator;

        return new LogicalExpression( logicalOperator, arguments);

    }

    protected void closeWindow() {
        dispose();
    }


    @Override
    public void checkBtnReady() {

        boolean noArgument = root == null;
        boolean leafNotAnAtomic = false;
        boolean anyAtomicIncomplete = false;

        if(!noArgument && root instanceof LogicalPane logicalPane){

            List<ExpressionPane> children = new ArrayList<>(logicalPane.getChildren());

            if(logicalPane.getChildren().isEmpty()) leafNotAnAtomic = true;

            do{

                List<ExpressionPane> childrenAux = new ArrayList<>(children);
                children.clear();
                for(ExpressionPane exp : childrenAux){

                    if(exp instanceof LogicalPane log){

                        if(log.getChildren().isEmpty()) leafNotAnAtomic = true;
                        children.addAll(log.getChildren());

                    }
                    if(exp instanceof AtomicPane atomicPane &&
                            (atomicPane.getAtomicExpression() == null || atomicPane.getAtomicExpression().isEmpty() ||
                            atomicPane.getAtomicExpression().isBlank())) {
                        anyAtomicIncomplete = true;
                    }

                }

            }while (!leafNotAnAtomic && !anyAtomicIncomplete && !children.isEmpty());

        }else if(!noArgument && root instanceof AtomicPane atomicPane &&
                (atomicPane.getAtomicExpression() == null || atomicPane.getAtomicExpression().isEmpty() ||
                atomicPane.getAtomicExpression().isBlank()))
            anyAtomicIncomplete = true;


        btnReady.setEnabled(!noArgument && !leafNotAnAtomic && !anyAtomicIncomplete);

        updateToolTipText(noArgument, leafNotAnAtomic, anyAtomicIncomplete);

    }

    @Override
    public void updateToolTipText(boolean ...conditions) {

        boolean noArgument = conditions[0];
        boolean leafNotAnAtomic = conditions[1];
        boolean anyAtomicIncomplete = conditions[2];

        String btnReadyToolTipText = "";

        if (noArgument)
            btnReadyToolTipText = "- " + ConstantController.getString("operationForm.booleanExpression.toolTip.selectOne");
        else if(leafNotAnAtomic)
            btnReadyToolTipText = "- " + ConstantController.getString("operationForm.booleanExpression.toolTip.mostInternal");
        else if(anyAtomicIncomplete)
            btnReadyToolTipText = "- " + ConstantController.getString("operationForm.booleanExpression.toolTip.incomplete");

        UIManager.put("ToolTip.foreground", Color.RED);

        btnReady.setToolTipText(btnReadyToolTipText.isEmpty() ? null : btnReadyToolTipText);

    }
}
