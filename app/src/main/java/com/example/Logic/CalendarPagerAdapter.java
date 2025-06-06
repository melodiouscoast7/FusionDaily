package com.example.Logic;

import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fusiondailytest.MainAppActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

public class CalendarPagerAdapter
        extends RecyclerView.Adapter<CalendarPagerAdapter.MonthViewHolder> {

    private final List<MainAppActivity.YearMonth> months;
    private final Set<Long> streakDays;
    private final long goalEndMillis;

    public CalendarPagerAdapter(List<MainAppActivity.YearMonth> months,
                                Set<Long> streakDays, long goalEndMillis) {
        this.months = months;
        this.streakDays = streakDays;
        this.goalEndMillis = goalEndMillis;
    }

    @NonNull @Override
    public MonthViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView rv = new RecyclerView(parent.getContext());
        rv.setLayoutParams(new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        return new MonthViewHolder(rv);
    }

    @Override
    public void onBindViewHolder(@NonNull MonthViewHolder holder, int position) {
        // Get the year and month for the current position
        MainAppActivity.YearMonth ym = months.get(position);
        // Create a list to hold the milliseconds for each day in the month
        List<Long> days = new ArrayList<>();
        // Get a Calendar instance and set it to the first day of the current month
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, ym.year);
        c.set(Calendar.MONTH, ym.month);
        c.set(Calendar.DAY_OF_MONTH, 1);
        // Zero out the time (hour, minute, second, millisecond)
        zeroTime(c);
        // Get the maximum day of the month
        int maxDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        // Loop through each day of the month
        for (int d = 1; d <= maxDay; d++) {
            // Set the current day and add the time in milliseconds to the list
            c.set(Calendar.DAY_OF_MONTH, d);
            days.add(c.getTimeInMillis());
        }

        CalendarAdapter dayAdapter = new CalendarAdapter(
                days, streakDays, goalEndMillis
        );
        // Set the layout manager and adapter for the inner RecyclerView (days)
        holder.recycler.setLayoutManager(new GridLayoutManager(
                holder.recycler.getContext(), 7
        ));
        holder.recycler.setAdapter(dayAdapter);
    }

    @Override public int getItemCount() { return months.size(); }

    static class MonthViewHolder extends RecyclerView.ViewHolder {
        final RecyclerView recycler;
        MonthViewHolder(@NonNull View itemView) {
            super(itemView);
            recycler = (RecyclerView) itemView;
        }
    }

    private void zeroTime(Calendar c) {
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
    }
}


