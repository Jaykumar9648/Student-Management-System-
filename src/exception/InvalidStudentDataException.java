package exception;

/**
 * Thrown when student input data fails validation
 * (e.g., empty name, invalid age, malformed email).
 */
public class InvalidStudentDataException extends Exception {
    public InvalidStudentDataException(String message) {
        super(message);
    }
}
