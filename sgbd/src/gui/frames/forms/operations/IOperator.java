package gui.frames.forms.operations;

import java.util.List;

import com.mxgraph.model.mxCell;

public interface IOperator {

		void executeOperation(mxCell jCell, List<String> data);
	
}
