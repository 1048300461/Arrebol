package com.example.arrebol.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.example.arrebol.R;
import com.example.arrebol.widget.Reader.TwistView;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity {

    private TwistView twistView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        initView();
    }

    private void initView() {
        twistView = findViewById(R.id.reading_page);

        Bitmap bitmap = null;
        List<Bitmap> bitmaps = new ArrayList<>();

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.page_img_a);
        bitmaps.add(bitmap);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.page_img_b);
        bitmaps.add(bitmap);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.page_img_c);
        bitmaps.add(bitmap);
//        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.page_img_d);
//        bitmaps.add(bitmap);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.page_img_e);
        bitmaps.add(bitmap);

        twistView.setBitmaps(bitmaps);
    }
}