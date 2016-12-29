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
import com.study.dokgo.projectcafe.models.Dish;
import com.study.dokgo.projectcafe.models.NetworkAPI;
import com.study.dokgo.projectcafe.view.EditDishActivity;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by dokgo on 27.11.16.
 */

public class MenuListAdapter extends
        RecyclerView.Adapter<MenuListAdapter.CafeViewHolder> {

    private List<Dish> menuList;
    NetworkAPI networkAPI;

    public MenuListAdapter(List<Dish> menuList, NetworkAPI networkAPI) {
        this.menuList = menuList;
        this.networkAPI = networkAPI;
    }

    @Override
    public int getItemCount() {
        return menuList != null ? menuList.size() : 0;
    }


    @Override
    public void onBindViewHolder(CafeViewHolder cafeViewHolder, int i) {
        Dish menui = menuList.get(i);

        cafeViewHolder.dishId = menui.getDishId();
        cafeViewHolder.vName.setText(menui.getName());
        cafeViewHolder.vType.setText("Тип : " + menui.getType());
        cafeViewHolder.vTime.setText("Время: " + menui.getTime());
        cafeViewHolder.vCost.setText("Цена: " + menui.getCost());
        cafeViewHolder.vPortion.setText("Порция: " + menui.getPortion());

        Context context = cafeViewHolder.itemView.getContext();



        cafeViewHolder.itemView.setOnClickListener(view -> {
            Activity parent_activity = (Activity) context;
            Intent intent = new Intent(view.getContext(), EditDishActivity.class);
            intent.putExtra("id", cafeViewHolder.dishId);
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
                                        .delete("dish", "dishId", cafeViewHolder.dishId)
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

    @Override
    public CafeViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.menu_card_view, viewGroup, false);

        return new CafeViewHolder(itemView);
    }

    class CafeViewHolder extends RecyclerView.ViewHolder {
        String dishId;
        TextView vName;
        TextView vType;
        TextView vTime;
        TextView vCost;
        TextView vPortion;

        CafeViewHolder(View v) {
            super(v);
            vType = (TextView) v.findViewById(R.id.card_dish_type);
            vName = (TextView) v.findViewById(R.id.card_dish_name_title);
            vTime = (TextView) v.findViewById(R.id.card_dish_time);
            vCost = (TextView) v.findViewById(R.id.card_dish_cost);
            vPortion = (TextView) v.findViewById(R.id.card_dish_portion);


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


    public void updateAll(List<Dish> newList){
        menuList.clear();
        menuList.addAll(newList);
        notifyDataSetChanged();
    }
}
