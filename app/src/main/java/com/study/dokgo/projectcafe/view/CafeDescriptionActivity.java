package com.study.dokgo.projectcafe.view;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.study.dokgo.projectcafe.R;
import com.study.dokgo.projectcafe.models.Cafe;
import com.study.dokgo.projectcafe.models.Cafes;
import com.study.dokgo.projectcafe.models.NetworkAPI;
import com.study.dokgo.projectcafe.presenter.CafeListAdapter;
import com.study.dokgo.projectcafe.presenter.RetrofitAPI;

import java.util.LinkedList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CafeDescriptionActivity extends AppCompatActivity {

    String cafeId;
    String cafeRank;
    NetworkAPI networkAPI;
    List<Cafe> cafeList = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cafe_description);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(arrow -> onBackPressed());


        try {
            cafeId = getIntent().getExtras().get("id").toString();
            cafeRank = getIntent().getExtras().get("rank").toString();
        } catch (NullPointerException e) {
            e.printStackTrace();
            return;
        }

        networkAPI =
                new RetrofitAPI()
                        .getRetrofit()

                        .create(NetworkAPI.class);

        Cafe[] cafe = new Cafe[1];

        networkAPI
                .getCafeById(cafeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(Cafes::getCafes)
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

                            /*Button editButton = (Button) findViewById(R.id.edit_button);
                            editButton.setOnClickListener(view -> {
                                Activity activity = (Activity) getWindow().getContext();
                                Intent intent = new Intent(activity, EditCafeInfoActivity.class);
                                intent.putExtra("id", cafe[0].getCafeId());
                                activity.startActivity(intent);
                            });*/
                        }
                );

        RecyclerView rvList =
                (RecyclerView) findViewById(R.id.suggested_cafes_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvList.setLayoutManager(layoutManager);

        CafeListAdapter cafeListAdapter = new CafeListAdapter(cafeList, R.layout.suggested_cafe_card_view);
        rvList.setAdapter(cafeListAdapter);

        networkAPI
                .getCafesData(cafeRank)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(Cafes::getCafes)
                .flatMap(Observable::from)
                .subscribe(cf -> {
                    Log.e("CAFE_DATA", cf.toString());
                    cafeList.add(cf);
                    cafeListAdapter.notifyItemInserted(cafeList.size() - 1);
                }, error -> {
                    Log.e("onError", error.toString());
                }, () -> {

                });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cafe, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();



        if (id == R.id.action_show_menu) {
            Intent intent = new Intent(this, MenuActivity.class);
            intent.putExtra("id", cafeId);
            this.startActivity(intent);
            return true;
        }

        if (id == R.id.action_edit_cafe) {
            Intent intent = new Intent(this, EditCafeInfoActivity.class);
            intent.putExtra("id", cafeId);
            this.startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
