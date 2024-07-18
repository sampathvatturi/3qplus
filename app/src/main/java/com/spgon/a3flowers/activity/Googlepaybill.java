package com.spgon.a3flowers.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.google.common.collect.ImmutableList;

import com.google.gson.JsonObject;
import com.spgon.a3flowers.R;
import com.spgon.a3flowers.database.AppDatabase;
import com.spgon.a3flowers.model.User;
import com.spgon.a3flowers.retrofit.APIClient;
import com.spgon.a3flowers.retrofit.GetResult;
import com.spgon.a3flowers.util.CommonFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;


public class Googlepaybill extends AppCompatActivity implements GetResult.MyListener {

    private BillingClient billingClient;
    private Button googlePayButton;

    private ProgressDialog progressDialog;
    private AppDatabase appDatabase;
    private Context mcontext;
    private String value;
    private User userData;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#DACAB6FA"));
        }
        openLoadingDialog();
        setContentView(R.layout.googlepay);


        appDatabase = CommonFunctions.getDBinstance(this);
        mcontext = getApplicationContext();


        value = CommonFunctions.getStringShared(getApplicationContext(), "currentUserPhone");
        userData = appDatabase.userDAO().getUser(value);


        // Initialize the BillingClient
        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases()
                .setListener(new PurchasesUpdatedListener() {
                    @Override
                    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
                        // Handle the purchase results here
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
                            for (Purchase purchase : purchases) {
                                handlePurchase(purchase);
                            }
                        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                            // Purchase was canceled by the user
                            showToast("Purchase canceled.");
                            onBackPressed();
                        } else {
                            // Handle other billing errors
                            showToast("Error during purchase: " + billingResult.getDebugMessage());
                            onBackPressed();
                        }
                    }
                })
                .build();

        // Start connection to the Google Play Billing service
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // BillingClient is ready
                    setupGooglePayButton();
                } else {
                    // Handle setup failure
                    showToast("Failed to connect to Google Play Billing: " + billingResult.getDebugMessage());
                    onBackPressed();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Handle the disconnection from the Billing service
                // You may want to retry connecting here
                showToast("onBillingServiceDisconnected ");
            }
        });
    }

    public void openLoadingDialog() {
        progressDialog = new ProgressDialog(this, R.style.MyAlertDialogStyle);
        progressDialog.setMessage("Loading. Please wait...");
        progressDialog.show();
//        ProgressDialog dialog = ProgressDialog.show(HomeActivity.this, "",
//                "Loading. Please wait...", true);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        }, 2000); //You can change this time as you wish
    }

    // Setup the Google Pay button
    private void setupGooglePayButton() {

        googlePayButton = findViewById(R.id.googlePayButtonn);
        googlePayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement the purchase flow
                initiatePurchase();
            }
        });
    }


    // Implement the purchase flow
    private void initiatePurchase() {
        // In a real-world scenario, you would fetch the product SKUs and their details from your server
        // For this example, we use a dummy product SKU
        String productSku = "yearly_subscription";


        QueryProductDetailsParams queryProductDetailsParams =
                QueryProductDetailsParams.newBuilder()
                        .setProductList(
                                ImmutableList.of(
                                        QueryProductDetailsParams.Product.newBuilder()
                                                .setProductId(productSku)
                                                .setProductType(BillingClient.ProductType.INAPP)
                                                .build()))
                        .build();
        billingClient.queryProductDetailsAsync(
                queryProductDetailsParams,
                new ProductDetailsResponseListener() {
                    public void onProductDetailsResponse(BillingResult billingResult,
                                                         List<ProductDetails> productDetailsList) {
                        launchPurchaseFlow(productDetailsList.get(0));
                    }
                }
        );

    }


    void launchPurchaseFlow(ProductDetails productDetails) {
        assert productDetails.getOneTimePurchaseOfferDetails() != null;
        ImmutableList productDetailsParamsList =
                ImmutableList.of(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(productDetails)

                                // .setOfferToken(productDetails
                                // .getSubscriptionOfferDetails().get(0).getOfferToken())
                                .build()
                );
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build();
        BillingResult billingResult = billingClient.launchBillingFlow(this,
                billingFlowParams);
    }

    // Handle the purchase results
    private void handlePurchase(Purchase purchase) {
        // Process the payment based on the purchase details

        ConsumeParams consumeParams =
                ConsumeParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();

        ConsumeResponseListener listener = new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {

                    userData.setSub_date(CommonFunctions.getCurrent_date_in_YYYY_MM_DD_HH_MM_SS_format());
                    userData.setSub_status(1);
                    appDatabase.userDAO().update(userData);
                    // TODO: Implement your server-side verification and processing of the purchase here

                    JSONObject jsonObject = new JSONObject();
                    try {

                        jsonObject.put("phone_number", userData.getPhoneNumber());
                        jsonObject.put("txn_id", purchase.getPurchaseToken());
                        jsonObject.put("more_info", purchase.getOriginalJson());
                        jsonObject.put("txn_status", "success");


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                    Call<JsonObject> call = APIClient.getInterface().postSubscription(bodyRequest);
                    String url = call.request().url().toString();
                    Log.d("Uday Debug", url);
                    GetResult getResult = new GetResult();
                    getResult.setMyListener(Googlepaybill.this);
                    getResult.callForLogin(call, "1");
                    System.out.println(getResult);
                }
            }
        };

        billingClient.consumeAsync(consumeParams, listener);


        // Show a success message to the user
        showToast("Purchase successful!");
    }

    // Show toast message
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (billingClient != null) {
            billingClient.endConnection();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    @Override
    public void callback(JsonObject result, String callNo) throws JSONException {
        Log.d("Uday Debug", result.toString());

        if (result.get("status").getAsBoolean() == true) {
            Intent intent = new Intent(this, MainActivity.class);

            startActivity(intent);
            finish();

        } else {
            Toast.makeText(getApplicationContext(), "Something went wrong, please contact support", Toast.LENGTH_SHORT).show();

        }
    }
}
