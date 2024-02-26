package entities.buttons;

import java.awt.event.ActionListener;

import java.util.concurrent.atomic.AtomicReference;

import javax.swing.AbstractButton;
import javax.swing.JToggleButton;

import entities.Action.CurrentAction;

public abstract class Button<T extends AbstractButton> {

    private final CurrentAction action;

    private T button;

    private final String name;

    protected Button(Class<T> buttonClass, String name, ActionListener listener, CurrentAction action) {
        this.name = name;

        try {
            this.button = buttonClass.getDeclaredConstructor(String.class).newInstance(name);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        if (this.button instanceof JToggleButton) {
            this.button.setSelected(true);
        }

        this.button.addActionListener(listener);
        this.action = action;
    }

    public T getButton() {
        return this.button;
    }

    public String getName() {
        return this.name;
    }

    public void setCurrentAction(AtomicReference<CurrentAction> currentActionRef) {
        currentActionRef.set(this.action);
    }

    public boolean hasAction() {
        return this.action != null;
    }
}
