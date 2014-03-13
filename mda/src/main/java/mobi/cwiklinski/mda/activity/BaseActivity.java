package mobi.cwiklinski.mda.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import mobi.cwiklinski.mda.R;
import mobi.cwiklinski.mda.util.TypefaceManager;
import mobi.cwiklinski.mda.util.UserPreferences;

public class BaseActivity extends Activity {

    private TypefaceManager mTypefaceManager;
    private UserPreferences mPreferences;
    private View mCustomActionBarView;
    private TextView mMainTitle;
    private TextView mSubTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment);
        setCustomActionBar();
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

    private void setCustomActionBar() {
        if (mCustomActionBarView == null) {
            mCustomActionBarView = getLayoutInflater().inflate(R.layout.actionbar, null);
            getActionBar().setDisplayShowCustomEnabled(true);
            getActionBar().setDisplayShowTitleEnabled(false);
            getActionBar().setCustomView(mCustomActionBarView);
            mMainTitle = (TextView) mCustomActionBarView.findViewById(R.id.main_title);
            mMainTitle.setTypeface(getTypefaceManager().getTypeface(TypefaceManager.FontFace.ROBOTO_BOLD));
            mSubTitle = (TextView) mCustomActionBarView.findViewById(R.id.sub_title);
            mSubTitle.setTypeface(getTypefaceManager().getTypeface(TypefaceManager.FontFace.ROBOTO_NORMAL));
        }
    }

    public void setMainTitle(int resourceId) {
        setMainTitle(getString(resourceId));
    }

    public void setSubTitle(int resourceId) {
        setSubTitle(getString(resourceId));
    }

    public void setMainTitle(String text) {
        setCustomActionBar();
        mMainTitle.setText(text);
    }

    public void setSubTitle(String text) {
        setCustomActionBar();
        mSubTitle.setText(text);
    }

}
