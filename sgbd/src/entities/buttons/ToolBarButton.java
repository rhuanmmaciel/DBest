package entities.buttons;

import java.awt.event.ActionListener;

import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import controller.CreateAction.CurrentAction;

public class ToolBarButton extends Button{

	public ToolBarButton(String name, ActionListener listener, JToolBar toolBar, CurrentAction action) {

		super(name, listener, action);
		toolBar.add(getButton());
		getButton().setHorizontalAlignment(SwingConstants.LEFT);

	}
	
}
