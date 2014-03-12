package mobi.cwiklinski.mda.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import mobi.cwiklinski.mda.R;
import mobi.cwiklinski.mda.model.TimeTable;
import mobi.cwiklinski.mda.util.Util;

public class TimeTableAdapter extends BaseAdapter {

    private List<TimeTable> mList;
    private Context mContext;
    private LayoutInflater mInflater;

    public TimeTableAdapter(Context context, List<TimeTable> list) {
        mContext = context;
        mList = list;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TimeTableHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.timetable_item, parent, false);
            viewHolder = new TimeTableHolder();
            viewHolder.start = (TextView) convertView.findViewById(R.id.tt_start);
            viewHolder.destination = (TextView) convertView.findViewById(R.id.tt_destination);
            viewHolder.departure = (TextView) convertView.findViewById(R.id.tt_departure);
            viewHolder.arrival = (TextView) convertView.findViewById(R.id.tt_arrival);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (TimeTableHolder) convertView.getTag();
        }
        TimeTable item = mList.get(position);
        viewHolder.start.setText(item.getStart());
        viewHolder.destination.setText(item.getDestination());
        if (item.getDeparture() != null) {
            viewHolder.departure.setText(Util.DATETIME_FORMAT.format(item.getDeparture().toDate()));
        }
        if (item.getArrival() != null) {
            viewHolder.arrival.setText(Util.DATETIME_FORMAT.format(item.getArrival().toDate()));
        }
        return convertView;
    }

    private class TimeTableHolder {
        TextView start;
        TextView destination;
        TextView departure;
        TextView arrival;
    }
}
