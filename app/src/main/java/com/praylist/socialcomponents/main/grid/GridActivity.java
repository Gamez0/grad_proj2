package com.praylist.socialcomponents.main.grid;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;
import com.praylist.socialcomponents.R;
import com.praylist.socialcomponents.adapters.EmoAdapter;
import com.praylist.socialcomponents.main.main.MainActivity;
import com.praylist.socialcomponents.main.todayViewPager.viewPagerActivity;
import com.praylist.socialcomponents.model.EmoItem;

import java.util.ArrayList;

public class GridActivity extends AppCompatActivity {
    GridView gridView;
    private TabLayout tabLayout;


    ArrayList<EmoItem> datas = new ArrayList<EmoItem>(){
        {add(new EmoItem("매일 그대와","Joy"));
            add(new EmoItem("울고 싶을 때","Sadness")); //슬픔,그리움,걱정,사랑
            add(new EmoItem("걱정 근심","Fear"));
            add(new EmoItem("해방, \n독립 운동의 꽃","Anger"));
            add(new EmoItem("성찰과 시","Admiration"));

        }
    };
    @Override
    protected void onPause(){
        super.onPause();
        overridePendingTransition(0,0);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid_view);
        final Intent intent_emotion = new Intent(getApplicationContext(), MainActivity.class);  // 리스트로 가고,
        tabLayout = (TabLayout) findViewById(R.id.tabs) ;
        TabLayout.Tab tab = tabLayout.getTabAt(1);
        tab.select();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();

                if(pos ==0){
                    //fragment를 호출하는게 좋음
                    Intent intent = new Intent(getApplicationContext(), viewPagerActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    finish();

                }else{
                    //fragment를 호출하는게 좋음
                    // 현재이므로 stay
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        EmoAdapter emoAdapter = new EmoAdapter(getApplicationContext(),datas);

        gridView =  (GridView) findViewById(R.id.grid_view);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    // 감정을 누르면 이동할 fragment 부르거나 intent
                    case 0:
//                        Toast.makeText(getApplicationContext(), "감정 : 기쁨", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
//                        Toast.makeText(getApplicationContext(), "감정 : 슬픔", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
//                        Toast.makeText(getApplicationContext(), "감정 : 두려움", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
//                        Toast.makeText(getApplicationContext(), "감정 : 저항의지", Toast.LENGTH_SHORT).show();
                        break;
                    case 4:
//                        Toast.makeText(getApplicationContext(), "감정 : 자연", Toast.LENGTH_SHORT).show();
                        break;
                }
                intent_emotion.putExtra("emotion",position);
                startActivity(intent_emotion);
            }
        });
        gridView.setAdapter(emoAdapter);
    }
}