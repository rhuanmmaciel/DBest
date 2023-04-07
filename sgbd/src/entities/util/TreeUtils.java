package entities.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mxgraph.model.mxCell;

import entities.Cell;
import entities.TableCell;
import entities.Tree;

public class TreeUtils {

	public static void updateTree(Map<Integer, Tree> trees, Map<mxCell, Cell> cells) {
		
	    trees.clear();

	    Set<TableCell> allRoots = new HashSet<>();
	    List<Set<TableCell>> treeRoots = new ArrayList<>();
	    
	    
	    for(Cell cell : cells.values()) {
	    	
	    	treeRoots.add(new HashSet<>(cell.getAllSourceTables()));
	    	for(TableCell root : cell.getAllSourceTables()) {
	    		
	    		if(root.hasChild()) allRoots.add(root);
	    		
	    	}
	    	
	    }
	    
	    boolean allListVerified = false;
	    while(!allListVerified){
	    	
	    	int[] indexes = new int[2]; 
	    	allListVerified = true;
	    	
	    	for(Set<TableCell> roots : treeRoots) {
	    		
	    		List<Set<TableCell>> treeRoots2 = new ArrayList<>(treeRoots);
	    		treeRoots2.remove(roots);
	    		
	    		for(Set<TableCell> roots2 : treeRoots2) {
	    			
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
	    for(Set<TableCell> roots : treeRoots) {
	    	
	    	trees.put(i, new Tree(i++, roots));
	    	
	    }

	}
	
}
