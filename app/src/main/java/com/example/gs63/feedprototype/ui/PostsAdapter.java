package com.example.gs63.feedprototype.ui;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gs63.feedprototype.R;
import com.example.gs63.feedprototype.datamodels.Post;
import com.example.gs63.feedprototype.repository.SessionManager;
import com.example.gs63.feedprototype.viewmodels.FeedViewModel;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostsViewHolder> {
    public MutableLiveData<Boolean> hideKeyboard = new MutableLiveData<>();

    public ArrayList<Post> getPosts() {
        return posts;
    }

    private ArrayList<Post> posts = new ArrayList<>();
    private FeedViewModel feedViewModel;

    public void addPosts(ArrayList<Post> posts) {
        int loadPosition = this.posts.size();
        for (int i = 0; i < posts.size(); i++) {
            if (!this.posts.contains(posts.get(i))) {
                this.posts.add(posts.get(i));
                notifyItemInserted(loadPosition + i);
            }
        }
    }

    public void addPostsToTop(ArrayList<Post> posts) {
        for (int i = 0; i < posts.size(); i++) {
            if (!this.posts.contains(posts.get(i))) {
                this.posts.add(0, posts.get(i));
                notifyItemInserted(i);
            }
        }
    }




    public PostsAdapter(FeedViewModel feedViewModel) {
        super();
        hideKeyboard.setValue(false);
        this.feedViewModel = feedViewModel;
    }

    @NonNull
    @Override
    public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.post_card_view, viewGroup, false);
        return new PostsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PostsViewHolder postsViewHolder, int i) {
    postsViewHolder.bindData(posts.get(i));
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class PostsViewHolder extends RecyclerView.ViewHolder{
        private TextView userId;
        private ImageView postImage;
        private TextView postCaption;
        private TextView timeStamp;
        private View itemView;
        private ImageView delete;
        private TextView edit;
        private EditText editText;


        public PostsViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            userId = itemView.findViewById(R.id.user_id_tv);
            postImage = itemView.findViewById(R.id.post_image);
            timeStamp = itemView.findViewById(R.id.time_stamp);
            postCaption = itemView.findViewById(R.id.post_caption);
            edit = itemView.findViewById(R.id.edit);
            delete = itemView.findViewById(R.id.delete);
            editText = itemView.findViewById(R.id.edit_text);
        }

        public void bindData(Post post){
            //resetting values to default because of recycler view behaviour
            resetToDefaultState();

            Typeface boldTypeface = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Lato-Bold.ttf");
            userId.setTypeface(boldTypeface);
            userId.setText(post.getUserId());
            postCaption.setText(post.getPostText());
            if(!post.getImageUrl().isEmpty()){
                Glide.with(itemView.getContext()).load(post.getImageUrl()).into(postImage);
                postImage.setVisibility(View.VISIBLE);
            }
            if(SessionManager.getUserId().equalsIgnoreCase(post.getUserId())){
                edit.setVisibility(View.VISIBLE);
                if(post.getImageUrl().isEmpty()) {
                    delete.setVisibility(View.VISIBLE);
                }
            }
            addClickListeners(post);
            timeStamp.setText(getTime(-(post.timeStamp)));
        }

        private void resetToDefaultState(){
            postImage.setVisibility(View.GONE);
            edit.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
            postImage.setImageBitmap(null);
            editText.setText("");
            editText.setVisibility(View.GONE);
            editText.clearFocus();
            postCaption.setText("");
            postCaption.setVisibility(View.VISIBLE);
        }

        private void addClickListeners(final Post post){
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = posts.indexOf(post);
                    posts.remove(index);
                    notifyItemRemoved(index);
                    feedViewModel.deletePost(post);

                }
            });

            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        hideKeyboard.setValue(true);
                        int index = posts.indexOf(post);
                        post.setPostText(editText.getText().toString());
                        feedViewModel.updatePost(post);
                        post.setTimeStamp(-1 * System.currentTimeMillis());
                        editText.setVisibility(View.GONE);
                        postCaption.setVisibility(View.VISIBLE);
                        notifyItemChanged(index);
                        Toast.makeText(itemView.getContext(),"Post Updated Successfully",Toast.LENGTH_LONG).show();
                        return true;
                    }
                    return false;
                }
            });

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editText.setHint(postCaption.getText().toString());
                    if(postCaption.getText().toString().trim().isEmpty()){
                        editText.setHint("Update your post caption here!");
                    }
                    postCaption.setVisibility(View.GONE);
                    editText.setVisibility(View.VISIBLE);
                    editText.requestFocus();
                    editText.setFocusable(true);
                    editText.setFocusableInTouchMode(true);
                    editText.setCursorVisible(true);
                    showKeyboard(editText);
                }
            });
        }

        private String getTime(Long timeStamp){
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd HH:mm");
            Date resultdate = new Date(timeStamp);
            return  sdf.format(resultdate);
        }

    }

    public void showKeyboard(EditText editText) {
        editText.requestFocus();
        editText.setFocusableInTouchMode(true);
        InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, 0);
    }
}
