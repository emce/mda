package mobi.cwiklinski.mda.net;


import android.os.Build;
import android.text.TextUtils;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobi.cwiklinski.mda.util.UserPreferences;


public class HttpUtil {

    public static final String TAG = HttpUtil.class.getCanonicalName();
    private Method mMethod = Method.GET;
    private String mUrl;
    private Map<String, Object> mParams = new HashMap<>();
    private String mRawResponse;
    private JSONObject mJSONObject;
    private Gson mGson;
    private String mCookie;
    private String mNewCookie;
    private static final String COOKIE_REGEX = "/.*PHPSESSID=([^;]*);.*/";
    private int mCode = 0;

    private HttpUtil() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        mGson = gsonBuilder.create();
    }

    public static HttpUtil getInstance() {
        return new HttpUtil();
    }

    public HttpUtil setMethod(Method method) {
        mMethod = method;
        return this;
    }

    public HttpUtil setUrl(String url) {
        mUrl = url;
        return this;
    }

    public HttpUtil setCookie(String cookie) {
        mCookie = cookie;
        return this;
    }

    public HttpUtil setCookieFromPreferences(UserPreferences preferences) {
        mCookie = preferences.getPhpSessionId();
        return this;
    }

    public HttpUtil setParams(Map<String, Object> params) {
        mParams = params;
        return this;
    }

    public HttpUtil connect() throws HttpRequest.HttpRequestException {
        HttpRequest request;
        switch (mMethod) {
            case POST:
                request = HttpRequest.post(mUrl);
                break;
            case DELETE:
                request = HttpRequest.delete(mUrl);
                break;
            default:
                request = HttpRequest.get(mUrl);
                break;
        }
        request.connectTimeout(10000);
        request.readTimeout(10000);
        request.header(HttpRequest.HEADER_ACCEPT, "*/*");
        request.header("API-Version", "1");
        request.header("From", Build.PRODUCT + " " + Build.MODEL);
        request.header("X-Requested-With", "XMLHttpRequest");
        request.header("Host", "rozklady.mda.malopolska.pl");
        request.header(HttpRequest.HEADER_REFERER, "http://rozklady.mda.malopolska.pl/");
        request.header("Cookie", mCookie);
        if (!mParams.isEmpty()) {
            request.form(mParams);
        }
        try {
            mRawResponse = request.body();
            mCode = request.code();
        } finally {
            request.disconnect();
        }
        List cookieList = request.headers().get("Set-Cookie");
        if (cookieList != null) {
            for (Object cookieTemp : cookieList) {
                if (cookieTemp.toString().contains("PHPSESSID")) {
                    mNewCookie = cookieTemp.toString().split("; ")[0];
                }
            }
        }
        try {
            if (mRawResponse.length() > 2 && mRawResponse.startsWith("{")) {
                mJSONObject = new JSONObject(mRawResponse);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public <T> ArrayList<T> getObjects(TypeToken token) {
        if (mRawResponse.length() > 2) {
            return mGson.fromJson(mRawResponse, token.getType());
        }
        return new ArrayList<>();
    }

    public enum Method {
        GET("GET"),
        POST("POST"),
        DELETE("DELETE");

        private String methodName;

        Method(String methodName) {
            this.methodName = methodName;
        }

        public String getMethodName() {
            return methodName;
        }
    }

    public String getCookie() {
        return mNewCookie;
    }

    public JSONObject getJSONObject() {
        return mJSONObject;
    }

    public String getResponse() {
        return mRawResponse;
    }

    public String toString() {
        String log = "Method: " + mMethod.getMethodName() + "\n";
        log += "Code: " + mCode + "\n";
        if (!mParams.isEmpty()) {
            log += "Params: " + mParams.toString() + "\n";
        }
        if (!TextUtils.isEmpty(mUrl)) {
            log += "Url: " + mUrl + "\n";
        }
        if (!TextUtils.isEmpty(mCookie)) {
            log += "Cookie: " + mCookie + "\n";
        }
        if (!TextUtils.isEmpty(mRawResponse)) {
            log += "Response: " + mRawResponse + "\n";
        }
        return log;
    }
}
