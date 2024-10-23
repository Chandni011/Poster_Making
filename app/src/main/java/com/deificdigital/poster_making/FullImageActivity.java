package com.deificdigital.poster_making;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.imageview.ShapeableImageView;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.TextStyleBuilder;

public class FullImageActivity extends AppCompatActivity {

    private PhotoEditorView mPhotoEditorView;
    private PhotoEditor mPhotoEditor;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int REQUEST_STORAGE_PERMISSION = 1;

    private boolean isCircularCrop = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        ImageView ivAddImage = findViewById(R.id.ivAddImage);
        ImageView ivDownload = findViewById(R.id.ivDownload);

        ivAddImage.setOnClickListener(v -> openGallery());
        ivDownload.setOnClickListener(v -> {checkPermissionAndSaveImage();});

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mPhotoEditorView = findViewById(R.id.photoEditorView);
        ImageView ivAddText = findViewById(R.id.ivAddText);

        // Initialize PhotoEditor
        mPhotoEditor = new PhotoEditor.Builder(this, mPhotoEditorView)
                .setPinchTextScalable(true) // Enable pinch to zoom for text
                .build();

        String imageUrl = getIntent().getStringExtra("image_url");
        Glide.with(this).load(imageUrl).into(mPhotoEditorView.getSource());

