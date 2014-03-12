package mobi.cwiklinski.mda.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import mobi.cwiklinski.mda.util.TypefaceManager;
import mobi.cwiklinski.mda.util.UserPreferences;

public class BaseActivity extends Activity {

    private TypefaceManager mTypefaceManager;
    private UserPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActionBar() != null) {
            int titleId = getResources().getIdentifier("action_bar_title", "id",
                "android");
            TextView actionBarTitle = (TextView) findViewById(titleId);
            actionBarTitle.setAllCaps(true);
            actionBarTitle.setTypeface(getTypefaceManager().getTypeface(TypefaceManager.FontFace.ROBOTO_BOLD));
        }
    }

    public TypefaceManager getTypefaceManager() {
        if (mTypefaceManager == null) {
            mTypefaceManager = TypefaceManager.getInstance(this);
        }
        return mTypefaceManager;
    }

    public UserPreferences getPreferences() {
        if (mPreferences == null) {
            mPreferences = new UserPreferences(this);
        }
        return mPreferences;
    }

}
