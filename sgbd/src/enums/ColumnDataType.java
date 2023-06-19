package enums;

import enums.interfaces.IColumnDataType;

public enum ColumnDataType implements IColumnDataType{
	
	INTEGER{

		@Override
		public String getTypeName() {
			return "integer";
		}
	
	},
	LONG{

		@Override
		public String getTypeName() {
			return "long";
		}

	},
	FLOAT{

		@Override
		public String getTypeName() {
			return "float";
		}
		
	},
	DOUBLE{

		@Override
		public String getTypeName() {
			return "double";
		}

	},
	CHARACTER{

		@Override
		public String getTypeName() {
			return "char";
		}
		
	},
	STRING{

		@Override
		public String getTypeName() {
			return "string";
		}
		
	},
	BOOLEAN{

		@Override
		public String getTypeName() {
			return "bool";
		}
		
	},
	NONE{

		@Override
		public String getTypeName() {
			return "none";
		}
		
	},
	UNDEFINED{

		@Override
		public String getTypeName() {
			return "undefined";
		}

	};

	
}