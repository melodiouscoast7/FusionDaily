package com.example.fusiondailytest;

//android imports
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.graphics.Paint;
import android.widget.Button;
import android.net.Uri;
import android.view.LayoutInflater;
import android.text.util.Linkify;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import android.view.View;
import android.util.Log;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.text.ParseException;
import java.util.Random;
import java.util.concurrent.*;

//firebase imports
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

//java imports
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Vector;

import com.example.Logic.*;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;


public class MainAppActivity extends AppCompatActivity {

    //firebase authentication
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    //Dashboard (db) Variables
    private ProgressBar dbTotalProgressBar;
    private TextView dbTotalProgressText;
    private ProgressBar dbDailyProgressBar;
    private TextView dbDailyProgressText;
    private TextView dbDailyStreakText;
    private Button dbSettingsButton;
    private Button dbGoalsButton;
    private Button dbCalendarButton;
    private Button dbResourcesButton;
    private TextView dbMonthText;
    private TextView dbDateText;
    private Vector<TextView> dbToDoLabels = new Vector<TextView>();
    private Vector<Button> dbToDoButtons = new Vector<Button>();

    //dashboard to do (td) variables
    private View tdLayout;
    private Button tdExitUIButton;
    private TextView tdGoalTitleText;
    private Vector<View> tdTaskLayouts = new Vector<View>();
    private Vector<TextView> tdTaskTitles = new Vector<TextView>();
    private Vector<TextView> tdTaskDescriptions = new Vector<TextView>();
    private Vector<Button> tdTaskCompleteButtons = new Vector<Button>();

    //Resource View (rv) Variables
    private LinearLayout rvPersonalizedContainer;

    private final String[] rvSTATIC_URLS = {
            "https://www.healthline.com/nutrition/27-health-and-nutrition-tips",
            "https://www.muscleandstrength.com/workout-routines",
            "https://mhanational.org/"
    };

    private final int[] rvINCLUDE_IDS = {
            R.id.link_healthline,
            R.id.link_bodybuilding,
            R.id.link_selfimprove
    };

    //Settings View (sv) Variables
    private Button svSignOutButton;
    private Button svChangePasswordButton;
    private Button svDeleteAccountButton;
    private Button svViewQuestionnaireButton;
    private Button svBackButton;
    private TextView svDeleteText;
    private boolean isPasswordChange;

    //Quote Screen Variables
    private View mainLayout;
    private View quoteOverlay;
    private TextView quoteTextView;
    private TextView authorTextView;
    private Handler quoteHandler;
    private Runnable hideQuoteRunnable;

    //Goals Overview (go) Variables
    private Vector<View> goGoalLayouts = new Vector<View>();
    private Vector<ProgressBar> goTotalProgressBars = new Vector<ProgressBar>();
    private Vector<TextView> goTotalProgressTexts = new Vector<TextView>();
    private Vector<TextView> goGoalTitles = new Vector<TextView>();
    private Vector<TextView> goGoalStreaks = new Vector<TextView>();
    private Vector<Button> goViewButtons = new Vector<Button>();
    private Button goBackButton;
    private Button goCreateButton;
    private Button goCreateFirstButton;

    //Goal View (gv) Variables
    private Vector<View> gvTaskLayouts = new Vector<View>();
    private Vector<TextView> gvTaskTitles = new Vector<TextView>();
    private Vector<TextView> gvTaskDescriptions = new Vector<TextView>();
    private Vector<Button> gvEditButtons = new Vector<Button>();
    private Button gvBackButton;
    private Button gvCreateButton;
    private Button gvEditGoalButton;
    private TextView gvDailyStreakText;
    private TextView gvStartDateText;
    private TextView gvEndDateText;
    private TextView gvGoalTitleText;
    private TextView gvGoalDescriptionText;


    //Goal Creation (gc) Variables
    private Button gcCancelButton;
    private Button gcNextButton;
    private TextView gcNameInput;
    private TextView gcDescriptionInput;
    private TextView gcCompletionDate;


    //Task Create (tc) Functions
    private Button tcCancelButton;
    private Button tcSaveButton;
    private TextView tcNameInput;
    private TextView tcDescriptionInput;

    //Goal Logic
    private Vector<Goal> goals = new Vector<Goal>();
    Goal tempGoal;
    private int goalNumber = 0, taskNumber = 0;
    private boolean isFromGoalView = false;
    private boolean isNewGoal = true;
    private boolean isNewTask = true;
    String currentDate = "";
    String previousDate = "";
    int targetColor;
    int prevTarget;

    //Calendar Variables
    Calendar localCalendar = Calendar.getInstance();
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    final private String day = "" + localCalendar.get(Calendar.DATE);
    final public int month = localCalendar.get(Calendar.MONTH);
    private List<YearMonth> monthsList;
    private ViewPager2 pager;
    String[] months = new String[]{"January", "February", "March", "April",
            "May", "June", "July", "August", "September", "October", "November", "December"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //load quote screen here
        currentDate = dateFormat.format(localCalendar.getTime());
        targetColor = getResources().getColor(R.color.wheel_gray);
        prevTarget = getResources().getColor(R.color.wheel_gray);
        Executors.newSingleThreadExecutor().execute(() -> {
            loadFirestoreData();
            //Happens once data is loaded, load quote before switching to main dashboard
            runOnUiThread(() ->
            {
                setContentView(R.layout.fragment_dashboard);
                showQuoteScreen();
                checkDayResetter();
                assignDashboard();
            });
        });
    }

