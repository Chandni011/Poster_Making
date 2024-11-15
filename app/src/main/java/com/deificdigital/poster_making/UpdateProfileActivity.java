package com.deificdigital.poster_making;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.deificdigital.poster_making.models.User;
import com.deificdigital.poster_making.responses.UserResponse;
import com.deificdigital.poster_making.responses.UserUpdateResponse;

import java.io.IOException;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public class UpdateProfileActivity extends AppCompatActivity {

    private EditText etName, etEmail, etNumber, etDesignation, etCompanyName;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        ImageView ivBack = findViewById(R.id.ivBack);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etNumber = findViewById(R.id.etNumber);
        etDesignation = findViewById(R.id.etDesignation);
        etCompanyName = findViewById(R.id.etCompanyName);
        AppCompatButton btnSave = findViewById(R.id.btnSave);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://postermaking.deifichrservices.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ivBack.setOnClickListener(v -> {finish();});

        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("user_id", "-1");

        if (Integer.parseInt(userId) != -1) {
            fetchUserData(Integer.parseInt(userId));
        } else {
            Log.e("API", "User ID not found!");
        }
        btnSave.setOnClickListener(v -> {
            updateUserData(Integer.parseInt(userId));
//            startActivity(new Intent(UpdateProfileActivity.this, MainActivity.class));
//            finish();
        });
    }
    private void fetchUserData(int userId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://postermaking.deifichrservices.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        RequestBody requestBody = new FormBody.Builder()
                .add("user_id", String.valueOf(userId))
                .build();
        Request request = new Request.Builder()
                .url("https://postermaking.deifichrservices.com/api/user/getData")
                .post(requestBody)
                .build();
        Call<UserResponse> call = apiService.getUserData(requestBody);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse responseModel = response.body();
                    if (responseModel.getStatus() == 1 && responseModel.getData() != null) {
                        User user = responseModel.getData().get(0);
                        setDataInUI(user);
                        setDataInUI(responseModel.getData().get(0));

                    } else {
                        Toast.makeText(UpdateProfileActivity.this, "No user data found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("API", "Response Error: " + response.message());
                }
            }
            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.e("API", "Request Failed: " + t.getMessage());
                Toast.makeText(UpdateProfileActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setDataInUI(User user) {
        etName.setText(user.getName());
        etEmail.setText(user.getEmail());
        etNumber.setText(user.getPhone());
        etDesignation.setText(user.getDesignation());
        etCompanyName.setText(user.getCompany_name());
    }
    private void updateUserData(int userId) {

        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etNumber.getText().toString().trim();
        String designation = etDesignation.getText().toString().trim();
        String companyName = etCompanyName.getText().toString().trim();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setMessage("Saving...");
        progressDialog.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://postermaking.deifichrservices.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);

        RequestBody requestBody = new FormBody.Builder()
                .add("user_id", String.valueOf(userId))
                .add("name", name)
                .add("email", email)
                .add("phone", phone)
                .add("designation", designation)
                .add("company_name", companyName)
                .build();
        Call<UserUpdateResponse> call = apiService.updateUserData(userId,requestBody);
        call.enqueue(new Callback<UserUpdateResponse>() {
            @Override
            public void onResponse(Call<UserUpdateResponse> call, Response<UserUpdateResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    progressDialog.dismiss();
                    UserUpdateResponse updateResponse = response.body();
                    if (updateResponse.getStatus() == 1) {
                        Toast.makeText(UpdateProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UpdateProfileActivity.this, updateResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    progressDialog.dismiss();
                    Log.e("API", "Update Error: " + response.code() + " - " + response.message());
                    if (response.errorBody() != null) {
                        try {
                            String errorResponse = response.errorBody().string();
                            Log.e("API", "Error Body: " + errorResponse);
                        } catch (IOException e) {
                            Log.e("API", "Error reading error body: " + e.getMessage());
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<UserUpdateResponse> call, Throwable t) {
                Log.e("API", "Update Failed: " + t.getMessage());
                Toast.makeText(UpdateProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public interface ApiService {
        @POST("user/get-user")
        Call<UserResponse> getUserData(@Body RequestBody requestBody);

        @PUT("api/user/{user_id}")
        Call<UserUpdateResponse> updateUserData(@Path("user_id") int userId, @Body RequestBody requestBody);
    }
}