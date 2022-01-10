package it.unive.cybertech.database.Profile.Exception;

/**
 * This class describe a no assistance type found exception. it extends runtime exception
 *
 * @author Davide Finesso
 */
public class NoAssistanceTypeFoundException extends RuntimeException{
    public NoAssistanceTypeFoundException(String message) {
        super(message);
    }
}
