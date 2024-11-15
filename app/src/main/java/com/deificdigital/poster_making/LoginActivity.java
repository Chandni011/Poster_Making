package com.deificdigital.poster_making;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

        // Check if the user is a returning user

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // from google-services.json
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.clLogin).setOnClickListener(v -> signIn());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        boolean isReturningUser = sharedPreferences.getBoolean("isReturningUser", false);
        if (isReturningUser) {
            // If the user has logged in before, redirect to MainActivity
            navigateToMainActivity();
            return; // Stop further execution of onCreate
        }
    }
    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result from launching the Intent from GoogleSignInClient.getSignInIntent
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign-In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Handle sign-in error
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading...");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    String name = user.getDisplayName();
                    String email = user.getEmail();

                    sharedPreferences.edit().putBoolean("isReturningUser", true).apply();

//                    boolean isReturningUser = sharedPreferences.getBoolean("isReturningUser", false);
//
//                    Intent intent;
//                    if (isReturningUser) {
//                        // If the user is returning, navigate to UpdateProfileActivity
//                        intent = new Intent(LoginActivity.this, UpdateProfileActivity.class);
//                    } else {
//                        // If it's a new user, navigate to PersonalDetailsActivity
//                        intent = new Intent(LoginActivity.this, PersonalDetailsActivity.class);
//                    }

//                    intent.putExtra("name", name);
//                    intent.putExtra("email", email);
//                    startActivity(intent);
//                    finish();

                    Intent intent = new Intent(LoginActivity.this, PersonalDetailsActivity.class);
                    intent.putExtra("name", name);
                    intent.putExtra("email", email);
                    progressDialog.dismiss();
                    startActivity(intent);
                    finish();
                }
            } else {
                // Handle sign-in failure
            }
        });
    }
}