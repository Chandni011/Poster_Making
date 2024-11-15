package com.deificdigital.poster_making;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.deificdigital.poster_making.Adapters.TransactionDetailsAdapter;
import com.deificdigital.poster_making.models.TransactionDetails;
import com.deificdigital.poster_making.responses.TransactionDetailsResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class Transaction_History_Activity extends AppCompatActivity {

    private ImageView ivBack;
    private RecyclerView rvTransaction;
    private TransactionDetailsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_transaction_history);

        ivBack = findViewById(R.id.ivBack);
        rvTransaction = findViewById(R.id.rvTransaction);
        rvTransaction.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("user_id", "-1");

        if (!userId.equals("-1")) {
            fetchTransactionData(Integer.parseInt(userId));
        } else {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
        }

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
    }

    public interface ApiService {
        @GET("api/user-transcation/{user_id}") // Replace with your actual API endpoint
        Call<TransactionDetailsResponse> getTransactionHistory(@Path("user_id") int userId);
    }
    public static class RetrofitClient {
        private static Retrofit retrofit;

        public static Retrofit getRetrofitInstance() {
            if (retrofit == null) {
                retrofit = new Retrofit.Builder()
                        .baseUrl("https://postermaking.deifichrservices.com/") // Replace with your base URL
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
            return retrofit;
        }
    }
    private void fetchTransactionData(int userId) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<TransactionDetailsResponse> call = apiService.getTransactionHistory(userId);

        call.enqueue(new Callback<TransactionDetailsResponse>() {
            @Override
            public void onResponse(Call<TransactionDetailsResponse> call, Response<TransactionDetailsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TransactionDetails> transactions = response.body().getTransactionData();
                    adapter = new TransactionDetailsAdapter(transactions);
                    rvTransaction.setAdapter(adapter);
                } else {
                    Toast.makeText(Transaction_History_Activity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<TransactionDetailsResponse> call, Throwable t) {
                Toast.makeText(Transaction_History_Activity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
