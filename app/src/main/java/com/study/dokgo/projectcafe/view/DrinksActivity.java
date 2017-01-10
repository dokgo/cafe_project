package com.study.dokgo.projectcafe.view;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.study.dokgo.projectcafe.MainActivity;
import com.study.dokgo.projectcafe.R;
import com.study.dokgo.projectcafe.models.Drink;
import com.study.dokgo.projectcafe.models.Drinks;
import com.study.dokgo.projectcafe.models.NetworkAPI;
import com.study.dokgo.projectcafe.presenter.DrinkListAdapter;
import com.study.dokgo.projectcafe.presenter.RetrofitAPI;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DrinksActivity extends AppCompatActivity {

    Subscription subscription;
    NetworkAPI networkAPI;
    String cafeId;
    List<Drink> drinkList;
    List<Drink> drinkListCopy;
    DrinkListAdapter drinkListAdapter;
    FloatingActionButton fab;
    String baseUrl = "http://cafepro.esy.es/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drinks);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Drinks");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(arrow -> onBackPressed());

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddDrinkActivity.class);
            intent.putExtra("id", cafeId);
            this.startActivity(intent);
        });
        if (MainActivity.user_status != 0) {
            fab.hide();
        }


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

        manageList();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_drinks, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search_menu);

        SearchManager searchManager = (SearchManager) DrinksActivity.this.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {

            searchView.setSearchableInfo(searchManager.getSearchableInfo(DrinksActivity.this.getComponentName()));
            searchView.setOnCloseListener(() -> {
               /* String s = Integer.toString(drinkListCopy.size());
                Toast t = Toast.makeText(DishesActivity.this, s, Toast.LENGTH_SHORT);
                t.show();*/

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
                    List<Drink> newList = new LinkedList<>();
                    String lower = newText.toLowerCase();
                    for(Drink dish: drinkListCopy){
                        if (dish.getName().toLowerCase().contains(lower)){
                            newList.add(dish);
                        }
                    }

                    drinkListAdapter
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

        if (id == R.id.action_menu_statistic) {
            String url = baseUrl + "generatePdf2.php?table=drink&id=" + cafeId;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_sort_menu_by_name_up && drinkListAdapter != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            builder.setTitle("Sort by").setView(inflater.inflate(R.layout.drinks_sort_dialog, null));
            builder.setPositiveButton("Sort", (dialog,i) -> dialog.dismiss());
            builder.setNegativeButton("Cancel", (dialog,i) -> {Collections.sort(drinkList, (a, b) -> a.getName().compareTo(b.getName()));
                drinkListAdapter.notifyDataSetChanged(); dialog.cancel();});
            AlertDialog dialog = builder.create();
            dialog.show();
            RadioGroup rg = (RadioGroup) dialog.findViewById(R.id.sort_radio_group);
            rg.setOnCheckedChangeListener((g,i) -> {
                switch (i) {
                    case R.id.name_up:
                        Collections.sort(drinkList, (a, b) -> a.compareToName(b));
                        drinkListAdapter.notifyDataSetChanged();
                        break;
                    case R.id.name_down:
                        Collections.sort(drinkList, (b, a) -> a.compareToName(b));
                        drinkListAdapter.notifyDataSetChanged();
                        break;
                    case R.id.price_up:
                        Collections.sort(drinkList, (a, b) -> a.compareToCost(b));
                        drinkListAdapter.notifyDataSetChanged();
                        break;
                    case R.id.price_down:
                        Collections.sort(drinkList,(a, b) -> b.compareToCost(a));
                        drinkListAdapter.notifyDataSetChanged();
                        break;
                    case R.id.volume_up:
                        Collections.sort(drinkList,(a, b) -> a.compareToVolume(b));
                        drinkListAdapter.notifyDataSetChanged();
                        break;
                    case R.id.volume_down:
                        Collections.sort(drinkList,(a, b) -> b.compareToVolume(a));
                        drinkListAdapter.notifyDataSetChanged();
                        break;
                }
            });
            return true;
        }

        if (id == R.id.action_sort_menu_by && drinkListAdapter != null) {
            Collections.sort(drinkList, (a, b) -> b.getName().compareTo(a.getName()));
            drinkListAdapter.notifyDataSetChanged();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    boolean setList(List<Drink> list) {
        try {
            RecyclerView rvList =
                    (RecyclerView) findViewById(R.id.drinks_list_recycler_view);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            rvList.setLayoutManager(layoutManager);
            rvList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (MainActivity.user_status == 0 && dy > 0 && fab.isShown()) {
                        fab.hide();
                    } else if (MainActivity.user_status == 0 &&dy < 0 && !fab.isShown()) {
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

            drinkListAdapter = new DrinkListAdapter(list, networkAPI);
            rvList.setAdapter(drinkListAdapter);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Subscription getSub(Observable<Drinks> mydata, List<Drink> cafeList) {
        return mydata.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(Drinks::getMenu)
                .flatMap(Observable::from)
                .subscribe(cafe -> {
                    Log.e("CAFE_DATA", cafe.toString());
                    cafeList.add(cafe);
                }, error -> {
                    Log.e("onError", "ERORR");
                }, () -> {
                    if (cafeList.isEmpty()){
                        Toast t = Toast.makeText(DrinksActivity.this, "No drinks in menu!", Toast.LENGTH_LONG);
                        t.show();
                    }
                    setList(cafeList);
                    drinkListCopy = new LinkedList<>(cafeList);
                });
    }

    public void manageList(){

        drinkList  = new LinkedList<>();
        subscription = getSub(networkAPI.getDrinksById(cafeId), drinkList);
    }
}
