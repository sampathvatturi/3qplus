package com.spgon.a3flowers.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.spgon.a3flowers.adapters.VideoListAdapter;
import com.spgon.a3flowers.R;
import com.spgon.a3flowers.database.AppDatabase;
import com.spgon.a3flowers.model.User;
import com.spgon.a3flowers.model.UserData;
import com.spgon.a3flowers.model.VideoItem;
import com.spgon.a3flowers.retrofit.APIClient;
import com.spgon.a3flowers.retrofit.GetResult;
import com.spgon.a3flowers.util.CommonFunctions;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class TasksActivity extends AppCompatActivity implements GetResult.MyListener {

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;

    private ProgressDialog progressDialog;

    private AppDatabase appDatabase;
    private Context mcontext;
    private String value;
    private UserData userData;
    @BindView(R.id.youtube_listview)
    ListView listView;

    @BindView(R.id.pay_view)
    LinearLayout pay_view;
    private User user;
    int currentDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
//        if (progressDialog != null && progressDialog.isShowing()) {
//            progressDialog.dismiss();
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#3572BE"));
        }
        ButterKnife.bind(this);
        appDatabase = CommonFunctions.getDBinstance(this);
        mcontext = getApplicationContext();
        value = CommonFunctions.getStringShared(getApplicationContext(), "currentUserPhone");
        userData = appDatabase.userDataDAO().getUser(value);
        user = appDatabase.userDAO().getUser(value);


    date();

    }
    void date(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("phone_number", user.getPhoneNumber());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

        Call<JsonObject> call = APIClient.getInterface().userstatus(requestBody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject userStatusData = response.body();
                    if (userStatusData != null && userStatusData.has("Result")) {
                        int subStatus = userStatusData.getAsJsonObject("Result").get("sub_status").getAsInt();
                        currentDay = userStatusData.getAsJsonObject("Result").get("current_day").getAsInt();
                        CommonFunctions.setStringShared(getApplicationContext(), "currentUserPhone", user.getPhoneNumber());
                        if (subStatus == 1) {
                            init();
                        } else {
                            listView.setVisibility(View.GONE);
//                            pay_view.setVisibility(View.VISIBLE);
                            Intent intent = new Intent(TasksActivity.this, TaskNotSubscribed.class);
                            try {
                                startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    // Handle the case when the API request is not successful
                    // You can display an error message or take appropriate action here
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                // Handle API call failure, such as network errors
            }
        });

    }
//    public void openLoadingDialog()
//    {
//        progressDialog = new ProgressDialog(this, R.style.MyAlertDialogStyle);
//        progressDialog.setMessage("Loading. Please wait...");
//        progressDialog.show();
//
//    }
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        // Dismiss the progress dialog if it's still showing
//        if (progressDialog != null && progressDialog.isShowing()) {
//            progressDialog.dismiss();
//        }
//        // Ensure to call setStopState in onDestroy
//    }
    void init() {
        bottomNavigationView.setSelectedItemId(R.id.bottom_tasks);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.bottom_home:
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(0, 0);
                    finishAffinity();
                    return true;
                case R.id.bottom_tasks:
                    // Handle tasks item selection
                    return true;
                case R.id.bottom_profile:
                    System.out.println("click happende");
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    overridePendingTransition(0, 0);
                    finishAffinity();
                    return true;
                default:
                    return false;
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.bottom_tasks);


        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        int subStatus = user.getSub_status();

        if(subStatus==1) {
            JSONObject jsonObject = new JSONObject();
            try {

                jsonObject.put("phone_number", user.getPhoneNumber());
                jsonObject.put("day", currentDay);

            } catch (Exception e) {
                e.printStackTrace();
            }
            RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
            Call<JsonObject> call = APIClient.getInterface().getTasks(bodyRequest);
            String url = call.request().url().toString();
            Log.d("Uday Debug", url);
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "1");
            System.out.println(getResult);
        }else {
//            listView.setVisibility(View.GONE);
//            pay_view.setVisibility(View.VISIBLE);
            //Toast.makeText(getApplicationContext(), "PhoneNumber not " +"registered", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void callback(JsonObject result, String callNo) throws JSONException {
        Log.d("Uday Debug", result.toString());
        ArrayList<VideoItem> videoItems = new ArrayList<VideoItem>();
        if (result.get("status").getAsBoolean() == true) {
            JsonArray videos = result.getAsJsonArray("Result");
            for (JsonElement v : videos) {
                JsonObject obj = v.getAsJsonObject();
                VideoItem item = new VideoItem(obj.get("id").getAsString(),
                        CommonFunctions.extractVideoId(obj.get("link").getAsString()),
                        obj.get("title").getAsString());
                videoItems.add(item);
                Log.d("Uday Debug link", obj.get("link").getAsString());
                String shorts = obj.get("link").getAsString();
                if (shorts.contains("https://youtube.com/shorts/")){
//                    openYouTubeShortVideo(shorts);
                }
            }
            VideoListAdapter adapter = new VideoListAdapter(getApplicationContext(),
                    videoItems);
            listView.setAdapter(adapter);

        } else {
//            listView.setVisibility(View.GONE);
//            pay_view.setVisibility(View.VISIBLE);
//            Toast.makeText(getApplicationContext(), "PhoneNumber not registered", Toast.LENGTH_SHORT).show();

        }
    }
    public void purchase_now(View view) {

        Intent intent = new Intent(this,Googlepaybill.class);
        startActivity(intent);

    }

    private void openYouTubeShortVideo(String videoId) {
        // Construct the URL for YouTube shorts video
        String shortVideoUrl = videoId;

        // Create an intent to view the YouTube short video
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(shortVideoUrl));

        // Set the package name to ensure the YouTube app is used
        intent.setPackage("com.google.android.youtube");

        // Check if the YouTube app is installed, if not, open in a web browser
        if (intent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
            // Add FLAG_ACTIVITY_NEW_TASK flag
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intent);
        } else {
            // If the YouTube app is not installed, open in a web browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(shortVideoUrl));
            // Add FLAG_ACTIVITY_NEW_TASK flag
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intent);
        }
    }

}