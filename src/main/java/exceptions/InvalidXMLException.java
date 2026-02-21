package exceptions;

/**
 * Exception thrown when XML parsing or processing fails
 */
public class InvalidXMLException extends Exception {
    
    public InvalidXMLException(String message) {
        super(message);
    }
    
    public InvalidXMLException(String message, Throwable cause) {
        super(message, cause);
    }
}
