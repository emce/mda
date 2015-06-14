package mobi.cwiklinski.mda.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import mobi.cwiklinski.mda.R;
import mobi.cwiklinski.mda.activity.TableActivity;
import mobi.cwiklinski.mda.util.ActivityHelper;
import mobi.cwiklinski.mda.util.Constant;

public class DateFragment extends AbstractFragment {

    public static final String FRAGMENT_TAG = DateFragment.class.getSimpleName();
    @InjectView(R.id.date_date) Button mDatePicker;
    @InjectView(R.id.date_time) Button mTimePicker;
    private DateTime mChosenDate = new DateTime().plusMinutes(5);

    public static DateFragment newInstance() {
        return new DateFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.date, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(Constant.EXTRA_DATETIME)) {
            MutableDateTime date = new DateTime().toMutableDateTime();
            date.setMillis(savedInstanceState.getLong(Constant.EXTRA_DATETIME));
            mChosenDate = date.toDateTime();
        } else {
            if (getPreferences().getDate() != null) {
                mChosenDate = getPreferences().getDate();
            }
        }
        setDateLabel();
        mDatePicker.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_small_calendar, 0, 0, 0);
        mDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(mChosenDate.getMillis());
                DatePickerDialog dpd = new DatePickerDialog(getActivity(), R.style.DialogTheme,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            MutableDateTime changed = mChosenDate.toMutableDateTime();
                            changed.setDate(year, monthOfYear + 1, dayOfMonth);
                            mChosenDate = changed.toDateTime();
                            setDateLabel();
                            setTimeLabel();
                        }
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
                );
                dpd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dpd.show();
            }
        });
        mTimePicker.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_small_clock, 0, 0, 0);
        mTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(mChosenDate.getMillis());
                TimePickerDialog dpd = new TimePickerDialog(getActivity(), R.style.DialogTheme,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            MutableDateTime changed = mChosenDate.toMutableDateTime();
                            changed.setTime(hourOfDay, minute, 0, 0);
                            mChosenDate = changed.toDateTime();
                            setTimeLabel();
                            setDateLabel();
                        }
                    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MONTH), true
                );
                dpd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dpd.show();
            }
        });
        setTimeLabel();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem near = menu.add(R.id.menu_group_main, R.id.menu_forward, ++mMenuOrder,
            R.string.menu_forward);
        ActivityHelper.setMenuItem(near, R.drawable.ic_menu_forward);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_forward:
                if (mChosenDate.isAfterNow()) {
                    getPreferences().saveDate(mChosenDate.toDateTime());
                    startActivity(new Intent(getActivity(), TableActivity.class));
                } else {
                    ActivityHelper.showMessage(getActivity(), R.string.incorrect_date);
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

    private void setTimeLabel() {
        mTimePicker.setText(mChosenDate.toString(DateTimeFormat.forPattern("HH:mm")));
    }
}
