package com.spgon.a3flowers.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.spgon.a3flowers.R;
import com.spgon.a3flowers.dao.UserDAO;
import com.spgon.a3flowers.database.AppDatabase;
import com.spgon.a3flowers.model.User;
import com.spgon.a3flowers.model.UserData;
import com.spgon.a3flowers.retrofit.APIClient;
import com.spgon.a3flowers.retrofit.GetResult;
import com.spgon.a3flowers.util.CommonFunctions;
import com.spgon.a3flowers.util.DecibelMeter;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements DecibelMeter.OnDecibelListener, GetResult.MyListener {

    private static final int RECORD_AUDIO_PERMISSION_CODE = 101;


    @BindView(R.id.breath_img)
    ImageView breath_img;

    @BindView(R.id.swipe_count)
    TextView swipeCountTextView;
    @BindView(R.id.breath_count)
    TextView breathCountTextView;

    @BindView(R.id.timerTextView)
    TextView timerTextView;
    @BindView(R.id.start_stop)
    TextView start_stop;
    @BindView(R.id.dayTextView)
    TextView dayTextView;


    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.matchView)
    LinearLayout matchLinearLayout;

    @BindView(R.id.overlays)
    LinearLayout overlaplayout;

    @BindView(R.id.matchPercentTextView)
    TextView matchPercentTextView;

    @BindView(R.id.topAppBar)
    MaterialToolbar menu;
    AppDatabase appDatabase;


    @BindView(R.id.circleTimer)
    ProgressBar progressBar;

    @BindView(R.id.profile_dropdown)
    Spinner profile_dropdown;
    private CountDownTimer countDownTimer;
    private DecibelMeter decibelMeter;
    public static Context mcontext;
    public static boolean isWorkoutRunning = false;
    private PowerManager.WakeLock wakeLock;
    private float startX, startY;
    int breath_min = 6;
    int breath_min_count = 0;
    private static long TIMER_DURATION_MS = 60 * 1000; // 1 minute
    private UserData userData;

    private static final long DEFAULT_TIMER_DURATION_MS = 60000; // Set your default timer duration here
    private boolean isTimerDurationSet = false;
    private User user;

    private Vibrator vibrator;

    @BindView(R.id.userChangeProgressBar)
    ProgressBar userChangeProgressBar;

    private Dialog overlayDialog;

    private MediaPlayer mediaPlayer;
    private Handler soundHandler = new Handler();
    private ProgressDialog progressDialog;

    private String value;


