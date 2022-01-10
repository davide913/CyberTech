package it.unive.cybertech.database.Material.Exception;

/**
 * This class describe a no material type found exception. it extends runtime exception
 *
 * @author Davide Finesso
 */
public class NoMaterialTypeFoundException extends RuntimeException{
    public NoMaterialTypeFoundException(String s){ super(s);}
}
