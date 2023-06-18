package entities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import controller.MainController;
import entities.cells.Cell;

public class Tree {

	private int index = 0;

	{

		while (MainController.getTrees().containsKey(index))
			index++;

		MainController.getTrees().put(index, this);

	}

	public Tree() {

	}
	
	public List<Cell> getLeaves() {

		List<Cell> leaves = new ArrayList<>();

		getCells().stream().filter(cell -> !cell.hasParents()).forEach(leaves::add);

		return leaves;
	}

	public Cell getRoot() {

		return getCells().stream().filter(cell -> !cell.hasChild()).findAny().orElseThrow();

	}

	public Set<Cell> getCells() {

		Set<Cell> cells = new HashSet<>();

		Cell.getCells()
				.values()
				.stream()
				.filter(cell -> cell.getTree() == this)
				.forEach(cells::add);

		return cells;

	}

	public int getIndex() {
		return index;
	}

	@Override
	public String toString() {

		StringBuilder text = new StringBuilder();

		text.append(index).append(": ");

		for (Cell cell : getCells())
			text.append(cell.getName()).append(", ");

		return text.toString();

	}

}
