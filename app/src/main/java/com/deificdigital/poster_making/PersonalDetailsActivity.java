package com.deificdigital.poster_making;

import android.content.Intent;
import android.content.SharedPreferences; // Import SharedPreferences
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.deificdigital.poster_making.models.login_model;
import com.deificdigital.poster_making.models.ResponseModel;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class PersonalDetailsActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;

    private ImageView ivDp;
    private String profilePicBase64;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_details);

        ivDp = findViewById(R.id.ivDp);
        RelativeLayout rlProfilePhoto = findViewById(R.id.rlProfilePhoto);
        AppCompatButton btnSave = findViewById(R.id.btnSave);

        EditText etNumber = findViewById(R.id.etNumber);
        EditText etName = findViewById(R.id.etName);
        EditText etEmail = findViewById(R.id.etEmail);
        EditText etDesignation = findViewById(R.id.etDesignation);
        EditText etCompanyName = findViewById(R.id.etCompanyName);

        etEmail.setEnabled(false);

        String name = getIntent().getStringExtra("name");
        String email = getIntent().getStringExtra("email");

        etName.setText(name);
        etEmail.setText(email);

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        rlProfilePhoto.setOnClickListener(v -> showImagePickerDialog());

        btnSave.setOnClickListener(v -> {
            String Name = etName.getText().toString().trim();
            String Email = etEmail.getText().toString().trim();
            String number = etNumber.getText().toString().trim();
            String designation = etDesignation.getText().toString().trim();
            String companyName = etCompanyName.getText().toString().trim();
            String login_with = "gmail"; // Example value
            String profile_pic = profilePicBase64 != null ? profilePicBase64 : "base64_string_of_image"; // Use Base64 string

            if (Name.isEmpty()) {
                etName.setError("Name is required");
                etName.requestFocus();
                return;
            }

            if (Email.isEmpty()) {
                etEmail.setError("Email is required");
                etEmail.requestFocus();
                return;
            }

            if (number.isEmpty()) {
                etNumber.setError("Number is required");
                etNumber.requestFocus();
                return;
            }

            login_model user = new login_model(name, email, number, login_with, profile_pic);
            user.setCompanyName(companyName);
            user.setDesignation(designation);

            sendUserData(user);
        });
    }

    private void sendUserData(login_model user) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://postermaking.deifichrservices.com/api/") // Replace with your base URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<ResponseModel> call = apiService.saveUserData(user);
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseModel responseModel = response.body();
                    Log.d("API", "User created: " + responseModel.getMessage());
                    Log.d("API", "User ID: " + responseModel.getUser_id());

                    int userId = responseModel.getUser_id();
                    SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("user_id", userId);
                    editor.apply();

                    Log.d("API", "User ID saved: " + userId); // Log saved ID for debugging

                    Intent intent = new Intent(PersonalDetailsActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.e("API", "Response Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Log.e("API", "Request Failed: " + t.getMessage());
            }
        });
    }

    public interface ApiService {
        @POST("user/store")
        Call<ResponseModel> saveUserData(@Body login_model user);
    }

    private void showImagePickerDialog() {
        String[] options = {"Gallery"};
        new AlertDialog.Builder(this)
                .setTitle("Choose Image Source")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent, PICK_IMAGE);
                    }
                })
                .show();
    }

    private String convertImageToBase64(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE && data != null) {
            Uri selectedImage = data.getData();
            profilePicBase64 = convertImageToBase64(selectedImage);
            Glide.with(this)
                    .load(selectedImage)
                    .apply(RequestOptions.circleCropTransform())
                    .into(ivDp);
        }
    }
}
