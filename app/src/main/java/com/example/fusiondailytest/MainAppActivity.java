package com.example.fusiondailytest;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import android.widget.Button;

import java.util.Calendar;
import java.util.Vector;
import com.example.Logic.*;
public class MainAppActivity extends AppCompatActivity {


    private FirebaseAuth auth;
    //Dashboard (db) Variables
    private ProgressBar dbTotalProgressBar;
    private TextView dbTotalProgressText;
    private ProgressBar dbDailyProgressBar;
    private TextView dbDailyProgressText;
    private TextView dbDailyStreakText;
    private Button dbSettingsButton;
    private Button dbGoalsButton;
    private Button dbFocusButton;
    private TextView dbMonthText;
    private TextView dbDateText;

    //Goals Overview (go) Variables
    private Vector<View> goGoalLayouts = new Vector<View>();
    private Vector<ProgressBar> goTotalProgressBars = new Vector<ProgressBar>();;
    private Vector<TextView> goTotalProgressTexts = new Vector<TextView>();;
    private Vector<TextView> goGoalTitles = new Vector<TextView>();;
    private Vector<TextView> goGoalStreaks = new Vector<TextView>();;
    private Vector<Button> goViewButtons = new Vector<Button>();;
    private Button goBackButton;
    private Button goCreateButton;
    private Button goCreateFirstButton;

    //Goal Creation (gc) Variables
    private Button gcCancelButton;
    private Button gcSaveButton;
    private TextView gcNameInput;
    private TextView gcDescriptionInput;

