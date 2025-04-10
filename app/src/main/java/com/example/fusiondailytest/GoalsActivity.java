package com.example.fusiondailytest;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import java.util.Vector;
public class GoalsActivity extends AppCompatActivity {

    private Vector<View> goalLayouts = new Vector<View>();
    private Vector<ProgressBar> totalProgressBars = new Vector<ProgressBar>();;
    private Vector<TextView> totalProgressTexts = new Vector<TextView>();;
    private Vector<TextView> goalTitles = new Vector<TextView>();;
    private Vector<TextView> goalStreaks = new Vector<TextView>();;
    private Vector<Button> viewButtons = new Vector<Button>();;

    private Button backButton;
    private Button createButton;
    private Button createFirstButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_goals_overview);
        assignGoalsOverview();
        updateGoalsOverview();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GoalsActivity.this, DashboardActivity.class));
                finish();
            }
        });


    }


    //Overview fragment functions
    private void assignGoalsOverview(){ //Assigns objects for the overview fragment

        goalLayouts.add(findViewById(R.id.goalOneLayout));
        goalLayouts.add(findViewById(R.id.goalTwoLayout));
        goalLayouts.add(findViewById(R.id.goalThreeLayout));
        goalLayouts.add(findViewById(R.id.goalFourLayout));
        goalLayouts.add(findViewById(R.id.goalFiveLayout));

        goalTitles.add(findViewById(R.id.goalOneText));
        goalTitles.add(findViewById(R.id.goalTwoText));
        goalTitles.add(findViewById(R.id.goalThreeText));
        goalTitles.add(findViewById(R.id.goalFourText));
        goalTitles.add(findViewById(R.id.goalFiveText));

        totalProgressBars.add(findViewById(R.id.goalOneProgressBar));
        totalProgressBars.add(findViewById(R.id.goalTwoProgressBar));
        totalProgressBars.add(findViewById(R.id.goalThreeProgressBar));
        totalProgressBars.add(findViewById(R.id.goalFourProgressBar));
        totalProgressBars.add(findViewById(R.id.goalFiveProgressBar));

        backButton = findViewById(R.id.backButton);
        createButton = findViewById(R.id.createButton);
        createFirstButton = findViewById(R.id.noGoalsButton);
    }

    private void updateGoalsOverview(){
        boolean anyGoals = true;
        //for(int i = 0; i < goalLayouts.size(); i++)
        //    goalLayouts.get(i).setVisibility(View.GONE);

        goalLayouts.get(2).setVisibility(View.GONE);
        goalLayouts.get(3).setVisibility(View.GONE);
        goalLayouts.get(4).setVisibility(View.GONE);

        if(anyGoals) {
            createFirstButton.setVisibility(View.GONE);
            createButton.setVisibility(View.VISIBLE);
        } else {
            createFirstButton.setVisibility(View.VISIBLE);
            createButton.setVisibility(View.GONE);
        }
    }


    //View fragment functions
    private void assignGoalsView(){ //Assigns objects for the view fragment

    }

    //Create fragment functions
    private void assignGoalsCreate(){ //Assigns objects for the create fragment

    }


}

