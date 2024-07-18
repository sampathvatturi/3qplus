package com.spgon.a3flowers.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.spgon.a3flowers.model.User;


public class SessionManager {

    private final SharedPreferences mPrefs;
    private SharedPreferences oldmPrefs;
    SharedPreferences.Editor mEditor;
    public static String intro = "intro";
    public static String login = "login";
    public static String isopen = "isopen";
    public static String user = "users";
    public static String dcharge = "dcharge";
    public static String address = "address";
    public static String wallet = "wallet";
    public static String istip = "tip";
    public static String tips = "tips";
    public static String istax = "tax";
    public static String taxs = "taxs";
    public static String walletname = "walletname";


    public static String currency = "currency";
    public static String pincode = "pincode";
    public static String pincoded = "pincoded";
    public static String coupon = "coupon";
    public static String couponid = "couponid";
    public static String storename = "storename";
    public static String restid = "restid";


    public static String terms = "terms";
    public static String contact = "contact";
    public static String about = "about";
    public static String policy = "policy";
    public static String languages = "language";

    public SessionManager(Context context) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        mEditor = mPrefs.edit();
    }



    public void setStringData(String key, String val) {
        mEditor.putString(key, val);
        mEditor.commit();
    }

    public String getStringData(String key) {
        return mPrefs.getString(key, "");
    }

    public String getStringDatal(String key) {
        return mPrefs.getString(key, "en");
    }

    public void setFloatData(String key, float val) {
        mEditor.putFloat(key, val);
        mEditor.commit();
    }

    public float getFloatData(String key) {
        return mPrefs.getFloat(key, 0);
    }

    public void setBooleanData(String key, Boolean val) {
        mEditor.putBoolean(key, val);
        mEditor.commit();
    }

    public boolean getBooleanData(String key) {
        return mPrefs.getBoolean(key, false);
    }

    public void setIntData(String key, int val) {
        mEditor.putInt(key, val);
        mEditor.commit();
    }

    public int getIntData(String key) {
        return mPrefs.getInt(key, 0);
    }

    //{"UserLogin":{"id":"770","name":"uday","mobile":"9966444468","password":"9966444468","rdate":"2023-02-09 15:57:04","status":"1","ccode":"+91","code":"0","refercode":null,"wallet":"0","is_verify":"0"},"AddressExist":false,"ResponseCode":"200","Result":"true","ResponseMsg":"Login successfully!"}
    public void setUserDetails(String key, User val) {
        mEditor.putString(user, new Gson().toJson(val));
        mEditor.commit();
    }

    public User getUserDetails(String key) {
        return new Gson().fromJson(mPrefs.getString(user, ""), User.class);
    }


    public void logoutUser() {
        mEditor.clear();
        mEditor.commit();
    }
}
