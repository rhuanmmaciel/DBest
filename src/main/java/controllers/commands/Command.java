package controllers.commands;

public interface Command {

    void execute();

    String getName();
}
