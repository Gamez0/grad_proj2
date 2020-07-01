/*
 *  Copyright 2017 Rozdoum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.praylist.socialcomponents.main.postDetails;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Transition;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import static android.speech.tts.TextToSpeech.ERROR;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.praylist.socialcomponents.R;
import com.praylist.socialcomponents.adapters.CommentsAdapter;
import com.praylist.socialcomponents.controllers.LikeController;
import com.praylist.socialcomponents.dialogs.EditCommentDialog;
import com.praylist.socialcomponents.enums.PostStatus;
import com.praylist.socialcomponents.listeners.CustomTransitionListener;
import com.praylist.socialcomponents.main.base.BaseActivity;
import com.praylist.socialcomponents.main.imageDetail.ImageDetailActivity;
import com.praylist.socialcomponents.main.post.addPost.AddPostActivity;
import com.praylist.socialcomponents.main.post.editPost.EditPostActivity;
import com.praylist.socialcomponents.main.profile.ProfileActivity;
import com.praylist.socialcomponents.managers.DatabaseHelper;
import com.praylist.socialcomponents.managers.PostManager;
import com.praylist.socialcomponents.model.Comment;
import com.praylist.socialcomponents.model.Post;
import com.praylist.socialcomponents.utils.AnimationUtils;
import com.praylist.socialcomponents.utils.FormatterUtil;
import com.praylist.socialcomponents.utils.GlideApp;
import com.praylist.socialcomponents.utils.ImageUtil;
import com.praylist.socialcomponents.utils.LogUtil;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class PostDetailsActivity extends BaseActivity<PostDetailsView, PostDetailsPresenter> implements PostDetailsView, EditCommentDialog.CommentDialogCallback {

    public static final String POST_ID_EXTRA_KEY = "PostDetailsActivity.POST_ID_EXTRA_KEY";
    public static final String AUTHOR_ANIMATION_NEEDED_EXTRA_KEY = "PostDetailsActivity.AUTHOR_ANIMATION_NEEDED_EXTRA_KEY";
    public static final int UPDATE_POST_REQUEST = 1;
    public static final String POST_STATUS_EXTRA_KEY = "PostDetailsActivity.POST_STATUS_EXTRA_KEY";
    public static final List<String> EMOTION = Collections.unmodifiableList(Arrays.asList("funny", "sad", "scared","anger","nature"));

    private EditText commentEditText;
    @Nullable
    private ScrollView scrollView;
    private ViewGroup likesContainer;
    private ImageView likesImageView;
    private TextView commentsLabel;
    private TextView likeCounterTextView;
    private TextView commentsCountTextView;
    private TextView watcherCounterTextView;
    private TextView authorTextView;
    private TextView dateTextView;
    private ImageView authorImageView;
    private ProgressBar progressBar;
    private ImageView postImageView;
    private TextView titleTextView;
    private TextView descriptionEditText;
    private ProgressBar commentsProgressBar;
    private RecyclerView commentsRecyclerView;
    private TextView warningCommentsTextView;
    private TextView prayerForTextView;
    private ImageView addToMyPrayListView;
    private ImageView makeNewMusic;

    //tts
    ImageView btn_tts;
    private int flag=0;
    private TextToSpeech tts;

    public MediaPlayer player = new MediaPlayer();
    public MediaPlayer player2 = new MediaPlayer();
    Random rand = new Random();

    private MenuItem complainActionMenuItem;
    private MenuItem editActionMenuItem;
    private MenuItem deleteActionMenuItem;

    private String postId;
    private long emotionType;

    private PostManager postManager;
    private LikeController likeController;
    private boolean authorAnimationInProgress = false;

    private boolean isAuthorAnimationRequired;
    private CommentsAdapter commentsAdapter;
    private ActionMode mActionMode;
    private boolean isEnterTransitionFinished = false;
    private Button sendButton;

    private Handler mHandler;
    private HandlerThread mHandlerThread;

    public boolean newRequst;

    public void startHandlerThread(){
        mHandlerThread = new HandlerThread("HandlerThread");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //tts 버튼
        btn_tts = findViewById(R.id.item_tts);

        postManager = PostManager.getInstance(this);

        isAuthorAnimationRequired = getIntent().getBooleanExtra(AUTHOR_ANIMATION_NEEDED_EXTRA_KEY, false);
        postId = getIntent().getStringExtra(POST_ID_EXTRA_KEY);
        emotionType = getIntent().getExtras().getLong("emotion");

        initMidi(0); // 미디 초기화

        incrementWatchersCount();

        titleTextView = findViewById(R.id.authorTextView);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        postImageView = findViewById(R.id.postImageView);
        progressBar = findViewById(R.id.progressBar);
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);
        scrollView = findViewById(R.id.scrollView);
        commentsLabel = findViewById(R.id.commentsLabel);
        commentEditText = findViewById(R.id.commentEditText);
        likesContainer = findViewById(R.id.likesContainer);
        likesImageView = findViewById(R.id.likesImageView);
        authorImageView = findViewById(R.id.authorImageView);
        authorTextView = findViewById(R.id.authorTextView);
        likeCounterTextView = findViewById(R.id.likeCounterTextView);
        commentsCountTextView = findViewById(R.id.commentsCountTextView);
        watcherCounterTextView = findViewById(R.id.watcherCounterTextView);
        dateTextView = findViewById(R.id.dateTextView);
        commentsProgressBar = findViewById(R.id.commentsProgressBar);
        warningCommentsTextView = findViewById(R.id.warningCommentsTextView);
        sendButton = findViewById(R.id.sendButton);
        prayerForTextView = findViewById(R.id.prayerForTextView);
        addToMyPrayListView = findViewById(R.id.addToMyPrayList);
        makeNewMusic = findViewById(R.id.makeNewMusic);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && isAuthorAnimationRequired) {
            authorImageView.setScaleX(0);
            authorImageView.setScaleY(0);

            // Add a listener to get noticed when the transition ends to animate the fab button
            getWindow().getSharedElementEnterTransition().addListener(new CustomTransitionListener() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onTransitionEnd(Transition transition) {
                    super.onTransitionEnd(transition);
                    //disable execution for exit transition
                    if (!isEnterTransitionFinished) {
                        isEnterTransitionFinished = true;
                        com.praylist.socialcomponents.utils.AnimationUtils.showViewByScale(authorImageView)
                                .setListener(authorAnimatorListener)
                                .start();
                    }
                }
            });
        }
        //tts
        //text를 음성으로 나레이션
        tts=new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != ERROR){
                    //한국어로
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });
        //버튼누르면 재생.
        btn_tts.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public void onClick(View view){
                if(flag==0) {
                    flag=1;
                    //tts.setPitch(0.5f);//음성톤
                    tts.setSpeechRate(0.5f);//읽는 속도 0.5로 느리게.

                    tts.speak(descriptionEditText.getText().toString(), TextToSpeech.QUEUE_FLUSH, null, "");
                }else {
                    //만약 이미 재생되고 있는데 다시 버튼이 눌렸다면.
                    flag=0;
                    tts.stop();

                }
            }

        });
        initRecyclerView();
        initListeners();

        presenter.loadPost(postId, emotionType);
        supportPostponeEnterTransition();
    }

    @Override
    protected void onDestroy() {    // 종료시 리소스 정리
        super.onDestroy();
        postManager.closeListeners(this);
        if(tts!=null){
            //낭송 중지.
            tts.stop();
            tts.shutdown();
            tts=null;
        }
        if(player!=null){
            player.release();
            player=null;
        }
        if(player2!=null){
            player2.release();
            player2=null;
        }
    }

    @NonNull
    @Override
    public PostDetailsPresenter createPresenter() {
        if (presenter == null) {
            return new PostDetailsPresenter(this);
        }
        return presenter;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    hideKeyboard();
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onBackPressed() {   // 뒤로가기 눌렸을 때
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && isAuthorAnimationRequired
                && !authorAnimationInProgress
                && !AnimationUtils.isViewHiddenByScale(authorImageView)) {

            ViewPropertyAnimator hideAuthorAnimator = com.praylist.socialcomponents.utils.AnimationUtils.hideViewByScale(authorImageView);
            hideAuthorAnimator.setListener(authorAnimatorListener);
            hideAuthorAnimator.withEndAction(PostDetailsActivity.this::onBackPressed);
        } else {
            super.onBackPressed();
        }

        // 뒤로가기 했을 때 음악이 꺼져야지지
       player.seekTo(0);
        player.pause();
    }

    private void initListeners() {
        postImageView.setOnClickListener(v -> presenter.onPostImageClick());

        commentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                sendButton.setEnabled(charSequence.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        sendButton.setOnClickListener(v -> presenter.onSendButtonClick());

        commentsCountTextView.setOnClickListener(view -> scrollToFirstComment());

        authorImageView.setOnClickListener(v -> presenter.onAuthorClick(v));
        authorTextView.setOnClickListener(v -> presenter.onAuthorClick(v));
//        addToMyPrayListView.setOnClickListener(v->presenter.onAddButtonClick(addToMyPrayListView));
        addToMyPrayListView.setOnClickListener(v-> {    // 재생 버튼, 재생 버튼을 눌렸을 때 음악이 재생하도록
            try{
                if(newRequst){
                    if(player2.isPlaying()){
                        int num = rand.nextInt(5);
                        if(num!=0){
                            player2.seekTo(num*1000+5000);
                        }
                        else{
                            player2.seekTo(10000);
                        }

                        player2.pause();
                    }else{
                        player2.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        player2.start();

                        Log.d(TAG,"playing player2 from activity.");
                    }
                }else{
                    if(player.isPlaying()){
                        player.seekTo(0);
                        player.pause();
                    }else{
                        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
//                    player.setDataSource("https://firebasestorage.googleapis.com/v0/b/test-55ccb.appspot.com/o/midis%2Ffunny%2Foutput0.mid?alt=media&token=e6c69305-aac9-4f95-976f-b280c91c5e69");
//                    player.prepare();
                        int num = rand.nextInt(5);
                        if(num!=0){
                            player.seekTo(num*1000);
                        }else{
                            player.seekTo(6000);
                        }

                        player.start();
                        // 여기서 재생이 되었다고 알려주자

//                    getPlayerCounter();
//                    setPlayerCounter(); // cnt 증가
                        Log.d(TAG,"playing from activity.");
                    }
                }

            } catch (Exception e){

            }

        });

        makeNewMusic.setOnClickListener(v-> {   // 새로운 노래 작곡 요청
            // 새로운 곡이 요청되었음을 firebase에 알려주기 위해서 감정별로 있는 request count값을 증가시켜준다
            increasePlayedCounter();
            // when we get request for new music
//            reinitMidi(300);

            Toast toast = Toast.makeText(this, "세상에 하나 뿐인 노래 작곡 중\n예상 45-60sec", Toast.LENGTH_SHORT);
            TextView text1 = (TextView) toast.getView().findViewById(android.R.id.message);
            if( text1 != null) text1.setGravity(Gravity.CENTER);
            toast.show();
//            newRequst=true;


            // media player 새로 설정해주기ㅑ
//            initMidi(5);
            //after several sec
            httpRequest();  // 요청 http로 보내기
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    newRequst=true;
                    Toast.makeText(getApplicationContext(),titleTextView.getText()+" 작곡 완료! 재생 버튼을 눌러보세요",Toast.LENGTH_LONG).show();
                    // send request to server
                    // build music
                    // receive music
                    // set music to be played
                    postManager.getMidiStorageRef("newsong.mid", (int)emotionType).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            try{
                                player.stop();
                                player.release();
                                player.setDataSource(uri.toString());
//                    player = MediaPlayer.create(getApplicationContext(),)
                                Log.d(TAG,"initnewsong :" + uri);
                                player.prepare();
//                    if(retry!=0){
//                        player.seekTo(retry*500);
//                    }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }, 60000);

//            mHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(getApplicationContext(),"작곡 완료! 재생 버튼을 눌러보세요",Toast.LENGTH_LONG).show();
//                }
//            },40000);
        });

        likesContainer.setOnClickListener(v -> {
            if (likeController != null && presenter.isPostExist()) {
                likeController.handleLikeClickAction(this, presenter.getPost());
            }
        });

        //long click for changing animation
        likesContainer.setOnLongClickListener(v -> {
            if (likeController != null) {
                likeController.changeAnimationType();
                return true;
            }

            return false;
        });
    }
    private void initMidi(int retry){
        int num = rand.nextInt(45+retry); // 0부터 9까지 난수 생성 sad의 경우 곡이 10개 30 - 10
        if(emotionType==2){
            num = rand.nextInt(44+retry); // 10-3
        }
        postManager.getMidiStorageRef("output"+num+".mid", (int)emotionType).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try{
                    player.setDataSource(uri.toString());
//                    player = MediaPlayer.create(getApplicationContext(),)
                    Log.d(TAG,"initmidi :" + uri);
                    player.prepare();
//                    if(retry!=0){
//                        player.seekTo(retry*500);
//                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        postManager.getMidiStorageRef("output"+(num+1)+".mid", (int)emotionType).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try{
                    player2.setDataSource(uri.toString());
//                    player = MediaPlayer.create(getApplicationContext(),)
                    Log.d(TAG,"initmidi :" + uri);
                    player2.prepare();
//                    if(retry!=0){
//                        player.seekTo(retry*500);
//                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }

    private void initRecyclerView() {
        commentsAdapter = new CommentsAdapter();
        commentsAdapter.setCallback(new CommentsAdapter.Callback() {
            @Override
            public void onLongItemClick(View view, int position) {
                Comment selectedComment = commentsAdapter.getItemByPosition(position);
                startActionMode(selectedComment);
            }

            @Override
            public void onAuthorClick(String authorId, View view) {
                openProfileActivity(authorId, view);
            }
        });
        commentsRecyclerView.setAdapter(commentsAdapter);
        commentsRecyclerView.setNestedScrollingEnabled(false);
        commentsRecyclerView.addItemDecoration(new DividerItemDecoration(commentsRecyclerView.getContext(),
                ((LinearLayoutManager) commentsRecyclerView.getLayoutManager()).getOrientation()));

        presenter.getCommentsList(this, postId);
    }

    private void startActionMode(Comment selectedComment) {
        if (mActionMode != null) {
            return;
        }

        //check access to modify or remove post
        if (presenter.hasAccessToEditComment(selectedComment.getAuthorId()) || presenter.hasAccessToModifyPost()) {
            mActionMode = startSupportActionMode(new ActionModeCallback(selectedComment));
        }
    }

    private void incrementWatchersCount() {
        postManager.incrementWatchersCount(postId);
        Intent intent = getIntent();
        setResult(RESULT_OK, intent.putExtra(POST_STATUS_EXTRA_KEY, PostStatus.UPDATED));
    }

    @Override
    public void scrollToFirstComment() {
        scrollView.smoothScrollTo(0, commentsLabel.getTop());
    }

    @Override
    public void clearCommentField() {
        commentEditText.setText(null);
        commentEditText.clearFocus();
        hideKeyboard();
    }

    private void scheduleStartPostponedTransition(final ImageView imageView) {
        imageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                imageView.getViewTreeObserver().removeOnPreDrawListener(this);
                supportStartPostponedEnterTransition();
                return true;
            }
        });
    }

    @Override
    public void openImageDetailScreen(String imageTitle) {
        Intent intent = new Intent(this, ImageDetailActivity.class);
        intent.putExtra(ImageDetailActivity.IMAGE_TITLE_EXTRA_KEY, imageTitle);
        startActivity(intent);
    }

    @Override
    public void openProfileActivity(String userId, View view) {
        Intent intent = new Intent(PostDetailsActivity.this, ProfileActivity.class);
        intent.putExtra(ProfileActivity.USER_ID_EXTRA_KEY, userId);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && view != null) {

            ActivityOptions options = ActivityOptions.
                    makeSceneTransitionAnimation(PostDetailsActivity.this,
                            new android.util.Pair<>(view, getString(R.string.post_author_image_transition_name)));
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }

    @Override
    public void setTitle(String title) {
        titleTextView.setText(title);
    }

    @Override
    public void setPrayerFor(String prayerFor, String username) {
        prayerForTextView.setText(username);
    }

    @Override
    public void openAddPostActivity(String prayer, String username, String authorId) {
        // 여기를 누르면 음악이 재생되게 하자

    }

    @Override
    public void setDescription(String description) {
        descriptionEditText.setText(description);
    }

    @Override
    public void loadPostDetailImage(String imageTitle) {
        postManager.loadImageMediumSize(GlideApp.with(this), imageTitle, postImageView, () -> {
            scheduleStartPostponedTransition(postImageView);
            progressBar.setVisibility(View.GONE);
        });
    }

    @Override
    public void loadAuthorPhoto(String photoUrl) {
        ImageUtil.loadImage(GlideApp.with(PostDetailsActivity.this), photoUrl, authorImageView, DiskCacheStrategy.DATA);
    }

    @Override
    public void setAuthorName(String username) {
        authorTextView.setText(username);
    }

    @Override
    public void initLikeController(@NonNull Post post) {
        likeController = new LikeController(this, post, likeCounterTextView, likesImageView, false);
    }

    @Override
    public void updateCounters(@NonNull Post post) {
        long commentsCount = post.getCommentsCount();
        commentsCountTextView.setText(String.valueOf(commentsCount));
        commentsLabel.setText(String.format(getString(R.string.label_comments), commentsCount));
        likeCounterTextView.setText(String.valueOf(post.getLikesCount()));
        likeController.setUpdatingLikeCounter(false);

        watcherCounterTextView.setText(String.valueOf(post.getWatchersCount()));

        CharSequence date = FormatterUtil.getRelativeTimeSpanStringShort(this, post.getCreatedDate());
        dateTextView.setText(date);

        presenter.updateCommentsVisibility(commentsCount);
    }

    @Override
    public void initLikeButtonState(boolean exist) {
        if (likeController != null) {
            likeController.initLike(exist);
        }
    }

    @Override
    public void showComplainMenuAction(boolean show) {
        if (complainActionMenuItem != null) {
            complainActionMenuItem.setVisible(show);
        }
    }

    @Override
    public void showEditMenuAction(boolean show) {
        if (editActionMenuItem != null) {
            editActionMenuItem.setVisible(show);
        }
    }

    @Override
    public void showDeleteMenuAction(boolean show) {
        if (deleteActionMenuItem != null) {
            deleteActionMenuItem.setVisible(show);
        }
    }

    @Override
    public String getCommentText() {
        return commentEditText.getText().toString();
    }

    @Override
    public void openEditPostActivity(Post post) {
        Intent intent = new Intent(PostDetailsActivity.this, EditPostActivity.class);
        intent.putExtra(EditPostActivity.POST_EXTRA_KEY, post);
        intent.putExtra("emotion",emotionType);
        startActivityForResult(intent, EditPostActivity.EDIT_POST_REQUEST);
    }

    @Override
    public void showCommentProgress(boolean show) {
        commentsProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showCommentsWarning(boolean show) {
        warningCommentsTextView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showCommentsRecyclerView(boolean show) {
        commentsRecyclerView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onCommentsListChanged(List<Comment> list) {
        commentsAdapter.setList(list);
    }

    @Override
    public void showCommentsLabel(boolean show) {
        commentsLabel.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void openEditCommentDialog(Comment comment) {
        EditCommentDialog editCommentDialog = new EditCommentDialog();
        Bundle args = new Bundle();
        args.putString(EditCommentDialog.COMMENT_TEXT_KEY, comment.getText());
        args.putString(EditCommentDialog.COMMENT_ID_KEY, comment.getId());
        editCommentDialog.setArguments(args);
        editCommentDialog.show(getFragmentManager(), EditCommentDialog.TAG);
    }

    @Override
    public void onCommentChanged(String newText, String commentId) {
        presenter.updateComment(newText, commentId);
    }

    @Override
    public void onPostRemoved() {
        Intent intent = getIntent();
        setResult(RESULT_OK, intent.putExtra(POST_STATUS_EXTRA_KEY, PostStatus.REMOVED));
    }

    private class ActionModeCallback implements ActionMode.Callback {

        Comment selectedComment;

        ActionModeCallback(Comment selectedComment) {
            this.selectedComment = selectedComment;
        }

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.comment_context_menu, menu);

            menu.findItem(R.id.editMenuItem).setVisible(presenter.hasAccessToEditComment(selectedComment.getAuthorId()));

            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }
        // Called when the user selects a contextual menu item

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.editMenuItem:
                    openEditCommentDialog(selectedComment);
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                case R.id.deleteMenuItem:
                    presenter.removeComment(selectedComment.getId());
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }
        // Called when the user exits the action mode

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    }

    Animator.AnimatorListener authorAnimatorListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            authorAnimationInProgress = true;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            authorAnimationInProgress = false;
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            authorAnimationInProgress = false;
        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.post_details_menu, menu);
        complainActionMenuItem = menu.findItem(R.id.complain_action);
        editActionMenuItem = menu.findItem(R.id.edit_post_action);
        deleteActionMenuItem = menu.findItem(R.id.delete_post_action);
        presenter.updateOptionMenuVisibility();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!presenter.isPostExist()) {
            return super.onOptionsItemSelected(item);
        }

        // Handle item selection
        switch (item.getItemId()) {
            case R.id.complain_action:
                presenter.doComplainAction();
                return true;

            case R.id.edit_post_action:
                presenter.editPostAction();
                return true;

            case R.id.delete_post_action:
                presenter.attemptToRemovePost();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void httpRequest(){
        // 새로운 곡을 요청할  때 작곡 서버로 직접 요청을 보낸다
        // 아래에 보이는 ip주소 요청을 보내면 된다.
        OkHttpClient client = new OkHttpClient();
        String url = "http://192.168.43.113:5000/?emotion="+(int)emotionType;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if(response.isSuccessful()){
                    // no need to do any thing in android
                }
            }
        });
    }


    public void increasePlayedCounter() {
        // 새로운 곡이 요청되었음을 가리키는 count값을 올려주는 function이다
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("cnt"+emotionType).child(EMOTION.get((int)emotionType));
        myRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Integer currentValue = mutableData.getValue(Integer.class);
                if (currentValue == null) {
                    mutableData.setValue(1);
                } else {
                    mutableData.setValue(currentValue + 1);
                }

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                LogUtil.logInfo(TAG, "Updating Watchers count transaction is completed.");
            }
        });

    }
}
