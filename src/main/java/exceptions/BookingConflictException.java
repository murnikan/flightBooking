package exceptions;
//конфликт бронирований ( один и тот же пассажир делает 2 и более броней на свои данные)
public class BookingConflictException extends Exception {
    public BookingConflictException(String msg) {
        super(msg);
    }
}
