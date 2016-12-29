package com.study.dokgo.projectcafe.view;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import com.study.dokgo.projectcafe.MainActivity;
import com.study.dokgo.projectcafe.R;
import com.study.dokgo.projectcafe.models.Cafe;
import com.study.dokgo.projectcafe.models.Dish;
import com.study.dokgo.projectcafe.models.Dishes;
import com.study.dokgo.projectcafe.models.NetworkAPI;
import com.study.dokgo.projectcafe.presenter.MenuListAdapter;
import com.study.dokgo.projectcafe.presenter.RetrofitAPI;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MenuActivity extends AppCompatActivity {

    Subscription subscription;
    NetworkAPI networkAPI;
    String cafeId;
    List<Dish> dishList;
    List<Dish> dishListCopy;
    MenuListAdapter menuListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
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
        getMenuInflater().inflate(R.menu.menu_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search_menu);

        SearchManager searchManager = (SearchManager) MenuActivity.this.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {

            searchView.setSearchableInfo(searchManager.getSearchableInfo(MenuActivity.this.getComponentName()));
            searchView.setOnCloseListener(() -> {
               /* String s = Integer.toString(dishListCopy.size());
                Toast t = Toast.makeText(MenuActivity.this, s, Toast.LENGTH_SHORT);
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
                    List<Dish> newList = new LinkedList<>();
                    String lower = newText.toLowerCase();
                    for(Dish dish: dishListCopy){
                        if (dish.getName().toLowerCase().contains(lower)){
                            newList.add(dish);
                        }
                    }

                    menuListAdapter
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
            String url = "http://test.site/generatePdf2.php?id=" + cafeId;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_add_dish) {
            Intent intent = new Intent(this, AddDishActivity.class);
            intent.putExtra("id", cafeId);
            this.startActivity(intent);
            return true;
        }

        if (id == R.id.action_sort_menu_by_name_up && menuListAdapter != null) {
            Collections.sort(dishList, (a, b) -> a.getName().compareTo(b.getName()));
            menuListAdapter.notifyDataSetChanged();
            return true;
        }

        if (id == R.id.action_sort_menu_by_name_down && menuListAdapter != null) {
            Collections.sort(dishList, (a, b) -> b.getName().compareTo(a.getName()));
            menuListAdapter.notifyDataSetChanged();
            return true;
        }

        if (id == R.id.action_sort_menu_by_cost_up && menuListAdapter != null) {
            Collections.sort(dishList, Dish::compareToUpCost);
            menuListAdapter.notifyDataSetChanged();
            return true;
        }

        if (id == R.id.action_sort_menu_by_cost_down && menuListAdapter != null) {
            Collections.sort(dishList, Dish::compareToDownCost);
            menuListAdapter.notifyDataSetChanged();
            return true;
        }

        if (id == R.id.action_sort_menu_by_portion_up && menuListAdapter != null) {
            Collections.sort(dishList, Dish::compareToUpPortion);
            menuListAdapter.notifyDataSetChanged();
            return true;
        }

        if (id == R.id.action_sort_menu_by_portion_down && menuListAdapter != null) {
            Collections.sort(dishList, Dish::compareToDownPortion);
            menuListAdapter.notifyDataSetChanged();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    boolean setList(List<Dish> list) {
        try {
            RecyclerView rvList =
                    (RecyclerView) findViewById(R.id.menu_list_recycler_view);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            rvList.setLayoutManager(layoutManager);

            menuListAdapter = new MenuListAdapter(list, networkAPI);
            rvList.setAdapter(menuListAdapter);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Subscription getSub(Observable<Dishes> mydata, List<Dish> cafeList) {
        return mydata.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(Dishes::getMenu)
                .flatMap(Observable::from)
                .subscribe(cafe -> {
                    Log.e("CAFE_DATA", cafe.toString());
                    cafeList.add(cafe);
                }, error -> {
                    Log.e("onError", "ERORR");
                }, () -> {
                    if (cafeList.isEmpty()){
                        Toast t = Toast.makeText(MenuActivity.this, "No dishes in menu!", Toast.LENGTH_LONG);
                        t.show();
                    }
                    setList(cafeList);
                    dishListCopy = new LinkedList<>(cafeList);
                });
    }

    public void manageList(){


        dishList  = new LinkedList<>();
        subscription = getSub(networkAPI.getMenuById(cafeId), dishList);
    }
}
