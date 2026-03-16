package com.example.ai_interviewer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PerformanceFragment extends Fragment {

    TextView resultText;
    TextView historyText;

    DatabaseReference resultsRef;

    public PerformanceFragment() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_performance, container, false);

        resultText = view.findViewById(R.id.resultText);
        historyText = view.findViewById(R.id.historyText);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return view;
        }

        String userId = auth.getCurrentUser().getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance(
                "https://aiinterviewerapp-a911e-default-rtdb.asia-southeast1.firebasedatabase.app"
        );

        resultsRef = database.getReference("InterviewResults").child(userId);

        loadLatestPerformance();
        loadInterviewHistory();

        return view;
    }

    private void loadLatestPerformance() {

        resultsRef.limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot data : snapshot.getChildren()) {

                    Integer score = data.child("score").getValue(Integer.class);
                    Integer correct = data.child("correct").getValue(Integer.class);
                    Integer wrong = data.child("wrong").getValue(Integer.class);
                    String skill = data.child("skill").getValue(String.class);

                    if (score == null) score = 0;
                    if (correct == null) correct = 0;
                    if (wrong == null) wrong = 0;

                    String performance = getPerformance(score);

                    String result =
                            "Latest Interview\n\n" +
                                    "Skill: " + skill + "\n\n" +
                                    "Score: " + score + "/100\n\n" +
                                    "Correct Answers: " + correct + "\n" +
                                    "Wrong Answers: " + wrong + "\n\n" +
                                    "Performance: " + performance;

                    resultText.setText(result);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                resultText.setText("Unable to load performance");
            }
        });
    }

    private void loadInterviewHistory() {

        resultsRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                StringBuilder history = new StringBuilder();

                history.append("\nPrevious Interviews\n\n");

                for (DataSnapshot data : snapshot.getChildren()) {

                    String skill = data.child("skill").getValue(String.class);
                    Integer score = data.child("score").getValue(Integer.class);

                    history.append("Skill: ").append(skill).append("\n");
                    history.append("Score: ").append(score).append("/100\n");
                    history.append("----------------------\n\n");
                }

                historyText.setText(history.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                historyText.setText("Failed to load history");
            }
        });
    }

    private String getPerformance(int score) {

        if (score >= 80) {
            return "Excellent";
        }
        else if (score >= 60) {
            return "Good";
        }
        else if (score >= 40) {
            return "Average";
        }
        else {
            return "Needs Improvement";
        }
    }
}
