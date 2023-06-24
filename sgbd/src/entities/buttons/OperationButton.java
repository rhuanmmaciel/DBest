package entities.buttons;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxStylesheet;

import enums.OperationType;

public class OperationButton extends Button<JButton>{

	private final String style;
	
	public OperationButton(mxStylesheet stylesheet, OperationType type, ActionListener listener, JPanel panel) {

		super(JButton.class, type.getDisplayNameAndSymbol(), listener, type.getAction());
		this.style = type.DISPLAY_NAME;
		getButton().setBounds(600, 300, 100, 50);
		JPanel typePanel = new JPanel();
		typePanel.add(getButton());
		stylesheet.putCellStyle(style, createStyle());
		panel.add(typePanel);

	}
	
	public String getStyle() {
		return style;
	}
	
	private Hashtable<String, Object> createStyle() {
		Hashtable<String, Object> style = new Hashtable<>();
		style.put(mxConstants.STYLE_FILLCOLOR, mxUtils.getHexColorString(Color.WHITE));
		style.put(mxConstants.STYLE_STROKEWIDTH, 1.5);
		style.put(mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(new Color(0, 0, 170)));
		style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR);
		style.put(mxConstants.STYLE_PERIMETER, mxConstants.PERIMETER_ELLIPSE);
		return style;
	}
	
}
