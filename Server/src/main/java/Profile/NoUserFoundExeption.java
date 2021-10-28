package Profile;

public class NoUserFoundExeption extends RuntimeException{
    public NoUserFoundExeption(String message) {
        super(message);
    }

}
