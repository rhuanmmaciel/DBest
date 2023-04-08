package entities.buttons;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.JPanel;

import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxStylesheet;

import controller.CreateAction.CurrentAction;

public class OperationButton extends Button{

	private JPanel typePanel;
	
	public OperationButton(mxStylesheet stylesheet, String name, String style, ActionListener listener, JPanel panel,
			 CurrentAction action) {

		super(name, listener, action);
		getButton().setBounds(600, 300, 100, 50);
		typePanel = new JPanel();
		typePanel.add(getButton());
		stylesheet.putCellStyle(style, createStyle());
		panel.add(typePanel);

	}
	
	public JPanel getPanel() {
		return typePanel;
	}
	
	private Hashtable<String, Object> createStyle() {
		Hashtable<String, Object> style = new Hashtable<String, Object>();
		style.put(mxConstants.STYLE_FILLCOLOR, mxUtils.getHexColorString(Color.WHITE));
		style.put(mxConstants.STYLE_STROKEWIDTH, 1.5);
		style.put(mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(new Color(0, 0, 170)));
		style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR);
		style.put(mxConstants.STYLE_PERIMETER, mxConstants.PERIMETER_ELLIPSE);
		return style;
	}
	
}
