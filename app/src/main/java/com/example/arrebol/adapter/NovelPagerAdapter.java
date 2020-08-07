package com.example.arrebol.adapter;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.arrebol.activity.TestActivity;
import com.example.arrebol.fragment.ContentFragment;
import com.example.arrebol.fragment.NovelFragment;

public class NovelPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {
    private final int PAGER_COUNT = 3;
    private Fragment previous = null;
    private Fragment current = null;
    private Fragment next = null;

    public NovelPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);

        previous = new ContentFragment(0);
        current = new ContentFragment(1);
        next = new ContentFragment(2);
    }


    @Override
    public int getCount() {
        return PAGER_COUNT;
    }



    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        Log.d("zcc", "getItem: " + position);
        switch (position){
            case TestActivity.PREVIOUS:
                fragment = previous;
                break;
            case TestActivity.CURRENT:
                fragment = current;
                break;
            case TestActivity.NEXT:
                fragment = next;
                break;
        }
        assert fragment != null;
        return fragment;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.destroyItem(container, position, object);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
