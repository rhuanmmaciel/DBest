package entities;

import java.util.Map;

import javax.swing.JOptionPane;

import com.mxgraph.model.mxCell;

import enums.OperationArity;

public class Edge {

	private mxCell parent;
	private mxCell child;

	public Edge() {
		parent = null;
		child = null;
	}

	public void addParent(mxCell parent, Map<mxCell, Cell> cells) {

		Cell cell = cells.get(parent);
		
		boolean cellHasTree; 
		
		if(cell instanceof OperatorCell) {

			cellHasTree = ((OperatorCell) cell).hasTree();
			
		}else {
			
			cellHasTree = true;
			
		}

		
		if (!cell.hasChild() && cellHasTree)
			this.parent = parent;
		
		if(!cellHasTree)
			JOptionPane.showMessageDialog(null, "Uma operação vazia não pode se associar a ninguém", "Erro", JOptionPane.ERROR_MESSAGE);
	}

	public void addChild(mxCell child, Map<mxCell, Cell> cells) {

		Cell cell = cells.get(child);

		boolean isOperatorCell = !(cell instanceof TableCell);
		boolean hasEnoughParents;
		
		if(isOperatorCell) {
			
			if(((OperatorCell)cell).getArity() == OperationArity.UNARY) {
					
				hasEnoughParents =((OperatorCell) cell).getParents().size() >= 1;
					
			}else{
				
				hasEnoughParents =((OperatorCell) cell).getParents().size() >= 2;
				
			}
			
		}else {
			
			hasEnoughParents = false;
			
		}

		if (hasParent() && isOperatorCell && !hasEnoughParents)
			this.child = child;

		if (!isOperatorCell)
			JOptionPane.showMessageDialog(null, "Uma tabela não pode ser associada a outra", "Erro", JOptionPane.ERROR_MESSAGE);

		if (hasEnoughParents && ((OperatorCell)cell).getArity() == OperationArity.UNARY)
			JOptionPane.showMessageDialog(null, "Não é possível associar duas tabelas a uma operação unária", "Erro",
					JOptionPane.ERROR_MESSAGE);

		if (hasEnoughParents && ((OperatorCell)cell).getArity() == OperationArity.BINARY)
			JOptionPane.showMessageDialog(null, "Não é possível associar três tabelas a uma operação binária", "Erro",
					JOptionPane.ERROR_MESSAGE);
	
	}

	public Boolean hasParent() {

		return parent != null;

	}

	public boolean isDifferent(mxCell jCell) {

		return hasParent() && jCell != parent;

	}

	public void reset() {
		parent = null;
		child = null;
	}

	public Boolean isReady() {

		return child != null && parent != null;

	}

	public mxCell[] getEdge() {

		return new mxCell[] { parent, child };

	}

	public mxCell getParent() {

		return parent;

	}

}
