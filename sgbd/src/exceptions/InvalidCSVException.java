package exceptions;

import java.io.Serial;

public class InvalidCSVException extends Exception {

    @Serial
    private static final long serialVersionUID = 9057235844324768504L;

    public InvalidCSVException(String message) {
        super(message);
    }
}
