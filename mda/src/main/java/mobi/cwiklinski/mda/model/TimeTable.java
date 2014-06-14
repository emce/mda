package mobi.cwiklinski.mda.model;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;

public class TimeTable extends Model {
    private String start;
    private String destination;
    private Long departure;
    private Long arrival;
    private String length;
    private Double price;
    private String tickets;
    private Detail detail;
    private String carrier;

    public TimeTable() {

    }

    public TimeTable(String start, String destination, DateTime departure, DateTime arrival,
                     String length, Double price, String tickets, Detail detail, String carrier) {
        this.start = start;
        this.destination = destination;
        this.departure = departure.getMillis();
        this.arrival = arrival.getMillis();
        this.length = length;
        this.price = price;
        this.tickets = tickets;
        this.detail = detail;
        this.carrier = carrier;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public DateTime getDeparture() {
        MutableDateTime date = new DateTime().toMutableDateTime();
        date.setMillis(departure);
        return date.toDateTime();
    }

    public void setDeparture(DateTime departure) {
        this.departure = departure.getMillis();
    }

    public DateTime getArrival() {
        MutableDateTime date = new DateTime().toMutableDateTime();
        date.setMillis(arrival);
        return date.toDateTime();
    }

    public void setArrival(DateTime arrival) {
        this.arrival = arrival.getMillis();
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getTickets() {
        return tickets;
    }

    public void setTickets(String tickets) {
        this.tickets = tickets;
    }

    public Detail getDetail() {
        return detail;
    }

    public void setDetail(Detail detail) {
        this.detail = detail;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }
}
