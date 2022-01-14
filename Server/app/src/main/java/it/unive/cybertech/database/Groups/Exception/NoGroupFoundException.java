package it.unive.cybertech.database.Groups.Exception;

/**
 * This class describe a no group found exception. it extends runtime exception
 *
 * @author Davide Finesso
 */
public class NoGroupFoundException extends RuntimeException{
    public NoGroupFoundException(String s){
        super(s);
    }
}
