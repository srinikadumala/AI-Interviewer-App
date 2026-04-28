package com.example.ai_interviewer;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;

public class RetrofitClient {

    private static final String BASE_URL = "http://10.205.192.224:8000/";

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {

        if (retrofit == null) {

            // 🔥 ADD TIMEOUT CLIENT HERE
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client) // 🔥 IMPORTANT LINE
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}