package mobi.cwiklinski.mda.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;

import mobi.cwiklinski.mda.model.Locality;

public class UserPreferences {
    private Context mContext;
    private SharedPreferences preferences;
    private Gson mGson;
    public static final String KEY_PHPSESSID = "php_session";
    public static final String KEY_DESTINATION = "destination";
    public static final String KEY_LOCALITY = "locality";
    public static final String KEY_DATE = "date";

    public UserPreferences(Context context) {
        mContext = context;
        GsonBuilder gsonBuilder = new GsonBuilder();
        mGson = gsonBuilder.create();
        preferences = getDefaultSharedPreferences();
    }

    private SharedPreferences getDefaultSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
    }

    private SharedPreferences.Editor getSharedPreferencesEditor() {
        return preferences.edit();
    }

    public String get(String key) {
        return preferences.getString(key, null);
    }

    public String get(String key, String defaultString) {
        return preferences.getString(key, defaultString);
    }

    public String getString(String key, String defaultValue) {
        try {
            return preferences.getString(key, defaultValue);
        } catch (ClassCastException e) {
            return defaultValue;
        }
    }

    public int getInt(String key, int defaultValue) {
        try {
            return preferences.getInt(key, defaultValue);
        } catch (ClassCastException e) {
            return Integer.parseInt(preferences.getString(key, Integer.toString(defaultValue)));
        }
    }

    public long getLong(String key, long defaultValue) {
        try {
            return preferences.getLong(key, defaultValue);
        } catch (ClassCastException e) {
            return Long.parseLong(preferences.getString(key, Long.toString(defaultValue)));
        }
    }

    public boolean getBool(String key, boolean defaultValue) {
        try {
            return preferences.getBoolean(key, defaultValue);
        } catch (ClassCastException e) {
            return defaultValue;
        }
    }

    public boolean saveField(String field, String value) {
        SharedPreferences.Editor editor = getSharedPreferencesEditor();
        editor.putString(field, value);
        return editor.commit();
    }

    public boolean saveField(String field, boolean value) {
        SharedPreferences.Editor editor = getSharedPreferencesEditor();
        editor.putBoolean(field, value);
        return editor.commit();
    }

    public boolean saveField(String field, int value) {
        SharedPreferences.Editor editor = getSharedPreferencesEditor();
        editor.putInt(field, value);
        return editor.commit();
    }

    public boolean saveField(String field, long value) {
        SharedPreferences.Editor editor = getSharedPreferencesEditor();
        editor.putLong(field, value);
        return editor.commit();
    }

    public void clear() {
        SharedPreferences.Editor editor = getSharedPreferencesEditor();
        editor.clear();
        editor.commit();
    }

    public void clearField(String field) {
        SharedPreferences.Editor editor = getSharedPreferencesEditor();
        editor.remove(field);
        editor.commit();
    }

    public void clearApp() {
        SharedPreferences.Editor editor = getSharedPreferencesEditor();
        editor.remove(KEY_DATE);
        editor.remove(KEY_DESTINATION);
        editor.remove(KEY_LOCALITY);
        editor.commit();
    }

    public boolean exists(String field) {
        return preferences.contains(field);
    }

    public String getPhpSessionId() {
        return preferences.getString(KEY_PHPSESSID, "");
    }

    public void saveDestination(Constant.Destination destination) {
        saveField(KEY_DESTINATION, destination.getId());
    }

    public Constant.Destination getDestination() {
        if (exists(KEY_DESTINATION)) {
            return Constant.Destination.values()[getInt(KEY_DESTINATION, -1)];
        }
        return null;
    }

    public void saveLocality(Locality locality) {
        saveField(KEY_LOCALITY, mGson.toJson(locality));
    }

    public Locality getLocality() {
        if (exists(KEY_LOCALITY)) {
            return mGson.fromJson(getString(KEY_LOCALITY, ""), Locality.class);
        }
        return null;
    }

    public void saveDate(DateTime dateTime) {
        saveField(KEY_DATE, dateTime.getMillis());
    }

    public DateTime getDate() {
        if (exists(KEY_DATE)) {
            MutableDateTime dateTime = new MutableDateTime();
            dateTime.setMillis(getLong(KEY_DATE, 0));
            return dateTime.toDateTime();
        }
        return null;
    }
}
