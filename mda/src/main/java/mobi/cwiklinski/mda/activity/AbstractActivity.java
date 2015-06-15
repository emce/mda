package mobi.cwiklinski.mda.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.GoogleAnalytics;

import org.joda.time.DateTime;

import de.greenrobot.event.EventBus;
import mobi.cwiklinski.mda.App;
import mobi.cwiklinski.mda.BuildConfig;
import mobi.cwiklinski.mda.R;
import mobi.cwiklinski.mda.event.ProgressEvent;
import mobi.cwiklinski.mda.fragment.InfoDialogFragment;
import mobi.cwiklinski.mda.net.DataService;
import mobi.cwiklinski.mda.util.ActivityHelper;
import mobi.cwiklinski.mda.util.UserPreferences;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

abstract public class AbstractActivity extends AppCompatActivity {

    public static final String TITLE = "AbstractActivity:title";
    protected Toolbar mToolbar;
    private UserPreferences mPreferences = null;
    protected FrameLayout mProgress;
    protected AdView ad;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_TIME_TICK)) {
                DateTime date = new DateTime();
                if (date.getMinuteOfHour() % 10 == 0) {
                    Log.e("BaseActivity", "Full five minutes");
                    DataService.startCheck(AbstractActivity.this);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!BuildConfig.DEBUG) {
            ((App) getApplication()).getTracker(App.TrackerName.APP_TRACKER);
        }
        setContentView(R.layout.base);
        setupAds();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mProgress = (FrameLayout) findViewById(R.id.progress);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ViewCompat.setTransitionName(mToolbar, TITLE);
        }
    }

    protected void setupAds() {
        ad = (AdView) findViewById(R.id.adView);
        if (isAdFree() && ad != null) {
            ad.setVisibility(View.GONE);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!BuildConfig.DEBUG) {
            GoogleAnalytics.getInstance(this).reportActivityStart(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!BuildConfig.DEBUG) {
            GoogleAnalytics.getInstance(this).reportActivityStop(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        registerReceiver(mReceiver, filter);
        EventBus.getDefault().registerSticky(this);
        if (ad != null) {
            if (!isAdFree()) {
                ad.loadAd(new AdRequest.Builder().build());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (!isAdFree()) {
            MenuItem info = menu.add(R.id.menu_group_main, R.id.menu_info, 1, R.string.menu_info);
            ActivityHelper.setMenuItem(info, R.drawable.ic_menu_info);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_info:
                InfoDialogFragment dialog =
                    InfoDialogFragment.newInstance();
                dialog.show(this.getSupportFragmentManager().beginTransaction(),
                    InfoDialogFragment.FRAGMENT_TAG);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public UserPreferences getPreferences() {
        if (mPreferences == null) {
            mPreferences = new UserPreferences(this);
        }
        return mPreferences;
    }

    public void showProgress() {
        mProgress.setVisibility(View.VISIBLE);
    }

    public void hideProgress() {
        mProgress.setVisibility(View.GONE);
    }

    public void onEventMainThread(ProgressEvent event) {
        if (event.isShown()) {
            showProgress();
        } else {
            hideProgress();
        }
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    public boolean isAdFree() {
        return getApplicationContext().getPackageName().contains(".adfree");
    }
}
