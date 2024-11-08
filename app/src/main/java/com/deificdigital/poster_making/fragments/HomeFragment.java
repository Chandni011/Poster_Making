package com.deificdigital.poster_making.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.deificdigital.poster_making.Adapters.CategoryAdapter;
import com.deificdigital.poster_making.Adapters.CustomAdapter;
import com.deificdigital.poster_making.Adapters.NewestAdapter;
import com.deificdigital.poster_making.Adapters.ViewPagerAdapter;
import com.deificdigital.poster_making.R;
import com.deificdigital.poster_making.models.Category;
import com.deificdigital.poster_making.models.CustomModel;
import com.deificdigital.poster_making.models.NewestModel;
import com.deificdigital.poster_making.responses.ApiResponse;
import com.deificdigital.poster_making.models.ImageData;
import com.deificdigital.poster_making.responses.CategoryResponse;
import com.deificdigital.poster_making.responses.CustomResponse;
import com.deificdigital.poster_making.responses.NewestResponse;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
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
    private CategoryAdapter adapter;
    private NewestAdapter newestAdapter;
    private RecyclerView newestRecyclerView;
    private RecyclerView customRecyclerView;
    private CustomAdapter customAdapter;

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
        ShimmerFrameLayout shimmerFrameLayoutUpcoming = view.findViewById(R.id.shimmerFrameLayoutUpcoming);
        ShimmerFrameLayout shimmerFrameLayoutCustom = view.findViewById(R.id.shimmerFrameLayoutCustom);
        ShimmerFrameLayout shimmerFrameLayoutNewest = view.findViewById(R.id.shimmerFrameLayoutNewest);
        ShimmerFrameLayout shimmerFrameLayoutViewPager = view.findViewById(R.id.shimmerFrameLayoutCustom);
        newestRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false));

        fetchPosts(shimmerFrameLayoutNewest); // fetched for newest section

        viewPager = view.findViewById(R.id.viewPager);
        ShimmerFrameLayout shimmerFrameLayoutViewPager2 = view.findViewById(R.id.shimmer_view_container);
        fetchImages(shimmerFrameLayoutViewPager2); // fetched for viewpager section

        recyclerView = view.findViewById(R.id.rvUpcoming);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false));

        fetchCategories(shimmerFrameLayoutUpcoming); // fetched for upcoming section-----

        customRecyclerView = view.findViewById(R.id.rvCustom);
        customRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false));

        fetchCustom(shimmerFrameLayoutCustom); // fetched for custom section-----

        return view;
    }

    private void fetchCustom(ShimmerFrameLayout shimmerFrameLayoutCustom) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                shimmerFrameLayoutCustom.startShimmer();
            }
        }, 5000);
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
                    shimmerFrameLayoutCustom.stopShimmer();
                    shimmerFrameLayoutCustom.setVisibility(View.GONE);
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

    private void fetchCategories(ShimmerFrameLayout shimmerFrameLayoutUpcoming) {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                shimmerFrameLayoutUpcoming.startShimmer();
            }
        }, 5000);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://postermaking.deifichrservices.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiServiceCategory apiServiceCategory = retrofit.create(ApiServiceCategory.class);
        Call<CategoryResponse> call = apiServiceCategory.getCategories();

        call.enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                shimmerFrameLayoutUpcoming.stopShimmer();
                shimmerFrameLayoutUpcoming.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categories = response.body().getData();
                    adapter = new CategoryAdapter(getActivity(), categories);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                Log.e("API", "Error: " + t.getMessage());
            }
        });
    }

    private void fetchImages(ShimmerFrameLayout shimmerFrameLayoutViewPager) { // viewpager section

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                shimmerFrameLayoutViewPager.startShimmer();
            }
        }, 5000);


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
                    viewPager.setVisibility(View.VISIBLE);
                    shimmerFrameLayoutViewPager.stopShimmer();
                    shimmerFrameLayoutViewPager.setVisibility(View.GONE);
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

    private boolean checkInternetConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }

    private void fetchPosts(ShimmerFrameLayout shimmerFrameLayoutNewest) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                shimmerFrameLayoutNewest.startShimmer();
            }
        }, 5000);

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
                    shimmerFrameLayoutNewest.stopShimmer();
                    shimmerFrameLayoutNewest.setVisibility(View.GONE);
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
        if(checkInternetConnection(getContext()) == false) {
            Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
        else {
            sliderHandler.removeCallbacks(sliderRunnable);
        }

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
        @GET("api/custom-category")
            // Replace with the actual endpoint
        Call<CustomResponse> fetchCategories();
    }
}