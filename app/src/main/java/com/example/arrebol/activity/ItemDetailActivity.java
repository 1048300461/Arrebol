package com.example.arrebol.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.arrebol.R;
import com.example.arrebol.adapter.ChapterAdapter;
import com.example.arrebol.entity.Chapter;
import com.example.arrebol.entity.SearchResult;
import com.example.arrebol.utils.HttpRequestUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 详细内容页面
 */
public class ItemDetailActivity extends AppCompatActivity {

    private Button search_read_btn, favorite_btn;

    private Context context;

    private TextView search_name, search_tag, search_author, search_introduce;

    private ImageView cover_iv, reverse_iv;

    private int chosenID = 2;

    private ArrayList<Chapter> characters = new ArrayList<>();

    private View split_v;

    //章节的recyclerview
    private RecyclerView chapter_rcv;

    //chapter_rcv的适配器
    private ChapterAdapter chapterAdapter;

    private Handler handler = new Handler();

    private static String detailUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        context = this;

        chosenID = getIntent().getIntExtra("chosenID", 1);
        detailUrl = getIntent().getStringExtra("url");

        //初始化页面
        initView();

        initData();

        chapter_rcv.setLayoutManager(new LinearLayoutManager(this));
        chapterAdapter = new ChapterAdapter(context, characters);
        chapter_rcv.setAdapter(chapterAdapter);

        initListener();

        EventBus.getDefault().register(this);
    }

    /**
     * 监听事件的初始化
     */
    private void initListener() {
        reverse_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Collections.reverse(characters);
                chapterAdapter.notifyDataSetChanged();
                //滑动到顶部
                chapter_rcv.scrollToPosition(0);
            }
        });

        ChapterAdapter.OnItemClickListener onItemClickListener = new ChapterAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String url) {
                if(chosenID == 1){
                    ReadingNovelActivity.startActivity(context, chosenID, url);
                }else if(chosenID == 2){
                    ReadingCartoonActivity.startActivity(context, chosenID, url);
                }else{
                    WatchingFilmActivity.startActivity(context, chosenID, url);
                }
            }
        };
        chapterAdapter.setOnItemClickListener(onItemClickListener);
    }


    /**
     * 初始化页面
     */
    private void initView() {
        search_read_btn = findViewById(R.id.search_read_btn);
        favorite_btn = findViewById(R.id.favorite_btn);
        reverse_iv = findViewById(R.id.reverse_iv);

        chapter_rcv = findViewById(R.id.chapter_rcv);

        split_v = findViewById(R.id.split_v);

        favorite_btn.setVisibility(View.VISIBLE);
        split_v.setVisibility(View.GONE);
        search_read_btn.setText(context.getResources().getString(R.string.start_reading));


        if(chosenID == 1){
            search_read_btn.setBackground(context.getResources().getDrawable(R.drawable.cycle_novel_color));
            favorite_btn.setBackground(context.getResources().getDrawable(R.drawable.cycle_novel_color));
        }else if(chosenID == 2){
            search_read_btn.setBackground(context.getResources().getDrawable(R.drawable.cycle_cartoon_color));
            favorite_btn.setBackground(context.getResources().getDrawable(R.drawable.cycle_cartoon_color));
        }else{
            search_read_btn.setBackground(context.getResources().getDrawable(R.drawable.cycle_filmtv_color));
            favorite_btn.setBackground(context.getResources().getDrawable(R.drawable.cycle_filmtv_color));
        }

        search_name = findViewById(R.id.search_name_tv);
        search_tag = findViewById(R.id.search_tag_tv);
        search_author = findViewById(R.id.search_author_tv);
        search_introduce = findViewById(R.id.search_introduce_tv);

        cover_iv = findViewById(R.id.cover_iv);

    }


    /**
     * 章节数据的加载
     */
    private void initData() {
//        for(int i = 0; i < 15; i++){
//            Chapter chapter = new Chapter("第" + i + "章", i+"");
//            characters.add(chapter);
//        }
        characters.clear();

        Map<String, String> params = new HashMap<>();
        if(chosenID == 1) params.put("xsurl1", detailUrl);
        if(chosenID == 2) params.put("mhurl1", detailUrl);
        if(chosenID == 3) params.put("ysurl", detailUrl);
        //Log.d("zcc", detailUrl + "");

        Call call = HttpRequestUtils.getCall("http://api.pingcc.cn/", params);

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
                if(chosenID == 1 || chosenID == 2){
                    //Log.d("zcc", "onResponse: " + response.body().toString());
                    HttpRequestUtils.parseDetailUrlsJson(response.body().string(), characters);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            chapterAdapter.notifyDataSetChanged();
                            //Log.d("zcc", "run: " + characters.size());
                        }
                    });
                }
            }
        });


    }

    /**
     * 启动活动
     */
    public static void startActivity(Context context, int chosenID, String detailUrl){
        Intent intent = new Intent(context, ItemDetailActivity.class);
        intent.putExtra("chosenID", chosenID);
        intent.putExtra("url", detailUrl);
        context.startActivity(intent);
    }

    /**
     * 收到点击子项的内容
     */
    @SuppressLint("SetTextI18n")
    @Subscribe(sticky = true, threadMode = ThreadMode.BACKGROUND)
    public void updateUI(SearchResult searchResult){
        search_name.setText(searchResult.getName());
        search_tag.setText(context.getString(R.string.type) + searchResult.getTag());
        search_author.setText(context.getString(R.string.author) + searchResult.getAuthor());
        search_introduce.setText(context.getString(R.string.introduce) + searchResult.getIntroduce());

        Glide.with(context).load(searchResult.getCover())
                .placeholder(R.drawable.cover)
                .into(cover_iv);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //取消注册unregister
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }
}