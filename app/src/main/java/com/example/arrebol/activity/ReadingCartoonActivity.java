package com.example.arrebol.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arrebol.R;
import com.example.arrebol.adapter.CartoonContentAdapter;
import com.example.arrebol.entity.Chapter;
import com.example.arrebol.utils.HttpRequestUtils;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ReadingCartoonActivity extends AppCompatActivity implements View.OnClickListener{

    private int chosenID;
    private String url;
    private ArrayList<String> imgUrls = new ArrayList<>();
    private Handler handler = new Handler();
    private Context context;

    //顶部操作栏的布局
    private RelativeLayout cartoon_content_top_rl;

    //底部操作栏的布局
    private LinearLayout cartoon_content_bottom_ll;

    private ImageView back_iv, favorite_iv, share_iv;
    private SeekBar cartoon_read_sb;
    private TextView cartoon_previous_tv, cartoon_next_tv,
            current_chapter_tv, current_page_tv;

    private RecyclerView cartoon_content_rcv;
    private CartoonContentAdapter cartoonContentAdapter;

    private Chapter chapter = new Chapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_cartoon);

        context = this;

        chosenID = getIntent().getIntExtra("chosenID", 1);
        chapter = (Chapter) getIntent().getSerializableExtra("chapter");

        initView();

        reloadView();

        initListener();
        //Log.d("zcc", "onCreate: " + chosenID + " "+ url);
    }

    private void initListener() {

        CartoonContentAdapter.OnItemClickListener onItemClickListener = new CartoonContentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                hideOrHidden();
                cartoon_read_sb.setProgress(position);
            }
        };
        cartoonContentAdapter.setOnItemClickListener(onItemClickListener);

        back_iv.setOnClickListener(this);
        share_iv.setOnClickListener(this);
        favorite_iv.setOnClickListener(this);
        cartoon_next_tv.setOnClickListener(this);
        cartoon_previous_tv.setOnClickListener(this);

        cartoon_read_sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {

                if(fromUser){
                    Toast.makeText(context, "当前进度" + (i+1) + "/" + imgUrls.size(), Toast.LENGTH_SHORT).show();
                    cartoon_content_rcv.scrollToPosition(i);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        cartoon_content_rcv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            //用来标记是否正在向最后一个滑动
            boolean isSlidingToLast = false;

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.d("zcc", "onScrollStateChanged: " + newState);
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                //当不滚动时
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    //获取最后一个完全显示的ItemPosition
                    int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
                    //获取item的数目
                    int totalItemCount = manager.getItemCount();


                    //获取第一个完全显示的ItemPosition
                    int firstVisibleItem = manager.findFirstCompletelyVisibleItemPosition();

                    //找到当前最后显示的图片item位置
                    cartoon_read_sb.setProgress(manager.findFirstVisibleItemPosition());
                    //更新当前页数显示
                    current_page_tv.setText((manager.findFirstVisibleItemPosition() + 1) + " / " + imgUrls.size());

                    if((lastVisibleItem == (totalItemCount - 1) && isSlidingToLast) || firstVisibleItem == 0){
                        hideOrHidden();
                    }

                }else{
                    cartoon_read_sb.setProgress(manager.findLastVisibleItemPosition());
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d("zcc", "onScrolled: ");
                isSlidingToLast = dy > 0;
            }
        });
    }

    private void initView() {
        cartoon_content_rcv = findViewById(R.id.cartoon_content_rcv);
        cartoon_content_top_rl = findViewById(R.id.cartoon_content_top_rl);
        cartoon_content_bottom_ll = findViewById(R.id.cartoon_content_bottom_ll);
        back_iv = findViewById(R.id.back_iv);
        favorite_iv = findViewById(R.id.favorite_iv);
        share_iv = findViewById(R.id.share_iv);
        cartoon_read_sb = findViewById(R.id.cartoon_read_sb);
        cartoon_previous_tv = findViewById(R.id.cartoon_previous_tv);
        cartoon_next_tv = findViewById(R.id.cartoon_next_tv);
        current_chapter_tv = findViewById(R.id.current_chapter_tv);
        current_page_tv = findViewById(R.id.current_page_tv);

        //初始化章节名称
        current_chapter_tv.setText(chapter.getCurrentChapterName());

        cartoon_content_rcv.setLayoutManager(new LinearLayoutManager(this));
        cartoonContentAdapter = new CartoonContentAdapter(this, imgUrls);
        cartoon_content_rcv.setAdapter(cartoonContentAdapter);
    }

    //获取数据
    private void initData() {
        imgUrls.clear();

        Map<String, String> params = new HashMap<>();
        params.put("mhurl2", url);
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
                HttpRequestUtils.parseCartoonContentJson(response.body().string(), imgUrls);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        cartoonContentAdapter.notifyDataSetChanged();
                        //设置seekbar的最大进度
                        cartoon_read_sb.setMax(imgUrls.size() - 1);
                        cartoon_read_sb.setProgress(0);
                        //初始化阅读页数
                        current_page_tv.setText("1 / " + imgUrls.size());
                        cartoon_content_rcv.scrollToPosition(0);
                        current_chapter_tv.setText(chapter.getCurrentChapterName());
                    }
                });
            }
        });
    }

    /**
     * 启动活动
     */
    public static void startActivity(Context context, int chosenID,Chapter chapter){
        Intent intent = new Intent(context, ReadingCartoonActivity.class);
        intent.putExtra("chosenID", chosenID);
        intent.putExtra("chapter",chapter);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_iv:
                finish();
                break;
            case R.id.share_iv:

                break;
            case R.id.favorite_iv:
                Log.d("zcc", "onClick: 点击了");
                if("unfavorite".equals(favorite_iv.getTag())){
                    favorite_iv.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_24));
                    favorite_iv.setTag("favorite");
                    //收藏操作
                }else{
                    favorite_iv.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_star_border_24));
                    favorite_iv.setTag("unfavorite");
                    //取消收藏操作
                }
                break;
            case R.id.cartoon_previous_tv:
                if(chapter.getPreviousChapter() == null){
                    //表示为第一话，没有前驱了
                    Toast.makeText(context, getResources().getString(R.string.no_previous_chapter),
                            Toast.LENGTH_SHORT).show();
                }else{
                    chapter = chapter.getPreviousChapter();
                    reloadView();
                }
                break;
            case R.id.cartoon_next_tv:
                if(chapter.getNextChapter() == null){
                    //表示为第一话，没有前驱了
                    Toast.makeText(context, getResources().getString(R.string.no_next_chapter),
                            Toast.LENGTH_SHORT).show();
                }else{
                    chapter = chapter.getNextChapter();
                    reloadView();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 加载视图
     */
    public void reloadView(){
        url = chapter.getUrl();
        initData();


        Toast.makeText(context, chapter.getCurrentChapterName(), Toast.LENGTH_SHORT).show();
    }

    /**
     * 隐藏或显示操作栏
     */
    public void hideOrHidden(){
        //顶部操作栏的显示与隐藏 底部操作栏的显示与隐藏
        if (cartoon_content_top_rl.getVisibility() == View.VISIBLE) {
            cartoon_content_top_rl.setVisibility(View.GONE);
            cartoon_content_bottom_ll.setVisibility(View.GONE);
            current_page_tv.setVisibility(View.VISIBLE);
        } else {
            cartoon_content_top_rl.setVisibility(View.VISIBLE);
            cartoon_content_bottom_ll.setVisibility(View.VISIBLE);
            current_page_tv.setVisibility(View.GONE);
        }
    }

}