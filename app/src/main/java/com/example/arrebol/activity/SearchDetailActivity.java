package com.example.arrebol.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.arrebol.R;
import com.example.arrebol.entity.History;

import org.greenrobot.eventbus.EventBus;

public class SearchDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_detail);

        String search_name = getIntent().getStringExtra("search_name");
        EventBus.getDefault().post(new History(search_name));
    }

    public static void startActivity(Context context, String search_name){
        Intent intent = new Intent(context, SearchDetailActivity.class);
        intent.putExtra("search_name", search_name);
        context.startActivity(intent);
    }
}