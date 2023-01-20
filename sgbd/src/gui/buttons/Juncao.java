package gui.buttons;

import java.awt.Color;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxStylesheet;

public class Juncao {

	private JButton juncaoButton;
	private JPanel panelJuncao;
	
	public Juncao(mxStylesheet stylesheet) {
				
		juncaoButton = new JButton("|X| Juncao");
		juncaoButton.setBounds(600, 300, 100, 50);
		panelJuncao = new JPanel();
		panelJuncao.add(juncaoButton);
	    stylesheet.putCellStyle("juncao",createJuncaoStyle());
	    
	    
	}

	
	public JPanel getPanel() {
		return panelJuncao;
	}
	
	public JButton getButton() {
		return juncaoButton;
	}
	
	private Hashtable<String,Object> createJuncaoStyle() {
	    Hashtable<String, Object> style = new Hashtable<String, Object>();
	    style.put(mxConstants.STYLE_FILLCOLOR, mxUtils.getHexColorString(Color.WHITE));
	    style.put(mxConstants.STYLE_STROKEWIDTH, 1.5);
	    style.put(mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(new Color(0, 0, 170)));
	    style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR);
	    style.put(mxConstants.STYLE_PERIMETER, mxConstants.PERIMETER_ELLIPSE);
	    return style;
	 }
	
	
	
}
