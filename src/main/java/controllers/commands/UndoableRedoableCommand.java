package controllers.commands;

public interface UndoableRedoableCommand extends Command {

    void undo();

    void redo();
}
