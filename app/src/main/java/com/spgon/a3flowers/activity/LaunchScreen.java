package com.spgon.a3flowers.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.spgon.a3flowers.R;
import com.spgon.a3flowers.database.AppDatabase;
import com.spgon.a3flowers.model.User;
import com.spgon.a3flowers.model.UserData;
import com.spgon.a3flowers.util.CommonFunctions;

import java.util.List;

public class LaunchScreen extends AppCompatActivity {

    private AppDatabase appDatabase;
    private List<User> users;
    UserData userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#DACAB6FA"));
        }
        setContentView(R.layout.activity_launch_screen);

        appDatabase = CommonFunctions.getDBinstance(getApplicationContext());
        users = appDatabase.userDAO().getAllUsers();
        if (users.size() != 0) {

            Intent intent = new Intent(this, TasksActivity.class);

            startActivity(intent);
            finish();
        }
    }

    public void loginClick(View view) {
        // Handle the click event here

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    public void SignupClick(View view) {

        Intent intent = new Intent(this, Registration.class);
        startActivity(intent);
        finish();
    }

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