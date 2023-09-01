package commands;

public interface UndoableRedoableCommand extends Command {

    void undo();

    void redo();
}
