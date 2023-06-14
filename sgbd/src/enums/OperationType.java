package enums;

import entities.Action.CreateOperationAction;
import enums.interfaces.IOperationType;
import gui.frames.forms.operations.IFormFrameOperation;
import gui.frames.forms.operations.unary.FormFrameGroup;
import gui.frames.forms.operations.unary.FormFrameSort;
import operations.IOperator;
import gui.frames.forms.operations.binary.FormFrameJoins;
import gui.frames.forms.operations.unary.FormFrameProjection;
import gui.frames.forms.operations.unary.FormFrameSelection;
import operations.binary.CartesianProduct;
import operations.binary.Intersection;
import operations.binary.joins.Join;
import operations.binary.joins.LeftJoin;
import operations.binary.joins.RightJoin;
import operations.binary.Union;
import operations.unary.Group;
import operations.unary.Projection;
import operations.unary.Selection;
import operations.unary.Sort;

import java.util.List;

public enum OperationType implements IOperationType {

	SELECTION {

		@Override
		public String getDisplayName() {
			return "Seleção";
		}

		@Override
		public String getDisplayNameAndSymbol() {
			return getSymbol() + " " + getDisplayName();
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
			return "selection[args](source)";
		}

		@Override
		public Class<? extends IFormFrameOperation> getForm() {
			return FormFrameSelection.class;
		}

		@Override
		public Class<? extends IOperator> getOperator() {
			return Selection.class;
		}

		@Override
		public CreateOperationAction getAction() {
			return new CreateOperationAction(this);
		}
	},

	PROJECTION {
		
		@Override
		public String getDisplayName() {
			return "Projeção";
		}

		@Override
		public String getDisplayNameAndSymbol() {
			return getSymbol() + " " + getDisplayName();
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
			return "projection[args](source)";
		}

		@Override
		public Class<? extends IFormFrameOperation> getForm() {
			return FormFrameProjection.class;
		}

		@Override
		public Class<? extends IOperator> getOperator() {
			return Projection.class;
		}

		@Override
		public CreateOperationAction getAction() {
			return new CreateOperationAction(this);
		}
	},

	JOIN {
		
		@Override
		public String getDisplayName() {
			return "Junção";
		}

		@Override
		public String getDisplayNameAndSymbol() {
			return getSymbol() + " " + getDisplayName();
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
			return "join[args](source1,source2)";
		}

		@Override
		public Class<? extends IFormFrameOperation> getForm() {
			return FormFrameJoins.class;
		}

		@Override
		public Class<? extends IOperator> getOperator() {
			return Join.class;
		}

		@Override
		public CreateOperationAction getAction() {
			return new CreateOperationAction(this);
		}
	},

	LEFT_JOIN {
		
		@Override
		public String getDisplayName() {
			return "Junção à esquerda";
		}

		@Override
		public String getDisplayNameAndSymbol() {
			return getSymbol() + " " + getDisplayName();
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
			return "leftJoin[args](source1,source2)";
		}

		@Override
		public Class<? extends IFormFrameOperation> getForm() {
			return FormFrameJoins.class;
		}

		@Override
		public Class<? extends IOperator> getOperator() {
			return LeftJoin.class;
		}

		@Override
		public CreateOperationAction getAction() {
			return new CreateOperationAction(this);
		}
	},

	RIGHT_JOIN {
		
		@Override
		public String getDisplayName() {
			return "Junção à direita";
		}

		@Override
		public String getDisplayNameAndSymbol() {
			return getSymbol() + " " + getDisplayName();
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
			return "rightJoin[args](source1,source2)";
		}

		@Override
		public Class<? extends IFormFrameOperation> getForm() {
			return FormFrameJoins.class;
		}

		@Override
		public Class<? extends IOperator> getOperator() {
			return RightJoin.class;
		}

		@Override
		public CreateOperationAction getAction() {
			return new CreateOperationAction(this);
		}
	},

