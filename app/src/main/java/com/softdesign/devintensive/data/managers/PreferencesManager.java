package com.softdesign.devintensive.data.managers;


import android.content.SharedPreferences;
import android.net.Uri;

import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.DevIntensiveApplication;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PreferencesManager {
    private SharedPreferences mSharedPreferences;

    public static final String[] USER_FIELDS = {
            ConstantManager.USER_PHONE_KEY,
            ConstantManager.USER_MAIL_KEY,
            ConstantManager.USER_VK_KEY,
            ConstantManager.USER_GIT_KEY,
            ConstantManager.USER_BIO_KEY};

    public static final String[] USER_VALUES={
            ConstantManager.USER_RATING_VALUE,
            ConstantManager.USER_CODE_LINES_VALUE,
            ConstantManager.USER_PROJECT_VALUE
    };
    public static final String[] USER_DRAWER_HEADER_VALUES={
            ConstantManager.USER_FIRSTNAME_KEY,
            ConstantManager.USER_SECONDNAME_KEY,
            ConstantManager.USER_MAIL_KEY1,
    };

    public PreferencesManager() {
        this.mSharedPreferences = DevIntensiveApplication.getSharedPreferences();
    }

    public void saveUserProfileData(List<String> userFields){
        SharedPreferences.Editor editor=mSharedPreferences.edit();
        for (int i = 0; i <USER_FIELDS.length ; i++) {
            editor.putString(USER_FIELDS[i],userFields.get(i));
        }
        editor.apply();
    }
    public List<String> loadUserProfileData(){
        List<String> userFields=new ArrayList<>();
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_PHONE_KEY,"null"));
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_MAIL_KEY,"null"));
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_VK_KEY,"null"));
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_GIT_KEY,"null"));
        userFields.add(mSharedPreferences.getString(ConstantManager.USER_BIO_KEY,"null"));
        return userFields;

    }
    public void saveUserPhoto(Uri uri){
        SharedPreferences.Editor editor=mSharedPreferences.edit();
        editor.putString(ConstantManager.USER_PHOTO_KEY, uri.toString());
        editor.apply();
    }

    public void saveUserPhotoValue(String photoValue){
        SharedPreferences.Editor editor=mSharedPreferences.edit();
        editor.putString(ConstantManager.USER_PHOTO_KEY, photoValue);
        editor.apply();
    }

    public void saveUserAvatarValue(String avatarValue){
        SharedPreferences.Editor editor=mSharedPreferences.edit();
        editor.putString(ConstantManager.USER_AVATAR_VALUE,avatarValue);
        editor.apply();
    }

    public Uri loadUserAvatar(){
        return Uri.parse(mSharedPreferences.getString(ConstantManager.USER_AVATAR_VALUE,
                "android.resource://com.softdesign.devintensive/drawable/user_avatar"));
    }

    public Uri loadUserPhoto(){
        return Uri.parse(mSharedPreferences.getString(ConstantManager.USER_PHOTO_KEY,
                "android.resource://com.softdesign.devintensive/drawable/user_foto"));
    }

    public void saveUserProfileValues(int[] userValues){
        SharedPreferences.Editor editor=mSharedPreferences.edit();
        for (int i = 0; i <USER_VALUES.length ; i++) {
            editor.putString(USER_VALUES[i], String.valueOf(userValues[i]));
        }
        editor.apply();

    }
    public void saveUserFieldValues(String[] userFieldValues){
        SharedPreferences.Editor editor=mSharedPreferences.edit();
        for (int i = 0; i <USER_FIELDS.length ; i++) {
            editor.putString(USER_FIELDS[i],userFieldValues[i]);
        }
        editor.apply();
    }

    public List<String> loadUserProfileValues(){
        List<String> userValues=new ArrayList<>();
        userValues.add(mSharedPreferences.getString(ConstantManager.USER_RATING_VALUE,"0"));
        userValues.add(mSharedPreferences.getString(ConstantManager.USER_CODE_LINES_VALUE,"0"));
        userValues.add(mSharedPreferences.getString(ConstantManager.USER_PROJECT_VALUE,"0"));
        return userValues ;
    }

    public void saveUserHeaderValue(String[] userFields){
        SharedPreferences.Editor editor=mSharedPreferences.edit();
        for (int i = 0; i <USER_DRAWER_HEADER_VALUES.length ; i++) {
            editor.putString(USER_DRAWER_HEADER_VALUES[i],userFields[i]);
        }
        editor.apply();
    }

    public List<String> loadUserHeaderValues() {
        List<String> userHeaderValues = new ArrayList<>();
        userHeaderValues.add(mSharedPreferences.getString(ConstantManager.USER_FIRSTNAME_KEY, "null"));
        userHeaderValues.add(mSharedPreferences.getString(ConstantManager.USER_SECONDNAME_KEY, "null"));
        userHeaderValues.add(mSharedPreferences.getString(ConstantManager.USER_MAIL_KEY1, "null"));
        return userHeaderValues;
    }
    public String getUserNumber(){
        return mSharedPreferences.getString(ConstantManager.USER_PHONE_KEY,"");
    }
    public String getUserEmail(){
        return mSharedPreferences.getString(ConstantManager.USER_MAIL_KEY,"");
    }
    public String getUserVk(){
        return mSharedPreferences.getString(ConstantManager.USER_VK_KEY,"");
    }
    public String getUserGithub(){
        return mSharedPreferences.getString(ConstantManager.USER_GIT_KEY,"");
    }
    public void saveAuthToken(String authToken){
        SharedPreferences.Editor editor=mSharedPreferences.edit();
        editor.putString(ConstantManager.AUTH_TOKEN_KEY,authToken);
        editor.apply();
    }
    public String getAuthToken(){
        return mSharedPreferences.getString(ConstantManager.AUTH_TOKEN_KEY,"null");
    }
    public void saveUserId(String userId){
        SharedPreferences.Editor editor=mSharedPreferences.edit();
        editor.putString(ConstantManager.USER_ID_KEY,userId);
        editor.apply();
    }
    public String getUserId(){
        return mSharedPreferences.getString(ConstantManager.USER_ID_KEY,"null");
    }
    public String getUserAvatar(){
        return  mSharedPreferences.getString(ConstantManager.USER_AVATAR_VALUE,"null");
    }


}
