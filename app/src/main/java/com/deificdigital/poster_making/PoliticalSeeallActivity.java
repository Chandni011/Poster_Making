package com.deificdigital.poster_making;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.deificdigital.poster_making.Adapters.CustomSeeAll_Adapter;
import com.deificdigital.poster_making.Adapters.PoliticalAdapter;
import com.deificdigital.poster_making.Adapters.PoliticalSeeAll_Adapter;
import com.deificdigital.poster_making.models.CustomModel;
import com.deificdigital.poster_making.models.PoliticalModel;
import com.deificdigital.poster_making.responses.CustomResponse;
import com.deificdigital.poster_making.responses.PoliticalResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class PoliticalSeeallActivity extends AppCompatActivity {

    private RecyclerView politicalSeeAll;
    private PoliticalSeeAll_Adapter politicalAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_political_seeall);

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> {startActivity(new Intent(PoliticalSeeallActivity.this, MainActivity.class));
            finish();});

        politicalSeeAll = findViewById(R.id.rvPoliticalSeeAll);
        politicalSeeAll.setLayoutManager(new GridLayoutManager(this, 3));
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://postermaking.deifichrservices.com/") // Base URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PoliticalSeeallActivity.ApiServicePolitical apiService = retrofit.create(PoliticalSeeallActivity.ApiServicePolitical.class);

        Call<PoliticalResponse> call = apiService.fetchCategories();
        call.enqueue(new Callback<PoliticalResponse>() {
            @Override
            public void onResponse(Call<PoliticalResponse> call, Response<PoliticalResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PoliticalModel> dataList = response.body().getData();
                    politicalAdapter = new PoliticalSeeAll_Adapter(PoliticalSeeallActivity.this, dataList);
                    politicalSeeAll.setAdapter(politicalAdapter);
                } else {
                    Toast.makeText(PoliticalSeeallActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PoliticalResponse> call, Throwable t) {
                Log.e("API Error", "onFailure: " + t.getMessage());
                Toast.makeText(PoliticalSeeallActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public interface ApiServicePolitical {
        @GET("api/political-category")
        Call<PoliticalResponse> fetchCategories();
    }
}