package gui.frames.forms.operations;

import com.mxgraph.model.mxCell;

public class ReferenceBooleanExpressionForm extends BooleanExpressionForm  {


    public ReferenceBooleanExpressionForm(mxCell jCell) {
        super(jCell);
    }
    
    @Override
    protected boolean acceptFilters(){
        return false;
    }
    
    @Override
    protected boolean acceptReferenceFilters(){
        return false;
    }
    

}
