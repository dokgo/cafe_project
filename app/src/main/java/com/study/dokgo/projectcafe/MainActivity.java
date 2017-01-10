package com.study.dokgo.projectcafe;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;

import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.study.dokgo.projectcafe.models.Cafe;
import com.study.dokgo.projectcafe.models.Cafes;
import com.study.dokgo.projectcafe.models.NetworkAPI;
import com.study.dokgo.projectcafe.presenter.CafeListAdapter;
import com.study.dokgo.projectcafe.presenter.FilterDialog;
import com.study.dokgo.projectcafe.presenter.RetrofitAPI;
import com.study.dokgo.projectcafe.view.AddCafeActivity;
import com.study.dokgo.projectcafe.view.LoginActivity;
import com.study.dokgo.projectcafe.view.QueryActivity;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    Subscription subscription;
    NetworkAPI networkAPI;
    public static List<Cafe> cafeList = new LinkedList<>();
    public static List<Cafe> cafeListCopy;
    Context context;
    public static CafeListAdapter cafeListAdapter;
    SharedPreferences preferences;
    public static int user_status;
    public static String user_email;
    private CrystalRangeSeekbar rangeSeekbar;
    private String baseUrl = "http://cafepro.esy.es/";



    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, (a, b) -> {
                    MainActivity.super.onBackPressed();

                }).create().show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        preferences = getSharedPreferences(LoginActivity.USER_PREFERENCES, Context.MODE_PRIVATE);
        user_status = preferences.getInt(LoginActivity.USER_STATUS, -1);
        user_email = preferences.getString(LoginActivity.USER_EMAIL, null);
        Log.e("STATUS", Integer.toString(preferences.getInt(LoginActivity.USER_STATUS, -1)));


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            startActivity(new Intent(this, AddCafeActivity.class));
        });
        if (user_status != 0) {
            fab.hide();
        }

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

        rvList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (user_status == 0 && dy > 0 && fab.isShown()) {
                    fab.hide();
                } else if (user_status == 0 && dy < 0 && !fab.isShown()) {
                    fab.show();
                }
            }

            /*@Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    fab.show();
                }

                super.onScrollStateChanged(recyclerView, newState);
            }*/
        });


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
                    for (Cafe cafe : cafeListCopy) {
                        if (cafe.getName().toLowerCase().contains(lower)) {
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
            String url = baseUrl + "generatePdf.php";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
            return true;
        }

        if (id == R.id.free_query) {
            startActivity(new Intent(this, QueryActivity.class));
            return true;
        }

        if (id == R.id.dismiss_filter) {
            cafeListAdapter
                    .updateAll(cafeListCopy);
            return true;
        }

        if (id == R.id.action_cafe_filter) {

            showDialog();
            //TODO:implement logic

        }

        if (id == R.id.action_sort_by_name_up && cafeListAdapter != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = this.getLayoutInflater();
            builder.setTitle("Sort by").setView(inflater.inflate(R.layout.main_sort_dialog, null));
            builder.setPositiveButton("Sort", (dialog, i) -> dialog.dismiss());
            builder.setNegativeButton("Cancel", (dialog, i) -> {
                Collections.sort(cafeList, (a, b) -> a.getName().compareTo(b.getName()));
                cafeListAdapter.notifyDataSetChanged();
                dialog.cancel();
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            RadioGroup rg = (RadioGroup) dialog.findViewById(R.id.sort_radio_group);
            rg.setOnCheckedChangeListener((g, i) -> {
                switch (i) {
                    case R.id.name_up:
                        Collections.sort(cafeList, (a, b) -> a.getName().compareTo(b.getName()));
                        cafeListAdapter.notifyDataSetChanged();
                        break;
                    case R.id.name_down:
                        Collections.sort(cafeList, (b, a) -> a.getName().compareTo(b.getName()));
                        cafeListAdapter.notifyDataSetChanged();
                        break;
                    case R.id.rank_up:
                        Collections.sort(cafeList, Cafe::compareToUp);
                        cafeListAdapter.notifyDataSetChanged();
                        break;
                    case R.id.rank_down:
                        Collections.sort(cafeList, Cafe::compareToDown);
                        cafeListAdapter.notifyDataSetChanged();
                        break;
                }
            });


            /*Collections.sort(cafeList, (a, b) -> a.getName().compareTo(b.getName()));
            cafeListAdapter.notifyDataSetChanged();*/
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showDialog(){
        FragmentManager manager = getSupportFragmentManager();
        FilterDialog filterDialog = FilterDialog.newInstance("Filter by");
        filterDialog.show(manager, "namre");

    }

}
