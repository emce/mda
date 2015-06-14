package mobi.cwiklinski.mda.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import mobi.cwiklinski.mda.R;
import mobi.cwiklinski.mda.fragment.DateFragment;
import mobi.cwiklinski.mda.model.Locality;

public class DateActivity extends AbstractOverflowActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            DateFragment dateFragment = DateFragment.newInstance();
            ft.replace(R.id.fragment_container, dateFragment, DateFragment.FRAGMENT_TAG);
            ft.commit();
        }
        Locality current = getPreferences().getLocality();
        getSupportActionBar().setSubtitle(String.format(getString(R.string.target_to), current.getName()));
    }
}
