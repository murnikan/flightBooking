package exceptions;
//пустые данные в указании брони
public class InvalidBookingDataException extends Exception {
    public InvalidBookingDataException(String msg) {
        super(msg);
    }
}
