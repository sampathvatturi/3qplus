package com.spgon.a3flowers.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.spgon.a3flowers.R;
import com.spgon.a3flowers.retrofit.APIClient;
import com.spgon.a3flowers.retrofit.GetResult;

import org.json.JSONObject;

import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;

public class Registration extends AppCompatActivity implements GetResult.MyListener {
    @BindView(R.id.studentname)
    EditText name;
    @BindView(R.id.churchname)
    EditText churchname;
    @BindView(R.id.studentphnumber)
    EditText PhoneNumber;
    @BindView(R.id.pastor_name)
    EditText pastorname;

    @BindView(R.id.pastoremail)
    EditText pastoremail;
    JSONObject jsonObject;
    @BindView(R.id.gender)
    RadioGroup gender;
//    @BindView(R.id.stustate)
//    RadioGroup stustate;
//
//    @BindView(R.id.maritalstatus)
//    RadioGroup marriage;

    @BindView(R.id.registration_submit)
    TextView registration_submit;

    @BindView(R.id.pastor_number)
    EditText pastornumber;

//    @BindView(R.id.parentemail)
//    EditText parentemail;
//
//    @BindView(R.id.parentname)
//    EditText prntname;
    private TextView textView;
    private EditText editText;
    private TextView textview2;
    private EditText editText2;

    private RadioGroup stateRadioGroup;
    private Spinner districtSpinner;
    private DistrictManager districtManager;

    private String selectedReligion;

    private String selectedGender;

    private ProgressDialog progressDialog;

    private String selectedMaritalStatus;
    private String selectedState;

    private String selectedClass;
    private String selectedDistrict;

    private EditText edittext1;

    private EditText editText3;

    private TextView pname;

    private TextView hname;

    private TextView pemail;

    private TextView hemail;

    String married;

    String unmarried;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#6E4CC0"));
        }
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        setContentView(R.layout.activity_registration);
        ButterKnife.bind(this);
//        Spinner dropdown = findViewById(R.id.religionspinner);
//        String[] items = new String[]{"HINDU", "CHIRSTIAN", "MUSLIM"};
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
//        dropdown.setAdapter(adapter);
//
//        //getting selected values from religion dropdown
//
//        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//                selectedReligion = items[position]; // Store the selected value in the variable
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parentView) {
//                // Handle when nothing is selected (if needed)
//            }
//        });
//
//
//
//
//        Spinner dropdownn = findViewById(R.id.spinner1);
//        String[] itemsss = new String[]{"8th", "9th", "10th", "INTER", "DEGREE"};
//        ArrayAdapter<String> adapterss = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, itemsss);
//        dropdownn.setAdapter(adapterss);
//
//
//        dropdownn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//                selectedClass = itemsss[position]; // Store the selected value in the variable
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parentView) {
//                // Handle when nothing is selected (if needed)
//            }
//        });






//        String vale = String.valueOf(items.getClass());

//        System.out.println(vale);

//        textView = findViewById(R.id.father);
        editText = findViewById(R.id.churchname);
//        textview2 = findViewById(R.id.husband);
//
//        pname = findViewById(R.id.fathername);
//        hname = findViewById(R.id.husbandname);
//        edittext1 = findViewById(R.id.parentname);
//
//        pemail = findViewById(R.id.fatheremail);
//        hemail = findViewById(R.id.husbandemail);
//        editText3 = findViewById(R.id.parentemail);
//
//        stateRadioGroup = findViewById(R.id.stustate);
//        districtSpinner = findViewById(R.id.districtSpinner);
//        districtManager = DistrictManager.getInstance(); // Get an instance of DistrictManager
//
//        stateRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                loadDistricts(checkedId);
//            }
//        });
//
//        // Initially, load districts for Andhra Pradesh (you can change this based on your default state)
//        loadDistricts(R.id.radioButtonAndhraPradesh);
//
//        stustate = findViewById(R.id.stustate);
//        stustate.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                switch (checkedId) {
//                    case R.id.radioButtonAndhraPradesh:
//                        selectedState = "Andhra pradesh";
//                        loadDistricts(R.id.radioButtonAndhraPradesh);// Set the selected value to "Male"
//                        break;
//                    case R.id.radioButtonTelangana:
//                        selectedState = "Telangana";
//                        loadDistricts(R.id.radioButtonTelangana);// Set the selected value to "Female"
//                        break;
//                    // Add more cases for other RadioButtons if needed
//                }
//            }
//        });
//
//
//
        gender = findViewById(R.id.gender); // Assuming gender is the RadioGroup
        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.male:
                        selectedGender = "Male"; // Set the selected value to "Male"
                        break;
                    case R.id.female:
                        selectedGender = "Female"; // Set the selected value to "Female"
                        break;
                    // Add more cases for other RadioButtons if needed
                }
            }
        });
