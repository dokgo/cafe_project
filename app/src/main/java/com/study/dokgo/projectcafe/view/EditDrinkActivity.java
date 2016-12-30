package com.study.dokgo.projectcafe.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.study.dokgo.projectcafe.R;
import com.study.dokgo.projectcafe.models.Drink;
import com.study.dokgo.projectcafe.models.Drinks;
import com.study.dokgo.projectcafe.models.NetworkAPI;
import com.study.dokgo.projectcafe.presenter.RetrofitAPI;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class EditDrinkActivity extends AppCompatActivity {

    NetworkAPI networkAPI;
    String drinkId;
    Drink[] drink = new Drink[1];
    String[] status = new String[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_drink);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(arrow -> onBackPressed());


        try {
            drinkId = getIntent().getExtras().get("id").toString();
        } catch (NullPointerException e) {
            return;
        }

        networkAPI =
                new RetrofitAPI()
                        .getRetrofit()

                        .create(NetworkAPI.class);

        networkAPI
                .getDrinkById(drinkId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(Drinks::getMenu)
                .flatMap(Observable::from)
                .single()
                .subscribe(
                        cf -> {
                            drink[0] = cf;
                            Log.e("obs", cf.toString());
                        },
                        throwable -> Log.e(getClass().getName(), throwable.toString()),
                        () -> {
                            toolbar.setTitle(drink[0].getName());

                            EditText drinkName = (EditText) findViewById(R.id.menu_drink_name_edit);
                            EditText drinkCost = (EditText) findViewById(R.id.menu_drink_cost_edit);
                            EditText drinkVolume = (EditText) findViewById(R.id.menu_drink_volume_edit);


                            drinkName.setText(drink[0].getName());
                            drinkCost.setText(drink[0].getCost());
                            drinkVolume.setText(drink[0].getVolume());

                            Button saveButton = (Button) findViewById(R.id.save_button_edit);
                            Context context = saveButton.getContext();

                            saveButton.setOnClickListener(view -> {

                                if (isEmpty(drinkName) ||
                                        isEmpty(drinkCost) ||
                                        isEmpty(drinkVolume)
                                        ) {

                                    AlertDialog.Builder builder = new AlertDialog.Builder(saveButton.getContext());
                                    builder.setTitle(R.string.dialog_about_title);
                                    builder.setMessage(R.string.dialog_about_message);
                                    builder.setCancelable(true);
                                    builder.setPositiveButton(android.R.string.ok, (d, w) -> d.dismiss());
                                    alertD(context, R.string.dialog_about_title, R.string.dialog_about_message).show();
                                } else {
                                    networkAPI
                                            .updateDrink(
                                                    drinkName.getText().toString(),
                                                    drinkCost.getText().toString(),
                                                    drinkVolume.getText().toString(),
                                                    drinkId
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
                                                            .delete("cafe", "cafeId", drinkId)
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
