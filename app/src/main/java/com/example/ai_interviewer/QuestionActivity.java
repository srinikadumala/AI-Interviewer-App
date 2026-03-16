package com.example.ai_interviewer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;

public class QuestionActivity extends AppCompatActivity {

    TextView questionText, feedbackText, timerText;
    EditText answerBox;

    Button submitBtn, hintBtn, speakBtn, finishBtn;

    ApiService apiService;

    String currentQuestion = "";
    String skill = "java";

    int questionCount = 1;
    int totalScore = 0;
    final int TOTAL_QUESTIONS = 10;

    CountDownTimer interviewTimer;

    TextToSpeech tts;
    boolean ttsReady = false;

    String userName = "Candidate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        questionText = findViewById(R.id.questionText);
        answerBox = findViewById(R.id.answerBox);
        feedbackText = findViewById(R.id.feedbackText);
        timerText = findViewById(R.id.timerText);

        submitBtn = findViewById(R.id.submitBtn);
        hintBtn = findViewById(R.id.hintBtn);
        speakBtn = findViewById(R.id.speakBtn);
        finishBtn = findViewById(R.id.finishBtn);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    1
            );
        }

        tts = new TextToSpeech(this, status -> {

            if (status == TextToSpeech.SUCCESS) {

                int result = tts.setLanguage(Locale.US);

                if (result == TextToSpeech.LANG_MISSING_DATA ||
                        result == TextToSpeech.LANG_NOT_SUPPORTED) {

                    Toast.makeText(this,
                            "Voice not supported",
                            Toast.LENGTH_LONG).show();

                } else {

                    tts.setSpeechRate(0.9f);
                    tts.setPitch(1.0f);

                    ttsReady = true;
                }
            }
        });

        apiService = RetrofitClient.getClient().create(ApiService.class);

        skill = getIntent().getStringExtra("skill");

        if (skill == null || skill.trim().isEmpty())
            skill = "java";

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

            if (email != null)
                userName = email.split("@")[0];
        }

        startTimer();
        generateQuestion(skill);

        submitBtn.setOnClickListener(v -> submitAnswer());
        hintBtn.setOnClickListener(v -> getHint());
        speakBtn.setOnClickListener(v -> speakAnswer());
        finishBtn.setOnClickListener(v -> finishInterview());
    }

    private void startTimer() {

        interviewTimer = new CountDownTimer(20 * 60 * 1000, 1000) {

            public void onTick(long millisUntilFinished) {

                long minutes = millisUntilFinished / 60000;
                long seconds = (millisUntilFinished % 60000) / 1000;

                timerText.setText("Time Left: " + minutes + ":" + String.format("%02d", seconds));
            }

            public void onFinish() {
                finishInterview();
            }

        }.start();
    }

    private void generateQuestion(String skill) {

        questionText.setText("Generating question...");

        SkillRequest request = new SkillRequest(skill);

        apiService.generateQuestion(request).enqueue(new Callback<QuestionResponse>() {

            @Override
            public void onResponse(Call<QuestionResponse> call, Response<QuestionResponse> response) {

                if (response.isSuccessful() && response.body() != null) {

                    currentQuestion = response.body().getQuestion();

                    questionText.setText(currentQuestion);

                    answerBox.setText("");
                    feedbackText.setText("");

                    questionText.postDelayed(() -> {
                        if (ttsReady) {
                            tts.speak(
                                    "Next question. " + currentQuestion,
                                    TextToSpeech.QUEUE_FLUSH,
                                    null,
                                    "QUESTION"
                            );
                        }
                    }, 500);

                } else {
                    questionText.setText("Server error");
                }
            }

            @Override
            public void onFailure(Call<QuestionResponse> call, Throwable t) {

                questionText.setText("Server error");

                Toast.makeText(
                        QuestionActivity.this,
                        "Error: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void submitAnswer() {

        String answer = answerBox.getText().toString().trim();

        if (answer.isEmpty()) {

            Toast.makeText(this, "Enter answer", Toast.LENGTH_SHORT).show();
            return;
        }

        AnswerRequest request = new AnswerRequest(currentQuestion, answer, userName);

        apiService.evaluateAnswer(request).enqueue(new Callback<AnswerResponse>() {

            @Override
            public void onResponse(Call<AnswerResponse> call, Response<AnswerResponse> response) {

                if (response.body() != null) {

                    String evaluation = response.body().getEvaluation();

                    feedbackText.setText(evaluation);

                    if (ttsReady) {
                        tts.speak(evaluation, TextToSpeech.QUEUE_FLUSH, null, "EVAL");
                    }

                    String evalLower = evaluation.toLowerCase();

                    if (evalLower.contains("correct")) {
                        totalScore += 10;
                    }

                    questionCount++;

                    if (questionCount <= TOTAL_QUESTIONS) {
                        generateQuestion(skill);
                    } else {
                        finishInterview();
                    }

                } else {

                    Toast.makeText(QuestionActivity.this,
                            "Evaluation failed",
                            Toast.LENGTH_SHORT).show();
                }
            }

            public void onFailure(Call<AnswerResponse> call, Throwable t) {
                Toast.makeText(QuestionActivity.this,
                        t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getHint() {

        HintRequest request = new HintRequest(currentQuestion);

        apiService.getHint(request).enqueue(new Callback<HintResponse>() {

            @Override
            public void onResponse(Call<HintResponse> call, Response<HintResponse> response) {

                if (response.body() != null) {

                    String hint = response.body().getHint();

                    feedbackText.setText("Hint: " + hint);

                    if (ttsReady) {
                        tts.speak(hint, TextToSpeech.QUEUE_FLUSH, null, "HINT");
                    }
                }
            }

            @Override
            public void onFailure(Call<HintResponse> call, Throwable t) {

                Toast.makeText(QuestionActivity.this,
                        "Hint error",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void speakAnswer() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        );

        intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault()
        );

        intent.putExtra(
                RecognizerIntent.EXTRA_PROMPT,
                "Speak your answer"
        );

        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {

            ArrayList<String> result =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            if (result != null && result.size() > 0) {

                String spokenText = result.get(0);

                answerBox.setText(spokenText);

                Toast.makeText(
                        this,
                        "Voice converted to text",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }

    private void finishInterview() {

        if (interviewTimer != null)
            interviewTimer.cancel();

        int correct = totalScore / 10;
        int wrong = TOTAL_QUESTIONS - correct;

        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance(
                "https://aiinterviewerapp-a911e-default-rtdb.asia-southeast1.firebasedatabase.app"
        );

        DatabaseReference resultsRef =
                database.getReference("InterviewResults").child(userId);

        Map<String, Object> interview = new HashMap<>();
        interview.put("skill", skill);
        interview.put("score", totalScore);
        interview.put("correct", correct);
        interview.put("wrong", wrong);
        interview.put("totalQuestions", TOTAL_QUESTIONS);
        interview.put("timestamp", System.currentTimeMillis());

        resultsRef.push().setValue(interview)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this,"Result saved",Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this,"Firebase error: "+e.getMessage(),Toast.LENGTH_LONG).show());

        questionText.setText("Interview Completed");

        feedbackText.setText(
                "Score: " + totalScore + "/100\n\n" +
                        "Correct: " + correct + "\n" +
                        "Wrong: " + wrong
        );

        timerText.setVisibility(View.GONE);
        answerBox.setVisibility(View.GONE);
        submitBtn.setVisibility(View.GONE);
        hintBtn.setVisibility(View.GONE);
        speakBtn.setVisibility(View.GONE);
        finishBtn.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {

        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }

        super.onDestroy();
    }
}
