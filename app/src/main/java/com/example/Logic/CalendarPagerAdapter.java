package com.example.Logic;

import com.example.fusiondailytest.MainAppActivity;
import com.example.fusiondailytest.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

public class CalendarPagerAdapter
        extends RecyclerView.Adapter<CalendarPagerAdapter.MonthViewHolder> {

    private final List<MainAppActivity.YearMonth> months;
    private final Set<Long> streakDays;
    private final Set<Long> goalEndDates;

    /**
     * @param months        a 12‑element list of YearMonth (6 past → current → 5 future)
     * @param streakDays    timestamps (midnight millis) representing the user’s streak
     * @param goalEndDates  timestamps (midnight millis) for the goal’s end date(s)
     */
    public CalendarPagerAdapter(List<MainAppActivity.YearMonth> months,
                                Set<Long> streakDays,
                                Set<Long> goalEndDates) {
        this.months       = months;
        this.streakDays   = streakDays;
        this.goalEndDates = goalEndDates;
    }

    @NonNull @Override
    public MonthViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the month‑grid RecyclerView container
        RecyclerView rv = (RecyclerView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.calendar_month_grid, parent, false);
        return new MonthViewHolder(rv);
    }

    @Override
    public void onBindViewHolder(@NonNull MonthViewHolder holder, int position) {
        MainAppActivity.YearMonth ym = months.get(position);

        // Build list of each day in this month as midnight‑normalized millis
        List<Long> daysInMonth = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR,  ym.year);
        c.set(Calendar.MONTH, ym.month);
        c.set(Calendar.DAY_OF_MONTH, 1);
        zeroTime(c);
        int maxDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int d = 1; d <= maxDay; d++) {
            c.set(Calendar.DAY_OF_MONTH, d);
            daysInMonth.add(c.getTimeInMillis());
        }

        // Set up the inner grid
        CalendarAdapter dayAdapter = new CalendarAdapter(
                daysInMonth, streakDays, goalEndDates
        );
        holder.recycler.setLayoutManager(
                new GridLayoutManager(holder.recycler.getContext(), 7)
        );
        holder.recycler.setAdapter(dayAdapter);
    }

    @Override
    public int getItemCount() {
        return months.size();
    }

    static class MonthViewHolder extends RecyclerView.ViewHolder {
        final RecyclerView recycler;
        MonthViewHolder(@NonNull View itemView) {
            super(itemView);
            // itemView is the RecyclerView itself (from calendar_month_grid.xml)
            recycler = (RecyclerView) itemView;
        }
    }

    // Helper to zero out time portion
    private void zeroTime(Calendar c) {
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE,      0);
        c.set(Calendar.SECOND,      0);
        c.set(Calendar.MILLISECOND, 0);
    }
}


