package gui.frames.forms.operations.panelstruct;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;

import com.mxgraph.model.mxCell;

import booleanexpression.BooleanExpressionRecognizer;

import controllers.ConstantController;

import gui.frames.forms.operations.AtomicExpressionForm;
import gui.frames.forms.operations.BooleanExpressionForm;

public class AtomicPane extends ExpressionPane implements ActionListener {

    private final JButton editButton = new JButton(ConstantController.getString("operationForm.atomicPane.edit"));

    private final JLabel expression = new JLabel();

    public AtomicPane(BooleanExpressionForm root, mxCell jCell) {
        super(root, jCell);

        this.add(new JLabel(ConstantController.getString("operationForm.atomicPane.atomicExpression")), BorderLayout.NORTH);

        this.add(this.expression, BorderLayout.CENTER);
        this.add(this.deleteExpressionButton, BorderLayout.EAST);
        this.add(this.editButton, BorderLayout.SOUTH);

        this.editButton.addActionListener(this);
        this.editButton.addActionListener(root);
        this.deleteExpressionButton.addActionListener(root);

        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == this.editButton) {
            this.booleanExpression = new AtomicExpressionForm(this.root, this.jCell).getResult();

            this.expression.setText(this.booleanExpression != null ? new BooleanExpressionRecognizer(this.jCell).recognizer(this.booleanExpression) : "");

            this.updateRootSize();
            this.root.checkReadyButton();
        }
    }

    public String getAtomicExpression() {
        return this.expression.getText();
    }
}
