package com.spgon.a3flowers.retrofit;

import android.util.Log;
import android.widget.Toast;

import com.spgon.a3flowers.MyApplication;
import com.spgon.a3flowers.util.Utility;
import com.google.gson.JsonObject;

import org.json.JSONException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GetResult {
    public static MyListener myListener;

    public void callForLogin(Call<JsonObject> call, String callno) {
        if(!Utility.internetChack()){
            Toast.makeText(MyApplication.mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show();
        }else {
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e("message", " : " + response.message());
                    Log.e("body", " : " + response.body());
                    Log.e("callno", " : " + callno);
                    try {
                        myListener.callback(response.body(), callno);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    try {
                        myListener.callback(null, callno);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    call.cancel();
                    t.printStackTrace();
                }
            });
        }
    }
    public interface MyListener {
        // you can define any parameter as per your requirement
        public void callback(JsonObject result, String callNo) throws JSONException;
    }
    public void setMyListener(MyListener myListener) {
        GetResult.myListener = myListener;
    }
}
