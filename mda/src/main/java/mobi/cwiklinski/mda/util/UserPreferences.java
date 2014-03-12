package mobi.cwiklinski.mda.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class UserPreferences {
    private Context mContext;
    private SharedPreferences preferences;
    public static final String KEY_PHPSESSID = "php_session";

    public UserPreferences(Context context) {
        mContext = context;
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

    public boolean exists(String field) {
        return preferences.contains(field);
    }

    public String getPhpSessionId() {
        return preferences.getString(KEY_PHPSESSID, "");
    }
}
