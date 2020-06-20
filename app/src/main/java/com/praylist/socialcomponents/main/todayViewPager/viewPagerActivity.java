package com.praylist.socialcomponents.main.todayViewPager;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.praylist.socialcomponents.R;
import com.praylist.socialcomponents.adapters.CardFragmentPagerAdapter;
import com.praylist.socialcomponents.adapters.CardPagerAdapter;
import com.praylist.socialcomponents.main.grid.GridActivity;
import com.praylist.socialcomponents.model.CardItem;

public class viewPagerActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private Button mButton;
    private ViewPager mViewPager;
    private TabLayout tabLayout;

    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;
    private CardFragmentPagerAdapter mFragmentCardAdapter;
    private ShadowTransformer mFragmentCardShadowTransformer;

    private boolean mShowingFragments = false;

    private ValueAnimator mColorAnimation;

    private FirebaseAnalytics mFirebaseAnalytics;

    Integer[] colors = null;
    Integer color1, color2, color3, color4;
    ArgbEvaluator argbEvaluator = new ArgbEvaluator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);

        // in app testing
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle params = new Bundle();
        params.putString("image_name", "테스트 메시지");
        params.putString("full_text", "in app messaging test 중입니다.");
        mFirebaseAnalytics.logEvent("makeNewMusic", params);


        color1 = getResources().getColor(R.color.page1);
        color2 = getResources().getColor(R.color.page2);
        color3 = getResources().getColor(R.color.page3);
        color4 = getResources().getColor(R.color.page4);
        Integer[] colors_temp = {color1,color2,color3,color4};
        colors = colors_temp;

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs) ;

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                if(pos ==0){
                    //fragment를 호출하는게 좋음
                    // 현재이므로 stay
                }else{
                    //fragment를 호출하는게 좋음
                    Intent intent = new Intent(getApplicationContext(), GridActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mCardAdapter = new CardPagerAdapter();
        mCardAdapter.addCardItem(new CardItem(R.string.title_1, R.string.text_1,0,R.raw.horror));
        mCardAdapter.addCardItem(new CardItem(R.string.title_2, R.string.text_2,1,R.raw.lionking));
        mCardAdapter.addCardItem(new CardItem(R.string.title_3, R.string.text_3,2,R.raw.lionking));
        mCardAdapter.addCardItem(new CardItem(R.string.title_4, R.string.text_4,3,R.raw.lionking));
        mFragmentCardAdapter = new CardFragmentPagerAdapter(getSupportFragmentManager(),
                dpToPixels(2, this));

        mCardShadowTransformer = new ShadowTransformer(mViewPager, mCardAdapter);
        mFragmentCardShadowTransformer = new ShadowTransformer(mViewPager, mFragmentCardAdapter);

        mViewPager.setAdapter(mCardAdapter);
        mViewPager.setPageTransformer(false, mCardShadowTransformer);
        mViewPager.setOffscreenPageLimit(3);

        mColorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), color1,color2,color3,color4);

        mColorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mViewPager.setBackgroundColor((Integer)animation.getAnimatedValue());

            }
        });
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if(position < (mCardAdapter.getCount() -1) && position < (colors.length - 1)) {

            mViewPager.setBackgroundColor((Integer) argbEvaluator.evaluate(positionOffset, colors[position], colors[position + 1]));

        } else {

            // the last page color
            mViewPager.setBackgroundColor(colors[colors.length - 1]);

        }
    }

//    @Override
//    public void onClick(View view) {
//        if (!mShowingFragments) {
//            mButton.setText("Views");
//            mViewPager.setAdapter(mFragmentCardAdapter);
//            mViewPager.setPageTransformer(false, mFragmentCardShadowTransformer);
//        } else {
//            mButton.setText("Fragments");
//            mViewPager.setAdapter(mCardAdapter);
//            mViewPager.setPageTransformer(false, mCardShadowTransformer);
//        }
//
//        mShowingFragments = !mShowingFragments;
//    }

    public static float dpToPixels(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().density);
    }

//    @Override
//    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//        mCardShadowTransformer.enableScaling(b);
//        mFragmentCardShadowTransformer.enableScaling(b);
//    }
    @Override
    protected void onPause(){
        super.onPause();
        overridePendingTransition(0,0);

    }
}