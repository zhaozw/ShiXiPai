package com.shixipai.interactor.login;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.shixipai.api.ApiClient;
import com.shixipai.bean.User;
import com.shixipai.bean.login.PostedJob;
import com.shixipai.ui.login.login.OnLoginCallback;
import com.shixipai.ui.login.login.OnSyncJobIdCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by xiepeng on 16/1/13.
 */
public class LoginInteractorImpl implements LoginInteractor {


    @Override
    public void login(final String username, final String password, final OnLoginCallback onLoginCallback){
        ApiClient.userLogin(username, password, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    String result = response.getString("result");

                    if (result.equals("login success")) {
                        onLoginCallback.onSuccess(new User(username, password));
                    } else if (result.equals("login failed,user not exists")) {
                        onLoginCallback.onFailure("用户名不存在");
                    } else if (result.equals("login failed,password worng")) {
                        onLoginCallback.onFailure("密码错误");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

            }
        });
    }

    @Override
    public void syncJobId(String username, final OnSyncJobIdCallback callback) {
        ApiClient.syncPostedJob(username, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                Gson gson = new Gson();
                try {
                    Type listType = new TypeToken<ArrayList<PostedJob>>() {}.getType();

                    ArrayList<PostedJob> list = new ArrayList<PostedJob>();
                    list = gson.fromJson(response.getString("data"),listType);

                    callback.onSyncSuccess(list);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}

