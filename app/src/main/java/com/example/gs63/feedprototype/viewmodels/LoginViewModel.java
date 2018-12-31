package com.example.gs63.feedprototype.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.widget.Toast;

import com.example.gs63.feedprototype.repository.SessionManager;
import com.example.gs63.feedprototype.ui.LoginActivity;

public class LoginViewModel extends ViewModel {

    public MutableLiveData<Boolean> loginSuccessful = new MutableLiveData<>();

    public void init(Context context){
        SessionManager.setContext(context);
        if(SessionManager.getUserId().isEmpty()) {
            loginSuccessful.postValue(false);
        }
        else{
            loginSuccessful.postValue(true);
        }
    }

    public void setUser(String userId){
        SessionManager.setUserId(userId);
        loginSuccessful.postValue(true);
    }
}
