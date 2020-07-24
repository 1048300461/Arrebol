package com.example.arrebol.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.arrebol.R;

public class WatchingFilmActivity extends AppCompatActivity {

    private int chosenID;
    private String m3u8Url, downloadUrl, onlineUrl;

    private VideoView video_vv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watching_film);

        m3u8Url = getIntent().getStringExtra("m3u8");
        downloadUrl = getIntent().getStringExtra("download");
        onlineUrl = getIntent().getStringExtra("online");

        video_vv = findViewById(R.id.video_vv);

        Uri uri = Uri.parse(m3u8Url);
        video_vv.setMediaController(new MediaController(this));
        video_vv.setVideoURI(uri);
        video_vv.requestFocus();
        video_vv.start();

        Log.d("zcc", "onCreate: \n" + m3u8Url + "\n" + onlineUrl + "\n" + downloadUrl);
    }

    /**
     * 启动活动
     */
    public static void startActivity(Context context, String m3u8, String online, String download){
        Intent intent = new Intent(context, WatchingFilmActivity.class);
        intent.putExtra("m3u8", m3u8);
        intent.putExtra("online", online);
        intent.putExtra("download", download);
        context.startActivity(intent);
    }
}