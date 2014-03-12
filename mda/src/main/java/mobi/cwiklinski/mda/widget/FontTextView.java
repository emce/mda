package mobi.cwiklinski.mda.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import mobi.cwiklinski.mda.R;
import mobi.cwiklinski.mda.util.TypefaceManager;

public class FontTextView extends TextView {

    public FontTextView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public FontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public FontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void setTypeface(int face) {
        setTypeface(TypefaceManager.getInstance(getContext()).getTypeface(TypefaceManager.FontFace.values()[face]));
    }

    public void setTypeface(TypefaceManager.FontFace face) {
        setTypeface(TypefaceManager.getInstance(getContext()).getTypeface(face));
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        if (isInEditMode()) {
            return;
        }

        int typefaceValue = 0;
        if (attrs != null) {
            TypedArray values = context.obtainStyledAttributes(attrs, R.styleable.FontTextView, defStyle, 0);
            typefaceValue = values.getInt(R.styleable.FontTextView_typeface, 0);
            values.recycle();
        }

        setTypeface(typefaceValue);
    }
}
