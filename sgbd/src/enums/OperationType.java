package enums;

public enum OperationType implements IOperationType {

	SELECTION {
		@Override
		public String getDisplayName() {
			return"Seleção";
		}

		@Override
		public String getDisplayNameAndSymbol() {
			return"σ Seleção";
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
	},

	PROJECTION {
		@Override
		public String getDisplayName() {
			return"Projeção";
		}

		@Override
		public String getDisplayNameAndSymbol() {
			return"π Projeção";
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
	},

	JOIN {
		@Override
		public String getDisplayName() {
			return"Junção";
		}

		@Override
		public String getDisplayNameAndSymbol() {
			return"|X| Junção";
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
	},

	LEFT_JOIN {
		@Override
		public String getDisplayName() {
			return"Junção à esquerda";
		}

		@Override
		public String getDisplayNameAndSymbol() {
			return"⟕ Junção à esquerda";
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
	},

	RIGHT_JOIN {
		@Override
		public String getDisplayName() {
			return"Junção à direita";
		}

		@Override
		public String getDisplayNameAndSymbol() {
			return"⟖ Junção à direita";
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
	},

	CARTESIAN_PRODUCT {
		@Override
		public String getDisplayName() {
			return"Produto Cartesiano";
		}

		@Override
		public String getDisplayNameAndSymbol() {
			return"✕ Produto Cartesiano";
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
	},

	UNION {
		@Override
		public String getDisplayName() {
			return"União";
		}

		@Override
		public String getDisplayNameAndSymbol() {
			return"∪ União";
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
	},

	DIFFERENCE {
		@Override
		public String getDisplayName() {
			return"Diferença";
		}

		@Override
		public String getDisplayNameAndSymbol() {
			return"- Diferença";
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
	},

	RENAME {
		@Override
		public String getDisplayName() {
			return"Renomeação";
		}

		@Override
		public String getDisplayNameAndSymbol() {
			return"ρ Renomeação";
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
	},

	AGGREGATION {
		@Override
		public String getDisplayName() {
			return"Agregação";
		}

		@Override
		public String getDisplayNameAndSymbol() {
			return"Σ Agregação";
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
	}
}
