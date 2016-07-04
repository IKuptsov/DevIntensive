package com.softdesign.devintensive.ui.activities;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.utils.ConstantManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = ConstantManager.TAG_PREFIX + "Main Activity";
    private DataManager mDataManager;
    private int mCurrentEditMode=0;
    private ImageView mCallimg;
    private CoordinatorLayout mCoordinatorLayout;
    private Toolbar mToolbar;
    private DrawerLayout mNavigationDrawer;
    private FloatingActionButton mFab;
    private EditText mUserPhone,mUserEmail,mUserVk,mUserGit,mUserBio;
    private List<EditText> mUserInfoViews;



    /**
     +     * Метод вызывается при создании активити после изменения конфигурации/возврата к текушей
     +     * активити после его уничтожения.
     +     *
     +     * в данном методе инициализируются/производится:
     +     * - UI пользовательский интерфейс (статика)
     +     * - инициализация статических данных активити
     +     * - связь данных со списками (инициализация адаптеров)
     +     *
     +     * Не запускать длительные операции по работе с данными в onCreate() !!!
     +     *
     +     * @param savedInstanceState - объект со значениями сохраненными в Bundle - состояние UI
     +     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "OnCreate");
        init();
        setupToolBar();
        setupDrawer();
        loadUserInfoValue();

        if (savedInstanceState == null) {
            //активность запускается в первый  раз

        } else {
            mCurrentEditMode=savedInstanceState.getInt(ConstantManager.EDIT_MORE_KEY,0);
            changeEditMode(mCurrentEditMode);

        }
    }
    /** Initializator*/
    private void init (){
        mCallimg = (ImageView) findViewById(R.id.call_img);
        mCoordinatorLayout=(CoordinatorLayout)findViewById(R.id.main_coordinator_container);
        mToolbar=(Toolbar)findViewById(R.id.toolbar);
        mDataManager=DataManager.getINSTANCE();
        mNavigationDrawer =(DrawerLayout) findViewById(R.id.navigation_drawer);
        mFab=(FloatingActionButton) findViewById(R.id.fab);
        mCallimg.setOnClickListener(this);
        mFab.setOnClickListener(this);
        mUserPhone=(EditText)findViewById(R.id.mobile_number) ;
        mUserEmail=(EditText)findViewById(R.id.email);
        mUserVk=(EditText)findViewById(R.id.vk_profile);
        mUserGit=(EditText)findViewById(R.id.github_reposit);
        mUserBio=(EditText)findViewById(R.id.bio);
        mUserInfoViews =new ArrayList<>();
        mUserInfoViews.add(mUserPhone);
        mUserInfoViews.add(mUserEmail);
        mUserInfoViews.add(mUserVk);
        mUserInfoViews.add(mUserGit);
        mUserInfoViews.add(mUserBio);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ConstantManager.EDIT_MORE_KEY,mCurrentEditMode);
    }

   /*Метод открывает меню по клику на иконку в левом углу */
   @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            mNavigationDrawer.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     +     * Метод вызывается при старте активити перед моментом того как UI станет достепен пользователю
     +     * как правило в данном методе происходит регистрация подписки на событиея остановка которых была
     +     * произведена в onStop()
     +     */
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    /**take Click*/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.call_img:
                /*showProgress();
                runWithDelay();*/
                break;
            case R.id.fab:
                if (mCurrentEditMode==0){
                    changeEditMode(1);
                    mCurrentEditMode=1;
                    showSnackBar("Включен режим реактирования");
                }else {
                    changeEditMode(0);
                    mCurrentEditMode=0;
                }
                break;
        }

    }

/*    private  void runWithDelay (){
        final Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //TODO: выполнить с задержкой
                hideProgress();
            }
        },5000);
    }*/
    private void showSnackBar(String message){
        Snackbar.make(mCoordinatorLayout, message,Snackbar.LENGTH_LONG).show();
    }

    /*Подменяем тулбар и ставим кнопку на открытие меню*/
    private void setupToolBar(){
        setSupportActionBar(mToolbar);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar !=null){
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    private void setupDrawer(){
        NavigationView navigationView=(NavigationView)findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener
                (new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                showSnackBar(item.getTitle().toString());
                item.setChecked(true);
                mNavigationDrawer.closeDrawer(GravityCompat.START);
                return false;
            }
        });
    }

    /**переключает режим редактирования
     * @param mode если 1 режим то режим редактирования, если 0 то режим чтения
     */
    private void changeEditMode(int mode){
        if(mode==1){
            mFab.setImageResource(R.drawable.ic_done_black_24dp);
            for(EditText userValue: mUserInfoViews){
                userValue.setEnabled(true);
                userValue.setFocusable(true);
                userValue.setFocusableInTouchMode(true);
            }
        }else {
            mFab.setImageResource(R.drawable.ic_create_black_24dp);
            for(EditText userValue: mUserInfoViews){
                userValue.setEnabled(false);
                userValue.setFocusable(false);
                userValue.setFocusableInTouchMode(false);
                saveUserInfoValue();
            }
        }
    }

    private void loadUserInfoValue(){
        List<String> userData=mDataManager.getPreferencesManager().loadUserProfileData();
        for (int i = 0; i <userData.size() ; i++) {
            mUserInfoViews.get(i).setText(userData.get(i));
        }

    }

    private void saveUserInfoValue(){
        List<String> userData=new ArrayList<>();
        for (EditText userFieldView:mUserInfoViews) {
            userData.add(userFieldView.getText().toString());
        }
        mDataManager.getPreferencesManager().saveUserProfileData(userData);

    }
}
