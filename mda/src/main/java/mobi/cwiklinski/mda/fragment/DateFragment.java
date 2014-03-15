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

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;

import mobi.cwiklinski.mda.R;
import mobi.cwiklinski.mda.activity.TableActivity;
import mobi.cwiklinski.mda.model.Locality;
import mobi.cwiklinski.mda.util.Constant;

public class DateFragment extends BaseFragment {

    public static final String FRAGMENT_TAG = DateFragment.class.getSimpleName();
    private DatePicker mDatePicker;
    private TimePicker mTimePicker;
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
                MutableDateTime dateTime = new DateTime().toMutableDateTime();
                dateTime.setYear(mDatePicker.getYear());
                dateTime.setMonthOfYear(mDatePicker.getMonth() + 1);
                dateTime.setDayOfMonth(mDatePicker.getDayOfMonth());
                dateTime.setHourOfDay(mTimePicker.getCurrentHour());
                dateTime.setMinuteOfHour(mTimePicker.getCurrentMinute());
                if (dateTime.isAfterNow()) {
                    getPreferences().saveDate(dateTime.toDateTime());
                    startActivity(new Intent(getActivity(), TableActivity.class));
                } else {
                    Toast.makeText(getActivity(), R.string.incorrect_date, Toast.LENGTH_LONG).show();
                }
            }
        });
        mDatePicker.setCalendarViewShown(true);
        mDatePicker.setSpinnersShown(false);
        mTimePicker.setIs24HourView(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mDestination != null) {
            int titleResource;
            switch(mDestination) {
                case TO_CRACOW:
                    titleResource = R.string.to_cracow;
                    break;
                case FROM_NOWY_SACZ:
                    titleResource = R.string.from_nowysacz;
                    break;
                case TO_NOWY_SACZ:
                    titleResource = R.string.to_nowysacz;
                    break;
                default:
                    titleResource = R.string.from_cracow;
                    break;
            }
            getBaseActivity().setMainTitle(titleResource);
            getBaseActivity().setSubTitle(mLocality.toLocalizedString(getResources()));
        }
    }
}
