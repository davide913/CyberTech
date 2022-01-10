package it.unive.cybertech.database.Profile.Exception;

/**
 * This class describe a no lending in progress found exception. it extends runtime exception
 *
 * @author Davide Finesso
 */
public class NoLendingInProgressFoundException extends RuntimeException{
    public NoLendingInProgressFoundException(String s){super(s);}
}
