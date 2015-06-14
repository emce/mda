package mobi.cwiklinski.mda;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class App extends Application {

    // The following line should be changed to include the correct property id.
    private static final String PROPERTY_ID = "UA-49029742-1";

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("RobotoCondensed-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    public enum TrackerName {
        APP_TRACKER,
        GLOBAL_TRACKER
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<>();

    public App() {
        super();
    }

    public synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(R.xml.app_tracker)
                : analytics.newTracker(PROPERTY_ID);
            mTrackers.put(trackerId, t);
        }
        return mTrackers.get(trackerId);
    }
}
