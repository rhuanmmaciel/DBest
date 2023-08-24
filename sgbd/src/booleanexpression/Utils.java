package booleanexpression;

import enums.ColumnDataType;
import lib.booleanexpression.entities.elements.Element;
import lib.booleanexpression.entities.elements.Value;
import lib.booleanexpression.entities.elements.Variable;
import sgbd.prototype.query.fields.*;

public class Utils {

    public static Element getElement(String txt){

        if(txt.contains("'")) return getValue(txt, ColumnDataType.STRING);

        try {

            return getValueAsNumber(txt);

        }catch (UnsupportedOperationException e){

            return getVariable(txt);

        }

    }

    public static Element getValue(String value, ColumnDataType type){

        return switch (type){

            case INTEGER, LONG, FLOAT, DOUBLE -> getValueAsNumber(value);
            case CHARACTER, STRING, BOOLEAN, NONE -> getValueAsString(value);

        };

    }

    public static Element getVariable(String txt){

        return new Variable(txt);

    }

    public static Element getValueAsString(String txt){

        return new Value(new StringField(txt.substring(txt.indexOf("'")+1, txt.lastIndexOf("'"))));

    }

    public static Element getValueAsNumber(String txt){

        try {

            return new Value(new IntegerField(Integer.parseInt(txt.strip())));

        }catch (NumberFormatException ignored){

        }

        try {

            return new Value(new LongField(Long.parseLong(txt.strip())));

        }catch (NumberFormatException ignored){

        }

        try {

            return new Value(new FloatField(Float.parseFloat(txt.strip())));

        }catch (NumberFormatException ignored){

        }

        try {

            return new Value(new DoubleField(Double.parseDouble(txt.strip())));

        }catch (NumberFormatException ignored){

        }

        throw new UnsupportedOperationException("This value is not a number");

    }

}
