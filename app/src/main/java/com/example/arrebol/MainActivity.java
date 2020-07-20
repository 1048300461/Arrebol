package com.example.arrebol;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import android.os.Bundle;

import com.example.arrebol.fragment.CartoonFragment;
import com.example.arrebol.fragment.FavoriteFragment;
import com.example.arrebol.fragment.FilmTvFragment;
import com.example.arrebol.fragment.NovelFragment;
import com.example.arrebol.fragment.SettingFragment;
import com.example.arrebol.widget.coolmenu.CoolMenuFrameLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Fragment> fragments = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CoolMenuFrameLayout coolMenuFrameLayout = findViewById(R.id.rl_main);
        String[] titles = {getString(R.string.setting),getString(R.string.novel), getString(R.string.cartoon), getString(R.string.filmtv), getString(R.string.favorite)};
        List<String> titleList = Arrays.asList(titles);
        coolMenuFrameLayout.setTitles(titleList);
        coolMenuFrameLayout.setMenuIcon(R.drawable.menu2);
        coolMenuFrameLayout.setSearchIcon(R.drawable.ic_baseline_search_24);

        fragments.add(new SettingFragment());
        fragments.add(new NovelFragment());
        fragments.add(new CartoonFragment());
        fragments.add(new FilmTvFragment());
        fragments.add(new FavoriteFragment());


        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        };

        coolMenuFrameLayout.setAdapter(adapter);
    }
}