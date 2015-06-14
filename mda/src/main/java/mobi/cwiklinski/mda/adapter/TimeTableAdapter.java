package mobi.cwiklinski.mda.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.List;

import mobi.cwiklinski.mda.R;
import mobi.cwiklinski.mda.model.TimeTable;
import mobi.cwiklinski.mda.util.Constant;

public class TimeTableAdapter extends BaseAdapter {

    private List<TimeTable> mList;
    private Context mContext;

    public TimeTableAdapter(Context context, List<TimeTable> list) {
        mContext = context;
        mList = list;
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
            ViewGroup view = (ViewGroup) convertView;
            viewHolder = new TimeTableHolder();
            viewHolder.startHour = (TextView) convertView.findViewById(R.id.tt_start_hour);
            viewHolder.startStation = (TextView) convertView.findViewById(R.id.tt_start_station);
            viewHolder.endHour = (TextView) convertView.findViewById(R.id.tt_end_hour);
            viewHolder.endStation = (TextView) convertView.findViewById(R.id.tt_end_station);
            viewHolder.price = (TextView) convertView.findViewById(R.id.tt_price);
            viewHolder.tickets = (TextView) convertView.findViewById(R.id.tt_tickets);
            viewHolder.carrier = (TextView) convertView.findViewById(R.id.tt_carrier);
            viewHolder.duration = (TextView) convertView.findViewById(R.id.tt_duration);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (TimeTableHolder) convertView.getTag();
        }
        TimeTable item = mList.get(position);
        if (item.getDeparture() != null) {
            viewHolder.startHour.setText(Constant.TIME_FORMAT.format(item.getDeparture().toDate()));
        }
        viewHolder.startStation.setText(item.getStart());
        if (item.getArrival() != null) {
            viewHolder.endHour.setText(Constant.TIME_FORMAT.format(item.getArrival().toDate()));
        }
        viewHolder.endStation.setText(item.getDestination());
        if (!TextUtils.isEmpty(item.getLength())) {
            if (item.getLength().contains(":")) {
                try {
                    String[] parts = item.getLength().split(":");
                    if (parts.length > 1) {
                        int hours = Integer.parseInt(parts[0]);
                        int minutes = Integer.parseInt(parts[1]);
                        String length = mContext.getResources().getQuantityString(R.plurals.hours, hours, hours);
                        if (minutes > 0) {
                            length += " " + mContext.getResources().getQuantityString(R.plurals.minutes, minutes, minutes);
                        }
                        viewHolder.duration.setText(length);
                    } else {
                        viewHolder.duration.setText(item.getLength());
                    }
                } catch (Exception e) {
                    viewHolder.duration.setText(item.getLength());
                }
            } else {
                viewHolder.duration.setText(item.getLength());
            }
        }
        if (item.getPrice() != null) {
            if (item.getPrice() > 0) {
                viewHolder.price.setText(NumberFormat.getCurrencyInstance().format(item.getPrice()));
            } else {
                viewHolder.price.setText(R.string.no_data);
            }
        }
        if (!TextUtils.isEmpty(item.getTickets())) {
            try {
                int tickets = Integer.parseInt(item.getTickets());
                viewHolder.tickets.setText(
                    mContext.getResources().getQuantityString(R.plurals.tickets, tickets, tickets));
            } catch (NumberFormatException e) {
                viewHolder.tickets.setText(item.getTickets());
            }
        }
        if (!TextUtils.isEmpty(item.getCarrier())) {
            viewHolder.carrier.setText(item.getCarrier());
        }
        return convertView;
    }

    private class TimeTableHolder {
        TextView startHour;
        TextView startStation;
        TextView endHour;
        TextView endStation;
        TextView price;
        TextView carrier;
        TextView duration;
        TextView tickets;
    }
}
