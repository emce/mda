package mobi.cwiklinski.mda.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;

import mobi.cwiklinski.mda.R;
import mobi.cwiklinski.mda.fragment.TableFragment;
import mobi.cwiklinski.mda.model.Locality;

public class TableActivity extends AbstractOverflowActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            TableFragment tableFragment = TableFragment.newInstance();
            ft.replace(R.id.fragment_container, tableFragment, TableFragment.FRAGMENT_TAG);
            ft.commit();
        }
        DateTime date = getPreferences().getDate();
        DateTime now = new DateTime();
        int difference = Days.daysBetween(
            now.withTimeAtStartOfDay(), date.withTimeAtStartOfDay()).getDays();
        String dateString;
        if (difference == 0) {
            dateString = getString(R.string.choose_date_today);
        } else if (difference == 1) {
            dateString = getString(R.string.choose_date_tomorrow);
        } else if (difference == 2) {
            dateString = getString(R.string.choose_date_day_after_tomorrow);
        } else {
            dateString = date.toString(DateTimeFormat.forPattern("dd MMMM yyyy"));
        }
        dateString += " " + date.toString(DateTimeFormat.forPattern("HH:mm"));
        Locality current = getPreferences().getLocality();
        if (!TextUtils.isEmpty(dateString)) {
            getSupportActionBar().setSubtitle(
                String.format(getString(R.string.target_to), current.getName()) + " "
                    + getString(R.string.on) + " " + dateString);
        }
    }
}
