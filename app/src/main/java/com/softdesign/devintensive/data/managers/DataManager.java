package com.softdesign.devintensive.data.managers;

import android.content.Context;
import android.util.Log;

import com.softdesign.devintensive.data.network.PicassoCache;
import com.softdesign.devintensive.data.network.RestService;
import com.softdesign.devintensive.data.network.ServiceGenerator;
import com.softdesign.devintensive.data.network.req.UserLoginReq;
import com.softdesign.devintensive.data.network.res.UserListRes;
import com.softdesign.devintensive.data.network.res.UserModelRes;
import com.softdesign.devintensive.data.storage.models.DaoSession;
import com.softdesign.devintensive.data.storage.models.User;
import com.softdesign.devintensive.data.storage.models.UserDao;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.DevIntensiveApplication;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;

public class DataManager {
    private static final String TAG = ConstantManager.TAG_PREFIX + "Data Manager";
    private static DataManager INSTANCE = null;
    private Picasso mPicasso;

    private Context mContext;
    private PreferencesManager mPreferencesManager;
    private RestService mRestService;

    private DaoSession mDaoSession;

    public DataManager() {
        this.mPreferencesManager = new PreferencesManager();
        this.mContext = DevIntensiveApplication.getContext();
        this.mRestService = ServiceGenerator.createService(RestService.class);
        this.mPicasso = new PicassoCache(mContext).getPicassoInstance();
        this.mDaoSession = DevIntensiveApplication.getDaoSession();
    }

    public static DataManager getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new DataManager();
        }
        return INSTANCE;
    }

    public Picasso getPicasso() {
        return mPicasso;
    }

    public PreferencesManager getPreferencesManager() {
        return mPreferencesManager;
    }

    //Region---------------Network
    @POST
    public Call<UserModelRes> loginUser(UserLoginReq userLoginReq) {
        return mRestService.loginUser(userLoginReq);
    }
  /*  @POST
    public Call<UploadPhotoRes>uploadPhoto (String userId, RequestBody photoFile){
        return mRestService.uploadPhoto(userId,photoFile);
    }*/


    @GET
    public Call<UserListRes> getUserListFromNetwork() {
        return mRestService.getUserList();
    }

    //EndRegion

    //Region---------------DATA
    public List<User> getUserListFromDb() {
        List<User> userList = new ArrayList<>();
        try {
            userList = mDaoSession.queryBuilder(User.class)
                    .where(UserDao.Properties.CodeLines.gt(0))
                    .orderDesc(UserDao.Properties.CodeLines)
                    .build()
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userList;
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }
    //End Data

    public List<User> getUserListByName(String query) {
        List<User> userList = new ArrayList<>();
        try {

            userList = mDaoSession.queryBuilder(User.class)
                    .where(UserDao.Properties.Rating.gt(0), UserDao.Properties.SearchName
                            .like("%" + query.toUpperCase() + "%"))
                    .orderDesc(UserDao.Properties.CodeLines)
                    .build()
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userList;
    }
}
