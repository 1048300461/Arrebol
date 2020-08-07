package com.example.arrebol.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.arrebol.R;
import com.example.arrebol.entity.Chapter;
import com.example.arrebol.utils.HttpRequestUtils;
import com.example.arrebol.widget.Reader1.PageTurnView;
import com.example.arrebol.widget.Reader1.TwistView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ReadingNovelActivity extends AppCompatActivity {

    private int chosenID;
    private String url;
    private Chapter chapter;
    private Handler handler = new Handler();
    private Context context;
    private ArrayList<String> contents = new ArrayList<>();
    private TwistView twistView;
    private PageTurnView pageTurnView;

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
                        //content_tv.setText(contents.get(0) + "\n" + contents.get(1) + contents.get(2));
                    }
                });
            }
        });
    }

    /**
     * 初始化视图
     */
    private void initView() {
        pageTurnView = findViewById(R.id.reading_page);

        Bitmap bitmap = null;
        List<Bitmap> bitmaps = new ArrayList<>();

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.page_img_a);
        bitmaps.add(bitmap);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.page_img_b);
        bitmaps.add(bitmap);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.page_img_c);
        bitmaps.add(bitmap);
//        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.page_img_d);
//        bitmaps.add(bitmap);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.page_img_e);
        bitmaps.add(bitmap);

        pageTurnView.setBitmaps(bitmaps);
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