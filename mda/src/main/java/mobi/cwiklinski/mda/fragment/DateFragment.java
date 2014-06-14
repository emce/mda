package mobi.cwiklinski.mda.fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.joanzapata.android.iconify.Iconify;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.Calendar;

import mobi.cwiklinski.mda.R;
import mobi.cwiklinski.mda.activity.TableActivity;
import mobi.cwiklinski.mda.util.Constant;
import mobi.cwiklinski.mda.util.Util;

public class DateFragment extends BaseFragment {

    public static final String FRAGMENT_TAG = DateFragment.class.getSimpleName();
    private Button mDatePicker;
    private TimePicker mTimePicker;
    private DateTime mChosenDate = new DateTime();

    public static DateFragment newInstance() {
        DateFragment fragment = new DateFragment();
        fragment.setRetainInstance(true);
        fragment.setHasOptionsMenu(true);
        return fragment;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.date;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(Constant.EXTRA_DATETIME)) {
            MutableDateTime date = new DateTime().toMutableDateTime();
            date.setMillis(savedInstanceState.getLong(Constant.EXTRA_DATETIME));
            mChosenDate = date.toDateTime();
        }
        getTypefaceManager().parse((ViewGroup) view);
        mDatePicker = (Button) view.findViewById(R.id.date_date);
        setDateLabel();
        mDatePicker.setCompoundDrawables(
            Util.fontToDrawable(getActivity(), Iconify.IconValue.fa_calendar, android.R.color.white, R.dimen.twenty_dips),
            null, null, null);
        mDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(mChosenDate.getMillis());
                DatePickerDialog dpd = new DatePickerDialog(getActivity(),
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            mChosenDate = new DateTime(year, monthOfYear + 1, dayOfMonth, 0, 0);
                            setDateLabel();
                        }
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show();
            }
        });
        mTimePicker = (TimePicker) view.findViewById(R.id.date_time);
        mTimePicker.setIs24HourView(true);
        DateTime now = new DateTime().plusMinutes(5);
        mTimePicker.setCurrentHour(now.getHourOfDay());
        mTimePicker.setCurrentMinute(now.getMinuteOfHour());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem near = menu.add(R.id.menu_group_main, R.id.menu_forward, ++mMenuOrder,
            R.string.menu_forward);
        setActionBarItem(near, Iconify.IconValue.fa_arrow_circle_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_forward:
                MutableDateTime dateTime = mChosenDate.toMutableDateTime();
                dateTime.setHourOfDay(mTimePicker.getCurrentHour());
                dateTime.setMinuteOfHour(mTimePicker.getCurrentMinute());
                if (dateTime.isAfterNow()) {
                    getPreferences().saveDate(dateTime.toDateTime());
                    startActivity(new Intent(getActivity(), TableActivity.class));
                } else {
                    Toast.makeText(getActivity(), R.string.incorrect_date, Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(Constant.EXTRA_DATETIME, mChosenDate.getMillis());
    }

    private void setDateLabel() {
        DateTime now = new DateTime();
        int difference = Days.daysBetween(
            now.withTimeAtStartOfDay(), mChosenDate.withTimeAtStartOfDay()).getDays();
        if (difference == 0) {
            mDatePicker.setText(R.string.choose_date_today);
        } else if (difference == 1) {
            mDatePicker.setText(R.string.choose_date_tomorrow);
        } else if (difference == 2) {
            mDatePicker.setText(R.string.choose_date_day_after_tomorrow);
        } else {
            mDatePicker.setText(mChosenDate.toString(DateTimeFormat.forPattern("dd MMMM yyyy")));
        }
    }
}
