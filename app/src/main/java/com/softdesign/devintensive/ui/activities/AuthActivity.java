package com.softdesign.devintensive.ui.activities;

import android.content.Context;
import android.content.Intent;

import android.net.Uri;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.network.req.UserLoginReq;
import com.softdesign.devintensive.data.network.res.UserListRes;
import com.softdesign.devintensive.data.network.res.UserModelRes;
import com.softdesign.devintensive.data.storage.models.Repository;

import com.softdesign.devintensive.data.storage.models.RepositoryDao;
import com.softdesign.devintensive.data.storage.models.User;

import com.softdesign.devintensive.data.storage.models.UserDao;
import com.softdesign.devintensive.utils.AppConfig;
import com.softdesign.devintensive.utils.NetworkStatusChecker;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthActivity extends BaseActivity implements View.OnClickListener {
    private Button mSignIn;
    private TextView mRememberPassword;
    private EditText mLogin, mPassword;
    private CoordinatorLayout mCoordinatorLayout;
    private DataManager mDataManager;
    private RepositoryDao mRepositoryDao;
    private UserDao mUserDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mDataManager = DataManager.getINSTANCE();
        mUserDao = mDataManager.getDaoSession().getUserDao();
        mRepositoryDao = mDataManager.getDaoSession().getRepositoryDao();
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_coordinator_container);
        mSignIn = (Button) findViewById(R.id.login_btn);
        mSignIn.setOnClickListener(this);
        mRememberPassword = (TextView) findViewById(R.id.login_forgot_password);
        mRememberPassword.setOnClickListener(this);
        mPassword = (EditText) findViewById(R.id.login_password_et);
        mPassword.setOnClickListener(this);
        mLogin = (EditText) findViewById(R.id.login_email_et);
        mLogin.setText(mDataManager.getPreferencesManager().getUserLogin());

        if (mLogin.getText().toString().matches("")) {

        } else {
            mPassword.requestFocus();
            showSnackbar("введите пароль");
        }
        mLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                signIn();
                break;
            case R.id.login_forgot_password:
                rememberPassword();
                break;
        }
    }

    private void showSnackbar(String message) {
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    private void rememberPassword() {
        Intent rememberIntent = new Intent(Intent.ACTION_VIEW, Uri.parse
                ("http://devintensive.softdesign-apps.ru/forgotpass"));
        startActivity(rememberIntent);
    }

    private void loginSuccsess(UserModelRes userModel) {
        showSnackbar("Вход успешный!");
        mDataManager.getPreferencesManager().saveAuthToken(userModel.getData().getToken());
        mDataManager.getPreferencesManager().saveUserId(userModel.getData().getUser().getId());
        mDataManager.getPreferencesManager().saveUserLogin(mLogin.getText());
        savedUserValues(userModel);
        saveUserInDb();
        savedUserFieldsValues(userModel);
        savedProfilePictureValue(userModel);
        savedHeaderAvatarValue(userModel);
        savedHeaderFieldsValue(userModel);
        showProgress();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent loginIntent = new Intent(AuthActivity.this, UserListActivity.class);
                startActivity(loginIntent);
            }
        }, AppConfig.STAR_DELAY);
    }

    private void signIn() {
        if (NetworkStatusChecker.isNetworkAvaliable(this)) {
            Call<UserModelRes> call = mDataManager.loginUser(new UserLoginReq(mLogin.getText().toString()
                    , mPassword.getText().toString()));
            call.enqueue(new Callback<UserModelRes>() {
                @Override
                public void onResponse(Call<UserModelRes> call, Response<UserModelRes> response) {
                    if (response.code() == 200) {
                        loginSuccsess(response.body());
                    } else if (response.code() == 404) {
                        showSnackbar("Неверный логин или пароль");
                    } else {
                        showSnackbar("Что то совсем не так");
                    }
                }

                @Override
                public void onFailure(Call<UserModelRes> call, Throwable t) {
                }
            });
        } else {
            showSnackbar("Сеть не доступна, попробуйте позже");
        }
    }

    private void savedUserValues(UserModelRes userModel) {
        int[] userValues = {
                userModel.getData().getUser().getProfileValues().getRating(),
                userModel.getData().getUser().getProfileValues().getLinesCode(),
                userModel.getData().getUser().getProfileValues().getProjects()
        };
        mDataManager.getPreferencesManager().saveUserProfileValues(userValues);

    }

    private void savedUserFieldsValues(UserModelRes userModel) {
        String[] userFieldValues = {
                userModel.getData().getUser().getContacts().getPhone(),
                userModel.getData().getUser().getContacts().getEmail(),
                userModel.getData().getUser().getContacts().getVk(),
                userModel.getData().getUser().getRepositories().getRepo().get(0).getGit(),
                userModel.getData().getUser().getPublicInfo().getBio()
        };
        mDataManager.getPreferencesManager().saveUserFieldValues(userFieldValues);
    }

    private void savedProfilePictureValue(UserModelRes userModel) {
        String profilePhotoValue = userModel.getData().getUser().getPublicInfo().getPhoto();
        mDataManager.getPreferencesManager().saveUserPhotoValue(profilePhotoValue);
    }

    private void savedHeaderFieldsValue(UserModelRes userModel) {
        String[] userHeaderValues = {
                userModel.getData().getUser().getFirstName(),
                userModel.getData().getUser().getSecondName(),
                userModel.getData().getUser().getContacts().getEmail()
        };
        mDataManager.getPreferencesManager().saveUserHeaderValue(userHeaderValues);
    }

    private void savedHeaderAvatarValue(UserModelRes userModel) {
        String profileAvatarValue = userModel.getData().getUser().getPublicInfo().getAvatar();
        mDataManager.getPreferencesManager().saveUserAvatarValue(profileAvatarValue);
    }

    private void saveUserInDb() {
        if (NetworkStatusChecker.isNetworkAvaliable(this)) {
            Call<UserListRes> call = mDataManager.getUserListFromNetwork();
            call.enqueue(new Callback<UserListRes>() {
                @Override
                public void onResponse(Call<UserListRes> call, Response<UserListRes> response) {

                    try {
                        if (response.code() == 200) {
                            List<Repository> allRepositories = new ArrayList<Repository>();
                            List<User> allUsers = new ArrayList<User>();

                            for (UserListRes.UserData userRes : response.body().getData()) {
                                allRepositories.addAll(getRepoListFromUserRes(userRes));
                                allUsers.add(new User(userRes));
                                Log.e(TAG, "Загрузка пользоваеля в базу данных " + userRes.getFullName());
                            }
                            mRepositoryDao.insertOrReplaceInTx(allRepositories);
                            mUserDao.insertOrReplaceInTx(allUsers);
                        } else {
                            showSnackbar("Список пользователей не может быть получен");
                            Log.e(TAG, "OnResponse: " + String.valueOf(response.errorBody().source()));

                        }

                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        showSnackbar("Что то пошло не так");
                    }
                }

                @Override
                public void onFailure(Call<UserListRes> call, Throwable t) {
                }
            });
            //end check network
        } else {
            showSnackbar("Сеть не доступна, попробуйте позже");
        }

    }


    private List<Repository> getRepoListFromUserRes(UserListRes.UserData userData) {
        final String userId = userData.getId();
        List<Repository> repositories = new ArrayList<>();
        for (UserModelRes.Repo repositoryRes : userData.getRepositories().getRepo()) {
            repositories.add(new Repository(repositoryRes, userId));
        }
        return repositories;
    }

}


