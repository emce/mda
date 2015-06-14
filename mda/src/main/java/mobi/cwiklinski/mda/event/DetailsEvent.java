package mobi.cwiklinski.mda.event;

import java.util.ArrayList;

import mobi.cwiklinski.mda.model.Stage;

public class DetailsEvent {

    private ArrayList<Stage> details = new ArrayList<>();

    public DetailsEvent(ArrayList<Stage> details) {
        this.details = details;
    }

    public ArrayList<Stage> getDetails() {
        return details;
    }
}
