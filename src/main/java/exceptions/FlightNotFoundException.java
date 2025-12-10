package exceptions;

public class FlightNotFoundException extends Exception {
    public FlightNotFoundException(String msg) {
        super(msg);
    }
}
