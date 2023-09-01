package entities.utils;

import java.util.ArrayList;
import java.util.List;

import entities.cells.Cell;

public class FindRoots {

	public static List<Cell> getRoots(Cell cell){
		List<Cell> roots = new ArrayList<>();

		getSourcesRecursively(cell, roots);
		
		return roots;
	}
	
    public static void getSourcesRecursively(Cell cell, List<Cell> sources) {
        if (!cell.hasParents()) {
            sources.add(cell);
        } else {
            for (Cell parent : cell.getParents()) {
                getSourcesRecursively(parent, sources);
            }
        }
    }
}
