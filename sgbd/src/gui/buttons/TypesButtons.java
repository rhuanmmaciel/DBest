package gui.buttons;

import java.awt.Color;
import java.util.Hashtable;


import javax.swing.JButton;
import javax.swing.JPanel;

import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxStylesheet;



public class TypesButtons{
	private JButton typeButton;
	private JPanel typePanel;
	
	public TypesButtons(mxStylesheet stylesheet,String name,String style) {
				
		typeButton = new JButton(name);
		typeButton.setBounds(600, 300, 100, 50);
		typePanel = new JPanel();
		typePanel.add(typeButton);
	    stylesheet.putCellStyle(style,createStyle());
	    
	    
	}

	
	public JPanel getPanel() {
		return typePanel;
	}
	
	public JButton getButton() {
		return typeButton;
	}
	
	private Hashtable<String,Object> createStyle() {
	    Hashtable<String, Object> style = new Hashtable<String, Object>();
	    style.put(mxConstants.STYLE_FILLCOLOR, mxUtils.getHexColorString(Color.WHITE));
	    style.put(mxConstants.STYLE_STROKEWIDTH, 1.5);
	    style.put(mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(new Color(0, 0, 170)));
	    style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR);
	    style.put(mxConstants.STYLE_PERIMETER, mxConstants.PERIMETER_ELLIPSE);
	    return style;
	 }
	
	
	

}