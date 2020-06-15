/*
 * Copyright 2018 Rozdoum
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.praylist.socialcomponents.main.post.addPost;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.praylist.socialcomponents.R;
import com.praylist.socialcomponents.main.post.BaseAddPostPresenter;
import com.praylist.socialcomponents.model.Post;

/**
 * Created by Alexey on 03.05.18.
 */

public class AddPostPresenter extends BaseAddPostPresenter<AddPostView> {

    public AddPostPresenter(Context context) {
        super(context);
    }



    @Override
    protected int getSaveFailMessage() {
        return R.string.error_fail_create_post;
    }

    @Override
    protected void savePost(String title, String description, boolean isGlobal, String prayerFor, String prayerForId) {
        ifViewAttached(view -> {
            view.showProgress(R.string.message_creating_post);
            Post post = new Post();
            post.setTitle(title);
            post.setDescription(description);
            post.setGlobal(isGlobal);
            post.setPrayerFor(prayerFor);
            post.setPrayerForId(prayerForId);
            post.setAuthorId(FirebaseAuth.getInstance().getCurrentUser().getUid()); // post의 주인을 세팅하는 부분인듯, 찌르기 구현할 때 써야될거 같으니 잘 봐야됨.
            post.setUsername(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            /* 찌르기에 답장이면
            post.setSecondAuthorId(FirebaseAuth.getInstance().getCurrentUser().getSecondUid());
             */
            postManager.createOrUpdatePostWithImage(view.getImageUri(), this, post, (int)post.getEmotionType());
        });
    }

    @Override
    protected boolean isImageRequired() {
        return true;
    }
}
