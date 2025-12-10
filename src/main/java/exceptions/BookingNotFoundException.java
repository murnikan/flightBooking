package exceptions;

public class BookingNotFoundException extends Exception {
    public BookingNotFoundException(String msg) {
        super(msg);
    }
}