	CARTESIAN_PRODUCT {
		
		@Override
		public String getDisplayName() {
			return "Produto Cartesiano";
		}

		@Override
		public String getDisplayNameAndSymbol() {
			return getSymbol() + " " + getDisplayName();
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
		public Class<? extends IFormFrameOperation> getForm() {
			return null;
		}

		@Override
		public Class<? extends IOperator> getOperator() {
			return CartesianProduct.class;
		}

		@Override
		public CreateOperationAction getAction() {
			return new CreateOperationAction(this);
		}
	},

	UNION {
		@Override
		public String getDisplayName() {
			return "União";
		}

		@Override
		public String getDisplayNameAndSymbol() {
			return getSymbol() + " " + getDisplayName();
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
			return "union[args](source1,source2)";
		}

		@Override
		public Class<? extends IFormFrameOperation> getForm() {
			return null;
		}

		@Override
		public Class<? extends IOperator> getOperator() {
			return Union.class;
		}

		@Override
		public CreateOperationAction getAction() {
			return new CreateOperationAction(this);
		}
	},

	INTERSECTION {
		@Override
		public String getDisplayName() {
			return "Interseção";
		}

		@Override
		public String getDisplayNameAndSymbol() {
			return getSymbol() + " " + getDisplayName();
		}

		@Override
		public OperationArity getArity() {
			return OperationArity.BINARY;
		}

		@Override
		public String getSymbol() {
			return "∩";
		}

		@Override
		public String getOperationName() {
			return "intersection";
		}

		@Override
		public String getDslOperation() {
			return "intersection[args](source1,source2)";
		}

		@Override
		public Class<? extends IFormFrameOperation> getForm() {
			return null;
		}

		@Override
		public Class<? extends IOperator> getOperator() {
			return Intersection.class;
		}

		@Override
		public CreateOperationAction getAction() {
			return new CreateOperationAction(this);
		}
	},

	DIFFERENCE {
		@Override
		public String getDisplayName() {
			return "Diferença";
		}

		@Override
		public String getDisplayNameAndSymbol() {
			return getSymbol() + " " + getDisplayName();
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
		public Class<? extends IFormFrameOperation> getForm() {
			return null;
		}

		@Override
		public Class<? extends IOperator> getOperator() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CreateOperationAction getAction() {
			return new CreateOperationAction(this);
		}
	},

	RENAME {
		@Override
		public String getDisplayName() {
			return "Renomeação";
		}

		@Override
		public String getDisplayNameAndSymbol() {
			return getSymbol() + " " + getDisplayName();
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
		public Class<? extends IFormFrameOperation> getForm() {
			return null;
		}

		@Override
		public Class<? extends IOperator> getOperator() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CreateOperationAction getAction() {
			return new CreateOperationAction(this);
		}
	},

	GROUP {
		@Override
		public String getDisplayName() {
			return "Agrupamento";
		}

		@Override
		public String getDisplayNameAndSymbol() {
			return getSymbol() + " " + getDisplayName();
		}

		@Override
		public OperationArity getArity() {
			return OperationArity.UNARY;
		}

		@Override
		public String getSymbol() {
			return "G";
		}

		@Override
		public String getOperationName() {
			return "group";
		}

		@Override
		public String getDslOperation() {
			return "group[args](relation)";
		}

		@Override
		public Class<? extends IFormFrameOperation> getForm() {
			return FormFrameGroup.class;
		}

		@Override
		public Class<? extends IOperator> getOperator() {
			return Group.class;
		}

		@Override
		public CreateOperationAction getAction() {
			return new CreateOperationAction(this);
		}
	},

	SORT{
		@Override
		public String getDisplayName() {
			return "Ordenação";
		}

		@Override
		public String getSymbol() {
			return "↕";
		}

		@Override
		public String getDisplayNameAndSymbol() {
			return getSymbol() + " " + getDisplayName();
		}

		@Override
		public String getOperationName() {
			return "sort";
		}

		@Override
		public String getDslOperation() {
			return "sort[args](relation)";
		}

		@Override
		public OperationArity getArity() {
			return OperationArity.UNARY;
		}

		@Override
		public Class<? extends IFormFrameOperation> getForm() {
			return FormFrameSort.class;
		}

		@Override
		public Class<? extends IOperator> getOperator() {
			return Sort.class;
		}

		@Override
		public CreateOperationAction getAction() {
			return new CreateOperationAction(this);
		}
	};

	public final static List<OperationType> OPERATIONS_WITHOUT_FORM = List.of(CARTESIAN_PRODUCT, UNION, INTERSECTION, DIFFERENCE);

	public static OperationType fromString(String operationType) {

		for (OperationType operation : OperationType.values())
			if (operation.getOperationName().equalsIgnoreCase(operationType))
				return operation;

		throw new IllegalArgumentException("Invalid operation type: " + operationType);

	}
}
