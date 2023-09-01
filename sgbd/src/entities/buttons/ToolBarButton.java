package entities.buttons;

import java.awt.*;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import entities.Action.CurrentAction;

public class ToolBarButton<T extends AbstractButton> extends Button<T> {

    public ToolBarButton(Class<T> buttonClass, String name, ActionListener listener, JToolBar toolBar, CurrentAction action) {
        super(buttonClass, name, listener, action);

        T button = this.getButton();

        button.setMargin(new Insets(2, 5, 2, 5));

        toolBar.add(button);

        this.getButton().setHorizontalAlignment(SwingConstants.LEFT);
    }
}
