package mobi.cwiklinski.mda.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.github.kevinsawicki.http.HttpRequest;
import com.joanzapata.android.iconify.Iconify;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;

import mobi.cwiklinski.mda.R;
import mobi.cwiklinski.mda.activity.DetailActivity;
import mobi.cwiklinski.mda.adapter.TimeTableAdapter;
import mobi.cwiklinski.mda.model.Detail;
import mobi.cwiklinski.mda.model.Locality;
import mobi.cwiklinski.mda.model.TimeTable;
import mobi.cwiklinski.mda.net.HttpUtil;
import mobi.cwiklinski.mda.util.Constant;
import mobi.cwiklinski.mda.util.UserPreferences;
import mobi.cwiklinski.mda.util.Util;

public class TableFragment extends BaseFragment implements ActionMode.Callback, AdapterView.OnItemClickListener {

    public static final String FRAGMENT_TAG = TableFragment.class.getSimpleName();
    private Locality mLocality;
    private FetchBusDataTask mTask;
    private Constant.Destination mDestination;
    private DateTime mDate;
    private ArrayList<TimeTable> mTimeTables = new ArrayList<>();
    private String mMessage;
    private int currentPosition = -10;
    private ListView mListView;
    private TextView mEmptyView;
    private LinearLayout mLoader;
    private FrameLayout mContainer;

