package mobi.cwiklinski.mda.activity;

import android.app.FragmentTransaction;
import android.os.Bundle;

import mobi.cwiklinski.mda.R;
import mobi.cwiklinski.mda.fragment.SearchFragment;
import mobi.cwiklinski.mda.util.GoogleAnalyticsHelper;

public class SearchActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GoogleAnalyticsHelper.activityStart(this);
        if (savedInstanceState == null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.fragment_in, R.anim.fragment_out);
            SearchFragment searchFragment = SearchFragment.newInstance();
            ft.replace(R.id.fragment_container, searchFragment, SearchFragment.FRAGMENT_TAG);
            ft.commit();
        }
    }
}
