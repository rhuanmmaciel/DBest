package gui.frames.forms.operations.panelstruct;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;

import com.mxgraph.model.mxCell;

import controllers.ConstantController;

import gui.frames.forms.operations.BooleanExpressionForm;

import lib.booleanexpression.enums.LogicalOperator;

public class LogicalPane extends ExpressionPane implements ActionListener {

    private final JButton andButton = new JButton("AND");

    private final JButton orButton = new JButton("OR");

    private final JButton atomicButton = new JButton(ConstantController.getString("operationForm.atomicPane.atomicExpression"));

    public final LogicalOperator logicalOperator;

    private final Box childrenBox = Box.createVerticalBox();

    private final List<ExpressionPane> children = new ArrayList<>();

    public LogicalPane(BooleanExpressionForm root, mxCell jCell, LogicalOperator logicalOperator) {
        super(root, jCell);

        this.logicalOperator = logicalOperator;

        this.add(new JLabel(logicalOperator.name()), BorderLayout.NORTH);

        Box boxButtons = Box.createHorizontalBox();

        boxButtons.add(this.andButton);
        boxButtons.add(this.orButton);
        boxButtons.add(this.atomicButton);
        boxButtons.add(this.deleteExpressionButton);

        this.add(boxButtons, BorderLayout.CENTER);

        Box boxPadding = Box.createHorizontalBox();

        boxPadding.add(Box.createHorizontalStrut(20));
        boxPadding.add(this.childrenBox);

        this.add(boxPadding, BorderLayout.SOUTH);

        this.andButton.addActionListener(this);
        this.orButton.addActionListener(this);
        this.atomicButton.addActionListener(this);

        this.andButton.addActionListener(root);
        this.orButton.addActionListener(root);
        this.atomicButton.addActionListener(root);
        this.deleteExpressionButton.addActionListener(root);

        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == this.andButton || actionEvent.getSource() == this.orButton) {
            LogicalPane logicalPane =
                actionEvent.getSource() == this.andButton ?
                    new LogicalPane(this.root, this.jCell, LogicalOperator.AND) :
                    new LogicalPane(this.root, this.jCell, LogicalOperator.OR);

            this.children.add(logicalPane);
            this.childrenBox.add(logicalPane);
        } else if (actionEvent.getSource() == this.atomicButton) {
            AtomicPane atomicPane = new AtomicPane(this.root, this.jCell);

            this.children.add(atomicPane);
            this.childrenBox.add(atomicPane);
        }

        this.revalidate();
        this.updateRootSize();
    }

    public List<ExpressionPane> getChildren() {
        return this.children;
    }

    public void removeChild(ExpressionPane expressionPane) {
        this.children.remove(expressionPane);
        this.childrenBox.remove(expressionPane);
    }
}
