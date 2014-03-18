package mobi.cwiklinski.mda.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import mobi.cwiklinski.mda.model.Locality;
import mobi.cwiklinski.mda.util.TypefaceManager;

public class LocalityRemoteAdapter extends ArrayAdapter<Locality> {

    private List<Locality> mList;
    private Context mContext;
    private TypefaceManager mTypefaceManager;

    public LocalityRemoteAdapter(Context context, List<Locality> list) {
        super(context, android.R.layout.simple_spinner_dropdown_item);
        mList = list;
        mContext = context;
        mTypefaceManager = TypefaceManager.getInstance(context);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        if (view != null && !mList.isEmpty()) {
            Locality item = mList.get(position);
            view.setTag(item);
            ((TextView) view).setText(item.toLocalizedString(mContext.getResources()));
            ((TextView) view).setTypeface(mTypefaceManager.getTypeface(TypefaceManager.FontFace.ROBOTO_NORMAL));
        }
        return view;
    }
}
