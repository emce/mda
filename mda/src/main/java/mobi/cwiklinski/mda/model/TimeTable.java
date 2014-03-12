package mobi.cwiklinski.mda.model;

import org.joda.time.MutableDateTime;

public class TimeTable extends Model {
    private String start;
    private String destination;
    private MutableDateTime departure;
    private MutableDateTime arrival;
    private String length;
    private Double price;
    private String tickets;
    private String description;

    public TimeTable() {

    }

    public TimeTable(String start, String destination, MutableDateTime departure, MutableDateTime arrival,
                     String length, Double price, String tickets, String description) {
        this.start = start;
        this.destination = destination;
        this.departure = departure;
        this.arrival = arrival;
        this.length = length;
        this.price = price;
        this.tickets = tickets;
        this.description = description;
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

    public MutableDateTime getDeparture() {
        return departure;
    }

    public void setDeparture(MutableDateTime departure) {
        this.departure = departure;
    }

    public MutableDateTime getArrival() {
        return arrival;
    }

    public void setArrival(MutableDateTime arrival) {
        this.arrival = arrival;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
