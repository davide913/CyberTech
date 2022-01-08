package it.unive.cybertech.database.Profile.Exception;

/**
 * This class describe a no user found exception. it extends runtime exception
 *
 * @author Davide Finesso
 */
public class NoUserFoundException extends RuntimeException{
    public NoUserFoundException(String message) {
        super(message);
    }

}
