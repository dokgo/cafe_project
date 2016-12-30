package com.study.dokgo.projectcafe.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.study.dokgo.projectcafe.R;
import com.study.dokgo.projectcafe.models.NetworkAPI;
import com.study.dokgo.projectcafe.presenter.RetrofitAPI;

import retrofit2.Retrofit;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class QueryActivity extends AppCompatActivity {

    static NetworkAPI networkAPI;
    static EditText query_box;
    static TextView result_box;
    static String str = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);
        query_box = (EditText) findViewById(R.id.query_box);
        result_box = (TextView) findViewById(R.id.query_result);

        Button send = (Button) findViewById(R.id.send);
        send.setOnClickListener(view -> {
            networkAPI =
                    new RetrofitAPI()
                    .getRetrofit()
                    .create(NetworkAPI.class);

            networkAPI
                    .getFreeQuery(query_box.getText().toString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(string -> {
                        setResult(string);
                        //str += string;

                    }, error -> {
                        error.printStackTrace();
                    }, () -> {
                        Log.i("completed", "OK");
                       // setResult(str);
                    });
        });

    }

    private static void setResult(String str) {
        result_box.setText(str);
    }

}
