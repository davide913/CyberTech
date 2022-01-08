package it.unive.cybertech.database.Profile.Exception;

/**
 * This class describe a no quarantine assistance found exception. it extends runtime exception
 *
 * @author Davide Finesso
 */
public class NoQuarantineAssistanceFoundException extends RuntimeException{
    public NoQuarantineAssistanceFoundException(String message) {
        super(message);
    }
}
