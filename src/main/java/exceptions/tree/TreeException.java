package exceptions.tree;

import java.io.Serial;

public abstract class TreeException extends Exception {

    @Serial
    private static final long serialVersionUID = 3453003137947098627L;

    public enum ExceptionType {
        ARGUMENTS, PARENTS_ERROR, PARENTS_AMOUNT
    }

    public TreeException(String message) {
        super(message);
    }

    public abstract ExceptionType type();
}
