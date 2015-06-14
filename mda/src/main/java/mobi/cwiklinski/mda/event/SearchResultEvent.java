package mobi.cwiklinski.mda.event;

import java.util.ArrayList;

import mobi.cwiklinski.mda.model.Locality;

public class SearchResultEvent {

    private ArrayList<Locality> result = new ArrayList<>();

    public SearchResultEvent(ArrayList<Locality> result) {
        this.result = result;
    }

    public ArrayList<Locality> getResult() {
        return result;
    }
}
