package com.example.fusiondailytest;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import android.view.Menu;
import android.view.MenuItem;
import com.google.firebase.auth.FirebaseAuth;
import android.widget.Button;
import java.util.TimeZone;
import java.util.Date;
import java.util.Calendar;
public class DashboardActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private ProgressBar totalProgressBar;
    private TextView totalProgressText;
    private ProgressBar dailyProgressBar;
    private TextView dailyProgressText;
    private Button settingsButton;

    private Button goalsButton;

    private Button focusButton;

    private TextView monthText;
    private TextView dateText;
    private int totalProgressValue = 0; // Variable to track task progress (0-100 scale)
    private int dailyProgressValue = 0;


    Calendar localCalendar = Calendar.getInstance();

    final private String day = "" + localCalendar.get(Calendar.DATE);
    final private int month = localCalendar.get(Calendar.MONTH);

    String[] months = new String[]{"January", "February", "March", "April",
            "May", "June", "July", "August", "September", "October", "November", "December"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize UI components
        totalProgressBar = findViewById(R.id.totalProgressBar);
        totalProgressText = findViewById(R.id.totalProgressBarText);

        dailyProgressBar = findViewById(R.id.dailyProgressBar);
        dailyProgressText = findViewById(R.id.dailyProgressBarText);

        goalsButton = findViewById(R.id.goalsButton);
        focusButton = findViewById(R.id.focusButton);
        settingsButton = findViewById(R.id.settingsButton);
        // Example logic to simulate progress updates
        updateTotalProgress(20); // Increment progress by 20%
        updateDailyProgress(30); // Increment progress by another 30%

        monthText = findViewById(R.id.monthText);
        dateText = findViewById(R.id.dateText);
        monthText.setText(months[month]);
        dateText.setText(day);

        auth = FirebaseAuth.getInstance();
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
                finish();
            }
        });

        goalsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDailyProgress(-5);

            }
        });

        focusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDailyProgress(5);
            }
        });
    }

    // Method to update progress
    private void updateTotalProgress(int increment) {
        totalProgressValue += increment;
        if (totalProgressValue > 100) {
            totalProgressValue = 100; // Cap progress at 100%
        }
        // Update the ProgressBar and display text
        totalProgressBar.setProgress(totalProgressValue);
        totalProgressText.setText("Total Progress: " + totalProgressValue + "%");
    }
    private void updateDailyProgress(int increment) {
        dailyProgressValue += increment;
        if (dailyProgressValue > 100) {
            dailyProgressValue = 100; // Cap progress at 100%
        }
        if (dailyProgressValue < 0) {
            dailyProgressValue = 0; // Cap progress at 100%
        }
        // Update the ProgressBar and display text
        dailyProgressBar.setProgress(dailyProgressValue);
        dailyProgressText.setText(dailyProgressValue + "% Complete");
    }



}
