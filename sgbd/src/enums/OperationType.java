package enums;

public enum OperationType implements IOperationType {

	SELECTION {
		@Override
		public String getName() {
			return"Seleção";
		}

		@Override
		public String getSymbol() {
			return"σ Seleção";
		}
	},

	PROJECTION {
		@Override
		public String getName() {
			return"Projeção";
		}

		@Override
		public String getSymbol() {
			return"π Projeção";
		}
	},

	JOIN {
		@Override
		public String getName() {
			return"Junção";
		}

		@Override
		public String getSymbol() {
			return"|X| Junção";
		}
	},

	LEFT_JOIN {
		@Override
		public String getName() {
			return"Junção à esquerda";
		}

		@Override
		public String getSymbol() {
			return"⟕ Junção à esquerda";
		}
	},

	RIGHT_JOIN {
		@Override
		public String getName() {
			return"Junção à direita";
		}

		@Override
		public String getSymbol() {
			return"⟖ Junção à direita";
		}
	},

	CARTESIAN_PRODUCT {
		@Override
		public String getName() {
			return"Produto Cartesiano";
		}

		@Override
		public String getSymbol() {
			return"✕ Produto Cartesiano";
		}
	},

	UNION {
		@Override
		public String getName() {
			return"União";
		}

		@Override
		public String getSymbol() {
			return"∪ União";
		}
	},

	DIFFERENCE {
		@Override
		public String getName() {
			return"Diferença";
		}

		@Override
		public String getSymbol() {
			return"- Diferença";
		}
	},

	RENAME {
		@Override
		public String getName() {
			return"Renomeação";
		}

		@Override
		public String getSymbol() {
			return"ρ Renomeação";
		}
	},

	AGGREGATION {
		@Override
		public String getName() {
			return"Agregação";
		}

		@Override
		public String getSymbol() {
			return"Σ Agregação";
		}
	}
}
