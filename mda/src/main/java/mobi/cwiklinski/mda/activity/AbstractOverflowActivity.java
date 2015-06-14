package mobi.cwiklinski.mda.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;

import mobi.cwiklinski.mda.R;

abstract public class AbstractOverflowActivity extends AbstractActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_overflow);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mProgress = (FrameLayout) findViewById(R.id.progress);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ViewCompat.setTransitionName(mToolbar, TITLE);
        }
        Drawable bg = getResources().getDrawable(R.drawable.krk_bg);
        switch (getPreferences().getDestination()) {
            case FROM_NOWY_SACZ:
                bg = getResources().getDrawable(R.drawable.ns_bg);
                getSupportActionBar().setTitle(R.string.from_nowysacz);
                break;
            case TO_NOWY_SACZ:
                bg = getResources().getDrawable(R.drawable.ns_bg);
                getSupportActionBar().setTitle(R.string.to_nowysacz);
                break;
            case FROM_CRACOW:
                getSupportActionBar().setTitle(R.string.from_cracow);
                break;
            case TO_CRACOW:
                getSupportActionBar().setTitle(R.string.to_cracow);
                break;
        }
        getToolbar().setBackgroundDrawable(bg);
    }
}
