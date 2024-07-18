package com.spgon.a3flowers.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.JsonObject;
import com.spgon.a3flowers.R;
import com.spgon.a3flowers.database.AppDatabase;
import com.spgon.a3flowers.model.User;
import com.spgon.a3flowers.model.UserData;
import com.spgon.a3flowers.retrofit.APIClient;
import com.spgon.a3flowers.util.CommonFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import kotlinx.coroutines.Delay;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskNotSubscribed extends AppCompatActivity {

    private static final String TAG = "TaskNotSubscribed";
    private static final String PREFS_NAME = "AppPrefs";
    private static final String KEY_IS_REFRESHING = "isRefreshing";

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;

    @BindView(R.id.profile_dropdown)
    Spinner profile_dropdown;

    private AppDatabase appDatabase;
    private User user;
    public static Context mcontext;
    private String current_phoneNumber;
    private SwipeRefreshLayout swipeRefreshLayout;

    private Handler refreshHandler = new Handler();
    private Runnable refreshRunnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_not_subscribed);
        ButterKnife.bind(this);


        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);
                    }
                },0000);
            }
        });

        appDatabase = CommonFunctions.getDBinstance(this);
        mcontext = getApplicationContext();
        current_phoneNumber = CommonFunctions.getStringShared(getApplicationContext(), "currentUserPhone");
        user = appDatabase.userDAO().getUser(current_phoneNumber);

        List<User> options = appDatabase.userDAO().getAllUsers();
        // Create a dummy User object for the "Add New User" option
        User addNewUserOption = new User("+  Add New User", "");
        options.add(addNewUserOption);


        // Create an ArrayAdapter to populate the Spinner
        ArrayAdapter<User> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        profile_dropdown = findViewById(R.id.profile_dropdown);
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

                if (selectedOption.equals(addNewUserOption)) {
                    Intent intent = new Intent(TaskNotSubscribed.this,
                            LoginActivity.class);
                    startActivity(intent);
                    finishAffinity();

                    return;
                }

                if (!TaskNotSubscribed.this.user.getPhoneNumber().equals(selectedOption.getPhoneNumber())) {
                    TaskNotSubscribed.this.user = selectedOption;
                    CommonFunctions.setStringShared(getApplicationContext(), "currentUserPhone", selectedOption.getPhoneNumber());
                    ProgressDialog progressDialog = new ProgressDialog(TaskNotSubscribed.this, R.style.MyAlertDialogStyle);
                    progressDialog.setMessage("Loading. Please wait...");
                    progressDialog.show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                    }, 2000); // 2000 milliseconds = 2 seconds

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Handle when no option is selected
            }
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottom_home:
                        Log.d(TAG, "Home selected");
                        // Handle home item selection
                        return true;
                    case R.id.bottom_tasks:
                        Log.d(TAG, "Tasks selected");
//                        if (user.getSub_status() != 1) return true;
//                        // Handle tasks item selection
//                        Intent tasksIntent = new Intent(getApplicationContext(), TasksActivity.class);
//                        startActivity(tasksIntent);
//                        overridePendingTransition(0, 0);
//                        finishAffinity();
                        return true;
                    case R.id.bottom_profile:
                        Log.d(TAG, "Profile selected");
                        // Handle profile item selection
                        Intent profileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                        startActivity(profileIntent);
                        overridePendingTransition(0, 0);
                        finishAffinity();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }


}
