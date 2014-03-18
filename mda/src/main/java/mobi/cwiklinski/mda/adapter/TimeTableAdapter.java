package mobi.cwiklinski.mda.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.List;

import mobi.cwiklinski.mda.R;
import mobi.cwiklinski.mda.model.TimeTable;
import mobi.cwiklinski.mda.util.Constant;
import mobi.cwiklinski.mda.util.TypefaceManager;

public class TimeTableAdapter extends BaseAdapter {

    private List<TimeTable> mList;
    private Context mContext;
    private TypefaceManager mTypefaceManager;

    public TimeTableAdapter(Context context, List<TimeTable> list) {
        mContext = context;
        mList = list;
        mTypefaceManager = TypefaceManager.getInstance(context);
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
            mTypefaceManager.parse(view);
            viewHolder = new TimeTableHolder();
            viewHolder.start = (TextView) convertView.findViewById(R.id.tt_start);
            viewHolder.destination = (TextView) convertView.findViewById(R.id.tt_destination);
            viewHolder.length = (TextView) convertView.findViewById(R.id.tt_length);
            viewHolder.price = (TextView) convertView.findViewById(R.id.tt_price);
            viewHolder.tickets = (TextView) convertView.findViewById(R.id.tt_tickets);
            viewHolder.start.setTypeface(mTypefaceManager.getTypeface(TypefaceManager.FontFace.ROBOTO_BOLD));
            viewHolder.destination.setTypeface(mTypefaceManager.getTypeface(TypefaceManager.FontFace.ROBOTO_BOLD));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (TimeTableHolder) convertView.getTag();
        }
        TimeTable item = mList.get(position);
        StringBuilder start = new StringBuilder();
        if (item.getDeparture() != null) {
            start.append(Constant.TIME_FORMAT.format(item.getDeparture().toDate()));
            start.append(": ");
        }
        start.append(item.getStart());
        viewHolder.start.setText(start.toString());
        StringBuilder destination = new StringBuilder();
        if (item.getArrival() != null) {
            destination.append(Constant.TIME_FORMAT.format(item.getArrival().toDate()));
            destination.append(": ");
        }
        destination.append(item.getDestination());
        viewHolder.destination.setText(destination.toString());
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
                        viewHolder.length.setText(length);
                    } else {
                        viewHolder.length.setText(item.getLength());
                    }
                } catch (Exception e) {
                    viewHolder.length.setText(item.getLength());
                }
            } else {
                viewHolder.length.setText(item.getLength());
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
            viewHolder.tickets.setText(item.getTickets());
        }
        return convertView;
    }

    private class TimeTableHolder {
        TextView start;
        TextView destination;
        TextView length;
        TextView price;
        TextView tickets;
    }
}
