package com.spgon.a3flowers.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.spgon.a3flowers.R;
import com.spgon.a3flowers.database.AppDatabase;
import com.spgon.a3flowers.retrofit.APIClient;
import com.spgon.a3flowers.retrofit.GetResult;

import org.json.JSONObject;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;

public class LoginActivity extends AppCompatActivity implements GetResult.MyListener {
    @BindView(R.id.phoneNumber)
    EditText PhoneNumber;

    @BindView(R.id.login_submit)
    TextView login_submit;
    private AppDatabase appDatabase;
    private String phnum;
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
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);


    }

    public void submit(View view)  {

        phnum = PhoneNumber.getText().toString();
        if(phnum.isEmpty()){
            Toast.makeText(this,"Enter phone number",Toast.LENGTH_SHORT).show();
        }else{
        if (TextUtils.isEmpty(PhoneNumber.getText().toString())) {
            Toast.makeText(this, "Enter Phone Number", Toast.LENGTH_SHORT).show();
        } else if (!TextUtils.isEmpty(PhoneNumber.getText().toString())) {
            if (!isValidPhoneNumber(phnum)) {
                Toast.makeText(getApplicationContext(), "Enter Valid Phone Number", Toast.LENGTH_SHORT).show();
            } else {
                start();
                openLoadingDialog();
            }
        }
        }
    }
    public void openLoadingDialog() {
        progressDialog = new ProgressDialog(this, R.style.MyAlertDialogStyle);
        progressDialog.setMessage("Loading. Please wait...");
        progressDialog.show();
    }
    public void start() {
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("phone_number", PhoneNumber.getText());

        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Call<JsonObject> call = APIClient.getInterface().loginUser(bodyRequest);
        String url = call.request().url().toString();
        Log.d("Uday Debug", url);

        GetResult getResult = new GetResult();
        getResult.setMyListener(this);
        getResult.callForLogin(call, "1");
        System.out.println(getResult);

    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        // Regular expression pattern for a valid phone number
        // This pattern allows for variations like: (123) 456-7890, 123-456-7890, 123.456.7890, 1234567890
        String regex = "^(\\(?\\d{3}\\)?)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}$";
        if (!Pattern.matches("\\d{10}", phoneNumber)) {
            return false;
        }
        return Pattern.matches(regex, phoneNumber);
    }

    @Override
    public void callback(JsonObject result, String callNo) {
        progressDialog.dismiss();
        Log.d("Uday Debug", result.toString());
        Toast.makeText(getApplicationContext(), "Registration done", Toast.LENGTH_SHORT);
        if (result.get("status").getAsBoolean() == true) {
            Bundle bundle = new Bundle();
            bundle.putString("phone", phnum);
            Intent intent = new Intent(this, OtpVerifyActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
            finishAffinity();
        } else {
            Toast.makeText(getApplicationContext(), "PhoneNumber not registered", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, Registration.class);
            startActivity(intent);
            finishAffinity();
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
}