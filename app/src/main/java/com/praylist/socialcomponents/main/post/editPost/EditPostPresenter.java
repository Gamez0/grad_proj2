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

package com.praylist.socialcomponents.main.post.editPost;

import android.content.Context;
import android.util.Log;

import com.praylist.socialcomponents.R;
import com.praylist.socialcomponents.main.post.BaseCreatePostPresenter;
import com.praylist.socialcomponents.managers.PostManager;
import com.praylist.socialcomponents.managers.listeners.OnPostChangedListener;
import com.praylist.socialcomponents.model.Post;

/**
 * Created by Alexey on 03.05.18.
 */

class EditPostPresenter extends BaseCreatePostPresenter<EditPostView> {

    private Post post;

    EditPostPresenter(Context context) {
        super(context);
    }

    public void setPost(Post post) {
        this.post = post;
    }

    @Override
    protected int getSaveFailMessage() {
        return R.string.error_fail_update_post;
    }

    @Override
    protected boolean isImageRequired() {
        return false;
    }

    private void updatePostIfChanged(Post updatedPost) {
        if (post.getLikesCount() != updatedPost.getLikesCount()) {
            post.setLikesCount(updatedPost.getLikesCount());
        }

        if (post.getCommentsCount() != updatedPost.getCommentsCount()) {
            post.setCommentsCount(updatedPost.getCommentsCount());
        }

        if (post.getWatchersCount() != updatedPost.getWatchersCount()) {
            post.setWatchersCount(updatedPost.getWatchersCount());
        }

        if (post.isHasComplain() != updatedPost.isHasComplain()) {
            post.setHasComplain(updatedPost.isHasComplain());
        }
    }

    @Override
    protected void savePost(final String title, final String description, final boolean isGlobal, final String prayerFor) {
        ifViewAttached(view -> {
            view.showProgress(R.string.message_saving);

            post.setTitle(title);
            post.setDescription(description);
            post.setGlobal(isGlobal);
            post.setPrayerFor(prayerFor);

            if (view.getImageUri() != null) {
                postManager.createOrUpdatePostWithImage(view.getImageUri(), this, post);
            } else {
                postManager.createOrUpdatePost(post);
                onPostSaved(true);
            }
        });
    }

    public void addCheckIsPostChangedListener(int emotionType) {
        PostManager.getInstance(context.getApplicationContext()).getPost(context, post.getId(), emotionType, new OnPostChangedListener() {
            @Override
            public void onObjectChanged(Post obj) {
                if (obj == null) {
                    ifViewAttached(view -> view.showWarningDialog(R.string.error_post_was_removed, (dialog, which) -> {
                        Log.d(TAG, "어디가 문제야? 4");
                        view.openMainActivity();
                        view.finish();
                    }));
                } else {
                    updatePostIfChanged(obj);
                }
            }

            @Override
            public void onError(String errorText) {
                ifViewAttached(view -> view.showWarningDialog(errorText, (dialog, which) -> {
                    view.openMainActivity();
                    view.finish();
                }));
            }
        });
    }

    public void closeListeners() {
        postManager.closeListeners(context);
    }
}
