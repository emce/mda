package mobi.cwiklinski.mda.event;

public class ProgressEvent {

    private boolean show = false;

    public ProgressEvent(boolean show) {
        this.show = show;
    }

    public boolean isShown() {
        return show;
    }
}
