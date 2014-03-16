package mobi.cwiklinski.mda.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.kevinsawicki.http.HttpRequest;

import org.joda.time.DateTime;

import java.io.IOException;

import mobi.cwiklinski.mda.R;
import mobi.cwiklinski.mda.net.HttpUtil;
import mobi.cwiklinski.mda.util.Constant;
import mobi.cwiklinski.mda.util.TypefaceManager;
import mobi.cwiklinski.mda.util.UserPreferences;

public class BaseActivity extends Activity {

    private TypefaceManager mTypefaceManager;
    private UserPreferences mPreferences;
    private View mCustomActionBarView;
    private TextView mMainTitle;
    private TextView mSubTitle;
    protected CheckConnectionTask mTask;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && (action.equals(Intent.ACTION_TIME_TICK)
                || action.equals(Intent.ACTION_TIMEZONE_CHANGED))) {
                DateTime date = new DateTime();
                if (date.getMinuteOfHour() % 5 == 0 || date.getMinuteOfHour() == 0) {
                    Log.e("BaseActivity", "Full five minutes");
                    mTask = new CheckConnectionTask();
                    mTask.execute();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment);
        setCustomActionBar();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mTask != null) {
            mTask.cancel(true);
        }
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        registerReceiver(mReceiver, filter);
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
        if (mCustomActionBarView == null && getActionBar() != null) {
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

    public void showMessage(int textResource) {
        showMessage(getString(textResource));
    }

    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public static Bundle intentToFragmentArguments(Intent intent) {
        Bundle arguments = new Bundle();
        if (intent == null) {
            return arguments;
        }

        final Uri data = intent.getData();
        if (data != null) {
            arguments.putParcelable("_uri", data);
        }

        final String action = intent.getAction();
        if (!TextUtils.isEmpty(action)) {
            arguments.putString("_action", action);
        }

        final Bundle extras = intent.getExtras();
        if (extras != null) {
            arguments.putAll(intent.getExtras());
        }

        return arguments;
    }

    protected class CheckConnectionTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                HttpUtil util = HttpUtil.getInstance();
                String cookie = util
                    .setUrl(Constant.URL_MAIN)
                    .connect()
                    .getCookie();
                getPreferences().saveField(UserPreferences.KEY_PHPSESSID, cookie);
            } catch (IOException | HttpRequest.HttpRequestException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
