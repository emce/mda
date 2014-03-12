package mobi.cwiklinski.mda.activity;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import mobi.cwiklinski.mda.R;
import mobi.cwiklinski.mda.fragment.ChooseFragment;
import mobi.cwiklinski.mda.fragment.SearchFragment;


public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment);
        if (savedInstanceState == null) {
            changeFragment(MainFragments.FRAGMENT_CHOOSE, null);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void changeFragment(MainFragments fragment, Bundle args) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.fragment_in, R.anim.fragment_out);
        switch (fragment) {
            case FRAGMENT_CHOOSE:
                ChooseFragment chooseFragment = ChooseFragment.newInstance();
                ft.replace(R.id.fragment_container, chooseFragment, ChooseFragment.FRAGMENT_TAG);
                ft.commit();
                break;
            case FRAGMENT_SEARCH:
                SearchFragment searchFragment = SearchFragment.newInstance(args);
                ft.replace(R.id.fragment_container, searchFragment, SearchFragment.FRAGMENT_TAG);
                ft.commit();
                break;
        }
    }

    public enum MainFragments {
        FRAGMENT_CHOOSE(1),
        FRAGMENT_SEARCH(2),
        FRAGMENT_SEARCH_COS(3);

        private int order;

        MainFragments(int order) {
            this.order = order;
        }

        public int getOrder() {
            return order;
        }
    }

}