        ivAddText.setOnClickListener(v -> showAddTextDialog(null, -1, -1, null));
    }

    private void showAddTextDialog(String existingText, int existingTextSize, int existingTextColor, TextView existingTextView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(existingText == null ? "Add Text" : "Edit Text");

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_text, null);
        builder.setView(dialogView);

        final EditText input = dialogView.findViewById(R.id.inputText);
        final TextView previewText = dialogView.findViewById(R.id.previewText);
        final SeekBar textSizeSeekBar = dialogView.findViewById(R.id.textSizeSeekBar);
        final SeekBar colorSeekBar = dialogView.findViewById(R.id.colorSeekBar);

        if (existingText != null) {
            input.setText(existingText);
            previewText.setText(existingText);
            textSizeSeekBar.setProgress(existingTextSize > 0 ? existingTextSize : 30); // Default size 30
            colorSeekBar.setProgress(getSeekBarProgressFromColor(existingTextColor));
            previewText.setTextSize(existingTextSize);
            previewText.setTextColor(existingTextColor);
        }

        textSizeSeekBar.setEnabled(existingText != null);
        colorSeekBar.setEnabled(existingText != null);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String userText = input.getText().toString();
            int textSize = textSizeSeekBar.getProgress();
            int color = getColorFromSeekBarValue(colorSeekBar.getProgress());

            if (!userText.isEmpty()) {
                if (existingTextView != null) {
                    // Update the existing text
                    existingTextView.setText(userText);
                    existingTextView.setTextSize(textSize);
                    existingTextView.setTextColor(color);
                } else {
                    // Add new text
                    addTextToImage(userText, textSize, color);
                }
            } else {
                Toast.makeText(FullImageActivity.this, "Please enter some text", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                previewText.setText(s.toString());
                boolean hasText = s.length() > 0;
                textSizeSeekBar.setEnabled(hasText);
                colorSeekBar.setEnabled(hasText);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        textSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                previewText.setTextSize(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        colorSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int color = getColorFromSeekBarValue(progress);
                previewText.setTextColor(color);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        builder.show();
    }

    private int getColorFromSeekBarValue(int progress) {
        if (progress < 100) {
            return interpolateColor(Color.WHITE, Color.BLACK, progress / 100f);
        } else if (progress < 200) {
            return interpolateColor(Color.BLACK, Color.YELLOW, (progress - 100) / 100f);
        } else if (progress < 300) {
            return interpolateColor(Color.YELLOW, Color.GREEN, (progress - 200) / 100f);
        } else if (progress < 400) {
            return interpolateColor(Color.GREEN, Color.CYAN, (progress - 300) / 100f);
        } else if (progress < 500) {
            return interpolateColor(Color.CYAN, Color.BLUE, (progress - 400) / 100f);
        } else if (progress < 600) {
            return interpolateColor(Color.BLUE, Color.rgb(0, 51, 102), (progress - 500) / 100f);
        } else if (progress < 700) {
            return interpolateColor(Color.rgb(0, 51, 102), Color.rgb(255, 128, 128), (progress - 600) / 100f);
        } else {
            return interpolateColor(Color.rgb(255, 128, 128), Color.RED, (progress - 700) / 300f);
        }
    }

    private int interpolateColor(int color1, int color2, float fraction) {
        fraction = Math.min(1f, Math.max(0f, fraction)); // Clamp fraction between 0 and 1
        int red1 = Color.red(color1);
        int green1 = Color.green(color1);
        int blue1 = Color.blue(color1);
        int red2 = Color.red(color2);
        int green2 = Color.green(color2);
        int blue2 = Color.blue(color2);

        int red = (int) (red1 + (red2 - red1) * fraction);
        int green = (int) (green1 + (green2 - green1) * fraction);
        int blue = (int) (blue1 + (blue2 - blue1) * fraction);

        return Color.rgb(red, green, blue);
    }

    private int getSeekBarProgressFromColor(int color) {
        return 0;
    }

    private void addTextToImage(String text, int textSize, int textColor) {
        TextStyleBuilder textStyleBuilder = new TextStyleBuilder();
        textStyleBuilder.withTextColor(textColor);
        textStyleBuilder.withTextSize((float) textSize);

        mPhotoEditor.addText(text, textStyleBuilder);

        View addedTextView = mPhotoEditorView.getChildAt(mPhotoEditorView.getChildCount() - 1);

        if (addedTextView instanceof TextView) {
            TextView textView = (TextView) addedTextView;
            textView.setOnClickListener(v -> {
                String currentText = textView.getText().toString();
                int currentTextColor = textView.getCurrentTextColor();
                float currentTextSize = textView.getTextSize();

                showAddTextDialog(currentText, (int) currentTextSize, currentTextColor, textView);
            });

            textView.setFocusable(true);
            textView.setClickable(true);
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_CODE) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    showShapeSelectionDialog(selectedImageUri);
                } else {
                    Toast.makeText(this, "Failed to get image", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == UCrop.REQUEST_CROP) {
                final Uri resultUri = UCrop.getOutput(data);
                if (resultUri != null) {
                    clearGalleryOverlays(); // Clear any existing overlays
                    overlayCroppedImage(resultUri, isCircularCrop); // Use the member variable
                }
            } else if (requestCode == UCrop.RESULT_ERROR) {
                final Throwable cropError = UCrop.getError(data);
                if (cropError != null) {
                    Toast.makeText(this, "Crop error: " + cropError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void showShapeSelectionDialog(Uri imageUri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Image Shape");

        String[] options = {"Circular", "Square"};

        builder.setItems(options, (dialog, which) -> {
            Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "cropped_image_" + System.currentTimeMillis() + ".jpg"));

            isCircularCrop = (which == 0);

            UCrop.Options option = new UCrop.Options();

            if (isCircularCrop) {
                option.setCircleDimmedLayer(true);
                option.setShowCropFrame(false);
                option.setShowCropGrid(false);
            } else {
                option.setCircleDimmedLayer(false);
                option.setShowCropFrame(true);
                option.setShowCropGrid(true);
            }

            UCrop.of(imageUri, destinationUri)
                    .withAspectRatio(1, 1)
                    .withMaxResultSize(1080, 1080)
                    .withOptions(option)
                    .start(this);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    private void overlayCroppedImage(Uri imageUri, boolean isCircular) {
        Glide.with(this)
                .load(imageUri)
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        ShapeableImageView overlayImageView = new ShapeableImageView(FullImageActivity.this);

                        overlayImageView.setImageDrawable(resource);
                        overlayImageView.setLayoutParams(new ViewGroup.LayoutParams(200, 200));

                        if (isCircular) {
                            overlayImageView.setShapeAppearanceModel(
                                    overlayImageView.getShapeAppearanceModel()
                                            .toBuilder()
                                            .setAllCorners(com.google.android.material.shape.CornerFamily.ROUNDED, 100f) // Full circle
                                            .build()
                            );
                        }
                        else
                        {
                            overlayImageView.setShapeAppearanceModel(
                                    overlayImageView.getShapeAppearanceModel()
                                            .toBuilder()
                                            .setAllCorners(com.google.android.material.shape.CornerFamily.ROUNDED, 0f) // No rounding
                                            .build()
                            );
                        }

                        overlayImageView.setTag("gallery_overlay");

                        // Initialize touch and scaling listeners
                        overlayImageView.setOnTouchListener(new View.OnTouchListener() {
                            float dX, dY;
                            ScaleGestureDetector scaleGestureDetector;
                            float scale = 1f;

                            {
                                scaleGestureDetector = new ScaleGestureDetector(FullImageActivity.this, new ScaleListener(overlayImageView));
                            }

                            @Override
                            public boolean onTouch(View view, MotionEvent event) {
                                scaleGestureDetector.onTouchEvent(event);

                                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                                    case MotionEvent.ACTION_DOWN:
                                        dX = view.getX() - event.getRawX();
                                        dY = view.getY() - event.getRawY();
                                        break;
                                    case MotionEvent.ACTION_MOVE:
                                        view.animate()
                                                .x(event.getRawX() + dX)
                                                .y(event.getRawY() + dY)
                                                .setDuration(0)
                                                .start();
                                        break;
                                    case MotionEvent.ACTION_UP:
                                        view.setOnClickListener(v -> {
                                            // Remove view on click
                                            ((ViewGroup) v.getParent()).removeView(v);
                                        });
                                        break;
                                    default:
                                        return false;
                                }
                                return true;
                            }

                            class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
                                private final View view;

                                ScaleListener(View view) {
                                    this.view = view;
                                }

                                @Override
                                public boolean onScale(ScaleGestureDetector detector) {
                                    scale *= detector.getScaleFactor();
                                    scale = Math.max(0.1f, Math.min(scale, 5.0f)); // Limit scale range
                                    view.setScaleX(scale);
                                    view.setScaleY(scale);
                                    return true;
                                }
                            }
                        });

                        mPhotoEditorView.addView(overlayImageView);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Handle placeholder if needed
                    }
                });
    }

    private void clearGalleryOverlays() {
        for (int i = mPhotoEditorView.getChildCount() - 1; i >= 0; i--) {
            View child = mPhotoEditorView.getChildAt(i);
            if ("gallery_overlay".equals(child.getTag())) {
                mPhotoEditorView.removeView(child);
            }
        }
    }

    private void checkPermissionAndSaveImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
            } else {
                saveImageToGallery();
            }
        } else {
            saveImageToGallery();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveImageToGallery();
            } else {
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveImageToGallery() {
        String appName = getString(R.string.app_name);
        String fileName = "IMG_" + System.currentTimeMillis() + ".jpg";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/" + appName);

            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri != null) {
                // Save image using PhotoEditor
                mPhotoEditor.saveAsFile(uri.getPath(), new PhotoEditor.OnSaveListener() {
                    @Override
                    public void onSuccess(@NonNull String imagePath) {
                        Toast.makeText(FullImageActivity.this, "Image saved", Toast.LENGTH_SHORT).show();

                        // Make the image visible in the user's gallery
                        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        mediaScanIntent.setData(uri);
                        sendBroadcast(mediaScanIntent);
                    }

                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(FullImageActivity.this, "Failed to save image", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            // Fallback for Android 9 (API 28) and below
            File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), appName);
            if (!folder.exists()) {
                folder.mkdirs(); // Create the folder if it doesn't exist
            }

            File file = new File(folder, fileName);

            mPhotoEditor.saveAsFile(file.getAbsolutePath(), new PhotoEditor.OnSaveListener() {
                @Override
                public void onSuccess(@NonNull String filePath) {
                    Toast.makeText(FullImageActivity.this, "Image saved", Toast.LENGTH_SHORT).show();

                    // Make the image visible in the user's gallery
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri contentUri = Uri.fromFile(file);
                    mediaScanIntent.setData(contentUri);
                    sendBroadcast(mediaScanIntent);
                }

                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(FullImageActivity.this, "Failed to save image", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
