package entities.util;

import java.util.ArrayList;
import java.util.List;

import entities.Cell;
import entities.OperationCell;
import entities.TableCell;

public class FindRoots {

	public static List<TableCell> getRoots(Cell cell){
			
		List<TableCell> parents = new ArrayList<>();
		getSourcesRecursive(cell, parents);
		
		return parents;
	}
	
    public static void getSourcesRecursive(Cell cell, List<TableCell> sources) {
    	
        if (cell instanceof TableCell) {
        	
            sources.add((TableCell)cell);
            
        } else {
        	
            for (Cell parent : ((OperationCell)cell).getParents()) {
            	
                getSourcesRecursive(parent, sources);
                
            }
            
        }
    }
	
	
}
