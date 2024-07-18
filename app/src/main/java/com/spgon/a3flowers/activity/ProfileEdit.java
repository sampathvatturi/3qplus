package com.spgon.a3flowers.activity;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.view.View;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.spgon.a3flowers.R;
import com.spgon.a3flowers.database.AppDatabase;
import com.spgon.a3flowers.model.User;
import com.spgon.a3flowers.retrofit.APIClient;
import com.spgon.a3flowers.util.CommonFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class ProfileEdit extends AppCompatActivity {

    @BindView(R.id.updatedsname)
    EditText stuname;
    @BindView(R.id.upparentsNumber)
    EditText parentsNumber;

    @BindView(R.id.upstudentphnumber)
    EditText stuphnnum;

    @BindView(R.id.mgmtemail)
    EditText mgmtemail;

    @BindView(R.id.updatebutton)
    Button button;

    @BindView(R.id.parentnumberupdate)
    EditText prntnum;
    @BindView(R.id.parentemailupdate)
    EditText prntemail;

    private AppDatabase appDatabase;
    private Context mcontext;
    private String current_phoneNumber;
    private User user;
    private String selectedClass;

    Spinner classSpinner;
    String[] classOptions = {"8th", "9th", "10th", "INTER", "DEGREE"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#6E4CC0"));
        }

        appDatabase = CommonFunctions.getDBinstance(this);
        mcontext = getApplicationContext();

        current_phoneNumber =
                CommonFunctions.getStringShared(getApplicationContext(), "currentUserPhone");
        user = appDatabase.userDAO().getUser(current_phoneNumber);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    updateProfile();
                    Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                    startActivity(intent);
                    finishAffinity();
                } else {
                    Toast.makeText(getApplicationContext(), "Something's wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        classSpinner = findViewById(R.id.updatespinner);
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, classOptions);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        classSpinner.setAdapter(adapter);
//
//        classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//                selectedClass = classOptions[position];
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parentView) {
//                // Handle when nothing is selected (if needed)
//            }
//        });

        // Make a network request to retrieve user data
        getUserData();
    }

    private void updateProfile() {
        String test = prntnum.getText().toString();
        System.out.println(test);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("phone_number", user.getPhoneNumber());
            jsonObject.put("name", stuname.getText().toString());
            jsonObject.put("class", selectedClass);
            jsonObject.put("org_email", mgmtemail.getText().toString());
            jsonObject.put("parent_number", prntnum.getText().toString());
            jsonObject.put("parent_email", prntemail.getText().toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

        // Make the API call to update the user profile
        Call<JsonObject> call = APIClient.getInterface().updateprofile(requestBody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    // Handle the successful response, if needed
                    Toast.makeText(ProfileEdit.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    // Handle the API error here
                    Toast.makeText(ProfileEdit.this, "API Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                // Handle network failure here
                Toast.makeText(ProfileEdit.this, "Network Error", Toast.LENGTH_SHORT).show();
                Log.e("ProfileEdit", "Network Error: " + t.getMessage());
            }
        });
    }

    void getUserData() {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("phone_number", user.getPhoneNumber());
        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(JSON, jsonObject.toString());

        Call<JsonObject> call = APIClient.getInterface().getProfile(requestBody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body();
                    if (jsonObject != null) {
                        JsonObject userdetails = jsonObject.getAsJsonObject("Result");

                        String name = userdetails.get("name").getAsString();
                        String phoneNumber = userdetails.get("phone_number").getAsString();
                        String parentPhone = userdetails.get("parent_number").getAsString();
                        String userClass = userdetails.get("class").getAsString();
                        String managementEmail = userdetails.get("org_email").getAsString();
                        String parentnum = userdetails.get("parent_number").getAsString();
                        String parentemail = userdetails.get("parent_email").getAsString();
                        String status = userdetails.get("sub_status").getAsString();

                        parentsNumber.setText(parentPhone);
                        stuname.setText(name);
                        stuphnnum.setText(phoneNumber);
                        mgmtemail.setText(managementEmail);
                        prntemail.setText(parentemail);
                        prntnum.setText(parentnum);
//
//                        ArrayAdapter<String> adapter = new ArrayAdapter<>(ProfileEdit.this, android.R.layout.simple_spinner_item, classOptions);
//                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                        classSpinner.setAdapter(adapter);
//
//                        int position = getPositionInArray(classOptions, userClass);
//                        classSpinner.setSelection(position);

                        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", name);
                        editor.putString("sub_status", status);
                        editor.apply();
                    }
                } else {
                    // Handle the API error here
                    Toast.makeText(ProfileEdit.this, "API Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                // Handle network failure here
                Toast.makeText(ProfileEdit.this, "Network Error", Toast.LENGTH_SHORT).show();
                Log.e("ProfileEdit", "Network Error: " + t.getMessage());
            }
        });
    }

    private int getPositionInArray(String[] array, String value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(value)) {
                return i;
            }
        }
        return 0; // Default to the first item if not found
    }

    public boolean validate() {
        if (TextUtils.isEmpty(stuname.getText().toString())) {
            Toast.makeText(this, "Enter student name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(stuphnnum.getText().toString())) {
            Toast.makeText(this, "Enter student's mobile number", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
