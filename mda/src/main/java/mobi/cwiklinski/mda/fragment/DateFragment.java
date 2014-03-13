package mobi.cwiklinski.mda.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import org.joda.time.MutableDateTime;

import java.util.Calendar;

import mobi.cwiklinski.mda.R;
import mobi.cwiklinski.mda.activity.TableActivity;
import mobi.cwiklinski.mda.model.Locality;
import mobi.cwiklinski.mda.util.Constant;

public class DateFragment extends BaseFragment {

    public static final String FRAGMENT_TAG = DateFragment.class.getSimpleName();
    private DatePicker mDatePicker;
    private TimePicker mTimePicker;
    private Calendar mCalendar = Calendar.getInstance();
    private Locality mLocality;
    private Constant.Destination mDestination = Constant.Destination.FROM_CRACOW;

    public static DateFragment newInstance() {
        DateFragment fragment = new DateFragment();
        fragment.setRetainInstance(true);
        fragment.setHasOptionsMenu(true);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDestination = getPreferences().getDestination();
        mLocality = getPreferences().getLocality();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.date, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDatePicker = (DatePicker) view.findViewById(R.id.date_date);
        mTimePicker = (TimePicker) view.findViewById(R.id.date_time);
        Button next = (Button) view.findViewById(R.id.date_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    MutableDateTime dateTime = new MutableDateTime();
                    dateTime.setYear(mDatePicker.getYear());
                    dateTime.setMonthOfYear(mDatePicker.getMonth());
                    dateTime.setDayOfMonth(mDatePicker.getDayOfMonth());
                    dateTime.setHourOfDay(mTimePicker.getCurrentHour());
                    dateTime.setMinuteOfHour(mTimePicker.getCurrentMinute());
                    getPreferences().saveDate(new MutableDateTime().toDateTime());
                    startActivity(new Intent(getActivity(), TableActivity.class));
                } else {
                    Toast.makeText(getActivity(), R.string.app_name, Toast.LENGTH_LONG).show();
                }
            }
        });
        mDatePicker.setCalendarViewShown(false);
        mTimePicker.setIs24HourView(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mDestination != null) {
            int titleResource;
            switch(mDestination) {
                case TO_CRACOW:
                    titleResource = R.string.choose_to_cracow_button;
                    break;
                case FROM_NOWY_SACZ:
                    titleResource = R.string.choose_from_nowysacz_button;
                    break;
                case TO_NOWY_SACZ:
                    titleResource = R.string.choose_to_nowysacz_button;
                    break;
                default:
                    titleResource = R.string.choose_from_cracow_button;
                    break;
            }
            getBaseActivity().setMainTitle(titleResource);
            getBaseActivity().setSubTitle(mLocality.toLocalizedString(getResources()));
        }
    }

    private boolean validate() {
        boolean validated = true;
        if (mDatePicker.getDayOfMonth() < mCalendar.get(Calendar.DAY_OF_MONTH)
            && mDatePicker.getMonth() <= mCalendar.get(Calendar.MONTH)
            && mDatePicker.getYear() <= mCalendar.get(Calendar.YEAR)) {
            validated = false;
        }
        if (validated
            && mTimePicker.getCurrentMinute() <= mCalendar.getMinimum(Calendar.MINUTE)
            && mTimePicker.getCurrentHour() <= mCalendar.get(Calendar.HOUR)) {
            validated = false;
        }
        return validated;
    }
}
