package Users;

import org.example.Flight;

public class FlightRequest {

    public enum RequestType { CREATE, DELETE }

    private final Flight flight;
    private final RequestType type;

    public FlightRequest(Flight flight, RequestType type) {
        this.flight = flight;
        this.type = type;
    }

    public Flight getFlight() {
        return flight;
    }

    public RequestType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FlightRequest)) return false;

        FlightRequest that = (FlightRequest) o;

        return flight.getFlightId() == that.flight.getFlightId()
                && type == that.type;
    }

    @Override
    public int hashCode() {
        int result = flight.getFlightId();
        result = 31 * result + type.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "FlightRequest{" +
                "flightId=" + flight.getFlightId() +
                ", type=" + type +
                '}';
    }
}