    //Goal Logic
    private Vector<Goal> goals = new Vector<Goal>();
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
        setContentView(R.layout.fragment_dashboard);
        // Initialize UI components
        assignDashboard();

    }




    // Dashboard (db) Functions
    private void assignDashboard() //Assigns objects for the dashboard fragment
    {
        dbTotalProgressBar = findViewById(R.id.totalProgressBar);
        dbTotalProgressText = findViewById(R.id.totalProgressBarText);

        dbDailyProgressBar = findViewById(R.id.dailyProgressBar);
        dbDailyProgressText = findViewById(R.id.dailyProgressBarText);

        dbGoalsButton = findViewById(R.id.goalsButton);
        dbFocusButton = findViewById(R.id.focusButton);
        dbSettingsButton = findViewById(R.id.settingsButton);

        // Example logic to simulate progress updates
        updateTotalProgress(20); // Increment progress by 20%
        updateDailyProgress(30); // Increment progress by another 30%

        dbMonthText = findViewById(R.id.monthText);
        dbDateText = findViewById(R.id.dateText);
        dbMonthText.setText(months[month]);
        dbDateText.setText(day);

        auth = FirebaseAuth.getInstance();

        dbSettingsButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                auth.signOut();
                startActivity(new Intent(MainAppActivity.this, LoginActivity.class));
            }
        });

        dbGoalsButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setContentView(R.layout.fragment_goals_overview);
                assignGoalsOverview();
                updateGoalsOverview();
            }
        });

        dbFocusButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                updateDailyProgress(5);
            }
        });
    }
    private void updateTotalProgress(int increment)
    {
        totalProgressValue += increment;
        if (totalProgressValue > 100) {
            totalProgressValue = 100; // Cap progress at 100%
        }
        // Update the ProgressBar and display text
        dbTotalProgressBar.setProgress(totalProgressValue);
        dbTotalProgressText.setText("Total Progress: " + totalProgressValue + "%");
    }
    private void updateDailyProgress(int increment)
    {
        dailyProgressValue += increment;
        if (dailyProgressValue > 100)
        {
            dailyProgressValue = 100; // Cap progress at 100%
        }
        if (dailyProgressValue < 0)
        {
            dailyProgressValue = 0; // Cap progress at 100%
        }
        // Update the ProgressBar and display text
        dbDailyProgressBar.setProgress(dailyProgressValue);
        dbDailyProgressText.setText(dailyProgressValue + "% Complete");
    }
    private void updateDBDailyStreak()
    {

    }



    //Goal Overview (go) Functions
    private void assignGoalsOverview() //Assigns objects for the overview fragment
    {
        goGoalLayouts.clear();
        goGoalLayouts.add(findViewById(R.id.goalOneLayout));
        goGoalLayouts.add(findViewById(R.id.goalTwoLayout));
        goGoalLayouts.add(findViewById(R.id.goalThreeLayout));
        goGoalLayouts.add(findViewById(R.id.goalFourLayout));
        goGoalLayouts.add(findViewById(R.id.goalFiveLayout));

        goGoalTitles.clear();
        goGoalTitles.add(findViewById(R.id.goalOneText));
        goGoalTitles.add(findViewById(R.id.goalTwoText));
        goGoalTitles.add(findViewById(R.id.goalThreeText));
        goGoalTitles.add(findViewById(R.id.goalFourText));
        goGoalTitles.add(findViewById(R.id.goalFiveText));

        goTotalProgressBars.clear();
        goTotalProgressBars.add(findViewById(R.id.goalOneProgressBar));
        goTotalProgressBars.add(findViewById(R.id.goalTwoProgressBar));
        goTotalProgressBars.add(findViewById(R.id.goalThreeProgressBar));
        goTotalProgressBars.add(findViewById(R.id.goalFourProgressBar));
        goTotalProgressBars.add(findViewById(R.id.goalFiveProgressBar));

        goTotalProgressTexts.clear();
        goTotalProgressTexts.add(findViewById(R.id.goalOneProgressBarText));
        goTotalProgressTexts.add(findViewById(R.id.goalTwoProgressBarText));
        goTotalProgressTexts.add(findViewById(R.id.goalThreeProgressBarText));
        goTotalProgressTexts.add(findViewById(R.id.goalFourProgressBarText));
        goTotalProgressTexts.add(findViewById(R.id.goalFiveProgressBarText));

        goGoalStreaks.clear();
        goGoalStreaks.add(findViewById(R.id.goalOneStreakCounterText));
        goGoalStreaks.add(findViewById(R.id.goalTwoStreakCounterText));
        goGoalStreaks.add(findViewById(R.id.goalThreeStreakCounterText));
        goGoalStreaks.add(findViewById(R.id.goalFourStreakCounterText));
        goGoalStreaks.add(findViewById(R.id.goalFiveStreakCounterText));

        goBackButton = findViewById(R.id.backButton);
        goCreateButton = findViewById(R.id.createButton);
        goCreateFirstButton = findViewById(R.id.noGoalsButton);

        goBackButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setContentView(R.layout.fragment_dashboard);
                assignDashboard();
            }
        });
        goCreateFirstButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setContentView(R.layout.fragment_goals_create);
                assignGoalsCreate();
            }
        });
        goCreateButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setContentView(R.layout.fragment_goals_create);
                assignGoalsCreate();
            }
        });
    }


    private void updateGoalsOverview()
    {
        for (int i = 0; i < goGoalLayouts.size(); i++)
            goGoalLayouts.get(i).setVisibility(View.GONE);

        if(!goals.isEmpty())
        {
            for (int i = 0; i < goals.size(); i++)
            {
                goGoalLayouts.get(i).setVisibility(View.VISIBLE);
                goGoalTitles.get(i).setText(goals.get(i).getName());
                goTotalProgressBars.get(i).setProgress(goals.get(i).getTotalProgress());
                String progressText = "Total Progress: " + goals.get(i).getTotalProgress() + "%";
                goTotalProgressTexts.get(i).setText(progressText);
                String streakCounter = "" + goals.get(i).getDailyStreak();
                goGoalStreaks.get(i).setText(streakCounter);
            }
            goCreateFirstButton.setVisibility(View.GONE);
            goCreateButton.setVisibility(View.VISIBLE);
        }
        else
        {
            goCreateFirstButton.setVisibility(View.VISIBLE);
            goCreateButton.setVisibility(View.GONE);
        }
    }



    //Goal Create (gc) Functions
    private void assignGoalsCreate() //Assigns objects for the view fragment
    {
        gcCancelButton = findViewById(R.id.goalCancelButton);
        gcSaveButton = findViewById(R.id.goalSaveButton);
        gcNameInput = findViewById(R.id.textInputName);
        gcDescriptionInput = findViewById(R.id.textInputDescription);

        gcCancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setContentView(R.layout.fragment_goals_overview);
                assignGoalsOverview();
                updateGoalsOverview();
            }
        });

        gcSaveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                goals.add(new Goal(gcNameInput.getText().toString(), gcDescriptionInput.getText().toString()));
                setContentView(R.layout.fragment_goals_overview);
                assignGoalsOverview();
                updateGoalsOverview();
            }
        });
    }


    //Goal View (gv) Functions
    private void assignGoalsView() //Assigns objects for the create fragment
    {

    }

}
