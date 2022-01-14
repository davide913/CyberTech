package it.unive.cybertech.database.Material.Exception;

/**
 * This class describe a no material found exception. it extends runtime exception
 *
 * @author Davide Finesso
 */
public class NoMaterialFoundException extends RuntimeException{
    public NoMaterialFoundException( String s){super(s);}
}
