package com.spgon.a3flowers.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.spgon.a3flowers.R;
import com.spgon.a3flowers.database.AppDatabase;
import com.spgon.a3flowers.model.User;
import com.spgon.a3flowers.model.UserData;
import com.spgon.a3flowers.retrofit.APIClient;
import com.spgon.a3flowers.retrofit.GetResult;
import com.spgon.a3flowers.util.CommonFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;

public class OtpVerifyActivity extends AppCompatActivity implements GetResult.MyListener {

    @BindView(R.id.otpEditText)
    EditText otpEditText;
    private JSONObject jsonObject;
    private String phoneNumber;
    private AppDatabase appDatabase;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#6E4CC0"));
        }
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        setContentView(R.layout.activity_otp_verify);
        ButterKnife.bind(this);
        Bundle receivedBundle = getIntent().getExtras();

        if (receivedBundle != null) {
            phoneNumber = receivedBundle.getString("phone");  // Replace "key"
            // with the key you used to store the data
        }
    }

    public void submit(View view) {
        String otp = otpEditText.getText().toString();
        if (otp.isEmpty()){
            Toast.makeText(this,"enter otp",Toast.LENGTH_SHORT).show();
        }else {


            jsonObject = new JSONObject();
            try {
                jsonObject.put("phone_number", phoneNumber);
                jsonObject.put("otp", otp);
            } catch (Exception e) {
                e.printStackTrace();
            }

            RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
            Call<JsonObject> call = APIClient.getInterface().verifyOTP(bodyRequest);
            String url = call.request().url().toString();
            Log.d("Uday Debug", url);

            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "1");
            System.out.println(getResult);

        }
    }

    @Override
    public void callback(JsonObject result, String callNo) throws JSONException {
        if (progressDialog != null)
            progressDialog.dismiss();
        Log.d("Uday Debug", result.toString());
        openLoadingDialog();
        Toast.makeText(getApplicationContext(), "Registration done", Toast.LENGTH_SHORT);

        if (result.get("status").getAsBoolean()) {

            appDatabase = CommonFunctions.getDBinstance(getApplicationContext());
            //{"message":"User Logged in Successfully","status":true,"result":{"id":"18","name":"uday","phone_number":"9966444468","parent_name":"","parent_email":"","religion":"","occupation":"","class":"","school_district":"","teacher_id":"0","otp":"949186","verification_status":"1","created_time":"2023-07-15 18:20:24"},"reponse_code":200}
            User user = new User();
            JsonObject userdetails = result.getAsJsonObject("result");
            user.setName(userdetails.get("name").getAsString());
            user.setPhoneNumber(phoneNumber);

            user.setSub_date(userdetails.get("sub_date").getAsString());
            user.setSub_status(userdetails.get("sub_status").getAsInt());


            appDatabase.userDAO().insertAll(user);

            UserData userData = new UserData();
            userData.setPhoneNumber(phoneNumber);
            userData.setDayNumber(1);
            appDatabase.userDataDAO().update(userData);
            CommonFunctions.setStringShared(getApplicationContext(), "currentUserPhone", phoneNumber);

            Bundle bundle = new Bundle();
            bundle.putString("phone", user.getPhoneNumber());
            Intent intent = new Intent(this, TasksActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
            finishAffinity();



        } else {
            Toast.makeText(this, "Enter Valid Otp", Toast.LENGTH_SHORT).show();
        }


    }

    public void openLoadingDialog() {
        progressDialog = new ProgressDialog(this, R.style.MyAlertDialogStyle);
        progressDialog.setMessage("Loading. Please wait...");
        progressDialog.show();
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