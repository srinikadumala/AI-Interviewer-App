package com.example.ai_interviewer;

public class AnswerRequest {

    private String question;
    private String answer;
    private String email;

    public AnswerRequest(String question, String answer, String email) {
        this.question = question;
        this.answer = answer;
        this.email = email;
    }
}