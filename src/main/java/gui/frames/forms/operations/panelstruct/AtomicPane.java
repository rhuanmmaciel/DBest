package gui.frames.forms.operations.panelstruct;

import booleanexpression.BooleanExpressionRecognizer;
import com.mxgraph.model.mxCell;
import controllers.ConstantController;
import gui.frames.forms.operations.AtomicExpressionForm;
import gui.frames.forms.operations.BooleanExpressionForm;
import lib.booleanexpression.entities.expressions.BooleanExpression;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AtomicPane extends ExpressionPane implements ActionListener {

    private final JButton btnEdit = new JButton(ConstantController.getString("operationForm.atomicPane.edit"));
    private final mxCell jCell;
    private final JLabel expression = new JLabel();

    public AtomicPane(BooleanExpressionForm root, mxCell jCell){

        super(root, jCell);
        this.jCell = jCell;
        add(new JLabel(ConstantController.getString("operationForm.atomicPane.atomicExpression")), BorderLayout.NORTH);

        add(expression, BorderLayout.CENTER);
        add(btnDeleteExpression, BorderLayout.EAST);
        add(btnEdit, BorderLayout.SOUTH);

        btnEdit.addActionListener(this);
        btnEdit.addActionListener(root);
        btnDeleteExpression.addActionListener(root);

        setVisible(true);

    }

    public AtomicPane(BooleanExpressionForm root, mxCell jCell, BooleanExpression booleanExpression){

        this(root, jCell);

        this.booleanExpression = booleanExpression;
        expression.setText(new BooleanExpressionRecognizer(jCell).recognizer(booleanExpression));
        updateRootSize();
        root.checkBtnReady();

    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {

        if(actionEvent.getSource() == btnEdit){

            booleanExpression = new AtomicExpressionForm(root, jCell).getResult();
            expression.setText( booleanExpression != null ?
                    new BooleanExpressionRecognizer(jCell).recognizer(booleanExpression) :
                    "");
            updateRootSize();
            root.checkBtnReady();

        }

    }

    public String getAtomicExpression(){

        return expression.getText();

    }
}
