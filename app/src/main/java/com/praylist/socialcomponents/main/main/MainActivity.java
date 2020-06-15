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

package com.praylist.socialcomponents.main.main;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.praylist.socialcomponents.R;
import com.praylist.socialcomponents.adapters.PostsAdapter;
import com.praylist.socialcomponents.adapters.PostsByEmotionAdapter;
import com.praylist.socialcomponents.adapters.PostsByUserAdapter;
import com.praylist.socialcomponents.main.base.BaseActivity;
import com.praylist.socialcomponents.main.followPosts.FollowingPostsActivity;
import com.praylist.socialcomponents.main.post.createPost.CreatePostActivity;
import com.praylist.socialcomponents.main.postDetails.PostDetailsActivity;
import com.praylist.socialcomponents.main.profile.ProfileActivity;
import com.praylist.socialcomponents.main.search.SearchActivity;
import com.praylist.socialcomponents.model.Post;
import com.praylist.socialcomponents.utils.AnimationUtils;

public class MainActivity extends BaseActivity<MainView, MainPresenter> implements MainView {

    private PostsAdapter postsAdapter;
    private PostsByEmotionAdapter postsByEmotionAdapter;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;

    private TextView newPostsCounterTextView;
    private boolean counterAnimationInProgress = false;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeContainer;

    private int emotionType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        emotionType = getIntent().getExtras().getInt("emotion");

