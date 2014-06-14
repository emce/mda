package mobi.cwiklinski.mda.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import mobi.cwiklinski.mda.R;
import mobi.cwiklinski.mda.activity.BaseActivity;

public class InfoDialogFragment extends DialogFragment {

    public static final String FRAGMENT_TAG = InfoDialogFragment.class.getSimpleName();

    public static InfoDialogFragment newInstance() {
        InfoDialogFragment fragment = new InfoDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(getContentView());
        builder.setTitle(R.string.app_name);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });
        return builder.create();
    }

    public View getContentView() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.info_dialog, null);
        if (view != null) {
            Button playStore = (Button) view.findViewById(R.id.info_adfree);
            playStore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(
                        Intent.ACTION_VIEW, Uri.parse(getString(R.string.no_ads_play_link))));
                }
            });
            getBaseActivity().getTypefaceManager().parse((LinearLayout) view);
            return view;
        }
        return null;
    }

    public BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }
}
