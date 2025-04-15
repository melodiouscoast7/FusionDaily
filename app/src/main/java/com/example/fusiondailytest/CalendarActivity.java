package com.example.fusiondailytest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CalendarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        // This is an example, plug the user's goal end dates here.
        Calendar goalEndCal = Calendar.getInstance();
        goalEndCal.add(Calendar.DAY_OF_MONTH, 10);
        zeroTime(goalEndCal);

        // Build streak set (last N days)
        Set<Long> streakSet = new HashSet<>();
        Calendar c = Calendar.getInstance();
        zeroTime(c);
        // Example streak length
        int streakDays = 15;
        for (int i = 0; i < streakDays; i++) {
            streakSet.add(c.getTimeInMillis());
            c.add(Calendar.DAY_OF_MONTH, -1);
        }

        // Build 12 months: 6 past, current, 5 future
        List<YearMonth> months = new ArrayList<>();
        Calendar mCal = Calendar.getInstance();
        mCal.add(Calendar.MONTH, -6);
        for (int i = 0; i < 12; i++) {
            months.add(new YearMonth(
                    mCal.get(Calendar.YEAR),
                    mCal.get(Calendar.MONTH)
            ));
            mCal.add(Calendar.MONTH, 1);
        }

        ViewPager2 pager = findViewById(R.id.calendarPager);
        CalendarPagerAdapter adapter = new CalendarPagerAdapter(
                months, streakSet, goalEndCal.getTimeInMillis()
        );
        pager.setAdapter(adapter);
        pager.setCurrentItem(6, false);  // Center on current month

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CalendarActivity.this, DashboardActivity.class));
                finish();
            }
        });
    }

    private void zeroTime(Calendar c) {
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
    }

    // Simple holder for year/month
    static class YearMonth {
        final int year, month;
        YearMonth(int year, int month) {
            this.year = year;
            this.month = month;
        }
    }
}


