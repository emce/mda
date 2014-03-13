package mobi.cwiklinski.mda.fragment;

import android.app.ListFragment;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.MenuItem;

import mobi.cwiklinski.mda.R;
import mobi.cwiklinski.mda.activity.BaseActivity;
import mobi.cwiklinski.mda.util.UserPreferences;
import mobi.cwiklinski.typiconic.TypiconicDrawable;
import mobi.cwiklinski.typiconic.Typiconify;

public class BaseListFragment extends ListFragment {

    protected int mMenuPosition = 0;

    public BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
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
