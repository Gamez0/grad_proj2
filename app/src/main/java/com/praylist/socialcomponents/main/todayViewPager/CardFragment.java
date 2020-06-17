package com.praylist.socialcomponents.main.todayViewPager;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.praylist.socialcomponents.R;
import com.praylist.socialcomponents.adapters.CardAdapter;

public class CardFragment extends Fragment {

    private CardView mCardView;
    ValueAnimator mColorAnimation;
    Integer color1, color2, color3, color4;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_pager_fragment_adapter, container, false);
        mCardView = (CardView) view.findViewById(R.id.cardView);
        mCardView.setMaxCardElevation(mCardView.getCardElevation()
                * CardAdapter.MAX_ELEVATION_FACTOR);

        color1 = getResources().getColor(R.color.page1);
        color2 = getResources().getColor(R.color.page2);
        color3 = getResources().getColor(R.color.page3);
        color4 = getResources().getColor(R.color.page4);
        mColorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), color1, color2, color3,color4);
        mColorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCardView.setCardBackgroundColor((Integer)animation.getAnimatedValue());
            }
        });
        mColorAnimation.setDuration((4 - 1) * 10000000000l);

//        mCardView.setCardBackgroundColor();
        return view;
    }

    public CardView getCardView() {
        return mCardView;
    }
}