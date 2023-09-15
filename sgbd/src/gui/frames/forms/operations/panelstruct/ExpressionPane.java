package gui.frames.forms.operations.panelstruct;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.mxgraph.model.mxCell;

import gui.frames.forms.operations.BooleanExpressionForm;

import lib.booleanexpression.entities.expressions.BooleanExpression;

public abstract class ExpressionPane extends JPanel {

    protected final JButton deleteExpressionButton = new JButton(" X ");

    protected final mxCell jCell;

    public BooleanExpression booleanExpression;

    protected final BooleanExpressionForm root;

    protected ExpressionPane(BooleanExpressionForm root, mxCell jCell) {
        this.root = root;
        this.jCell = jCell;

        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        this.deleteExpressionButton.addActionListener(actionEvent -> root.removeLayer(this));
    }

    protected void updateRootSize() {
        this.root.revalidate();
        this.root.pack();
    }
}
