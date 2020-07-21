package com.example.arrebol.widget.coolmenu;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.viewpager.widget.PagerAdapter;

import com.example.arrebol.R;
import com.example.arrebol.activity.SearchActivity;

import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public final class CoolMenuFrameLayout extends FrameLayout {

//    private static final String TAG = CoolMenuFrameLayout.class.getName();

    private Context mContext;

    private int num = 3;

    private int mTitleColor;

    private float mTitleSize;

    //菜单的drawable资源
    private Drawable mMenuIcon;

    //搜索的drawable资源
    private Drawable mSearchIcon;


    private int[] ids = {R.id.view0, R.id.view1, R.id.view2, R.id.view3, R.id.view4};

    private Interpolator mInterpolator = new AccelerateDecelerateInterpolator();

    private ObjectAnimator[] mOpenAnimators;

    private ObjectAnimator[] mChosenAnimators;

    private ObjectAnimator[] mMenuOpenAnimators;

    private ObjectAnimator[] mSearchOpenAnimators;

    private PagerAdapter mAdapter;

    private MenuObserver mObserver;

    private MenuChooser mMenuChooser = new MenuChooser();

    //菜单点击事件的实现
    private TranslateLayout.OnMenuClickListener menuListener = new MenuListener();

    //搜索点击事件的实现
    private TranslateLayout.OnSearchClickListener searchListener = new SearchListener();

    private boolean opening = false;

    private List<Object> objects = new ArrayList<>();

    private int chosen;

    public CoolMenuFrameLayout(Context context) {
        this(context, null);
    }

    public CoolMenuFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CoolMenuFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CoolMenuFrameLayout);
        num = array.getInteger(R.styleable.CoolMenuFrameLayout_num, 3);
        mTitleColor = array.getColor(R.styleable.CoolMenuFrameLayout_titleColor,
                getResources().getColor(android.R.color.primary_text_light));
        mTitleSize = array.getDimension(R.styleable.CoolMenuFrameLayout_titleSize,
                getResources().getDimension(R.dimen.cl_title_size));
        mMenuIcon = array.getDrawable(R.styleable.CoolMenuFrameLayout_titleMenuIcon);
        mSearchIcon = array.getDrawable(R.styleable.CoolMenuFrameLayout_titleSearchIcon);
        array.recycle();
        init();
    }


    private void init() {
        if (!isInEditMode()) {
            setWillNotDraw(true);
            chosen = num - 1;
            for (int i = 0; i < num; i++) {
                TranslateLayout frameLayout = new TranslateLayout(mContext);
                frameLayout.setId(ids[i]);
                frameLayout.setTag(i);
                //设置fragment的点击事件
                frameLayout.setOnClickListener(mMenuChooser);
                //设置菜单的点击事件
                frameLayout.setOnMenuClickListener(menuListener);
                //设置搜索的点击事件
                frameLayout.setOnSearchClickListener(searchListener);
                if(mMenuIcon != null) {
                    frameLayout.setMenuIcon(mMenuIcon);
                }
                if(mSearchIcon != null){
                    frameLayout.setSearchIcon(mSearchIcon);
                }
                //设置标题文字的大小
                frameLayout.setMenuTitleSize(mTitleSize);
                //设置标题文字颜色
                frameLayout.setMenuTitleColor(mTitleColor);

                if (i == num - 1) frameLayout.setMenuAlpha(1);
                LayoutParams layoutParams = new LayoutParams(MATCH_PARENT, MATCH_PARENT);
                frameLayout.setLayoutParams(layoutParams);

                if(i == 0 || i == 4){
                    ImageView imageView = frameLayout.findViewById(R.id.cl_search);
                    imageView.setVisibility(GONE);
                }
                addView(frameLayout);

            }
            mOpenAnimators = new ObjectAnimator[num];
            mChosenAnimators = new ObjectAnimator[num];
            mMenuOpenAnimators = new ObjectAnimator[num];
            mSearchOpenAnimators = new ObjectAnimator[num];
            initAnim();
        }
    }

    private void initAnim() {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            float transX = (float) ((i + 1) * 0.06);
            float transY = (float) ((i + 1) * 0.12);
            ObjectAnimator animator;

            PropertyValuesHolder valuesHolderX = PropertyValuesHolder.ofFloat("XFraction", 0, transX);
            PropertyValuesHolder valuesHolderY = PropertyValuesHolder.ofFloat("YFraction", 0, transY);
            animator = ObjectAnimator.ofPropertyValuesHolder(child, valuesHolderX, valuesHolderY);
            animator.setInterpolator(mInterpolator);
            animator.setDuration(300);
            mOpenAnimators[i] = animator;

            PropertyValuesHolder valuesHolderXReverse = PropertyValuesHolder.ofFloat("XFraction", transX, 1);
            animator = ObjectAnimator.ofPropertyValuesHolder(child, valuesHolderXReverse);
            animator.setInterpolator(mInterpolator);
            animator.setDuration(300);
            mChosenAnimators[i] = animator;

            PropertyValuesHolder valuesHolderAlpha = PropertyValuesHolder.ofFloat("menuAlpha", 1, 0);
            animator = ObjectAnimator.ofPropertyValuesHolder(child, valuesHolderAlpha);
            animator.setInterpolator(mInterpolator);
            animator.setDuration(300);
            mMenuOpenAnimators[i] = animator;

            PropertyValuesHolder valuesHolderAlpha2 = PropertyValuesHolder.ofFloat("searchAlpha", 1, 0);
            animator = ObjectAnimator.ofPropertyValuesHolder(child, valuesHolderAlpha2);
            animator.setInterpolator(mInterpolator);
            animator.setDuration(300);
            mSearchOpenAnimators[i] = animator;
        }
    }


    public void setAdapter(PagerAdapter adapter) {
        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mObserver);
            mAdapter.startUpdate(this);
            for (int i = 0; i < num; i++) {
                mAdapter.destroyItem((ViewGroup) getChildAt(i), i, objects.get(i));
            }
            mAdapter.finishUpdate(this);
        }

        this.mAdapter = adapter;
        if (mAdapter == null) {
            return;
        }

        if (mObserver == null) {
            mObserver = new MenuObserver();
        }
        mAdapter.registerDataSetObserver(mObserver);

        int count = mAdapter.getCount();
        if (count != num) {
            throw new RuntimeException("number of view should equal 'num' that declared in xml");
        }

        for (int i = 0; i < count; i++) {
            Object object = mAdapter.instantiateItem((ViewGroup) getChildAt(i), i);
            objects.add(object);
        }
        mAdapter.finishUpdate(this);
    }

    @UiThread
    public void setTitles(@NonNull List<String> titles) {
        for (int i = 0; i < num; i++) {
            ((TranslateLayout) getChildAt(i)).setTitle(titles.get(i));
        }
    }

    @UiThread
    public void setTitleByIndex(@NonNull String title, int index) {
        if (index > num -1) {
            throw new IndexOutOfBoundsException();
        }
        ((TranslateLayout) getChildAt(index)).setTitle(title);
    }

    @UiThread
    public void setMenuIcon(@NonNull int resId) {
        for (int i = 0; i < num; i++) {
            ((TranslateLayout) getChildAt(i)).setMenuIcon(resId);
        }
    }

    @UiThread
    public void setSearchIcon(@NonNull int resId) {
        for (int i = 0; i < num; i++) {
            ((TranslateLayout) getChildAt(i)).setSearchIcon(resId);
        }
    }

    public void toggle() {
        if (opening) {
            close();
        } else {
            open();
        }
    }

    private void open() {
        opening = true;
        for (int i = 0; i < num; i++) {
            if (i == chosen) {
                mMenuOpenAnimators[i].start();
                mOpenAnimators[i].start();
            } else if (i > chosen) {
                mChosenAnimators[i].reverse();
            } else {
                mOpenAnimators[i].start();
            }
        }
        chosen = num - 1;
    }

    private void close() {
        opening = false;
        for (ObjectAnimator mAnimator : mOpenAnimators) {
            mAnimator.reverse();
        }
    }

    private void dataSetChanged() {
        if (opening) {
            close();
        }
        if (mAdapter != null) {
            mAdapter.startUpdate(this);
            for (int i = 0; i < num; i++) {
                mAdapter.destroyItem((ViewGroup) getChildAt(i), i, objects.get(i));
            }
            for (int i = 0; i < num; i++) {
                mAdapter.instantiateItem((ViewGroup) getChildAt(i), i);
            }
            mAdapter.finishUpdate(this);
        }
    }

    boolean isOpening() {
        return opening;
    }

    private class MenuObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            dataSetChanged();
        }

        @Override
        public void onInvalidated() {
            dataSetChanged();
        }
    }

    private class MenuChooser implements OnClickListener {

        @Override
        public void onClick(View v) {
            if (opening) {
                chosen = (int) v.getTag();
                for (int i = 0; i < num; i++) {
                    if (i <= chosen) {
                        mOpenAnimators[i].reverse();
                    } else {
                        mChosenAnimators[i].start();
                    }
                }
                mMenuOpenAnimators[chosen].reverse();
            }
            opening = false;
        }
    }

    private class MenuListener implements TranslateLayout.OnMenuClickListener {

        @Override
        public void onMenuClick() {
            toggle();
        }
    }

    private class SearchListener implements TranslateLayout.OnSearchClickListener {
        @Override
        public void onSearchClick() {
            SearchActivity.startActivity(mContext, chosen);
        }
    }
}
