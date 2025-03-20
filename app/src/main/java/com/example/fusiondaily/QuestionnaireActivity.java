package com.example.fusiondaily;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fusiondailytest.R;

public class QuestionnaireActivity extends AppCompatActivity {

    private View fragmentOne, fragmentTwo, fragmentThree, fragmentFour, fragmentFive;

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

        // Set up buttons for navigation from Fragment One
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
        Button buttonFinishFrom5 = findViewById(R.id.buttonFinish_from5);
        buttonToFragmentFourFrom5.setOnClickListener(v -> showFragment(fragmentFour));
        buttonFinishFrom5.setOnClickListener(v -> finishQuestionnaire());
    }

    // This method hides all fragments and shows the selected one.
    private void showFragment(View fragment) {
        fragmentOne.setVisibility(View.GONE);
        fragmentTwo.setVisibility(View.GONE);
        fragmentThree.setVisibility(View.GONE);
        fragmentFour.setVisibility(View.GONE);
        fragmentFive.setVisibility(View.GONE);

        fragment.setVisibility(View.VISIBLE);
    }

    // This method handles finishing the questionnaire.
    private void finishQuestionnaire() {
        // Add your finish logic (e.g., save data, submit responses)
        finish();  // For now, simply finish the activity
    }
}
