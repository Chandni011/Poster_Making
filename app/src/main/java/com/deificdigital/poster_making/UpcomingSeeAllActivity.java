package com.deificdigital.poster_making;

import static androidx.core.app.PendingIntentCompat.getActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.deificdigital.poster_making.Adapters.UpcomingSeeAll_Adapter;
import com.deificdigital.poster_making.models.Category;
import com.deificdigital.poster_making.responses.CategoryResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class UpcomingSeeAllActivity extends AppCompatActivity {

    private RecyclerView rvUpcoming;
    private UpcomingSeeAll_Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_upcoming_see_all);

        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> {startActivity(new Intent(UpcomingSeeAllActivity.this, MainActivity.class));
            finish();});

        rvUpcoming = findViewById(R.id.rvUpcomingSeeAll);
        rvUpcoming.setLayoutManager(new GridLayoutManager(this, 3));

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        fetchCategories();
    }


    public interface ApiServiceCategory {
        @GET("api/upcomingdays")
        Call<CategoryResponse> getCategories();
    }

    private void fetchCategories() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://postermaking.deifichrservices.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UpcomingSeeAllActivity.ApiServiceCategory apiServiceCategory = retrofit.create(UpcomingSeeAllActivity.ApiServiceCategory.class);
        Call<CategoryResponse> call = apiServiceCategory.getCategories();

        call.enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categories = response.body().getData();
                    adapter = new UpcomingSeeAll_Adapter(UpcomingSeeAllActivity.this, categories);
                    rvUpcoming.setAdapter(adapter);
                }
            }
            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                Log.e("API", "Error: " + t.getMessage());
            }
        });
    }
}