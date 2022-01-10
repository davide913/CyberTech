package it.unive.cybertech.database.Profile.Exception;

/**
 * This class describe a no device found exception. it extends runtime exception
 *
 * @author Davide Finesso
 */
public class NoDeviceFoundException extends RuntimeException{
    public NoDeviceFoundException(String message) {
        super(message);
    }
}
