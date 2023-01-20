package gui.buttons;

import java.awt.Color;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxStylesheet;

public class Selecao{
	private JButton selecaoButton;
	private JPanel panelSelecao;
	
	public Selecao(mxStylesheet stylesheet) {
		
		selecaoButton = new JButton("σ Selecao");
		selecaoButton.setBounds(600, 300, 100, 50);
		panelSelecao = new JPanel();
		panelSelecao.add(selecaoButton);
	    stylesheet.putCellStyle("selecao",createSelecaoStyle());
	}

	public JPanel getPanel() {
		return panelSelecao;
	}
	
	public JButton getButton() {
		return selecaoButton;
	}
	
	private Hashtable<String,Object> createSelecaoStyle() {
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
	
	
	
	/*
	Operator where = new FilterOperator(selectSomeUsers,(Tuple t)->{
        return t.getContent("users").getInt("idade") >=18;
    });
	*/
	/*
	public Operator OperacaoSelecao(Object cellObj,Operator op,String input) {
		mxCell cell = (mxCell) cellObj;
		String value = cell.getValue().toString();
		input = input.trim();
		//ver como seria a criacao de tabela
		//como passaria o operador
		//como a gente vai pegar essas informacoes para passar
		//tipo neste caso eh um inteiro, mais que 18 ok, mas se quero algo como no exemplo do pdf, tipo "duracao= 3 ^ custo> 12000" 
		//to pegando oq a pessoa escreve e passando para um string para manipular desta forma, mas teria que ver como identificar esses campos para passar para o operador
		//como quer mostrar esta resposta
		
		int index_equal = input.indexOf("=");
		if(input.contains(",")) {
			
		}
		String column = input.substring(0,index_equal);
		
		int index_
		String value = input.substring(index_equal)
		
		
		
		Operator where = new FilterOperator(op,(Tuple t)->{
	        return t.getContent(tabela).getInt(value) >=18;
	    });
		
	}
	*/
}
