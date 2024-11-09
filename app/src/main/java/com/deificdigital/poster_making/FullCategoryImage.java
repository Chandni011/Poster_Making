package com.deificdigital.poster_making;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.deificdigital.poster_making.Adapters.CategoryImageAdapter;
import com.deificdigital.poster_making.models.CustomImageModel;
import com.deificdigital.poster_making.responses.CustomImageResponse;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class FullCategoryImage extends AppCompatActivity {

    private RecyclerView rvCategoryImage;
    private ImageView ivBack;
    private TextView ivTabName;
    private CategoryImageAdapter adapter;
    private String category__Id;
    private String categoryName;
    private String category__Type;
    private String upcoming__Id;
    private String upcomingName;
    private String upcoming__Type;
    private String political__Id;
    private String politicalName;
    private String political__Type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_full_category_image);

        ivBack = findViewById(R.id.ivBack);
        ivTabName = findViewById(R.id.ivTabName);
        rvCategoryImage = findViewById(R.id.rvCategoryImage);

        category__Id = String.valueOf(getIntent().getIntExtra("id", -1)); // The intent extra for "id" maps to "category_type" in fetchPosts
        categoryName = getIntent().getStringExtra("category_name");
        category__Type = getIntent().getStringExtra("category_type");// This should map to "category" in fetchPosts
        Log.d("Get intent", "category__id "+category__Id + ",category__type" + category__Type);

        upcoming__Id = String.valueOf(getIntent().getIntExtra("id", -1)); // The intent extra for "id" maps to "category_type" in fetchPosts
        upcomingName = getIntent().getStringExtra("category_name");
        upcoming__Type = getIntent().getStringExtra("category_type");// This should map to "category" in fetchPosts
        Log.d("Get intent", "category__id "+upcoming__Id + ",category__type" + upcoming__Type);

        political__Id = String.valueOf(getIntent().getIntExtra("id", -1)); // The intent extra for "id" maps to "category_type" in fetchPosts
        politicalName = getIntent().getStringExtra("category_name");
        political__Type = getIntent().getStringExtra("category_type");// This should map to "category" in fetchPosts
        Log.d("Get intent", "category__id "+political__Id + ",category__type" + political__Type);

        ivBack.setOnClickListener(v -> {
            startActivity(new Intent(FullCategoryImage.this, MainActivity.class));
            finish();
        });

        rvCategoryImage.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new CategoryImageAdapter(this, new ArrayList<>());
        rvCategoryImage.setAdapter(adapter);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://postermaking.deifichrservices.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        if (category__Id.equals("-1")) {
            Log.e("FullCategoryImage", "Invalid category ID received.");
        } else {
            ivTabName.setText(categoryName);
            fetchPosts(apiService, category__Id, category__Type);
        }

        Retrofit retrofitPolitical = new Retrofit.Builder()
                .baseUrl("https://postermaking.deifichrservices.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiServicePolitical apiServicePolitical = retrofitPolitical.create(ApiServicePolitical.class);

        if (political__Id.equals("-1")) {
            Log.e("FullCategoryImage", "Invalid category ID received.");
        } else {
            ivTabName.setText(politicalName);
            fetchPoliticalPosts(apiServicePolitical, category__Id, category__Type);
        }

        Retrofit retrofitUpcoming = new Retrofit.Builder()
                .baseUrl("https://postermaking.deifichrservices.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiServiceUpcoming apiServiceUpcoming = retrofitUpcoming.create(ApiServiceUpcoming.class);

        if (upcoming__Id.equals("-1")) {
            Log.e("FullCategoryImage", "Invalid category ID received.");
        } else {
            ivTabName.setText(upcomingName);
            fetchUpcomingPosts(apiServiceUpcoming, upcoming__Id, upcoming__Type);
        }
    }

    private void fetchPosts(ApiService apiService, String category__Id, String category__Type) {
        Call<CustomImageResponse> call = apiService.fetchPostsByCategory(category__Id, category__Type);
        call.enqueue(new Callback<CustomImageResponse>() {
            @Override
            public void onResponse(Call<CustomImageResponse> call, Response<CustomImageResponse> response) {
                Log.d("API Response", "Response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    Log.d("API Response", "Response body: " + new Gson().toJson(response.body()));

                    List<CustomImageModel> postList = response.body().getData();
                    Log.d("API Response", "Data received: " + postList.size());

                    if (postList.isEmpty()) {
                        Log.d("API Response", "Post list is empty, no filtering to be done.");
                    } else {
                        List<CustomImageModel> filteredPosts = new ArrayList<>();
                        for (CustomImageModel post : postList) {
                            Log.d("Post Debug", "Post category: " + post.getCategory() + ", Post category type: " + post.getCategory_type());

                            // Check category and category_type fields
                            if (post.getCategory().equals(category__Type) && post.getCategory_type().equals(category__Id)) {
                                filteredPosts.add(post);
                                Log.d("Post After Condition", "Post category: " + post.getCategory() + ", Post category type: " + post.getCategory_type()+",Post category name" + post.getName());
                            }
                        }
                        if (!filteredPosts.isEmpty()) {
                            adapter.updateData(filteredPosts);
                        } else {
                            Log.d("API Response", "No posts found for this category.");
                        }
                    }
                } else {
                    try {
                        Log.d("API Error", "Error body: " + response.errorBody().string());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<CustomImageResponse> call, Throwable t) {
                Log.e("API Error", "Failed to fetch data", t);
            }
        });
    }

    private void fetchPoliticalPosts(ApiServicePolitical apiService, String political__Id, String political__Type) {
        Call<CustomImageResponse> call = apiService.fetchPoliticalPostsByCategory(political__Id, political__Type);
        call.enqueue(new Callback<CustomImageResponse>() {
            @Override
            public void onResponse(Call<CustomImageResponse> call, Response<CustomImageResponse> response) {
                Log.d("API Response", "Response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    Log.d("API Response", "Response body: " + new Gson().toJson(response.body()));

                    List<CustomImageModel> postList = response.body().getData();
                    Log.d("API Response", "Data received: " + postList.size());

                    if (postList.isEmpty()) {
                        Log.d("API Response", "Post list is empty, no filtering to be done.");
                    } else {
                        List<CustomImageModel> filteredPosts = new ArrayList<>();
                        for (CustomImageModel post : postList) {
                            Log.d("Post Debug", "Post category: " + post.getCategory() + ", Post category type: " + post.getCategory_type());

                            // Check category and category_type fields
                            if (post.getCategory().equals(political__Type) && post.getCategory_type().equals(political__Id)) {
                                filteredPosts.add(post);
                                Log.d("Post After Condition", "Post category: " + post.getCategory() + ", Post category type: " + post.getCategory_type()+",Post category name" + post.getName());
                            }
                        }
                        if (!filteredPosts.isEmpty()) {
                            adapter.updateData(filteredPosts);
                        } else {
                            Log.d("API Response", "No posts found for this category.");
                        }
                    }
                } else {
                    try {
                        Log.d("API Error", "Error body: " + response.errorBody().string());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<CustomImageResponse> call, Throwable t) {
                Log.e("API Error", "Failed to fetch data", t);
            }
        });
    }

    private void fetchUpcomingPosts(ApiServiceUpcoming apiServiceUpcoming, String upcoming__Id, String upcoming__Type) {
        Call<CustomImageResponse> call = apiServiceUpcoming.fetchUpcomingPostsByCategory(upcoming__Id, upcoming__Type);
        call.enqueue(new Callback<CustomImageResponse>() {
            @Override
            public void onResponse(Call<CustomImageResponse> call, Response<CustomImageResponse> response) {
                Log.d("API Response", "Response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    Log.d("API Response", "Response body: " + new Gson().toJson(response.body()));

                    List<CustomImageModel> postList = response.body().getData();
                    Log.d("API Response", "Data received: " + postList.size());

                    if (postList.isEmpty()) {
                        Log.d("API Response", "Post list is empty, no filtering to be done.");
                    } else {
                        List<CustomImageModel> filteredPosts = new ArrayList<>();
                        for (CustomImageModel post : postList) {
                            Log.d("Post Debug", "Post category: " + post.getCategory() + ", Post category type: " + post.getCategory_type());

                            if (post.getCategory().equals(upcoming__Type) && post.getCategory_type().equals(upcoming__Id)) {
                                filteredPosts.add(post);
                                Log.d("Post After Condition", "Post category: " + post.getCategory() + ", Post category type: " + post.getCategory_type()+",Post category name" + post.getName());
                            }
                        }
                        if (!filteredPosts.isEmpty()) {
                            adapter.updateData(filteredPosts);
                        } else {
                            Log.d("API Response", "No posts found for this category.");
                        }
                    }
                } else {
                    try {
                        Log.d("API Error", "Error body: " + response.errorBody().string());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<CustomImageResponse> call, Throwable t) {
                Log.e("API Error", "Failed to fetch data", t);
            }
        });
    }

    public interface ApiService {
        @GET("api/get-postbyCategory")
        Call<CustomImageResponse> fetchPostsByCategory(
                @Query("category_id") String categoryId,
                @Query("category_type") String categoryType
        );
    }

    public interface ApiServiceUpcoming {
        @GET("api/get-postbyCategory")
        Call<CustomImageResponse> fetchUpcomingPostsByCategory(
                @Query("category_id") String upcomingId,
                @Query("category_type") String upcomingType
        );
    }

    public interface ApiServicePolitical {
        @GET("api/get-postbyCategory")
        Call<CustomImageResponse> fetchPoliticalPostsByCategory(
                @Query("category_id") String politicalId,
                @Query("category_type") String politicalType
        );
    }
}
