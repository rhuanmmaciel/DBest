package entities.buttons;

import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.AbstractButton;
import javax.swing.JToggleButton;

import entities.Action.CurrentAction;

public abstract class Button<T extends AbstractButton> {

	private CurrentAction action;
	private T button;
	private String name;

	public Button(Class<T> buttonClass, String name, ActionListener listener, CurrentAction action) {

		this.name = name;
		try {
			
			button = buttonClass.getDeclaredConstructor(String.class).newInstance(name);
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
		}

		if (button instanceof JToggleButton) {
			((JToggleButton) button).setSelected(true);
		}
		button.addActionListener(listener);
		this.action = action;

	}

	public T getButton() {
		return button;
	}

	public String getName() {
		return name;
	}

	public void setCurrentAction(AtomicReference<CurrentAction> currentActionRef) {
		currentActionRef.set(action);
	}

	public boolean hasAction() {
		return action != null;
	}

}