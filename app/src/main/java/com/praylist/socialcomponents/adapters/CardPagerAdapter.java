package com.praylist.socialcomponents.adapters;

import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;

import com.praylist.socialcomponents.R;
import com.praylist.socialcomponents.model.CardItem;

import java.util.ArrayList;
import java.util.List;

public class CardPagerAdapter extends PagerAdapter implements CardAdapter {

    private List<CardView> mViews;
    private List<CardItem> mData;
    private float mBaseElevation;
    private MediaPlayer mediaPlayer0= new MediaPlayer();
    private MediaPlayer mediaPlayer1= new MediaPlayer();
    int cnt =0;

    public CardPagerAdapter() {
        mData = new ArrayList<>();
        mViews = new ArrayList<>();
    }

    public void addCardItem(CardItem item) {
        mViews.add(null);
        mData.add(item);
    }

    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mViews.get(position);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.view_pager_adapter, container, false);
        container.addView(view);
        bind(mData.get(position), view);
        CardView cardView = (CardView) view.findViewById(R.id.cardView);

        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }

        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
        mViews.set(position, cardView);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
    }

    private void bind(final CardItem item, View view) {
        TextView titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        TextView contentTextView = (TextView) view.findViewById(R.id.contentTextView);
        Button playMusicButton = (Button) view.findViewById(R.id.playMusicButton);
        mediaPlayer0 = MediaPlayer.create(view.getContext(),R.raw.firstpoet);
        mediaPlayer1 = MediaPlayer.create(view.getContext(),R.raw.secondpoet);
        // 각자 mediaplayer를 선언하면 리소스가 부족하여 제대로 스트리밍이 되지 않는다.

        titleTextView.setText(item.getTitle());
        contentTextView.setText(item.getText());

        playMusicButton.setOnClickListener(new View.OnClickListener() {
            // 재생 버튼이 눌리면 그 시에 맞는 음악을 재생해준다.
            @Override
            public void onClick(View v) {
                if(cnt==0){

                    if(mediaPlayer0.isPlaying()){
                        mediaPlayer0.stop();
                        mediaPlayer0.release();
                        mediaPlayer0 = MediaPlayer.create(view.getContext(),R.raw.secondpoet);
                        cnt++;
                    }else{
                        mediaPlayer0.start();

                    }
                }else if(cnt==1){

                    if(mediaPlayer0.isPlaying()){
                        mediaPlayer0.stop();
                        mediaPlayer0.release();
                        mediaPlayer0 = MediaPlayer.create(view.getContext(),R.raw.thirdpoet);
                        cnt++;
                    }else{
                        mediaPlayer0.start();
                    }
                }else if(cnt==2){

                    if(mediaPlayer0.isPlaying()){
                        mediaPlayer0.stop();
                        mediaPlayer0.release();
                        mediaPlayer0 = MediaPlayer.create(view.getContext(),R.raw.fourthpoet);
                        cnt++;
                    }else{
                        mediaPlayer0.start();
                    }
                }else if(cnt==3){

                    if(mediaPlayer0.isPlaying()){
                        mediaPlayer0.stop();
                        mediaPlayer0.release();
                        mediaPlayer0 = MediaPlayer.create(view.getContext(),R.raw.firstpoet);
                        cnt=0;
                    }else{

                        mediaPlayer0.start();
                    }
                }
            }
        });
    }

}