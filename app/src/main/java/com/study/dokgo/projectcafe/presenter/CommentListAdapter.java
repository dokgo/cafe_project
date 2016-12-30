package com.study.dokgo.projectcafe.presenter;

/**
 * Created by dokgo on 30.12.16.
 */

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.study.dokgo.projectcafe.MainActivity;
import com.study.dokgo.projectcafe.R;
import com.study.dokgo.projectcafe.models.Comment;
import com.study.dokgo.projectcafe.models.NetworkAPI;
import com.study.dokgo.projectcafe.view.EditCommentActivity;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by dokgo on 27.11.16.
 */

public class CommentListAdapter extends
        RecyclerView.Adapter<CommentListAdapter.CafeViewHolder> {

    private List<Comment> commentsList;
    NetworkAPI networkAPI;

    public CommentListAdapter(List<Comment> commentsList, NetworkAPI networkAPI) {
        this.commentsList = commentsList;
        this.networkAPI = networkAPI;
    }

    @Override
    public int getItemCount() {
        return commentsList != null ? commentsList.size() : 0;
    }


    @Override
    public void onBindViewHolder(CafeViewHolder cafeViewHolder, int i) {
        Comment commentsi = commentsList.get(i);

        cafeViewHolder.commentId = commentsi.getCommentId();
        cafeViewHolder.vEmail.setText(commentsi.getEmail());
        cafeViewHolder.vContent.setText("Содержание: " + commentsi.getContent());
        cafeViewHolder.vRate.setText("Рейтинг: " + commentsi.getRate());

        Context context = cafeViewHolder.itemView.getContext();



        if (MainActivity.user_status == 0 || MainActivity.user_email.equals(cafeViewHolder.vEmail.getText().toString())) {

            cafeViewHolder.itemView.setOnClickListener(view -> {
                Activity parent_activity = (Activity) context;
                Intent intent = new Intent(view.getContext(), EditCommentActivity.class);
                intent.putExtra("id", cafeViewHolder.commentId);
                parent_activity.getWindow().setExitTransition(new Explode());
                parent_activity.startActivity(
                        intent,
                        ActivityOptions.makeSceneTransitionAnimation(parent_activity).toBundle()
                );

            });

            cafeViewHolder.itemView.setOnLongClickListener(view -> {
                        String[] status = new String[1];
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Delete?");
                        // builder.setMessage(text);
                        builder.setCancelable(false);
                        builder.setPositiveButton("Yes",
                                (d, w) -> {
                                    networkAPI
                                            .delete("comment", "commentId", cafeViewHolder.commentId)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(n -> {
                                                        if (n.equals("OK")) {
                                                            Log.e("DELETE: ", "SUCCESSFUL");
                                                            status[0] = "OK";
                                                            commentsList.remove(i);
                                                            notifyItemRemoved(i);
                                                        } else
                                                            status[0] = n;
                                                        Log.e("ErrorPHP", n);
                                                    },
                                                    t -> Log.e("DELETE ERROR", t.toString()),
                                                    () -> {
                                                        if (status[0].equals("OK")) {
                                                            alertD(
                                                                    context,
                                                                    R.string.update_status,//TODO:Change string resources names
                                                                    R.string.successful
                                                            ).show();
                                                        } else {
                                                            alertD(
                                                                    context,
                                                                    R.string.update_status,
                                                                    R.string.error
                                                            ).show();
                                                        }

                                                    });

                                }
                        );
                        builder.setNegativeButton("No", (d, w) -> {

                        });
                        builder.create().show();

                        return true;
                    }


            );
        }
    }

    @Override
    public CafeViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.comment_card_view, viewGroup, false);

        return new CafeViewHolder(itemView);
    }

    class CafeViewHolder extends RecyclerView.ViewHolder {
        String commentId;
        TextView vEmail;
        TextView vContent;
        TextView vRate;

        CafeViewHolder(View v) {
            super(v);
            vEmail = (TextView) v.findViewById(R.id.card_comment_email);
            vContent = (TextView) v.findViewById(R.id.card_comment_content);
            vRate = (TextView) v.findViewById(R.id.card_comment_rank);


        }
    }

    private AlertDialog alertD(Context context, int title, int text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(text);
        builder.setCancelable(false);
        builder.setPositiveButton(android.R.string.ok, null);
        return builder.create();
    }


    public void updateAll(List<Comment> newList){
        commentsList.clear();
        commentsList.addAll(newList);
        notifyDataSetChanged();
    }
}
