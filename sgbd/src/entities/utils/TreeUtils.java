package entities.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mxgraph.model.mxCell;

import controller.ActionClass;
import entities.Tree;
import entities.cells.Cell;
import entities.cells.OperationCell;

public class TreeUtils {
	
	public static Map<mxCell, Cell> cells = ActionClass.getCells();
	public static Map<Integer, Tree> trees = ActionClass.getTrees();
	
	public static void updateTree(Cell cell) {
		
		Set<Tree> trees = new HashSet<>();
		
		Tree tree = cell.getTree();
		
		List<Cell> level = cell.getAllSourceTables();
		
		while (!level.isEmpty()) {
			
			Set<Cell> children = new HashSet<>();

			for (Cell cellAux : level) {

				trees.add(cellAux.getTree());

				cellAux.setNewTree(tree);
				
				if (cellAux.hasChild()) {

					children.add(cellAux.getChild());

				}

			}

			level.clear();
			level.addAll(children);

		}
		
		for(Tree treeAux : trees) updateTree(treeAux);
		
	}

	private static void updateTree(Tree tree) {

		int counter = 0;
		
		for (Cell cellAux : cells.values()) {
			if (cellAux.getTree() != null && cellAux.getTree().equals(tree))
				counter++;
		}

		if (counter <= 0)
			trees.remove(tree.getIndex());

	}
	
	public static void deleteTree(Tree tree) {
		trees.remove(tree.getIndex());
	}

	public static void recalculateContent(Tree tree) {
		
		if(tree != null) {
			
			List<Cell> level = tree.getRoots();
	
			while (!level.isEmpty()) {
	
				Set<Cell> children = new HashSet<>();
	
				for (Cell cell : level) {
	
					if (cell instanceof OperationCell) {
	
						((OperationCell) cell).updateOperation();
	
					}
	
					if (cell.hasChild()) {
	
						children.add(cell.getChild());
	
					}
	
				}
	
				level.clear();
				level.addAll(children);
	
			}
			
		}
		
	}
	
	public static void recalculateContent(Cell cell) {
		
		if(cell != null) {
			
			Cell currentCell = cell;
			
			if (currentCell instanceof OperationCell) 
				((OperationCell) currentCell).updateOperation();
			
			while(currentCell.hasChild()){
	
				currentCell = currentCell.getChild();
				((OperationCell) currentCell).updateOperation();
	
			}
			
		}
		
	}

}
