package mobi.cwiklinski.mda.util;

import android.content.Context;
import android.graphics.Typeface;
import android.util.SparseArray;

public class TypefaceManager {

    private SparseArray<Typeface> mTypefaceList = new SparseArray<>();
    private static TypefaceManager mInstance;

    private TypefaceManager(Context context) {
        mTypefaceList.put(FontFace.ROBOTO_NORMAL.getId(), createTypeface(context, FontFace.ROBOTO_NORMAL));
        mTypefaceList.put(FontFace.ROBOTO_BOLD.getId(), createTypeface(context, FontFace.ROBOTO_BOLD));
    }

    public static TypefaceManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new TypefaceManager(context);
        }
        return mInstance;
    }

    private Typeface createTypeface(Context context, FontFace fontFace) {
        Typeface typeface = null;
        switch (fontFace) {
            case ROBOTO_NORMAL:
                typeface = Typeface.createFromAsset(context.getAssets(), "RobotoCondensed-Regular.ttf");
                break;
            case ROBOTO_BOLD:
                typeface = Typeface.createFromAsset(context.getAssets(), "RobotoCondensed-Bold.ttf");
                break;
        }
        return typeface;
    }

    public Typeface getTypeface(FontFace face) {
        return mTypefaceList.get(face.getId());
    }

    public enum FontFace {
        ROBOTO_NORMAL(0),
        ROBOTO_BOLD(1);

        private int id;

        FontFace(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }
}
