package exceptions.dsl;

import dsl.DslErrorListener;

public class InputException extends Exception {

    public InputException(String message) {
        super(message);

        DslErrorListener.addErrors(message);
    }
}
