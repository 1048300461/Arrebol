package com.example.arrebol.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arrebol.R;
import com.example.arrebol.adapter.TopAdapter;
import com.example.arrebol.entity.Top;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * 搜索页面
 */
public class SearchActivity extends AppCompatActivity implements View.OnClickListener {


    private TextView cancel_tv;

    private SearchView search_v;

    private ArrayList<Top> topArrayList;

    //热门搜索的recyclerview
    private RecyclerView top_rcv;

    //热门搜索的adapter
    private TopAdapter topAdapter;

    //当前Fragment的ID
    int chosenID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        chosenID = getIntent().getIntExtra("chosenID", 0);

        initView();

        initTopData();

        top_rcv.setLayoutManager(new LinearLayoutManager(this));
        topAdapter = new TopAdapter(topArrayList, this);
        top_rcv.setAdapter(topAdapter);

        initListener();
    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        cancel_tv.setOnClickListener(this);

        search_v.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
//                Toast.makeText(getApplicationContext(), "搜索内容为：" + s, Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
//                Toast.makeText(getApplicationContext(), "输入为：" + s.length(), Toast.LENGTH_SHORT).show();
                if(s.length() == 0){
                    topAdapter.closeFilter(topArrayList);
                }else{
                    topAdapter.setFilter(filter(topArrayList, s));
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
        search_v = findViewById(R.id.search_v);
        top_rcv = findViewById(R.id.top_rcv);

        initHintText();
    }

    private void initTopData(){
        topArrayList = new ArrayList<>();

        for(int i = 0; i < 5; i++){
            topArrayList.add(new Top((i+1)+"", "斗罗大陆"+i));
        }
        //topArrayList.add(new Top((6)+"", "斗罗大陆"+0));
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
                getResources().getDimension(R.dimen.input_size));
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


}