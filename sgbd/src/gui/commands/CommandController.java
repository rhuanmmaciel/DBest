package gui.commands;

import java.util.*;

public class CommandController {

    private final List<Command> history;

    private final Deque<UndoableRedoableCommand> undos;

    private final Deque<UndoableRedoableCommand> redos;

    public CommandController() {
        this.history = new ArrayList<>();
        this.undos = new ArrayDeque<>();
        this.redos = new ArrayDeque<>();
    }

    public void execute(Command command) {
        if (command == null) return;

        command.execute();

        this.history.add(command);

        this.undos.clear();

        this.redos.clear();
    }

    public void execute(UndoableRedoableCommand command) {
        if (command == null) return;

        command.execute();

        this.history.add(command);

        this.redos.clear();

        this.undos.push(command);

        System.out.printf("EXECUTE: %s%n", command);
    }

    public void undo() {
        if (this.undos.isEmpty()) return;

        UndoableRedoableCommand command = this.undos.pop();

        command.undo();

        this.redos.push(command);

        this.history.add(command);

        System.out.printf("   UNDO: %s%n", command);
    }

    public void redo() {
        if (this.redos.isEmpty()) return;

        UndoableRedoableCommand command = this.redos.pop();

        command.redo();

        this.undos.push(command);

        this.history.add(command);

        System.out.printf("   REDO: %s%n", command);
    }

    public boolean canUndo() {
        return !this.undos.isEmpty();
    }

    public boolean canRedo() {
        return !this.redos.isEmpty();
    }

    public List<Command> getHistory() {
        return Collections.unmodifiableList(this.history);
    }
}
