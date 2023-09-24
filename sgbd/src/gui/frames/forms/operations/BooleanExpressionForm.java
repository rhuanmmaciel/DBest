package gui.frames.forms.operations;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

import com.mxgraph.model.mxCell;

import booleanexpression.BooleanExpressionRecognizer;
import controllers.ConstantController;
import gui.frames.forms.IFormCondition;
import gui.frames.forms.operations.panelstruct.AtomicPane;
import gui.frames.forms.operations.panelstruct.ExpressionPane;
import gui.frames.forms.operations.panelstruct.LogicalPane;
import lib.booleanexpression.entities.expressions.BooleanExpression;
import lib.booleanexpression.entities.expressions.LogicalExpression;
import lib.booleanexpression.enums.LogicalOperator;

public class BooleanExpressionForm extends OperationForm implements ActionListener, IOperationForm, IFormCondition {

    private final JButton andButton = new JButton(ConstantController.getString("operationForm.booleanExpression.andButton"));

    private final JButton orButton = new JButton(ConstantController.getString("operationForm.booleanExpression.orButton"));

    private final JButton atomicButton = new JButton(ConstantController.getString("operationForm.booleanExpression.atomicExpressionButton"));

    private final mxCell jCell;

    private ExpressionPane root = null;

    public BooleanExpressionForm(mxCell jCell) {
        super(jCell);
        this.jCell = jCell;
        this.initGUI();
    }

    @Override
    protected void setPreviousArgs() {

    }

