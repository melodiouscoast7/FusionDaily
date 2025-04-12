package com.example.fusiondailytest;

import android.widget.Button;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.text.util.Linkify;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;

public class ResourcesActivity extends AppCompatActivity {
    private LinearLayout personalizedContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resources);

        personalizedContainer = findViewById(R.id.personalized_container);

        Button dashboardButton = findViewById(R.id.resource_dashboard_Button);
        dashboardButton.setOnClickListener(v -> {
            startActivity(new Intent(ResourcesActivity.this, DashboardActivity.class));
            finish();
        });

        // Wire up the static links
        setupStaticLinks();

        fetchPersonalizedResources();
    }

    private final String[] STATIC_URLS = {
            "https://www.healthline.com/nutrition/27-health-and-nutrition-tips",
            "https://www.muscleandstrength.com/workout-routines",
            "https://mhanational.org/"
    };

    private final int[] INCLUDE_IDS = {
            R.id.link_healthline,
            R.id.link_bodybuilding,
            R.id.link_selfimprove
    };

    private void setupStaticLinks() {
        for (int i = 0; i < INCLUDE_IDS.length; i++) {
            View item = findViewById(INCLUDE_IDS[i]);
            String url = STATIC_URLS[i];
            TextView linkView = item.findViewById(R.id.resource_link);
            linkView.setText(url);
            linkView.setOnClickListener(v ->
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url))));
            Linkify.addLinks(linkView, Linkify.WEB_URLS);
        }
    }

    private void fetchPersonalizedResources() {
        String prompt = "Find three reputable online resources (URLs only) to help achieve a random goal. Format as one URL per line, No other text, (Top Priority) No number list, and just provide the home page URLs.";
        new OpenAIManager().sendPrompt(prompt, new OpenAIManager.OpenAIResponseCallback() {
            @Override
            public void onSuccess(String json) {
                try {
                    // parse JSON → choices[0].message.content
                    JSONObject root = new JSONObject(json);
                    String content = root
                            .getJSONArray("choices")
                            .getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content");

                    // Expect lines of URLs
                    for (String line : content.split("\n")) {
                        String url = line.trim();
                        if (url.startsWith("http")) {
                            addPersonalizedLink(url);
                        }
                    }
                } catch (Exception e) {
                    runOnUiThread(() ->
                            Toast.makeText(ResourcesActivity.this,
                                    "Error parsing resources", Toast.LENGTH_SHORT).show());
                }
            }
            @Override
            public void onFailure(Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(ResourcesActivity.this,
                                "Failed to load personalized links", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void addPersonalizedLink(String url) {
        runOnUiThread(() -> {
            View item = LayoutInflater.from(this)
                    .inflate(R.layout.item_resource, personalizedContainer, false);
            TextView linkView = item.findViewById(R.id.resource_link);
            linkView.setText(url);
            linkView.setOnClickListener(v ->
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url))));
            personalizedContainer.addView(item);
        });
    }
}

