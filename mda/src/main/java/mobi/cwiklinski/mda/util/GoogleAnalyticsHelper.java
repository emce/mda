package mobi.cwiklinski.mda.util;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.ExceptionParser;
import com.google.analytics.tracking.android.ExceptionReporter;
import com.google.analytics.tracking.android.GAServiceManager;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

public class GoogleAnalyticsHelper {

    public static void activityStart(Activity activity) {
        if (0 != (activity.getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE)) {
            EasyTracker.getInstance().activityStart(activity);
            setExceptionHandler(new DefaultExceptionParser(activity.getApplicationContext()),
                false, activity);
        }
    }

    public static void serviceStart(Context context) {
        serviceStart(context, false);
    }

    public static void serviceStart(Context context, boolean consumeCrash) {
        EasyTracker.getInstance().setContext(context.getApplicationContext());
        setExceptionHandler(
            new DefaultExceptionParser(
                context.getApplicationContext()),
            consumeCrash,
            context);
    }

    private static void setExceptionHandler(ExceptionParser parser, boolean consumeCrash, Context context) {
        ExceptionReporter exceptionReporter = new ExceptionReporter(
            EasyTracker.getTracker(),
            GAServiceManager.getInstance(),
            consumeCrash ? null : Thread.getDefaultUncaughtExceptionHandler(),
            context);
        exceptionReporter.setExceptionParser(parser);
        Thread.currentThread().setUncaughtExceptionHandler(exceptionReporter);
    }


    public static abstract class CustomExceptionParser implements ExceptionParser {

        protected final Context mContext;

        public CustomExceptionParser(Context context) {
            this.mContext = context;
        }

        @Override
        public String getDescription(final String threadName, final Throwable t) {
            String result = getStackTraceAsString(t);
            EasyTracker.getTracker().sendEvent(Categories.EXCEPTIONS, result, getDetailedDescription().toString(), null);
            return result;
        }

        protected abstract StringBuilder getDetailedDescription();
    }

    public static class DefaultExceptionParser extends CustomExceptionParser {

        public DefaultExceptionParser(Context context) {
            super(context);
        }

        @Override
        protected StringBuilder getDetailedDescription() {
            return GoogleAnalyticsHelper.getDeviceAndUserInfo(mContext);
        }
    }

    public static class ConstraintExceptionParser extends DefaultExceptionParser {

        private ContentValues values;

        public ConstraintExceptionParser(Context context, ContentValues values) {
            super(context);
            this.values = values;
        }

        @Override
        protected StringBuilder getDetailedDescription() {
            StringBuilder builder = super.getDetailedDescription();
            builder.append("ContentValues: ");
            for (Map.Entry<String, Object> key : values.valueSet()) {
                builder.append(key.getKey());
                builder.append(" => ");
                builder.append(key.getValue());
                builder.append(", ");
            }
            return builder;
        }
    }

    public static void trackError(Context context, String category, Exception e) {
        EasyTracker.getInstance().setContext(context);
        EasyTracker.getTracker().sendEvent(category, getStackTraceAsString(e), getDeviceAndUserInfo(context).toString(), null);
    }

    static StringBuilder getDeviceAndUserInfo(Context context) {
        StringBuilder builder = new StringBuilder();
        builder.append("\nDevice: ");
        builder.append(android.os.Build.MODEL);
        builder.append("\nAPI Level: ");
        builder.append(android.os.Build.VERSION.SDK_INT);
        builder.append("\nBuild: ");
        builder.append(android.os.Build.DISPLAY);
        return builder;
    }

    public static String getStackTraceAsString(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }


    public interface Categories {
        String EXCEPTIONS = "exceptions";
    }

    public interface Actions {

    }

    public interface Labels {

    }

}
