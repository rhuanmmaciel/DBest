package entities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import controller.MainController;
import entities.cells.Cell;

public class Tree {

	private int index;
	
	{
		
		index = 0;
		
		while(MainController.getTrees().containsKey(index)) {
			index++;
		}
			
		MainController.getTrees().put(index, this);
		
	}
	
	public Tree() {
				
	}
	
	public List<Cell> getRoots() {
		
		List<Cell> roots = new ArrayList<>();
		
		for(Cell cell : getCells()) if(!cell.hasParents()) roots.add(cell);
		
		return roots;
	}
	
	public Set<Cell> getCells(){
		
		Set<Cell> cells = new HashSet<>();
		
		for(Cell cell : MainController.getCells().values()) {
			
			if(cell.getTree() == this) cells.add(cell);
			
		}
		
		return cells;
		
	}

	public int getIndex() {
		return index;
	}
	
	@Override
	public String toString() {
		
		StringBuilder text = new StringBuilder();
		
		text.append(index+": ");
		
		for(Cell cell : getCells()) {
			text.append(cell.getName() + ", ");
		}
		
		return text.toString();
		
	}
	
}
