package mobi.cwiklinski.mda.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import mobi.cwiklinski.mda.R;
import mobi.cwiklinski.mda.fragment.DetailFragment;

public class DetailActivity extends AbstractActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            DetailFragment detailFragment = DetailFragment.newInstance(getIntent());
            ft.replace(R.id.fragment_container, detailFragment, DetailFragment.FRAGMENT_TAG);
            ft.commit();
        }
    }
}
