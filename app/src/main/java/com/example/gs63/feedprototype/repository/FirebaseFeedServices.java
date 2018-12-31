package com.example.gs63.feedprototype.repository;

import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.airbnb.lottie.L;
import com.example.gs63.feedprototype.datamodels.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class FirebaseFeedServices {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Long lastFetchedKey;
    MutableLiveData<ArrayList<Post>> posts = new MutableLiveData<>();
    ValueEventListener valueEventListener;


    public FirebaseFeedServices() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("/posts");
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Post> list = new ArrayList<Post>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    list.add(child.getValue(Post.class));
                }

                if (list.size() > 0) {
                    lastFetchedKey = list.get(list.size() - 1).getTimeStamp();
                }
                if (list.size() > 0) {
                    list.remove(list.size() - 1);
                }
                posts.setValue(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };


    }

    public MutableLiveData<ArrayList<Post>> fetchPosts() {
        if (lastFetchedKey != null) {
            databaseReference.orderByChild("timeStamp").startAt(lastFetchedKey).limitToFirst(6).addValueEventListener(valueEventListener);
        } else {
            databaseReference.orderByChild("timeStamp").limitToFirst(6).addValueEventListener(valueEventListener);
        }
        return posts;
    }

    public void removePost(Post post) {
        databaseReference.child(post.getPushKey()).removeValue();
    }

    public void updatePost(Post post) {
        databaseReference.child(post.getPushKey()).setValue(post);
    }
}