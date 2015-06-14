package mobi.cwiklinski.mda.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.ActionMode;
import android.view.View;
import android.widget.TextView;

import mobi.cwiklinski.mda.R;
import mobi.cwiklinski.mda.activity.AbstractActivity;
import mobi.cwiklinski.mda.model.Locality;
import mobi.cwiklinski.mda.util.UserPreferences;

abstract public class AbstractFragment extends Fragment {

    protected int mMenuOrder = 0;
    protected ActionMode mActionMode;
    protected boolean isLoaded = false;

    public AbstractActivity getBaseActivity() {
        return (AbstractActivity) getActivity();
    }

    public UserPreferences getPreferences() {
        return getBaseActivity().getPreferences();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }
}
