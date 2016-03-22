package librarymanager.managers.impl;

/**
 *
 * @author Marek Janco
 */
public class FailureException extends RuntimeException {

    public FailureException() {
        super();
    }
    
    public FailureException(String message) {
        super(message);
    }
    
    public FailureException(Throwable ex) {
        super(ex);
    }
    
    public FailureException(String message, Throwable ex) {
        super(message, ex);
    }
    
}
