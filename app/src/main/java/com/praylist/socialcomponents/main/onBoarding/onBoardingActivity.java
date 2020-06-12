package com.praylist.socialcomponents.main.onBoarding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.praylist.socialcomponents.R;
import com.praylist.socialcomponents.adapters.SliderAdapter;
import com.praylist.socialcomponents.main.main.MainActivity;
import com.praylist.socialcomponents.main.todayViewPager.viewPagerActivity;

public class onBoardingActivity extends AppCompatActivity {
    Handler handler = new Handler();

    private ViewPager mSlideViewPager;
    private LinearLayout mDotLayout;

    private TextView[] mDots;

    private SliderAdapter sliderAdapter;

    private TextView titleView;
    private Button mNextBtn;
    private Button mBackBtn;

    private int mCurrentPage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

        mSlideViewPager = (ViewPager) findViewById(R.id.slideViewPager);
        mDotLayout = (LinearLayout) findViewById(R.id.dotsLayout);
        mNextBtn = (Button) findViewById(R.id.nextBtn);
        mBackBtn = (Button) findViewById(R.id.prevBtn);
        titleView = (TextView) findViewById(R.id.titleTextView);
        sliderAdapter = new SliderAdapter(this);

        mSlideViewPager.setAdapter(sliderAdapter);

        addDotsIndicator(0);

        mSlideViewPager.addOnPageChangeListener(viewListener);

        //onClickListener

        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlideViewPager.setCurrentItem(mCurrentPage+1);
                if(mCurrentPage==mDots.length-1){
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(getApplicationContext(), viewPagerActivity.class);// 튜토리얼 끝나고 실행하는 곳
                            startActivity(intent);

                            finish();
                        }
                    },1);
                }
            }
        });
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlideViewPager.setCurrentItem(mCurrentPage-1);
            }
        });
    }

    public void addDotsIndicator(int position){

        mDots = new TextView[5];
        mDotLayout.removeAllViews();
        for(int i = 0; i< mDots.length; i++){

            mDots[i]= new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.dart_transparent));

            mDotLayout.addView(mDots[i]);
        }

        if(mDots.length>0){
            mDots[position].setTextColor(getResources().getColor(R.color.white));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener(){
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
            mCurrentPage = position;

            if(position==0){
                mNextBtn.setEnabled(true);
                mBackBtn.setEnabled(false);
                mBackBtn.setVisibility(View.INVISIBLE);
                titleView.setVisibility(View.VISIBLE);
                mNextBtn.setText("다음");
                mBackBtn.setText("");
            }else if(position==mDots.length-1){
                mNextBtn.setEnabled(true);
                mBackBtn.setEnabled(true);
                mBackBtn.setVisibility(View.VISIBLE);
                titleView.setVisibility(View.INVISIBLE);
                mNextBtn.setText("시작하기");
                mBackBtn.setText("이전");
            }else if(position==mDots.length){
                // do nothing
            }
            else{
                mNextBtn.setEnabled(true);
                mBackBtn.setEnabled(true);
                mBackBtn.setVisibility(View.VISIBLE);
                titleView.setVisibility(View.INVISIBLE);
                mNextBtn.setText("다음");
                mBackBtn.setText("이전");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
