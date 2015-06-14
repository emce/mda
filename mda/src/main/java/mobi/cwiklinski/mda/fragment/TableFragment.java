package mobi.cwiklinski.mda.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import mobi.cwiklinski.mda.R;
import mobi.cwiklinski.mda.activity.DetailActivity;
import mobi.cwiklinski.mda.adapter.TimeTableAdapter;
import mobi.cwiklinski.mda.event.BusDataEvent;
import mobi.cwiklinski.mda.model.Locality;
import mobi.cwiklinski.mda.model.TimeTable;
import mobi.cwiklinski.mda.net.DataService;
import mobi.cwiklinski.mda.util.ActivityHelper;
import mobi.cwiklinski.mda.util.Constant;
import mobi.cwiklinski.mda.util.Util;

public class TableFragment extends AbstractFragment implements AdapterView.OnItemClickListener {

    public static final String FRAGMENT_TAG = TableFragment.class.getSimpleName();
    private Locality mLocality;
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
        View view = inflater.inflate(R.layout.list, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView = (ListView) view.findViewById(android.R.id.list);
        mEmptyView = (TextView) view.findViewById(android.R.id.empty);
        mListView.setEmptyView(mEmptyView);
        mListView.setOnItemClickListener(this);
        mListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        mContainer = (FrameLayout) view.findViewById(R.id.list_container);
        mLoader = (LinearLayout) view.findViewById(R.id.progress_container);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView.setDivider(getResources().getDrawable(R.drawable.divider));
        mListView.setDividerHeight(getResources().getDimensionPixelSize(R.dimen.one));
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(Constant.EXTRA_TIMETABLE_LIST)) {
                mTimeTables = (ArrayList<TimeTable>) savedInstanceState.getSerializable(
                    Constant.EXTRA_TIMETABLE_LIST);
                mListView.setAdapter(new TimeTableAdapter(getActivity(), mTimeTables));
            }
        }
        if (!isLoaded) {
            setListShown(false);
            DataService.fetchBus(getActivity(), prepareTimeTableUrl());
        }
        registerForContextMenu(mListView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == android.R.id.list) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            TimeTable timeTable = mTimeTables.get(info.position);
            menu.setHeaderTitle(timeTable.getStart() + " "
                + Constant.TIME_FORMAT.format(timeTable.getDeparture().toDate())
                + " -> " + timeTable.getDestination() + " "
                + Constant.TIME_FORMAT.format(timeTable.getArrival().toDate()));
            ActivityHelper.setMenuItem(menu.add(R.id.menu_group_main,
                R.id.menu_details, ++mMenuOrder, R.string.detail_title), R.drawable.ic_menu_details);
            ActivityHelper.setMenuItem(menu.add(R.id.menu_group_main,
                R.id.menu_sms, ++mMenuOrder, R.string.menu_sms), R.drawable.ic_menu_message);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.menu_details:
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(Constant.EXTRA_DETAIL, mTimeTables.get(info.position).getDetail());
                startActivity(intent);
                return true;
            case R.id.menu_sms:
                startActivity(Util.sendSms(Util.generateSmsBody(getActivity(), mTimeTables.get(info.position))));
                return true;
            default:
                return super.onContextItemSelected(item);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra(Constant.EXTRA_DETAIL, mTimeTables.get(position).getDetail());
        startActivity(intent);
    }

    private void setListShown(boolean shown) {
        mContainer.setVisibility(shown ? View.VISIBLE : View.GONE);
        mLoader.setVisibility(shown ? View.GONE : View.VISIBLE);
    }

    public void onEventMainThread(BusDataEvent event) {
        if (event.getBusData().size() > 0) {
            mTimeTables = event.getBusData();
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
