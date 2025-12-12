package exceptions;
//ошибки при создании/удалении ьрони

public class BookingServiceException extends Exception {
    public BookingServiceException(String msg) {
        super(msg);
    }
}
