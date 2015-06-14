package mobi.cwiklinski.mda.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import mobi.cwiklinski.mda.R;
import mobi.cwiklinski.mda.fragment.SearchFragment;

public class SearchActivity extends AbstractOverflowActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            SearchFragment searchFragment = SearchFragment.newInstance();
            ft.replace(R.id.fragment_container, searchFragment, SearchFragment.FRAGMENT_TAG);
            ft.commit();
        }
        getSupportActionBar().setSubtitle(R.string.target_searching);
    }
}
