package com.example.ai_interviewer;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    TextView name,email,joinDate;
    Button logoutBtn;

    FirebaseAuth auth;
    FirebaseUser user;

    public ProfileFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        name = view.findViewById(R.id.profileName);
        email = view.findViewById(R.id.profileEmail);
        joinDate = view.findViewById(R.id.joinDate);
        logoutBtn = view.findViewById(R.id.btnLogout);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if(user != null){

            String userEmail = user.getEmail();
            email.setText(userEmail);

            String username = userEmail.substring(0,userEmail.indexOf("@"));
            name.setText(username);

            long timestamp = user.getMetadata().getCreationTimestamp();

            SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
            String date = sdf.format(new Date(timestamp));

            joinDate.setText("Joined " + date);
        }

        logoutBtn.setOnClickListener(v -> {

            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        });

        return view;
    }
}