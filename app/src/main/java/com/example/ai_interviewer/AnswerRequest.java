package com.example.ai_interviewer;

public class AnswerRequest {

    private String question;
    private String answer;
    private String userName;

    public AnswerRequest(String question, String answer, String userName) {
        this.question = question;
        this.answer = answer;
        this.userName = userName;
    }
}