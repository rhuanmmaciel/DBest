package entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Tree {

	private int index;
	private List<Cell> roots;
	
	public Tree(int index, Set<Cell> roots) {
		
		this.index = index;

		this.roots = new ArrayList<>();
		
		if(roots != null && !roots.isEmpty()) this.roots.addAll(roots);
		
	}

	public List<Cell> getRoots() {
		return roots;
	}

	public void addRoots(List<TableCell> roots) {
		this.roots.addAll(roots);
	}
	
	public void addRoot(Cell root) {
		this.roots.add(root);
	}

	public int getIndex() {
		return index;
	}
	
	@Override
	public String toString() {
		
		StringBuilder text = new StringBuilder();
		
		text.append(index+": ");
		
		for(Cell cell : roots) {
			text.append(cell.getName() + ", ");
		}
		
		return text.toString();
		
	}
	
}
