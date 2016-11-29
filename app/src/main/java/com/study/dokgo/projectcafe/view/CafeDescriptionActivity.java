package com.study.dokgo.projectcafe.view;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.study.dokgo.projectcafe.R;
import com.study.dokgo.projectcafe.models.Cafe;
import com.study.dokgo.projectcafe.models.CafeService;
import com.study.dokgo.projectcafe.models.NetworkAPI;
import com.study.dokgo.projectcafe.presenter.RetrofitAPI;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CafeDescriptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cafe_description);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(arrow -> onBackPressed());


        String cafeId;
        try {
            cafeId = getIntent().getExtras().get("id").toString();
        } catch (NullPointerException e) {
            Log.e(getClass().getName(), "WTFFFFFFFFFFFFFFFFFFFFFFFFFFF");
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
                        throwable -> Log.e(getClass().getName(), "errorhere"),
                        () -> {
                            toolbar.setTitle(cafe[0].getName());
                            TextView descText = (TextView) findViewById(R.id.description_text);
                            TextView descAddress = (TextView) findViewById(R.id.description_address);
                            ImageView imageView = (ImageView) findViewById(R.id.cafe_description_image);

                            Picasso
                                    .with(getBaseContext())
                                    .load(cafe[0].getSrc())
                                    .error(R.drawable.image_loading_error)
                                    .fit()
                                    .into(imageView);
                            descAddress.setText(cafe[0].getAdress());
                            descText.setText(cafe[0].getDescription());

                            Button editButton = (Button) findViewById(R.id.edit_button);
                            editButton.setOnClickListener(view -> {
                                Activity activity = (Activity) getWindow().getContext();
                                Intent intent = new Intent(activity, EditCafeInfoActivity.class);
                                intent.putExtra("id", cafe[0].getCafeId());
                                activity.startActivity(intent);
                            });
                        }
                );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
