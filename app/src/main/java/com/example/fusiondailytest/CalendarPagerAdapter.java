package com.example.fusiondailytest;

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

    private final List<CalendarActivity.YearMonth> months;
    private final Set<Long> streakDays;
    private final long goalEndMillis;

    public CalendarPagerAdapter(List<CalendarActivity.YearMonth> months,
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
        CalendarActivity.YearMonth ym = months.get(position);
        List<Long> days = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, ym.year);
        c.set(Calendar.MONTH, ym.month);
        c.set(Calendar.DAY_OF_MONTH, 1);
        zeroTime(c);
        int maxDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int d = 1; d <= maxDay; d++) {
            c.set(Calendar.DAY_OF_MONTH, d);
            days.add(c.getTimeInMillis());
        }

        CalendarAdapter dayAdapter = new CalendarAdapter(
                days, streakDays, goalEndMillis
        );
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


