package com.example.arrebol.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arrebol.R;
import com.example.arrebol.adapter.HistorySearchAdapter;
import com.example.arrebol.adapter.SearchResultAdapter;
import com.example.arrebol.adapter.TopSearchAdapter;
import com.example.arrebol.entity.History;
import com.example.arrebol.entity.SearchResult;
import com.example.arrebol.entity.Top;
import com.example.arrebol.utils.HttpRequestUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 搜索页面
 */
public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    private Context context;

    private TextView cancel_tv, top_search_tv;

    private SearchView search_v;

    //热门搜索列表
    private ArrayList<Top> topArrayList;

    //历史搜索列表
    private ArrayList<History> historyArrayList;

    //搜索到结果的列表
    private ArrayList<SearchResult> searchResultArrayList = new ArrayList<>();

    private RelativeLayout history_rl;

    private LinearLayout root_ly, root_result_ly, not_find_root_ll;

    //热门搜索的recyclerview
    private RecyclerView top_rcv;

    //热门搜索的adapter
    private TopSearchAdapter topSearchAdapter;

    //历史搜索的recyclerview
    private RecyclerView history_rcv;

    //历史搜索的adapter
    private HistorySearchAdapter historySearchAdapter;

    //搜索到结果的recyclerview
    private RecyclerView search_result_rv;

    //搜索到结果的adapter
    private SearchResultAdapter searchResultAdapter;

    //handler
    private Handler handler = new Handler();

    //搜索请求的网址
    private String[] urls = {"http://api.pingcc.cn/", "http://api.pingcc.cn/", "http://api.pingcc.cn/"};

    //历史搜索的清除历史搜索
    ImageView clear_iv;

    //当前Fragment的ID
    int chosenID;

    private boolean isFind = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        chosenID = getIntent().getIntExtra("chosenID", 0);

        context = this;

        //fake data
        initTopData();

        //加载历史搜索数据
        initHistoryData();

        //初始化视图
        initView();

        //热门搜索的recyclerview设置
        top_rcv.setLayoutManager(new LinearLayoutManager(this));
        topSearchAdapter = new TopSearchAdapter(topArrayList, this);
        top_rcv.setAdapter(topSearchAdapter);

        //历史搜索的recyclerview设置
        history_rcv.setLayoutManager(new GridLayoutManager(this, 2));
        historySearchAdapter = new HistorySearchAdapter(historyArrayList, this);
        history_rcv.setAdapter(historySearchAdapter);

        search_result_rv.setLayoutManager(new LinearLayoutManager(this));

        initListener();

        EventBus.getDefault().register(this);
    }

    /**
     * 加载搜索结果页面
     */
    private void loadSearchResultView() {
        //搜索到结果recyclerview设置
        searchResultAdapter = new SearchResultAdapter(this, searchResultArrayList);
        search_result_rv.setAdapter(searchResultAdapter);
    }

    /**
     * 加载历史数据
     */
    private void initHistoryData() {
        historyArrayList = (ArrayList<History>) LitePal.findAll(History.class);
        Collections.reverse(historyArrayList);
    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        cancel_tv.setOnClickListener(this);
        clear_iv.setOnClickListener(this);

        search_v.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String searchInfo) {
                //隐藏热门搜索和历史搜索的根布局
                root_ly.setVisibility(View.GONE);

                //通知修改历史搜索
                EventBus.getDefault().post(new History(searchInfo));

                //获取数据
                requestData(searchInfo);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(s.length() == 0){
                    topSearchAdapter.closeFilter(topArrayList);
                    top_search_tv.setVisibility(View.VISIBLE);
                    if(historySearchAdapter.getItemCount() != 0){
                        history_rl.setVisibility(View.VISIBLE);
                    }
                }else{
                    topSearchAdapter.setFilter(filter(topArrayList, s));
                    history_rl.setVisibility(View.GONE);
                    top_search_tv.setVisibility(View.GONE);
                }
                //显示热门搜索和历史搜索的根布局
                root_ly.setVisibility(View.VISIBLE);
                //隐藏搜索结果的根布局
                root_result_ly.setVisibility(View.GONE);
                return false;
            }
        });

        //历史搜索的回调事件实现
        HistorySearchAdapter.TextViewClickListener textViewClickListener = new HistorySearchAdapter.TextViewClickListener() {
            @Override
            public void onTvClickListener(String name) {
                //隐藏热门搜索和历史搜索的根布局
                root_ly.setVisibility(View.GONE);

                //通知修改历史搜索
                EventBus.getDefault().post(new History(name));

                //获取数据
                requestData(name);

                search_v.setQuery(name, false);
            }
        };
        historySearchAdapter.setTextViewClickListener(textViewClickListener);
    }

    /**
     * 更新布局
     */
    private void updateUI(){
        //显示搜索结果的根布局
        root_result_ly.setVisibility(View.VISIBLE);

        if(isFind){
            //如果找到
            //显示搜索结果布局
            search_result_rv.setVisibility(View.VISIBLE);
            //隐藏未找到布局
            not_find_root_ll.setVisibility(View.GONE);

            //加载搜索到结果的数据布局
            loadSearchResultView();

        }else{
            //如果没找到
            //隐藏搜索结果布局
            search_result_rv.setVisibility(View.GONE);
            //显示未找到布局
            not_find_root_ll.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 请求数据
     */
    private void requestData(String searchInfo) {
        //请求参数
        Map<String, String> params = new HashMap<>();
        switch (chosenID){
            case 1://小说
                params.put("xsname", searchInfo);
                break;
            case 2://漫画
                params.put("mhname", searchInfo);
                break;
            case 3://影视
                params.put("ysname", searchInfo);
                break;
            default:
                break;
        }

        Call call = HttpRequestUtils.getSearchCall(urls[chosenID-1], params);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //Toast.makeText(context, "数据请求失败："+ e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                //Toast.makeText(context, response.body().string(), Toast.LENGTH_SHORT).show();
                //Log.d("ZCC", "onResponse: " + response.body().string());
                final int status = HttpRequestUtils.parseNovelJson(response.body().string(), searchResultArrayList);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        handlerNovelResult(status);
                    }
                });
            }
        });
    }

    /**
     * 处理解析后的小说搜索数据
     */
    private void handlerNovelResult(int status) {
        if(status == 1){
            Toast.makeText(context, context.getString(R.string.not_find), Toast.LENGTH_SHORT).show();
            isFind = false;
        }else{
            isFind = true;
        }
        updateUI();

    }

    /**
     * 初始化视图
     */
    private void initView() {
        cancel_tv = findViewById(R.id.cancel_tv);
        top_search_tv = findViewById(R.id.top_search_tv);
        search_v = findViewById(R.id.search_v);
        top_rcv = findViewById(R.id.top_rcv);
        clear_iv = findViewById(R.id.clear_iv);
        history_rcv = findViewById(R.id.history_rcv);
        history_rl = findViewById(R.id.history_rl);

        //搜索中的布局
        root_ly = findViewById(R.id.root_ly);

        //搜索结果的布局
        root_result_ly = findViewById(R.id.root_result_ly);

        //未搜索到结果的布局
        not_find_root_ll = findViewById(R.id.not_find_root_ll);

        //搜索到结果的recyclerview
        search_result_rv = findViewById(R.id.search_result_rv);

        if(historyArrayList.size() == 0){
            history_rl.setVisibility(View.GONE);
        }else{
            history_rl.setVisibility(View.VISIBLE);
        }

        initHintText();
    }

    /**
     * 加载假数据
     */
    private void initTopData(){
        topArrayList = new ArrayList<>();

        for(int i = 0; i < 5; i++){
            topArrayList.add(new Top((i+1)+"", "斗罗大陆"+i));
        }

    }

    /**
     * 初始化search_et的hint
     */
    private void initHintText(){
        search_v.setIconifiedByDefault(false);
        search_v.setSubmitButtonEnabled(false);
        int id = search_v.getContext().getResources().getIdentifier("android:id/search_src_text",null,null);
        TextView textView = (TextView) search_v.findViewById(id);
        //字体、提示字体大小
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimension(R.dimen.input_text_size));
        switch (chosenID){
            case 1://小说
                search_v.setQueryHint(getString(R.string.search_novel_hint));
                break;
            case 2://漫画
                search_v.setQueryHint(getString(R.string.search_cartoon_hint));
                break;
            case 3://影视
                search_v.setQueryHint(getString(R.string.search_filmtv_hint));
                break;
        }
    }

    /**
     * 点击事件的处理
     * @param view
     */
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.cancel_tv){
            finish();
        }else if(view.getId() == R.id.clear_iv){
            //清除历史搜索数据
            historySearchAdapter.clearData();
            //同时清空数据库
            LitePal.deleteAll(History.class);
            history_rl.setVisibility(View.GONE);
        }
    }

    /**
     * 搜索的过滤器
     * @param tops
     * @param query
     * @return
     */
    private ArrayList<Top> filter(ArrayList<Top> tops, String query){
        final ArrayList<Top> filterTopList = new ArrayList<>();

        for(Top top: tops){
            final String name = top.getName();
            if(name.contains(query)){
                filterTopList.add(top);
            }
        }

        return filterTopList;
    }

    /**
     * 启动活动
     * @param context
     * @param chosen
     */
    public static void startActivity(Context context, int chosen){
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra("chosenID", chosen);
        context.startActivity(intent);
    }

    /**
     * 接受消息
     * @param history
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateHistory(History history){
        historySearchAdapter.addData(history);
        history_rl.setVisibility(View.VISIBLE);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消注册unregister
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }

        //保存数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(History history:historySearchAdapter.getHistories()){
                    history.save();
                }
            }
        }).start();
    }
}