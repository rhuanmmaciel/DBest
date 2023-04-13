package entities.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import controller.ActionClass;
import entities.Cell;
import entities.OperationCell;
import entities.Tree;

public class TreeUtils {

	public static void identifyTrees(Map<Integer, Tree> trees) {
		
	    trees.clear();

	    Set<Cell> allRoots = new HashSet<>();
	    List<Set<Cell>> treeRoots = new ArrayList<>();
	    
	    
	    for(Cell cell : ActionClass.getCells().values()) {
	    	
	    	treeRoots.add(new HashSet<>(cell.getAllSourceTables()));
	    	
	    	if(!cell.hasParents()) {
	    		
	    		allRoots.add(cell);
	    		
	    	}
	    	
	    }
	    
	    boolean allListVerified = false;
	    while(!allListVerified){
	    	
	    	int[] indexes = new int[2]; 
	    	allListVerified = true;
	    	
	    	for(Set<Cell> roots : treeRoots) {
	    		
	    		List<Set<Cell>> treeRoots2 = new ArrayList<>(treeRoots);
	    		treeRoots2.remove(roots);
	    		
	    		for(Set<Cell> roots2 : treeRoots2) {
	    			
	    			if(!Collections.disjoint(roots, roots2)) {
	    				
	    				allListVerified = false;
	    				indexes[0] = treeRoots.indexOf(roots);
	    				indexes[1] = treeRoots.indexOf(roots2);
	    				break;
	    			}
	    			
	    		}
	    		
	    		if(!allListVerified) break;
	    		
	    	}
	    	
	    	if(!allListVerified) {
	    		
	    		treeRoots.get(indexes[0]).addAll(treeRoots.get(indexes[1]));
	    		treeRoots.remove(indexes[1]);
	    		
	    	}

	    }
	    
	    int i = 0;
	    for(Set<Cell> roots : treeRoots) {
	    	
	    	trees.put(i, new Tree(i++, roots));
	    	
	    }
	    
    	for(Tree tree : trees.values()) {
    		recalculateContent(tree);
    	}

	}
	
	public static void recalculateContent(Tree tree) {
		
		List<Cell> level = tree.getRoots();
		
		while(!level.isEmpty()) {
			
			Set<Cell> children = new HashSet<>();
			
			for(Cell cell : level) {
				
				if(cell instanceof OperationCell) {
					
					((OperationCell)cell).updateOperation();
				
				}
				
				if(cell.hasChild()) {
					
					children.add(cell.getChild());
					
				}
				
			}
			
			level.clear();
			level.addAll(children);
			
		}
		
		
	}
	
}
