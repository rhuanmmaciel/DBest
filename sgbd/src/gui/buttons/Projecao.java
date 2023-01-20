package gui.buttons;

import java.awt.Color;
import java.util.Hashtable;


import javax.swing.JButton;
import javax.swing.JPanel;

import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxStylesheet;

public class Projecao{
	private JButton projecaoButton;
	private JPanel panelProjecao;
	
	public Projecao(mxStylesheet stylesheet) {
				
		projecaoButton = new JButton("π Projecao");
		projecaoButton.setBounds(600, 300, 100, 50);
		panelProjecao = new JPanel();
		panelProjecao.add(projecaoButton);
	    stylesheet.putCellStyle("projecao",createProjecaoStyle());
	    
	    
	}

	
	public JPanel getPanel() {
		return panelProjecao;
	}
	
	public JButton getButton() {
		return projecaoButton;
	}
	
	private Hashtable<String,Object> createProjecaoStyle() {
	    Hashtable<String, Object> style = new Hashtable<String, Object>();
	    style.put(mxConstants.STYLE_FILLCOLOR, mxUtils.getHexColorString(Color.WHITE));
	    style.put(mxConstants.STYLE_STROKEWIDTH, 1.5);
	    style.put(mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(new Color(0, 0, 170)));
	    style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR);
	    style.put(mxConstants.STYLE_PERIMETER, mxConstants.PERIMETER_ELLIPSE);
	    return style;
	 }
	

}