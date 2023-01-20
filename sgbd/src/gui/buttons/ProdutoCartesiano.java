package gui.buttons;

import java.awt.Color;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxStylesheet;


public class ProdutoCartesiano{
	private JButton produtoCartesianoButton;
	private JPanel panelProdutoCartesiano;

	
	public ProdutoCartesiano(mxStylesheet stylesheet) {
		
		produtoCartesianoButton = new JButton("✕ Produto Cartesiano");
		produtoCartesianoButton.setBounds(600, 300, 100, 50);
		panelProdutoCartesiano = new JPanel();
		panelProdutoCartesiano.add(produtoCartesianoButton);
	    stylesheet.putCellStyle("produtoCartesiano",createProdutoCartesianoStyle());
	}
	
	public JPanel getPanel() {
		return panelProdutoCartesiano;
	}
	
	public JButton getButton() {
		return produtoCartesianoButton;
	}
	
	private Hashtable<String,Object> createProdutoCartesianoStyle() {
		Hashtable<String, Object> style = new Hashtable<String, Object>();
	    style.put(mxConstants.STYLE_FILLCOLOR, mxUtils.getHexColorString(Color.WHITE));
	    style.put(mxConstants.STYLE_STROKEWIDTH, 1.5);
	    style.put(mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(new Color(0, 0, 170)));
	    style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR);
	    style.put(mxConstants.STYLE_PERIMETER, mxConstants.PERIMETER_ELLIPSE);
	    return style;
	 }
	
	//binaria
	public Boolean checkRules(Object cellObj) {
		mxCell cell = (mxCell) cellObj;
		if(cell.getChildCount() >= 2) {
			return false;
		}
		if(cell.getParent() != null) {
			return false;
		}
		
		return true;
	}
	
	
}