package com.deificdigital.poster_making.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.deificdigital.poster_making.Adapters.PoliticalAdapter;
import com.deificdigital.poster_making.Adapters.UpcomingAdapter;
import com.deificdigital.poster_making.Adapters.CustomAdapter;
import com.deificdigital.poster_making.Adapters.NewestAdapter;
import com.deificdigital.poster_making.Adapters.ViewPagerAdapter;
import com.deificdigital.poster_making.CustomSeeAllActivity;
import com.deificdigital.poster_making.PoliticalSeeallActivity;
import com.deificdigital.poster_making.R;
import com.deificdigital.poster_making.UpcomingSeeAllActivity;
import com.deificdigital.poster_making.models.Category;
import com.deificdigital.poster_making.models.CustomModel;
import com.deificdigital.poster_making.models.NewestModel;
import com.deificdigital.poster_making.models.PoliticalModel;
import com.deificdigital.poster_making.responses.ApiResponse;
import com.deificdigital.poster_making.models.ImageData;
import com.deificdigital.poster_making.responses.CategoryResponse;
import com.deificdigital.poster_making.responses.CustomResponse;
import com.deificdigital.poster_making.responses.NewestResponse;
import com.deificdigital.poster_making.responses.PoliticalResponse;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class HomeFragment extends Fragment {

    private ViewPager2 viewPager;
    private ViewPagerAdapter pagerAdapter;
    private Handler sliderHandler;
    private Runnable sliderRunnable;
    private int currentPage = 0;
    private RecyclerView recyclerView;
    private UpcomingAdapter adapter;
    private NewestAdapter newestAdapter;
    private RecyclerView newestRecyclerView;
    private RecyclerView customRecyclerView;
    private CustomAdapter customAdapter;
    private PoliticalAdapter politicalAdapter;
    private RecyclerView politicalRecyclerView;
    private ShimmerFrameLayout shimmerViewPager, shimmerUpcoming, shimmerCustom, shimmerNewest;

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        newestRecyclerView = view.findViewById(R.id.rvNewest);
        newestRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false));

        fetchPosts();

        viewPager = view.findViewById(R.id.viewPager);
        fetchImages();

        recyclerView = view.findViewById(R.id.rvUpcoming);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false));

        fetchCategories();

        customRecyclerView = view.findViewById(R.id.rvCustom);
        customRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false));
        fetchCustom();

        politicalRecyclerView = view.findViewById(R.id.rvPolitical);
        politicalRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false));
        fetchPolitical();

        TextView UpcomingSeeAll = view.findViewById(R.id.tvUpcomingSeeAll);
        UpcomingSeeAll.setOnClickListener(v -> {startActivity(new Intent(getActivity(), UpcomingSeeAllActivity.class));});

        TextView CustomSeeAll = view.findViewById(R.id.tvCustomSeeAll);
        CustomSeeAll.setOnClickListener(v -> {startActivity(new Intent(getActivity(), CustomSeeAllActivity.class));});

        TextView PoliticalSeeAll = view.findViewById(R.id.tvPoliticalSeeAll);
        PoliticalSeeAll.setOnClickListener(v -> {startActivity(new Intent(getActivity(), PoliticalSeeallActivity.class));});

        return view;
    }

    private void fetchPolitical(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://postermaking.deifichrservices.com/") // Base URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiServicePolitical apiService = retrofit.create(ApiServicePolitical.class);

        Call<PoliticalResponse> call = apiService.fetchCategories();
        call.enqueue(new Callback<PoliticalResponse>() {
            @Override
            public void onResponse(Call<PoliticalResponse> call, Response<PoliticalResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PoliticalModel> dataList = response.body().getData();
                    politicalAdapter = new PoliticalAdapter(getContext(), dataList);
                    politicalRecyclerView.setAdapter(politicalAdapter);
                } else {
                    Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PoliticalResponse> call, Throwable t) {
                Log.e("API Error", "onFailure: " + t.getMessage());
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchCustom(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://postermaking.deifichrservices.com/") // Base URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiServiceCustom apiService = retrofit.create(ApiServiceCustom.class);

        Call<CustomResponse> call = apiService.fetchCategories();
        call.enqueue(new Callback<CustomResponse>() {
            @Override
            public void onResponse(Call<CustomResponse> call, Response<CustomResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CustomModel> dataList = response.body().getData();
                    customAdapter = new CustomAdapter(getContext(), dataList);
                    customRecyclerView.setAdapter(customAdapter);
                } else {
                    Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CustomResponse> call, Throwable t) {
                Log.e("API Error", "onFailure: " + t.getMessage());
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchCategories() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://postermaking.deifichrservices.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiServiceCategory apiServiceCategory = retrofit.create(ApiServiceCategory.class);
        Call<CategoryResponse> call = apiServiceCategory.getCategories();

        call.enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categories = response.body().getData();
                    adapter = new UpcomingAdapter(getActivity(), categories);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                Log.e("API", "Error: " + t.getMessage());
            }
        });
    }

    private void fetchImages() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://postermaking.deifichrservices.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<ApiResponse> call = apiService.fetchImages();

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ImageData> images = response.body().getData();
                    pagerAdapter = new ViewPagerAdapter(images);
                    viewPager.setAdapter(pagerAdapter);
                    startAutoSlide();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("API", "Error: " + t.getMessage());
            }
        });
    }
    private void fetchPosts() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://postermaking.deifichrservices.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiServiceNewest apiService = retrofit.create(ApiServiceNewest.class);
        Call<NewestResponse> call = apiService.getPosts();
        call.enqueue(new Callback<NewestResponse>() {
            @Override
            public void onResponse(Call<NewestResponse> call, Response<NewestResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<NewestModel> postList = response.body().getData();

                    for (NewestModel post : postList) {
                        Log.d("API", "Image URL: " + post.getPostImage());
                    }

                    newestAdapter = new NewestAdapter(postList, getActivity());
                    newestRecyclerView.setAdapter(newestAdapter);
                }
            }

            @Override
            public void onFailure(Call<NewestResponse> call, Throwable t) {
                Log.e("API", "Error: " + t.getMessage());
            }
        });
    }


    private void startAutoSlide() {
        sliderHandler = new Handler();
        sliderRunnable = new Runnable() {
            @Override
            public void run() {
                if (currentPage == pagerAdapter.getItemCount()) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
                sliderHandler.postDelayed(this, 2000);
            }
        };
        sliderHandler.postDelayed(sliderRunnable, 2000);
    }

    @Override
    public void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sliderRunnable != null) {
            sliderHandler.postDelayed(sliderRunnable, 2000);
        }
    }

    public interface ApiService {
        @GET("api/imageSlider")
        Call<ApiResponse> fetchImages();
    }

    public interface ApiServiceCategory {
        @GET("api/upcomingdays")
        Call<CategoryResponse> getCategories();
    }

    public interface ApiServiceNewest {
        @GET("api/upcomming-dayspost")
        Call<NewestResponse> getPosts();
    }

    public interface ApiServiceCustom {
        @GET("api/custom-category") // Replace with the actual endpoint
        Call<CustomResponse> fetchCategories();
    }

    public interface ApiServicePolitical {
        @GET("api/political-category") // Replace with the actual endpoint
        Call<PoliticalResponse> fetchCategories();
    }
}