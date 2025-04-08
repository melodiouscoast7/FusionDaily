package com.example.fusiondaily;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import android.os.Looper;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import androidx.appcompat.app.AppCompatActivity;
import com.example.fusiondailytest.R;
import com.example.fusiondailytest.OpenAIManager;
import com.example.fusiondailytest.DashboardActivity;

public class QuestionnaireActivity extends AppCompatActivity {

    // Description of what the AI should do.
    public static final String TASK_DESCRIPTION = "Generate a concise game-plan (<=334 characters) that helps the user achieve their goal based on their answers. present it in the following format: Goal: [goal], Timeline: [timeline], Actions: [actions]. Add a - in front of each action";

    // The fixed questionnaire questions.
    public static final String QUESTIONS = "Questions: 1. What’s your name? 2. Describe your current lifestyle. 3. What are your primary areas of focus? 4. What goal do you want to achieve? 5. Why is it important? 6. What’s your ideal timeline? 7. How much time can you commit? 8. What obstacles do you face? 9. Is your goal specific? (If not, could you describe it more clearly?) 10. How will you measure your progress? (e.g., milestones, check-ins, tracking tools, etc.) 11. Is this goal achievable for you within the given time frame? 12. Is this goal relevant to your overall life or long-term plans? (How does it fit with your values and priorities?) 13. What’s a realistic timeline for this goal, considering your current circumstances?";

