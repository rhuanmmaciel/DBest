package entities.utils;

import java.util.ArrayList;
import java.util.List;

import entities.cells.Cell;

public class RootFinder {

    private RootFinder() {

    }

	public static List<Cell> findRoots(Cell cell){
		List<Cell> roots = new ArrayList<>();

        if (cell == null) return roots;

		findRootsRecursively(cell, roots);
		
		return roots;
	}
	
    private static void findRootsRecursively(Cell cell, List<Cell> sources) {
        if (!cell.hasParents()) {
            sources.add(cell);
        } else {
            for (Cell parent : cell.getParents()) {
                findRootsRecursively(parent, sources);
            }
        }
    }
}
