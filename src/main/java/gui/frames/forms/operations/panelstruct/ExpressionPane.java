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
    
    protected boolean acceptFilters = true;
    protected boolean acceptReferenceFilters = true;
    

    public ExpressionPane(BooleanExpressionForm root, mxCell jCell, boolean acceptFilters, boolean acceptReferenceFilters){

        this.root = root;
        this.jCell = jCell;
        this.acceptFilters = acceptFilters;
        this.acceptReferenceFilters = acceptReferenceFilters;

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
