package com.study.dokgo.projectcafe;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.study.dokgo.projectcafe.models.Cafe;
import com.study.dokgo.projectcafe.models.Cafes;
import com.study.dokgo.projectcafe.models.NetworkAPI;
import com.study.dokgo.projectcafe.presenter.CafeListAdapter;
import com.study.dokgo.projectcafe.presenter.RetrofitAPI;
import com.study.dokgo.projectcafe.view.AddCafeActivity;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    Subscription subscription;
    NetworkAPI networkAPI;
    List<Cafe> cafeList = new LinkedList<>();
    List<Cafe> cafeListCopy;
    Context context;
    CafeListAdapter cafeListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       // cafeList = new LinkedList<>();
        context = this;

        networkAPI =
                new RetrofitAPI()
                        .getRetrofit()
                        .create(NetworkAPI.class);

        RecyclerView rvList =
                (RecyclerView) findViewById(R.id.cafe_list_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvList.setLayoutManager(layoutManager);

        cafeListAdapter = new CafeListAdapter(cafeList);
        rvList.setAdapter(cafeListAdapter);

        networkAPI
                .getCafesData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(Cafes::getCafes)
                .flatMap(Observable::from)
                .subscribe(cafe -> {
                    Log.e("CAFE_DATA", cafe.toString());
                    cafeList.add(cafe);
                    cafeListAdapter.notifyItemInserted(cafeList.size() - 1);
                }, error -> {
                    Log.e("onError", "ERORR");
                }, () -> {
                    cafeListCopy = new LinkedList<>(cafeList);
                });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(MainActivity.this.getComponentName()));
            searchView.setOnCloseListener(() -> {
                /*String s = Integer.toString(cafeListCopy.size());
                Toast t = Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT);
                t.show();
                */
                return false;
            });
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    // User pressed the search button
                    return false;
                }


                @Override
                public boolean onQueryTextChange(String newText) {
                    Log.e("input", newText);
                    List<Cafe> newList = new LinkedList<>();
                    String lower = newText.toLowerCase();
                    for(Cafe cafe: cafeListCopy){
                        if (cafe.getName().toLowerCase().contains(lower)){
                            newList.add(cafe);
                        }
                    }

                    cafeListAdapter
                            .updateAll(newList);
                    return false;
                }
            });


        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_cafe_statistic) {
            String url = "http://test.site/generatePdf.php";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_sort_by_name_up && cafeListAdapter != null) {
            Collections.sort(cafeList, (a, b) -> a.getName().compareTo(b.getName()));
            cafeListAdapter.notifyDataSetChanged();
            return true;
        }

        if (id == R.id.action_sort_by_name_down && cafeListAdapter != null) {
            Collections.sort(cafeList, (a, b) -> b.getName().compareTo(a.getName()));
            cafeListAdapter.notifyDataSetChanged();
            return true;
        }

        if (id == R.id.action_sort_by_rank_up && cafeListAdapter != null) {
            Collections.sort(cafeList, Cafe::compareToUp);
            cafeListAdapter.notifyDataSetChanged();
            return true;
        }

        if (id == R.id.action_sort_by_rank_down && cafeListAdapter != null) {
            Collections.sort(cafeList, Cafe::compareToDown);
            cafeListAdapter.notifyDataSetChanged();
            return true;
        }

        if (id == R.id.action_add_cafe) {

            startActivity(new Intent(this, AddCafeActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
