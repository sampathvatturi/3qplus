package com.spgon.a3flowers.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
//import android.widget RadioButton;
//import android.widget RadioGroup;
//import android.widget TextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.spgon.a3flowers.database.AppDatabase;
import com.spgon.a3flowers.model.Question;
import com.spgon.a3flowers.model.QuestionResponse;
import com.spgon.a3flowers.model.UserData;
import com.spgon.a3flowers.retrofit.APIClient;
import com.spgon.a3flowers.retrofit.GetResult;
import com.spgon.a3flowers.retrofit.UserService;
import com.spgon.a3flowers.util.CommonFunctions;
import com.spgon.a3flowers.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import butterknife.ButterKnife;

public class      Quiz extends AppCompatActivity implements GetResult.MyListener {

    String quiz_id;
    private RecyclerView recyclerView;
    private QuestionAdapter questionAdapter;
    private List<Question> questionList;
    private ProgressBar progressBar;
    private int correct_counter = 0;
    private Button button_submit;
    public static Context mcontext;
    private UserData userData;
    AppDatabase appDatabase;
    private int selectedOptionsCount = 0;
    private int answeredQuestionsCount = 0;
    private int  corretCounter=0;
    private int questionsCount=0;

    private HashMap<String, String> selectedAnswers = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#3572BE"));
        }
        setContentView(R.layout.activity_quiz);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.top_app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle receivedBundle = getIntent().getExtras();
        quiz_id = receivedBundle.getString("video_id");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        call_API();
        button_submit = findViewById(R.id.tasks_submit);

        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    // Create a JSON object to send the phone_number and yt_ids
                    JSONObject jsonObject = new JSONObject();
                    appDatabase = CommonFunctions.getDBinstance(getApplicationContext());
                    Bundle receivedBundle = getIntent().getExtras();
                    String value = null;
                    if (receivedBundle != null) {
                        value = receivedBundle.getString("phone");  // Replace "key" with the key you used to store the data
                    }
                    if (value == null) {
                        value = CommonFunctions.getStringShared(getApplicationContext(), "currentUserPhone");
                    } else {
                        CommonFunctions.setStringShared(getApplicationContext(), "currentUserPhone", value);
                    }
                    userData = appDatabase.userDataDAO().getUser(value);
//                    Log.d("sai Debug userdata quiz", userData.toString());
//                    System.out.println("sai debug user");
//                    System.out.println(userData);
//                    Log.d("Sai Debug value quiz", value.toString());
//
//                    System.out.println(value);
//
//                    UserData u = new UserData();
//                    String num = u.getPhoneNumber();
//                    Log.d("sai Debug num quiz", num.toString());
//                    System.out.println(num);

                    try {
                        jsonObject.put("phone_number",   CommonFunctions.getStringShared(getApplicationContext(), "currentUserPhone")); // Assuming you have 'userId' defined somewhere
                        jsonObject.put("yt_id", quiz_id);
                        JSONObject selectedAnswersObject = new JSONObject(selectedAnswers);
                        if (correct_counter == questionList.size()) {
                            jsonObject.put("status", true);
                        }
                        else
                        {
                            jsonObject.put("status", false);
                        }


                        jsonObject.put("selected_answers", selectedAnswersObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // Create a RequestBody from the JSON object
                    RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

                    // Make the API call to send the data
                    UserService userService;
                    userService = APIClient.getInterface();
                    Call<JsonObject> call = userService.sendVideoId(requestBody);

                    call.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            if (response.isSuccessful()) {
                                // Request was successful, handle the response if needed
                                JsonObject jsonResponse = response.body();
                                // Process jsonResponse as needed
                                Toast.makeText(getApplicationContext(), "Data sent to server", Toast.LENGTH_SHORT).show();
                            } else {
                                // Handle the error here if the request was not successful
                                Toast.makeText(getApplicationContext(), "Failed to send data", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            // Handle network errors or other failures here
                            Toast.makeText(getApplicationContext(), "Network error", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Intent intent = new Intent(getApplicationContext(), TasksActivity.class);
                    startActivity(intent);
                    finishAffinity();

            }
        });
    }

    private void call_API() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("yt_id", quiz_id);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        Call<JsonObject> call = APIClient.getInterface().getQuiz(bodyRequest);
        String url = call.request().url().toString();
        Log.d("Uday Debug", url);

        GetResult getResult = new GetResult();
        getResult.setMyListener(this);
        getResult.callForLogin(call, "1");
        System.out.println(getResult);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void callback(JsonObject result, String callNo) throws JSONException {
        Log.d("Uday Debug", result.toString());
        progressBar.setVisibility(View.GONE);
        if (result.get("status").getAsBoolean() == true) {
            Gson gson = new Gson();
            QuestionResponse questionResponse = gson.fromJson(result.getAsJsonObject(), QuestionResponse.class);
            questionList = questionResponse.getResult().getQasData();
        } else {
            Toast.makeText(getApplicationContext(), "PhoneNumber not registered", Toast.LENGTH_SHORT).show();
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        questionAdapter = new QuestionAdapter(questionList);
        recyclerView.setAdapter(questionAdapter);
    }

    private class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {
        private List<Question> questionList;

        public QuestionAdapter(List<Question> questionList) {
            this.questionList = questionList;
        }

        @NonNull
        @Override
        public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question, parent, false);
            return new QuestionViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
            Question question = questionList.get(position);
            holder.bind(question);
        }

        @Override
        public int getItemCount() {
            return questionList.size();
        }

        class QuestionViewHolder extends RecyclerView.ViewHolder {
            private TextView questionTextView;
            private RadioGroup optionsRadioGroup;
            private Button submitButton;

            public QuestionViewHolder(@NonNull View itemView) {
                super(itemView);
                questionTextView = itemView.findViewById(R.id.questionTextView);
                optionsRadioGroup = itemView.findViewById(R.id.optionsRadioGroup);
                submitButton = itemView.findViewById(R.id.submitButton);
            }

            public void bind(Question question) {
                questionTextView.setText(question.getQuestion());

                optionsRadioGroup.removeAllViews(); // Clear existing radio buttons

                JSONObject optionsJson;
                try {
                    optionsJson = new JSONObject(question.getOptions());
                    for (int i = 1; i <= optionsJson.length(); i++) {
                        RadioButton radioButton = new RadioButton(itemView.getContext());
                        radioButton.setText(optionsJson.getString("option" + i));
                        optionsRadioGroup.addView(radioButton);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                optionsRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        int selectedOptionId = optionsRadioGroup.getCheckedRadioButtonId();

                        RadioButton selectedRadioButton = itemView.findViewById(selectedOptionId);
                        String selectedAnswer = selectedRadioButton.getText().toString();

                        String questionId = question.getId();

                        // Store the selected answer in the HashMap with the question ID as the key
                        selectedAnswers.put(questionId, selectedAnswer);

                        if (selectedAnswer.equals(question.getAnswer())) {
                            // Correct answer, show success message
                          //  Toast.makeText(itemView.getContext(), "Correct!", Toast.LENGTH_SHORT).show();
                            correct_counter += 1;
//                            for (int i = 0; i < optionsRadioGroup.getChildCount(); i++) {
//                                optionsRadioGroup.getChildAt(i).setEnabled(false);
//                            }
//                            selectedRadioButton.setEnabled(false);
                        } else {
                            // Incorrect answer, show error message
                           // Toast.makeText(itemView.getContext(),
                            // "Incorrect ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }
}
