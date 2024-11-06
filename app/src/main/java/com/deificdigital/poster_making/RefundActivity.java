package com.deificdigital.poster_making;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.deificdigital.poster_making.responses.Data;
import com.deificdigital.poster_making.responses.PrivacyPolicyResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class RefundActivity extends AppCompatActivity {

    private TextView tvPrivacyTitle;
    private WebView wvPrivacyDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_refund);

        ImageView ivBack = findViewById(R.id.ivBack);
        tvPrivacyTitle = findViewById(R.id.tvPrivacyTitle);
        wvPrivacyDetails = findViewById(R.id.wvPrivacyDetails);

        fetchRefund();

        ivBack.setOnClickListener(v -> {finish();});
    }

    private void fetchRefund() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://postermaking.deifichrservices.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RefundActivity.ApiService apiService = retrofit.create(RefundActivity.ApiService.class);

        Call<PrivacyPolicyResponse> call = apiService.getPrivacyPolicy();
        call.enqueue(new Callback<PrivacyPolicyResponse>() {
            @Override
            public void onResponse(Call<PrivacyPolicyResponse> call, Response<PrivacyPolicyResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PrivacyPolicyResponse policyResponse = response.body();
                    if (policyResponse.getStatus() == 1 && policyResponse.getData() != null && !policyResponse.getData().isEmpty()) {
                        Data policyData = policyResponse.getData().get(0);
                        tvPrivacyTitle.setText(policyData.getName());
                        wvPrivacyDetails.loadData(policyData.getDetails(), "text/html", "UTF-8");
                    } else {
                        Toast.makeText(RefundActivity.this, policyResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RefundActivity.this, "Failed to load Privacy Policy", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PrivacyPolicyResponse> call, Throwable t) {
                Toast.makeText(RefundActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public interface ApiService {
        @GET("api/refund-return")
        Call<PrivacyPolicyResponse> getPrivacyPolicy();
    }
}