package com.softdesign.devintensive.ui.activities;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.utils.CircleTransform;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.NetworkStatusChecker;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = ConstantManager.TAG_PREFIX + "Main Activity";
    private DataManager mDataManager;
    private int mCurrentEditMode = 0;
    private ImageView mCallimg;
    private ImageView mEmailimg;
    private ImageView mShowVkimg;
    private ImageView mShowGithubimg;
    private TextInputLayout mInputLayoutMobileNumber;
    private CoordinatorLayout mCoordinatorLayout;
    private RelativeLayout mProfilePlaceholder;
    private Toolbar mToolbar;
    private DrawerLayout mNavigationDrawer;
    private FloatingActionButton mFab;
    private EditText mUserPhone, mUserEmail, mUserVk, mUserGit, mUserBio;
    private List<EditText> mUserInfoViews;
    private TextView mUserValueRating, mUserValueCodeLines, mUserValueProject,
            mUserFirstName, mUserSecondName, mUserFullname, mUserHeaderEmail;
    private List<TextView> mUserValueViews;
    private List<TextView> mUserHeaderValue;

    private CollapsingToolbarLayout mCollapsingToolbar;
    private AppBarLayout.LayoutParams mAppBarParams = null;
    private AppBarLayout mAppBarLayout;
    private File mPhotoFile = null;

    private Uri mSelectedImage = null;
    private ImageView mProfileImage;


    private View mHeaderLayout;
    private ImageView mUserAvatar;
    private NavigationView mNavigationView;


    /**
     * +     * Метод вызывается при создании активити после изменения конфигурации/возврата к текушей
     * +     * активити после его уничтожения.
     * +     *
     * +     * в данном методе инициализируются/производится:
     * +     * - UI пользовательский интерфейс (статика)
     * +     * - инициализация статических данных активити
     * +     * - связь данных со списками (инициализация адаптеров)
     * +     *
     * +     * Не запускать длительные операции по работе с данными в onCreate() !!!
     * +     *
     * +     * @param savedInstanceState - объект со значениями сохраненными в Bundle - состояние UI
     * +
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "OnCreate");
        init();
        setupToolBar();
        setupDrawer();
        initUserFields();
        initUserHeaderValue();

        if (NetworkStatusChecker.isNetworkAvaliable(this)) {
            initUserFieldsValue();
        } else {

        }
        initUserInfoValue();

        Picasso.with(this)
                .load(mDataManager.getPreferencesManager().loadUserPhoto())
                .placeholder(R.drawable.user_foto)
                .fit()
                .centerCrop()
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(mProfileImage);

        Picasso.with(this)
                .load(mDataManager.getPreferencesManager().loadUserAvatar())
                .transform(new CircleTransform())
                .placeholder(R.drawable.user_avatar)
                .fit()
                .centerCrop()
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(mUserAvatar);

        if (savedInstanceState == null) {
            //активность запускается в первый  раз
        } else {
            mCurrentEditMode = savedInstanceState.getInt(ConstantManager.EDIT_MORE_KEY, 0);
            changeEditMode(mCurrentEditMode);
        }
    }

    /*Initializator*/

    /**
     * For initalize
     */
    private void init() {
        mCallimg = (ImageView) findViewById(R.id.call_img);
        mCallimg.setOnClickListener(this);
        mEmailimg = (ImageView) findViewById(R.id.send_email_icon);
        mEmailimg.setOnClickListener(this);
        mShowVkimg = (ImageView) findViewById(R.id.show_vk);
        mShowVkimg.setOnClickListener(this);
        mShowGithubimg = (ImageView) findViewById(R.id.show_github);
        mShowGithubimg.setOnClickListener(this);
        mInputLayoutMobileNumber = (TextInputLayout) findViewById(R.id.input_layout_mobile_number);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_coordinator_container);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mDataManager = DataManager.getINSTANCE();
        mNavigationDrawer = (DrawerLayout) findViewById(R.id.navigation_drawer);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(this);
        mUserPhone = (EditText) findViewById(R.id.mobile_number);
        mUserPhone.setOnClickListener(this);
        mUserEmail = (EditText) findViewById(R.id.email);
        mUserVk = (EditText) findViewById(R.id.vk_profile);
        mUserGit = (EditText) findViewById(R.id.github_reposit);
        mUserBio = (EditText) findViewById(R.id.bio);
        mProfilePlaceholder = (RelativeLayout) findViewById(R.id.profile_placeholder);
        mProfilePlaceholder.setOnClickListener(this);
        mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
        mProfileImage = (ImageView) findViewById(R.id.user_foto_img);
        mUserInfoViews = new ArrayList<>();
        mUserInfoViews.add(mUserPhone);
        mUserInfoViews.add(mUserEmail);
        mUserInfoViews.add(mUserVk);
        mUserInfoViews.add(mUserGit);
        mUserInfoViews.add(mUserBio);
        mUserValueRating = (TextView) findViewById(R.id.count_rate);
        mUserValueCodeLines = (TextView) findViewById(R.id.count_code_strings);
        mUserValueProject = (TextView) findViewById(R.id.count_projects);
        mUserHeaderValue = new ArrayList<>();
        mUserHeaderValue.add(mUserFirstName);
        mUserHeaderValue.add(mUserSecondName);
        mUserHeaderValue.add(mUserHeaderEmail);
        mUserValueViews = new ArrayList<>();
        mUserValueViews.add(mUserValueRating);
        mUserValueViews.add(mUserValueCodeLines);
        mUserValueViews.add(mUserValueProject);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mHeaderLayout = (View) mNavigationView.getHeaderView(0);
        mUserFullname = (TextView) mHeaderLayout.findViewById(R.id.user_name_txt);
        mUserHeaderEmail = (TextView) mHeaderLayout.findViewById(R.id.user_email_txt);
        mUserAvatar = (ImageView) mHeaderLayout.findViewById(R.id.user_avatar);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.navigation_drawer);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ConstantManager.EDIT_MORE_KEY, mCurrentEditMode);
    }

    /*Метод открывает меню по клику на иконку в левом углу */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mNavigationDrawer.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * +     * Метод вызывается при старте активити перед моментом того как UI станет достепен пользователю
     * +     * как правило в данном методе происходит регистрация подписки на событиея остановка которых была
     * +     * произведена в onStop()
     * +
     */
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

    /**
     * take Click
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.call_img:
                Log.d(TAG, "onClick call");
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE)
                        == PackageManager.PERMISSION_GRANTED) {
                    // TODO: 07.07.2016 поставить проверку на заполненость данных
                    Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
                            + DataManager.getINSTANCE().getPreferencesManager().getUserNumber()));
                    startActivity(dialIntent);
                }
                /*showProgress();
                runWithDelay();*/
                break;

            case R.id.send_email_icon:
                Log.d(TAG, "onClick icon email");
                // TODO: 07.07.2016 поставить проверку на заполненость данных
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]
                        {DataManager.getINSTANCE().getPreferencesManager().getUserEmail()});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(emailIntent, "Send"));

                break;
            case R.id.show_vk:
                Log.d(TAG, "onClick show VK");
                // TODO: 07.07.2016 поставить проверку на заполненость данных
                Intent vkIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www."
                        + DataManager.getINSTANCE().getPreferencesManager().getUserVk()));
                startActivity(vkIntent);
                break;

            case R.id.fab:
                if (mCurrentEditMode == 0) {
                    changeEditMode(1);
                    mCurrentEditMode = 1;

                    showSnackBar("Включен режим реактирования");
                } else {
                    changeEditMode(0);
                    mCurrentEditMode = 0;
                }
                break;
            case R.id.show_github:
                Log.d(TAG, "onClick show GITHUB");
                // TODO: 07.07.2016 поставить проверку на заполненость данных
                Intent githubIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www."
                        + DataManager.getINSTANCE().getPreferencesManager().getUserGithub()));
                startActivity(githubIntent);
                break;
            case R.id.profile_placeholder:
                showDialog(ConstantManager.LOAD_PROFILE_PHOTO);
                break;
            case R.id.mobile_number:
                Log.d(TAG, "case R.id.mobile_number");

                break;

        }

    }

    private void runWithDelay() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideProgress();
            }
        }, 3000);
    }

    private void showSnackBar(String message) {
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    /*Подменяем тулбар и ставим кнопку на открытие меню*/
    private void setupToolBar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        mAppBarParams = (AppBarLayout.LayoutParams) mCollapsingToolbar.getLayoutParams();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    /*Side Draver setup*/
    private void setupDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        final  Intent usersList=new Intent(this,UserListActivity.class);

        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener
                    (new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(MenuItem item) {
                            item.setChecked(true);

                            switch (item.getItemId()) {
                                case R.id.user_profile_menu:
                                    showSnackBar(item.getTitle().toString());
                                    onBackPressed();
                                    break;
                                case R.id.user_team_menu:
                                    showSnackBar(item.getTitle().toString());
                                    startActivity(usersList);
                                    break;
                            }
                            mNavigationDrawer.closeDrawer(GravityCompat.START);
                            return false;
                        }

                    });
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ConstantManager.REQUEST_GALLERY_PICTURE:
                if (resultCode == RESULT_OK && data != null) {
                    mSelectedImage = data.getData();
                    insertProfileImage(mSelectedImage);
                }
                break;
            case ConstantManager.REQUEST_CAMERA_PICTURE:
                if (resultCode == RESULT_OK && mPhotoFile != null) {
                    mSelectedImage = Uri.fromFile(mPhotoFile);
                    insertProfileImage(mSelectedImage);
                }
                break;
        }
    }


    /**
     * переключает режим редактирования
     *
     * @param mode если 1 режим то режим редактирования, если 0 то режим чтения
     */
    private void changeEditMode(int mode) {
        if (mode == 1) {
            mFab.setImageResource(R.drawable.ic_done_black_24dp);
            for (EditText userValue : mUserInfoViews) {
                userValue.setEnabled(true);
                userValue.setFocusable(true);
                userValue.setFocusableInTouchMode(true);
                mUserPhone.requestFocus();
                showProfilePlaceholder();
                lockToolbar();
                mCollapsingToolbar.setExpandedTitleColor(Color.TRANSPARENT);
            }
        } else {
            mFab.setImageResource(R.drawable.ic_create_black_24dp);
            for (EditText userValue : mUserInfoViews) {
                userValue.setEnabled(false);
                userValue.setFocusable(false);
                userValue.setFocusableInTouchMode(false);
                saveUserFields();
                hideProfilePlaceholder();
                unlockToolbar();
                mCollapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.color_white));
            }
        }
    }

    /**
     * Загрузка ранее введенных данных пользователя
     */
    private void initUserFields() {
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileData();
        for (int i = 0; i < userData.size(); i++) {
            mUserInfoViews.get(i).setText(userData.get(i));
        }

    }

    /**
     * Сохранение введенных данных пользователем и фото
     */
    private void saveUserFields() {
        List<String> userData = new ArrayList<>();
        for (EditText userFieldView : mUserInfoViews) {
            userData.add(userFieldView.getText().toString());
        }
        mDataManager.getPreferencesManager().saveUserProfileData(userData);

    }

    //загружает данные с сайта в поля
    private void initUserInfoValue() {
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileValues();
        for (int i = 0; i < userData.size(); i++) {
            mUserValueViews.get(i).setText(userData.get(i));
        }
    }

    private void initUserFieldsValue() {
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileData();
        for (int i = 0; i < userData.size(); i++) {
            mUserInfoViews.get(i).setText(userData.get(i));
        }

    }

    private void initUserHeaderValue() {
        List<String> userData = mDataManager.getPreferencesManager().loadUserHeaderValues();
        String fName = userData.get(0);
        String sName = userData.get(1);
        String fullName = fName + " " + sName;
        mUserFullname.setText(fullName);
        mUserHeaderEmail.setText(userData.get(2));
    }


    /**
     * load фотографии from gallery
     */
    private void loadPhotoFromGallery() {
        Intent takeGallaryIntent =
                new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        takeGallaryIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(takeGallaryIntent, getString(R.string.user_profile_choose_message)),
                ConstantManager.REQUEST_GALLERY_PICTURE);

    }

    /**
     * load from camera
     */
    private void loadPhotoFromCamera() {
        Log.d(TAG, "loadPhotoFromCamera");
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission
                (this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Intent takeCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                mPhotoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (mPhotoFile != null) {
                takeCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
                startActivityForResult(takeCaptureIntent, ConstantManager.REQUEST_CAMERA_PICTURE);
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                            android.Manifest.permission.CAMERA,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    ConstantManager.CAMERA_REQUEST_PERMISSION_CODE);
        }
        Snackbar.make(mCoordinatorLayout, "Для корректной работы приложения," +
                " необходимо дать требуемые разрешения", Snackbar.LENGTH_LONG)
                .setAction("Разрешить", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openApplicationSettings();
                    }
                }).show();
    }

    @Override
    public void onRequestPermissionsResult
            (int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ConstantManager.CAMERA_REQUEST_PERMISSION_CODE
                && grantResults.length == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // TODO: make request logic call working with camera
                loadPhotoFromCamera();
            }
            if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // TODO: make request logic call save file
            }
        }
    }

    private void hideProfilePlaceholder() {
        mProfilePlaceholder.setVisibility(View.GONE);

    }

    private void showProfilePlaceholder() {
        mProfilePlaceholder.setVisibility(View.VISIBLE);

    }

    /*Lock toolbar for collaps*/
    private void lockToolbar() {
        mAppBarLayout.setExpanded(true, true);
        mAppBarParams.setScrollFlags(0);
        mCollapsingToolbar.setLayoutParams(mAppBarParams);
    }

    /*UnLock toolbar for collaps*/
    private void unlockToolbar() {
        mAppBarParams.setScrollFlags
                (AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL |
                        AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
        mCollapsingToolbar.setLayoutParams(mAppBarParams);

    }


    /*Создаем диалог
    * @param id ConstantManager.*/
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case ConstantManager.LOAD_PROFILE_MANAGER:
                String[] selectItems = {getString(R.string.user_profile_dialog_gallery),
                        getString(R.string.user_profile_dialog_camera),
                        getString(R.string.user_profile_dialog_cancel)};
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.user_profile_dialog_title));
                builder.setItems(selectItems, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int choiceItem) {
                        switch (choiceItem) {
                            case 0:
                                loadPhotoFromGallery();
                                //showSnackBar("Загрузить из галлереи");
                                break;
                            case 1:
                                loadPhotoFromCamera();
                                //showSnackBar("сделать снимок");
                                break;
                            case 2:
                                dialogInterface.cancel();
                                showSnackBar("Отмена");
                                break;
                        }
                    }
                });
                return builder.create();
            default:
                return null;
        }

    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
        values.put(MediaStore.MediaColumns.DATA, image.getAbsolutePath());

        this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        return image;

    }

    private void insertProfileImage(Uri selectedImage) {
        Picasso.with(this)
                .load(selectedImage)
                .into(mProfileImage);
        mDataManager.getPreferencesManager().saveUserPhoto(selectedImage);
    }


    public void openApplicationSettings() {
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(appSettingsIntent, ConstantManager.PERMISSION_REQUEST_SETTINGS_CODE);

    }


}




