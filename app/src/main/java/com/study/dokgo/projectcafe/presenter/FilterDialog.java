package com.study.dokgo.projectcafe.presenter;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.study.dokgo.projectcafe.R;

/**
 * Created by dokgo on 02.01.17.
 */

public class FilterDialog extends DialogFragment {
    public FilterDialog() {
    }

    public static FilterDialog newInstance(String title) {
        FilterDialog frag = new FilterDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title", "Filter");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setPositiveButton("Apply", (d, i) -> {
            Log.i("Apply", "clicked");
            d.dismiss();
        });
        builder.setNegativeButton("Cancel", (d, i) -> {
            Log.i("Cancel", "clicked");
            dismiss();
        });

        View view = getActivity().getLayoutInflater().inflate(R.layout.filter_dialog, null);

        builder.setView(view);
        setSeekBar(view);
        return builder.create();
    }

    public void setSeekBar(View view) {
        final CrystalRangeSeekbar rangeSeekbar = (CrystalRangeSeekbar) view.findViewById(R.id.price_seekbar);
        // get min and max text view
        final TextView tvMin = (TextView) view.findViewById(R.id.minVal);
        final TextView tvMax = (TextView) view.findViewById(R.id.maxVal);

        // set listener
        rangeSeekbar.setOnRangeSeekbarChangeListener((min, max) -> {
            tvMin.setText(String.valueOf(min));
            tvMax.setText(String.valueOf(max));
        });
    }
}
