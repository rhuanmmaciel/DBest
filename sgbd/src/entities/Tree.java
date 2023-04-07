package entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Tree {

	private int index;
	private List<TableCell> roots;
	
	public Tree(int index, Set<TableCell> roots) {
		
		this.index = index;

		this.roots = new ArrayList<>();
		
		if(roots != null && !roots.isEmpty()) this.roots.addAll(roots);
		
	}

	public List<TableCell> getRoots() {
		return roots;
	}

	public void addRoots(List<TableCell> roots) {
		this.roots.addAll(roots);
	}
	
	public void addRoot(TableCell root) {
		this.roots.add(root);
	}

	public int getIndex() {
		return index;
	}
	
	@Override
	public String toString() {
		
		StringBuilder text = new StringBuilder();
		
		text.append(index+": ");
		
		for(TableCell cell : roots) {
			text.append(cell.getName() + ", ");
		}
		
		return text.toString();
		
	}
	
}
