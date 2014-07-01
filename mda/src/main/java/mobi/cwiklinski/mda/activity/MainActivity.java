package mobi.cwiklinski.mda.activity;

import android.app.FragmentTransaction;
import android.os.Bundle;

import mobi.cwiklinski.mda.R;
import mobi.cwiklinski.mda.fragment.ChooseFragment;


public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferences().clearApp();
        if (savedInstanceState == null) {
            mTask = new CheckConnectionTask();
            mTask.execute();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.fragment_in, R.anim.fragment_out);
            ChooseFragment chooseFragment = ChooseFragment.newInstance();
            ft.replace(R.id.fragment_container, chooseFragment, ChooseFragment.FRAGMENT_TAG);
            ft.commit();
        }
    }
}
