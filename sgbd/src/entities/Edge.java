package entities;

import java.io.Serializable;

import java.util.Optional;

import javax.swing.JOptionPane;

import com.mxgraph.model.mxCell;

import entities.cells.Cell;
import entities.cells.OperationCell;
import entities.cells.TableCell;
import entities.utils.cells.CellUtils;

import enums.OperationArity;

public class Edge implements Serializable {

    private mxCell parent;

    private mxCell child;

    public Edge() {
        this.parent = null;
        this.child = null;
    }

    public void addParent(mxCell parent) {
        Optional<Cell> optionalCell = CellUtils.getActiveCell(parent);

        if (optionalCell.isEmpty()) return;

        Cell cell = optionalCell.get();

        boolean cellHasError = cell.hasError();
        boolean cellIsParent = cell.hasChild();
        boolean cellHasTree;

        if (cell instanceof OperationCell operationCell) {
            cellHasTree = operationCell.hasTree();
        } else {
            cellHasTree = true;
        }

        if (cellIsParent) {
            JOptionPane.showMessageDialog(null, "Essa célula já possui filho", "Erro", JOptionPane.ERROR_MESSAGE);
        } else if (!cellHasTree) {
            JOptionPane.showMessageDialog(null, "Uma operação vazia não pode se associar a ninguém", "Erro", JOptionPane.ERROR_MESSAGE);
        } else if (cellHasError) {
            JOptionPane.showMessageDialog(null, "Não é possível associar uma operação com erros a outra", "Erro", JOptionPane.ERROR_MESSAGE);
        } else {
            this.parent = parent;
        }
    }

    public void addChild(mxCell child) {
        Optional<Cell> optionalCell = CellUtils.getActiveCell(child);

        if (optionalCell.isEmpty()) return;

        Cell cell = optionalCell.get();

        boolean isOperatorCell = !(cell instanceof TableCell);
        boolean hasEnoughParents;

        if (isOperatorCell) {
            if (((OperationCell) cell).getArity() == OperationArity.UNARY) {
                hasEnoughParents = cell.getParents().size() == 1;
            } else {
                hasEnoughParents = cell.getParents().size() >= 2;
            }
        } else {
            hasEnoughParents = false;
        }

        if (this.hasParent() && isOperatorCell && !hasEnoughParents) {
            this.child = child;
        }

        if (!isOperatorCell) {
            JOptionPane.showMessageDialog(null, "Uma tabela não pode ser associada a outra", "Erro", JOptionPane.ERROR_MESSAGE);
        } else if (hasEnoughParents && ((OperationCell) cell).getArity() == OperationArity.UNARY) {
            JOptionPane.showMessageDialog(null, "Não é possível associar duas tabelas a uma operação unária", "Erro", JOptionPane.ERROR_MESSAGE);
        } else if (hasEnoughParents && ((OperationCell) cell).getArity() == OperationArity.BINARY) {
            JOptionPane.showMessageDialog(null, "Não é possível associar três tabelas a uma operação binária", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean hasParent() {
        return this.parent != null;
    }

    public boolean hasChild() {
        return this.child != null;
    }

    public boolean isDifferentFrom(mxCell jCell) {
        return this.hasParent() && jCell != this.parent;
    }

    public void reset() {
        this.parent = null;
        this.child = null;
    }

    public Boolean isReady() {
        return this.parent != null && this.child != null;
    }

    public mxCell[] getEdge() {
        return new mxCell[]{this.parent, this.child};
    }

    public mxCell getParent() {
        return this.parent;
    }

    public mxCell getChild() {
        return this.child;
    }

    @Override
    public String toString() {
        return String.format("%s %s", this.parent, this.child);
    }
}
