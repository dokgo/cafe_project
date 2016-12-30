package com.study.dokgo.projectcafe.presenter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.study.dokgo.projectcafe.MainActivity;
import com.study.dokgo.projectcafe.R;
import com.study.dokgo.projectcafe.models.Drink;
import com.study.dokgo.projectcafe.models.NetworkAPI;
import com.study.dokgo.projectcafe.view.EditDrinkActivity;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by dokgo on 27.11.16.
 */

public class DrinkListAdapter extends
        RecyclerView.Adapter<DrinkListAdapter.CafeViewHolder> {

    private List<Drink> menuList;
    NetworkAPI networkAPI;

    public DrinkListAdapter(List<Drink> menuList, NetworkAPI networkAPI) {
        this.menuList = menuList;
        this.networkAPI = networkAPI;
    }

    @Override
    public int getItemCount() {
        return menuList != null ? menuList.size() : 0;
    }


    @Override
    public void onBindViewHolder(CafeViewHolder cafeViewHolder, int i) {
        Drink menui = menuList.get(i);

        cafeViewHolder.drinkId = menui.getDrinkId();
        cafeViewHolder.vName.setText(menui.getName());
        cafeViewHolder.vCost.setText("Цена: " + menui.getCost());
        cafeViewHolder.vVolume.setText("Объем: " + menui.getVolume());

        Context context = cafeViewHolder.itemView.getContext();


        if (MainActivity.user_status == 0) {

            cafeViewHolder.itemView.setOnClickListener(view -> {
                Activity parent_activity = (Activity) context;
                Intent intent = new Intent(view.getContext(), EditDrinkActivity.class);
                intent.putExtra("id", cafeViewHolder.drinkId);
                parent_activity.getWindow().setExitTransition(new Explode());
                parent_activity.startActivity(
                        intent,
                        ActivityOptions.makeSceneTransitionAnimation(parent_activity).toBundle()
                );

            });

            cafeViewHolder.itemView.setOnLongClickListener(view -> {
                        String[] status = new String[1];
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Delete?");
                        // builder.setMessage(text);
                        builder.setCancelable(false);
                        builder.setPositiveButton("Yes",
                                (d, w) -> {
                                    networkAPI
                                            .delete("drink", "drinkId", cafeViewHolder.drinkId)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(n -> {
                                                        if (n.equals("OK")) {
                                                            Log.e("DELETE: ", "SUCCESSFUL");
                                                            status[0] = "OK";
                                                            menuList.remove(i);
                                                            notifyItemRemoved(i);
                                                        } else
                                                            status[0] = n;
                                                        Log.e("ErrorPHP", n);
                                                    },
                                                    t -> Log.e("DELETE ERROR", t.toString()),
                                                    () -> {
                                                        if (status[0].equals("OK")) {
                                                            alertD(
                                                                    context,
                                                                    R.string.update_status,//TODO:Change string resources names
                                                                    R.string.successful
                                                            ).show();
                                                        } else {
                                                            alertD(
                                                                    context,
                                                                    R.string.update_status,
                                                                    R.string.error
                                                            ).show();
                                                        }

                                                    });

                                }
                        );
                        builder.setNegativeButton("No", (d, w) -> {

                        });
                        builder.create().show();

                        return true;
                    }


            );
        }

    }

    @Override
    public CafeViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.drink_card_view, viewGroup, false);

        return new CafeViewHolder(itemView);
    }

    class CafeViewHolder extends RecyclerView.ViewHolder {
        String drinkId;
        TextView vName;
        TextView vCost;
        TextView vVolume;

        CafeViewHolder(View v) {
            super(v);
            vName = (TextView) v.findViewById(R.id.card_drink_name_title);
            vCost = (TextView) v.findViewById(R.id.card_drink_cost);
            vVolume = (TextView) v.findViewById(R.id.card_drink_volume);


        }
    }

    private AlertDialog alertD(Context context, int title, int text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(text);
        builder.setCancelable(false);
        builder.setPositiveButton(android.R.string.ok, null);
        return builder.create();
    }


    public void updateAll(List<Drink> newList){
        menuList.clear();
        menuList.addAll(newList);
        notifyDataSetChanged();
    }
}
