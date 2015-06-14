package mobi.cwiklinski.mda.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HttpUtils {

    public static final String TAG = HttpUtils.class.getSimpleName();

    private static OkHttpClient client;
    private static CookieManager cookieManager;
    private static Gson gson;


    public static boolean isConnectionAvailable(Context context) {
        try {
            ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = conMan.getActiveNetworkInfo();
            return networkInfo.isConnected();
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean isHostReachable(String host) {
        try {
            return InetAddress.getByName(host).isReachable(1000);
        } catch (Exception e) {
            return false;
        }
    }

    public static Response post(String url, HashMap<String, String> params) throws IOException {
        FormEncodingBuilder builder = new FormEncodingBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }
        RequestBody formBody = builder.build();
        Log.e(TAG, "Requesting " + url + " with " + params.toString());
        Request request = setRequest(new Request.Builder())
            .url(url)
            .post(formBody)
            .build();
        return getClient().newCall(request).execute();
    }

    public static Response get(String url) throws IOException {
        Log.e(TAG, "Requesting " + url);
        Request request = new Request.Builder()
            .url(url)
            .build();
        return getClient().newCall(request).execute();
    }

    private static Request.Builder setRequest(Request.Builder request) {
        request.addHeader("Accept", "*/*");
        request.addHeader("Accept-Encoding", "gzip, deflate, sdch");
        request.addHeader("Accept-Language", "pl-PL,pl;q=0.8,en-US;q=0.6,en;q=0.4");
        request.addHeader("X-Requested-With", "XMLHttpRequest");
        request.addHeader("Host", "rozklady.mda.malopolska.pl");
        request.addHeader("Referer", "http://rozklady.mda.malopolska.pl/");
        request.addHeader("Connection", "Connection:keep-alive");
        return request;
    }

    public static OkHttpClient getClient() {
        if (client == null) {
            client = new OkHttpClient();
            client.setConnectTimeout(10, TimeUnit.SECONDS);
            client.setReadTimeout(10, TimeUnit.SECONDS);
            cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            CookieHandler.setDefault(cookieManager);
            client.setCookieHandler(cookieManager);
            GsonBuilder gsonBuilder = new GsonBuilder();
            gson = gsonBuilder.create();
        }
        return client;
    }

    public static <T> ArrayList<T> getObjects(String response, TypeToken token) {
        if (response.length() > 2) {
            return gson.fromJson(response, token.getType());
        }
        return new ArrayList<>();
    }
}
