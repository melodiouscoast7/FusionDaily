package com.example.fusiondaily;

import android.os.Bundle;
import android.widget.TextView;
import java.util.ArrayList;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fusiondailytest.R;

public class QuestionnaireActivity extends AppCompatActivity {

    private View fragmentOne, fragmentTwo, fragmentThree, fragmentFour, fragmentFive;
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
// 2 => Fragment Two: Activate square1, 3 => Fragment Three: Activate square2, etc
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
            // If intro fragment, do nothing or reset all
            break;
    }
}

    // This method handles finishing the questionnaire.
    private void finishQuestionnaire() {
        // Add your finish logic (e.g., save data, submit responses)
        finish();  // For now, simply finish the activity
    }
}
