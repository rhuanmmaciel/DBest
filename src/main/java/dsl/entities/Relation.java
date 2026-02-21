package dsl.entities;

import dsl.utils.DslUtils;
import entities.cells.TableCell;
import java.util.ArrayList;
import java.util.List;

public final class Relation extends Expression<TableCell> {

    private final String name;
    private final String firstName;
    private TableCell cell = null;
    private final List<String> arguments = new ArrayList<>();

    public Relation(String command) {

        super(command);
        
        // Find out if this relation has arguments
        int index = command.indexOf("]");
        
        if (index!=-1){
            setArguments(List.of(command.substring(command.indexOf("[") + 1, command.indexOf("]")).split(",")));
            command = command.substring(index + 1);
        }
        
        this.name = DslUtils.getRealName(command);
        this.firstName = DslUtils.clearTableName(command);
        setCoordinates(command);

    }
    
    protected void setArguments(List<String> arguments) {
        // Filter out empty strings after stripping and add them to the list
        this.arguments.addAll(
                arguments.stream()
                        .map(String::strip) // Strip leading/trailing whitespace
                        .filter(s -> !s.isEmpty()) // Filter out empty strings
                        .toList() // Convert back to a list
        );
        //this.arguments.addAll(arguments.stream().map(String::strip).toList());

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

        if (this.cell == null) {
            this.cell = cell;
        }

    }

    public String getFirstName() {
        return firstName;
    }
}
