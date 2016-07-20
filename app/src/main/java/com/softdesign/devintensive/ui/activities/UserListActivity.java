package com.softdesign.devintensive.ui.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.storage.models.User;
import com.softdesign.devintensive.data.storage.models.UserDTO;
import com.softdesign.devintensive.ui.adapters.UserAdapter;
import com.softdesign.devintensive.utils.CircleTransform;
import com.softdesign.devintensive.utils.ConstantManager;
import com.squareup.picasso.Picasso;

import java.util.List;


public class UserListActivity extends AppCompatActivity {

    private static final String TAG = ConstantManager.TAG_PREFIX + "UserListActivity";


    private CoordinatorLayout mCoordinatorLayout;
    private Toolbar mToolbar;
    private DrawerLayout mNavigationDrawer;
    private RecyclerView mRecyclerView;
    private DataManager mDataManager;
    private UserAdapter mUserAdapter;
    private List<User> mUsers;
    private MenuItem mSearchItem;
    private String mQuery;

    private View mHeaderLayout;
    private ImageView mUserAvatar;

    private NavigationView mNavigationView;
    private Handler mHandler;


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
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mHeaderLayout = (View) mNavigationView.getHeaderView(0);
        mUserAvatar = (ImageView) mHeaderLayout.findViewById(R.id.user_avatar);
        Picasso.with(this)
                .load(mDataManager.getPreferencesManager().loadUserAvatar())
                .transform(new CircleTransform())
                .placeholder(R.drawable.user_avatar)
                .into(mUserAvatar);

        mHandler = new Handler();
        setupToolbar();
        setupDrawer();
        loadUserFromDb();
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

    private void loadUserFromDb() {

        mUsers = mDataManager.getUserListFromDb();
        mUserAdapter = new UserAdapter(mUsers,
                new UserAdapter.UserViewHolder.CustomClickListener() {
                    @Override
                    public void onUserItemClickListener(int position) {
                        UserDTO userDTO = new UserDTO(mUsers.get(position));
                        Intent profileIntent =
                                new Intent(UserListActivity.this, ProfileUserActivity.class);
                        profileIntent.putExtra(ConstantManager.PARCELABLE_KEY, userDTO);
                        startActivity(profileIntent);
                    }
                });
        mRecyclerView.setAdapter(mUserAdapter);
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionbar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        final Intent mainIntent = new Intent(this, MainActivity.class);

        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener
                    (new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(MenuItem item) {
                            item.setChecked(true);

                            switch (item.getItemId()) {
                                case R.id.user_profile_menu:
                                    startActivity(mainIntent);

                                    break;
                                case R.id.user_team_menu:
                                    mNavigationDrawer.closeDrawer(GravityCompat.START);
                                    break;
                            }
                            mNavigationDrawer.closeDrawer(GravityCompat.START);
                            return false;
                        }

                    });
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        mSearchItem = menu.findItem(R.id.search_action);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(mSearchItem);

        searchView.setQueryHint("Введите имя пользователя");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                showUsersByQuery(newText);
                return false;
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    private void showUsers(List<User> users) {
        mUsers = users;

        mUserAdapter = new UserAdapter(mUsers,
                new UserAdapter.UserViewHolder.CustomClickListener() {
                    @Override
                    public void onUserItemClickListener(int position) {
                        UserDTO userDTO = new UserDTO(mUsers.get(position));
                        Intent profileIntent =
                                new Intent(UserListActivity.this, ProfileUserActivity.class);
                        profileIntent.putExtra(ConstantManager.PARCELABLE_KEY, userDTO);
                        startActivity(profileIntent);
                    }
                });
        mRecyclerView.swapAdapter(mUserAdapter, false);
    }

    private void showUsersByQuery(final String query) {
        mQuery = query;
        Runnable searchUsers = new Runnable() {
            @Override
            public void run() {

                showUsers(mDataManager.getUserListByName(query));
            }
        };
        mHandler.removeCallbacks(searchUsers);
        mHandler.postDelayed(searchUsers, ConstantManager.SEARCH_DELAY);
    }


}





