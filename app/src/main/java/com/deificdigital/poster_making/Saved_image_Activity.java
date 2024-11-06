package com.deificdigital.poster_making;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import ja.burhanrashid52.photoeditor.PhotoEditor;

public class Saved_image_Activity extends AppCompatActivity {
    private final int REQUEST_GALLERY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_saved_image);
        ImageView ivDelete = findViewById(R.id.ivDelete);
        ImageView ivImage = findViewById(R.id.ivImage);
        ImageView ivBack = findViewById(R.id.ivBack);
        ImageView ivDownload = findViewById(R.id.ivDownload);
        Intent intent = getIntent();
        String imagePath = intent.getStringExtra("imagePath");
        if (imagePath != null) {
            if (imagePath.startsWith("content://")) {
                Glide.with(this)
                        .load(Uri.parse(imagePath))
                        .into(ivImage);
            } else {
                Glide.with(this)
                        .load(new File(imagePath))
                        .into(ivImage);
            }
        } else {
            Log.e("SavedImageActivity", "Image path is null");
        }
        ivDownload.setOnClickListener(v -> {checkPermissionAndSaveImage();});
        ivBack.setOnClickListener(v -> finish());
        ivDelete.setOnClickListener(v -> showDeleteConfirmationDialog(imagePath));
    }
    private void showDeleteConfirmationDialog(String imagePath) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Image")
                .setMessage("Are you sure you want to delete this image?")
                .setPositiveButton("OK", (dialog, which) -> {
                    Intent intent = new Intent();
                    intent.putExtra("deletedImagePath", imagePath);
                    setResult(RESULT_OK, intent);
                    finish();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }
    private void checkPermissionAndSaveImage() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            // For Android 9 and below, request WRITE_EXTERNAL_STORAGE permission
            if (ContextCompat.checkSelfPermission(Saved_image_Activity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                saveImageToGallery();
            } else {
                ActivityCompat.requestPermissions(Saved_image_Activity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_GALLERY);
            }
        } else {
            // For Android 10 and above, no permission is required to save images to MediaStore
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
        ImageView ivImage = findViewById(R.id.ivImage);
        ivImage.setDrawingCacheEnabled(true);
        ivImage.buildDrawingCache();
        Bitmap bitmap = ivImage.getDrawingCache();
        String appName = getString(R.string.app_name);
        String fileName = "IMG_" + System.currentTimeMillis() + ".jpeg";
        saveImageToExternalStorage(bitmap, appName, fileName);
        ivImage.setDrawingCacheEnabled(false);
    }
    private String saveImageToExternalStorage(Bitmap bitmap, String appName, String fileName) {
        String savedImagePath = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/" + appName);
            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri != null) {
                try (OutputStream out = getContentResolver().openOutputStream(uri)) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    savedImagePath = uri.toString();
                    Toast.makeText(this, "Image saved to gallery", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(this, "Error saving image: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
        return savedImagePath;
    }
}