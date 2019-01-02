package com.example.gs63.feedprototype.viewmodels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.gs63.feedprototype.datamodels.Post;
import com.example.gs63.feedprototype.repository.FirebaseFeedServices;

import java.util.ArrayList;

public class FeedViewModel extends ViewModel {

    private FirebaseFeedServices fetchPostsService;
    public MutableLiveData<Boolean> updatingPost = new MutableLiveData<>();

    public FeedViewModel() {
    }

    public void init(){
        fetchPostsService = new FirebaseFeedServices();
    }

    public MutableLiveData<ArrayList<Post>> fetchPosts(){
        return fetchPostsService.fetchPosts();
    }

    public void deletePost(Post post){
       fetchPostsService.removePost(post);
    }

    public void updatePost(Post post){
        updatingPost.postValue(true);
        fetchPostsService.updatePost(post);
    }
}
