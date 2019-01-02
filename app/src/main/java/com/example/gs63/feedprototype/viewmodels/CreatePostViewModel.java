package com.example.gs63.feedprototype.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;

import com.example.gs63.feedprototype.datamodels.Post;
import com.example.gs63.feedprototype.repository.CreatePostFirebase;
import com.example.gs63.feedprototype.repository.SessionManager;
import com.example.gs63.feedprototype.repository.UploadImageFirebase;
import com.example.gs63.feedprototype.ui.CreatePostActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CreatePostViewModel extends ViewModel {
    public String imageExtension;
    public MutableLiveData<Integer> progress = new MutableLiveData<>();
    public Uri imageUri;
    private UploadImageFirebase uploadImageFirebase;
    private CreatePostFirebase createPostFirebase;
    private String imageUrl;
    private ContentResolver contentResolver;

    public void init(ContentResolver contentResolver){
     uploadImageFirebase = new UploadImageFirebase();
     createPostFirebase = new CreatePostFirebase();
     this.contentResolver = contentResolver;
    }

    public MutableLiveData<Boolean> setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
        return uploadImageFirebase.uploadImage(imageExtension, compressImageAndConvertToByteArray());
    }

    public MutableLiveData<Boolean> createPost(String text){
        Post post = new Post();
        post.setImageUrl(imageUrl);
        post.setPostText(text);
        post.setUserId(SessionManager.getUserId());
        post.setTimeStamp(-1 * (System.currentTimeMillis()));
        return createPostFirebase.createPost(post);
    }

    public String setImageUrl(){
        this.imageUrl = uploadImageFirebase.imageUrl;
        return imageUrl;
    }

    private byte[] compressImageAndConvertToByteArray(){
        Bitmap bmp = null;
        try {
            bmp = MediaStore.Images.Media.getBitmap(contentResolver, imageUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public MutableLiveData<Integer> getProgress(){
        return uploadImageFirebase.progress;
    }
}
