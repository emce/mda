package mobi.cwiklinski.mda.fragment;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.MenuItem;

import mobi.cwiklinski.mda.R;
import mobi.cwiklinski.mda.activity.MainActivity;
import mobi.cwiklinski.mda.util.TypefaceManager;
import mobi.cwiklinski.mda.util.UserPreferences;
import mobi.cwiklinski.typiconic.TypiconicDrawable;
import mobi.cwiklinski.typiconic.Typiconify;

abstract public class BaseFragment extends Fragment {

    protected int mMenuPosition = 0;

    public TypefaceManager getTypefaceManager() {
        return getBaseActivity().getTypefaceManager();
    }

    public MainActivity getBaseActivity() {
        return (MainActivity) getActivity();
    }

    public UserPreferences getPreferences() {
        return getBaseActivity().getPreferences();
    }

    public Drawable getMenuIcon(Typiconify.IconValue icon) {
        if (getActivity() != null) {
            return new TypiconicDrawable(getActivity(), icon)
                .colorRes(R.color.green)
                .sizeDp(44);
        }
        return null;
    }

    public void addMenuItem(Menu menu, int id, int textResource, Typiconify.IconValue icon, int showAsAction) {
        MenuItem item = menu.add(R.id.menu_group_main, id, mMenuPosition++, textResource);
        item.setIcon(getMenuIcon(icon));
        item.setShowAsAction(showAsAction);
    }
}
