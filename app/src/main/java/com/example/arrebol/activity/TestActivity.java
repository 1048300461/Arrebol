package com.example.arrebol.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.example.arrebol.R;
import com.example.arrebol.adapter.NovelPagerAdapter;
import com.example.arrebol.fragment.ContentFragment;
import com.example.arrebol.widget.Reader1.PageTurnView;
import com.example.arrebol.widget.Reader1.TwistView;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity {

    private TwistView twistView;
    private PageTurnView pageTurnView;
    private NovelPagerAdapter novelPagerAdapter;
    private ViewPager viewPager;

    public static final int PREVIOUS = 0;
    public static final int CURRENT = 1;
    public static final int NEXT = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        initView();

        novelPagerAdapter = new NovelPagerAdapter(getSupportFragmentManager(),FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(novelPagerAdapter);
        viewPager.setCurrentItem(0);
    }

    private void initView() {


        viewPager = findViewById(R.id.novel_content_vp);

//        Bitmap bitmap = null;
//        List<Bitmap> bitmaps = new ArrayList<>();
//
//        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.page_img_a);
//        bitmaps.add(bitmap);
//        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.page_img_b);
//        bitmaps.add(bitmap);
//        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.page_img_c);
//        bitmaps.add(bitmap);
////        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.page_img_d);
////        bitmaps.add(bitmap);
//        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.page_img_e);
//        bitmaps.add(bitmap);
//
//        pageTurnView.setBitmaps(bitmaps);
    }
}