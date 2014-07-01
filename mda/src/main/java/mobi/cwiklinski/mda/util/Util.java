package mobi.cwiklinski.mda.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;

import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

import java.text.NumberFormat;

import mobi.cwiklinski.mda.BuildConfig;
import mobi.cwiklinski.mda.R;
import mobi.cwiklinski.mda.model.TimeTable;

public class Util {

    public static Intent sendSms(String body) {
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setData(Uri.parse("sms:"));
        sendIntent.putExtra("sms_body", body);
        return sendIntent;
    }

    public static Drawable fontToDrawable(Context context, Iconify.IconValue icon, int color, int size) {
        return new IconDrawable(context, icon)
            .colorRes(color)
            .sizeRes(size);
    }

    public static String generateSmsBody(Context context, TimeTable timeTable) {
        StringBuilder sms = new StringBuilder();
        sms.append(timeTable.getStart());
        if (timeTable.getDeparture() != null) {
            sms.append(" (")
                .append(Constant.TIME_FORMAT.format(timeTable.getDeparture().toDate()))
                .append(") - ");
        }
        sms.append(timeTable.getDestination());
        if (timeTable.getArrival() != null) {
            sms.append(" (")
                .append(Constant.TIME_FORMAT.format(timeTable.getArrival().toDate()))
                .append(")");
        }
        sms.append(", ");
        if (timeTable.getPrice() != null) {
            if (timeTable.getPrice() > 0) {
                sms.append(NumberFormat.getCurrencyInstance().format(timeTable.getPrice()))
                    .append(", ");
            }
        }
        if (!TextUtils.isEmpty(timeTable.getTickets())) {
            try {
                int tickets = Integer.parseInt(timeTable.getTickets());
                sms.append(context.getResources().getQuantityString(R.plurals.tickets, tickets, tickets))
                    .append(", ");
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        if (!TextUtils.isEmpty(timeTable.getCarrier())) {
            sms.append(timeTable.getCarrier()).append(", ");
        }
        String body = sms.toString();
        return body.substring(0, body.length() - 2);
    }

    public static boolean isDebug() {
        return BuildConfig.DEBUG;
    }
}
