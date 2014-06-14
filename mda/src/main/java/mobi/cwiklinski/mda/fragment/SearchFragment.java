package mobi.cwiklinski.mda.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.reflect.TypeToken;
import com.joanzapata.android.iconify.Iconify;

import java.io.IOException;
import java.util.ArrayList;

import mobi.cwiklinski.mda.R;
import mobi.cwiklinski.mda.activity.DateActivity;
import mobi.cwiklinski.mda.adapter.LocalityRemoteAdapter;
import mobi.cwiklinski.mda.model.Locality;
import mobi.cwiklinski.mda.net.HttpUtil;
import mobi.cwiklinski.mda.util.Constant;

public class SearchFragment extends BaseFragment implements TextWatcher {

    public static final String FRAGMENT_TAG = SearchFragment.class.getSimpleName();
    private AutoCompleteTextView mSuggest;
    private TextView mCity;
    private TextView mProvince;
    private TextView mDistrict;
    private TextView mCommunity;
    private LinearLayout mInfo;
    private Locality mCurrentLocality;
    private Constant.Destination mDestination = Constant.Destination.FROM_CRACOW;
    private FetchLocalityTask mTask;
    private boolean load = false;

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
    protected int getLayoutResource() {
        return R.layout.search;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getTypefaceManager().parse((ViewGroup) view);
        mSuggest = (AutoCompleteTextView) view.findViewById(R.id.search_auto);
        mInfo = (LinearLayout) view.findViewById(R.id.search_info);
        mCity = (TextView) view.findViewById(R.id.search_city);
        mProvince = (TextView) view.findViewById(R.id.search_province);
        mDistrict = (TextView) view.findViewById(R.id.search_district);
        mCommunity = (TextView) view.findViewById(R.id.search_community);
        mSuggest.addTextChangedListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSuggest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view != null && view.getTag() != null) {
                    mCurrentLocality = (Locality) view.getTag();
                    fillLocality();
                    load = true;
                    mSuggest.setText(mCurrentLocality.getName());
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mSuggest.getWindowToken(), 0);
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
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mTask != null) {
            mTask.cancel(true);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (mCurrentLocality != null) {
            MenuItem near = menu.add(R.id.menu_group_main, R.id.menu_forward, ++mMenuOrder,
                R.string.menu_forward);
            setActionBarItem(near, Iconify.IconValue.fa_arrow_circle_right);
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
            getActivity().invalidateOptionsMenu();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!load && s.length() > 3) {
            if (mTask != null) {
                mTask.cancel(true);
            }
            mTask = new FetchLocalityTask();
            mTask.execute(s.toString());
        }
        load = false;
    }

    @Override
    public void afterTextChanged(Editable s) { }

    private class FetchLocalityTask extends AsyncTask<String, Void, ArrayList<Locality>> {

        @Override
        protected ArrayList<Locality> doInBackground(String... params) {
            HttpUtil util = HttpUtil.getInstance();
            String text = params[0];
            try {
                return util
                    .setMethod(HttpUtil.Method.GET)
                    .setUrl("http://rozklady.mda.malopolska.pl/ws/getCity.php?miejscowosc=" + text)
                    .setCookieFromPreferences(getPreferences())
                    .connect()
                    .getObjects(new TypeToken<ArrayList<Locality>>() {
                    });
            } catch (IOException | HttpRequest.HttpRequestException e) {
                notifyConnectionError();
            }
            return new ArrayList<>();
        }

        @Override
        protected void onPreExecute() {
            mSuggest.setCompoundDrawablesWithIntrinsicBounds(null, null,
                getResources().getDrawable(R.drawable.loading), null);
        }

        @Override
        protected void onPostExecute(ArrayList<Locality> localities) {
            Log.e(FRAGMENT_TAG, "loaded localities: " + localities.size());
            if (localities.size() > 0) {
                mSuggest.setAdapter(new LocalityRemoteAdapter(getActivity(), localities));
                mSuggest.invalidate();
                mSuggest.showDropDown();
            }
            mSuggest.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }

        @Override
        protected void onCancelled() {
            mSuggest.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
    }
}
