package mobi.cwiklinski.mda.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import mobi.cwiklinski.mda.R;
import mobi.cwiklinski.mda.fragment.ChooseFragment;
import mobi.cwiklinski.mda.net.DataService;

public class MainActivity extends AbstractActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferences().clearApp();
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ChooseFragment chooseFragment = ChooseFragment.newInstance();
            ft.replace(R.id.fragment_container, chooseFragment, ChooseFragment.FRAGMENT_TAG);
            ft.commit();
        }
        DataService.startCheck(this);
    }
}
