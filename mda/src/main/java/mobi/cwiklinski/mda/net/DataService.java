package mobi.cwiklinski.mda.net;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Response;

import org.joda.time.MutableDateTime;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import mobi.cwiklinski.mda.R;
import mobi.cwiklinski.mda.event.BusDataEvent;
import mobi.cwiklinski.mda.event.CarrierEvent;
import mobi.cwiklinski.mda.event.DetailsEvent;
import mobi.cwiklinski.mda.event.SearchResultEvent;
import mobi.cwiklinski.mda.model.Detail;
import mobi.cwiklinski.mda.model.Locality;
import mobi.cwiklinski.mda.model.Stage;
import mobi.cwiklinski.mda.model.TimeTable;
import mobi.cwiklinski.mda.util.Constant;
import mobi.cwiklinski.mda.util.UserPreferences;

public class DataService extends IntentService {

    public static final String TAG = DataService.class.getSimpleName();
    private UserPreferences mPreferences;

    public DataService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mPreferences = new UserPreferences(this);
        DataServiceAction action
                = (DataServiceAction) intent.getSerializableExtra(Constant.EXTRA_DATA_ACTION);
        if (intent.getExtras() != null) {
            switch (action) {
                case CHECK_CONNECTION:
                    if (HttpUtils.isConnectionAvailable(this)) {
                        try {
                            HttpUtils.get(Constant.URL_MAIN);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case SEARCH:
                    search(intent);
                    break;
                case DETAILS:
                    details(intent);
                    break;
                case BUS:
                    bus(intent);
                    break;
            }
        }
    }

    private void bus(Intent intent) {
        String params = intent.getExtras().getString(Constant.EXTRA_DATA_DATA);
        ArrayList<TimeTable> busData = new ArrayList<>();
        if (HttpUtils.isConnectionAvailable(this) && !TextUtils.isEmpty(params)) {
            try {
                Response response
                    = HttpUtils.get("http://rozklady.mda.malopolska.pl/ws/getBusSchedule.php?data=" + params);
                if (response.isSuccessful()) {
                    String responseText = response.body().string();
                    Log.i(TAG, "Response for bus: " + responseText);
                    if (!TextUtils.isEmpty(responseText)) {
                        JSONObject json = new JSONObject(responseText);
                        if (json.has("html")) {
                            Locality locality = mPreferences.getLocality();
                            Constant.Destination destination = mPreferences.getDestination();
                            Document doc = Jsoup.parse(json.getString("html"));
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
                                                        if (destination.equals(Constant.Destination.TO_CRACOW)
                                                            || destination.equals(Constant.Destination.TO_NOWY_SACZ)) {
                                                            timeTable.setCarrier(td.text().replace(locality.getName() + " ", ""));
                                                            timeTable.setStart(locality.getName());
                                                        } else {
                                                            for (Element b : td.getElementsByTag("b")) {
                                                                timeTable.setCarrier(b.text());
                                                            }
                                                            timeTable.setStart(getString(getCityResource(destination)));
                                                        }
                                                        break;
                                                    case 1:
                                                        if (destination.equals(Constant.Destination.TO_CRACOW)
                                                            || destination.equals(Constant.Destination.TO_NOWY_SACZ)) {
                                                            timeTable.setDestination(getString(getCityResource(destination)));
                                                        } else {
                                                            timeTable.setDestination(locality.getName());
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
                                        busData.add(j, timeTable);
                                        j++;
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        EventBus.getDefault().post(new BusDataEvent(busData));
    }

    private void details(Intent intent) {
        ArrayList<Stage> details = new ArrayList<>();
        Detail detail = (Detail) intent.getExtras().getSerializable(Constant.EXTRA_DATA_DATA);
        if (HttpUtils.isConnectionAvailable(this) && detail != null) {
            try {
                Response response
                    = HttpUtils.get(detail.getDetailUrl());
                if (response.isSuccessful()) {
                    String responseText = response.body().string();
                    Log.i(TAG, "Response for details: " + responseText);
                    StringBuilder carrier = new StringBuilder(getString(R.string.carrier));
                    carrier.append(": ");
                    String[] parts = responseText.split("<br />");
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
                    if (!TextUtils.isEmpty(carrier.toString())) {
                        EventBus.getDefault().post(new CarrierEvent(carrier.toString()));
                    }
                    Document doc = Jsoup.parse(responseText);
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
                                details.add(j, stage);
                                j++;
                            }
                        }

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        EventBus.getDefault().post(new DetailsEvent(details));
    }

    private void search(Intent intent) {
        String data = intent.getExtras().getString(Constant.EXTRA_DATA_DATA);
        ArrayList<Locality> result = new ArrayList<>();
        if (HttpUtils.isConnectionAvailable(this) && !TextUtils.isEmpty(data)) {
            try {
                Response response
                    = HttpUtils.get("http://rozklady.mda.malopolska.pl/ws/getCity.php?miejscowosc="
                    + data);
                if (response.isSuccessful()) {
                    String responseText = response.body().string();
                    Log.e(TAG, "Search response: " + responseText);
                    result = HttpUtils.getObjects(responseText,
                        new TypeToken<ArrayList<Locality>>() { });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        EventBus.getDefault().post(new SearchResultEvent(result));
    }

    private int getCityResource(Constant.Destination destination) {
        switch (destination) {
            case FROM_NOWY_SACZ:
            case TO_NOWY_SACZ:
                return R.string.nowysacz;
            default:
                return R.string.cracow;
        }
    }

    public enum DataServiceAction {
        CHECK_CONNECTION,
        SEARCH,
        DATE,
        DETAILS,
        BUS
    }

    public static void startCheck(Context context) {
        Intent intent = new Intent(context, DataService.class);
        intent.putExtra(Constant.EXTRA_DATA_ACTION, DataServiceAction.CHECK_CONNECTION);
        context.startService(intent);
        Log.e(TAG, "Firing connection check...");
    }

    public static void startSearch(Context context, String search) {
        Intent intent = new Intent(context, DataService.class);
        intent.putExtra(Constant.EXTRA_DATA_ACTION, DataServiceAction.SEARCH);
        intent.putExtra(Constant.EXTRA_DATA_DATA, search);
        context.startService(intent);
        Log.e(TAG, "Firing search for " + search);
    }

    public static void fetchDetails(Context context, Detail search) {
        Intent intent = new Intent(context, DataService.class);
        intent.putExtra(Constant.EXTRA_DATA_ACTION, DataServiceAction.DETAILS);
        intent.putExtra(Constant.EXTRA_DATA_DATA, search);
        context.startService(intent);
        Log.e(TAG, "Firing details fetch for " + search.getDetailUrl());
    }

    public static void fetchBus(Context context, String search) {
        Intent intent = new Intent(context, DataService.class);
        intent.putExtra(Constant.EXTRA_DATA_ACTION, DataServiceAction.BUS);
        intent.putExtra(Constant.EXTRA_DATA_DATA, search);
        context.startService(intent);
        Log.e(TAG, "Firing bus fetch for " + search);
    }
}
