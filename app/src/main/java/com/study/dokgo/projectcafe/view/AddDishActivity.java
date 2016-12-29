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

import com.study.dokgo.projectcafe.MainActivity;
import com.study.dokgo.projectcafe.R;
import com.study.dokgo.projectcafe.models.NetworkAPI;
import com.study.dokgo.projectcafe.presenter.RetrofitAPI;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AddDishActivity extends AppCompatActivity {

    String cafeId;
    NetworkAPI networkAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dish);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Add dish");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(arrow -> onBackPressed());

        try {
            cafeId = getIntent().getExtras().get("id").toString();
        } catch (NullPointerException e) {
            Log.e(getClass().getName(), "WTFFFFFFFFFFFFFFFFFFFFFFFFFFF");
            return;
        }

        networkAPI =
                new RetrofitAPI()
                        .getRetrofit()
                        .create(NetworkAPI.class);


        EditText dishName = (EditText) findViewById(R.id.menu_dish_name);
        EditText dishType = (EditText) findViewById(R.id.menu_dish_type);
        EditText dishPortion = (EditText) findViewById(R.id.menu_dish_portion);
        EditText dishCost = (EditText) findViewById(R.id.menu_dish_cost);
        EditText dishTime = (EditText) findViewById(R.id.menu_dish_time);
        EditText dishCuisine = (EditText) findViewById(R.id.menu_dish_cuisine);

        String[] status = new String[1];

        Button saveButton = (Button) findViewById(R.id.save_button_add);
        Context context = saveButton.getContext();
        saveButton.setOnClickListener(view -> {

            if (isEmpty(dishName) ||
                    isEmpty(dishPortion) ||
                    isEmpty(dishCost) ||
                    isEmpty(dishType) ||
                    isEmpty(dishTime) ||
                    isEmpty(dishCuisine)
                    ) {

                alertD(context, R.string.dialog_about_title, R.string.dialog_about_message).show();
            } else {
                networkAPI
                        .insertDish(
                                dishName.getText().toString(),
                                dishType.getText().toString(),
                                dishPortion.getText().toString(),
                                dishCost.getText().toString(),
                                dishTime.getText().toString(),
                                dishCuisine.getText().toString(),
                                cafeId
                        )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                n -> {
                                    if (n.equals("OK")) {
                                        Log.e("INSERT: ", "SUCCESSFUL");
                                        status[0] = "OK";
                                    } else
                                        status[0] = n;
                                    Log.e("ErrorPHP", status[0]);
                                },
                                t -> Log.e("INSERT ERROR", t.toString()),
                                () -> {
                                    if (status[0].equals("OK")) {
                                        alertD(
                                                context,
                                                R.string.insertion_status,
                                                R.string.successful
                                        ).show();
                                    } else {
                                        alertD(
                                                context,
                                                R.string.insertion_status,
                                                R.string.error
                                        ).show();
                                    }

                                }

                        );

            }
        });
    }

    private boolean isEmpty(EditText myeditText) {
        return myeditText.getText().toString().trim().length() == 0;
    }

    public AlertDialog alertD(Context context, int title, int text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(text);
        builder.setCancelable(false);
        builder.setPositiveButton(android.R.string.ok,
                (d, w) -> {
                    if (text != R.string.dialog_about_message && text != R.string.error)
                        finish();
                }
        );
        return builder.create();
    }
}