//
//        marriage = findViewById(R.id.maritalstatus); // Assuming 'marriage' is the RadioGroup for marital status
//        marriage.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                switch (checkedId) {
//                    case R.id.married:
//                        //show husband/ wife text and edit text
//                        selectedMaritalStatus = "Married";
//
////                        married = String.valueOf(0);
////                        selectedMaritalStatus = married;
////                        System.out.println(selectedMaritalStatus);
//
//                        textview2.setVisibility(View.VISIBLE);
//                        textView.setVisibility(View.GONE);
//                        editText.setVisibility(View.VISIBLE);
//                        //name
//                        pname.setVisibility(View.GONE);
//                        hname.setVisibility(View.VISIBLE);
//                        edittext1.setVisibility(View.VISIBLE);
//
//                        //email
//                        pemail.setVisibility(View.GONE);
//                        hemail.setVisibility(View.VISIBLE);
//                        editText3.setVisibility(View.VISIBLE);
//
//                        // Set the selected value to "Married"
//                        break;
//                    case R.id.unmarried:
//                        //show father/ mother text and edit text
//                        selectedMaritalStatus = "Unmarried";
//
////                        unmarried = String.valueOf(1);
////                        selectedMaritalStatus = unmarried;
////                        System.out.println(selectedMaritalStatus);
//
//
//                        textview2.setVisibility(View.GONE);
//                        textView.setVisibility(View.VISIBLE);
//                        editText.setVisibility(View.VISIBLE);
//
//                        //name
//                        hname.setVisibility(View.GONE);
//                        pname.setVisibility(View.VISIBLE);
//                        edittext1.setVisibility(View.VISIBLE);
//
//                        //email
//                        hemail.setVisibility(View.GONE);
//                        pemail.setVisibility(View.VISIBLE);
//                        editText3.setVisibility(View.VISIBLE);
//                        // Set the selected value to "Unmarried"
//                        break;
//
//                    default:
//                        Toast.makeText(getApplicationContext(), "select marital status", Toast.LENGTH_SHORT).show();
//
//                        // Add more cases for other RadioButtons if needed
//                }
//            }
//        });



//        districtSpinner = findViewById(R.id.districtSpinner); // Assuming 'districtSpinner' is the Spinner for districts
//        districtSpinner.setAdapter(adapter);

//getting selected values from district dropdown
//        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//                selectedDistrict = (String) parentView.getItemAtPosition(position); // Store the selected value in the variable
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parentView) {
//                // Handle when nothing is selected (if needed)
//            }
//        });

        registration_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

    }
    public void openLoadingDialog() {
        progressDialog = new ProgressDialog(this, R.style.MyAlertDialogStyle);
        progressDialog.setMessage("Loading. Please wait...");
        progressDialog.show();
    }
