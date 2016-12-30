package com.study.dokgo.projectcafe.view;


import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.study.dokgo.projectcafe.MainActivity;
import com.study.dokgo.projectcafe.R;
import com.study.dokgo.projectcafe.models.NetworkAPI;
import com.study.dokgo.projectcafe.models.Comment;
import com.study.dokgo.projectcafe.models.Comments;
import com.study.dokgo.projectcafe.presenter.RetrofitAPI;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class EditCommentActivity extends AppCompatActivity {

    NetworkAPI networkAPI;
    String commentId;
    Comment[] comment = new Comment[1];
    String[] status = new String[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_comment);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(arrow -> onBackPressed());


        try {
            commentId = getIntent().getExtras().get("id").toString();
        } catch (NullPointerException e) {
            return;
        }

        networkAPI =
                new RetrofitAPI()
                        .getRetrofit()
                        .create(NetworkAPI.class);

        networkAPI
                .getCommentById(commentId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(Comments::getComments)
                .flatMap(Observable::from)
                .single()
                .subscribe(
                        cf -> {
                            comment[0] = cf;
                            Log.e("obs", cf.toString());
                        },
                        throwable -> Log.e(getClass().getName(), throwable.toString()),
                        () -> {
                            toolbar.setTitle(comment[0].getEmail());

                            EditText commentContent = (EditText) findViewById(R.id.menu_comment_content_edit);
                            EditText commentRank = (EditText) findViewById(R.id.menu_comment_rank_edit);


                            commentContent.setText(comment[0].getContent());
                            commentRank.setText(comment[0].getRate());

                            Button saveButton = (Button) findViewById(R.id.save_button_edit);
                            Context context = saveButton.getContext();

                            saveButton.setOnClickListener(view -> {

                                if (isEmpty(commentContent) ||
                                        isEmpty(commentRank)
                                        ) {

                                    AlertDialog.Builder builder = new AlertDialog.Builder(saveButton.getContext());
                                    builder.setTitle(R.string.dialog_about_title);
                                    builder.setMessage(R.string.dialog_about_message);
                                    builder.setCancelable(true);
                                    builder.setPositiveButton(android.R.string.ok, (d, w) -> d.dismiss());
                                    alertD(context, R.string.dialog_about_title, R.string.dialog_about_message).show();
                                } else {
                                    networkAPI
                                            .updateComment(
                                                    MainActivity.user_email,
                                                    commentContent.getText().toString(),
                                                    commentRank.getText().toString(),
                                                    commentId
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

                        }
                );
    }


    public AlertDialog alertD(Context context, int title, int text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(text);
        builder.setCancelable(true);
        builder.setPositiveButton(android.R.string.ok,
                (d, w) -> {
                    if (text != R.string.error)
                        finish();
                }
        );
        return builder.create();
    }


    private boolean isEmpty(EditText myeditText) {
        return myeditText.getText().toString().trim().length() == 0;
    }
}
