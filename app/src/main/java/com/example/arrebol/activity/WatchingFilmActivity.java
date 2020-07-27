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
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class WatchingFilmActivity extends AppCompatActivity {

    private Context context;

    private String m3u8Url, downloadUrl, onlineUrl;

    private PlayerView explayer_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watching_film);

        context = this;

        m3u8Url = getIntent().getStringExtra("m3u8");
        downloadUrl = getIntent().getStringExtra("download");
        onlineUrl = getIntent().getStringExtra("online");

        explayer_view = findViewById(R.id.explayer_view);

        initPlayer(onlineUrl);
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

    private void initPlayer(String url){
        SimpleExoPlayer player = new SimpleExoPlayer.Builder(context).build();
        explayer_view.setPlayer(player);
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, context.getString(R.string.app_name)));
        MediaSource videoSource = new ProgressiveMediaSource
                .Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(url));
        player.prepare(videoSource);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}