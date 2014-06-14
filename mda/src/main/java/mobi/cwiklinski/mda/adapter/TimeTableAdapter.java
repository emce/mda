package mobi.cwiklinski.mda.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.joanzapata.android.iconify.Iconify;

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
            viewHolder.arrow = (TextView) convertView.findViewById(R.id.tt_arrow);
            viewHolder.arrow.setText(R.string.icon_arrow_right);
            viewHolder.bus = (TextView) convertView.findViewById(R.id.tt_bus);
            viewHolder.bus.setText(R.string.icon_bus);
            viewHolder.clock = (TextView) convertView.findViewById(R.id.tt_clock);
            viewHolder.clock.setText(R.string.icon_clock);
            viewHolder.money = (TextView) convertView.findViewById(R.id.tt_money);
            viewHolder.money.setText(R.string.icon_money);
            viewHolder.ticket = (TextView) convertView.findViewById(R.id.tt_ticket);
            viewHolder.ticket.setText(R.string.icon_ticket);
            viewHolder.start = (TextView) convertView.findViewById(R.id.tt_start);
            viewHolder.startCity = (TextView) convertView.findViewById(R.id.tt_start_city);
            viewHolder.destination = (TextView) convertView.findViewById(R.id.tt_destination);
            viewHolder.destinationCity = (TextView) convertView.findViewById(R.id.tt_destination_city);
            viewHolder.length = (TextView) convertView.findViewById(R.id.tt_length);
            viewHolder.price = (TextView) convertView.findViewById(R.id.tt_price);
            viewHolder.tickets = (TextView) convertView.findViewById(R.id.tt_tickets);
            viewHolder.carrier = (TextView) convertView.findViewById(R.id.tt_carrier);
            viewHolder.start.setTypeface(
                mTypefaceManager.getTypeface(TypefaceManager.FontFace.ROBOTO_BOLD));
            viewHolder.destination.setTypeface(
                mTypefaceManager.getTypeface(TypefaceManager.FontFace.ROBOTO_BOLD));
            Iconify.addIcons(viewHolder.arrow, viewHolder.money, viewHolder.clock, viewHolder.bus, viewHolder.ticket);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (TimeTableHolder) convertView.getTag();
        }
        TimeTable item = mList.get(position);
        if (item.getDeparture() != null) {
            viewHolder.start.setText(Constant.TIME_FORMAT.format(item.getDeparture().toDate()));
        }
        viewHolder.startCity.setText(item.getStart());
        if (item.getArrival() != null) {
            viewHolder.destination.setText(Constant.TIME_FORMAT.format(item.getArrival().toDate()));
        }
        viewHolder.destinationCity.setText(item.getDestination());
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
        TextView arrow;
        TextView bus;
        TextView clock;
        TextView money;
        TextView ticket;
        TextView start;
        TextView startCity;
        TextView destination;
        TextView destinationCity;
        TextView length;
        TextView price;
        TextView tickets;
        TextView carrier;
    }
}
