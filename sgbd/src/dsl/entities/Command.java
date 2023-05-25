package dsl.entities;

import entities.cells.Cell;

public abstract sealed class Command permits Expression<? extends Cell>, Import, Declaration {

	private String command;
	
	public Command(String command) {
		this.command = command;
	}
	
	public String getCommand() {
		return command;
	}
	
}
