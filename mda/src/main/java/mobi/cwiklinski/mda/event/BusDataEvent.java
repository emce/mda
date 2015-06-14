package mobi.cwiklinski.mda.event;

import java.util.ArrayList;

import mobi.cwiklinski.mda.model.TimeTable;

public class BusDataEvent {

    private ArrayList<TimeTable> busData;

    public BusDataEvent(ArrayList<TimeTable> busData) {
        this.busData = busData;
    }

    public ArrayList<TimeTable> getBusData() {
        return busData;
    }
}
