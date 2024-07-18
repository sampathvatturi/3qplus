package com.spgon.a3flowers.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.spgon.a3flowers.R;
import com.spgon.a3flowers.retrofit.APIClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Products extends AppCompatActivity {

    private ListView listView;
    private List<String> names;
    private List<String> links;
    private ArrayAdapter<String> nameAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#3572BE"));
        }
        listView = findViewById(R.id.products_listview);
        names = new ArrayList<>();
        links = new ArrayList<>();
        nameAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names);

        // Set the adapter for the ListView
        listView.setAdapter(nameAdapter);

        // Fetch data from the API and populate the ListView
        fetchDataFromApi();

        MaterialToolbar topAppBar = findViewById(R.id.buyprodctAppBar);
        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event here to go back to the previous page
                onBackPressed();
            }
        });
    }

    private void fetchDataFromApi() {
        Call<JsonObject> call = APIClient.getInterface().getproductlink();

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject responseData = response.body();

                    if (responseData != null && responseData.has("Result")) {
                        JsonArray resultArray = responseData.getAsJsonArray("Result");

                        for (JsonElement element : resultArray) {
                            if (element.isJsonObject()) {
                                JsonObject item = element.getAsJsonObject();
                                String name = item.get("name").getAsString();
                                String link = item.get("link").getAsString();
                                names.add(name);
                                links.add(link);
                            }
                        }

                        // Update the adapter to refresh the ListView
                        nameAdapter.notifyDataSetChanged();

                        // Set click listener for ListView items
                        listView.setOnItemClickListener((parent, view, position, id) -> {
                            String selectedLink = links.get(position);
                            openLinkInBrowser(selectedLink);
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                // Handle any exceptions or failures
            }
        });
    }

    private void openLinkInBrowser(String link) {
        // Create an Intent to open the link in a browser
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        startActivity(intent);
    }
}
