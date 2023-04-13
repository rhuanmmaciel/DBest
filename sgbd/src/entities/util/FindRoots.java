package entities.util;

import java.util.ArrayList;
import java.util.List;

import entities.Cell;
import entities.OperationCell;

public class FindRoots {

	public static List<Cell> getRoots(Cell cell){
			
		List<Cell> parents = new ArrayList<>();
		getSourcesRecursive(cell, parents);
		
		return parents;
	}
	
    public static void getSourcesRecursive(Cell cell, List<Cell> sources) {
    	
        if (!cell.hasParents()) {
        	
            sources.add(cell);
            
        } else {
        	
            for (Cell parent : ((OperationCell)cell).getParents()) {
            	
                getSourcesRecursive(parent, sources);
                
            }
            
        }
    }
	
	
}
