package dsl.entities;

import dsl.utils.DslUtils;
import entities.cells.TableCell;

public final class Relation extends Expression<TableCell> {

	private final String name;
    private final String firstName;
	private TableCell cell = null;
	
	public Relation(String command) {

		super(command);

        this.name = DslUtils.getRealName(command);
		this.firstName = DslUtils.clearTableName(command);
		setCoordinates(command);
		
	}

	public String getName() {
		return name;
	}

	@Override
	public TableCell getCell() {
		return cell;
	}

	@Override
	public void setCell(TableCell cell) {

		if(this.cell == null) this.cell = cell;
		
	}

    public String getFirstName() {
        return firstName;
    }
}
