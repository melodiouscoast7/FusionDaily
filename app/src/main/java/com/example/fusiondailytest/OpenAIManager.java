package com.example.fusiondailytest;

import android.os.Handler;
import android.os.Looper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;

public class OpenAIManager {
    private static final String API_KEY = "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";
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final int MAX_RETRIES = 3;
    private static final long INITIAL_BACKOFF_MS = 1000;
    private boolean isCooldown = false;

    private OkHttpClient client;

    public OpenAIManager() {
        client = new OkHttpClient();
    }

    /**
     * Sends a prompt to the OpenAI API and returns the response via a callback.
     *
     * @param prompt The text prompt to send.
     * @param callback The callback to receive the API response.
     */
    public void sendPrompt(String prompt, final OpenAIResponseCallback callback) {
        if (isCooldown) {
            callback.onFailure(new Exception("Please wait before sending another request."));
            return;
        }

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("model", "gpt-3.5-turbo");

            JSONArray messages = new JSONArray();
            JSONObject message = new JSONObject();
            message.put("role", "user");
            message.put("content", prompt);
            messages.put(message);

            jsonBody.put("messages", messages);
            jsonBody.put("temperature", 0.7);

            RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
            Request request = new Request.Builder()
                    .url(API_URL)
                    .header("Authorization", "Bearer " + API_KEY)
                    .post(body)
                    .build();

            sendWithRetry(request, callback, 0, INITIAL_BACKOFF_MS);

            // Start cooldown
            isCooldown = true;
            new Handler(Looper.getMainLooper()).postDelayed(() -> isCooldown = false, 3000);

        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    private void sendWithRetry(Request request, OpenAIResponseCallback callback, int retryCount, long backoffMs) {
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onFailure(e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 429 && retryCount < MAX_RETRIES) {
                    // Retry with exponential backoff
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        sendWithRetry(request, callback, retryCount + 1, backoffMs * 2);
                    }, backoffMs);
                } else if (!response.isSuccessful()) {
                    new Handler(Looper.getMainLooper()).post(() ->
                            callback.onFailure(new IOException("Unexpected code " + response)));
                } else {
                    String responseBody = response.body().string();
                    new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(responseBody));
                }
            }
        });
    }

    // Callback interface to return the response or error.
    public interface OpenAIResponseCallback {
        void onSuccess(String response);
        void onFailure(Exception e);
    }
}
