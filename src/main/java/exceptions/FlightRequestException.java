package exceptions;

//для ошибок флайтреквеста
public class FlightRequestException extends Exception {
    public FlightRequestException(String msg) {
        super(msg);
    }
}
