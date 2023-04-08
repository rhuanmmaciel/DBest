package entities.buttons;

import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JButton;

import controller.CreateAction.CurrentAction;

public abstract class Button {
	
	private CurrentAction action;
	private JButton jButton;
	private String name;

	public Button(String name, ActionListener listener, CurrentAction action) {
		
		this.name = name;
		jButton = new JButton(name);
		jButton.addActionListener(listener);
		this.action = action;
		
	}

	public JButton getButton() {
		return jButton;
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