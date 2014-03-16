package mobi.cwiklinski.mda.fragment;

import android.app.ListFragment;
import android.os.Bundle;

import mobi.cwiklinski.mda.R;
import mobi.cwiklinski.mda.activity.BaseActivity;
import mobi.cwiklinski.mda.util.Constant;
import mobi.cwiklinski.mda.util.UserPreferences;

public class BaseListFragment extends ListFragment {

    protected boolean isLoaded = false;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(Constant.EXTRA_LOADED)) {
            isLoaded = savedInstanceState.getBoolean(Constant.EXTRA_LOADED);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(Constant.EXTRA_LOADED, isLoaded);
    }

    public BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

    public UserPreferences getPreferences() {
        return getBaseActivity().getPreferences();
    }

    public void notifyConnectionError() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getBaseActivity().showMessage(R.string.connection_error);
                }
            });
        }
    }
}
