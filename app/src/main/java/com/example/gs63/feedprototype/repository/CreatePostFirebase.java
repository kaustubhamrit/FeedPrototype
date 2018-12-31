package com.example.gs63.feedprototype.repository;

import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.example.gs63.feedprototype.datamodels.Post;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreatePostFirebase {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    public MutableLiveData<Boolean> successfullyPosted = new MutableLiveData<>();

    public CreatePostFirebase() {
        this.firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("posts");
    }

    public MutableLiveData<Boolean> createPost(Post post){
        String pushPosition = databaseReference.push().getKey();
        post.setPushKey(pushPosition);
        databaseReference.child(pushPosition).setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                successfullyPosted.setValue(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                successfullyPosted.setValue(false);
            }
        });
      return  successfullyPosted;
    }
}
