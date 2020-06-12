package com.praylist.socialcomponents.adapters.holders;

import android.view.View;

import com.praylist.socialcomponents.main.base.BaseActivity;
import com.praylist.socialcomponents.managers.listeners.OnPostChangedListener;
import com.praylist.socialcomponents.model.FollowingPost;
import com.praylist.socialcomponents.model.Post;
import com.praylist.socialcomponents.utils.LogUtil;

/**
 * Created by Alexey on 22.05.18.
 */
public class FollowPostViewHolder extends PostViewHolder {


    public FollowPostViewHolder(View view, OnClickListener onClickListener, BaseActivity activity) {
        super(view, onClickListener, activity);
    }

    public FollowPostViewHolder(View view, OnClickListener onClickListener, BaseActivity activity, boolean isAuthorNeeded) {
        super(view, onClickListener, activity, isAuthorNeeded);
    }

    public void bindData(FollowingPost followingPost) { // 팔로잉 없잖아 TODO 팔로잉 만들고 싶으면
//        postManager.getSinglePostValue(followingPost.getPostId(), new OnPostChangedListener() {
//            @Override
//            public void onObjectChanged(Post obj) {
//                bindData(obj);
//            }
//
//            @Override
//            public void onError(String errorText) {
//                LogUtil.logError(TAG, "bindData", new RuntimeException(errorText));
//            }
//        });
    }

}
