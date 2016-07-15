package com.softdesign.devintensive.ui.activities;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.network.res.UserListRes;
import com.softdesign.devintensive.data.storage.models.UserDTO;
import com.softdesign.devintensive.ui.adapters.UserAdapter;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.NetworkStatusChecker;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserListActivity extends AppCompatActivity {
    private CoordinatorLayout mCoordinatorLayout;
    private Toolbar mToolbar;
    private DrawerLayout mNavigationDrawer;
    private RecyclerView mRecyclerView;
    private DataManager mDataManager;
    private UserAdapter mUserAdapter;
    private List<UserListRes.UserData> mUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        mDataManager = DataManager.getINSTANCE();
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_container);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mNavigationDrawer = (DrawerLayout) findViewById(R.id.navigation_drawer);
        mRecyclerView = (RecyclerView) findViewById(R.id.user_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        setupToolbar();
        setupDrawer();
        loadUser();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mNavigationDrawer.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSnackbar(String message) {
        Snackbar.make(mRecyclerView, message, Snackbar.LENGTH_LONG).show();
    }

    private void loadUser() {

        if (NetworkStatusChecker.isNetworkAvaliable(this)) {
            Call<UserListRes> call = mDataManager.getUserList();
            call.enqueue(new Callback<UserListRes>() {
                @Override
                public void onResponse(Call<UserListRes> call, Response<UserListRes> response) {
                    if (response.code() == 200) {
                        mUsers = (ArrayList<UserListRes.UserData>) response.body().getData();
                        mUserAdapter = new UserAdapter(mUsers,
                                new UserAdapter.UserViewHolder.CustomClickListener() {
                                    @Override
                                    public void onUserItemClickListener(int position) {
                                        UserDTO userDTO=new UserDTO(mUsers.get(position));
                                        Intent profileIntent=
                                                new Intent(UserListActivity.this,ProfileUserActivity.class);
                                        profileIntent.putExtra(ConstantManager.PARCELABLE_KEY,userDTO);
                                        startActivity(profileIntent);
                                    }
                                });
                        mRecyclerView.setAdapter(mUserAdapter);
                    } else if (response.code() == 404) {
                        showSnackbar("Неверный логин или пароль");
                    } else {
                        showSnackbar("Что то совсем не так");
                    }
                }

                @Override
                public void onFailure(Call<UserListRes> call, Throwable t) {
                }
            });
        } else {
            showSnackbar("Сеть не доступна, попробуйте позже");
        }
    }

    private void setupToolbar() {
// TODO: 14.07.2016 реализовать переход в другую активити при клике в меню в Navigation Drawer
    }

    private void setupDrawer() {
        setSupportActionBar(mToolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionbar.setDisplayHomeAsUpEnabled(true);
        }
    }

}
