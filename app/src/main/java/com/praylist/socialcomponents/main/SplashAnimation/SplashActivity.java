package com.praylist.socialcomponents.main.SplashAnimation;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.praylist.socialcomponents.main.main.MainActivity;
import com.praylist.socialcomponents.main.onBoarding.onBoardingActivity;
import com.praylist.socialcomponents.main.todayViewPager.viewPagerActivity;

public class SplashActivity extends AppCompatActivity {

    Handler handler = new Handler();
    String myid;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            myid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } catch (Exception e){
            myid=null;
        }



        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(myid!=null){
                    Intent intent = new Intent(getApplicationContext(), viewPagerActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent = new Intent(getApplicationContext(), viewPagerActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        },1500);
    }
}
