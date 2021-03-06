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

public class AddCafeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cafe);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Add cafe");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(arrow -> onBackPressed());


        NetworkAPI cafeService =
                new RetrofitAPI()
                        .getRetrofit()

                        .create(NetworkAPI.class);


        EditText editName = (EditText) findViewById(R.id.cafe_name_add);
        EditText editAddress = (EditText) findViewById(R.id.cafe_address_add);
        EditText editRank = (EditText) findViewById(R.id.cafe_rank_add);
        EditText editDesc = (EditText) findViewById(R.id.cafe_description_add);
        EditText editLink = (EditText) findViewById(R.id.cafe_src_add);

        String[] status = new String[1];

        Button saveButton = (Button) findViewById(R.id.save_button_add);
        Context context = saveButton.getContext();
        saveButton.setOnClickListener(view -> {

            if (isEmpty(editName) ||
                    isEmpty(editRank) ||
                    isEmpty(editDesc) ||
                    isEmpty(editAddress) ||
                    isEmpty(editLink)
                    ) {
                alertD(context, R.string.dialog_about_title, R.string.dialog_about_message).show();
            } else {
                cafeService
                        .insertCafe(
                                editAddress.getText().toString(),
                                editRank.getText().toString(),
                                editDesc.getText().toString(),
                                editName.getText().toString(),
                                editLink.getText().toString()
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
                                    Log.e("ErrorPHP", n);
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
                    if (text != R.string.dialog_about_message)
                        startActivity(
                                new Intent(context,
                                        MainActivity.class)
                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }
        );
        return builder.create();
    }

    public static class AddDrinkActivity extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_add_drink);
        }
    }
}
