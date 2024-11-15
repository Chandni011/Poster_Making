package com.deificdigital.poster_making;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.deificdigital.poster_making.Adapters.FontAdapter;
import com.deificdigital.poster_making.classes.EditOperation;
import com.deificdigital.poster_making.classes.SharedSavedImageViewModel;
import com.deificdigital.poster_making.classes.SharedViewModel;
import com.deificdigital.poster_making.models.FontModel;
import com.deificdigital.poster_making.models.User;
import com.deificdigital.poster_making.responses.StatusResponse;
import com.deificdigital.poster_making.responses.UserResponse;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public class FullImageActivity extends AppCompatActivity {

    private PhotoEditorView mPhotoEditorView;
    private PhotoEditor mPhotoEditor;
    private ImageView ivAddImage;
    private final int REQUEST_GALLERY = 1;
    private TextView selectedTextView = null;
    private RecyclerView rvFonts;
    private FontAdapter fontAdapter;
    private List<FontModel> fontList;
    private SharedSavedImageViewModel sharedSavedImageViewModel;
    private boolean isEdited = false;
    private boolean isDataFetched = false;
    private List<EditOperation> editOperations = new ArrayList<>();
    private LottieAnimationView tickMark;

    private static final String PREFS_NAME = "SavedImagePrefs";
    private static final String KEY_IMAGE_PATHS = "image_paths";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_full_image);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        rvFonts = findViewById(R.id.rvFonts);
        rvFonts.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("user_id", "-1");

