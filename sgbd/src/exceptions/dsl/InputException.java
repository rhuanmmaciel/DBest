package exceptions.dsl;

import dsl.DslErrorListener;

public class InputException extends Exception{

    public InputException(String txt){

        super(txt);
        DslErrorListener.addErrors(txt);

    }

}
