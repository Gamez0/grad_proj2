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
    private MediaPlayer mediaPlayer = new MediaPlayer();
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
//        mediaPlayer = MediaPlayer.create(view.getContext(),R.raw.horror);
        mediaPlayer0 = MediaPlayer.create(view.getContext(),R.raw.horror);
        mediaPlayer1 = MediaPlayer.create(view.getContext(),R.raw.lionking);

        titleTextView.setText(item.getTitle());
        contentTextView.setText(item.getText());
//        switch(num){
//            case 0:
//                mediaPlayer = MediaPlayer.create(view.getContext(),R.raw.horror);
//                break;
//            case 1:
//                mediaPlayer = MediaPlayer.create(view.getContext(),R.raw.lionking);
//                break;
//            case 2:
//                break;
//        }

//        mediaPlayer.setNextMediaPlayer(mediaPlayer0);
        playMusicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 우선 가라로 구현한다.
                if(cnt==0){
                    if(mediaPlayer0.isPlaying()){
                        Toast.makeText(v.getContext(), "호러 중지", Toast.LENGTH_SHORT).show();
                        mediaPlayer0.pause();
                        mediaPlayer0.seekTo(0);
                        cnt=1;
                        mediaPlayer0.setNextMediaPlayer(mediaPlayer1);
//                    playButton1.setBackgroundResource(R.drawable.ic_play);
                    }else{
                        Toast.makeText(v.getContext(), "호러 재생", Toast.LENGTH_SHORT).show();
                        mediaPlayer0.start();
//                    playButton1.setBackgroundResource(R.drawable.ic_pause);
                    }
                }else if(cnt==1){

                    if(mediaPlayer0.isPlaying()){
                        Toast.makeText(v.getContext(), "라이온킹 중지", Toast.LENGTH_SHORT).show();
                        mediaPlayer0.pause();
                        mediaPlayer0.seekTo(0);
                        cnt=0;
//                        mediaPlayer0.setNextMediaPlayer(mediaPlayer);
//                    playsButton1.setBackgroundResource(R.drawable.ic_play);
                    }else{
                        Toast.makeText(v.getContext(), "라이온킹 재생", Toast.LENGTH_SHORT).show();
                        mediaPlayer0.seekTo(65000);
                        mediaPlayer0.start();
//                    playButton1.setBackgroundResource(R.drawable.ic_pause);
                    }
                }
            }
        });
    }

}