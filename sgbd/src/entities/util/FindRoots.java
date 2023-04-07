package entities.util;

import java.util.ArrayList;
import java.util.List;

import entities.Cell;
import entities.OperatorCell;
import entities.TableCell;

public class FindRoots {

	public static List<Cell> getRoots(Cell cell){
			
		List<Cell> parents = new ArrayList<>();
		getSourcesRecursive(cell, parents);
		
		return parents;
	}
	
    public static void getSourcesRecursive(Cell cell, List<Cell> sources) {
    	
        if (cell instanceof TableCell) {
        	
            sources.add(cell);
            
        } else {
        	
            for (Cell parent : ((OperatorCell)cell).getParents()) {
            	
                getSourcesRecursive(parent, sources);
                
            }
            
        }
    }
	
	
}
