package com.example.ai_interviewer;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    View startInterviewBtn;
    View historyBtn;
    View profileBtn;

    public HomeFragment() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_home, container, false);
        View startInterviewCard = view.findViewById(R.id.startInterviewCard);

        startInterviewCard.setOnClickListener(v -> {

            Intent intent = new Intent(getActivity(), InterviewActivity.class);
            startActivity(intent);

        });

        // buttons
        startInterviewBtn = view.findViewById(R.id.startInterviewCard);
        historyBtn = view.findViewById(R.id.historyCard);
        profileBtn = view.findViewById(R.id.profileCard);

        // START INTERVIEW
        startInterviewBtn.setOnClickListener(v -> {

            Intent intent = new Intent(getActivity(), InterviewActivity.class);
            startActivity(intent);

        });

        // HISTORY TAB
        historyBtn.setOnClickListener(v -> {

            if(getActivity() instanceof MainActivity){
                ((MainActivity)getActivity()).bottomNavigationView.setSelectedItemId(R.id.nav_performance);
            }

        });

        // PROFILE TAB
        profileBtn.setOnClickListener(v -> {

            if(getActivity() instanceof MainActivity){
                ((MainActivity)getActivity()).bottomNavigationView.setSelectedItemId(R.id.nav_profile);
            }

        });

        return view;

    }
}