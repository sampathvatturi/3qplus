package com.spgon.a3flowers.retrofit;


import com.google.gson.JsonObject;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface UserService {

    @POST(APIClient.APPEND_URL + "register.php")
    Call<JsonObject> createUser(@Body RequestBody requestBody);

    @POST(APIClient.APPEND_URL + "update_profile.php")
    Call<JsonObject> updateprofile(@Body RequestBody requestBody);

    @POST(APIClient.APPEND_URL + "login.php")
    Call<JsonObject> loginUser(@Body RequestBody requestBody);

    @POST(APIClient.APPEND_URL + "get_profile.php")
    Call<JsonObject> getProfile(@Body RequestBody requestBody);

    @POST(APIClient.APPEND_URL + "verify.php")
    Call<JsonObject> verifyOTP(@Body RequestBody requestBody);

    @POST(APIClient.APPEND_URL + "get_yt.php")
    Call<JsonObject> getTasks(@Body RequestBody requestBody);

    @POST(APIClient.APPEND_URL + "get_yt_tasks.php")
    Call<JsonObject> getQuiz(@Body RequestBody requestBody);

    @POST(APIClient.APPEND_URL + "day_report_submit.php") // Replace with your actual endpoint
    Call<JsonObject> sendVideoId(@Body RequestBody requestBody);

    @POST(APIClient.APPEND_URL + "breathe_activity_submit.php")
    Call<JsonObject> postBreathActivity(@Body RequestBody requestBody);


    @POST(APIClient.APPEND_URL + "subscription_callback.php")
    Call<JsonObject> postSubscription(@Body RequestBody requestBody);

    @GET(APIClient.APPEND_URL + "get_demolink.php")
    Call<JsonObject> getdemolink();

    @GET(APIClient.APPEND_URL + "get_products.php")
    Call<JsonObject> getproductlink();

    @POST(APIClient.APPEND_URL + "user_status.php")
    Call<JsonObject> userstatus(@Body RequestBody requestBody);



}
