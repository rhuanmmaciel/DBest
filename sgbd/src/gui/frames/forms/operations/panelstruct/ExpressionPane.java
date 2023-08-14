package gui.frames.forms.operations.panelstruct;

import com.mxgraph.model.mxCell;
import gui.frames.forms.operations.BooleanExpressionForm;
import lib.booleanexpression.entities.expressions.BooleanExpression;

import javax.swing.*;
import java.awt.*;

public abstract class ExpressionPane extends JPanel {

    protected final JButton btnDeleteExpression = new JButton(" X ");
    protected final mxCell jCell;

    public BooleanExpression booleanExpression;

    protected final BooleanExpressionForm root;

    public ExpressionPane(BooleanExpressionForm root, mxCell jCell){

        this.root = root;
        this.jCell = jCell;

        setLayout(new BorderLayout());

        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        btnDeleteExpression.addActionListener(actionEvent -> {
            root.removeLayer(this);
        });

    }

    protected void updateRootSize(){

        root.revalidate();
        root.pack();

    }

}