//    private void loadDistricts(int checkedId) {
//        // Clear existing district data
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        districtSpinner.setAdapter(adapter);
//
//        // Load districts based on the selected state
//        if (checkedId == R.id.radioButtonAndhraPradesh) {
//            // Load Andhra Pradesh districts using DistrictManager
//            List<String> andhraPradeshDistricts = districtManager.getAndhraPradeshDistricts();
//            adapter.addAll(andhraPradeshDistricts);
//        } else if (checkedId == R.id.radioButtonTelangana) {
//            // Load Telangana districts using DistrictManager
//            List<String> telanganaDistricts = districtManager.getTelanganaDistricts();
//            adapter.addAll(telanganaDistricts);
//        }
//        // Notify the adapter that data has changed
//        adapter.notifyDataSetChanged();
//    }

    public boolean validate() {
        if (TextUtils.isEmpty(name.getText().toString())) {
            Toast.makeText(this, "Enter student name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (gender.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getApplicationContext(), "Please select gender", Toast.LENGTH_SHORT).show();
            return false;
        }
//        if (marriage.getCheckedRadioButtonId() == -1) {
//            Toast.makeText(getApplicationContext(), "Please select marital status", Toast.LENGTH_SHORT).show();
//            return false;
//        }
        if (TextUtils.isEmpty(churchname.getText().toString())) {
            Toast.makeText(this, "Enter church name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(PhoneNumber.getText().toString())) {
            Toast.makeText(this, "Enter student phone number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!TextUtils.isEmpty(PhoneNumber.getText().toString())) {
            String phoneNumber = PhoneNumber.getText().toString();
            if (isValidPhoneNumber(phoneNumber)) {
            } else {
                Toast.makeText(this, "enter valid number", Toast.LENGTH_SHORT).show();
                return false;
            }

        }
//        if (stustate.getCheckedRadioButtonId() == -1) {
//            Toast.makeText(getApplicationContext(), "Please select state", Toast.LENGTH_SHORT).show();
//            return false;
//        }
        if (TextUtils.isEmpty(pastorname.getText().toString())) {
            Toast.makeText(this, "Enter pastor name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(pastoremail.getText().toString())) {
            Toast.makeText(this, "Enter pastor email", Toast.LENGTH_SHORT).show();
            return false;
        }


        return true;

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

    public void submit() {

        String pname = churchname.getText().toString();

        Log.e(pname,"parent number");

        if (validate()) {

            jsonObject = new JSONObject();
            try {
                jsonObject.put("name", name.getText());
                jsonObject.put("gender", selectedGender);
                jsonObject.put("phone_number", PhoneNumber.getText());
                jsonObject.put("pastor_name", pastorname.getText());
                jsonObject.put("church_name", churchname.getText());
                jsonObject.put("pastor_number", pastornumber.getText());
                jsonObject.put("pastor_mail", pastoremail.getText());


//            //     jsonObject.put("password", user.getPassword());
//            jsonObject.put("refercode", user.getRefercode());
            } catch (Exception e) {
                e.printStackTrace();
            }
            RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
            Call<JsonObject> call = APIClient.getInterface().createUser(bodyRequest);
            String url = call.request().url().toString();
            Log.d("Uday Debug", url);

            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "1");
            System.out.println(getResult);
        }

    }

    @Override
    public void callback(JsonObject result, String callNo) {
        if(result!=null){
            Log.d("Uday Debug", result.toString());
            Toast.makeText(getApplicationContext(), "Registration done", Toast.LENGTH_SHORT);
            openLoadingDialog();
            if (result.get("status").getAsBoolean()==true) {
                Bundle bundle = new Bundle();
                bundle.putString("phone", PhoneNumber.getText().toString());
                Intent intent = new Intent(this, OtpVerifyActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                finishAffinity();

            }if(result.get("status").getAsBoolean()==false) {
              new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Call your function to close the loading dialog
                        progressDialog.dismiss();
                    }
                }, 2000);
                String message = result.get("message").getAsString();
                if(message.isEmpty()){
                    Toast.makeText(this,"something went wrong",Toast.LENGTH_SHORT).show();
                }
                if (!message.isEmpty()){
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                }
                System.out.println(message);

            }
        }else {
            Log.d("sai mani debug", String.valueOf(result));
            // Handle the case when result is null
            Toast.makeText(getApplicationContext(), "Registration failed", Toast.LENGTH_SHORT).show();
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }



    }

    public void onBackPressed() {

        Intent intent = new Intent(this, LaunchScreen.class);
        startActivity(intent);
        finishAffinity();

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

