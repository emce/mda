package mobi.cwiklinski.mda.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;

import com.github.kevinsawicki.http.HttpRequest;

import org.joda.time.MutableDateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.text.ParseException;
import java.util.ArrayList;

import mobi.cwiklinski.mda.R;
import mobi.cwiklinski.mda.activity.BaseActivity;
import mobi.cwiklinski.mda.adapter.StageAdapter;
import mobi.cwiklinski.mda.model.Detail;
import mobi.cwiklinski.mda.model.Locality;
import mobi.cwiklinski.mda.model.Stage;
import mobi.cwiklinski.mda.model.TimeTable;
import mobi.cwiklinski.mda.net.HttpUtil;
import mobi.cwiklinski.mda.util.Constant;

public class DetailFragment extends BaseListFragment {

    public static final String FRAGMENT_TAG = DetailFragment.class.getSimpleName();
    private Detail mDetail;
    private ArrayList<Stage> mList = new ArrayList<>();
    private FetchDetails mTask;
    private String mCarrier;

    public static DetailFragment newInstance(Intent intent) {
        DetailFragment fragment = new DetailFragment();
        fragment.setRetainInstance(true);
        fragment.setHasOptionsMenu(true);
        fragment.setArguments(BaseActivity.intentToFragmentArguments(intent));
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() == null || !getArguments().containsKey(Constant.EXTRA_DETAIL)) {
            getBaseActivity().showMessage(R.string.no_required_data);
            getActivity().finish();
        } else {
            Locality locality = getPreferences().getLocality();
            Constant.Destination destination = getPreferences().getDestination();
            TimeTable timeTable = getPreferences().getTimetable();
            StringBuilder title = new StringBuilder();
            switch (destination) {
                case FROM_CRACOW:
                    title.append("Kraków - ").append(locality.getName());
                    break;
                case TO_CRACOW:
                    title.append(locality.getName()).append(" - Kraków");
                    break;
                case FROM_NOWY_SACZ:
                    title.append("Nowy Sącz - ").append(locality.getName());
                    break;
                case TO_NOWY_SACZ:
                    title.append(locality.getName()).append(" - Nowy Sącz");
                    break;
            }
            title.append(" ")
                .append(getString(R.string.at))
                .append(" ")
                .append(Constant.TIMEDATE_FORMAT.format(timeTable.getDeparture().toDate()));
            mDetail = (Detail) getArguments().getSerializable(Constant.EXTRA_DETAIL);
            if (savedInstanceState != null && savedInstanceState.containsKey(Constant.EXTRA_STAGE_LIST)) {
                mList = (ArrayList<Stage>) savedInstanceState.getSerializable(Constant.EXTRA_STAGE_LIST);
                setListAdapter(new StageAdapter(getActivity(), mList));
                mCarrier = savedInstanceState.getString(Constant.EXTRA_CARRIER);
            }
            if (!isLoaded) {
                mTask = new FetchDetails();
                mTask.execute();
            }
            getBaseActivity().setMainTitle(title.toString());
            getBaseActivity().setSubTitle(mCarrier);
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
            } catch (HttpRequest.HttpRequestException e) {
                getBaseActivity().showMessage(R.string.connection_error);
            }
            return list;
        }

        @Override
        protected void onPostExecute(ArrayList<Stage> stages) {
            if (stages.size() > 0) {
                mList = stages;
                setListAdapter(new StageAdapter(getActivity(), mList));
                getBaseActivity().setSubTitle(mCarrier);
            } else {
                setEmptyText(getString(R.string.no_results));
            }
            setListShown(true);
        }
    }
}
