package mobi.cwiklinski.mda.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import mobi.cwiklinski.mda.R;
import mobi.cwiklinski.mda.activity.SearchActivity;
import mobi.cwiklinski.mda.util.Constant;

public class ChooseFragment extends AbstractFragment {

    public static final String FRAGMENT_TAG = ChooseFragment.class.getSimpleName();
    @InjectView(R.id.choose_from_cracow) CardView fromCracow;
    @InjectView(R.id.choose_from_cracow_text) TextView fromCracowText;
    @InjectView(R.id.choose_to_cracow) CardView toCracow;
    @InjectView(R.id.choose_to_cracow_text) TextView toCracowText;
    @InjectView(R.id.choose_from_nowy_sacz) CardView fromNowySacz;
    @InjectView(R.id.choose_from_nowy_sacz_text) TextView fromNowySaczText;
    @InjectView(R.id.choose_to_nowy_sacz) CardView toNowySacz;
    @InjectView(R.id.choose_to_nowy_sacz_text) TextView toNowySaczText;
    private Handler handler = new Handler();

    public static ChooseFragment newInstance() {
        return new ChooseFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Drawable krk = getResources().getDrawable(R.drawable.krk_bg);
        if (krk != null) {
            krk.setAlpha(50);
        }
        Drawable ns = getResources().getDrawable(R.drawable.ns_bg);
        if (ns != null) {
            ns.setAlpha(50);
        }
        fromCracow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getPreferences().saveDestination(Constant.Destination.FROM_CRACOW);
                        startActivity(new Intent(getActivity(), SearchActivity.class));
                    }
                }, 300);
            }
        });
        fromCracowText.setBackgroundDrawable(krk);
        toCracow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getPreferences().saveDestination(Constant.Destination.TO_CRACOW);
                        startActivity(new Intent(getActivity(), SearchActivity.class));
                    }
                }, 300);
            }
        });
        toCracowText.setBackgroundDrawable(krk);
        fromNowySacz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getPreferences().saveDestination(Constant.Destination.FROM_NOWY_SACZ);
                        startActivity(new Intent(getActivity(), SearchActivity.class));
                    }
                }, 300);
            }
        });
        fromNowySaczText.setBackgroundDrawable(ns);
        toNowySacz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getPreferences().saveDestination(Constant.Destination.TO_NOWY_SACZ);
                        startActivity(new Intent(getActivity(), SearchActivity.class));
                    }
                }, 300);
            }
        });
        toNowySaczText.setBackgroundDrawable(ns);
    }
}
