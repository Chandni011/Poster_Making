package com.deificdigital.poster_making;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.deificdigital.poster_making.Adapters.CustomAdapter;
import com.deificdigital.poster_making.Adapters.CustomSeeAll_Adapter;
import com.deificdigital.poster_making.fragments.HomeFragment;
import com.deificdigital.poster_making.models.CustomModel;
import com.deificdigital.poster_making.responses.CustomResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class CustomSeeAllActivity extends AppCompatActivity {

    private RecyclerView customSeeAll;
    private CustomSeeAll_Adapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_custom_see_all);

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> {startActivity(new Intent(CustomSeeAllActivity.this, MainActivity.class));
        finish();});

        customSeeAll = findViewById(R.id.rvCustomSeeAll);
        customSeeAll.setLayoutManager(new GridLayoutManager(this, 3));
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://postermaking.deifichrservices.com/") // Base URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CustomSeeAllActivity.ApiServiceCustom apiService = retrofit.create(CustomSeeAllActivity.ApiServiceCustom.class);

        Call<CustomResponse> call = apiService.fetchCategories();
        call.enqueue(new Callback<CustomResponse>() {
            @Override
            public void onResponse(Call<CustomResponse> call, Response<CustomResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CustomModel> dataList = response.body().getData();
                    customAdapter = new CustomSeeAll_Adapter(CustomSeeAllActivity.this, dataList);
                    customSeeAll.setAdapter(customAdapter);
                } else {
                    Toast.makeText(CustomSeeAllActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CustomResponse> call, Throwable t) {
                Log.e("API Error", "onFailure: " + t.getMessage());
                Toast.makeText(CustomSeeAllActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public interface ApiServiceCustom {
        @GET("api/custom-category")
        Call<CustomResponse> fetchCategories();
    }
}