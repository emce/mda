package mobi.cwiklinski.mda.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import mobi.cwiklinski.mda.R;
import mobi.cwiklinski.mda.model.Stage;
import mobi.cwiklinski.mda.util.Constant;
import mobi.cwiklinski.mda.util.TypefaceManager;

public class StageAdapter extends ArrayAdapter<Stage> {

    private ArrayList<Stage> mStages = new ArrayList<>();
    private Context mContext;
    private TypefaceManager mTypefaceManager;

    public StageAdapter(Context context, ArrayList<Stage> objects) {
        super(context, android.R.layout.simple_spinner_dropdown_item);
        mStages = objects;
        mContext = context;
        mTypefaceManager = TypefaceManager.getInstance(context);
    }

    @Override
    public int getCount() {
        return mStages.size();
    }

    @Override
    public Stage getItem(int position) {
        return mStages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        StageHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.stage_item, parent, false);
            mTypefaceManager.parse((ViewGroup) convertView);
            viewHolder = new StageHolder();
            viewHolder.order = (TextView) convertView.findViewById(R.id.st_order);
            viewHolder.station = (TextView) convertView.findViewById(R.id.st_station);
            viewHolder.arrival = (TextView) convertView.findViewById(R.id.st_arrival);
            viewHolder.price = (TextView) convertView.findViewById(R.id.st_price);
            viewHolder.station.setTypeface(mTypefaceManager.getTypeface(TypefaceManager.FontFace.ROBOTO_BOLD));
            viewHolder.order.setTypeface(mTypefaceManager.getTypeface(TypefaceManager.FontFace.ROBOTO_BOLD));
            viewHolder.arrival.setTypeface(mTypefaceManager.getTypeface(TypefaceManager.FontFace.ROBOTO_NORMAL));
            viewHolder.price.setTypeface(mTypefaceManager.getTypeface(TypefaceManager.FontFace.ROBOTO_NORMAL));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (StageHolder) convertView.getTag();
        }
        Stage item = getItem(position);
        viewHolder.order.setText(Integer.toString(position + 1));
        if (!TextUtils.isEmpty(item.getStation())) {
            viewHolder.station.setText(item.getStation());
        } else {
            viewHolder.station.setText(R.string.no_data);
        }
        if (item.getArrival() != null) {
            viewHolder.arrival.setText(Constant.TIME_FORMAT.format(item.getArrival().toDate()));
        } else {
            viewHolder.arrival.setText(R.string.no_data);
        }
        if (!TextUtils.isEmpty(item.getPrice())) {
            viewHolder.price.setText(item.getPrice());
        } else {
            viewHolder.price.setText(R.string.no_data);
        }
        return convertView;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    private class StageHolder {
        TextView order;
        TextView station;
        TextView arrival;
        TextView price;
    }
}
