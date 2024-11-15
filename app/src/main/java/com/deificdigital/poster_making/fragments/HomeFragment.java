package com.deificdigital.poster_making.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.airbnb.lottie.LottieAnimationView;
import com.deificdigital.poster_making.Adapters.PoliticalAdapter;
import com.deificdigital.poster_making.Adapters.UpcomingAdapter;
import com.deificdigital.poster_making.Adapters.CustomAdapter;
import com.deificdigital.poster_making.Adapters.NewestAdapter;
import com.deificdigital.poster_making.Adapters.ViewPagerAdapter;
import com.deificdigital.poster_making.CustomSeeAllActivity;
import com.deificdigital.poster_making.PoliticalSeeallActivity;
import com.deificdigital.poster_making.Premium_Description_Activity;
import com.deificdigital.poster_making.R;
import com.deificdigital.poster_making.UpcomingSeeAllActivity;
import com.deificdigital.poster_making.models.Category;
import com.deificdigital.poster_making.models.CustomModel;
import com.deificdigital.poster_making.models.NewestModel;
import com.deificdigital.poster_making.models.PoliticalModel;
import com.deificdigital.poster_making.responses.ViewPagerResponse;
import com.deificdigital.poster_making.models.ImageData;
import com.deificdigital.poster_making.responses.CategoryResponse;
import com.deificdigital.poster_making.responses.CustomResponse;
import com.deificdigital.poster_making.responses.NewestResponse;
import com.deificdigital.poster_making.responses.PoliticalResponse;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class HomeFragment extends Fragment {

    private boolean isFragmentLoaded = false;
    private ViewPager2 viewPager;
    private ViewPagerAdapter pagerAdapter;

    private TextView tvUpcomingDaysAndFestival;
    private TextView tvUpcomingSeeAll;

    private TextView tvCustom;
    private TextView tvCustomSeeAll;

    private TextView tvNewest;

    private TextView tvPolitical;
    private TextView tvPoliticalSeeAll;

    private ImageView ivOffline;

    private NestedScrollView nestedScrollView;

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
    private LottieAnimationView premiumCrown;
    private Handler handler;
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

//        nestedScrollView = view.findViewById(R.id.nestedScrollView);
        ivOffline = view.findViewById(R.id.ivOffline);

//        startInternetCheck();

//        handler = new Handler(Looper.getMainLooper());
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Bundle bundle = getArguments();
//                boolean flag = true;
//                if(bundle != null) {
//                    flag = bundle.getBoolean("boolCheckInternet");
//                }
//                if (flag == false) {
//                    nestedScrollView.setVisibility(View.GONE);
//                    ivOffline.setVisibility(View.VISIBLE);
//                }
//                else {
//                    nestedScrollView.setVisibility(View.VISIBLE);
//                    ivOffline.setVisibility(View.GONE);
//                }
//            }
//        }, 3000); // internet not found delay shimmer

        tvUpcomingDaysAndFestival = view.findViewById(R.id.tvUpcomingDaysAndFestival);
        tvUpcomingSeeAll = view.findViewById(R.id.tvUpcomingSeeAll);
        tvCustom = view.findViewById(R.id.tvCustom);
        tvCustomSeeAll = view.findViewById(R.id.tvCustomSeeAll);
        tvPolitical = view.findViewById(R.id.tvPolitical);
        tvPoliticalSeeAll = view.findViewById(R.id.tvPoliticalSeeAll);
        tvNewest = view.findViewById(R.id.tvNewest);

        ShimmerFrameLayout shimmerFrameLayoutUpcoming = view.findViewById(R.id.shimmerFrameLayoutUpcoming);
        ShimmerFrameLayout shimmerFrameLayoutCustom = view.findViewById(R.id.shimmerFrameLayoutCustom);
        ShimmerFrameLayout shimmerFrameLayoutNewest = view.findViewById(R.id.shimmerFrameLayoutNewest);
        ShimmerFrameLayout shimmerFrameLayoutViewPager = view.findViewById(R.id.shimmerFrameLayoutCustom);
        ShimmerFrameLayout shimmerFrameLayoutPolitical = view.findViewById(R.id.shimmerFrameLayoutPoliticalParty);

        if (checkInternetConnection(getContext()) == false) {
            shimmerFrameLayoutNewest.stopShimmer();
            shimmerFrameLayoutUpcoming.stopShimmer();
            shimmerFrameLayoutCustom.stopShimmer();
            shimmerFrameLayoutPolitical.stopShimmer();
        }


        newestRecyclerView = view.findViewById(R.id.rvNewest);
        newestRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false));

        fetchPosts(shimmerFrameLayoutNewest); //fetched for newest section

        viewPager = view.findViewById(R.id.viewPager);
        ShimmerFrameLayout shimmerFrameLayoutViewPager2 = view.findViewById(R.id.shimmer_view_container);
        fetchImages(shimmerFrameLayoutViewPager2); // fetched for viewpager section

        recyclerView = view.findViewById(R.id.rvUpcoming);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false));

        fetchCategories(shimmerFrameLayoutUpcoming); // fetching for upcoming section

        customRecyclerView = view.findViewById(R.id.rvCustom);
        customRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false));
        fetchCustom(shimmerFrameLayoutCustom); // fetching for custom section

        politicalRecyclerView = view.findViewById(R.id.rvPolitical);
        politicalRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false));
        fetchPolitical(shimmerFrameLayoutPolitical); // fetching for political section

        premiumCrown = view.findViewById(R.id.premiumCrown);
        premiumCrown.setOnClickListener(v -> {startActivity(new Intent(getActivity(), Premium_Description_Activity.class));});

        TextView UpcomingSeeAll = view.findViewById(R.id.tvUpcomingSeeAll);
        UpcomingSeeAll.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), UpcomingSeeAllActivity.class));
        });

        TextView CustomSeeAll = view.findViewById(R.id.tvCustomSeeAll);
        CustomSeeAll.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), CustomSeeAllActivity.class));
        });

        TextView PoliticalSeeAll = view.findViewById(R.id.tvPoliticalSeeAll);
        PoliticalSeeAll.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), PoliticalSeeallActivity.class));
        });

        return view;
    }

