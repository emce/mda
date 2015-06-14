package mobi.cwiklinski.mda.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import mobi.cwiklinski.mda.R;

public class ActivityHelper {

    public static Bundle intentToFragmentArguments(Intent intent) {
        Bundle arguments = new Bundle();
        if (intent == null) {
            return arguments;
        }

        final Uri data = intent.getData();
        if (data != null) {
            arguments.putParcelable("_uri", data);
        }

        final String action = intent.getAction();
        if (!TextUtils.isEmpty(action)) {
            arguments.putString("_action", action);
        }

        final Bundle extras = intent.getExtras();
        if (extras != null) {
            arguments.putAll(intent.getExtras());
        }

        return arguments;
    }

    public static Intent fragmentArgumentsToIntent(Bundle arguments) {
        Intent intent = new Intent();
        if (arguments == null) {
            return intent;
        }

        final Uri data = arguments.getParcelable("_uri");
        if (data != null) {
            intent.setData(data);
        }

        final String action = arguments.getString("_action");
        if (!TextUtils.isEmpty(action)) {
            intent.setAction(action);
        }

        intent.putExtras(arguments);
        intent.removeExtra("_uri");
        intent.removeExtra("_action");
        return intent;
    }

    public static void fragmentArgumentsToIntent(Intent intent, Bundle arguments) {
        if (arguments == null) {
            return;
        }

        final Uri data = arguments.getParcelable("_uri");
        if (data != null) {
            intent.setData(data);
        }

        final String action = arguments.getString("_action");
        if (!TextUtils.isEmpty(action)) {
            intent.setAction(action);
        }

        intent.putExtras(arguments);
        intent.removeExtra("_uri");
        intent.removeExtra("_action");
    }

    public static void goTo(Context context, Class<?> cls) {
        Intent i = new Intent(context, cls);
        context.startActivity(i);
    }

    public static void goTo(Context context, Class<?> cls, Bundle extras) {
        Intent i = new Intent(context, cls);
        i.putExtras(extras);
        context.startActivity(i);
    }

    public static void goToAndClear(Activity activity, Class<?> cls) {
        Intent i = new Intent(activity, cls);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(i);
        activity.finish();
    }

    public static void goToAndClear(Activity activity, Class<?> cls, Bundle extras) {
        Intent i = new Intent(activity, cls);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtras(extras);
        activity.startActivity(i);
        activity.finish();
    }

    public static void setMenuItem(MenuItem item, int icon) {
        item.setIcon(icon);
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
    }

    public static void showMessage(Activity activity, String message) {
        final Snackbar snackbar = Snackbar.make(activity.findViewById(R.id.root), message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(activity.getResources().getColor(R.color.primary_dark));
        TextView tv = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snackbar.setAction(R.string.close, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.setActionTextColor(activity.getResources().getColor(R.color.text_accent));
        snackbar.show();
    }

    public static void showMessage(Activity activity, int messageResource) {
        showMessage(activity, activity.getString(messageResource));
    }
}
