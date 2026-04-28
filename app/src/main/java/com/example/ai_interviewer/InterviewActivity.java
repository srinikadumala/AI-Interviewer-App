package com.example.ai_interviewer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.speech.RecognizerIntent;
import android.widget.Toast;

import java.util.ArrayList;

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

    // 🔊 Voice input (kept ready if needed later)
    private void speakAnswer() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN");

        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speak your answer clearly");

        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);

        try {
            startActivityForResult(intent, 100);
        } catch (Exception e) {
            Toast.makeText(this,
                    "Voice not supported",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // ⚠️ DO NOT handle answerBox here (this screen doesn't have it)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {

            ArrayList<String> results =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            if (results != null && !results.isEmpty()) {

                String spokenText = results.get(0);

                // 👉 For now just show as toast (safe)
                Toast.makeText(this,
                        "You said: " + spokenText,
                        Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(this,
                        "Didn't catch that. Try again.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openInterview(String skill){

        Intent intent = new Intent(InterviewActivity.this, QuestionActivity.class);
        intent.putExtra("skill", skill);
        startActivity(intent);
    }
}