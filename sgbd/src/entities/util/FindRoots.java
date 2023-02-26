package entities.util;

import java.util.ArrayList;
import java.util.List;

import entities.Cell;

public class FindRoots {

	public static List<Cell> getRoots(Cell cell){
			
		List<Cell> parents = new ArrayList<>();
		getSourcesRecursive(cell, parents);
		
		return parents;
	}
	
    public static void getSourcesRecursive(Cell cell, List<Cell> sources) {
        if (cell.getParents().isEmpty()) {
            sources.add(cell);
        } else {
            for (Cell parent : cell.getParents()) {
                getSourcesRecursive(parent, sources);
            }
        }
    }
	
	
}
