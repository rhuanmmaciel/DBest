package entities.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mxgraph.model.mxCell;

import controller.MainController;
import entities.Tree;
import entities.cells.Cell;
import entities.cells.OperationCell;

public class TreeUtils {
	
	public static Map<mxCell, Cell> cells = Cell.getCells();
	public static Map<Integer, Tree> trees = MainController.getTrees();
	
	public static void updateTreesAboveAndBelow(List<Cell> parents, OperationCell child) {
		
		if(child == null && (parents == null || parents.isEmpty())) return;
		
		Tree previousTree = null;
		
		if(child != null) {
			
			previousTree = child.getTree();
			
			Tree childTree = new Tree();
			
			fromLeavesToRoot(child.getAllSourceTables(), childTree);

			recalculateContent(child);
			
		}
		
		if(parents != null && !parents.isEmpty()) {
			
			for(Cell parent : parents) {
				
				previousTree = parent.getTree();
				Tree parentTree = new Tree();
				fromLeavesToRoot(parent.getAllSourceTables(), parentTree);
			
			}
			
		}
		
		deleteTree(previousTree);
		
	}
	
	private static void fromLeavesToRoot(List<Cell> level, Tree tree) {
		
		while(!level.isEmpty()) {
			
			Set<Cell> children = new HashSet<>();

			for (Cell cellAux : level) {

				cellAux.setNewTree(tree);
				
				if (cellAux.hasChild()) {

					children.add(cellAux.getChild());

				}

			}

			level.clear();
			level.addAll(children);
			
		}
		
	}
	
	public static void updateTree(Cell cell) {
		
//		System.out.println("update tree cell" + cell);
		
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
		
//		System.out.println("update tree" + tree);
		
		int counter = 0;
		
		for (Cell cellAux : cells.values()) {
			if (cellAux.getTree() != null && cellAux.getTree().equals(tree))
				counter++;
		}

		if (counter <= 0)
			trees.remove(tree.getIndex());

	}
	
	public static void deleteTree(Tree tree) {
		
		if(tree == null) return;
	
		trees.remove(tree.getIndex());
	
	}

	public static void recalculateContent(Tree tree) {
//		System.out.println("recalculate COntent t" + tree);

		if(tree != null) {
			
			List<Cell> level = tree.getLeaves();
	
			while (!level.isEmpty()) {
	
				Set<Cell> children = new HashSet<>();
	
				for (Cell cell : level) {
	
					if (cell instanceof OperationCell operationCell) {
	
						operationCell.updateOperation();
	
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
			
			while(currentCell.hasChild()){
	
				currentCell = currentCell.getChild();
				((OperationCell) currentCell).updateOperation();
	
			}
			
		}
		
	}

}
