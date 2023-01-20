package gui.buttons;

import java.awt.Color;
import java.util.Hashtable;


import javax.swing.JButton;
import javax.swing.JPanel;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxStylesheet;

public class Renomeacao{
	private JButton renomeacaoButton;
	private JPanel panelRenomeacao;
	
	public Renomeacao(mxStylesheet stylesheet) {
				
		renomeacaoButton = new JButton("ρ Renomeacao");
		renomeacaoButton.setBounds(600, 300, 100, 50);
		panelRenomeacao = new JPanel();
		panelRenomeacao.add(renomeacaoButton);
	    stylesheet.putCellStyle("renomeacao",createRenomeacaoStyle());
	    
	    
	}

	
	public JPanel getPanel() {
		return panelRenomeacao;
	}
	
	public JButton getButton() {
		return renomeacaoButton;
	}
	
	private Hashtable<String,Object> createRenomeacaoStyle() {
		Hashtable<String, Object> style = new Hashtable<String, Object>();
	    style.put(mxConstants.STYLE_FILLCOLOR, mxUtils.getHexColorString(Color.WHITE));
	    style.put(mxConstants.STYLE_STROKEWIDTH, 1.5);
	    style.put(mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(new Color(0, 0, 170)));
	    style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR);
	    style.put(mxConstants.STYLE_PERIMETER, mxConstants.PERIMETER_ELLIPSE);
	    return style;
	 }
	
	//unaria
	public Boolean checkRules(Object cellObj) {
		mxCell cell = (mxCell) cellObj;
		if(cell.getParent() != null) {
			return false;
		}
		if(cell.getChildCount() != 0) {
			return false;
		}
		
		return true;
	}

}