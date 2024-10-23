package com.deificdigital.poster_making.fragments;

import static android.content.Intent.getIntent;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.deificdigital.poster_making.PersonalDetailsActivity;
import com.deificdigital.poster_making.R;
import com.deificdigital.poster_making.UpdateProfileActivity;

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

        llEdit.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), UpdateProfileActivity.class));
//            getActivity().finish();
        });
        return view;

    }
}