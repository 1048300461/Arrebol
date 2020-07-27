package com.example.arrebol.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.arrebol.R;
import com.example.arrebol.entity.Chapter;

public class ReadingNovelActivity extends AppCompatActivity {

    private int chosenID;
    private String url;
    private Chapter chapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_novel);

        chosenID = getIntent().getIntExtra("chosenID", 1);
        chapter = (Chapter) getIntent().getSerializableExtra("chapter");
        url = chapter.getUrl();

        Log.d("zcc", "onCreate: " + chosenID + " "+ url);
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