//    private void startInternetCheck() {
//        Handler handler = new Handler(Looper.getMainLooper());
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // Continue checking until the fragment is loaded or we get a positive connection
//                if (isFragmentLoaded && checkInternetConnection(requireContext()) == false) {
//                    nestedScrollView.setVisibility(View.GONE);
//                    ivOffline.setVisibility(View.VISIBLE);
//                    handler.postDelayed(this, 3000); // Repeat every 3 seconds
//                } else {
//                    // Stop the repeated checks
//                    handler.removeCallbacks(this);
//                }
//            }
//        }, 3000); // Initial delay
//    }

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        isFragmentLoaded = true;
//    }

    private boolean checkInternetConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }


    private void fetchPolitical(ShimmerFrameLayout shimmerFrameLayoutPolitical) {
        tvPolitical.setVisibility(View.GONE);
        tvPoliticalSeeAll.setVisibility(View.GONE);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                shimmerFrameLayoutPolitical.startShimmer();

            }
        }, 5000);
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
                    tvPolitical.setVisibility(View.VISIBLE);
                    tvPoliticalSeeAll.setVisibility(View.VISIBLE);
                    shimmerFrameLayoutPolitical.stopShimmer();
                    shimmerFrameLayoutPolitical.setVisibility(View.GONE);
                    List<PoliticalModel> dataList = response.body().getData();
                    politicalAdapter = new PoliticalAdapter(getContext(), dataList);
                    politicalRecyclerView.setAdapter(politicalAdapter);
                } else {
//                    Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PoliticalResponse> call, Throwable t) {
                Log.e("API Error", "onFailure: " + t.getMessage());
            }
        });
    }

    private void fetchCustom(ShimmerFrameLayout shimmerFrameLayoutCustom) {

        tvCustom.setVisibility(View.GONE);
        tvCustomSeeAll.setVisibility(View.GONE);
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
                    tvCustom.setVisibility(View.VISIBLE);
                    tvCustomSeeAll.setVisibility(View.VISIBLE);
                    shimmerFrameLayoutCustom.stopShimmer();
                    shimmerFrameLayoutCustom.setVisibility(View.GONE);
                    List<CustomModel> dataList = response.body().getData();
                    customAdapter = new CustomAdapter(getContext(), dataList);
                    customRecyclerView.setAdapter(customAdapter);
                } else {
//                    Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CustomResponse> call, Throwable t) {
                Log.e("API Error", "onFailure: " + t.getMessage());
            }
        });
    }

    private void fetchCategories(ShimmerFrameLayout shimmerFrameLayoutUpcoming) {

        tvUpcomingDaysAndFestival.setVisibility(View.GONE);
        tvUpcomingSeeAll.setVisibility(View.GONE);

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
                if (response.isSuccessful() && response.body() != null) {
                    tvUpcomingDaysAndFestival.setVisibility(View.VISIBLE);
                    tvUpcomingSeeAll.setVisibility(View.VISIBLE);
                    shimmerFrameLayoutUpcoming.stopShimmer();
                    shimmerFrameLayoutUpcoming.setVisibility(View.GONE);
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

    private void fetchImages(ShimmerFrameLayout shimmerFrameLayoutViewPager) {
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
        Call<ViewPagerResponse> call = apiService.fetchImages();

        call.enqueue(new Callback<ViewPagerResponse>() {
            @Override
            public void onResponse(Call<ViewPagerResponse> call, Response<ViewPagerResponse> response) {
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
            public void onFailure(Call<ViewPagerResponse> call, Throwable t) {
                Log.e("API", "Error: " + t.getMessage());
            }
        });
    }

    private void fetchPosts(ShimmerFrameLayout shimmerFrameLayoutNewest) {
        tvNewest.setVisibility(View.GONE);
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
                    tvNewest.setVisibility(View.VISIBLE);
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
//        if(checkInternetConnection(getContext()) == false) {
//            // -------------------------------------
//        }
//        else {
        if(sliderHandler != null) {
            sliderHandler.removeCallbacks(sliderRunnable);
        }

//        }

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
        Call<ViewPagerResponse> fetchImages();
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

    public interface ApiServicePolitical {
        @GET("api/political-category")
            // Replace with the actual endpoint
        Call<PoliticalResponse> fetchCategories();
    }
}