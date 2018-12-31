package com.example.gs63.feedprototype.repository;

import android.arch.lifecycle.MutableLiveData;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.webkit.MimeTypeMap;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UploadImageFirebase {

    private StorageReference storageReference;
    public MutableLiveData<Boolean> uploadSuccessful = new MutableLiveData<>();
    public MutableLiveData<Integer> progress = new MutableLiveData<>();
    public String imageUrl;

    public UploadImageFirebase() {
        this.storageReference = FirebaseStorage.getInstance().getReference("uploads");
    }

    public MutableLiveData<Boolean> uploadImage(String imageExtension, final byte[] imageUri){
       if(imageUri!=null){
           final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + imageExtension);
           UploadTask uploadTask = fileReference.putBytes(imageUri);
           uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
               @Override
               public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                   fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                       @Override
                       public void onSuccess(Uri uri) {
                           imageUrl = uri.toString();
                           uploadSuccessful.setValue(true);
                       }
                   });
               }
           }).addOnFailureListener(new OnFailureListener() {
               @Override
               public void onFailure(@NonNull Exception e) {
                   uploadSuccessful.setValue(false);
               }
           }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
               @Override
               public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                   progress.setValue(100 * ((int) taskSnapshot.getBytesTransferred())/ ((int) taskSnapshot.getTotalByteCount()));
               }
           });
       }
       return uploadSuccessful;
    }

}
