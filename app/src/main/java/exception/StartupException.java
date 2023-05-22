package exception;

public class StartupException extends RuntimeException {
    public StartupException(final String errorMessage) {
        super(errorMessage);
    }
}
