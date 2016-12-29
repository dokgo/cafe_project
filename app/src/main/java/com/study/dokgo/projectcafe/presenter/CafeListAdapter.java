package com.study.dokgo.projectcafe.presenter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.study.dokgo.projectcafe.R;
import com.study.dokgo.projectcafe.models.Cafe;
import com.study.dokgo.projectcafe.view.CafeDescriptionActivity;

import java.util.List;

/**
 * Created by dokgo on 27.11.16.
 */

public class CafeListAdapter extends
        RecyclerView.Adapter<CafeListAdapter.CafeViewHolder> {

    private List<Cafe> cafeList;
    private int layout = R.layout.cafe_card_view;

    public CafeListAdapter(List<Cafe> cafeList) {
        this.cafeList = cafeList;
    }

    public CafeListAdapter(List<Cafe> cafeList, int layout) {
        this.cafeList = cafeList;
        this.layout = layout;
    }

    @Override
    public int getItemCount() {
        return cafeList != null ? cafeList.size() : 0;
    }


    @Override
    public void onBindViewHolder(CafeViewHolder cafeViewHolder, int i) {
        Context context = cafeViewHolder.vAddress.getContext();
        Cafe cafei = cafeList.get(i);
        cafeViewHolder.cafeId = cafei.getCafeId();

        if (R.layout.cafe_card_view == layout)
            Picasso
                    .with(context)
                    .load(cafei.getSrc())
                    .error(R.drawable.image_loading_error)
                     .fit()
                    .into(cafeViewHolder.vImage);
        else
            Picasso
                    .with(context)
                    .load(cafei.getSrc())
                    .error(R.drawable.image_loading_error)
                    .into(cafeViewHolder.vImage);

        cafeViewHolder.vName.setText(cafei.getName());

        cafeViewHolder.vAddress.setText(
                context
                        .getString(R.string.cafe_address, cafei.getAdress())
        );

        cafeViewHolder.vRank.setText(
                context
                        .getString(R.string.cafe_rank, cafei.getRank())
        );

        cafeViewHolder.itemView.setOnClickListener(view -> {
            Activity parent_activity = (Activity) context;
            Intent intent = new Intent(view.getContext(), CafeDescriptionActivity.class);
            intent.putExtra("id", cafeViewHolder.cafeId);
            intent.putExtra("rank", cafei.getRank());
            //parent_activity.getWindow().setExitTransition(new Explode());
            parent_activity.startActivity(intent);

        });

        cafeViewHolder.itemView.setOnLongClickListener(view -> {
            Activity parent_activity = (Activity) context;
            Intent intent = new Intent(view.getContext(), CafeDescriptionActivity.class);
            intent.putExtra("id", cafeViewHolder.cafeId);
            intent.putExtra("rank", cafei.getRank());
            parent_activity.getWindow().setExitTransition(new Explode());
            parent_activity.startActivity(
                    intent,
                    ActivityOptions.makeSceneTransitionAnimation(parent_activity).toBundle()
            );

            return true;
        });
    }

    @Override
    public CafeViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(layout, viewGroup, false);

        return new CafeViewHolder(itemView);
    }

    class CafeViewHolder extends RecyclerView.ViewHolder {
        String cafeId;
        ImageView vImage;
        TextView vName;
        TextView vAddress;
        TextView vRank;

        CafeViewHolder(View v) {
            super(v);
            vImage = (ImageView) v.findViewById(R.id.cafe_image);
            vName = (TextView) v.findViewById(R.id.cafe_name_text);
            vAddress = (TextView) v.findViewById(R.id.cafe_address_text);
            vRank = (TextView) v.findViewById(R.id.cafe_rank_text);

        }
    }

    public void updateAll(List<Cafe> newList){
        cafeList.clear();
        cafeList.addAll(newList);
        notifyDataSetChanged();
    }

}
