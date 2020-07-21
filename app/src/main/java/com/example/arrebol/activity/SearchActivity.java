package com.example.arrebol.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.arrebol.R;
import com.example.arrebol.adapter.HistorySearchAdapter;
import com.example.arrebol.adapter.TopSearchAdapter;
import com.example.arrebol.entity.History;
import com.example.arrebol.entity.Top;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * 搜索页面
 */
public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    private Context context;

    private TextView cancel_tv, top_search_tv;

    private SearchView search_v;

    private ArrayList<Top> topArrayList;

    private ArrayList<History> historyArrayList;

    private RelativeLayout history_rl;

    //热门搜索的recyclerview
    private RecyclerView top_rcv;

    //热门搜索的adapter
    private TopSearchAdapter topSearchAdapter;

    //历史搜索的recyclerview
    private RecyclerView history_rcv;

    //历史搜索的adapter
    private HistorySearchAdapter historySearchAdapter;

    //历史搜索的清除历史搜索
    ImageView clear_iv;

    //当前Fragment的ID
    int chosenID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        chosenID = getIntent().getIntExtra("chosenID", 0);

        context = this;

        //初始化视图
        initView();

        //fake data
        initTopData();

        top_rcv.setLayoutManager(new LinearLayoutManager(this));
        topSearchAdapter = new TopSearchAdapter(topArrayList, this);
        top_rcv.setAdapter(topSearchAdapter);

        history_rcv.setLayoutManager(new GridLayoutManager(this, 2));
        historySearchAdapter = new HistorySearchAdapter(historyArrayList, this);
        history_rcv.setAdapter(historySearchAdapter);

        initListener();

        EventBus.getDefault().register(this);
    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        cancel_tv.setOnClickListener(this);
        clear_iv.setOnClickListener(this);

        search_v.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                SearchDetailActivity.startActivity(context,s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(s.length() == 0){
                    topSearchAdapter.closeFilter(topArrayList);
                    history_rl.setVisibility(View.VISIBLE);
                    top_search_tv.setVisibility(View.VISIBLE);
                }else{
                    topSearchAdapter.setFilter(filter(topArrayList, s));
                    history_rl.setVisibility(View.GONE);
                    top_search_tv.setVisibility(View.GONE);
                }
                return false;
            }
        });
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

        initHintText();
    }

    /**
     * 加载假数据
     */
    private void initTopData(){
        topArrayList = new ArrayList<>();
        historyArrayList = new ArrayList<>();

        for(int i = 0; i < 5; i++){
            topArrayList.add(new Top((i+1)+"", "斗罗大陆"+i));
        }
        //topArrayList.add(new Top((6)+"", "斗罗大陆"+0));
        for(int i = 0; i < 8; i++){
            History history = new History();
            history.setName("武动乾坤" + i);
            historyArrayList.add(history);
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

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.cancel_tv){
            finish();
        }else if(view.getId() == R.id.clear_iv){
            //清除历史搜索数据
            historySearchAdapter.clearData();
            history_rl.setVisibility(View.GONE);
        }
    }

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

    public static void startActivity(Context context, int chosen){
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra("chosenID", chosen);
        context.startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateHistory(History history){
        historySearchAdapter.addData(history);
        Log.d("zcc", "updateHistory: " + history.getName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }

    }
}