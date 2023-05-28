package enums;

import enums.interfaces.IOperationType;
import gui.frames.forms.operations.FormFrameOperation;
import gui.frames.forms.operations.IOperator;
import gui.frames.forms.operations.binary.FormFrameJoin;
import gui.frames.forms.operations.binary.FormFrameLeftJoin;
import gui.frames.forms.operations.binary.FormFrameRightJoin;
import gui.frames.forms.operations.binary.FormFrameUnion;
import gui.frames.forms.operations.unary.FormFrameProjection;
import gui.frames.forms.operations.unary.FormFrameSelection;
import operations.binary.CartesianProduct;
import operations.binary.Join;
import operations.binary.LeftJoin;
import operations.binary.RightJoin;
import operations.binary.Union;
import operations.unary.Projection;
import operations.unary.Selection;

public enum OperationType implements IOperationType {

	SELECTION {

		@Override
		public String getDisplayName() {
			return "Seleção";
		}

		@Override
		public String getDisplayNameAndSymbol() {
			return "σ Seleção";
		}

		@Override
		public OperationArity getArity() {
			return OperationArity.UNARY;
		}

		@Override
		public String getSymbol() {
			return "σ";
		}

		@Override
		public String getOperationName() {
			return "selection";
		}

		@Override
		public String getDslOperation() {
			return "selection[predicate](source)";
		}

		@Override
		public Class<? extends FormFrameOperation> getForm() {
			return FormFrameSelection.class;
		}

		@Override
		public Class<? extends IOperator> getOperator() {
			return Selection.class;
		}
	},

	PROJECTION {
		@Override
		public String getDisplayName() {
			return "Projeção";
		}

		@Override
		public String getDisplayNameAndSymbol() {
			return "π Projeção";
		}

		@Override
		public String getSymbol() {
			return "π";
		}

		@Override
		public OperationArity getArity() {
			return OperationArity.UNARY;
		}

		@Override
		public String getOperationName() {
			return "projection";
		}

		@Override
		public String getDslOperation() {
			return "projection[predicate](source)";
		}

		@Override
		public Class<? extends FormFrameOperation> getForm() {
			return FormFrameProjection.class;
		}

		@Override
		public Class<? extends IOperator> getOperator() {
			return Projection.class;
		}
	},

	JOIN {
		@Override
		public String getDisplayName() {
			return "Junção";
		}

		@Override
		public String getDisplayNameAndSymbol() {
			return "|X| Junção";
		}

		@Override
		public String getSymbol() {
			return "|X|";
		}

		@Override
		public OperationArity getArity() {
			return OperationArity.BINARY;
		}

		@Override
		public String getOperationName() {
			return "join";
		}

		@Override
		public String getDslOperation() {
			return "join[predicate](source1,source2)";
		}

		@Override
		public Class<? extends FormFrameOperation> getForm() {
			return FormFrameJoin.class;
		}

		@Override
		public Class<? extends IOperator> getOperator() {
			return Join.class;
		}
	},

	LEFT_JOIN {
		@Override
		public String getDisplayName() {
			return "Junção à esquerda";
		}

		@Override
		public String getDisplayNameAndSymbol() {
			return "⟕ Junção à esquerda";
		}

		@Override
		public OperationArity getArity() {
			return OperationArity.BINARY;
		}

		@Override
		public String getSymbol() {
			return "⟕";
		}

		@Override
		public String getOperationName() {
			return "leftJoin";
		}

		@Override
		public String getDslOperation() {
			return "leftJoin[predicate](source1,source2)";
		}

		@Override
		public Class<? extends FormFrameOperation> getForm() {
			return FormFrameLeftJoin.class;
		}

		@Override
		public Class<? extends IOperator> getOperator() {
			return LeftJoin.class;
		}
	},

	RIGHT_JOIN {
		@Override
		public String getDisplayName() {
			return "Junção à direita";
		}

		@Override
		public String getDisplayNameAndSymbol() {
			return "⟖ Junção à direita";
		}

		@Override
		public OperationArity getArity() {
			return OperationArity.BINARY;
		}

		@Override
		public String getSymbol() {
			return "⟖";
		}

		@Override
		public String getOperationName() {
			return "rightJoin";
		}

		@Override
		public String getDslOperation() {
			return "rightJoin[predicate](source1,source2)";
		}

		@Override
		public Class<? extends FormFrameOperation> getForm() {
			return FormFrameRightJoin.class;
		}

		@Override
		public Class<? extends IOperator> getOperator() {
			return RightJoin.class;
		}
	},

