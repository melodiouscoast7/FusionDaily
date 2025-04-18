package com.example.fusiondailytest;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.auth.FirebaseAuth;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import com.example.Logic.*;

import android.net.Uri;
import android.view.LayoutInflater;
import android.text.util.Linkify;
import android.widget.LinearLayout;
import android.widget.Toast;
import org.json.JSONObject;

public class MainAppActivity extends AppCompatActivity {

    //firebase authentication
    private FirebaseAuth auth;

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
    private TextView gvDateText;
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

    //Calendar Variables
    Calendar localCalendar = Calendar.getInstance();

    final private String day = "" + localCalendar.get(Calendar.DATE);
    final public int month = localCalendar.get(Calendar.MONTH);
    private List<YearMonth> monthsList;
    private ViewPager2 pager;
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
        dbCalendarButton = findViewById(R.id.calendarButton);
        dbSettingsButton = findViewById(R.id.settingsButton);
        dbResourcesButton = findViewById(R.id.resourcesButton);

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
        //updateDBDailyStreak();
    }

    private void updateTotalProgress()
    {
        int totalProgressValue = 0;

        if (totalProgressValue > 100)
        {
            totalProgressValue = 100; // Cap progress at 100%
        }
        // Update the ProgressBar and display text
        dbTotalProgressBar.setProgress(totalProgressValue);
        dbTotalProgressText.setText("Total Progress: " + totalProgressValue + "%");
    }

    private void updateDailyProgress()
    {
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
            dailyProgressValue = (completedTasks/totalTasks)*100;
        // Update the ProgressBar and display text
        dbDailyProgressBar.setProgress(dailyProgressValue);
        dbDailyProgressText.setText(dailyProgressValue + "% Complete");
    }

    private void updateDBDailyStreak()
    {
        if(goals.size() > 0) {
            int compare = goals.get(0).getDailyStreak();
            for (int i = 0; i < goals.size(); i++) {
                if(goals.get(i).getDailyStreak() > compare)
                    compare = goals.get(i).getDailyStreak();
            }
            String output = "" + compare;
            dbDailyStreakText.setText(output);
        }
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

        // 1) build 12-month window (6 past, current, 5 future)
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

        // 2) find the ViewPager
        pager = findViewById(R.id.calendarPager);

        // 3) load goals and render their buttons
        loadGoals();      // replace with your real data load
        setupGoalButtons();

        // 4) default to first goal
        if (!goals.isEmpty()) {
            selectGoal(goals.get(0));
        }

        // 1) Find the new header TextView
        TextView monthHeader = findViewById(R.id.monthHeader);

        // 2) After setting adapter, initialize header to the current page:
        int defaultPosition = 6; // the index you use for “now”
        updateMonthHeader(defaultPosition, monthHeader);

        // 3) Add a page‐change callback to update it as you swipe:
        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateMonthHeader(position, monthHeader);
            }
        });

        // 5) back button returns you to the dashboard
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            setContentView(R.layout.fragment_dashboard);
            assignDashboard();
        });
    }

    /** Helper to display "Month Year" for page at index */
    private void updateMonthHeader(int position, TextView header) {
        MainAppActivity.YearMonth ym = monthsList.get(position);
        // month is 0-based: 0=Jan … 11=Dec
        String monthName = months[ym.month];      // your String[] months array
        String text = monthName + " " + ym.year;
        header.setText(text);
    }

    private void loadGoals() {
        goals.clear();
        Goal g1 = new Goal("Fitness",     "Stay fit",       "2025-05-15");
        g1.setDailyStreak(3);
        goals.add(g1);
        Goal g2 = new Goal("Reading",     "Read books",     "2025-06-01");
        g2.setDailyStreak(7);
        goals.add(g2);
        Goal g3 = new Goal("Meditation",  "Mindfulness",    "2025-05-30");
        g3.setDailyStreak(1);
        goals.add(g3);
        Goal g4 = new Goal("Coding",      "Practice daily", "2025-07-10");
        g4.setDailyStreak(10);
        goals.add(g4);
        Goal g5 = new Goal("Diet",        "Healthy eating", "2025-05-20");
        g5.setDailyStreak(5);
        goals.add(g5);
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

            // Create LayoutParams with MATCH_PARENT width and WRAP_CONTENT height
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );

            // Apply your margins (left, top, right, bottom)
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
            String[] p = goal.getCompletionDate().split("-");
            goalCal.set(Integer.parseInt(p[0]), Integer.parseInt(p[1]) - 1, Integer.parseInt(p[2]));
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
        pager.setCurrentItem(6, false);  // center on “current” month
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
                if(isNewGoal) {
                    goals.add(new Goal(gcNameInput.getText().toString(), gcDescriptionInput.getText().toString(), gcCompletionDate.getText().toString()));
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
        gvDateText = findViewById(R.id.completionDate);

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
            gvDateText.setText(goals.get(goalNumber).getCompletionDate());
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
                if(isFromGoalView) {
                    if (isNewTask) {
                        //new task and existing goal
                        goals.get(goalNumber).createTask(tcNameInput.getText().toString(), tcDescriptionInput.getText().toString());
                        tempGoal = goals.get(goalNumber);
                        setContentView(R.layout.fragment_goals_view);
                        assignGoalsView();
                    } else {
                        //existing task and existing goal
                        goals.get(goalNumber).getTask(goalNumber).setName(tcNameInput.getText().toString());
                        goals.get(goalNumber).getTask(goalNumber).setDescription(tcDescriptionInput.getText().toString());
                        tempGoal = goals.get(goalNumber);
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
                }
            }
        });
    }

    //Settings View (sv) Functions
    private void assignSettingsView() //Assigns objects for the create fragment
    {
        svBackButton = findViewById(R.id.settingsBackButton);
        svSignOutButton = findViewById(R.id.settingsSignOutButton);
        svViewQuestionnaireButton = findViewById(R.id.settingsQuestionnaireButton);
        svChangePasswordButton = findViewById(R.id.settingsPasswordButton);
        svDeleteAccountButton = findViewById(R.id.settingsDeleteButton);

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

        svViewQuestionnaireButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(MainAppActivity.this, QuestionnaireActivity.class));
            }
        });

        svChangePasswordButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //TO DO - Create UI for changing password, add functionality to change password
            }
        });

        svDeleteAccountButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //TO DO - Create UI for confirmation of deleting password and program functionality, delete data from firebase.
            }
        });
    }
}
