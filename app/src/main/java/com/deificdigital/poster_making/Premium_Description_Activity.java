package com.deificdigital.poster_making;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.deificdigital.poster_making.Adapters.PlansAdapter;
import com.deificdigital.poster_making.models.AllPlans;
import com.deificdigital.poster_making.models.PlanData;
import com.deificdigital.poster_making.responses.PremiumResponse;
import com.deificdigital.poster_making.responses.TransactionResponse;
import com.deificdigital.poster_making.responses.TransactionUpdateResponse;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public class Premium_Description_Activity extends AppCompatActivity implements PaymentResultListener {

    private RecyclerView recyclerView;
    private PlansAdapter plansAdapter;
    private SharedPreferences sharedPreferences;
    private String receiptId;
    private String packageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_premium_description);

        recyclerView = findViewById(R.id.rvSubscriptionDetails);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        loadUserData();

        fetchPlans();
    }

    private void loadUserData() {
        String userId = sharedPreferences.getString("user_id", "-1");
        String userName = sharedPreferences.getString("user_name", "");
        String userEmail = sharedPreferences.getString("user_email", "");
        String userMobile = sharedPreferences.getString("user_mobile", "");

        Log.d("PremiumDescriptionActivity", "user_id: " + userId);
        Log.d("PremiumDescriptionActivity", "user_name: " + userName);
        Log.d("PremiumDescriptionActivity", "user_email: " + userEmail);
        Log.d("PremiumDescriptionActivity", "user_mobile: " + userMobile);

        if (Integer.parseInt(userId) == -1 || userName.isEmpty() || userEmail.isEmpty() || userMobile.isEmpty()) {
            Toast.makeText(this, "User data is missing", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchPlans() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://postermaking.deifichrservices.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<PremiumResponse> call = apiService.getPlans();
        call.enqueue(new Callback<PremiumResponse>() {
            @Override
            public void onResponse(Call<PremiumResponse> call, Response<PremiumResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<AllPlans> plansList = response.body().getAllPlans();
                    if (plansList != null) {

                        for (AllPlans allPlans : plansList) {
                            PlanData planData = allPlans.getPlanDetails().getPlanData(); // get PlanData from AllPlans
                            if (planData != null) {
                                packageId = String.valueOf(planData.getId()); // fetch the id
                                Log.d("PremiumDescriptionActivity", "Plan ID: " + packageId);
                            }
                        }

                        plansAdapter = new PlansAdapter(plansList, Premium_Description_Activity.this);
                        recyclerView.setAdapter(plansAdapter);
                    }
                } else {
                    Log.e("PremiumDescriptionActivity", "Failed to fetch plans: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<PremiumResponse> call, Throwable t) {
                Log.e("PremiumDescriptionActivity", "Failed to connect to server: " + t.getMessage());
            }
        });
    }

    public interface ApiService {
        @GET("api/premium")
        Call<PremiumResponse> getPlans();

        @POST("api/transcation")  // Replace with the correct endpoint
        @FormUrlEncoded
        Call<TransactionResponse> recordTransaction(
                @Field("user_id") int userId,
                @Field("user_name") String userName,
                @Field("user_email") String userEmail,
                @Field("user_mobile") String userMobile,
                @Field("amount") String amount,
                @Field("transcation_status") String transactionStatus,
                @Field("package_id") String packageId
        );

        @POST("api/transcation_complete")  // Replace with the correct endpoint for updating transactions
        @FormUrlEncoded
        Call<TransactionUpdateResponse> updateTransaction(
                @Field("user_id") String userID,
                @Field("transcation_id") String transactionId,
                @Field("receipt_number") String receiptId,
                @Field("transcation_status") String transactionStatus,
                @Field("package_id") String packageId
        );
    }

    @Override
    public void onPaymentSuccess(String transactionId) {
        String userID = sharedPreferences.getString("user_id", "-1");
        String transactionStatus = "1";
        if (receiptId != null && userID != null) {
            updateTransactionOnServer(userID, transactionId, receiptId, transactionStatus, packageId);
            Log.d("Premium", "onPaymentSuccess: " + receiptId + " " + userID + " " + transactionId);
        } else {
            Log.e("PremiumDescriptionActivity", "Receipt ID or User ID is missing. Cannot update transaction.");
            Toast.makeText(Premium_Description_Activity.this, "Receipt ID or User ID is missing", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateTransactionOnServer(String userID, String transactionId, String receiptId, String transactionStatus, String packageId) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://postermaking.deifichrservices.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<TransactionUpdateResponse> call = apiService.updateTransaction(userID, transactionId, receiptId, transactionStatus, packageId);

        call.enqueue(new Callback<TransactionUpdateResponse>() {
            @Override
            public void onResponse(Call<TransactionUpdateResponse> call, Response<TransactionUpdateResponse> response) {
                if (response.isSuccessful()) {
                    TransactionUpdateResponse transactionResponse = response.body();

                    if (transactionResponse != null && transactionResponse.getStatus() == 1) {
                        Log.d("PremiumDescriptionActivity", "Transaction updated successfully: " + transactionResponse.getMessage());
                        Toast.makeText(Premium_Description_Activity.this, "Transaction updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("PremiumDescriptionActivity", "Failed to update transaction. Response: " + transactionResponse);
                        Log.d("PremiumDescriptionActivity", "onResponse: "+ userID + " "+receiptId+" "+transactionId);
                        Toast.makeText(Premium_Description_Activity.this, "Failed to update transaction", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.d("PremiumDescriptionActivity", "onResponse: "+ userID + " "+receiptId+" "+transactionId);
                        Log.e("PremiumDescriptionActivity", "Error response: " + errorBody);
                        Log.d("PremiumDescriptionActivity", "onResponse: " + response.body());
                    } catch (IOException e) {
                        Log.e("PremiumDescriptionActivity", "Error reading error body", e);
                    }
                    Toast.makeText(Premium_Description_Activity.this, "Failed to update transaction", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TransactionUpdateResponse> call, Throwable t) {
                Log.e("PremiumDescriptionActivity", "Failed to connect to server: " + t.getMessage());
                Toast.makeText(Premium_Description_Activity.this, "Failed to connect to server", Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public void onPaymentError(int errorCode, String errorMessage) {
        Toast.makeText(this, "Payment Failed: " + errorMessage, Toast.LENGTH_LONG).show();
    }

    public void startPayment(String price) {
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_BkaNzhJLHuLxPL");
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", "RazorPay Demo");
            jsonObject.put("description", "This is your payment.");
            jsonObject.put("theme.color", "#3399cc");
            jsonObject.put("currency", "INR");
            jsonObject.put("amount", (int) (Double.parseDouble(price) * 100));

            JSONObject retryObj = new JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            jsonObject.put("retry", retryObj);

            checkout.open(this, jsonObject);
        } catch (Exception e) {
            Log.e("PremiumDescriptionActivity", "Error in starting payment", e);
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
    }

    public void recordTransactionAndStartPayment(PlanData planData) {
        String userId = sharedPreferences.getString("user_id", "-1");
        String userName = sharedPreferences.getString("user_name", "");
        String userEmail = sharedPreferences.getString("user_email", "");
        String userMobile = sharedPreferences.getString("user_mobile", "");
        String transactionStatus = "0";
        String amount = planData.getPrice();
        String packageId = String.valueOf(planData.getId());

        if (userId.equals("-1") || userName.isEmpty() || userEmail.isEmpty() || userMobile.isEmpty()) {
            Toast.makeText(this, "User data missing, cannot proceed with transaction", Toast.LENGTH_SHORT).show();
            return;
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://postermaking.deifichrservices.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<TransactionResponse> call = apiService.recordTransaction(
                Integer.parseInt(userId), userName, userEmail, userMobile, amount, transactionStatus, packageId
        );
        call.enqueue(new Callback<TransactionResponse>() {
            @Override
            public void onResponse(Call<TransactionResponse> call, Response<TransactionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    receiptId = response.body().getReceiptId();                    startPayment(amount);
                } else {
                    Log.e("PremiumDescriptionActivity", "Failed to record transaction before payment");
                    Toast.makeText(Premium_Description_Activity.this, "Failed to record transaction", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<TransactionResponse> call, Throwable t) {
                Log.e("PremiumDescriptionActivity", "Failed to record transaction: " + t.getMessage());
                Toast.makeText(Premium_Description_Activity.this, "Failed to connect to server", Toast.LENGTH_SHORT).show();
            }
        });
    }
}