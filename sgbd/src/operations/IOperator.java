package operations;

import com.mxgraph.model.mxCell;

import java.util.List;

public interface IOperator {

		void executeOperation(mxCell jCell, List<String> arguments);
	
}
