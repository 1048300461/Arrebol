package com.example.arrebol.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.arrebol.R;
import com.example.arrebol.entity.Section;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class WatchingFilmActivity extends AppCompatActivity {

    private Context context;

    private String m3u8Url, downloadUrl, onlineUrl;

    private PlayerView explayer_view;

    private ImageButton full_btn;

    private Section section;

    private SimpleExoPlayer player;

    private boolean isFullScreen = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watching_film);

        context = this;

        section = (Section) getIntent().getSerializableExtra("section");
        m3u8Url = section.getM3u8Url();
        downloadUrl = section.getDownload();
        onlineUrl = section.getOnlineUrl();

        explayer_view = findViewById(R.id.explayer_view);
        full_btn = findViewById(R.id.exo_fullscreen_button);
        full_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int flagFullScreen = WindowManager.LayoutParams.FLAG_FULLSCREEN;
                if(isFullScreen){
                    getWindow().addFlags(flagFullScreen);//设置全屏
                    //若上面无效，换成下面的
                    if(getSupportActionBar() != null) getSupportActionBar().hide();
                    isFullScreen = false;
                }else{
                    //退出全屏
                    WindowManager.LayoutParams attrs = getWindow().getAttributes();
                    attrs.flags &= (~flagFullScreen);
                    getWindow().setAttributes(attrs);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                    if(getSupportActionBar() != null) getSupportActionBar().show();
                    isFullScreen = true;
                }
            }
        });

        initPlayer(onlineUrl);
        Log.d("zcc", "onCreate: \n" + m3u8Url + "\n" + onlineUrl + "\n" + downloadUrl);
    }

    /**
     * 启动活动
     */
    public static void startActivity(Context context, Section section){
        Intent intent = new Intent(context, WatchingFilmActivity.class);
//        intent.putExtra("m3u8", m3u8);
//        intent.putExtra("online", online);
//        intent.putExtra("download", download);
        intent.putExtra("section", section);
        context.startActivity(intent);
    }

    private void initPlayer(String url){
        //player = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector(), new DefaultLoadControl());
        player = new SimpleExoPlayer.Builder(context).build();
        player.setPlayWhenReady(true);
        explayer_view.setPlayer(player);

        Uri uri = Uri.parse(section.getM3u8Url());
        DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("user-agent");
        //ProgressiveMediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
        HlsMediaSource mediaSource = new HlsMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri, null, null);
        // 播放
        player.prepare(mediaSource);


//        SimpleExoPlayer player = new SimpleExoPlayer.Builder(context).build();
//        explayer_view.setPlayer(player);
//        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
//                Util.getUserAgent(context, context.getString(R.string.app_name)));
//        MediaSource videoSource = new ProgressiveMediaSource
//                .Factory(dataSourceFactory)
//                .createMediaSource(Uri.parse(url));
//        player.prepare(videoSource);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        player.release();
    }
}