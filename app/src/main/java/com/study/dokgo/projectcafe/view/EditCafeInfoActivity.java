package com.study.dokgo.projectcafe.view;

import android.app.Activity;
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
import com.study.dokgo.projectcafe.models.CafeService;
import com.study.dokgo.projectcafe.models.NetworkAPI;
import com.study.dokgo.projectcafe.presenter.RetrofitAPI;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class EditCafeInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_cafe_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(arrow -> onBackPressed());

        String cafeId;
        try {
            cafeId = getIntent().getExtras().get("id").toString();
        } catch (NullPointerException e) {
            return;
        }

        NetworkAPI cafeService =
                new RetrofitAPI()
                        .getRetrofit()

                        .create(NetworkAPI.class);

        Cafe[] cafe = new Cafe[1];
        cafeService
                .getCafeById(cafeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(CafeService::getCafes)
                .flatMap(Observable::from)
                .single()
                .subscribe(
                        cf -> {
                            cafe[0] = cf;
                            Log.e("obs", cf.toString());
                        },
                        throwable -> Log.e(getClass().getName(), throwable.toString()),
                        () -> {
                            toolbar.setTitle(toolbar.getContext().getString(R.string.edit, cafe[0].getName()));
                            EditText editName = (EditText) findViewById(R.id.cafe_name_input);
                            EditText editAddress = (EditText) findViewById(R.id.cafe_address_input);
                            EditText editRank = (EditText) findViewById(R.id.cafe_rank_input);
                            EditText editDesc = (EditText) findViewById(R.id.cafe_description_input);
                            EditText editLink = (EditText) findViewById(R.id.cafe_src_input);
                            ImageView imageView = (ImageView) findViewById(R.id.cafe_edit_image);

                            Picasso
                                    .with(getBaseContext())
                                    .load(cafe[0].getSrc())
                                    .error(R.drawable.image_loading_error)
                                    .fit()
                                    .into(imageView);

                            editName.setText(cafe[0].getName());
                            editAddress.setText(cafe[0].getAdress());
                            editRank.setText(cafe[0].getRank());
                            editDesc.setText(cafe[0].getDescription());
                            editLink.setText(cafe[0].getSrc());

                            String[] status = new String[1];

                            Button saveButton = (Button) findViewById(R.id.save_button);
                            Button deleteButton = (Button) findViewById(R.id.delete_button);
                            Context context = saveButton.getContext();
                            saveButton.setOnClickListener(view -> {

                                if (isEmpty(editName) ||
                                        isEmpty(editRank) ||
                                        isEmpty(editDesc) ||
                                        isEmpty(editAddress) ||
                                        isEmpty(editLink)
                                        ) {

                                    AlertDialog.Builder builder = new AlertDialog.Builder(saveButton.getContext());
                                    builder.setTitle(R.string.dialog_about_title);
                                    builder.setMessage(R.string.dialog_about_message);
                                    builder.setCancelable(true);
                                    builder.setPositiveButton(android.R.string.ok, (d, w) -> d.dismiss());
                                    alertD(context, R.string.dialog_about_title, R.string.dialog_about_message).show();
                                } else {
                                    cafeService
                                            .updateCafe(
                                                    editAddress.getText().toString(),
                                                    editRank.getText().toString(),
                                                    editDesc.getText().toString(),
                                                    editName.getText().toString(),
                                                    editLink.getText().toString(),
                                                    cafeId
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

                            deleteButton.setOnClickListener(
                                    view -> {
                                        new AlertDialog.Builder(this)
                                                .setTitle(R.string.confirm)
                                                .setMessage(R.string.delete_confirm)
                                                .setCancelable(false)
                                                .setPositiveButton("Yes", (d, i) -> {
                                                    cafeService
                                                            .delete("cafe", "cafeId", cafeId)
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
                                                                                    R.string.update_status,//TODO:Change string resourses names
                                                                                    R.string.successful
                                                                            ).show();
                                                                            startActivity(
                                                                                    new Intent(context,
                                                                                            MainActivity.class)
                                                                                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
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
                            );

                        }
                );
    }


    public AlertDialog alertD(Context context, int title, int text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(text);
        builder.setCancelable(true);
        builder.setPositiveButton(android.R.string.ok,
                (d, w) -> startActivity(
                        new Intent(context,
                                MainActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        );
        return builder.create();
    }


    private boolean isEmpty(EditText myeditText) {
        return myeditText.getText().toString().trim().length() == 0;
    }
}
