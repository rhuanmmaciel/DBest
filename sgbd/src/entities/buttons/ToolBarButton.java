package entities.buttons;

import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import entities.Action.CurrentAction;

public class ToolBarButton<T extends AbstractButton> extends Button<T>{

	public ToolBarButton(Class<T> buttonClass, String name, ActionListener listener, JToolBar toolBar, CurrentAction action) {

		super(buttonClass, name, listener, action);
		toolBar.add(getButton());
		getButton().setHorizontalAlignment(SwingConstants.LEFT);

	}
	
}
