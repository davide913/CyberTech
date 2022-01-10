package it.unive.cybertech.database.Profile.Exception;

/**
 * This class describe general lending in progress exception. it extends runtime exception
 *
 * @author Davide Finesso
 */
public class LendingInProgressException extends RuntimeException{
    public LendingInProgressException(String message) {
        super(message);
    }
}
