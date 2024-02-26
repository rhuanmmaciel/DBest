package exceptions.tree;

import java.io.Serial;

public class ParentsErrorException extends ParentsException {

    @Serial
    private static final long serialVersionUID = 7046253632577090944L;

    public ParentsErrorException(String message) {
        super(message);
    }

    @Override
    public ExceptionType type() {
        return ExceptionType.PARENTS_ERROR;
    }
}
