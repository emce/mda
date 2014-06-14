package mobi.cwiklinski.mda.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.github.kevinsawicki.http.HttpRequest;
import com.joanzapata.android.iconify.Iconify;

import org.joda.time.MutableDateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;

import mobi.cwiklinski.mda.R;
import mobi.cwiklinski.mda.activity.BaseActivity;
import mobi.cwiklinski.mda.adapter.StageAdapter;
import mobi.cwiklinski.mda.model.Detail;
import mobi.cwiklinski.mda.model.Stage;
import mobi.cwiklinski.mda.model.TimeTable;
import mobi.cwiklinski.mda.net.HttpUtil;
import mobi.cwiklinski.mda.util.Constant;
import mobi.cwiklinski.mda.util.TypefaceManager;
import mobi.cwiklinski.mda.util.Util;

public class DetailFragment extends BaseFragment {

    public static final String FRAGMENT_TAG = DetailFragment.class.getSimpleName();
    private Detail mDetail;
    private ArrayList<Stage> mList = new ArrayList<>();
    private FetchDetails mTask;
    private String mCarrier;
    private ListView mListView;
    private TextView mEmptyView;
    private LinearLayout mLoader;
    private FrameLayout mContainer;

    public static DetailFragment newInstance(Intent intent) {
        DetailFragment fragment = new DetailFragment();
        fragment.setRetainInstance(true);
        fragment.setHasOptionsMenu(true);
        fragment.setArguments(BaseActivity.intentToFragmentArguments(intent));
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView = (ListView) view.findViewById(android.R.id.list);
        mEmptyView = (TextView) view.findViewById(android.R.id.empty);
        mListView.setEmptyView(mEmptyView);
        mListView.setChoiceMode(AbsListView.CHOICE_MODE_NONE);
        mContainer = (FrameLayout) view.findViewById(R.id.listContainer);
        mLoader = (LinearLayout) view.findViewById(R.id.progressContainer);


        TextView arrow = (TextView) view.findViewById(R.id.tt_arrow);
        arrow.setText(getResources().getBoolean(R.bool.isLandscape) ?
            R.string.icon_arrow_bottom : R.string.icon_arrow_right);
        TextView bus = (TextView) view.findViewById(R.id.tt_bus);
        bus.setText(R.string.icon_bus);
        TextView clock = (TextView) view.findViewById(R.id.tt_clock);
        clock.setText(R.string.icon_clock);
        TextView money = (TextView) view.findViewById(R.id.tt_money);
        money.setText(R.string.icon_money);
        TextView ticket = (TextView) view.findViewById(R.id.tt_ticket);
        ticket.setText(R.string.icon_ticket);
        TextView start = (TextView) view.findViewById(R.id.tt_start);
        TextView startCity = (TextView) view.findViewById(R.id.tt_start_city);
        TextView destination = (TextView) view.findViewById(R.id.tt_destination);
        TextView destinationCity = (TextView) view.findViewById(R.id.tt_destination_city);
        TextView length = (TextView) view.findViewById(R.id.tt_length);
        TextView price = (TextView) view.findViewById(R.id.tt_price);
        TextView tickets = (TextView) view.findViewById(R.id.tt_tickets);
        TextView carrier = (TextView) view.findViewById(R.id.tt_carrier);
        start.setTypeface(
            getTypefaceManager().getTypeface(TypefaceManager.FontFace.ROBOTO_BOLD));
        destination.setTypeface(
            getTypefaceManager().getTypeface(TypefaceManager.FontFace.ROBOTO_BOLD));
        Iconify.addIcons(arrow, money, clock, bus, ticket);

        TimeTable item = getPreferences().getTimetable();
        if (item != null) {
            if (item.getDeparture() != null) {
                start.setText(Constant.TIME_FORMAT.format(item.getDeparture().toDate()));
            }
            startCity.setText(item.getStart());
            if (item.getArrival() != null) {
                destination.setText(Constant.TIME_FORMAT.format(item.getArrival().toDate()));
            }
            destinationCity.setText(item.getDestination());
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
                            length.setText(len);
                        } else {
                            length.setText(item.getLength());
                        }
                    } catch (Exception e) {
                        length.setText(item.getLength());
                    }
                } else {
                    length.setText(item.getLength());
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
            getBaseActivity().showMessage(R.string.no_required_data);
            getActivity().finish();
        } else {
            mDetail = (Detail) getArguments().getSerializable(Constant.EXTRA_DETAIL);
            if (savedInstanceState != null && savedInstanceState.containsKey(Constant.EXTRA_STAGE_LIST)) {
                mList = (ArrayList<Stage>) savedInstanceState.getSerializable(Constant.EXTRA_STAGE_LIST);
                mListView.setAdapter(new StageAdapter(getActivity(), mList));
                mCarrier = savedInstanceState.getString(Constant.EXTRA_CARRIER);
            }
            if (!isLoaded) {
                setListShown(false);
                mTask = new FetchDetails();
                mTask.execute();
            }
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
        outState.putSerializable(Constant.EXTRA_STAGE_LIST, mList);
        outState.putString(Constant.EXTRA_CARRIER, mCarrier);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.detail;
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        setActionBarItem(menu.add(R.id.menu_group_main, R.id.menu_sms, ++mMenuOrder,
            R.string.menu_sms), Iconify.IconValue.fa_envelope);
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

    private class FetchDetails extends AsyncTask<Void, Void, ArrayList<Stage>> {

        @Override
        protected ArrayList<Stage> doInBackground(Void... params) {
            ArrayList<Stage> list = new ArrayList<>();
            HttpUtil util = HttpUtil.getInstance();
            try {
                util
                    .setUrl(mDetail.getDetailUrl())
                    .setCookieFromPreferences(getPreferences())
                    .connect();
                if (!TextUtils.isEmpty(util.getResponse())) {
                    StringBuilder carrier = new StringBuilder(getString(R.string.carrier));
                    carrier.append(": ");
                    String[] parts = util.getResponse().split("<br />");
                    if (parts.length > 0) {
                        String carrierName = Jsoup.parse(parts[0]).text();
                        if (carrierName.contains(":")) {
                            String[] carriers = carrierName.split(":");
                            carrier.append(carriers[1].trim());
                        } else {
                            carrier.append(getString(R.string.no_data));
                        }
                    } else {
                        carrier.append(getString(R.string.no_data));
                    }
                    mCarrier = carrier.toString();
                    Document doc = Jsoup.parse(util.getResponse());
                    Element table = doc.getElementById("przystanki");
                    int i = 0;
                    int j = 0;
                    if (table != null) {
                        for (Element tr : table.getElementsByTag("tr")) {
                            if (tr.getElementsByTag("td").size() > 0) {
                                Stage stage = new Stage();
                                for (Element td : tr.getElementsByTag("td")) {
                                    try {
                                        switch (i) {
                                            case 0:
                                                stage.setDestination(td.text());
                                                break;
                                            case 1:
                                                stage.setStation(td.text());
                                                break;
                                            case 2:
                                                stage.setArrival(new MutableDateTime(
                                                    Constant.FULL_DATETIME_FORMAT.parse(td.text())).toDateTime());
                                                break;
                                            case 3:
                                                stage.setPrice(td.text());
                                                break;
                                        }
                                    } catch (IllegalArgumentException | ParseException e) {
                                        e.printStackTrace();
                                    }
                                    i++;
                                }
                                i = 0;
                                list.add(j, stage);
                                j++;
                            }
                        }

                    }
                }
            } catch (IOException | HttpRequest.HttpRequestException e) {
                notifyConnectionError();
            }
            return list;
        }

        @Override
        protected void onPostExecute(ArrayList<Stage> stages) {
            if (stages.size() > 0) {
                mList = stages;
                mListView.setAdapter(new StageAdapter(getActivity(), mList));
            } else {
                mEmptyView.setText(R.string.no_results);
            }
            setListShown(true);
        }
    }
}
