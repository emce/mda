package mobi.cwiklinski.mda.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import com.google.gson.reflect.TypeToken;

import java.util.List;

import mobi.cwiklinski.mda.model.Locality;
import mobi.cwiklinski.mda.net.HttpUtil;
import mobi.cwiklinski.mda.util.UserPreferences;

public class LocalityRemoteAdapter extends ArrayAdapter<Locality> {

    private List<Locality> mList;
    private Context mContext;

    public LocalityRemoteAdapter(Context context, List<Locality> list) {
        super(context, android.R.layout.simple_spinner_dropdown_item);
        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Locality getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                HttpUtil util = HttpUtil.getInstance();
                if (constraint != null && constraint.length() > 2) {
                    mList = util
                        .setMethod(HttpUtil.Method.GET)
                        .setUrl("http://rozklady.mda.malopolska.pl/ws/getCity.php?miejscowosc=" + constraint)
                        .setCookieFromPreferences(new UserPreferences(mContext))
                        .connect()
                        .getObjects(new TypeToken<List<Locality>>() {});

                    filterResults.values = mList;
                    filterResults.count = mList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        if (view != null && !mList.isEmpty()) {
            view.setTag(mList.get(position));
        }
        return view;
    }
}
