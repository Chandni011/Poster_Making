package com.deificdigital.poster_making.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.deificdigital.poster_making.FullImageActivity;
import com.deificdigital.poster_making.LoginActivity;
import com.deificdigital.poster_making.PrivacyPolicyActivity;
import com.deificdigital.poster_making.R;
import com.deificdigital.poster_making.RefundActivity;
import com.deificdigital.poster_making.TermsConditionsActivity;
import com.deificdigital.poster_making.Transaction_History_Activity;
import com.deificdigital.poster_making.UpdateProfileActivity;
import com.deificdigital.poster_making.models.User;
import com.deificdigital.poster_making.responses.UserResponse;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.internal.GoogleSignInOptionsExtensionParcelable;
import com.google.firebase.auth.FirebaseAuth;

import okhttp3.FormBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class ProfileFragment extends Fragment {

    private TextView tvUsername, tvUserEmail;
    private boolean isDataFetched = false;

    public ProfileFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvUsername = view.findViewById(R.id.tvUsername);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);

        RelativeLayout llEdit = view.findViewById(R.id.llEdit);
        ConstraintLayout llPrivacy = view.findViewById(R.id.llPrivacy);
        ConstraintLayout llTerms = view.findViewById(R.id.llTerms);
        ConstraintLayout llRefund = view.findViewById(R.id.llRefund);

        llPrivacy.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), PrivacyPolicyActivity.class));
        });

        llEdit.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), UpdateProfileActivity.class));
        });

        llTerms.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), TermsConditionsActivity.class));
        });

        llRefund.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), RefundActivity.class));
        });

        ConstraintLayout llLogOut = view.findViewById(R.id.llLogOut);
        llLogOut.setOnClickListener(v -> logOut());

        ConstraintLayout llTransactionHistory = view.findViewById(R.id.llTransaction);
        llTransactionHistory.setOnClickListener(v -> {startActivity(new Intent(getContext(), Transaction_History_Activity.class));});

        fetchUserData();

        return view;
    }
    private void logOut() {
//        FirebaseAuth.getInstance().signOut();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("Confirm Log Out");
        alertDialogBuilder.setMessage("Are you sure you want to log out?");
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Please wait...");
        progressDialog.setMessage("Logging you out...");

        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog.show();
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
                GoogleSignIn.getClient(getContext(), gso).signOut(); // to make the Google Sign In Intent visible after logging out session.
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserPrefs", MODE_PRIVATE);
                sharedPreferences.edit().clear().apply();
                progressDialog.dismiss();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                if (getActivity() != null) {
                    getActivity().finish();
                }

            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();


    }
    public interface ApiService {
        @POST("user/get-user")
        Call<UserResponse> getUserData(@Body RequestBody requestBody);
    }
    private void fetchUserData() {
        isDataFetched = true;
        Context context = getContext(); // get the context

        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            String userId = sharedPreferences.getString("user_id", "-1");

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://postermaking.deifichrservices.com/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ApiService apiService = retrofit.create(ApiService.class);

            RequestBody requestBody = new FormBody.Builder()
                    .add("user_id", userId)
                    .build();

            Call<UserResponse> call = apiService.getUserData(requestBody);
            call.enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        UserResponse userResponse = response.body();
                        if (userResponse.getStatus() == 1 && userResponse.getData() != null && !userResponse.getData().isEmpty()) {
                            User user = userResponse.getData().get(0);
                            tvUsername.setText(user.getName());
                            tvUserEmail.setText(user.getEmail());
                        } else {
                            Toast.makeText(getContext(), "No user data found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("API", "Response Error: " + response.message());
                    }
                }
                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {
                    Log.e("API", "Request Failed: " + t.getMessage());
                    Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}