	CARTESIAN_PRODUCT {
		@Override
		public String getDisplayName() {
			return "Produto Cartesiano";
		}

		@Override
		public String getDisplayNameAndSymbol() {
			return "✕ Produto Cartesiano";
		}

		@Override
		public OperationArity getArity() {
			return OperationArity.BINARY;
		}

		@Override
		public String getSymbol() {
			return "✕";
		}

		@Override
		public String getOperationName() {
			return "cartesianProduct";
		}

		@Override
		public String getDslOperation() {
			return "cartesianProduct(source1,source2)";
		}

		@Override
		public Class<? extends FormFrameOperation> getForm() {
			return null;
		}

		@Override
		public Class<? extends IOperator> getOperator() {
			return CartesianProduct.class;
		}
	},

	UNION {
		@Override
		public String getDisplayName() {
			return "União";
		}

		@Override
		public String getDisplayNameAndSymbol() {
			return "∪ União";
		}

		@Override
		public OperationArity getArity() {
			return OperationArity.BINARY;
		}

		@Override
		public String getSymbol() {
			return "∪";
		}

		@Override
		public String getOperationName() {
			return "union";
		}

		@Override
		public String getDslOperation() {
			return "union[predicate](source1,source2)";
		}

		@Override
		public Class<? extends FormFrameOperation> getForm() {
			return FormFrameUnion.class;
		}

		@Override
		public Class<? extends IOperator> getOperator() {
			return Union.class;
		}
	},

	DIFFERENCE {
		@Override
		public String getDisplayName() {
			return "Diferença";
		}

		@Override
		public String getDisplayNameAndSymbol() {
			return "- Diferença";
		}

		@Override
		public OperationArity getArity() {
			return OperationArity.BINARY;
		}

		@Override
		public String getSymbol() {
			return "-";
		}

		@Override
		public String getOperationName() {
			return "difference";
		}

		@Override
		public String getDslOperation() {
			return null;
		}

		@Override
		public Class<? extends FormFrameOperation> getForm() {
			return null;
		}

		@Override
		public Class<? extends IOperator> getOperator() {
			// TODO Auto-generated method stub
			return null;
		}
	},

	RENAME {
		@Override
		public String getDisplayName() {
			return "Renomeação";
		}

		@Override
		public String getDisplayNameAndSymbol() {
			return "ρ Renomeação";
		}

		@Override
		public OperationArity getArity() {
			return OperationArity.UNARY;
		}

		@Override
		public String getSymbol() {
			return "ρ";
		}

		@Override
		public String getOperationName() {
			return "rename";
		}

		@Override
		public String getDslOperation() {
			return null;
		}

		@Override
		public Class<? extends FormFrameOperation> getForm() {
			return null;
		}

		@Override
		public Class<? extends IOperator> getOperator() {
			// TODO Auto-generated method stub
			return null;
		}
	},

	AGGREGATION {
		@Override
		public String getDisplayName() {
			return "Agregação";
		}

		@Override
		public String getDisplayNameAndSymbol() {
			return "Σ Agregação";
		}

		@Override
		public OperationArity getArity() {
			return OperationArity.UNARY;
		}

		@Override
		public String getSymbol() {
			return "Σ";
		}

		@Override
		public String getOperationName() {
			return "aggregation";
		}

		@Override
		public String getDslOperation() {
			return null;
		}

		@Override
		public Class<? extends FormFrameOperation> getForm() {
			return null;
		}

		@Override
		public Class<? extends IOperator> getOperator() {
			// TODO Auto-generated method stub
			return null;
		}
	};

	public static OperationType fromString(String operationType) {
		
		for (OperationType operation : OperationType.values()) 
			if (operation.getOperationName().equalsIgnoreCase(operationType)) 
				return operation;
			
		throw new IllegalArgumentException("Invalid operation type: " + operationType);
		
	}
}
