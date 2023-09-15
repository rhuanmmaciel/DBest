package gui.commands;

public abstract class BaseCommand implements Command {

    protected final Class<?> clazz;

    protected final CommandController commandController;

    protected BaseCommand() {
        this.clazz = this.getClass();
        this.commandController = new CommandController();
    }

    @Override
    public String getName() {
        return this.clazz.getSimpleName();
    }

    @Override
    public String toString() {
        return String.format("%s@%s", this.getName(), Integer.toHexString(System.identityHashCode(this)));
    }
}
