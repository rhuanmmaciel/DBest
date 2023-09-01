package exceptions.tree;

import java.io.Serial;

public class ArgumentsException extends TreeException {

    @Serial
    private static final long serialVersionUID = 6944760171982147427L;

    public ArgumentsException(String message) {
        super(message);
    }

    @Override
    public ExceptionType type() {
        return ExceptionType.ARGUMENTS;
    }
}