    private View fragmentOne, fragmentTwo, fragmentThree, fragmentFour, fragmentFive, fragmentSix;
    // Header square TextViews
    private TextView square1, square2, square3, square4;
    private ArrayList<TextView> squares;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);

        // Initialize fragment views
        fragmentOne = findViewById(R.id.fragment_one);
        fragmentTwo = findViewById(R.id.fragment_two);
        fragmentThree = findViewById(R.id.fragment_three);
        fragmentFour = findViewById(R.id.fragment_four);
        fragmentFive = findViewById(R.id.fragment_five);
        fragmentSix = findViewById(R.id.fragment_six);

        // Initialize header squares
        square1 = findViewById(R.id.square1);
        square2 = findViewById(R.id.square2);
        square3 = findViewById(R.id.square3);
        square4 = findViewById(R.id.square4);

        squares = new ArrayList<>();
        squares.add(square1);
        squares.add(square2);
        squares.add(square3);
        squares.add(square4);

        // Set up buttons for navigation from Fragment One (Intro)
        Button buttonToFragmentTwoFrom1 = findViewById(R.id.buttonToFragmentTwo_from1);
        buttonToFragmentTwoFrom1.setOnClickListener(v -> showFragment(fragmentTwo));

        // Set up buttons for navigation from Fragment Two
        Button buttonToFragmentOneFrom2 = findViewById(R.id.buttonToFragmentOne_from2);
        Button buttonToFragmentThreeFrom2 = findViewById(R.id.buttonToFragmentThree_from2);
        buttonToFragmentOneFrom2.setOnClickListener(v -> showFragment(fragmentOne));
        buttonToFragmentThreeFrom2.setOnClickListener(v -> showFragment(fragmentThree));

        // Set up buttons for navigation from Fragment Three
        Button buttonToFragmentTwoFrom3 = findViewById(R.id.buttonToFragmentTwo_from3);
        Button buttonToFragmentFourFrom3 = findViewById(R.id.buttonToFragmentFour_from3);
        buttonToFragmentTwoFrom3.setOnClickListener(v -> showFragment(fragmentTwo));
        buttonToFragmentFourFrom3.setOnClickListener(v -> showFragment(fragmentFour));

        // Set up buttons for navigation from Fragment Four
        Button buttonToFragmentThreeFrom4 = findViewById(R.id.buttonToFragmentThree_from4);
        Button buttonToFragmentFiveFrom4 = findViewById(R.id.buttonToFragmentFive_from4);
        buttonToFragmentThreeFrom4.setOnClickListener(v -> showFragment(fragmentThree));
        buttonToFragmentFiveFrom4.setOnClickListener(v -> showFragment(fragmentFive));

        // Set up buttons for navigation from Fragment Five
        Button buttonToFragmentFourFrom5 = findViewById(R.id.buttonToFragmentFour_from5);
        Button buttonToFragmentSixFrom5 = findViewById(R.id.buttonToFragmentSix_from5);
        buttonToFragmentFourFrom5.setOnClickListener(v -> showFragment(fragmentFour));
        buttonToFragmentSixFrom5.setOnClickListener(v -> finishQuestionnaire());

        // Set up buttons for navigation from Fragment Six (Results)
        Button buttonFinishFrom6 = findViewById(R.id.buttonFinish_from6);
        buttonFinishFrom6.setOnClickListener(v -> leaveQuestionnaire());
    }

    // This method hides all fragments and shows the selected one.
    private void showFragment(View fragment) {
        fragmentOne.setVisibility(View.GONE);
        fragmentTwo.setVisibility(View.GONE);
        fragmentThree.setVisibility(View.GONE);
        fragmentFour.setVisibility(View.GONE);
        fragmentFive.setVisibility(View.GONE);
        fragmentSix.setVisibility(View.GONE);

        fragment.setVisibility(View.VISIBLE);

        // Update header squares based on which fragment is visible
        if (fragment == fragmentTwo) {
            updateHeaderSquares(2);
        } else if (fragment == fragmentThree) {
            updateHeaderSquares(3);
        } else if (fragment == fragmentFour) {
            updateHeaderSquares(4);
        } else if (fragment == fragmentFive) {
            updateHeaderSquares(5);
        } else {
            // For the Intro fragment, reset all squares to inactive
            updateHeaderSquares(1);
        }
    }

    // This method updates the header squares:
    // 2 => Fragment Two: Activate square1, 3 => Fragment Three: Activate square2, etc.
    private void updateHeaderSquares(int fragmentNumber) {
        // Reset all squares to inactive background
        for (TextView square : squares) {
            square.setBackgroundResource(R.drawable.qs_square_inactive);
            square.setTextColor(getResources().getColor(R.color.white));
        }

        // Update based on the fragment number
        switch (fragmentNumber) {
            case 2:
                square1.setBackgroundResource(R.drawable.qs_square_active);
                square1.setTextColor(getResources().getColor(R.color.black));
                break;
            case 3:
                square2.setBackgroundResource(R.drawable.qs_square_active);
                square2.setTextColor(getResources().getColor(R.color.black));
                break;
            case 4:
                square3.setBackgroundResource(R.drawable.qs_square_active);
                square3.setTextColor(getResources().getColor(R.color.black));
                break;
            case 5:
                square4.setBackgroundResource(R.drawable.qs_square_active);
                square4.setTextColor(getResources().getColor(R.color.black));
                break;
            default:
                // For the Intro fragment, do nothing or reset all
                break;
        }
    }

    // This method collects the user's answers, builds the prompt, sends it to OpenAI, and shows the results.
    private void finishQuestionnaire() {
        // Example: Collect answers from EditText fields (adjust IDs as necessary)
        String answer1 = ((EditText) findViewById(R.id.editTextText)).getText().toString().trim();
        String answer2 = ((EditText) findViewById(R.id.editTextTextMultiLine)).getText().toString().trim();
        String answer3 = ((EditText) findViewById(R.id.editTextTextMultiLine2)).getText().toString().trim();
        String answer4 = ((EditText) findViewById(R.id.editTextTextMultiLine3)).getText().toString().trim();
        String answer5 = ((EditText) findViewById(R.id.editTextTextMultiLine4)).getText().toString().trim();
        String answer6 = ((EditText) findViewById(R.id.editTextText4)).getText().toString().trim();
        String answer7 = ((EditText) findViewById(R.id.editTextText5)).getText().toString().trim();
        String answer8 = ((EditText) findViewById(R.id.editTextTextMultiLine5)).getText().toString().trim();
        String answer9 = ((EditText) findViewById(R.id.editTextTextMultiLine6)).getText().toString().trim();
        String answer10 = ((EditText) findViewById(R.id.editTextTextMultiLine7)).getText().toString().trim();
        String answer11 = ((EditText) findViewById(R.id.editTextText6)).getText().toString().trim();
        String answer12 = ((EditText) findViewById(R.id.editTextTextMultiLine8)).getText().toString().trim();
        String answer13 = ((EditText) findViewById(R.id.editTextTextMultiLine9)).getText().toString().trim();

        // Build a string with user answers. Here, we simply concatenate for demonstration.
        String userAnswers = "Answers: 1. " + answer1 + " 2. " + answer2 + " 3. " + answer3 + " 4. " + answer4 + " 5. " + answer5 + " 6. " + answer6 + " 7. " + answer7 + " 8. " + answer8 + " 9. " + answer9 + " 10. " + answer10 + " 11. " + answer11 + " 12. " + answer12 + " 13. " + answer13;

        // Construct the prompt using the fixed strings and the user-provided answers.
        String prompt = TASK_DESCRIPTION + " " + QUESTIONS + " " + userAnswers;

        // Create an instance of the OpenAIManager.
        OpenAIManager openAIManager = new OpenAIManager();
        openAIManager.sendPrompt(prompt, new OpenAIManager.OpenAIResponseCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    // Parse the JSON response
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray choices = jsonObject.getJSONArray("choices");
                    if (choices.length() > 0) {
                        JSONObject firstChoice = choices.getJSONObject(0);
                        JSONObject messageObject = firstChoice.getJSONObject("message");
                        String content = messageObject.getString("content").trim();

                        // Split content into categories
                        String goal = "";
                        String timeline = "";
                        StringBuilder actions = new StringBuilder();

                        String[] lines = content.split("\n");

                        for (String line : lines) {
                            line = line.trim();
                            if (line.startsWith("Goal:")) {
                                goal = line.substring(5).trim();
                            } else if (line.startsWith("Timeline:")) {
                                timeline = line.substring(9).trim();
                            } else if (line.startsWith("-")) {
                                actions.append(line).append("\n");
                            }
                        }

                        // Update UI on the main thread
                        String finalGoal = goal;
                        String finalTimeline = timeline;
                        new Handler(Looper.getMainLooper()).post(() -> {
                            TextView goalContent = findViewById(R.id.GoalContent);
                            TextView timelineContent = findViewById(R.id.TimelineContent);
                            TextView actionsContent = findViewById(R.id.ActionsContent);

                            goalContent.setText(finalGoal);
                            timelineContent.setText(finalTimeline);
                            actionsContent.setText(actions.toString().trim());

                            // Show the results fragment
                            showFragment(fragmentSix);
                        });

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(QuestionnaireActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void leaveQuestionnaire() {
        startActivity(new Intent(QuestionnaireActivity.this, DashboardActivity.class));
        finish();
    }
}