//    @BindView(R.id.usernameEditText)
//    TextView username;

    private UserDAO userDAO;
    int currentDay;
    int date;

    private void getUserStatus() {


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


                        user.setSub_status(subStatus);

                        UserData userData = new UserData();
                        userData.setPhoneNumber(user.getPhoneNumber());
                        userData.setDayNumber(currentDay);
                        appDatabase.userDataDAO().update(userData);

                        appDatabase.userDAO().insertAll(user);

                        if (subStatus == 1) {
                            continueAppInitialization();

                        } else {
//                            startActivity(new Intent(getApplicationContext(), TaskNotSubscribed.class));
                            initCommon();

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appDatabase = CommonFunctions.getDBinstance(this);
        mcontext = getApplicationContext();


        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        value = CommonFunctions.getStringShared(getApplicationContext(), "currentUserPhone");
        userData = appDatabase.userDataDAO().getUser(value);
        user = appDatabase.userDAO().getUser(value);
        if (user == null) {
            user = appDatabase.userDAO().getAllUsers().get(0);
            userData = appDatabase.userDataDAO().getUser(user.getPhoneNumber());
        }

        getUserStatus();
    }
//    public void openLoadingDialog() {
//        if (!isFinishing()) {
//            progressDialog = new ProgressDialog(this, R.style.MyAlertDialogStyle);
//            progressDialog.setMessage("Loading. Please wait...");
//            progressDialog.show();
//        }
//    }

    private void initCommon() {




        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottom_home:
                        // Handle home item selection
                        return true;
                    case R.id.bottom_tasks:

                        if (user.getSub_status() != 1)
                            return true;
                        // Handle search item selection
                        Intent intent = new Intent(getApplicationContext(), TasksActivity.class);
                        startActivity(intent);
//                        startActivity(new Intent(getApplicationContext(), TasksActivity.class));
                        overridePendingTransition(0, 0);
                        finishAffinity();
                        return true;
                    case R.id.bottom_profile:

//                        Intent intents = new Intent(getApplicationContext(),Googlepaybill.class);
//                        startActivity(intents);
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        overridePendingTransition(0, 0);
                        finishAffinity();
                        return true;
                    default:
                        return false;
                }
            }
        });

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
                    Intent intent = new Intent(MainActivity.this,
                            LoginActivity.class);
                    startActivity(intent);
                    finishAffinity();

                    return;
                }

                if (!MainActivity.this.user.getPhoneNumber().equals(selectedOption.getPhoneNumber())) {
                    MainActivity.this.user = selectedOption;
                    CommonFunctions.setStringShared(getApplicationContext(), "currentUserPhone", selectedOption.getPhoneNumber());
                    getUserStatus();
                    ProgressDialog progressDialog = new ProgressDialog(MainActivity.this, R.style.MyAlertDialogStyle);
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

//                // Show progress dialog
//                ProgressDialog progressDialog = new ProgressDialog(MainActivity.this, R.style.MyAlertDialogStyle);
//                progressDialog.setMessage("Loading. Please wait...");
//                progressDialog.show();
//
//                // Dismiss the progress dialog after 2 seconds
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        // Dismiss the progress dialog
//                        if (progressDialog != null && progressDialog.isShowing()) {
//                            progressDialog.dismiss();
//                        }
//
//                        // Load user data in a background thread or AsyncTask
//                        new AsyncTask<Void, Void, User>() {
//                            @Override
//                            protected User doInBackground(Void... voids) {
//                                // Load user data or perform necessary tasks here
//                                return appDatabase.userDAO().getUser(selectedOption.getPhoneNumber());
//                            }
//
//                            @Override
//                            protected void onPostExecute(User user) {
//                                // Do something with the loaded user data
//                                MainActivity.this.user = user;
//                                // Update UI or perform any other necessary tasks
//                            }
//                        }.execute();
//                    }
//                }, 2000); // 2000 milliseconds = 2 seconds
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Handle when no option is selected
            }
        });


    }


    private void continueAppInitialization() {

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initCommon();
//        if (progressDialog != null && progressDialog.isShowing()) {
//            progressDialog.dismiss();
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#DACAB6FA"));
        }


        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String usernameValue = sharedPreferences.getString("username", null);
        if (!isTimerDurationSet) {
            long timerDuration = sharedPreferences.getLong("timer_duration", DEFAULT_TIMER_DURATION_MS);

            date = currentDay;
            if (date == 1) {
                TIMER_DURATION_MS = date * timerDuration;
            } else {
                TIMER_DURATION_MS = date * timerDuration / 2 + 30000;
            }

            isTimerDurationSet = true; // Mark the timer duration as set
        }


        init();

        // Check if the permission is already granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            // You have the permission, do your audio-related tasks
        } else {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO_PERMISSION_CODE);
        }
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLockTag");
        }
        decibelMeter = new DecibelMeter();
        decibelMeter.setDecibelListener(this);

    }


    public void demo(View view) {
        // Make the API call
        Call<JsonObject> call = APIClient.getInterface().getdemolink();

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject responseData = response.body();

                    if (responseData != null && responseData.has("Result")) {
                        // Extract the "link" from the API response
                        String link = responseData.getAsJsonArray("Result").get(0).getAsJsonObject().get("link").getAsString();

                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));

                        // Set the package to the YouTube app
                        intent.setPackage("com.google.android.youtube");

                        // Check if the YouTube app is available
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            // The YouTube app is available, so open the video in it
                            startActivity(intent);
                        } else {
                            // If the YouTube app is not available, you can open the link in a web browser
                            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                            startActivity(intent);
                        }
                        // Now, 'link' contains the URL you extracted from the API response
                        Log.d("Sai Debug", "Link from API: " + link);
                    }
                } else {
                    // Handle the API request not being successful
                    Log.e("Sai Debug", "API request failed with response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                // Handle any exceptions or failures
            }
        });
    }


    public void profile_click(View view) {
        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        overridePendingTransition(0, 0);
        finishAffinity();


    }


    void init() {


        int seconds = (int) (TIMER_DURATION_MS / 1000);
//        int sec = 60;
        timerTextView.setText(String.format("%02d:%02d", seconds / 60, seconds % 60));

//        dayTextView.setText("Day " + userData.getDayNumber());
        dayTextView.setText("Day " + currentDay);


        breath_img.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                        return true;

                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        float endX = event.getX();
                        float endY = event.getY();
                        if (isSwipeGesture(startX, startY, endX, endY)) {
                            handleSwipeGesture(startX, startY, endX, endY);
                            return true; // Consume the touch event
                        }
                        break;
                }
                return false;
            }
        });

        countDownTimer = new CountDownTimer(TIMER_DURATION_MS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Update the timer display and progress bar on each tick
                long seconds = millisUntilFinished / 1000;
                timerTextView.setText(String.format("%02d:%02d", seconds / 60, seconds % 60));

                int progress = (int) ((TIMER_DURATION_MS - millisUntilFinished) * 1000 * 100 / (TIMER_DURATION_MS * 1000));
                Log.d("uday progress", "Current Progress: " + progress);
                progressBar.setProgress(progress);
            }


            @Override
            public void onFinish() {
                progressBar.setProgress(100);
                setStopState();
                vibrateFor5Seconds();

            }
        };


    }

    private void vibrateFor5Seconds() {
        if (vibrator.hasVibrator()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                // Create a strong vibration effect for 5 seconds
                VibrationEffect effect = VibrationEffect.createOneShot(5000, VibrationEffect.DEFAULT_AMPLITUDE);
                vibrator.vibrate(effect);
            } else {
                // For devices older than Android Oreo (API 26)
                // Use a deprecated method to create a strong vibration for 5 seconds
                long[] pattern = {0, 5000}; // Vibrate for 5 seconds
                vibrator.vibrate(pattern, -1);
            }
        }
    }

    private boolean isSwipeGesture(float startX, float startY, float endX, float endY) {
        float dx = endX - startX;
        float dy = endY - startY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        float threshold = getResources().getDisplayMetrics().density * 100; // Set your own threshold here
        return distance > threshold;
    }

    // Handle the permission request response
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RECORD_AUDIO_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted, do your audio-related tasks
            } else {
                // Permission is denied, handle accordingly (e.g., show a message or disable audio-related features)
            }
        }
    }

    public void start_stop(View view) {

        // Handle the click event here

        if (start_stop.getText().equals(getString(R.string.btn_start_txt))) {
            setStartState();
        } else if (start_stop.getText().equals(getString(R.string.btn_stop_txt))) {
            setStopState();

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Dismiss the progress dialog if it's still showing
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        // Ensure to call setStopState in onDestroy
        try {
            setStopState();
        } catch (Exception e) {

        }
    }

    void setStartState() {
        overlaplayout.setVisibility(View.VISIBLE);
        matchLinearLayout.setVisibility(View.GONE);
        decibelMeter.start();
        start_stop.setText(R.string.btn_stop_txt);
        countDownTimer.start();
        isWorkoutRunning = true;
        wakeLock.acquire();
        swipeCountTextView.setText(0 + "");
        breathCountTextView.setText(0 + "");
        breathCountTextView.setVisibility(View.GONE);

    }

    void setStopState() {
        overlaplayout.setVisibility(View.GONE);
        matchLinearLayout.setVisibility(View.VISIBLE);
        decibelMeter.stop();
        countDownTimer.cancel();
        isWorkoutRunning = false;
        start_stop.setText(R.string.btn_start_txt);
        breathCountTextView.setVisibility(View.VISIBLE);
        // Release the WakeLock
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }


        double breathcount = Double.parseDouble((String) breathCountTextView.getText());
        double swipecount = Double.parseDouble((String) swipeCountTextView.getText());

        double matchPercent = 0;
        if (breathcount != 0 && swipecount != 0) {
            if (breathcount > swipecount) {
                matchPercent = swipecount / breathcount * 100;
            } else {
                matchPercent = breathcount / swipecount * 100;
            }
        } else {
            matchPercent = 0;
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String formattedValue = decimalFormat.format(matchPercent);
        matchPercentTextView.setText(formattedValue + "%");
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        if (matchPercent > 90) {
//            if (currentHour < 7 || (currentHour == 7 && currentMinute < 30)) {
//                // The current time is before 7:30 AM displaying a toast
//
//                Toast.makeText(this, "please try again before 8 am", Toast.LENGTH_SHORT).show();
//            } else if (currentHour == 7 && currentMinute >= 30 && currentHour == 8 && currentMinute < 01) {
            // The current time is between 7:30 AM and 7:59 AM
            // Allow access to another activity
//            userData.setDayNumber(userData.getDayNumber() + 1);
            appDatabase.userDataDAO().update(userData);
            sendDataToserver(breathcount, swipecount);
//            Intent intent = new Intent(this, TasksActivity.class);
//            intent.putExtra("date",date);
//            startActivity(intent);
//            } else {
//                // The current time is after 7:59 AM
//                // Display a message or take some action to inform the user they can't use the app now
//                Toast.makeText(this, "please try again before 8 am", Toast.LENGTH_SHORT).show();
//            }
        } else {
//            Toast.makeText(this,"Match percentage is less than 90 %",Toast.LENGTH_SHORT).show();
        }


    }

    private void sendDataToserver(double breathcount, double swipecount) {

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("breathe_count", breathcount + "");
            jsonObject.put("swipe_count", swipecount + "");
            jsonObject.put("phone_number", userData.getPhoneNumber());
            jsonObject.put("task_day", userData.getDayNumber() - 1);


        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Call<JsonObject> call = APIClient.getInterface().postBreathActivity(bodyRequest);
        String url = call.request().url().toString();
        Log.d("Uday Debug", url);

        GetResult getResult = new GetResult();
        getResult.setMyListener(this);
        getResult.callForLogin(call, "1");
        System.out.println(getResult);
    }


    private void handleSwipeGesture(float startX, float startY, float endX, float endY) {
        if (isWorkoutRunning) {
            int count = Integer.parseInt((String) swipeCountTextView.getText());
            count++;
            swipeCountTextView.setText(count + "");
            vibrateForShortDuration();
        }
    }

    private void vibrateForShortDuration() {
        if (vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Create a short vibration effect with lower amplitude
                VibrationEffect effect = VibrationEffect.createOneShot(100, 50); // 50 is the lower amplitude
                vibrator.vibrate(effect);
            } else {
                vibrator.vibrate(100);
            }
        }
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onDecibelChanged(double decibel) {
        // Update UI or perform any actions with the decibel value
        Log.d("uday Decibel", "Current Decibel: " + decibel);

        if (decibel >= breath_min) {

            if (breath_min_count == 3) {
                breath_min_count++;
                if (isWorkoutRunning) {
                    int count = Integer.parseInt((String) breathCountTextView.getText());
                    count++;
                    breathCountTextView.setText(count + "");
                }
            } else {
                breath_min_count++;
            }
        } else {
            breath_min_count = 0;
        }
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

    @Override
    public void callback(JsonObject result, String callNo) throws JSONException {
        if (result.get("status").getAsBoolean() == true) {
            Toast.makeText(getApplicationContext(), "PhoneNumber registered", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "PhoneNumber not registered", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, Googlepaybill.class);
            startActivity(intent);

        }
    }
}