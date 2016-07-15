package com.softdesign.devintensive.data.managers;

import android.content.Context;

import com.softdesign.devintensive.data.network.RestService;
import com.softdesign.devintensive.data.network.ServiceGenerator;
import com.softdesign.devintensive.data.network.req.UserLoginReq;
import com.softdesign.devintensive.data.network.res.UploadPhotoRes;
import com.softdesign.devintensive.data.network.res.UserListRes;
import com.softdesign.devintensive.data.network.res.UserModelRes;
import com.softdesign.devintensive.utils.DevIntensiveApplication;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;

public class DataManager {
    private static DataManager INSTANCE = null;

    private Context mContext;
    private PreferencesManager mPreferencesManager;
    private RestService mRestService;

    public DataManager() {
        this.mPreferencesManager = new PreferencesManager();
        this.mContext = DevIntensiveApplication.getContext();
        this.mRestService = ServiceGenerator.createService(RestService.class);
    }

    public static DataManager getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new DataManager();
        }
        return INSTANCE;
    }

    public PreferencesManager getPreferencesManager() {
        return mPreferencesManager;
    }

    //Region---------------Network
    @POST
    public Call<UserModelRes> loginUser( UserLoginReq userLoginReq) {
        return mRestService.loginUser(userLoginReq );
    }
   /* @POST
    public Call<UploadPhotoRes>*/
   @GET
   public Call<UserListRes> getUserList() {
       return mRestService.getUserList();
   }

    //EndRegion

    //Region---------------DATA


    //End Data
}
