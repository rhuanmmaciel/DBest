package exceptions.tree;

import java.io.Serial;

public abstract class ParentsException extends TreeException {

    @Serial
    private static final long serialVersionUID = 4664804339235357480L;

    public ParentsException(String message) {
        super(message);
    }
}
