package mobi.cwiklinski.mda.model;

import org.joda.time.DateTime;

public class Stage extends Model {

    private String destination;
    private String station;
    private Long arrival;
    private String price;

    public Stage() { }

    public Stage(String destination, String station, DateTime arrival, String price) {
        this.destination = destination;
        this.station = station;
        if (arrival != null) {
            this.arrival = arrival.getMillis();
        }
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
        if (arrival != null) {
            return new DateTime(arrival);
        }
        return null;
    }

    public void setArrival(DateTime arrival) {
        if (arrival != null) {
            this.arrival = arrival.getMillis();
        }
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }


}
