package com.example.gs63.feedprototype.ui;

import android.app.ActivityOptions;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.util.Util;
import com.example.gs63.feedprototype.R;
import com.example.gs63.feedprototype.animators.SlideInLeftAnimator;
import com.example.gs63.feedprototype.datamodels.Post;
import com.example.gs63.feedprototype.utils.RecyclerViewEndlessScrollListener;
import com.example.gs63.feedprototype.viewmodels.FeedViewModel;
import com.google.gson.Gson;

import java.util.ArrayList;

public class FeedActivity extends AppCompatActivity {

    private RecyclerView postsList;
    private PostsAdapter postsAdapter;
    private FeedViewModel mFeedViewModel;
    private LottieAnimationView loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_right, R.anim.anim_slide_out_left);
        setContentView(R.layout.activity_feed);
        initializeViewModel();
        bindView();
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        overridePendingTransition(0,0);
        if(data.getBooleanExtra("POST_CREATED",false)){
            Gson gson = new Gson();
            Post post = gson.fromJson(data.getStringExtra("POST"),Post.class);
            postsAdapter.addPostsToTop(post);
        }
        Fade fade = new Fade();
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);
        postsList.post(new Runnable() {
            @Override
            public void run() {
                postsList.scrollToPosition(0);
            }
        });
    }

    private void bindView(){
        loader = findViewById(R.id.loader);
        postsList = findViewById(R.id.posts_list);
        postsList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        postsAdapter = new PostsAdapter(mFeedViewModel);
        postsList.setAdapter(postsAdapter);
        postsAdapter.hideKeyboard.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if(aBoolean!=null && aBoolean) {
                    hideKeyBoard();
                }
            }
        });

        postsList.addOnScrollListener(new RecyclerViewEndlessScrollListener(((LinearLayoutManager) postsList.getLayoutManager())) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                mFeedViewModel.fetchPosts();
                Toast.makeText(FeedActivity.this,"Loading more posts", Toast.LENGTH_SHORT).show();
                }

            @Override
            public void onScrolled(RecyclerView view, int dx, int dy) {
                super.onScrolled(view, dx, dy);
                if(dy>300){
                    hideKeyBoard();
                }
            }
        });

        postsList.setItemAnimator(new SlideInLeftAnimator());
        FloatingActionButton createPost = (FloatingActionButton) findViewById(R.id.fab);
        createPost.setTransitionName("Create_post");
        createPost.setTag("Create_post");
        createPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCreatePost(view);
            }
        });
    }

    private void initializeViewModel(){
        mFeedViewModel = ViewModelProviders.of(this).get(FeedViewModel.class);
        mFeedViewModel.init();
        addObservers();
    }

    private void addObservers(){
        mFeedViewModel.fetchPosts().observe(this, new Observer<ArrayList<Post>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Post> posts) {
                if(posts!=null) {
                        postsAdapter.addPosts(posts);
                        loader.cancelAnimation();
                        loader.setVisibility(View.GONE);
                    }
            }
        });

    }

    private void hideKeyBoard(){
        View focusedView = getCurrentFocus();
        if (focusedView != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        }
    }

    private void startCreatePost(View view){
        Intent intent = new Intent(this, CreatePostActivity.class);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this,
                view, view.getTransitionName()).toBundle());
        overridePendingTransition(0, 0);

    }
}
