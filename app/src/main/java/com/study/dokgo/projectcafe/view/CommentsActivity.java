package com.study.dokgo.projectcafe.view;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.RadioGroup;
import android.widget.Toast;

import com.study.dokgo.projectcafe.MainActivity;
import com.study.dokgo.projectcafe.R;
import com.study.dokgo.projectcafe.models.Comment;
import com.study.dokgo.projectcafe.models.Comments;
import com.study.dokgo.projectcafe.models.NetworkAPI;
import com.study.dokgo.projectcafe.presenter.CommentListAdapter;
import com.study.dokgo.projectcafe.presenter.RetrofitAPI;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CommentsActivity extends AppCompatActivity {

    Subscription subscription;
    NetworkAPI networkAPI;
    String cafeId;
    List<Comment> commentList;
    List<Comment> commentListCopy;
    CommentListAdapter commentListAdapter;
    FloatingActionButton fab;
    String baseUrl = "http://cafepro.esy.es/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Comments");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(arrow -> onBackPressed());

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddCommentActivity.class);
            intent.putExtra("id", cafeId);
            this.startActivity(intent);
        });

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
        getMenuInflater().inflate(R.menu.menu_comments, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search_menu);

        SearchManager searchManager = (SearchManager) CommentsActivity.this.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {

            searchView.setSearchableInfo(searchManager.getSearchableInfo(CommentsActivity.this.getComponentName()));
            searchView.setOnCloseListener(() -> {
               /* String s = Integer.toString(commentListCopy.size());
                Toast t = Toast.makeText(CommentsActivity.this, s, Toast.LENGTH_SHORT);
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
                    List<Comment> newList = new LinkedList<>();
                    String lower = newText.toLowerCase();
                    for(Comment comment: commentListCopy){
                        if (comment.getEmail().toLowerCase().contains(lower)){
                            newList.add(comment);
                        }
                    }

                    commentListAdapter
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
            String url = baseUrl + "generatePdf2.php?table=comment&id=" + cafeId;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
            return true;
        }


        /*if (id == R.id.action_sort_menu_by_name_up && commentListAdapter != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            builder.setTitle("Sort by").setView(inflater.inflate(R.layout.comment_sort_dialog, null));
            builder.setPositiveButton("Sort", (dialog,i) -> dialog.dismiss());
            builder.setNegativeButton("Cancel", (dialog,i) -> {Collections.sort(commentList, (a, b) -> a.getEmail().compareTo(b.getEmail()));
                commentListAdapter.notifyDataSetChanged(); dialog.cancel();});
            AlertDialog dialog = builder.create();
            dialog.show();
            RadioGroup rg = (RadioGroup) dialog.findViewById(R.id.sort_radio_group);
            rg.setOnCheckedChangeListener((g,i) -> {
                switch (i) {
                    case R.id.title_up:
                        Collections.sort(commentList, (a, b) -> a.compareToName(b));
                        commentListAdapter.notifyDataSetChanged();
                        break;
                    case R.id.title_down:
                        Collections.sort(commentList, (b, a) -> a.compareToName(b));
                        commentListAdapter.notifyDataSetChanged();
                        break;
                    case R.id.rank_up:
                        Collections.sort(commentList, (a, b) -> a.compareToPrice(b));
                        commentListAdapter.notifyDataSetChanged();
                        break;
                    case R.id.rank_down:
                        Collections.sort(commentList,(a, b) -> b.compareToPrice(a));
                        commentListAdapter.notifyDataSetChanged();
                        break;
                }
            });
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    boolean setList(List<Comment> list) {
        try {
            RecyclerView rvList =
                    (RecyclerView) findViewById(R.id.comment_list_recycler_view);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            rvList.setLayoutManager(layoutManager);

            commentListAdapter = new CommentListAdapter(list, networkAPI);
            rvList.setAdapter(commentListAdapter);
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
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Subscription getSub(Observable<Comments> mydata, List<Comment> cafeList) {
        return mydata.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(Comments::getComments)
                .flatMap(Observable::from)
                .subscribe(cafe -> {
                    Log.e("CAFE_DATA", cafe.toString());
                    cafeList.add(cafe);
                }, error -> {
                    Log.e("onError", "ERORR");
                }, () -> {
                    if (cafeList.isEmpty()){
                        Toast t = Toast.makeText(CommentsActivity.this, "No comments in list!", Toast.LENGTH_LONG);
                        t.show();
                    }
                    setList(cafeList);
                    commentListCopy = new LinkedList<>(cafeList);
                });
    }

    public void manageList(){


        commentList  = new LinkedList<>();
        subscription = getSub(networkAPI.getCommentsById(cafeId), commentList);
    }
}
