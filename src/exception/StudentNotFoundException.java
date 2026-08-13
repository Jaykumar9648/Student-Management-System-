package exception;

/**
 * Thrown when an operation (update/delete) targets a student ID
 * that does not exist in the records.
 */
public class StudentNotFoundException extends Exception {
    public StudentNotFoundException(String message) {
        super(message);
    }
}
