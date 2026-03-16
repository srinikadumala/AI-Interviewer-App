package com.example.ai_interviewer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class InterviewActivity extends AppCompatActivity {

    View javaCard, pythonCard, dsaCard, cCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interview);

        javaCard = findViewById(R.id.javaCard);
        pythonCard = findViewById(R.id.pythonCard);
        dsaCard = findViewById(R.id.dsaCard);
        cCard = findViewById(R.id.cCard);

        javaCard.setOnClickListener(v -> openInterview("java"));
        pythonCard.setOnClickListener(v -> openInterview("python"));
        dsaCard.setOnClickListener(v -> openInterview("dsa"));
        cCard.setOnClickListener(v -> openInterview("c"));
    }

    private void openInterview(String skill){

        Intent intent = new Intent(InterviewActivity.this, QuestionActivity.class);
        intent.putExtra("skill", skill);
        startActivity(intent);
    }
}