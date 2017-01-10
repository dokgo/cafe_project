package com.study.dokgo.projectcafe.presenter;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.study.dokgo.projectcafe.MainActivity;
import com.study.dokgo.projectcafe.R;
import com.study.dokgo.projectcafe.models.Cafe;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by dokgo on 02.01.17.
 */

public class FilterDialog extends DialogFragment {
    CheckBox top_ten;
    CheckBox has_food;
    TextView tvMin;
    TextView tvMax;

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
            List<Cafe> newList = new LinkedList<>();

            if (top_ten.isChecked()) {
                Collections.sort(MainActivity.cafeListCopy, (a, b) -> a.getRank().compareTo(b.getRank()));
            }

            for (Cafe cafe : MainActivity.cafeListCopy) {
                int avg = Integer.parseInt(cafe.getAvg());
                int min = Integer.parseInt(tvMin.getText().toString());
                int max = Integer.parseInt(tvMax.getText().toString());

                if (avg >= min && avg <= max) {
                    Log.e("AVG", cafe.getAvg());
                    Log.e("EQUALS", Boolean.toString(cafe.getAvg().equals("0")));

                    if (has_food.isChecked() && !cafe.getAvg().equals("0")) {
                        Log.e("AVG", "checked");
                        newList.add(cafe);
                    }
                    if (!has_food.isChecked()) {
                        newList.add(cafe);
                    }
                }

            }

            MainActivity.cafeListAdapter
                    .updateAll(newList);
            d.dismiss();
        });
        builder.setNegativeButton("Cancel", (d, i) -> {
            Log.i("Cancel", "clicked");
            MainActivity.cafeListAdapter
                    .updateAll(MainActivity.cafeListCopy);
            dismiss();
        });

        View view = getActivity().getLayoutInflater().inflate(R.layout.filter_dialog, null);

        builder.setView(view);
        setSeekBar(view);
        setFields(view);

        return builder.create();
    }

    public void setSeekBar(View view) {
        final CrystalRangeSeekbar rangeSeekbar = (CrystalRangeSeekbar) view.findViewById(R.id.price_seekbar);
        // get min and max text view
        tvMin = (TextView) view.findViewById(R.id.minVal);
        tvMax = (TextView) view.findViewById(R.id.maxVal);

        // set listener
        rangeSeekbar.setOnRangeSeekbarChangeListener((min, max) -> {
            tvMin.setText(String.valueOf(min));
            tvMax.setText(String.valueOf(max));
        });
    }

    public void setFields(View view) {
        top_ten = (CheckBox) view.findViewById(R.id.to_ten_checkbox);
        has_food = (CheckBox) view.findViewById(R.id.has_food_checkbox);
    }
}
