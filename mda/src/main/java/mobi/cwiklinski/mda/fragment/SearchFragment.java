package mobi.cwiklinski.mda.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TimePicker;

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
import java.util.List;

import mobi.cwiklinski.mda.R;
import mobi.cwiklinski.mda.adapter.LocalityRemoteAdapter;
import mobi.cwiklinski.mda.adapter.TimeTableAdapter;
import mobi.cwiklinski.mda.model.Locality;
import mobi.cwiklinski.mda.model.TimeTable;
import mobi.cwiklinski.mda.net.HttpUtil;
import mobi.cwiklinski.mda.util.Constant;
import mobi.cwiklinski.mda.util.UserPreferences;
import mobi.cwiklinski.mda.util.Util;
import mobi.cwiklinski.typiconic.Typiconify;

public class SearchFragment extends BaseFragment implements DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    public static final String FRAGMENT_TAG = SearchFragment.class.getSimpleName();
    private AutoCompleteTextView mSuggest;
    private Button mDatePicker;
    private Button mTimePicker;
    private LinearLayout mDateContainer;
    private LinearLayout mTimeTableContainer;
    private ListView mList;
    private MutableDateTime mDate = new DateTime().toMutableDateTime();
    private Locality mCurrentLocality;
    private FetchBusDataTask mTask;
    private Destination mDestination = Destination.FROM_CRACOW;

    public static SearchFragment newInstance(Bundle args) {
        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(args);
        fragment.setHasOptionsMenu(true);
        return fragment;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mTask != null) {
            mTask.cancel(true);
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
        mDatePicker = (Button) view.findViewById(R.id.search_date);
        mTimePicker = (Button) view.findViewById(R.id.search_time);
        mDateContainer = (LinearLayout) view.findViewById(R.id.search_date_container);
        mTimeTableContainer = (LinearLayout) view.findViewById(R.id.search_list_container);
        mList = (ListView) view.findViewById(R.id.search_timetable);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(Constant.EXTRA_DESTINATION)) {
            mDestination = (Destination) getArguments().getSerializable(Constant.EXTRA_DESTINATION);
        }
        switch (mDestination) {
            case FROM_CRACOW:
                getActivity().setTitle(R.string.choose_from_cracow_button);
                break;
            case TO_CRACOW:
                getActivity().setTitle(R.string.choose_to_cracow_button);
                break;
            case FROM_NOWY_SACZ:
                getActivity().setTitle(R.string.choose_from_nowysacz_button);
                break;
            case TO_NOWY_SACZ:
                getActivity().setTitle(R.string.choose_to_nowysacz_button);
                break;
        }
        final DatePickerDialog dateDialog = new DatePickerDialog(getActivity(), this,
            mDate.getYear(), mDate.getMonthOfYear() - 1, mDate.getDayOfMonth());
        dateDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(android.R.string.ok),
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }
        );
        final TimePickerDialog timeDialog = new TimePickerDialog(getActivity(), this,
            mDate.getHourOfDay(), mDate.getMinuteOfHour(), true);
        timeDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(android.R.string.ok),
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }
        );
        mSuggest.setAdapter(new LocalityRemoteAdapter(getActivity(), new ArrayList<Locality>()));
        mSuggest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view != null && view.getTag() != null) {
                    mCurrentLocality = (Locality) view.getTag();
                    mSuggest.setText(mCurrentLocality.getName());
                    mDateContainer.setVisibility(View.VISIBLE);
                }
            }
        });
        mDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateDialog.show();
            }
        });
        mTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeDialog.show();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (mDate.isAfterNow()) {
            addMenuItem(menu, R.id.menu_search_item, R.string.app_name, Typiconify.IconValue.ti_zoom,
                MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search_item:
                mTask = new FetchBusDataTask();
                mTask.execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        mDate.setYear(year);
        mDate.setMonthOfYear(monthOfYear + 1);
        mDate.setDayOfMonth(dayOfMonth);
        mDatePicker.setText(Util.DATE_FORMAT.format(mDate.toDate()));
        if (TextUtils.isEmpty(mTimePicker.getText())) {
            mTimePicker.setText(Util.TIME_FORMAT.format(mDate.toDate()));
        }
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mDate.setHourOfDay(hourOfDay);
        mDate.setMinuteOfHour(minute);
        mTimePicker.setText(Util.TIME_FORMAT.format(mDate.toDate()));
        if (TextUtils.isEmpty(mDatePicker.getText())) {
            mDatePicker.setText(Util.DATE_FORMAT.format(mDate.toDate()));
        }
        getActivity().invalidateOptionsMenu();
    }
    
    private String prepareTimeTableUrl() {
        JSONObject object = new JSONObject();
        try {
            object.put("cityId", mCurrentLocality.getId());
            object.put("miejscowosc", mCurrentLocality.getName());
            object.put("woj", mCurrentLocality.getProvince());
            object.put("powiat", mCurrentLocality.getDistrict());
            object.put("gmina", mCurrentLocality.getCommunity());
            object.put("k", mDestination.getId());
            object.put("mstart", "KRAKÓW");
            object.put("data", mDatePicker.getText().toString());
            object.put("godz", mTimePicker.getText().toString());
            return URLEncoder.encode(object.toString(), "utf-8");
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    private class FetchBusDataTask extends AsyncTask<Void, Void, List<TimeTable>> {

        @Override
        protected List<TimeTable> doInBackground(Void... params) {
            ArrayList<TimeTable> list = new ArrayList<>();
            HttpUtil util = HttpUtil.getInstance();
            util
                .setUrl("http://rozklady.mda.malopolska.pl/ws/getBusSchedule.php?data="
                    + prepareTimeTableUrl())
                .setCookieFromPreferences(new UserPreferences(getActivity()))
                .connect();
            if (util.getJSONObject() != null && util.getJSONObject().has("html")) {
                try {
                    Document doc = Jsoup.parse(util.getJSONObject().getString("html"));
                    Log.e(FRAGMENT_TAG, doc.normalise().toString());
                    Element table = doc.getElementById("rozklad");
                    int i = 0;
                    int j = 0;
                    for (Element tr : table.getElementsByTag("tr")) {
                        if (tr.getElementsByTag("td").size() > 0) {
                            TimeTable timeTable = new TimeTable();
                            for (Element td : tr.getElementsByTag("td")) {
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
                                                Util.FULL_DATETIME_FORMAT.parse(td.text())));
                                            break;
                                        case 3:
                                            timeTable.setArrival(new MutableDateTime(
                                                Util.FULL_DATETIME_FORMAT.parse(td.text())));
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
                                            timeTable.setDescription(td.text());
                                            break;
                                    }
                                } catch (IllegalArgumentException | ParseException e) {
                                    e.printStackTrace();
                                }
                                i++;
                            }
                            list.add(j, timeTable);
                            j++;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<TimeTable> timeTables) {
            if (timeTables.size() > 0) {
                mTimeTableContainer.setVisibility(View.VISIBLE);
                mList.setAdapter(new TimeTableAdapter(getActivity(), timeTables));
            }
        }
    }

    public enum Destination {
        FROM_CRACOW(1),
        TO_CRACOW(2),
        FROM_NOWY_SACZ(3),
        TO_NOWY_SACZ(4);

        int id;

        Destination(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }
}
