package com.deificdigital.poster_making.fragments;

import static android.content.Intent.getIntent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.deificdigital.poster_making.LoginActivity;
import com.deificdigital.poster_making.PersonalDetailsActivity;
import com.deificdigital.poster_making.PrivacyPolicyActivity;
import com.deificdigital.poster_making.R;
import com.deificdigital.poster_making.RefundActivity;
import com.deificdigital.poster_making.TermsConditionsActivity;
import com.deificdigital.poster_making.UpdateProfileActivity;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class ProfileFragment extends Fragment {

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

        LinearLayout llEdit = view.findViewById(R.id.llEdit);
        LinearLayout llPrivacy = view.findViewById(R.id.llPrivacy);
        LinearLayout llTerms = view.findViewById(R.id.llTerms);
        LinearLayout llRefund = view.findViewById(R.id.llRefund);

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

        LinearLayout llLogOut = view.findViewById(R.id.llLogOut);
        llLogOut.setOnClickListener(v -> logOut());

        return view;
    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        if (getActivity() != null) {
            getActivity().finish();
        }
    }

}