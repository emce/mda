package mobi.cwiklinski.mda.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import mobi.cwiklinski.mda.R;
import mobi.cwiklinski.mda.activity.DateActivity;
import mobi.cwiklinski.mda.adapter.LocalityRemoteAdapter;
import mobi.cwiklinski.mda.event.SearchResultEvent;
import mobi.cwiklinski.mda.model.Locality;
import mobi.cwiklinski.mda.net.DataService;
import mobi.cwiklinski.mda.util.ActivityHelper;
import mobi.cwiklinski.mda.util.Constant;

public class SearchFragment extends AbstractFragment implements TextWatcher {

    public static final String FRAGMENT_TAG = SearchFragment.class.getSimpleName();
    @InjectView(R.id.search_auto) AppCompatAutoCompleteTextView mSuggest;
    @InjectView(R.id.search_city) TextView mCity;
    @InjectView(R.id.search_province) TextView mProvince;
    @InjectView(R.id.search_district) TextView mDistrict;
    @InjectView(R.id.search_community) TextView mCommunity;
    @InjectView(R.id.search_info) LinearLayout mInfo;
    private Locality mCurrentLocality;
    private Constant.Destination mDestination = Constant.Destination.FROM_CRACOW;
    private boolean settled = false;

    public static SearchFragment newInstance() {
        return new SearchFragment();
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSuggest.addTextChangedListener(this);
        mSuggest.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mSuggest.addTextChangedListener(SearchFragment.this);
                }
            }
        });
        mSuggest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view != null && view.getTag() != null) {
                    mSuggest.removeTextChangedListener(SearchFragment.this);
                    settled = true;
                    mCurrentLocality = (Locality) view.getTag();
                    fillLocality();
                    mSuggest.setText(mCurrentLocality.getName());
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mSuggest.getWindowToken(), 0);
                    mSuggest.clearFocus();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mDestination != null) {
            fillLocality();
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mCurrentLocality != null) {
            outState.putSerializable(Constant.EXTRA_LOCALITY, mCurrentLocality);
            mSuggest.setText("");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (mCurrentLocality != null) {
            MenuItem forward = menu.add(R.id.menu_group_main, R.id.menu_forward, ++mMenuOrder,
                R.string.menu_forward);
            ActivityHelper.setMenuItem(forward, R.drawable.ic_menu_forward);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_forward:
                if (mCurrentLocality != null) {
                    getPreferences().saveLocality(mCurrentLocality);
                    startActivity(new Intent(getActivity(), DateActivity.class));
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void fillLocality() {
        if (mCurrentLocality != null) {
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
            getActivity().supportInvalidateOptionsMenu();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() > 3) {
            getBaseActivity().showProgress();
            settled = false;
            DataService.startSearch(getActivity(), s.toString());
        }
    }

    @Override
    public void afterTextChanged(Editable s) { }

    public void onEventMainThread(SearchResultEvent event) {
        if (!settled && mSuggest.hasFocus()) {
            if (event.getResult().size() > 0) {
                Log.e(FRAGMENT_TAG, "loaded localities: " + event.getResult().size());
                mSuggest.setAdapter(new LocalityRemoteAdapter(getActivity(), event.getResult()));
                mSuggest.invalidate();
                mSuggest.showDropDown();
            }
        }
        getBaseActivity().hideProgress();
    }
}