    private void initGUI() {
        this.centerPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent event) {
                BooleanExpressionForm.this.closeWindow();
            }
        });

        this.setMinimumSize(new Dimension(
            (int) (ConstantController.UI_SCREEN_WIDTH * 0.8),
            (int) (ConstantController.UI_SCREEN_HEIGHT * 0.8)
        ));

        this.centerPanel.removeAll();

        Container parent = this.centerPanel.getParent();

        parent.remove(this.centerPanel);
        parent.add(this.centerPanel, BorderLayout.NORTH);

        this.readyButton.addActionListener(this);
        this.andButton.addActionListener(this);
        this.orButton.addActionListener(this);
        this.atomicButton.addActionListener(this);

        this.initButtons();

        this.addExtraComponent(new JLabel(" "), 0, 1, 1, 1);

        this.setPreviousArgs();
        this.checkReadyButton();

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void initButtons() {
        this.addExtraComponent(this.andButton, 0, 0, 1, 1);
        this.addExtraComponent(new JLabel(" "), 1, 0, 1, 1);
        this.addExtraComponent(this.orButton, 2, 0, 1, 1);
        this.addExtraComponent(new JLabel(" "), 3, 0, 1, 1);
        this.addExtraComponent(this.atomicButton, 4, 0, 1, 1);
    }

    public void removeLayer(ExpressionPane pane) {
        if (this.root != null) {
            if (this.root == pane) {
                this.centerPanel.removeAll();
                this.initButtons();
                this.root = null;
                this.setButtonsEnabled(true);
            } else {
                this.removeExpressionPane(this.root, pane);
            }

            this.revalidate();
            this.pack();
        }
    }

    private boolean removeExpressionPane(ExpressionPane parent, ExpressionPane target) {
        if (parent instanceof LogicalPane logicalPane) {
            if (logicalPane.getChildren().contains(target)) {
                logicalPane.removeChild(target);
                return true;
            } else {
                for (ExpressionPane child : logicalPane.getChildren()) {
                    if (this.removeExpressionPane(child, target)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void setButtonsEnabled(boolean isEnabled) {
        this.andButton.setEnabled(isEnabled);
        this.orButton.setEnabled(isEnabled);
        this.atomicButton.setEnabled(isEnabled);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (
            this.root == null &&
            (
                actionEvent.getSource() == this.andButton ||
                actionEvent.getSource() == this.orButton ||
                actionEvent.getSource() == this.atomicButton
            )
        ) {
            ExpressionPane expressionPane;

            if (actionEvent.getSource() == this.andButton) {
                expressionPane = new LogicalPane(this, this.jCell, LogicalOperator.AND);
            } else if (actionEvent.getSource() == this.orButton) {
                expressionPane = new LogicalPane(this, this.jCell, LogicalOperator.OR);
            } else {
                expressionPane = new AtomicPane(this, this.jCell);
            }

            this.root = expressionPane;

            this.addExtraComponent(new JScrollPane(expressionPane), 0, 2, 1, 1);
            this.setButtonsEnabled(false);
        }

        if (this.root != null && actionEvent.getSource() == this.readyButton) {
            if (this.root instanceof AtomicPane atomicPane) {
                this.arguments.add(atomicPane.getAtomicExpression());
            } else {
                this.arguments.add(new BooleanExpressionRecognizer(this.jCell).recognizer(this.getBooleanExpression(this.root)));
            }

            this.onReadyButtonClicked();
        }

        this.checkReadyButton();

        this.revalidate();
        this.pack();
    }

    private BooleanExpression getBooleanExpression(ExpressionPane expressionPane) {
        if (expressionPane instanceof AtomicPane atomicPane) return atomicPane.booleanExpression;

        List<BooleanExpression> arguments = new ArrayList<>();

        for (ExpressionPane childExpressionPane : ((LogicalPane) expressionPane).getChildren()) {
            arguments.add(this.getBooleanExpression(childExpressionPane));
        }

        LogicalOperator logicalOperator = ((LogicalPane) expressionPane).logicalOperator;

        return new LogicalExpression(logicalOperator, arguments);
    }

    protected void closeWindow() {
        this.dispose();
    }

    @Override
    public void checkReadyButton() {
        boolean hasNoArgument = this.root == null;
        boolean leafIsNotAnAtomic = false;
        boolean anyAtomicIncomplete = false;

        if (!hasNoArgument && this.root instanceof LogicalPane logicalPane) {
            List<ExpressionPane> children = new ArrayList<>(logicalPane.getChildren());

            if (logicalPane.getChildren().isEmpty()) leafIsNotAnAtomic = true;

            do {
                List<ExpressionPane> childrenAux = new ArrayList<>(children);

                children.clear();

                for (ExpressionPane exp : childrenAux) {
                    if (exp instanceof LogicalPane log) {
                        if (log.getChildren().isEmpty()) leafIsNotAnAtomic = true;
                        children.addAll(log.getChildren());
                    }

                    if (
                        exp instanceof AtomicPane atomicPane &&
                        (
                            atomicPane.getAtomicExpression() == null ||
                            atomicPane.getAtomicExpression().isEmpty() ||
                            atomicPane.getAtomicExpression().isBlank()
                        )
                    ) {
                        anyAtomicIncomplete = true;
                        System.out.println(atomicPane.getAtomicExpression());
                    }
                }
            } while (!leafIsNotAnAtomic && !anyAtomicIncomplete && !children.isEmpty());
        } else if (
            !hasNoArgument &&
            this.root instanceof AtomicPane atomicPane &&
            (
                atomicPane.getAtomicExpression() == null ||
                atomicPane.getAtomicExpression().isEmpty() ||
                atomicPane.getAtomicExpression().isBlank()
            )
        ) {
            anyAtomicIncomplete = true;
        }

        this.readyButton.setEnabled(!hasNoArgument && !leafIsNotAnAtomic && !anyAtomicIncomplete);

        this.updateToolTipText(hasNoArgument, leafIsNotAnAtomic, anyAtomicIncomplete);
    }

    @Override
    public void updateToolTipText(boolean... conditions) {
        boolean noArgument = conditions[0];
        boolean leafNotAnAtomic = conditions[1];
        boolean anyAtomicIncomplete = conditions[2];

        String readyButtonToolTipText = "";

        if (noArgument) {
            readyButtonToolTipText = String.format("- %s", ConstantController.getString("operationForm.booleanExpression.toolTip.selectOne"));
        } else if (leafNotAnAtomic) {
            readyButtonToolTipText = String.format("- %s", ConstantController.getString("operationForm.booleanExpression.toolTip.mostInternal"));
        } else if (anyAtomicIncomplete) {
            readyButtonToolTipText = String.format("- %s", ConstantController.getString("operationForm.booleanExpression.toolTip.incomplete"));
        }

        UIManager.put("ToolTip.foreground", Color.RED);

        this.readyButton.setToolTipText(readyButtonToolTipText.isEmpty() ? null : readyButtonToolTipText);
    }
}
