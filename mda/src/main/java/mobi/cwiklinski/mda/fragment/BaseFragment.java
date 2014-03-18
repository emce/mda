package mobi.cwiklinski.mda.fragment;

import android.app.Fragment;
import android.view.ViewGroup;

import mobi.cwiklinski.mda.R;
import mobi.cwiklinski.mda.activity.BaseActivity;
import mobi.cwiklinski.mda.util.TypefaceManager;
import mobi.cwiklinski.mda.util.UserPreferences;

abstract public class BaseFragment extends Fragment {

    public TypefaceManager getTypefaceManager() {
        return getBaseActivity().getTypefaceManager();
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
