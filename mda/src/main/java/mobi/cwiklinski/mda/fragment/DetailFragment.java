package mobi.cwiklinski.mda.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import mobi.cwiklinski.mda.R;
import mobi.cwiklinski.mda.adapter.StageAdapter;
import mobi.cwiklinski.mda.event.CarrierEvent;
import mobi.cwiklinski.mda.event.DetailsEvent;
import mobi.cwiklinski.mda.model.Detail;
import mobi.cwiklinski.mda.model.Stage;
import mobi.cwiklinski.mda.model.TimeTable;
import mobi.cwiklinski.mda.net.DataService;
import mobi.cwiklinski.mda.util.ActivityHelper;
import mobi.cwiklinski.mda.util.Constant;
import mobi.cwiklinski.mda.util.Util;

public class DetailFragment extends AbstractFragment {

    public static final String FRAGMENT_TAG = DetailFragment.class.getSimpleName();
    private ArrayList<Stage> mList = new ArrayList<>();
    private String mCarrier;
    @InjectView(android.R.id.list) ListView mListView;
    @InjectView(android.R.id.empty) TextView mEmptyView;
    @InjectView(R.id.progress_container) LinearLayout mLoader;
    @InjectView(R.id.list_container) FrameLayout mContainer;

    @InjectView(R.id.detail_duration) TextView duration;
    @InjectView(R.id.detail_price) TextView price;
    @InjectView(R.id.detail_tickets) TextView tickets;
    @InjectView(R.id.detail_carrier) TextView carrier;

    public static DetailFragment newInstance(Intent intent) {
        DetailFragment fragment = new DetailFragment();
        fragment.setRetainInstance(true);
        fragment.setHasOptionsMenu(true);
        fragment.setArguments(ActivityHelper.intentToFragmentArguments(intent));
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView.setEmptyView(mEmptyView);
        mListView.setChoiceMode(AbsListView.CHOICE_MODE_NONE);

        TimeTable item = getPreferences().getTimetable();
        if (item != null) {
            if (!TextUtils.isEmpty(item.getLength())) {
                if (item.getLength().contains(":")) {
                    try {
                        String[] parts = item.getLength().split(":");
                        if (parts.length > 1) {
                            int hours = Integer.parseInt(parts[0]);
                            int minutes = Integer.parseInt(parts[1]);
                            String len = getResources().getQuantityString(R.plurals.hours, hours, hours);
                            if (minutes > 0) {
                                len += " " + getResources().getQuantityString(R.plurals.minutes, minutes, minutes);
                            }
                            duration.setText(len);
                        } else {
                            duration.setText(item.getLength());
                        }
                    } catch (Exception e) {
                        duration.setText(item.getLength());
                    }
                } else {
                    duration.setText(item.getLength());
                }
            }
            if (item.getPrice() != null) {
                if (item.getPrice() > 0) {
                    price.setText(NumberFormat.getCurrencyInstance().format(item.getPrice()));
                } else {
                    price.setText(R.string.no_data);
                }
            }
            if (!TextUtils.isEmpty(item.getTickets())) {
                try {
                    int ticketsAmount = Integer.parseInt(item.getTickets());
                    tickets.setText(
                        getResources().getQuantityString(R.plurals.tickets, ticketsAmount, ticketsAmount));
                } catch (NumberFormatException e) {
                    tickets.setText(item.getTickets());
                }
            }
            if (!TextUtils.isEmpty(item.getCarrier())) {
                carrier.setText(item.getCarrier());
            }
        } else {
            view.findViewById(R.id.detail_description).setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() == null || !getArguments().containsKey(Constant.EXTRA_DETAIL)) {
            ActivityHelper.showMessage(getActivity(), R.string.no_required_data);
            getActivity().finish();
        } else {
            Detail detail = (Detail) getArguments().getSerializable(Constant.EXTRA_DETAIL);
            if (savedInstanceState != null && savedInstanceState.containsKey(Constant.EXTRA_STAGE_LIST)) {
                mList = (ArrayList<Stage>) savedInstanceState.getSerializable(Constant.EXTRA_STAGE_LIST);
                mListView.setAdapter(new StageAdapter(getActivity(), mList));
                mCarrier = savedInstanceState.getString(Constant.EXTRA_CARRIER);
            }
            if (!isLoaded) {
                setListShown(false);
                DataService.fetchDetails(getActivity(), detail);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(Constant.EXTRA_STAGE_LIST, mList);
        outState.putString(Constant.EXTRA_CARRIER, mCarrier);
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        ActivityHelper.setMenuItem(menu.add(R.id.menu_group_main, R.id.menu_sms, ++mMenuOrder,
            R.string.menu_sms), R.drawable.ic_menu_message);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sms:
                TimeTable timeTable = getPreferences().getTimetable();
                if (timeTable != null) {
                    startActivity(Util.sendSms(Util.generateSmsBody(getActivity(), timeTable)));
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setListShown(boolean shown) {
        mContainer.setVisibility(shown ? View.VISIBLE : View.GONE);
        mLoader.setVisibility(shown ? View.GONE : View.VISIBLE);
    }

    public void onEventMainThread(CarrierEvent event) {
        mCarrier = event.getCarrier();
    }

    public void onEventMainThread(DetailsEvent event) {
        if (event.getDetails().size() > 0) {
            mList = event.getDetails();
            mListView.setAdapter(new StageAdapter(getActivity(), mList));
        } else {
            mEmptyView.setText(R.string.no_results);
        }
        setListShown(true);
    }
}
