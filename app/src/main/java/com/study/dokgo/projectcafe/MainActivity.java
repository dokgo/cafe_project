package com.study.dokgo.projectcafe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.study.dokgo.projectcafe.models.Cafe;
import com.study.dokgo.projectcafe.models.CafeService;
import com.study.dokgo.projectcafe.models.NetworkAPI;
import com.study.dokgo.projectcafe.presenter.CafeListAdapter;
import com.study.dokgo.projectcafe.presenter.RetrofitAPI;

import java.util.LinkedList;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    Subscription subscription;
    NetworkAPI cafeService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cafeService =
                new RetrofitAPI()
                        .getRetrofit()
                        .create(NetworkAPI.class);

        manageList();
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

    @Override
    protected void onDestroy() {
        subscription.unsubscribe();
        Log.e("onDestroy", "DESTROYED");
        super.onDestroy();
    }

    @Override
    protected void onPause(){
        subscription.unsubscribe();
        super.onPause();
    }

    @Override
    protected void onResume(){
        manageList();
        super.onResume();
    }

    boolean setList(List<Cafe> list) {
        try {
            RecyclerView rvList =
                    (RecyclerView) findViewById(R.id.cafe_list_recycler_view);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            rvList.setLayoutManager(layoutManager);

            CafeListAdapter cafeListAdapter = new CafeListAdapter(list);
            rvList.setAdapter(cafeListAdapter);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Subscription getSub(Observable<CafeService> mydata, List<Cafe> cafeList) {
        return mydata.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(CafeService::getCafes)
                .flatMap(Observable::from)
                .subscribe(cafe -> {
                    Log.e("CAFE_DATA", cafe.toString());
                    cafeList.add(cafe);
                }, error -> {
                    Log.e("onError", "ERORR");
                }, () -> {
                    setList(cafeList);
                });
    }

    public void manageList(){

        List<Cafe> cafeList = new LinkedList<>();
        subscription = getSub(cafeService.getCafeServiceData(), cafeList);
    }

}