//        ImageView ivUndo = findViewById(R.id.ivUndo);
//        ImageView ivRedo =  findViewById(R.id.ivRedo);
        ImageView ivAddText = findViewById(R.id.ivAddText);
        ImageView ivDownload = findViewById(R.id.ivDownload);
        ImageView ivBack = findViewById(R.id.ivBack);
        mPhotoEditorView = findViewById(R.id.photoEditorView);

        String imagePath = getIntent().getStringExtra("image_path");
        String source = getIntent().getStringExtra("source");

        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            if (bitmap != null) {
                mPhotoEditorView.getSource().setImageBitmap(bitmap);
            } else {
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No image path provided", Toast.LENGTH_SHORT).show();
        }
        if (!"drafts".equals(source)) {
            // Only call fetchDataFromApi if not coming from the DraftFragment
            if (!isDataFetched) {
                fetchUserData();
            }
        }
        ivDownload.setOnClickListener(v -> {
            checkUserStatusAndDownload(Integer.parseInt(userId));
        });
        sharedSavedImageViewModel = new ViewModelProvider(this).get(SharedSavedImageViewModel.class);

        ivBack.setOnClickListener(v -> {
            startActivity(new Intent(FullImageActivity.this, MainActivity.class));
            finish();
        });
        ivAddText.setOnClickListener(v -> {
            EditText editText = new EditText(this);
                new AlertDialog.Builder(this)
                        .setTitle("Add Text")
                        .setView(editText)
                        .setPositiveButton("OK", (dialog, which) -> {
                            String newText = editText.getText().toString().trim();
                            if (!newText.isEmpty()) {
                                addTextToImage(newText, 100, 100, Color.WHITE, Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                            } else {
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            isEdited = true;
        });
        ImageView ivChangeFont = findViewById(R.id.ivChangeFont);
        LinearLayout llFont = findViewById(R.id.llFont);
        ImageView ivDown2 = findViewById(R.id.ivDown2);

        ivChangeFont.setOnClickListener(v -> {llFont.setVisibility(View.VISIBLE); isEdited = true;});
        ivDown2.setOnClickListener(v -> {llFont.setVisibility(View.GONE);});
        fontList = new ArrayList<>();
        fontAdapter = new FontAdapter(this, fontList, font -> {
            if (selectedTextView != null) {
                Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/" + font.getFontName() + ".ttf");
                selectedTextView.setTypeface(typeface);
            } else {
                Toast.makeText(this, "Please select a text box first", Toast.LENGTH_SHORT).show();
            }
        });
        loadFonts();
        rvFonts.setAdapter(fontAdapter);
        ImageView ivChangeColor = findViewById(R.id.ivChangeColor);
        LinearLayout llColor = findViewById(R.id.llColor);
        ImageView ivDown = findViewById(R.id.ivDown);
        LinearLayout colorPalette = findViewById(R.id.colorPalette);
        ivChangeColor.setOnClickListener(v -> {llColor.setVisibility(View.VISIBLE); isEdited = true;});
        ivDown.setOnClickListener(v -> {llColor.setVisibility(View.GONE);});
        int[] colors = {
                Color.parseColor("#ffffff"), Color.parseColor("#cccccc"), Color.parseColor("#999999"),
                Color.parseColor("#666666"), Color.parseColor("#333333"), Color.parseColor("#000000"),
                Color.parseColor("#ffee90"), Color.parseColor("#ffd700"), Color.parseColor("#daa520"),
                Color.parseColor("#b8860b"), Color.parseColor("#ccff66"), Color.parseColor("#adff2f"),
                Color.parseColor("#00fa9a"), Color.parseColor("#00ff7f"), Color.parseColor("#00ff00"),
                Color.parseColor("#32cd32"), Color.parseColor("#3cb371"), Color.parseColor("#99cccc"),
                Color.parseColor("#66cccc"), Color.parseColor("#339999"), Color.parseColor("#669999"),
                Color.parseColor("#006666"), Color.parseColor("#336666"), Color.parseColor("#ffcccc"),
                Color.parseColor("#ff9999"), Color.parseColor("#ff6666"), Color.parseColor("#ff3333"),
                Color.parseColor("#ff0033"), Color.parseColor("#cc0033"), Color.parseColor("#66021B")
        };
        for (int color : colors) {
            View colorView = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(25, LinearLayout.LayoutParams.MATCH_PARENT);
            params.setMargins(0, 0, 0, 0);
            colorView.setLayoutParams(params);
            colorView.setBackgroundColor(color);

            colorView.setOnClickListener(v -> {
                if (selectedTextView != null) {
                    selectedTextView.setTextColor(color);
                } else {
                    Toast.makeText(this, "Please select a text box first", Toast.LENGTH_SHORT).show();
                }
            });
            colorPalette.addView(colorView);
        }
        ivAddImage = findViewById(R.id.ivAddImage);
        ivAddImage.setOnClickListener(v -> {openGallery(); isEdited = true;});

        mPhotoEditorView = findViewById(R.id.photoEditorView);
        mPhotoEditor = new PhotoEditor.Builder(this, mPhotoEditorView).build();
        String imageUrl = getIntent().getStringExtra("image_url");
        loadImage(imageUrl);
//        fetchUserData();

        findViewById(R.id.main).setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                hideAllIcons();
            }
            return false;
        });
    }
    private void loadFonts() {
        fontList.add(new FontModel("arya_regular", "arya_regular"));
        fontList.add(new FontModel("calibrid", "calibrid"));
        fontList.add(new FontModel("kanit_bold", "kanit_bold"));
        fontList.add(new FontModel("kanit_semibolditalic", "kanit_semibolditalic"));
        fontList.add(new FontModel("lato_regular", "lato_regular"));
        fontList.add(new FontModel("roboto_black", "roboto_black"));
        fontList.add(new FontModel("roboto_italic", "roboto_italic"));
        fontList.add(new FontModel("yatraone_regular", "yatraone_regular"));
        fontAdapter.notifyDataSetChanged();
    }
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GALLERY);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            startCrop(imageUri);
        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            Uri croppedUri = UCrop.getOutput(data);

            new AlertDialog.Builder(this)
                    .setTitle("Remove Background")
                    .setMessage("Do you want to remove the background of this image?")
                    .setPositiveButton("Yes", (dialog, which) -> removeBackground(croppedUri))
                    .setNegativeButton("No", (dialog, which) -> addRoundedCornersToImage(croppedUri))
                    .show();
        }
    }
    private void addRoundedCornersToImage(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
            roundedBitmapDrawable.setCornerRadius(Math.min(bitmap.getWidth(), bitmap.getHeight()) / 1.0f); // Circular effect
            ImageView overlayImageView = new ImageView(this);
            overlayImageView.setImageDrawable(roundedBitmapDrawable);

            int overlayWidth = mPhotoEditorView.getWidth() / 4;
            int overlayHeight = (int) ((float) bitmap.getHeight() / bitmap.getWidth() * overlayWidth);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(overlayWidth, overlayHeight);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            overlayImageView.setLayoutParams(params);
            mPhotoEditorView.addView(overlayImageView);
            setupTouchListeners(overlayImageView);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
        }
    }
    private void startCrop(Uri uri) {
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(80);
        options.setFreeStyleCropEnabled(true);
        UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), "cropped_image.jpg")))
                .withOptions(options)
                .start(this);
    }
    private void fetchUserData() {
        isDataFetched = true;
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("user_id", "-1");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://postermaking.deifichrservices.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);

        RequestBody requestBody = new FormBody.Builder()
                .add("user_id",(userId))
                .build();

        Call<UserResponse> call = apiService.getUserData(requestBody);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse userResponse = response.body();
                    if (userResponse.getStatus() == 1 && userResponse.getData() != null && !userResponse.getData().isEmpty()) {
                        User user = userResponse.getData().get(0);
                        addUserDetailsToImage(user);
//                        String userId = String.valueOf(userResponse.getData().get(0).getId());
//                        // Proceed with checking the premium status
//                        checkPremiumStatus(userId, post_name);
                    } else {
                        Toast.makeText(FullImageActivity.this, "No user data found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("API", "Response Error: " + response.message());
                }
            }
            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.e("API", "Request Failed: " + t.getMessage());
                Toast.makeText(FullImageActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadImage(String imageUrl) {
        Glide.with(this)
                .load(imageUrl)
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        mPhotoEditorView.getSource().setImageDrawable(resource);
                    }
                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }
    private void addUserDetailsToImage(User user) {
        int textColor = Color.WHITE;
        Typeface typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD);
        addTextToImage("" + user.getName(), 50, 600, textColor, typeface);
        addTextToImage("" + user.getDesignation(), 50, 640, textColor, typeface);
        addTextToImage("" + user.getPhone(), 50, 680, textColor, typeface);
        addTextToImage("" + user.getCompany_name(), 400, 680, textColor, typeface);
    }
    private void addTextToImage(String text, int x, int y, int textColor, Typeface typeface) {
        View container = getLayoutInflater().inflate(R.layout.text_box_layout, mPhotoEditorView, false);

        TextView textView = container.findViewById(R.id.textBoxText);
        View deleteIcon = container.findViewById(R.id.deleteIcon);
        View resizeIcon = container.findViewById(R.id.resizeIcon);
        RelativeLayout textBoxLayout = container.findViewById(R.id.textBoxLayout);

        textView.setText(text);
        textView.setTextColor(textColor);
        textView.setTypeface(typeface);
        deleteIcon.setVisibility(View.GONE);
        resizeIcon.setVisibility(View.GONE);
        textBoxLayout.setBackgroundColor(Color.TRANSPARENT);

        RelativeLayout.LayoutParams containerParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        containerParams.leftMargin = x;
        containerParams.topMargin = y;
        mPhotoEditorView.addView(container, containerParams);

        deleteIcon.setOnClickListener(v -> mPhotoEditorView.removeView(container));
        textView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (selectedTextView != null) {
                    selectedTextView.setBackground(null);
                }
                selectedTextView = textView;

                deleteIcon.setVisibility(View.VISIBLE);
                resizeIcon.setVisibility(View.VISIBLE);
                textBoxLayout.setBackgroundResource(R.drawable.border);
            }
            return true;
        });
        ImageView ivEditText = findViewById(R.id.ivEditText);
        ivEditText.setOnClickListener(v -> {
            if (selectedTextView != null) {
                showEditTextDialog();
            }
            else{
                Toast.makeText(this, "Please select any text.", Toast.LENGTH_SHORT).show();
            }
            isEdited = true;
        });
        resizeIcon.setOnTouchListener(new View.OnTouchListener() {
            private float initialY;
            private float initialTextSize;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialY = event.getRawY();
                        initialTextSize = textView.getTextSize();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        float deltaY = event.getRawY() - initialY;
                        float newSize = initialTextSize + deltaY / 10;
                        if (newSize > 10 && newSize < 100) {
                            textView.setTextSize(newSize / getResources().getDisplayMetrics().scaledDensity);
                        }
                        break;
                }
                return true;
            }
        });
        container.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (deleteIcon.getVisibility() == View.GONE) {
                    deleteIcon.setVisibility(View.VISIBLE);
                    resizeIcon.setVisibility(View.VISIBLE);
                    textBoxLayout.setBackgroundColor(Color.TRANSPARENT);
                } else {
                    deleteIcon.setVisibility(View.GONE);
                    resizeIcon.setVisibility(View.GONE);
                    textBoxLayout.setBackgroundColor(Color.TRANSPARENT);
                }
            }
            return true;
        });
        container.setOnTouchListener(new View.OnTouchListener() {
            private float dX, dY;
            private int lastAction;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Drawable drawable = mPhotoEditorView.getSource().getDrawable();
                if (drawable == null) return false;

                int intrinsicWidth = drawable.getIntrinsicWidth();
                int intrinsicHeight = drawable.getIntrinsicHeight();
                float scaleX = (float) mPhotoEditorView.getWidth() / (float) intrinsicWidth;
                float scaleY = (float) mPhotoEditorView.getHeight() / (float) intrinsicHeight;
                float scale = Math.min(scaleX, scaleY);
                int actualImageWidth = (int) (intrinsicWidth * scale);
                int actualImageHeight = (int) (intrinsicHeight * scale);
                int offsetX = (mPhotoEditorView.getWidth() - actualImageWidth) / 2;
                int offsetY = (mPhotoEditorView.getHeight() - actualImageHeight) / 2;
                int viewWidth = v.getWidth();
                int viewHeight = v.getHeight();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dX = v.getX() - event.getRawX();
                        dY = v.getY() - event.getRawY();
                        lastAction = MotionEvent.ACTION_DOWN;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float newX = event.getRawX() + dX;
                        float newY = event.getRawY() + dY;
                        if (newX < offsetX) newX = offsetX;
                        if (newX + viewWidth > offsetX + actualImageWidth) newX = offsetX + actualImageWidth - viewWidth;
                        if (newY < offsetY) newY = offsetY;
                        if (newY + viewHeight > offsetY + actualImageHeight) newY = offsetY + actualImageHeight - viewHeight;
                        v.animate().x(newX).y(newY).setDuration(0).start();
                        lastAction = MotionEvent.ACTION_MOVE;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (lastAction == MotionEvent.ACTION_DOWN) {
                        }
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
    }
    private void hideAllIcons() {
        for (int i = 0; i < mPhotoEditorView.getChildCount(); i++) {
            View child = mPhotoEditorView.getChildAt(i);
            if (child instanceof RelativeLayout) {
                View deleteIcon = child.findViewById(R.id.deleteIcon);
                View resizeIcon = child.findViewById(R.id.resizeIcon);
                RelativeLayout textBoxLayout = child.findViewById(R.id.textBoxLayout);
                if (deleteIcon != null && resizeIcon != null) {
                    deleteIcon.setVisibility(View.GONE);
                    resizeIcon.setVisibility(View.GONE);
                    textBoxLayout.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        }
        selectedTextView = null;
    }
    public interface RemoveBgApiService {
        @Multipart
        @POST("removebg")
        Call<ResponseBody> removeBackground(
                @Part MultipartBody.Part image,
                @Part("size") RequestBody size
        );
    }
    public static class RetrofitClient {
        private static final String BASE_URL = "https://api.remove.bg/v1.0/";
        private static Retrofit retrofit;

        public static RemoveBgApiService getRemoveBgApiService() {
            if (retrofit == null) {
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);

                OkHttpClient client = new OkHttpClient.Builder()
                        .addInterceptor(logging)
                        .addInterceptor(chain -> {
                            return chain.proceed(chain.request().newBuilder()
                                    .addHeader("X-Api-Key", "4ogFA9kxNSw8aPx1RVoUbhDE") // Replace with your Remove.bg API key
                                    .build());
                        })
                        .build();

                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
            return retrofit.create(RemoveBgApiService.class);
        }
    }
    private void removeBackground(Uri imageUri) {
        File imageFile = new File(imageUri.getPath());
        if (!imageFile.exists()) {
            Log.e("RemoveBg", "File does not exist");
            return;
        }
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image_file", imageFile.getName(), requestFile);
        RequestBody size = RequestBody.create(MediaType.parse("text/plain"), "auto");
        RemoveBgApiService apiService = RetrofitClient.getRemoveBgApiService();

        Call<ResponseBody> call = apiService.removeBackground(body, size);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        byte[] imageBytes = response.body().bytes();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                        ImageView overlayImageView = new ImageView(FullImageActivity.this);
                        overlayImageView.setImageBitmap(bitmap);

                        int overlayWidth = mPhotoEditorView.getWidth() / 4;
                        int overlayHeight = (int) ((float) bitmap.getHeight() / bitmap.getWidth() * overlayWidth);
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(overlayWidth, overlayHeight);

                        params.addRule(RelativeLayout.CENTER_IN_PARENT);
                        overlayImageView.setLayoutParams(params);
                        mPhotoEditorView.addView(overlayImageView);
                        setupTouchListeners(overlayImageView);

                        Toast.makeText(FullImageActivity.this, "Background removed!", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Log.e("RemoveBg", "Error processing response: " + e.getMessage());
                    }
                } else {
                    Log.e("RemoveBg", "Failed response: " + response.message());
                    Toast.makeText(FullImageActivity.this, "Failed to remove background", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("RemoveBg", "API call failed: " + t.getMessage());
                Toast.makeText(FullImageActivity.this, "Failed to connect to Remove.bg", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setupTouchListeners(ImageView overlayImageView) {
        ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float scaleFactor = detector.getScaleFactor();
                overlayImageView.setScaleX(overlayImageView.getScaleX() * scaleFactor);
                overlayImageView.setScaleY(overlayImageView.getScaleY() * scaleFactor);
                return true;
            }
        });
        overlayImageView.setOnTouchListener(new View.OnTouchListener() {
            private float dX, dY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                scaleGestureDetector.onTouchEvent(event);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dX = v.getX() - event.getRawX();
                        dY = v.getY() - event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        v.animate()
                                .x(event.getRawX() + dX)
                                .y(event.getRawY() + dY)
                                .setDuration(0)
                                .start();
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
    }
    public interface ApiService {
        @POST("user/get-user")
        Call<UserResponse> getUserData(@Body RequestBody requestBody);
    }
    private void showEditTextDialog() {
        EditText editText = new EditText(this);
        editText.setText(selectedTextView.getText().toString());

        new AlertDialog.Builder(this)
                .setTitle("Edit Text")
                .setView(editText)
                .setPositiveButton("OK", (dialog, which) -> {
                    selectedTextView.setText(editText.getText().toString());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    private void checkPermissionAndSaveImage() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(FullImageActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                saveImageToGallery();
            } else {
                ActivityCompat.requestPermissions(FullImageActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_GALLERY);
            }
        } else {
            saveImageToGallery();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_GALLERY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveImageToGallery();
            } else {
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void saveImageToGallery() {
        String appName = getString(R.string.app_name);
        String fileName = "IMG_" + System.currentTimeMillis() + ".jpeg";
        String filePath = getFilesDir() + "/edited_image.jpeg";
        try {
            mPhotoEditor.saveAsFile(filePath, new PhotoEditor.OnSaveListener() {
                @Override
                public void onSuccess(@NonNull String s) {
                    String savedImagePath = saveImageToExternalStorage(s, appName, fileName);
                    if (savedImagePath != null) {
                        saveImagePathToPrefs(savedImagePath);
                    }
                }
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(FullImageActivity.this, "Failed to save image: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private String saveImageToExternalStorage(String imagePath, String appName, String fileName) {
        String savedImagePath = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/" + appName);

            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri != null) {
                try (OutputStream out = getContentResolver().openOutputStream(uri);
                     FileInputStream fis = new FileInputStream(imagePath)) {
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = fis.read(buffer)) > 0) {
                        out.write(buffer, 0, len);
                    }
                    savedImagePath = uri.toString();
                    Toast.makeText(this, "Image saved to gallery", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(this, "Error saving image: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
        return savedImagePath;
    }
    private void saveImagePathToPrefs(String imagePath) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String existingPaths = prefs.getString(KEY_IMAGE_PATHS, "");
        if (existingPaths.isEmpty()) {
            existingPaths = imagePath;
        } else {
            existingPaths += "," + imagePath;
        }
        prefs.edit().putString(KEY_IMAGE_PATHS, existingPaths).apply();
    }
    public static class retrofitClientCheck {
        private static Retrofit instance;
        private static final String BASE_URL = "https://postermaking.deifichrservices.com/";

        private retrofitClientCheck() {}

        public static Retrofit getInstance() {
            if (instance == null) {
                instance = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
            return instance;
        }
    }
    public interface ApiServiceStatus {
        @POST("api/check-limit")
        Call<StatusResponse> checkPremiumStatus(@Body Map<String, String> params);
    }
    String post_name = "qwe";

    private void checkUserStatusAndDownload(int userId) {
        fetchUserId(userId);
    }
    private void fetchUserId(int userId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://postermaking.deifichrservices.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UpdateProfileActivity.ApiService apiService = retrofit.create(UpdateProfileActivity.ApiService.class);
        RequestBody requestBody = new FormBody.Builder()
                .add("user_id", String.valueOf(userId))
                .build();
        Request request = new Request.Builder()
                .url("https://postermaking.deifichrservices.com/api/user/getData")
                .post(requestBody)
                .build();   Call<UserResponse> call = apiService.getUserData(requestBody);

        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse userResponse = response.body();
                    if (userResponse.getStatus() == 1 && userResponse.getData() != null && !userResponse.getData().isEmpty()) {
                        String userId = String.valueOf(userResponse.getData().get(0).getId());
                        checkPremiumStatus(userId, post_name);
                    } else {
                        Toast.makeText(FullImageActivity.this, "User not found or error fetching data", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(FullImageActivity.this, "Failed to fetch user data. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(FullImageActivity.this, "Network error while fetching user data", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void checkPremiumStatus(String userId, String post_name) {
        Map<String, String> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("post_name", post_name);

        ApiServiceStatus apiService = retrofitClientCheck.getInstance().create(ApiServiceStatus.class);
        Call<StatusResponse> call = apiService.checkPremiumStatus(params);
        call.enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    StatusResponse apiResponse = response.body();
                    if (apiResponse.getStatus() == 1) {
                        // Premium status is valid, proceed with saving the image
                        playTickAnimation();
                        checkPermissionAndSaveImage();
                    } else {
                        startActivity(new Intent(FullImageActivity.this, Premium_Description_Activity.class));
                        finish();
//                        Toast.makeText(FullImageActivity.this, "Please take premium subscription. User ID: " + userId, Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Log the error details to understand why it's failing
                    Log.e("API Error", "Status Code: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            String errorResponse = response.errorBody().string();
                            Log.e("API Error", "Error Response: " + errorResponse);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(FullImageActivity.this, "Failed to check status. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                // Handle failure
                Toast.makeText(FullImageActivity.this, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void playTickAnimation() {
        CardView llLottie = findViewById(R.id.llLottie);
        LottieAnimationView tickAnimation = findViewById(R.id.tickAnimation);

        llLottie.setVisibility(View.VISIBLE);
        tickAnimation.setVisibility(View.VISIBLE);
        tickAnimation.playAnimation();

        new Handler().postDelayed(() -> llLottie.setVisibility(View.GONE), 2000);
    }
    @Override
    public void onBackPressed() {
        if (isEdited) {
            Bitmap bitmap = Bitmap.createBitmap(mPhotoEditorView.getWidth(), mPhotoEditorView.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            mPhotoEditorView.draw(canvas);

            String filePath = saveBitmapToFile(bitmap);
            saveImagePathToPreferences(filePath);

            SharedViewModel sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
            sharedViewModel.setSavedImagePath(filePath);
        }
        Intent intent = new Intent(FullImageActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    private String saveBitmapToFile(Bitmap bitmap) {
        try {
            String uniqueFileName = "edited_image_" + System.currentTimeMillis() + ".png";
            File file = new File(getCacheDir(), uniqueFileName);
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private void saveImagePathToPreferences(String imagePath) {
        SharedPreferences sharedDraftPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedDraftPreferences.getString("image_paths", "[]");
        List<String> imagePaths = gson.fromJson(json, new TypeToken<List<String>>() {}.getType());
        if (!imagePaths.contains(imagePath)) {
            imagePaths.add(imagePath);
        }
        SharedPreferences.Editor editor = sharedDraftPreferences.edit();
        editor.putString("image_paths", gson.toJson(imagePaths));
        editor.apply();
    }
}