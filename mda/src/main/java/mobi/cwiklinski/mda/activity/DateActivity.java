package mobi.cwiklinski.mda.activity;

import android.app.FragmentTransaction;
import android.os.Bundle;

import mobi.cwiklinski.mda.R;
import mobi.cwiklinski.mda.fragment.DateFragment;
import mobi.cwiklinski.mda.util.GoogleAnalyticsHelper;

public class DateActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GoogleAnalyticsHelper.activityStart(this);
        if (savedInstanceState == null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.fragment_in, R.anim.fragment_out);
            DateFragment dateFragment = DateFragment.newInstance();
            ft.replace(R.id.fragment_container, dateFragment, DateFragment.FRAGMENT_TAG);
            ft.commit();
        }
    }
}
