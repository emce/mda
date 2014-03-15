package mobi.cwiklinski.mda.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.github.kevinsawicki.http.HttpRequest;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

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

public class TableFragment extends BaseListFragment {

    public static final String FRAGMENT_TAG = TableFragment.class.getSimpleName();
    private Locality mLocality;
    private FetchBusDataTask mTask;
    private Constant.Destination mDestination;
    private DateTime mDate;
    private ArrayList<TimeTable> mTimeTables = new ArrayList<>();
    private String mMessage;

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
            String subTitle = Constant.DATETIME_FORMAT.format(mDate.toDate());
            subTitle += ", " + mLocality.toLocalizedString(getResources());
            getBaseActivity().setSubTitle(subTitle);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(Constant.EXTRA_TIMETABLE_LIST)) {
                mTimeTables = (ArrayList<TimeTable>) savedInstanceState.getSerializable(
                    Constant.EXTRA_TIMETABLE_LIST);
                getListView().setAdapter(new TimeTableAdapter(getActivity(), mTimeTables));
            }
        }
        setEmptyText(getString(R.string.no_results));
        if (!isLoaded) {
            mTask = new FetchBusDataTask();
            mTask.execute();
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        TimeTable item = mTimeTables.get(position);
        if (item != null) {
            getPreferences().saveTimetable(item);
            Intent intent = new Intent(getActivity(), DetailActivity.class);
            intent.putExtra(Constant.EXTRA_DETAIL, item.getDetail());
            startActivity(intent);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!mTimeTables.isEmpty()) {
            outState.putSerializable(Constant.EXTRA_TIMETABLE_LIST, mTimeTables);
        }
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
                                        Log.d(FRAGMENT_TAG, "td (" + i + ") :" + td.text());
                                        try {
                                            switch (i) {
                                                case 0:
                                                    Log.e(FRAGMENT_TAG, "start: " + td.text());
                                                    timeTable.setStart(td.text());
                                                    break;
                                                case 1:
                                                    timeTable.setDestination(td.text());
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
            } catch (HttpRequest.HttpRequestException e) {
                getBaseActivity().showMessage(R.string.connection_error);
            }
            return list;
        }

        @Override
        protected void onPostExecute(ArrayList<TimeTable> timeTables) {
            setEmptyText(getString(R.string.no_results));
            if (timeTables.size() > 0) {
                mTimeTables = timeTables;
                setListAdapter(new TimeTableAdapter(getActivity(), mTimeTables));
            } else {
                if (!TextUtils.isEmpty(mMessage)) {
                    setEmptyText(mMessage);
                    mMessage = null;
                }
            }
            setListShown(true);
        }
    }
}
