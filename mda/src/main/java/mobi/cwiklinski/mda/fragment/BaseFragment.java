package mobi.cwiklinski.mda.fragment;

import android.app.Fragment;

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
}
