package exceptions;

public class NoSeatsAvailableException extends Exception {
    public NoSeatsAvailableException(String msg) {
        super(msg);
    }
}