        Toast.makeText(getApplicationContext(),"onCreate() called. emotionType is "+emotionType,Toast.LENGTH_LONG).show();
        initContentView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.updateNewPostCounter();
//        emotionType = getIntent().getExtras().getInt("emotion");
//        Toast.makeText(getApplicationContext(),"resume() called. emotionType is "+emotionType,Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        Toast.makeText(getApplicationContext(),"onRestart() called.",Toast.LENGTH_LONG).show();
    }

    @NonNull
    @Override
    public MainPresenter createPresenter() {
        if (presenter == null) {
            return new MainPresenter(this);
        }
        return presenter;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ProfileActivity.CREATE_POST_FROM_PROFILE_REQUEST:
                    refreshPostList();
                    break;
                case CreatePostActivity.CREATE_NEW_POST_REQUEST:
                    presenter.onPostCreated();
                    break;

                case PostDetailsActivity.UPDATE_POST_REQUEST:
                    presenter.onPostUpdated(data);
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        attemptToExitIfRoot(floatingActionButton);
    }

    public void refreshPostList() {
        postsAdapter.loadFirstPage();
        if (postsAdapter.getItemCount() > 0) {
            recyclerView.scrollToPosition(0);
        }
    }

    @Override
    public void removePost() {
        postsAdapter.removeSelectedPost();
    }

    @Override
    public void updatePost() {
        postsAdapter.updateSelectedPost();
    }

    @Override
    public void showCounterView(int count) {
        AnimationUtils.showViewByScaleAndVisibility(newPostsCounterTextView);
        String counterFormat = getResources().getQuantityString(R.plurals.new_posts_counter_format, count, count);
        newPostsCounterTextView.setText(String.format(counterFormat, count));
    }

    private void initContentView() {
        if (recyclerView == null) {
            progressBar = findViewById(R.id.progressBar);
            swipeContainer = findViewById(R.id.swipeContainer);

            initFloatingActionButton();
            //initPostListRecyclerView();
            //if login loadmypostslist but not login? then loadpostlist
//            loadMyPostsList();// 꼼수지만 뜨게 할 수 있다!
//            try{
//                loadMyPostsList();
//                Log.d(TAG,"loadMyPostsList init");
//                Toast.makeText(getApplicationContext(),"loadMyPostsList()",Toast.LENGTH_LONG).show();
//            }catch (Exception e){
                //그냥 모든 포스트가 첫 화면이면
                initPostListRecyclerView();

            //Toast.makeText(getApplicationContext(),"initPostListRecyclerView()",Toast.LENGTH_LONG).show();
                Log.d(TAG,"initPostListRecyclerView init");
//            }
            initPostCounter();
        }
    }

    private void initFloatingActionButton() {
        floatingActionButton = findViewById(R.id.addNewPostFab);
        if (floatingActionButton != null) {
            floatingActionButton.setOnClickListener(v -> presenter.onCreatePostClickAction(floatingActionButton));
        }
    }

    private void initPostListRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view);
        postsAdapter = new PostsAdapter(this, swipeContainer, emotionType);
        postsAdapter.setCallback(new PostsAdapter.Callback() {
            @Override
            public void onItemClick(final Post post, final View view) {
                presenter.onPostClicked(post, view, emotionType);
                Log.d(TAG,"emotion 뭐로 찍혀? : "+post.getEmotionType());
                Log.d(TAG,"description 뭐로 찍혀? : "+post.getDescription());
            }

            @Override
            public void onListLoadingFinished() {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onAuthorClick(String authorId, View view) {
                openProfileActivity(authorId, view);
            }

            @Override
            public void onCanceled(String message) {
                progressBar.setVisibility(View.GONE);
                showToast(message);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setAdapter(postsAdapter);
        postsAdapter.loadFirstPage();
    }

    private void loadMyPostsList() {// 내 프레이리스트
        //presenter.onProfileMenuActionClicked();

//            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();// 사용자 userId 불러오는 법
            recyclerView = findViewById(R.id.recycler_view);
            postsByEmotionAdapter = new PostsByEmotionAdapter(this, emotionType);
            postsByEmotionAdapter.setCallBack(new PostsByUserAdapter.CallBack() {
                @Override
                public void onItemClick(Post post, View view) {
                    presenter.onPostClick(post, view);
                }

                @Override
                public void onPostsListChanged(int postsCount) {
                    presenter.onPostListChanged(postsCount);
                }

                @Override
                public void onPostLoadingCanceled() {
                    hideLoadingPostsProgress();
                }
            });

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
            recyclerView.setAdapter(postsByEmotionAdapter);
            postsByEmotionAdapter.loadPosts();


    }

    private void initPostCounter() {
        newPostsCounterTextView = findViewById(R.id.newPostsCounterTextView);
        newPostsCounterTextView.setOnClickListener(v -> refreshPostList());

        presenter.initPostCounter();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                hideCounterView();
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    @Override
    public void hideCounterView() {
        if (!counterAnimationInProgress && newPostsCounterTextView.getVisibility() == View.VISIBLE) {
            counterAnimationInProgress = true;
            AlphaAnimation alphaAnimation = AnimationUtils.hideViewByAlpha(newPostsCounterTextView);
            alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    counterAnimationInProgress = false;
                    newPostsCounterTextView.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            alphaAnimation.start();
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void openPostDetailsActivity(Post post, View v) {
        Intent intent = new Intent(MainActivity.this, PostDetailsActivity.class);
        intent.putExtra(PostDetailsActivity.POST_ID_EXTRA_KEY, post.getId());
        intent.putExtra("emotion",(long)emotionType);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            View imageView = v.findViewById(R.id.postImageView);
            View authorImageView = v.findViewById(R.id.authorImageView);

            ActivityOptions options = ActivityOptions.
                    makeSceneTransitionAnimation(MainActivity.this,
                            new android.util.Pair<>(imageView, getString(R.string.post_image_transition_name)),
                            new android.util.Pair<>(authorImageView, getString(R.string.post_author_image_transition_name))
                    );
            startActivityForResult(intent, PostDetailsActivity.UPDATE_POST_REQUEST, options.toBundle());
        } else {
            startActivityForResult(intent, PostDetailsActivity.UPDATE_POST_REQUEST);
        }
    }

    public void showFloatButtonRelatedSnackBar(int messageId) {
        showSnackBar(floatingActionButton, messageId);
    }

    @Override
    public void openCreatePostActivity() {
        Intent intent = new Intent(this, CreatePostActivity.class);
        startActivityForResult(intent, CreatePostActivity.CREATE_NEW_POST_REQUEST);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void openProfileActivity(String userId, View view) {
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        intent.putExtra(ProfileActivity.USER_ID_EXTRA_KEY, userId);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && view != null) {

            View authorImageView = view.findViewById(R.id.authorImageView);

            ActivityOptions options = ActivityOptions.
                    makeSceneTransitionAnimation(MainActivity.this,
                            new android.util.Pair<>(authorImageView, getString(R.string.post_author_image_transition_name)));
            startActivityForResult(intent, ProfileActivity.CREATE_POST_FROM_PROFILE_REQUEST, options.toBundle());
        } else {
            startActivityForResult(intent, ProfileActivity.CREATE_POST_FROM_PROFILE_REQUEST);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.profile:
                presenter.onProfileMenuActionClicked();
                return true;

            case R.id.followingPosts:
                Intent followingPosts = new Intent(this, FollowingPostsActivity.class);
                startActivity(followingPosts);
                return true;

            case R.id.search:
                Intent searchIntent = new Intent(this, SearchActivity.class);
                startActivity(searchIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void hideLoadingPostsProgress() {
        swipeContainer.setRefreshing(false);
        if (progressBar.getVisibility() != View.GONE) {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void showLikeCounter(boolean show) {// 여기 좋아요 수 구현안해도 될듯!
        //likesCountersTextView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void updatePostsCounter(Spannable text) {
        //postsCounterTextView.setText(text);
    }

    @Override
    public void showPostCounter(boolean show) {
        //postsCounterTextView.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
