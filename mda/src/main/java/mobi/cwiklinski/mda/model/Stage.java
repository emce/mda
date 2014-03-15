package mobi.cwiklinski.mda.model;

import org.joda.time.DateTime;

public class Stage extends Model {

    private String destination;
    private String station;
    private DateTime arrival;
    private String price;

    public Stage() { }

    public Stage(String destination, String station, DateTime arrival, String price) {
        this.destination = destination;
        this.station = station;
        this.arrival = arrival;
        this.price = price;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public DateTime getArrival() {
        return arrival;
    }

    public void setArrival(DateTime arrival) {
        this.arrival = arrival;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
