package com.example.gs63.feedprototype.ui;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.transition.Fade;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.L;
import com.bumptech.glide.Glide;
import com.example.gs63.feedprototype.R;
import com.example.gs63.feedprototype.repository.CreatePostFirebase;
import com.example.gs63.feedprototype.viewmodels.CreatePostViewModel;

public class CreatePostActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView uploadedImage;
    private Button uploadImage;
    private CreatePostViewModel createPostViewModel;
    private EditText postCaption;
    private TextView progressBar;
    private boolean postCreated = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fade fade = new Fade();
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);
        setContentView(R.layout.activity_create_post);
        bindListenersAndViews();
        initializeViewModel();
    }

    private void bindListenersAndViews(){
    uploadedImage = findViewById(R.id.image_view);
    uploadImage = findViewById(R.id.upload_image);
    progressBar = findViewById(R.id.progress);
    Typeface boldTypeface = Typeface.createFromAsset(getAssets(), "fonts/Lato-Bold.ttf");
    progressBar.setTypeface(boldTypeface);
    postCaption = findViewById(R.id.post_caption);

    //adding listeners
    postCaption.setOnEditorActionListener(new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction (TextView v,int actionId, KeyEvent event){
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            hideKeyBoard();
            return true;
        }
        return false;
    }
    });

    uploadImage.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick (View view){
        if (isReadStoragePermissionGranted()) {
            openImageChooser();
        }
    }
    });

    FloatingActionButton createPost = findViewById(R.id.fab);
        createPost.setTransitionName("Create_post");
        createPost.setTag("Create_post");
        createPost.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick (View v){
        if (postCaption.getText() != null && !postCaption.getText().toString().isEmpty()) {
            postCreated = true;
            createPostViewModel.createPost(postCaption.getText().toString().trim()).observe(CreatePostActivity.this, new Observer<Boolean>() {
                @Override
                public void onChanged(@Nullable Boolean aBoolean) {
                    if (aBoolean != null && aBoolean) {
                        finishAfterTransition();
                        overridePendingTransition(0, 0);
                    }
                    else{
                        Toast.makeText(CreatePostActivity.this, "Post could'nt get created.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(CreatePostActivity.this, "Post caption cannot be empty.", Toast.LENGTH_SHORT).show();
        }
    }
    });
}
    private void initializeViewModel(){
        createPostViewModel = ViewModelProviders.of(this).get(CreatePostViewModel.class);
        createPostViewModel.init(getContentResolver());
        createPostViewModel.getProgress().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if(integer!=null) {
                    progressBar.setText("Uploaded " + integer + "%");
                    if (integer == 100) {
                       progressBar.setText("DONE");
                    }
                }
            }
        });
    }
    private void openImageChooser(){
        progressBar.setVisibility(View.VISIBLE);
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    private String getImageExtension(){
        return "png";
    }

    public  boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            openImageChooser();
        }

    }

    public void hideKeyBoard(){
        View focusedView = getCurrentFocus();
        if (focusedView != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        }
    }

    @Override
    public void finishAfterTransition() {
        Intent intent = new Intent();
        if(postCreated){
        intent.putExtra("POST_CREATED",true);
        postCreated = false;
        }
        else{
            intent.putExtra("POST_CREATED",false);
        }
        setResult(RESULT_OK, intent);
        super.finishAfterTransition();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // put this data in view model
            createPostViewModel.setImageUri(data.getData()).observe(CreatePostActivity.this, new Observer<Boolean>() {
                @Override
                public void onChanged(@Nullable Boolean aBoolean) {
                    if (aBoolean != null && aBoolean) {
                        Glide.with(CreatePostActivity.this).load(createPostViewModel.imageUri).into(uploadedImage);
                        uploadedImage.setVisibility(View.VISIBLE);
                        createPostViewModel.setImageUrl();
                        //setting max length of caption to 50 in case of image posts
                        int maxLength = 50;
                        postCaption.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});

                        if(postCaption.getText().toString().length()>50){
                            Toast.makeText(CreatePostActivity.this,"Text for image post cannot be greater than 50 characters.Truncating post caption to 50 characters",Toast.LENGTH_LONG).show();
                            postCaption.setText(postCaption.getText().toString().substring(0,50));
                        }
                    }
                    else{
                        Toast.makeText(CreatePostActivity.this,"Oops!Image could'nt be uploaded",Toast.LENGTH_LONG).show();
                    }
                }
            });
            createPostViewModel.imageExtension = getImageExtension();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.slide_out_right);
    }

}
