package gui.frames.forms.operations.panelstruct;

import booleanexpression.BooleanExpressionRecognizer;

import com.mxgraph.model.mxCell;
import controllers.ConstantController;
import gui.frames.forms.operations.BooleanExpressionForm;
import lib.booleanexpression.entities.expressions.BooleanExpression;
import lib.booleanexpression.entities.expressions.LogicalExpression;
import lib.booleanexpression.enums.LogicalOperator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class LogicalPane extends ExpressionPane implements ActionListener {

    private final JButton btnAnd = new JButton("AND");
    private final JButton btnOr = new JButton("OR");
    private final JButton btnAtomic = new JButton(ConstantController.getString("operationForm.atomicPane.atomicExpression"));

    public final LogicalOperator logicalOperator;

    private final Box boxChildren = Box.createVerticalBox();

    private final List<ExpressionPane> children = new ArrayList<>();

    public LogicalPane(BooleanExpressionForm root, mxCell jCell, LogicalOperator logicalOperator){

        super(root, jCell);

        this.logicalOperator = logicalOperator;

        add(new JLabel(logicalOperator.name()), BorderLayout.NORTH);

        Box boxButtons = Box.createHorizontalBox();
        boxButtons.add(btnAnd);
        boxButtons.add(btnOr);
        boxButtons.add(btnAtomic);
        boxButtons.add(btnDeleteExpression);

        add(boxButtons, BorderLayout.CENTER);
        Box boxPadding = Box.createHorizontalBox();
        boxPadding.add(Box.createHorizontalStrut(20));
        boxPadding.add(boxChildren);
        add(boxPadding, BorderLayout.SOUTH);

        btnAnd.addActionListener(this);
        btnOr.addActionListener(this);
        btnAtomic.addActionListener(this);

        btnAnd.addActionListener(root);
        btnOr.addActionListener(root);
        btnAtomic.addActionListener(root);
        btnDeleteExpression.addActionListener(root);

        setVisible(true);

    }
    public LogicalPane(BooleanExpressionForm root, mxCell jCell, BooleanExpression booleanExpression){

        this(root, jCell, ((LogicalExpression)booleanExpression).getLogicalOperator());

        LogicalExpression logicalExpression = (LogicalExpression) booleanExpression;

        for (BooleanExpression expression : logicalExpression.getExpressions()){

            if(expression instanceof LogicalExpression subLogicalExpression){

                LogicalPane logicalPane = new LogicalPane(root, jCell, subLogicalExpression);
                children.add(logicalPane);
                boxChildren.add(logicalPane);

            }else{

                AtomicPane atomicPane = new AtomicPane(root, jCell, expression);
                children.add(atomicPane);
                boxChildren.add(atomicPane);

            }

            revalidate();
            updateRootSize();

        }

    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {

        if(actionEvent.getSource() == btnAnd || actionEvent.getSource() == btnOr ){

            LogicalPane logicalPane = actionEvent.getSource() == btnAnd ? new LogicalPane(root, jCell, LogicalOperator.AND) :
                    new LogicalPane(root, jCell, LogicalOperator.OR) ;
            children.add(logicalPane);
            boxChildren.add(logicalPane);

        }else if(actionEvent.getSource() == btnAtomic){

            AtomicPane atomicPane = new AtomicPane(root, jCell);
            children.add(atomicPane);
            boxChildren.add(atomicPane);

        }

        revalidate();
        updateRootSize();

    }

    public List<ExpressionPane> getChildren(){
        return children;
    }

    public void removeChild(ExpressionPane expressionPane){
        children.remove(expressionPane);
        boxChildren.remove(expressionPane);
    }

}
