package mobi.cwiklinski.mda.fragment;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

import mobi.cwiklinski.mda.R;
import mobi.cwiklinski.mda.activity.BaseActivity;
import mobi.cwiklinski.mda.model.Locality;
import mobi.cwiklinski.mda.util.TypefaceManager;
import mobi.cwiklinski.mda.util.UserPreferences;

abstract public class BaseFragment extends Fragment {

    protected int mMenuOrder = 0;
    protected ActionMode mActionMode;
    protected boolean isLoaded = false;

    public TypefaceManager getTypefaceManager() {
        return getBaseActivity().getTypefaceManager();
    }

    public BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

    abstract protected int getLayoutResource();

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.global, container, false);
    }

    public boolean isAdFree() {
        return getActivity().getApplicationContext().getPackageName().contains(".adfree");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FrameLayout content = (FrameLayout) view.findViewById(R.id.content);
        if (content != null) {
            LayoutInflater.from(getActivity()).inflate(getLayoutResource(), content, true);
        }
        AdView ad = (AdView) view.findViewById(R.id.adView);
        if (isAdFree()) {
            ad.setVisibility(View.GONE);
        } else {
            if (view.findViewById(R.id.adView) != null) {
                ad.loadAd(new AdRequest.Builder().build());
            }
        }
        if (view.findViewById(R.id.current_city) != null && getPreferences().getLocality() != null) {
            Locality locality = getPreferences().getLocality();
            TextView city = (TextView) view.findViewById(R.id.current_city);
            TextView province = (TextView) view.findViewById(R.id.current_province);
            TextView district = (TextView) view.findViewById(R.id.current_district);
            TextView community = (TextView) view.findViewById(R.id.current_community);
            if (locality.getName() != null) {
                city.setText(locality.getName());
            }
            if (locality.getProvince() != null) {
                province.setText(locality.getProvince().toLowerCase());
            }
            if (locality.getDistrict() != null) {
                district.setText(locality.getDistrict());
            }
            if (locality.getCommunity() != null) {
                community.setText(locality.getCommunity());
            }
            city.setTypeface(
                getTypefaceManager().getTypeface(TypefaceManager.FontFace.ROBOTO_BOLD));
            province.setTypeface(
                getTypefaceManager().getTypeface(TypefaceManager.FontFace.ROBOTO_BOLD));
            district.setTypeface(
                getTypefaceManager().getTypeface(TypefaceManager.FontFace.ROBOTO_BOLD));
            community.setTypeface(
                getTypefaceManager().getTypeface(TypefaceManager.FontFace.ROBOTO_BOLD));
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (!isAdFree()) {
            MenuItem info = menu.add(R.id.menu_group_main, R.id.menu_info, ++mMenuOrder,
                R.string.menu_info);
            setActionBarItem(info, Iconify.IconValue.fa_info_circle);
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_info:
                InfoDialogFragment dialog =
                    InfoDialogFragment.newInstance();
                dialog.show(getBaseActivity().getFragmentManager().beginTransaction(),
                    InfoDialogFragment.FRAGMENT_TAG);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setActionBarItem(MenuItem item, Iconify.IconValue icon) {
        int show;
        boolean isHorizontal = getResources().getBoolean(R.bool.isLandscape);
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if (isHorizontal || isTablet) {
            show = MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT;
        } else {
            show = MenuItem.SHOW_AS_ACTION_IF_ROOM;
        }
        item.setIcon(getMenuIcon(icon));
        item.setShowAsAction(show);
    }

    public Drawable getMenuIcon(Iconify.IconValue icon) {
        return new IconDrawable(getActivity(), icon)
            .colorRes(R.color.dark_blue)
            .sizeDp(32);
    }
}
