package com.example.gs63.feedprototype.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gs63.feedprototype.R;
import com.example.gs63.feedprototype.viewmodels.LoginViewModel;

public class LoginActivity extends AppCompatActivity {

    private TextView title;
    private LoginViewModel mLoginViewModel;
    private Button loginButton;
    private EditText userInputEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bindViews();
        initializeViewModel();

    }

    private void initializeViewModel(){
        mLoginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        mLoginViewModel.init(LoginActivity.this);
        mLoginViewModel.loginSuccessful.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if(aBoolean!=null && aBoolean){
                    Intent intent = new Intent(LoginActivity.this,FeedActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(0, R.anim.anim_slide_out_left);

                }
            }
        });
    }

    private void bindViews(){
        title = findViewById(R.id.app_title);
        Typeface boldTypeface = Typeface.createFromAsset(getAssets(), "fonts/Lato-Bold.ttf");
        title.setTypeface(boldTypeface);
        loginButton = findViewById(R.id.login_button);
        userInputEditText = findViewById(R.id.user_id_input);

        loginButton.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideKeyBoard();
                    return true;
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userInputEditText.getText().toString().trim().isEmpty()){
                    Toast.makeText(LoginActivity.this,"User Id cannot be empty.Please enter an Id.",Toast.LENGTH_LONG).show();
                    return;
                }
                mLoginViewModel.setUser(userInputEditText.getText().toString().trim());

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

}
