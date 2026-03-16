package com.example.ai_interviewer;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("generate_question")
    Call<QuestionResponse> generateQuestion(@Body SkillRequest skillRequest);

    @POST("evaluate_answer")
    Call<AnswerResponse> evaluateAnswer(@Body AnswerRequest answerRequest);

    @POST("get_hint")
    Call<HintResponse> getHint(@Body HintRequest hintRequest);

}