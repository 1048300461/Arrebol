package com.example.arrebol.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.arrebol.R;

public class ReadingNovelActivity extends AppCompatActivity {

    private int chosenID;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_novel);

        chosenID = getIntent().getIntExtra("chosenID", 1);
        url = getIntent().getStringExtra("url");

        Log.d("zcc", "onCreate: " + url);
    }

    /**
     * 启动活动
     */
    public static void startActivity(Context context, int chosenID, String detailUrl){
        Intent intent = new Intent(context, ReadingNovelActivity.class);
        intent.putExtra("chosenID", chosenID);
        intent.putExtra("url", detailUrl);
        context.startActivity(intent);
    }
}