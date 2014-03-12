package mobi.cwiklinski.mda.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import mobi.cwiklinski.mda.R;
import mobi.cwiklinski.mda.activity.MainActivity;
import mobi.cwiklinski.mda.net.HttpUtil;
import mobi.cwiklinski.mda.util.Constant;
import mobi.cwiklinski.mda.util.TypefaceManager;
import mobi.cwiklinski.mda.util.UserPreferences;

public class ChooseFragment extends BaseFragment {

    public static final String FRAGMENT_TAG = ChooseFragment.class.getSimpleName();
    private Button mFromCracowButton;
    private Button mToCracowButton;
    private Button mFromNowySaczButton;
    private Button mToNowySaczButton;
    private CheckConnectionTask mTask;

    public static ChooseFragment newInstance() {
        return new ChooseFragment();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mTask != null) {
            mTask.cancel(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mTask = new CheckConnectionTask();
        mTask.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.choose, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFromCracowButton = (Button) view.findViewById(R.id.cracow_choose_from);
        mToCracowButton = (Button) view.findViewById(R.id.cracow_choose_to);
        mFromCracowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBaseActivity().changeFragment(MainActivity.MainFragments.FRAGMENT_SEARCH,
                    createArguments(SearchFragment.Destination.FROM_CRACOW));
            }
        });
        mToCracowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBaseActivity().changeFragment(MainActivity.MainFragments.FRAGMENT_SEARCH,
                    createArguments(SearchFragment.Destination.TO_CRACOW));
            }
        });
        mFromNowySaczButton = (Button) view.findViewById(R.id.nowysacz_choose_from);
        mToNowySaczButton = (Button) view.findViewById(R.id.nowysacz_choose_to);
        mFromNowySaczButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBaseActivity().changeFragment(MainActivity.MainFragments.FRAGMENT_SEARCH,
                    createArguments(SearchFragment.Destination.FROM_NOWY_SACZ));
            }
        });
        mToNowySaczButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBaseActivity().changeFragment(MainActivity.MainFragments.FRAGMENT_SEARCH,
                    createArguments(SearchFragment.Destination.TO_NOWY_SACZ));
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFromCracowButton.setTypeface(getTypefaceManager().getTypeface(TypefaceManager.FontFace.ROBOTO_BOLD));
        mToCracowButton.setTypeface(getTypefaceManager().getTypeface(TypefaceManager.FontFace.ROBOTO_BOLD));
        mFromNowySaczButton.setTypeface(getTypefaceManager().getTypeface(TypefaceManager.FontFace.ROBOTO_BOLD));
        mToNowySaczButton.setTypeface(getTypefaceManager().getTypeface(TypefaceManager.FontFace.ROBOTO_BOLD));
    }

    private Bundle createArguments(SearchFragment.Destination destination) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.EXTRA_DESTINATION, destination);
        return bundle;
    }

    private class CheckConnectionTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            HttpUtil util = HttpUtil.getInstance();
            String cookie = util
                .setUrl(Constant.URL_MAIN)
                .connect()
                .getCookie();
            getPreferences().saveField(UserPreferences.KEY_PHPSESSID, cookie);
            return null;
        }
    }
}
