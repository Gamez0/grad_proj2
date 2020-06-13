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

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.praylist.socialcomponents.R;
import com.praylist.socialcomponents.main.post.BaseAddPostActivity;

//import com.rozdoum.socialcomponents.main.post.BasePostActivity;
//import com.rozdoum.socialcomponents.main.post.createPost.CreatePostPresenter;
//import com.rozdoum.socialcomponents.main.post.createPost.CreatePostView;


public class AddPostActivity extends BaseAddPostActivity<AddPostView, AddPostPresenter> implements AddPostView {
    public static final int CREATE_NEW_POST_REQUEST = 11;
    public static final String POST_EXTRA_KEY = "AddPostActivity.POST_EXTRA_KEY";
    public static final String USERNAME_EXTRA_KEY = "AddPostActivity.USERNAME_EXTRA_KEY";
    public static final String AUTHOR_EXTRA_KEY = "AddPostActivity.AUTHOR_EXTRA_KEY";

    public static final int request_ok =1;

    //protected String prayerForId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String prayer = (String) getIntent().getSerializableExtra(POST_EXTRA_KEY);
        String prayerFor =(String) getIntent().getSerializableExtra(USERNAME_EXTRA_KEY);
        String prayerForId = (String) getIntent().getSerializableExtra(AUTHOR_EXTRA_KEY);

        fillUIFields(prayer,prayerFor,prayerForId);
    }

    @NonNull
    @Override
    public AddPostPresenter createPresenter() {
        if (presenter == null) {
            return new AddPostPresenter(this);
        }
        return presenter;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create_post_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.post:
                presenter.doSavePost(imageUri);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Toast.makeText(this.getApplicationContext(),"onActivityResult() called.",Toast.LENGTH_LONG).show();// not working
//        Log.d(TAG,"onActivityResult called.");
        if(requestCode==request_ok){
//            Log.d(TAG,"requestCode == request_ok");
            if(resultCode==0){
                //모두에게 공개
//                Toast.makeText(this.getApplicationContext(),"모두에게 공개",Toast.LENGTH_LONG).show();
//                Log.d(TAG,"모두에게 공개");
            } else if(resultCode==1){
                //동역자들에게 공개
//                Log.d(TAG,"동역자에게 공개");
            } else if(resultCode==2){
                //나만
//                Log.d(TAG,"나만 보기");
            }
        }
    }
    private void fillUIFields(String prayer, String prayerFor,String prayerForId) {
        prayerNameTextView.setText(prayerFor);
        descriptionEditText.setText(prayer);
        prayerForIdTextView.setText(prayerForId);
    }


}
