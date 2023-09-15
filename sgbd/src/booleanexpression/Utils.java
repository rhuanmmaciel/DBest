package booleanexpression;

import enums.ColumnDataType;

import lib.booleanexpression.entities.elements.Element;
import lib.booleanexpression.entities.elements.Value;
import lib.booleanexpression.entities.elements.Variable;

import sgbd.prototype.query.fields.*;

public class Utils {

    private Utils() {

    }

    public static Element getElement(String text) {
        if (text.contains("'")) return getValue(text, ColumnDataType.STRING);

        try {
            return getValueAsNumber(text);
        } catch (UnsupportedOperationException exception) {
            return getVariable(text);
        }
    }

    public static Element getValue(String value, ColumnDataType type) {
        return switch (type) {
            case INTEGER, LONG, FLOAT, DOUBLE -> getValueAsNumber(value);
            case CHARACTER, STRING, BOOLEAN, NONE -> getValueAsString(value);
        };
    }

    public static Element getVariable(String text) {
        return new Variable(text);
    }

    public static Element getValueAsString(String text) {
        return new Value(new StringField(text.substring(text.indexOf("'") + 1, text.lastIndexOf("'"))));
    }

    public static Element getValueAsNumber(String text) {
        try {
            return new Value(new IntegerField(Integer.parseInt(text.strip())));
        } catch (NumberFormatException ignored) {

        }

        try {
            return new Value(new LongField(Long.parseLong(text.strip())));
        } catch (NumberFormatException ignored) {

        }

        try {
            return new Value(new FloatField(Float.parseFloat(text.strip())));
        } catch (NumberFormatException ignored) {

        }

        try {
            return new Value(new DoubleField(Double.parseDouble(text.strip())));
        } catch (NumberFormatException ignored) {

        }

        throw new UnsupportedOperationException("This value is not a number");
    }
}
