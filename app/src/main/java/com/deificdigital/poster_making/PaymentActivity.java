package com.deificdigital.poster_making;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

public class PaymentActivity extends AppCompatActivity implements PaymentResultListener {

    private TextView txtPaymentStatus;
    private Button btnPayNow;
    private EditText editAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment);
        txtPaymentStatus = findViewById(R.id.paymentStatus);
        btnPayNow = findViewById(R.id.btn_pay);
        editAmount = findViewById(R.id.edit_amount);

        editAmount.setEnabled(false);
        Checkout.preload(PaymentActivity.this);

        String price = getIntent().getStringExtra("price");
        if (price != null) {
            editAmount.setText(price);
        }
        btnPayNow.setOnClickListener(v -> {
            startPayment(Double.parseDouble(editAmount.getText().toString()));
        });
    }
    public void startPayment(double amount) {
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_BkaNzhJLHuLxPL");
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", "RazorPay Demo");
            jsonObject.put("description", "This is your payment.");
            jsonObject.put("theme.color", "#3399cc");
            jsonObject.put("Currency", "INR");

            // Convert amount to integer cents by multiplying and casting
            jsonObject.put("amount", (int) (amount * 100));

            JSONObject retryObj = new JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            jsonObject.put("retry", retryObj);

            checkout.open(PaymentActivity.this, jsonObject);
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong!!", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onPaymentSuccess(String s) {
        txtPaymentStatus.setText(s);
    }
    @Override
    public void onPaymentError(int i, String s) {
        txtPaymentStatus.setText("Error :"+ s);
    }
}