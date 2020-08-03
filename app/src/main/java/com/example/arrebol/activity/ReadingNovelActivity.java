package com.example.arrebol.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arrebol.R;
import com.example.arrebol.entity.Chapter;
import com.example.arrebol.utils.HttpRequestUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ReadingNovelActivity extends AppCompatActivity {

    private int chosenID;
    private String url;
    private Chapter chapter;
    private TextView content_tv;
    private Handler handler = new Handler();
    private Context context;
    private ArrayList<String> contents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_novel);

        chosenID = getIntent().getIntExtra("chosenID", 1);
        chapter = (Chapter) getIntent().getSerializableExtra("chapter");
        url = chapter.getUrl();
        context = this;

        initView();

        initData();

        Log.d("zcc", "onCreate: " + chosenID + " "+ url);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        contents.clear();

        Map<String, String> params = new HashMap<>();
        params.put("xsurl2", url);
        Call call = HttpRequestUtils.getCall("http://api.pingcc.cn/",params);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //数据请求失败
                        Toast.makeText(context, context.getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                HttpRequestUtils.parseNovelContentJson(response.body().string(), contents);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        content_tv.setText(contents.get(0) + "\n" + contents.get(1) + contents.get(2));
                    }
                });
            }
        });
    }

    /**
     * 初始化视图
     */
    private void initView() {
        content_tv = findViewById(R.id.content_tv);
    }

    /**
     * 启动活动
     */
    public static void startActivity(Context context, int chosenID, Chapter chapter){
        Intent intent = new Intent(context, ReadingNovelActivity.class);
        intent.putExtra("chosenID", chosenID);
        intent.putExtra("chapter", chapter);
        context.startActivity(intent);
    }
}