    // Dashboard (db) Functions
    private void assignDashboard() //Assigns objects for the dashboard fragment
    {
        dbTotalProgressBar = findViewById(R.id.totalProgressBar);
        dbTotalProgressText = findViewById(R.id.totalProgressBarText);

        dbDailyProgressBar = findViewById(R.id.dailyProgressBar);
        dbDailyProgressText = findViewById(R.id.dailyProgressBarText);

        dbGoalsButton = findViewById(R.id.goalsButton);
        dbCalendarButton = findViewById(R.id.calendarButton);
        dbSettingsButton = findViewById(R.id.settingsButton);
        dbResourcesButton = findViewById(R.id.resourcesButton);

        dbDailyStreakText = findViewById(R.id.streakNumberText);
        dbMonthText = findViewById(R.id.monthText);
        dbDateText = findViewById(R.id.dateText);
        dbMonthText.setText(months[month]);
        dbDateText.setText(day);

        dbToDoButtons.clear();
        dbToDoButtons.add(findViewById(R.id.todoButton));
        dbToDoButtons.add(findViewById(R.id.todoButton1));
        dbToDoButtons.add(findViewById(R.id.todoButton2));
        dbToDoButtons.add(findViewById(R.id.todoButton3));
        dbToDoButtons.add(findViewById(R.id.todoButton4));

        dbToDoLabels.clear();
        dbToDoLabels.add(findViewById(R.id.todoButtonText));
        dbToDoLabels.add(findViewById(R.id.todoButtonText2));
        dbToDoLabels.add(findViewById(R.id.todoButtonText3));
        dbToDoLabels.add(findViewById(R.id.todoButtonText4));
        dbToDoLabels.add(findViewById(R.id.todoButtonText5));

        dbToDoButtons.get(0).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(goals.size()>0)
                {
                    goalNumber = 0;
                    runToDoUI();
                }
            }
        });

        dbToDoButtons.get(1).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(goals.size()>1)
                {
                    goalNumber = 1;
                    runToDoUI();
                }
            }
        });

        dbToDoButtons.get(2).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(goals.size()>2)
                {
                    goalNumber = 2;
                    runToDoUI();
                }
            }
        });

        dbToDoButtons.get(3).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(goals.size()>3)
                {
                    goalNumber = 3;
                    runToDoUI();
                }
            }
        });

        dbToDoButtons.get(4).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(goals.size()>4)
                {
                    goalNumber = 4;
                    runToDoUI();
                }
            }
        });

        dbSettingsButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setContentView(R.layout.fragment_settings);
                assignSettingsView();
            }
        });

        dbGoalsButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setContentView(R.layout.fragment_goals_overview);
                assignGoalsOverview();
            }
        });

        dbCalendarButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setContentView(R.layout.fragment_calendar);
                assignCalendarView();
            }
        });
        dbResourcesButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setContentView(R.layout.fragment_resources);
                assignResourceView();
            }
        });

        updateTotalProgress();
        updateDailyProgress();
        updateDBDailyStreak();
        updateToDoButtons();
        assignToDoUI();
    }

    private void updateTotalProgress(){
        int totalProgressValue = 0;
        int furthestGoal = Integer.MIN_VALUE;
        long startDate = 0;
        for (Goal goal : goals)
        {
            Calendar cal = Calendar.getInstance();
            try
            {
                cal.setTime(Objects.requireNonNull(dateFormat.parse(goal.getStartDate())));
                startDate = cal.getTimeInMillis();
                cal.setTime(Objects.requireNonNull(dateFormat.parse(goal.getCompletionDate())));
                long totalDays = TimeUnit.MILLISECONDS.toDays(cal.getTimeInMillis() - startDate);
                long daysPassed = TimeUnit.MILLISECONDS.toDays(localCalendar.getTimeInMillis() - startDate);
                totalProgressValue = (int)((float)daysPassed / totalDays * 100);
                totalProgressValue = Math.max(0 , Math.min(totalProgressValue, 100));
                goal.setTotalProgress(totalProgressValue);
                db.collection("users").document(userId).collection("goals").document(goal.getDocID()).update("totalProgress", goal.getTotalProgress());
            } catch(Exception e) {
                Log.e("Goal", "Wrongly Formatted goal completion date", e);
            }
            furthestGoal = Math.max(totalProgressValue, furthestGoal);
        }



        totalProgressValue = furthestGoal;
        // Update the ProgressBar and display text
        dbTotalProgressBar.setProgress(totalProgressValue);
        dbTotalProgressText.setText("Total Progress: " + totalProgressValue + "%");
    }


    private void updateDailyProgress()
    {
        //daily progress logic
        int dailyProgressValue = 0;
        int totalTasks = 0, completedTasks = 0;
        for(int i = 0; i < goals.size(); i++)
        {
            for (int j = 0; j < goals.get(i).getTaskAmount(); j++)
            {
                if(goals.get(i).getTask(j).isComplete())
                    completedTasks++;
                totalTasks++;
            }
        }
        if (totalTasks > 0)
            dailyProgressValue = (int)(((float) completedTasks / totalTasks) * 100);

        dailyProgressValue = Math.max(0 , Math.min(dailyProgressValue, 100));


        //daily progress animations
        ValueAnimator progressAnimator = ValueAnimator.ofInt(dbDailyProgressBar.getProgress(), dailyProgressValue);
        progressAnimator.setDuration(1000);
        progressAnimator.addUpdateListener(animation -> {
            int animatedValue = (int) animation.getAnimatedValue();
            dbDailyProgressBar.setProgress(animatedValue);
            dbDailyProgressText.setText(animatedValue + "% Complete");
        });
        progressAnimator.start();


        if (dailyProgressValue < 100)
        {
            prevTarget = targetColor;
            targetColor = (int) new ArgbEvaluator().evaluate((float)dailyProgressValue/100, getResources().getColor(R.color.wheel_gray), getResources().getColor(R.color.accentGreen));
        }
        else
        {
            prevTarget = targetColor;
            targetColor = getResources().getColor(R.color.accentBlue);
        }


        ValueAnimator colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), prevTarget, targetColor);
        colorAnimator.setDuration(1000);
        colorAnimator.addUpdateListener(animator -> {
            int animatedColor = (int) animator.getAnimatedValue();
            dbDailyProgressBar.getProgressDrawable().setTint(animatedColor);
            dbDailyProgressText.setTextColor(animatedColor);
        });

        colorAnimator.start();

    }

    private void updateDBDailyStreak()
    {
        if(!goals.isEmpty())
        {
            updateStreak(goals.get(goalNumber));
            int compare = Integer.MIN_VALUE;
            for (Goal goal : goals) {
                compare = Math.max(compare, goal.getDailyStreak());
            }

            String output = "" + compare;
            dbDailyStreakText.setText(output);
        }
    }

    private void updateStreak(Goal goal)
    {
        boolean isComplete = false;
        if(goal.getTaskAmount() > 0) {
            isComplete = true;
            for (int i = 0; i < goal.getTaskAmount(); i++)
                if (!goal.getTask(i).isComplete())
                    isComplete = false;
        }

        if(isComplete && !goal.isCompletedToday())
        {
            goal.setDailyStreak(goal.getDailyStreak() + 1);
            db.collection("users").document(userId).collection("goals").document(goal.getDocID()).update("dailyStreak", goal.getDailyStreak());
            goal.setCompletedToday(true);
            db.collection("users").document(userId).collection("goals").document(goal.getDocID()).update("completedToday", goal.isCompletedToday());
        }
        else if(goal.getDailyStreak() > 0 && goal.isCompletedToday() && !isComplete) {
            goal.setDailyStreak(goal.getDailyStreak() - 1);
            db.collection("users").document(userId).collection("goals").document(goal.getDocID()).update("dailyStreak", goal.getDailyStreak());
            goal.setCompletedToday(false);
            db.collection("users").document(userId).collection("goals").document(goal.getDocID()).update("completedToday", goal.isCompletedToday());
        }
    }

    private void checkDayResetter()
    {
        if(!previousDate.equals(currentDate))
        {
            //check if any tasks are incomplete, if so, reset that goal's daily streak, then set all task completions to false
            for (Goal goal : goals)
            {
                goal.setCompletedToday(false);
                for (int i = 0; i < goal.getTaskAmount(); i++)
                {
                    if(!goal.getTask(i).isComplete())
                    {
                        goal.setDailyStreak(0);
                    }
                    goal.getTask(i).setCompletion(false);
                }
                saveGoalToFirestore(goal);
            }
        }
    }

    private void updateToDoButtons()
    {
        for(int i = 0; i < 5; i++)
        {
            dbToDoButtons.get(i).setTranslationY(dbToDoButtons.get(i).getTranslationY() + 250);
            dbToDoLabels.get(i).setVisibility(View.INVISIBLE);
        }
        for(int i = 0; i < goals.size(); i++)
        {
            dbToDoButtons.get(i).setTranslationY(dbToDoButtons.get(i).getTranslationY() - 250);
            dbToDoLabels.get(i).setVisibility(View.VISIBLE);
        }
    }

    private void assignToDoUI()
    {
        tdLayout = findViewById(R.id.todoLayout);
        tdLayout.setFocusable(false);
        tdLayout.setFocusableInTouchMode(false);

        tdExitUIButton = findViewById(R.id.todoUIExitButton);
        tdGoalTitleText = findViewById(R.id.todoGoalTitle);

        tdTaskLayouts.clear();
        tdTaskLayouts.add(findViewById(R.id.taskLayout1));
        tdTaskLayouts.add(findViewById(R.id.taskLayout2));
        tdTaskLayouts.add(findViewById(R.id.taskLayout3));
        tdTaskLayouts.add(findViewById(R.id.taskLayout4));
        tdTaskLayouts.add(findViewById(R.id.taskLayout5));

        tdTaskTitles.clear();
        tdTaskTitles.add(findViewById(R.id.taskTitle1));
        tdTaskTitles.add(findViewById(R.id.taskTitle2));
        tdTaskTitles.add(findViewById(R.id.taskTitle3));
        tdTaskTitles.add(findViewById(R.id.taskTitle4));
        tdTaskTitles.add(findViewById(R.id.taskTitle5));

        tdTaskDescriptions.clear();
        tdTaskDescriptions.add(findViewById(R.id.taskDescription1));
        tdTaskDescriptions.add(findViewById(R.id.taskDescription2));
        tdTaskDescriptions.add(findViewById(R.id.taskDescription3));
        tdTaskDescriptions.add(findViewById(R.id.taskDescription4));
        tdTaskDescriptions.add(findViewById(R.id.taskDescription5));

        tdTaskCompleteButtons.clear();
        tdTaskCompleteButtons.add(findViewById(R.id.taskCompleteButton1));
        tdTaskCompleteButtons.add(findViewById(R.id.taskCompleteButton2));
        tdTaskCompleteButtons.add(findViewById(R.id.taskCompleteButton3));
        tdTaskCompleteButtons.add(findViewById(R.id.taskCompleteButton4));
        tdTaskCompleteButtons.add(findViewById(R.id.taskCompleteButton5));

        tdTaskCompleteButtons.get(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!goals.get(goalNumber).getTask(0).isComplete()) {
                    tdTaskCompleteButtons.get(0).setText("Undo Complete");
                    goals.get(goalNumber).getTask(0).setCompletion(true);
                    tdTaskTitles.get(0).setPaintFlags(tdTaskTitles.get(0).getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    tdTaskCompleteButtons.get(0).setText("Complete");
                    goals.get(goalNumber).getTask(0).setCompletion(false);
                    tdTaskTitles.get(0).setPaintFlags(tdTaskTitles.get(0).getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }
                db.collection("users").document(userId).collection("goals").document(goals.get(goalNumber).getDocID()).update("task" + 1 + ".isComplete", goals.get(goalNumber).getTask(0).isComplete());
                updateDailyProgress();
                updateTotalProgress();
                updateDBDailyStreak();
            }
        });

        tdTaskCompleteButtons.get(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!goals.get(goalNumber).getTask(1).isComplete()) {
                    tdTaskCompleteButtons.get(1).setText("Undo Complete");
                    goals.get(goalNumber).getTask(1).setCompletion(true);
                    tdTaskTitles.get(1).setPaintFlags(tdTaskTitles.get(1).getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    tdTaskCompleteButtons.get(1).setText("Complete");
                    goals.get(goalNumber).getTask(1).setCompletion(false);
                    tdTaskTitles.get(1).setPaintFlags(tdTaskTitles.get(1).getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }
                db.collection("users").document(userId).collection("goals").document(goals.get(goalNumber).getDocID()).update("task" + 2 + ".isComplete", goals.get(goalNumber).getTask(1).isComplete());
                updateDailyProgress();
                updateTotalProgress();
                updateDBDailyStreak();
            }
        });

        tdTaskCompleteButtons.get(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!goals.get(goalNumber).getTask(2).isComplete()) {
                    tdTaskCompleteButtons.get(2).setText("Undo Complete");
                    goals.get(goalNumber).getTask(2).setCompletion(true);
                    tdTaskTitles.get(2).setPaintFlags(tdTaskTitles.get(2).getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    tdTaskCompleteButtons.get(2).setText("Complete");
                    goals.get(goalNumber).getTask(2).setCompletion(false);
                    tdTaskTitles.get(2).setPaintFlags(tdTaskTitles.get(2).getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }
                db.collection("users").document(userId).collection("goals").document(goals.get(goalNumber).getDocID()).update("task" + 3 + ".isComplete", goals.get(goalNumber).getTask(2).isComplete());
                updateDailyProgress();
                updateTotalProgress();
                updateDBDailyStreak();
            }
        });

        tdTaskCompleteButtons.get(3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!goals.get(goalNumber).getTask(3).isComplete()) {
                    tdTaskCompleteButtons.get(3).setText("Undo Complete");
                    goals.get(goalNumber).getTask(3).setCompletion(true);
                    tdTaskTitles.get(3).setPaintFlags(tdTaskTitles.get(3).getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    tdTaskCompleteButtons.get(3).setText("Complete");
                    goals.get(goalNumber).getTask(3).setCompletion(false);
                    tdTaskTitles.get(3).setPaintFlags(tdTaskTitles.get(3).getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }
                db.collection("users").document(userId).collection("goals").document(goals.get(goalNumber).getDocID()).update("task" + 4 + ".isComplete", goals.get(goalNumber).getTask(3).isComplete());
                updateDailyProgress();
                updateTotalProgress();
                updateDBDailyStreak();
            }
        });

        tdTaskCompleteButtons.get(4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!goals.get(goalNumber).getTask(4).isComplete()) {
                    tdTaskCompleteButtons.get(4).setText("Undo Complete");
                    goals.get(goalNumber).getTask(4).setCompletion(true);
                    tdTaskTitles.get(4).setPaintFlags(tdTaskTitles.get(4).getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    tdTaskCompleteButtons.get(4).setText("Complete");
                    goals.get(goalNumber).getTask(4).setCompletion(false);
                    tdTaskTitles.get(4).setPaintFlags(tdTaskTitles.get(4).getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }
                db.collection("users").document(userId).collection("goals").document(goals.get(goalNumber).getDocID()).update("task" + 5 + ".isComplete", goals.get(goalNumber).getTask(4).isComplete());
                updateDailyProgress();
                updateTotalProgress();
                updateDBDailyStreak();
            }
        });

        tdExitUIButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                tdLayout.setVisibility(View.GONE);
                tdExitUIButton.setVisibility(View.GONE);
                dbResourcesButton.setVisibility(View.VISIBLE);
                dbCalendarButton.setVisibility(View.VISIBLE);
                dbResourcesButton.setVisibility(View.VISIBLE);
                dbGoalsButton.setVisibility(View.VISIBLE);
                for(int i = 0; i < goals.size(); i++)
                {
                    dbToDoButtons.get(i).setTranslationY(dbToDoButtons.get(i).getTranslationY() + 720);
                    dbToDoLabels.get(i).setTranslationY(dbToDoButtons.get(i).getTranslationY() + 5);
                }
            }
        });
        tdLayout.setVisibility(View.GONE);
    }

    private void runToDoUI()
    {
        tdExitUIButton.setVisibility(View.VISIBLE);
        tdGoalTitleText.setText(goals.get(goalNumber).getName());
        for(View layout : tdTaskLayouts)
            layout.setVisibility(View.GONE);
        if(!(tdLayout.getVisibility() == View.VISIBLE))
            for(int i = 0; i < goals.size(); i++)
            {
                dbToDoButtons.get(i).setTranslationY(dbToDoButtons.get(i).getTranslationY() - 720);
                dbToDoLabels.get(i).setTranslationY(dbToDoButtons.get(i).getTranslationY() - 5);
            }

        for(int i = 0; i < goals.get(goalNumber).getTaskAmount(); i++)
        {
            tdTaskLayouts.get(i).setVisibility(View.VISIBLE);
            tdTaskTitles.get(i).setText(goals.get(goalNumber).getTask(i).getName());
            tdTaskDescriptions.get(i).setText(goals.get(goalNumber).getTask(i).getDescription());
            if (goals.get(goalNumber).getTask(i).isComplete())
            {
                tdTaskCompleteButtons.get(i).setText("Undo Complete");
                tdTaskTitles.get(i).setPaintFlags(tdTaskTitles.get(i).getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
            else
            {
                tdTaskCompleteButtons.get(i).setText("Complete");
                tdTaskTitles.get(i).setPaintFlags(tdTaskTitles.get(i).getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
        }
        dbResourcesButton.setVisibility(View.GONE);
        dbCalendarButton.setVisibility(View.GONE);
        dbResourcesButton.setVisibility(View.GONE);
        dbGoalsButton.setVisibility(View.GONE);
        tdLayout.setVisibility(View.VISIBLE);
    }

    //Quote Screen Functions
    private void assignQuoteScreenViews() {
        // after assignDashboard() in onCreate():
        mainLayout      = findViewById(R.id.mainLayout);
        quoteOverlay    = findViewById(R.id.quoteOverlay);
        quoteTextView   = findViewById(R.id.quoteTextView);
        authorTextView  = findViewById(R.id.authorTextView);

        // prepare handler for auto-hide
        quoteHandler      = new Handler(Looper.getMainLooper());
        hideQuoteRunnable = this::hideQuoteScreen;
    }

    private void showQuoteScreen() {
        // Hide dashboard & show overlay, but start invisible
        assignQuoteScreenViews();

        mainLayout.setVisibility(View.GONE);
        quoteOverlay.setAlpha(0f);
        quoteOverlay.setVisibility(View.VISIBLE);

        refreshQuote();

        // Fade the overlay itself in
        quoteOverlay.animate()
                .alpha(1f)
                .setDuration(500)
                .start();

        // Now fade in the quote text, author, and refresh button
        quoteTextView.setAlpha(0f);
        authorTextView.setAlpha(0f);

        quoteTextView.animate()
                .alpha(1f)
                .setStartDelay(300)
                .setDuration(500)
                .start();

        authorTextView.animate()
                .alpha(1f)
                .setStartDelay(500)
                .setDuration(500)
                .start();

        // Auto-hide after 5 seconds (with fade-out)
        quoteHandler.postDelayed(hideQuoteRunnable, 5000);
    }

    private void hideQuoteScreen() {
        // Fade out the overlay
        quoteOverlay.animate()
                .alpha(0f)
                .setDuration(500)
                .withEndAction(() -> {
                    // when the animation ends, hide it and show the dashboard
                    quoteOverlay.setVisibility(View.GONE);
                    mainLayout.setVisibility(View.VISIBLE);

                    // reset overlay alpha for next time
                    quoteOverlay.setAlpha(1f);
                })
                .start();
    }

    private void refreshQuote() {
        Quote newQuote = getRandomQuote();
        quoteTextView.setText("“" + newQuote.getQuote() + "”");
        authorTextView.setText("- " + newQuote.getAuthor());
    }

    // Simple in-activity Quote class + random picker
    private static class Quote {
        private final String quote, author;
        Quote(String q, String a) { quote = q; author = a; }
        String getQuote()  { return quote; }
        String getAuthor() { return author; }
    }

    private Quote getRandomQuote() {
        List<Quote> quotes = new ArrayList<>();
        quotes.add(new Quote("The only way to do great work is to love what you do.", "Steve Jobs"));
        quotes.add(new Quote("Strive not to be a success, but rather to be of value.", "Albert Einstein"));
        quotes.add(new Quote("I have not failed. I've just found 10,000 ways that won't work.", "Thomas A. Edison"));
        quotes.add(new Quote("The mind is everything. What you think you become.", "Buddha"));
        quotes.add(new Quote("The best and most beautiful things in the world cannot be seen or even touched—they must be felt with the heart.", "Helen Keller"));
        quotes.add(new Quote("Success usually comes to those who are too busy to be looking for it.", "Henry David Thoreau"));
        quotes.add(new Quote("Don’t watch the clock; do what it does. Keep going.", "Sam Levenson"));
        quotes.add(new Quote("Keep your eyes on the stars, and your feet on the ground.", "Theodore Roosevelt"));
        quotes.add(new Quote("The future depends on what you do today.", "Mahatma Gandhi"));
        quotes.add(new Quote("It does not matter how slowly you go as long as you do not stop.", "Confucius"));
        quotes.add(new Quote("Everything you’ve ever wanted is on the other side of fear.", "George Addair"));
        quotes.add(new Quote("Hardships often prepare ordinary people for an extraordinary destiny.", "C.S. Lewis"));
        quotes.add(new Quote("Believe you can and you’re halfway there.", "Theodore Roosevelt"));
        quotes.add(new Quote("What we achieve inwardly will change outer reality.", "Plutarch"));
        quotes.add(new Quote("Either you run the day or the day runs you.", "Jim Rohn"));
        quotes.add(new Quote("Act as if what you do makes a difference. It does.", "William James"));
        quotes.add(new Quote("Quality is not an act, it is a habit.", "Aristotle"));
        quotes.add(new Quote("The secret of getting ahead is getting started.", "Mark Twain"));
        quotes.add(new Quote("Don’t limit your challenges. Challenge your limits.", "Unknown"));
        quotes.add(new Quote("Dream big and dare to fail.", "Norman Vaughan"));
        quotes.add(new Quote("If opportunity doesn’t knock, build a door.", "Milton Berle"));
        quotes.add(new Quote("You miss 100% of the shots you don’t take.", "Wayne Gretzky"));
        quotes.add(new Quote("The only person you are destined to become is the person you decide to be.", "Ralph Waldo Emerson"));
        quotes.add(new Quote("Go confidently in the direction of your dreams. Live the life you have imagined.", "Henry David Thoreau"));
        quotes.add(new Quote("Start where you are. Use what you have. Do what you can.", "Arthur Ashe"));
        quotes.add(new Quote("The harder the conflict, the more glorious the triumph.", "Thomas Paine"));
        quotes.add(new Quote("Don’t be afraid to give up the good to go for the great.", "John D. Rockefeller"));
        quotes.add(new Quote("I attribute my success to this: I never gave or took any excuse.", "Florence Nightingale"));
        quotes.add(new Quote("You are never too old to set another goal or to dream a new dream.", "C.S. Lewis"));
        quotes.add(new Quote("A champion is defined not by their wins but by how they can recover when they fall.", "Serena Williams"));
        quotes.add(new Quote("It always seems impossible until it’s done.", "Nelson Mandela"));
        quotes.add(new Quote("Fall seven times, stand up eight.", "Japanese Proverb"));
        quotes.add(new Quote("When the going gets tough, the tough get going.", "Joe Kennedy"));
        quotes.add(new Quote("Everything you can imagine is real.", "Pablo Picasso"));
        quotes.add(new Quote("Believe in yourself and all that you are.", "Christian D. Larson"));
        quotes.add(new Quote("Don’t count the days, make the days count.", "Muhammad Ali"));
        quotes.add(new Quote("Impossible is just an opinion.", "Paulo Coelho"));
        quotes.add(new Quote("The only limit to our realization of tomorrow is our doubts of today.", "Franklin D. Roosevelt"));
        quotes.add(new Quote("Perseverance is failing 19 times and succeeding the 20th.", "Julie Andrews"));
        quotes.add(new Quote("What you get by achieving your goals is not as important as what you become by achieving your goals.", "Zig Ziglar"));
        quotes.add(new Quote("Keep going. Everything you need will come to you at the perfect time.", "Unknown"));
        quotes.add(new Quote("You don’t have to be great to start, but you have to start to be great.", "Zig Ziglar"));
        quotes.add(new Quote("Success is not final, failure is not fatal: it is the courage to continue that counts.", "Winston Churchill"));
        quotes.add(new Quote("The best time to plant a tree was 20 years ago. The second best time is now.", "Chinese Proverb"));
        quotes.add(new Quote("Don’t wish it were easier. Wish you were better.", "Jim Rohn"));
        quotes.add(new Quote("Everything has beauty, but not everyone can see.", "Confucius"));
        quotes.add(new Quote("Do something today that your future self will thank you for.", "Unknown"));
        quotes.add(new Quote("The key to success is to focus on goals, not obstacles.", "Unknown"));
        quotes.add(new Quote("Small steps in the right direction can turn out to be the biggest step of your life.", "Unknown"));
        quotes.add(new Quote("The difference between ordinary and extraordinary is that little extra.", "Jimmy Johnson"));
        quotes.add(new Quote("Challenges are what make life interesting; overcoming them is what makes life meaningful.", "Joshua Marine"));
        quotes.add(new Quote("Strength does not come from physical capacity. It comes from indomitable will.", "Mahatma Gandhi"));
        quotes.add(new Quote("You are braver than you believe, stronger than you seem and smarter than you think.", "A.A. Milne"));
        quotes.add(new Quote("Opportunities don’t happen, you create them.", "Chris Grosser"));
        quotes.add(new Quote("Success is walking from failure to failure with no loss of enthusiasm.", "Winston Churchill"));
        quotes.add(new Quote("Dream big. Start small. Act now.", "Robin Sharma"));
        quotes.add(new Quote("If you can dream it, you can achieve it.", "Zig Ziglar"));
        quotes.add(new Quote("Magic is believing in yourself, if you can do that, you can make anything happen.", "Johann Wolfgang von Goethe"));
        quotes.add(new Quote("The only way to achieve the impossible is to believe it is possible.", "Charles Kingsleigh"));
        quotes.add(new Quote("The harder I work, the luckier I get.", "Samuel Goldwyn"));
        quotes.add(new Quote("Don’t stop until you’re proud.", "Unknown"));
        quotes.add(new Quote("Wake up with determination. Go to bed with satisfaction.", "Unknown"));
        quotes.add(new Quote("The man who moves a mountain begins by carrying away small stones.", "Confucius"));
        quotes.add(new Quote("Success usually comes to those who are too busy to be looking for it.", "Henry David Thoreau"));
        quotes.add(new Quote("Opportunities don’t happen, you create them.", "Chris Grosser"));
        quotes.add(new Quote("Try not to become a man of success but rather try to become a man of value.", "Albert Einstein"));
        quotes.add(new Quote("What seems to us as bitter trials are often blessings in disguise.", "Oscar Wilde"));
        quotes.add(new Quote("It’s not whether you get knocked down; it’s whether you get up.", "Vince Lombardi"));
        quotes.add(new Quote("The road to freedom is uphill, even if it’s downhill.", "Arnold Schwarzenegger"));
        quotes.add(new Quote("No masterpiece was ever created by a lazy artist.", "Unknown"));
        quotes.add(new Quote("Good things come to people who wait, but better things come to those who go out and get them.", "Unknown"));
        quotes.add(new Quote("Success is the sum of small efforts repeated day in and day out.", "Robert Collier"));
        quotes.add(new Quote("Positive anything is better than negative nothing.", "Elbert Hubbard"));
        quotes.add(new Quote("Energy and persistence conquer all things.", "Benjamin Franklin"));
        quotes.add(new Quote("Life is 10% what happens to us and 90% how we react to it.", "Charles R. Swindoll"));
        quotes.add(new Quote("I find that the harder I work, the more luck I seem to have.", "Thomas Jefferson"));
        quotes.add(new Quote("Don’t let yesterday take up too much of today.", "Will Rogers"));
        quotes.add(new Quote("The only place where success comes before work is in the dictionary.", "Vidal Sassoon"));
        quotes.add(new Quote("The secret of getting things done is to act!", "Unknown"));
        quotes.add(new Quote("The best revenge is massive success.", "Frank Sinatra"));
        quotes.add(new Quote("You must do the thing you think you cannot do.", "Eleanor Roosevelt"));
        quotes.add(new Quote("Success is liking yourself, liking what you do, and liking how you do it.", "Maya Angelou"));
        quotes.add(new Quote("There are no shortcuts to any place worth going.", "Beverly Sills"));
        quotes.add(new Quote("Go the extra mile. It’s never crowded there.", "Dr. Wayne Dyer"));
        quotes.add(new Quote("Hard work beats talent when talent doesn’t work hard.", "Tim Notke"));
        quotes.add(new Quote("If you want something you’ve never had, you must be willing to do something you’ve never done.", "Thomas Jefferson"));
        quotes.add(new Quote("Failure will never overtake me if my determination to succeed is strong enough.", "Og Mandino"));
        quotes.add(new Quote("You don’t have to be great to start, but you have to start to be great.", "Zig Ziglar"));
        quotes.add(new Quote("Either you run the day or the day runs you.", "Jim Rohn"));
        quotes.add(new Quote("The way to get started is to quit talking and start doing.", "Walt Disney"));
        quotes.add(new Quote("Only put off until tomorrow what you are willing to die having left undone.", "Pablo Picasso"));
        quotes.add(new Quote("The pain you feel today will be the strength you feel tomorrow.", "Unknown"));
        quotes.add(new Quote("Don’t wait. The time will never be just right.", "Napoleon Hill"));
        quotes.add(new Quote("We generate fears while we sit. We overcome them by action.", "Dr. Henry Link"));
        quotes.add(new Quote("Quality is more important than quantity. One home run is much better than two doubles.", "Steve Jobs"));
        quotes.add(new Quote("It’s not about ideas. It’s about making ideas happen.", "Scott Belsky"));
        quotes.add(new Quote("If you are not willing to risk the usual you will have to settle for the ordinary.", "Jim Rohn"));
        quotes.add(new Quote("Build your own dreams, or someone else will hire you to build theirs.", "Farrah Gray"));
        quotes.add(new Quote("Do one thing every day that scares you.", "Eleanor Roosevelt"));
        quotes.add(new Quote("Whenever you see a successful person you only see the public glories, never the private sacrifices.", "Vaibhav Shah"));
        quotes.add(new Quote("What you lack in talent can be made up with desire, hustle, and giving 110% all the time.", "Don Zimmer"));
        quotes.add(new Quote("Develop success from failures. Discouragement and failure are two of the surest stepping stones to success.", "Dale Carnegie"));
        quotes.add(new Quote("You don’t drown by falling in water; you drown by staying there.", "Ed Cole"));
        quotes.add(new Quote("The distance between insanity and genius is measured only by success.", "Bruce Feirstein"));

        int idx = new Random().nextInt(quotes.size());
        return quotes.get(idx);
    }

    //Resource view (rv) Functions
    private void assignResourceView() //Assigns objects for the overview fragment
    {
        rvPersonalizedContainer = findViewById(R.id.personalized_container);

        Button dashboardButton = findViewById(R.id.resource_dashboard_Button);
        dashboardButton.setOnClickListener(v -> {
            setContentView(R.layout.fragment_dashboard);
            assignDashboard();
        });

        // Wire up the static links
        setupStaticLinks();

        fetchPersonalizedResources();
    }

    private void setupStaticLinks()
    {
        for (int i = 0; i < rvINCLUDE_IDS.length; i++)
        {
            View item = findViewById(rvINCLUDE_IDS[i]);
            String url = rvSTATIC_URLS[i];
            TextView linkView = item.findViewById(R.id.resource_link);
            linkView.setText(url);
            linkView.setOnClickListener(v ->
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url))));
            Linkify.addLinks(linkView, Linkify.WEB_URLS);
        }
    }

    private void fetchPersonalizedResources()
    {
        String prompt = "Find three reputable online resources (URLs only) to help achieve a random goal. Format as one URL per line, No other text, (Top Priority) No number list, and just provide the home page URLs.";
        new OpenAIManager().sendPrompt(prompt, new OpenAIManager.OpenAIResponseCallback()
        {
            @Override
            public void onSuccess(String json)
            {
                try
                {
                    // parse JSON → choices[0].message.content
                    JSONObject root = new JSONObject(json);
                    String content = root
                            .getJSONArray("choices")
                            .getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content");

                    // Expect lines of URLs
                    for (String line : content.split("\n"))
                    {
                        String url = line.trim();
                        if (url.startsWith("http"))
                        {
                            addPersonalizedLink(url);
                        }
                    }
                }
                catch (Exception e)
                {
                    runOnUiThread(() ->
                            Toast.makeText(MainAppActivity.this,
                                    "Error parsing resources", Toast.LENGTH_SHORT).show());
                }
            }
            @Override
            public void onFailure(Exception e)
            {
                runOnUiThread(() ->
                        Toast.makeText(MainAppActivity.this,
                                "Failed to load personalized links", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void addPersonalizedLink(String url) {
        runOnUiThread(() -> {
            View item = LayoutInflater.from(this)
                    .inflate(R.layout.item_resource, rvPersonalizedContainer, false);
            TextView linkView = item.findViewById(R.id.resource_link);
            linkView.setText(url);
            linkView.setOnClickListener(v ->
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url))));
            rvPersonalizedContainer.addView(item);
        });
    }

    //Calendar View (cv) Functions
    private void assignCalendarView() {

        // build 12-month window (6 past, current, 5 future)
        monthsList = new ArrayList<>();
        Calendar mCal = Calendar.getInstance();
        mCal.add(Calendar.MONTH, -6);
        for (int i = 0; i < 12; i++) {
            monthsList.add(new YearMonth(
                    mCal.get(Calendar.YEAR),
                    mCal.get(Calendar.MONTH)
            ));
            mCal.add(Calendar.MONTH, 1);
        }

        pager = findViewById(R.id.calendarPager);
        View noGoalsText = findViewById(R.id.noGoalsText);

        // load goals and render their buttons
        //loadGoals();
        setupGoalButtons();

        // default to first goal
        if (!goals.isEmpty()) {
            selectGoal(goals.get(0));
            noGoalsText.setVisibility(View.GONE);
        } else {
            // display the calendar view even if no goals are loaded
            // install a default adapter without any highlight sets
            pager.setAdapter(new CalendarPagerAdapter(monthsList, Collections.emptySet(), Collections.emptySet()));
        }

        // Find the new header TextView
        TextView monthHeader = findViewById(R.id.monthHeader);

        // After setting adapter, initialize header to the current page:
        int defaultPosition = 6;
        updateMonthHeader(defaultPosition, monthHeader);

        // Add a page‐change callback to update it as you swipe:
        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateMonthHeader(position, monthHeader);
            }
        });

        // back button returns you to the dashboard
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            setContentView(R.layout.fragment_dashboard);
            assignDashboard();
        });
    }

    /** Helper to display "Month Year" for page at index */
    private void updateMonthHeader(int position, TextView header) {
        MainAppActivity.YearMonth ym = monthsList.get(position);
        String monthName = months[ym.month];
        String text = monthName + " " + ym.year;
        header.setText(text);
    }

    private void setupGoalButtons() {
        LinearLayout container = findViewById(R.id.goalButtonsContainer);
        container.removeAllViews();

        int padding = dpToPx(8);
        int margin  = dpToPx(4);   // 4dp margin around each button

        for (Goal goal : goals) {
            Button btn = new Button(this);
            btn.setText(goal.getName());
            btn.setTextColor(getResources().getColor(R.color.white));
            btn.setBackground(getResources().getDrawable(R.drawable.dark_bubble));
            btn.setAllCaps(false);
            btn.setPadding(padding, padding, padding, padding);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );

            lp.setMargins(margin, margin, margin, margin);

            btn.setLayoutParams(lp);
            btn.setOnClickListener(v -> selectGoal(goal));

            container.addView(btn);
        }
    }

    private void selectGoal(Goal goal) {
        // parse end date
        Calendar goalCal = Calendar.getInstance();
        try {
            String[] p = goal.getCompletionDate().split("/");
            goalCal.set(Integer.parseInt(p[2]), Integer.parseInt(p[0]) - 1, Integer.parseInt(p[1]));
        } catch (Exception e) { /* use today on parse fail */ }
        zeroTime(goalCal);

        // build streak days set
        Set<Long> streakSet = new HashSet<>();
        Calendar c = Calendar.getInstance();
        zeroTime(c);
        for (int i = 0; i < goal.getDailyStreak(); i++) {
            streakSet.add(c.getTimeInMillis());
            c.add(Calendar.DAY_OF_MONTH, -1);
        }

        // end date set
        Set<Long> endSet = Collections.singleton(goalCal.getTimeInMillis());

        // install the adapter
        CalendarPagerAdapter adapter = new CalendarPagerAdapter(
                monthsList, streakSet, endSet
        );
        pager.setAdapter(adapter);
        // center on “current” month
        pager.setCurrentItem(6, false);
    }

    private void zeroTime(Calendar c) {
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE,      0);
        c.set(Calendar.SECOND,      0);
        c.set(Calendar.MILLISECOND, 0);
    }

    private int dpToPx(int dp) {
        float d = getResources().getDisplayMetrics().density;
        return Math.round(dp * d);
    }

    // inner holder for year+month pairs
    public static class YearMonth {
        public final int year;
        public final int month;
        YearMonth(int y, int m) { year = y; month = m; }
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

        goViewButtons.clear();
        goViewButtons.add(findViewById(R.id.goalOneViewButton));
        goViewButtons.add(findViewById(R.id.goalTwoViewButton));
        goViewButtons.add(findViewById(R.id.goalThreeViewButton));
        goViewButtons.add(findViewById(R.id.goalFourViewButton));
        goViewButtons.add(findViewById(R.id.goalFiveViewButton));

        goBackButton = findViewById(R.id.backButton);
        goCreateButton = findViewById(R.id.createButton);
        goCreateFirstButton = findViewById(R.id.noGoalsButton);


        goViewButtons.get(0).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                goalNumber = 0;
                tempGoal = goals.get(0);
                setContentView(R.layout.fragment_goals_view);
                assignGoalsView();
            }
        });
        goViewButtons.get(1).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                goalNumber = 1;
                tempGoal = goals.get(1);
                setContentView(R.layout.fragment_goals_view);
                assignGoalsView();
            }
        });
        goViewButtons.get(2).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                goalNumber = 2;
                tempGoal = goals.get(2);
                setContentView(R.layout.fragment_goals_view);
                assignGoalsView();
            }
        });
        goViewButtons.get(3).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                goalNumber = 3;
                tempGoal = goals.get(3);
                setContentView(R.layout.fragment_goals_view);
                assignGoalsView();
            }
        });
        goViewButtons.get(4).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                goalNumber = 4;
                tempGoal = goals.get(4);
                setContentView(R.layout.fragment_goals_view);
                assignGoalsView();
            }
        });

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
                isFromGoalView = false;
                isNewGoal = true;
                isNewTask = true;
                setContentView(R.layout.fragment_goals_create);
                assignGoalsCreate();
            }
        });
        goCreateButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                isFromGoalView = false;
                isNewGoal = true;
                isNewTask = true;
                setContentView(R.layout.fragment_goals_create);
                assignGoalsCreate();
            }
        });
        updateGoalsOverview();
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
        gcNextButton = findViewById(R.id.goalNextButton);
        gcNameInput = findViewById(R.id.textInputName);
        gcDescriptionInput = findViewById(R.id.textInputDescription);
        gcCompletionDate = findViewById((R.id.textInputTimeline));

        if(isFromGoalView)
        {
            gcCancelButton.setText("Delete Goal");
            gcNextButton.setText("Save Goal");
        }
        gcCancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(isNewGoal) {
                    setContentView(R.layout.fragment_goals_overview);
                    assignGoalsOverview();
                }
                else
                {
                    deleteGoalFromFirestore(goals.get(goalNumber));
                    goals.remove(goalNumber);
                    setContentView(R.layout.fragment_goals_overview);
                    assignGoalsOverview();
                }
            }
        });

        gcNextButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    dateFormat.parse(gcCompletionDate.getText().toString());
                }
                catch(ParseException e)
                {
                    return;
                }
                if(isNewGoal) {
                    goals.add(new Goal(gcNameInput.getText().toString(), gcDescriptionInput.getText().toString(), currentDate, gcCompletionDate.getText().toString()));
                    setContentView(R.layout.fragment_tasks_create);
                    assignTasksCreate();
                    if(!isNewTask)
                    {
                        tcNameInput.setText(tempGoal.getTask(0).getName());
                        tcDescriptionInput.setText(tempGoal.getTask(0).getDescription());
                    }
                    isNewTask = true;
                }
                else
                {
                    goals.get(goalNumber).setName(gcNameInput.getText().toString());
                    goals.get(goalNumber).setDescription(gcDescriptionInput.getText().toString());
                    goals.get(goalNumber).setCompletionDate(gcCompletionDate.getText().toString());
                    setContentView(R.layout.fragment_goals_view);
                    assignGoalsView();
                }
            }
        });
    }


    //Goal View (gv) Functions
    private void assignGoalsView() //Assigns objects for the create fragment
    {

        gvGoalTitleText = findViewById(R.id.goalViewTitle);
        gvGoalDescriptionText = findViewById(R.id.goalViewDescription);
        gvDailyStreakText = findViewById(R.id.streakCounterText);
        gvStartDateText = findViewById(R.id.startDate);
        gvEndDateText = findViewById(R.id.completionDate);

        gvTaskLayouts.clear();
        gvTaskLayouts.add(findViewById(R.id.taskOneLayout));
        gvTaskLayouts.add(findViewById(R.id.taskTwoLayout));
        gvTaskLayouts.add(findViewById(R.id.taskThreeLayout));
        gvTaskLayouts.add(findViewById(R.id.taskFourLayout));
        gvTaskLayouts.add(findViewById(R.id.taskFiveLayout));

        gvTaskTitles.clear();
        gvTaskTitles.add(findViewById(R.id.taskOneTitle));
        gvTaskTitles.add(findViewById(R.id.taskTwoTitle));
        gvTaskTitles.add(findViewById(R.id.taskThreeTitle));
        gvTaskTitles.add(findViewById(R.id.taskFourTitle));
        gvTaskTitles.add(findViewById(R.id.taskFiveTitle));

        gvTaskDescriptions.clear();
        gvTaskDescriptions.add(findViewById(R.id.taskOneDescription));
        gvTaskDescriptions.add(findViewById(R.id.taskTwoDescription));
        gvTaskDescriptions.add(findViewById(R.id.taskThreeDescription));
        gvTaskDescriptions.add(findViewById(R.id.taskFourDescription));
        gvTaskDescriptions.add(findViewById(R.id.taskFiveDescription));

        gvEditButtons.clear();
        gvEditButtons.add(findViewById(R.id.taskOneEditButton));
        gvEditButtons.add(findViewById(R.id.taskTwoEditButton));
        gvEditButtons.add(findViewById(R.id.taskThreeEditButton));
        gvEditButtons.add(findViewById(R.id.taskFourEditButton));
        gvEditButtons.add(findViewById(R.id.taskFiveEditButton));

        gvBackButton = findViewById(R.id.backButton);
        gvCreateButton = findViewById(R.id.createButton);
        gvEditGoalButton = findViewById(R.id.editGoalButton);

        gvEditButtons.get(0).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                isFromGoalView = true;
                isNewGoal = false;
                isNewTask = false;
                taskNumber = 0;
                setContentView(R.layout.fragment_tasks_create);
                assignTasksCreate();
                tcNameInput.setText(goals.get(goalNumber).getTask(0).getName());
                tcDescriptionInput.setText(goals.get(goalNumber).getTask(0).getDescription());
            }
        });
        gvEditButtons.get(1).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                isFromGoalView = true;
                isNewGoal = false;
                isNewTask = false;
                taskNumber = 1;
                setContentView(R.layout.fragment_tasks_create);
                assignTasksCreate();
                tcNameInput.setText(goals.get(goalNumber).getTask(1).getName());
                tcDescriptionInput.setText(goals.get(goalNumber).getTask(1).getDescription());
            }
        });
        gvEditButtons.get(2).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                isFromGoalView = true;
                isNewGoal = false;
                isNewTask = false;
                taskNumber = 2;
                setContentView(R.layout.fragment_tasks_create);
                assignTasksCreate();
                tcNameInput.setText(goals.get(goalNumber).getTask(2).getName());
                tcDescriptionInput.setText(goals.get(goalNumber).getTask(2).getDescription());
            }
        });
        gvEditButtons.get(3).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                isFromGoalView = true;
                isNewGoal = false;
                isNewTask = false;
                taskNumber = 3;
                setContentView(R.layout.fragment_tasks_create);
                assignTasksCreate();
                tcNameInput.setText(goals.get(goalNumber).getTask(3).getName());
                tcDescriptionInput.setText(goals.get(goalNumber).getTask(3).getDescription());
            }
        });
        gvEditButtons.get(4).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                isFromGoalView = true;
                isNewGoal = false;
                isNewTask = false;
                taskNumber = 4;
                setContentView(R.layout.fragment_tasks_create);
                assignTasksCreate();
                tcNameInput.setText(goals.get(goalNumber).getTask(4).getName());
                tcDescriptionInput.setText(goals.get(goalNumber).getTask(4).getDescription());
            }
        });

        gvBackButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setContentView(R.layout.fragment_goals_overview);
                assignGoalsOverview();
                saveGoalToFirestore(goals.get(goalNumber));
            }
        });
        gvCreateButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                isFromGoalView = true;
                isNewGoal = false;
                isNewTask = true;
                setContentView(R.layout.fragment_tasks_create);
                assignTasksCreate();
            }
        });
        gvEditGoalButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                isFromGoalView = true;
                isNewGoal = false;
                isNewTask = true;
                setContentView(R.layout.fragment_goals_create);
                assignGoalsCreate();
                gcNameInput.setText(goals.get(goalNumber).getName());
                gcDescriptionInput.setText(goals.get(goalNumber).getDescription());
                gcCompletionDate.setText(goals.get(goalNumber).getCompletionDate());
            }
        });
        updateGoalsView();
    }

    private void updateGoalsView()
    {
        for (int i = 0; i < gvTaskLayouts.size(); i++)
            gvTaskLayouts.get(i).setVisibility(View.INVISIBLE);

        if(goals.get(goalNumber).getTaskAmount() > 0)
        {
            for (int i = 0; i < goals.get(goalNumber).getTaskAmount(); i++)
            {
                gvTaskLayouts.get(i).setVisibility(View.VISIBLE);
                gvTaskTitles.get(i).setText(goals.get(goalNumber).getTask(i).getName());
                gvTaskDescriptions.get(i).setText(goals.get(goalNumber).getTask(i).getDescription());
            }
            String streakText = "" + goals.get(goalNumber).getDailyStreak();
            gvDailyStreakText.setText(streakText);
            gvStartDateText.setText(goals.get(goalNumber).getStartDate());
            gvEndDateText.setText(goals.get(goalNumber).getCompletionDate());
            gvGoalTitleText.setText(goals.get(goalNumber).getName());
            gvGoalDescriptionText.setText(goals.get(goalNumber).getDescription());
        }
        if(goals.get(goalNumber).getTaskAmount() >= 5)
            gvCreateButton.setVisibility(View.INVISIBLE);
    }

    //Task Create (tc) Functions
    private void assignTasksCreate() //Assigns objects for the create fragment
    {
        tcCancelButton = findViewById(R.id.taskCreateBackButton);
        tcDescriptionInput = findViewById(R.id.taskCreateTextInputDescription);
        tcNameInput = findViewById(R.id.taskCreateTextInputName);
        tcSaveButton = findViewById(R.id.taskCreateSaveButton);
        if(isFromGoalView)
            tcCancelButton.setText("Delete Task");
        tcCancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(isFromGoalView)
                {
                    if(isNewTask)
                    {
                        //A new Task from an existing goal
                        setContentView(R.layout.fragment_goals_view);
                        assignGoalsView();
                    }
                    else
                    {
                        //an existing task from an existing goal
                        goals.get(goalNumber).deleteTask(taskNumber);
                        setContentView(R.layout.fragment_goals_view);
                        assignGoalsView();
                    }
                }
                else
                {
                    //A new task from a new goal
                    goals.get(goals.size() - 1).createTask(tcNameInput.getText().toString(), tcDescriptionInput.getText().toString());
                    isNewTask = false;
                    tempGoal = goals.get(goals.size()-1);
                    goals.remove(goals.size()-1);

                    setContentView(R.layout.fragment_goals_create);
                    assignGoalsCreate();
                    gcNameInput.setText(tempGoal.getName());
                    gcDescriptionInput.setText(tempGoal.getDescription());
                    gcCompletionDate.setText(tempGoal.getCompletionDate());
                }

            }
        });
        tcSaveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(isFromGoalView)
                {
                    if (isNewTask)
                    {
                        //new task and existing goal
                        goals.get(goalNumber).createTask(tcNameInput.getText().toString(), tcDescriptionInput.getText().toString());
                        tempGoal = goals.get(goalNumber);
                        saveGoalToFirestore(goals.get(goalNumber));
                        setContentView(R.layout.fragment_goals_view);
                        assignGoalsView();
                    } else {
                        //existing task and existing goal
                        goals.get(goalNumber).getTask(goalNumber).setName(tcNameInput.getText().toString());
                        goals.get(goalNumber).getTask(goalNumber).setDescription(tcDescriptionInput.getText().toString());
                        tempGoal = goals.get(goalNumber);
                        saveGoalToFirestore(goals.get(goalNumber));
                        setContentView(R.layout.fragment_goals_view);
                        assignGoalsView();
                    }
                }
                else
                {
                    //new task and new goal
                    goals.get(goals.size() - 1).createTask(tcNameInput.getText().toString(), tcDescriptionInput.getText().toString());
                    setContentView(R.layout.fragment_goals_overview);
                    assignGoalsOverview();
                    saveGoalToFirestore(goals.get(goals.size() - 1));
                }
            }
        });
    }

    //Settings View (sv) Functions
    private void assignSettingsView() //Assigns objects for the create fragment
    {
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        svBackButton = findViewById(R.id.settingsBackButton);
        svSignOutButton = findViewById(R.id.settingsSignOutButton);
        svViewQuestionnaireButton = findViewById(R.id.settingsQuestionnaireButton);
        svChangePasswordButton = findViewById(R.id.settingsPasswordButton);
        svDeleteAccountButton = findViewById(R.id.settingsDeleteButton);
        svDeleteText = findViewById(R.id.warningDeleteText);
        EditText newPasswordInput = findViewById(R.id.svNewPasswordInput);
        Button confirmButton = findViewById(R.id.svConfirmChangePasswordButton);

        svDeleteText.setVisibility(View.INVISIBLE);
        newPasswordInput.setVisibility(View.INVISIBLE);
        confirmButton.setVisibility(View.INVISIBLE);

        svBackButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setContentView(R.layout.fragment_dashboard);
                assignDashboard();
            }
        });

        svSignOutButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                auth.signOut();
                startActivity(new Intent(MainAppActivity.this, LoginActivity.class));
            }
        });

        svDeleteAccountButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                isPasswordChange = false;
                svDeleteText.setVisibility(View.VISIBLE);
                newPasswordInput.setVisibility(View.VISIBLE);
                confirmButton.setText("DELETE ACCOUNT");
                newPasswordInput.requestFocus();
                newPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT);
                newPasswordInput.setHint("type 'DELETE'");
            }
        });

        svViewQuestionnaireButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(MainAppActivity.this, QuestionnaireActivity.class));
            }
        });

        svChangePasswordButton.setOnClickListener(v ->
        {
            isPasswordChange = true;
            svDeleteText.setVisibility(View.INVISIBLE);
            newPasswordInput.setVisibility(View.VISIBLE);
            confirmButton.setText("Confirm Change");
            newPasswordInput.setHint("Enter new password");
            newPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            newPasswordInput.requestFocus(); // Optional: opens keyboard
        });

        // Show confirm button only when user starts typing
        newPasswordInput.setOnFocusChangeListener((v, hasFocus) ->
        {
            if (hasFocus) {
                confirmButton.setVisibility(View.VISIBLE);
            }
        });

        confirmButton.setOnClickListener(v ->
        {
            if(isPasswordChange)
            {
                String newPassword = newPasswordInput.getText().toString();
                if (!newPassword.isEmpty())
                {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null)
                    {
                        user.updatePassword(newPassword).addOnCompleteListener(task ->
                        {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                                newPasswordInput.setVisibility(View.INVISIBLE);
                                confirmButton.setVisibility(View.INVISIBLE);
                                newPasswordInput.setText("");
                            } else {
                                Toast.makeText(this, "Failed to update password", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                else
                {
                    Toast.makeText(this, "Please enter a new password", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                if (newPasswordInput.getText().toString().equals("DELETE"))
                {
                    FirebaseAuth.getInstance().getCurrentUser().delete();
                    startActivity(new Intent(MainAppActivity.this, LoginActivity.class));
                }
                else
                    Toast.makeText(this, "Type 'DELETE' first! (case sensitive)", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadFirestoreData()
    {
        try
        {
            // Load previous date
            Task<DocumentSnapshot> userDateTask = db.collection("users").document(userId).get();
            DocumentSnapshot userDateSnapshot = Tasks.await(userDateTask); // Wait for Firestore response
            if (userDateSnapshot.exists()) {
                previousDate = userDateSnapshot.getString("currentDate");
            }

            // Update current date in Firestore
            Map<String, Object> updateData = new HashMap<>();
            updateData.put("currentDate", currentDate);
            Task<Void> updateTask = db.collection("users").document(userId).update(updateData);
            Tasks.await(updateTask); // Wait for update completion

            // Load goals
            Task<QuerySnapshot> goalsTask = db.collection("users").document(userId).collection("goals").orderBy("startDate", Query.Direction.ASCENDING).get();
            QuerySnapshot goalsSnapshot = Tasks.await(goalsTask); // Wait for Firestore response
            for (DocumentSnapshot doc : goalsSnapshot.getDocuments())
            {
                String name = doc.getString("name");
                String description = doc.getString("description");
                String startDate = doc.getString("startDate");
                String completionDate = doc.getString("completionDate");
                int totalProgress = doc.getLong("totalProgress").intValue();
                int dailyStreak = doc.getLong("dailyStreak").intValue();
                boolean dailyCompletion = doc.getBoolean("completedToday");
                String docID = doc.getId();
                Goal goal = new Goal(name, description, startDate, completionDate, totalProgress, dailyStreak, dailyCompletion, docID);

                // Load tasks inside each goal
                for (int i = 0; i < 5; i++)
                {
                    if (doc.contains("task" + (i + 1)))
                    {
                        Map<String, Object> taskMap = (Map<String, Object>) doc.get("task" + (i + 1));
                        String taskName = (String) taskMap.get("name");
                        String taskDescription = (String) taskMap.get("description");
                        boolean isComplete = (boolean) taskMap.get("isComplete");
                        goal.createTask(taskName, taskDescription, isComplete);
                    }
                }
                goals.add(goal);
            }

            Log.d("Firestore", "Goals loaded: " + goals.size());

        }
        catch (ExecutionException | InterruptedException e)
        {
            Log.e("Firestore", "Error loading data from Firestore", e);
        }
    }




    private void saveGoalToFirestore(Goal goal)
    {
        db = FirebaseFirestore.getInstance();

        //
        Map<String, Object> goalMap = new HashMap<>();
        goalMap.put("name", goal.getName());
        goalMap.put("description", goal.getDescription());
        goalMap.put("startDate", goal.getStartDate());
        goalMap.put("completionDate", goal.getCompletionDate());
        goalMap.put("totalProgress", goal.getTotalProgress());
        goalMap.put("completedToday", goal.isCompletedToday());
        goalMap.put("dailyStreak", goal.getDailyStreak());
        for(int i = 0; i < goal.getTaskAmount(); i++)
        {
            Map<String, Object> taskMap = new HashMap<>();
            taskMap.put("name", goal.getTask(i).getName());
            taskMap.put("description", goal.getTask(i).getDescription());
            taskMap.put("isComplete", goal.getTask(i).isComplete());
            goalMap.put("task" + (i+1), taskMap);
        }

        if(!goal.getDocID().isEmpty())
        {
            db.collection("users").document(userId).collection("goals").get().addOnSuccessListener(querySnapshot ->
            {
                for (DocumentSnapshot document : querySnapshot.getDocuments())
                {
                    if (document.getId().equals(goal.getDocID()))
                    {
                        db.collection("users").document(userId).collection("goals").document(goal.getDocID()).set(goalMap);
                    }
                }
            }).addOnFailureListener(e -> Log.w("Firestore", "Error getting documents", e));
        }
        else
        {
            db.collection("users").document(userId).collection("goals").add(goalMap).addOnSuccessListener(documentReference ->
            {
                for(int i = 0; i < goals.size(); i++)
                {
                    if(goals.get(i).equals(goal))
                        goals.get(i).setDocID(documentReference.getId());
                }
                Log.d("Firestore", "Goal saved with ID: " + documentReference.getId());
            }).addOnFailureListener(e ->
            {
                Log.e("Firestore", "Error saving goal", e);
            });
        }
    }

    private void deleteGoalFromFirestore(Goal goal)
    {
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(userId).collection("goals").document(goal.getDocID());
        docRef.delete().addOnSuccessListener(aVoid -> Log.d("Firestore", "Document successfully deleted")).addOnFailureListener(e -> Log.w("Firestore", "Error deleting document", e));
    }
}
