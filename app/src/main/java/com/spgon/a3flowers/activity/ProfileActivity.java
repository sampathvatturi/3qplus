package com.spgon.a3flowers.activity;

import static android.app.PendingIntent.getActivity;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.spgon.a3flowers.R;
import com.spgon.a3flowers.database.AppDatabase;
import com.spgon.a3flowers.model.User;
import com.spgon.a3flowers.model.UserData;
import com.spgon.a3flowers.retrofit.APIClient;
import com.spgon.a3flowers.util.CommonFunctions;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.android.billingclient.api.BillingClient;

import org.json.JSONObject;

import java.io.IOException;
import java.security.AccessController;
import java.util.Date;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;

    //    @BindView(R.id.googlePayButton)
//    Button button;
    @BindView(R.id.profile_dropdown)
    Spinner profile_dropdown;

    private AppDatabase appDatabase;

    private UserData userData;

    User user;
    private ProgressDialog progressDialog;

    SessionManager sessionManager;
    private BillingClient billingClient;

    int value = 0;
    private String current_phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#DACAB6FA"));
        }
//        if (progressDialog != null && progressDialog.isShowing()) {
//            progressDialog.dismiss();
//        }
        ButterKnife.bind(this);

        appDatabase = CommonFunctions.getDBinstance(this);
        current_phoneNumber =
                CommonFunctions.getStringShared(getApplicationContext(),
                        "currentUserPhone");
        user = appDatabase.userDAO().getUser(current_phoneNumber);
        init();

    }

    //   public void profile(View view){
//        Intent intent = new Intent(this, ProfileEdit.class);
//        startActivity(intent);
//   }
    public void help(View view) {
        Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=+919391001437"); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void products(View view) {
        Intent intent = new Intent(this,Products.class);
        startActivity(intent);
    }

//    public void openLoadingDialog() {
//        progressDialog = new ProgressDialog(this, R.style.MyAlertDialogStyle);
//        progressDialog.setMessage("Loading. Please wait...");
//        progressDialog.show();
//    }

    public void privacy(View view) {
        Log.d("Profile activity", "Privacy method called");
        Uri uri = Uri.parse("https://3qualities.in/privacy-policy");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

    }
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        // Dismiss the progress dialog if it's still showing
//        if (progressDialog != null && progressDialog.isShowing()) {
//            progressDialog.dismiss();
//        }
//    }
    public void logout(View view) {

        appDatabase.userDAO().deleteUsers();
        clearUserSession();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    // Inside your activity
    private void clearUserSession() {
        // Clear authentication tokens and any other user-related data
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("auth_token"); // Remove the authentication token
        // Clear any other user-related data if needed
        // ...
        editor.apply();

        // Optional: Clear cookies if using WebView
        // CookieManager cookieManager = CookieManager.getInstance();
        // cookieManager.removeAllCookies(null);
        // cookieManager.flush();
    }

    public void googlePayButton(View view) {
        Intent intent = new Intent(this, Googlepaybill.class);
        startActivity(intent);

    }

    public void editprofile(View view) {
        Intent intent = new Intent(this, ProfileEdit.class);
        startActivity(intent);
    }

    void init() {

        bottomNavigationView.setSelectedItemId(R.id.bottom_profile);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottom_home:

                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        finishAffinity();
                        return true;
                    case R.id.bottom_tasks:

                        // Handle search item selection
                        Intent intent = new Intent(getApplicationContext(), TasksActivity.class);
                        startActivity(intent);
//                        startActivity(new Intent(getApplicationContext(), TasksActivity.class));
                        overridePendingTransition(0, 0);
                        finishAffinity();
                        return true;
                    case R.id.bottom_profile:

                        return true;
                    default:
                        return false;
                }
            }
        });

//        updatePaybutton();


        List<User> options = appDatabase.userDAO().getAllUsers();

        // Create an ArrayAdapter to populate the Spinner
        ArrayAdapter<User> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the adapter for the Spinner
        profile_dropdown.setAdapter(adapter);
        int position = options.indexOf(user);
        profile_dropdown.setSelection(position);

        // Handle item selection if needed
        profile_dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                // Handle the selected option
                User selectedOption = (User) adapterView.getItemAtPosition(position);


                CommonFunctions.setStringShared(getApplicationContext(),
                        "currentUserPhone", selectedOption.getPhoneNumber());

                user = appDatabase.userDAO().getUser(selectedOption.getPhoneNumber());
//                updatePaybutton();
                // Do something with selectedOption
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Handle when no option is selected
            }
        });
    }

//    private void updatePaybutton() {
//        if (user.getSub_date_in_dateformat() != null) {
//            Date added_year =
//                    CommonFunctions.add_years_to_date(user.getSub_date_in_dateformat(), 1);
//            Date currentDate = new Date();
//            if (added_year.before(currentDate)) {
//                button.setVisibility(View.VISIBLE);
//            } else {
//                button.setVisibility(View.GONE);
//            }
//        }
//    }


    @Override
    protected void onResume() {
        super.onResume();
        // Resume any operations that need to be active in the foreground
        // For example, location updates, media playback, animations, etc.
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Release resources or stop operations that are not needed while the activity is not visible
        // For example, stop location updates, release media player, pause animations, etc.
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Release resources or stop operations that are not needed while the activity is not visible
        // For example, stop location updates, release media player, pause animations, etc.
    }


}