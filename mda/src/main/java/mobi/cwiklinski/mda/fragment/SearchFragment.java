package mobi.cwiklinski.mda.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import mobi.cwiklinski.mda.R;
import mobi.cwiklinski.mda.activity.DateActivity;
import mobi.cwiklinski.mda.adapter.LocalityRemoteAdapter;
import mobi.cwiklinski.mda.model.Locality;
import mobi.cwiklinski.mda.util.Constant;

public class SearchFragment extends BaseFragment {

    public static final String FRAGMENT_TAG = SearchFragment.class.getSimpleName();
    private AutoCompleteTextView mSuggest;
    private TextView mCity;
    private TextView mProvince;
    private TextView mDistrict;
    private TextView mCommunity;
    private LinearLayout mInfo;
    private Locality mCurrentLocality;
    private Constant.Destination mDestination = Constant.Destination.FROM_CRACOW;

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        fragment.setRetainInstance(true);
        fragment.setHasOptionsMenu(true);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDestination = getPreferences().getDestination();
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(Constant.EXTRA_LOCALITY)) {
                mCurrentLocality = (Locality) savedInstanceState.getSerializable(Constant.EXTRA_LOCALITY);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSuggest = (AutoCompleteTextView) view.findViewById(R.id.search_auto);
        mInfo = (LinearLayout) view.findViewById(R.id.search_info);
        mCity = (TextView) view.findViewById(R.id.search_city);
        mProvince = (TextView) view.findViewById(R.id.search_province);
        mDistrict = (TextView) view.findViewById(R.id.search_district);
        mCommunity = (TextView) view.findViewById(R.id.search_community);
        Button next = (Button) view.findViewById(R.id.search_next);
        mSuggest.setThreshold(3);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentLocality != null) {
                    getPreferences().saveLocality(mCurrentLocality);
                    startActivity(new Intent(getActivity(), DateActivity.class));
                }
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mDestination != null) {
            switch (mDestination) {
                case FROM_CRACOW:
                    getBaseActivity().setMainTitle(R.string.choose_from_cracow_button);
                    break;
                case TO_CRACOW:
                    getBaseActivity().setMainTitle(R.string.choose_to_cracow_button);
                    break;
                case FROM_NOWY_SACZ:
                    getBaseActivity().setMainTitle(R.string.choose_from_nowysacz_button);
                    break;
                case TO_NOWY_SACZ:
                    getBaseActivity().setMainTitle(R.string.choose_to_nowysacz_button);
                    break;
            }
        }
        mSuggest.setAdapter(new LocalityRemoteAdapter(getActivity(), new ArrayList<Locality>()));
        mSuggest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view != null && view.getTag() != null) {
                    mCurrentLocality = (Locality) view.getTag();
                    fillLocality();
                    mSuggest.setText(mCurrentLocality.getName());
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mDestination != null) {
            int titleResource;
            switch(mDestination) {
                case TO_CRACOW:
                    titleResource = R.string.choose_to_cracow_button;
                    break;
                case FROM_NOWY_SACZ:
                    titleResource = R.string.choose_from_nowysacz_button;
                    break;
                case TO_NOWY_SACZ:
                    titleResource = R.string.choose_to_nowysacz_button;
                    break;
                default:
                    titleResource = R.string.choose_from_cracow_button;
                    break;
            }
            getBaseActivity().setMainTitle(titleResource);
            getBaseActivity().setSubTitle(R.string.choose_city_title);
            fillLocality();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mCurrentLocality != null) {
            outState.putSerializable(Constant.EXTRA_LOCALITY, mCurrentLocality);
            mSuggest.setText("");
        }
    }

    private void fillLocality() {
        if (mCurrentLocality != null) {
            getBaseActivity().setSubTitle(mCurrentLocality.getName());
            mCity.setText(mCurrentLocality.getName());
            if (!TextUtils.isEmpty(mCurrentLocality.getProvince())) {
                mProvince.setText(mCurrentLocality.getProvince());
            } else {
                mProvince.setText(R.string.no_data);
            }
            if (!TextUtils.isEmpty(mCurrentLocality.getDistrict())) {
                mDistrict.setText(mCurrentLocality.getDistrict());
            } else {
                mDistrict.setText(R.string.no_data);
            }
            if (!TextUtils.isEmpty(mCurrentLocality.getCommunity())) {
                mCommunity.setText(mCurrentLocality.getCommunity());
            } else {
                mCommunity.setText(R.string.no_data);
            }
            mInfo.setVisibility(View.VISIBLE);
        }
    }
}