    public static TableFragment newInstance() {
        TableFragment fragment = new TableFragment();
        fragment.setRetainInstance(true);
        fragment.setHasOptionsMenu(true);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocality = getPreferences().getLocality();
        mDestination = getPreferences().getDestination();
        mDate = getPreferences().getDate();
        if (savedInstanceState != null && savedInstanceState.containsKey(Constant.EXTRA_POSITION)) {
            currentPosition = savedInstanceState.getInt(Constant.EXTRA_POSITION);
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView = (ListView) view.findViewById(android.R.id.list);
        mEmptyView = (TextView) view.findViewById(android.R.id.empty);
        mListView.setEmptyView(mEmptyView);
        mListView.setOnItemClickListener(this);
        mListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        mContainer = (FrameLayout) view.findViewById(R.id.listContainer);
        mLoader = (LinearLayout) view.findViewById(R.id.progressContainer);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(Constant.EXTRA_TIMETABLE_LIST)) {
                mTimeTables = (ArrayList<TimeTable>) savedInstanceState.getSerializable(
                    Constant.EXTRA_TIMETABLE_LIST);
                mListView.setAdapter(new TimeTableAdapter(getActivity(), mTimeTables));
            }
        }
        mListView.setDivider(null);
        if (!isLoaded) {
            setListShown(false);
            mTask = new FetchBusDataTask();
            mTask.execute();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!mTimeTables.isEmpty()) {
            outState.putSerializable(Constant.EXTRA_TIMETABLE_LIST, mTimeTables);
        }
        outState.putInt(Constant.EXTRA_POSITION, currentPosition);
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        if (actionMode != null) {
            setActionBarItem(menu.add(R.id.menu_group_main, R.id.menu_details, ++mMenuOrder,
                R.string.detail_title), Iconify.IconValue.fa_list_alt);
            setActionBarItem(menu.add(R.id.menu_group_main, R.id.menu_sms, ++mMenuOrder,
                R.string.menu_sms), Iconify.IconValue.fa_envelope);
            return true;
        }
        return false;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem item) {
        if (currentPosition >= 0) {
            TimeTable timeTable = mTimeTables.get(currentPosition);
            if (item != null && timeTable != null) {
                switch (item.getItemId()) {
                    case R.id.menu_details:
                        Intent intent = new Intent(getActivity(), DetailActivity.class);
                        intent.putExtra(Constant.EXTRA_DETAIL, timeTable.getDetail());
                        startActivity(intent);
                        if (actionMode != null) {
                            actionMode.finish();
                        }
                        return true;
                    case R.id.menu_sms:
                        startActivity(Util.sendSms(Util.generateSmsBody(getActivity(), timeTable)));
                        if (actionMode != null) {
                            actionMode.finish();
                        }
                        return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        currentPosition = -10;
    }

    private String prepareTimeTableUrl() {
        JSONObject object = new JSONObject();
        try {
            object.put("cityId", mLocality.getId());
            object.put("miejscowosc", mLocality.getName());
            object.put("woj", mLocality.getProvince());
            object.put("powiat", mLocality.getDistrict());
            object.put("gmina", mLocality.getCommunity());
            object.put("k", mDestination.getId() + 1);
            if (mDestination.getId() > 1) {
                object.put("mstart", "NOWY SĄCZ");
            } else {
                object.put("mstart", "KRAKÓW");
            }
            object.put("data", mDate.toDateTime());
            object.put("godz", mDate.toDateTime());
            return URLEncoder.encode(object.toString(), "utf-8");
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    private int getCityResource() {
        switch (mDestination) {
            case FROM_NOWY_SACZ:
            case TO_NOWY_SACZ:
                return R.string.nowysacz;
            default:
                return R.string.cracow;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        currentPosition = position;
        TimeTable timeTable = mTimeTables.get(currentPosition);
        getPreferences().saveTimetable(timeTable);
        if (mActionMode != null) {
            mActionMode.finish();
        }
        if (getBaseActivity() != null) {
            mActionMode = getBaseActivity().startActionMode(this);
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.list;
    }

    private void setListShown(boolean shown) {
        mContainer.setVisibility(shown ? View.VISIBLE : View.GONE);
        mLoader.setVisibility(shown ? View.GONE : View.VISIBLE);
    }

    private class FetchBusDataTask extends AsyncTask<Void, Void, ArrayList<TimeTable>> {

        @Override
        protected ArrayList<TimeTable> doInBackground(Void... params) {
            ArrayList<TimeTable> list = new ArrayList<>();
            HttpUtil util = HttpUtil.getInstance();
            try {
                util
                    .setUrl("http://rozklady.mda.malopolska.pl/ws/getBusSchedule.php?data="
                        + prepareTimeTableUrl())
                    .setCookieFromPreferences(new UserPreferences(getActivity()))
                    .connect();
                if (util.getJSONObject() != null && util.getJSONObject().has("html")) {
                    try {
                        Document doc = Jsoup.parse(util.getJSONObject().getString("html"));
                        Element table = doc.getElementById("rozklad");
                        int i = 0;
                        int j = 0;
                        if (table != null) {
                            for (Element tr : table.getElementsByTag("tr")) {
                                if (tr.getElementsByTag("td").size() > 0) {
                                    TimeTable timeTable = new TimeTable();
                                    for (Element td : tr.getElementsByTag("td")) {
                                        try {
                                            switch (i) {
                                                case 0:
                                                    if (mDestination.equals(Constant.Destination.TO_CRACOW)
                                                        || mDestination.equals(Constant.Destination.TO_NOWY_SACZ)) {
                                                        timeTable.setCarrier(td.text().replace(mLocality.getName() + " ", ""));
                                                        timeTable.setStart(mLocality.getName());
                                                    } else {
                                                        for (Element b : td.getElementsByTag("b")) {
                                                            timeTable.setCarrier(b.text());
                                                        }
                                                        timeTable.setStart(getString(getCityResource()));
                                                    }
                                                    break;
                                                case 1:
                                                    if (mDestination.equals(Constant.Destination.TO_CRACOW)
                                                        || mDestination.equals(Constant.Destination.TO_NOWY_SACZ)) {
                                                        timeTable.setDestination(getString(getCityResource()));
                                                    } else {
                                                        timeTable.setDestination(mLocality.getName());
                                                    }
                                                    break;
                                                case 2:
                                                    timeTable.setDeparture(new MutableDateTime(
                                                        Constant.FULL_DATETIME_FORMAT.parse(td.text())).toDateTime());
                                                    break;
                                                case 3:
                                                    timeTable.setArrival(new MutableDateTime(
                                                        Constant.FULL_DATETIME_FORMAT.parse(td.text())).toDateTime());
                                                    break;
                                                case 4:
                                                    timeTable.setLength(td.text());
                                                    break;
                                                case 5:
                                                    timeTable.setPrice(Double.parseDouble(td.text().replace(",", ".")));
                                                    break;
                                                case 6:
                                                    timeTable.setTickets(td.text());
                                                    break;
                                                case 7:
                                                    String url = td.getElementsByTag("a").attr("id");
                                                    Detail detail = Detail.parseFromString(url);
                                                    timeTable.setDetail(detail);
                                                    break;
                                            }
                                        } catch (IllegalArgumentException | ParseException e) {
                                            e.printStackTrace();
                                        }
                                        i++;
                                    }
                                    i = 0;
                                    list.add(j, timeTable);
                                    j++;
                                }
                            }
                        } else {
                            mMessage = doc.getElementsByTag("p").text();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException | HttpRequest.HttpRequestException e) {
                notifyConnectionError();
            }
            return list;
        }

        @Override
        protected void onPostExecute(ArrayList<TimeTable> timeTables) {
            if (timeTables.size() > 0) {
                mTimeTables = timeTables;
                mListView.setAdapter(new TimeTableAdapter(getActivity(), mTimeTables));
            } else {
                if (!TextUtils.isEmpty(mMessage)) {
                    mEmptyView.setText(mMessage);
                    mMessage = null;
                }
            }
            setListShown(true);
        }
    }
}
