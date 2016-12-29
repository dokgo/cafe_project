package com.study.dokgo.projectcafe.view;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.study.dokgo.projectcafe.MainActivity;
import com.study.dokgo.projectcafe.R;
import com.study.dokgo.projectcafe.models.Cafe;
import com.study.dokgo.projectcafe.models.Cafes;
import com.study.dokgo.projectcafe.models.Dish;
import com.study.dokgo.projectcafe.models.Dishes;
import com.study.dokgo.projectcafe.models.NetworkAPI;
import com.study.dokgo.projectcafe.presenter.RetrofitAPI;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class EditDishActivity extends AppCompatActivity {

    NetworkAPI networkAPI;
    String dishId;
    Dish[] dish = new Dish[1];
    String[] status = new String[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_dish);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(arrow -> onBackPressed());


        try {
            dishId = getIntent().getExtras().get("id").toString();
        } catch (NullPointerException e) {
            return;
        }

        networkAPI =
                new RetrofitAPI()
                        .getRetrofit()

                        .create(NetworkAPI.class);

        networkAPI
                .getDishById(dishId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(Dishes::getMenu)
                .flatMap(Observable::from)
                .single()
                .subscribe(
                        cf -> {
                            dish[0] = cf;
                            Log.e("obs", cf.toString());
                        },
                        throwable -> Log.e(getClass().getName(), throwable.toString()),
                        () -> {
                            toolbar.setTitle(dish[0].getName());

                            EditText dishName = (EditText) findViewById(R.id.menu_dish_name_edit);
                            EditText dishType = (EditText) findViewById(R.id.menu_dish_type_edit);
                            EditText dishPortion = (EditText) findViewById(R.id.menu_dish_portion_edit);
                            EditText dishCost = (EditText) findViewById(R.id.menu_dish_cost_edit);
                            EditText dishTime = (EditText) findViewById(R.id.menu_dish_time_edit);
                            EditText dishCuisine = (EditText) findViewById(R.id.menu_dish_cuisine_edit);


                            dishName.setText(dish[0].getName());
                            dishType.setText(dish[0].getType());
                            dishPortion.setText(dish[0].getPortion());
                            dishCost.setText(dish[0].getCost());
                            dishTime.setText(dish[0].getTime());
                            dishCuisine.setText(dish[0].getCuisine());

                            Button saveButton = (Button) findViewById(R.id.save_button_edit);
                            Context context = saveButton.getContext();

                            saveButton.setOnClickListener(view -> {

                                if (isEmpty(dishName) ||
                                        isEmpty(dishType) ||
                                        isEmpty(dishPortion) ||
                                        isEmpty(dishCost) ||
                                        isEmpty(dishTime) ||
                                        isEmpty(dishCuisine)
                                        ) {

                                    AlertDialog.Builder builder = new AlertDialog.Builder(saveButton.getContext());
                                    builder.setTitle(R.string.dialog_about_title);
                                    builder.setMessage(R.string.dialog_about_message);
                                    builder.setCancelable(true);
                                    builder.setPositiveButton(android.R.string.ok, (d, w) -> d.dismiss());
                                    alertD(context, R.string.dialog_about_title, R.string.dialog_about_message).show();
                                } else {
                                    networkAPI
                                            .updateDish(
                                                    dishName.getText().toString(),
                                                    dishType.getText().toString(),
                                                    dishPortion.getText().toString(),
                                                    dishCost.getText().toString(),
                                                    dishTime.getText().toString(),
                                                    dishCuisine.getText().toString(),
                                                    dishId
                                            )
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(
                                                    n -> {
                                                        if (n.equals("OK")) {
                                                            Log.e("UPDATE: ", "SUCCESSFUL");
                                                            status[0] = "OK";
                                                        } else
                                                            status[0] = n;
                                                        Log.e("ErrorPHP", n);
                                                    },
                                                    t -> Log.e("UPDATE ERROR", t.toString()),
                                                    () -> {
                                                        if (status[0].equals("OK")) {
                                                            alertD(
                                                                    context,
                                                                    R.string.update_status,
                                                                    R.string.successful
                                                            ).show();
                                                        } else {
                                                            alertD(
                                                                    context,
                                                                    R.string.update_status,
                                                                    R.string.error
                                                            ).show();
                                                        }

                                                    }

                                            );

                                }
                            });

                            /*deleteButton.setOnClickListener(
                                    view -> {
                                        new AlertDialog.Builder(this)
                                                .setTitle(R.string.confirm)
                                                .setMessage(R.string.delete_confirm)
                                                .setCancelable(false)
                                                .setPositiveButton("Yes", (d, i) -> {
                                                    networkAPI
                                                            .delete("cafe", "cafeId", dishId)
                                                            .subscribeOn(Schedulers.io())
                                                            .observeOn(AndroidSchedulers.mainThread())
                                                            .subscribe(n -> {
                                                                        if (n.equals("OK")) {
                                                                            Log.e("DELETE: ", "SUCCESSFUL");
                                                                            status[0] = "OK";
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
                                                })
                                                .setNegativeButton("No", null)
                                                .show();
                                    }
                            );*/

                        }
                );
    }


    public AlertDialog alertD(Context context, int title, int text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(text);
        builder.setCancelable(true);
        builder.setPositiveButton(android.R.string.ok,
                (d, w) -> {
                    if (text != R.string.error)
                    finish();
                }
        );
        return builder.create();
    }


    private boolean isEmpty(EditText myeditText) {
        return myeditText.getText().toString().trim().length() == 0;
    }
}
