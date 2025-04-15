package com.example.fusiondailytest;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

public class CalendarAdapter
        extends RecyclerView.Adapter<CalendarAdapter.DayViewHolder> {

    private final List<Long> daysInMillis;
    private final Set<Long> streakDays;
    private final long goalEndMillis;

    public CalendarAdapter(List<Long> daysInMillis,
                           Set<Long> streakDays, long goalEndMillis) {
        this.daysInMillis = daysInMillis;
        this.streakDays = streakDays;
        this.goalEndMillis = goalEndMillis;
    }

    @NonNull @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_calendar_day, parent, false);
        int width = parent.getMeasuredWidth() / 7;
        v.setLayoutParams(new RecyclerView.LayoutParams(width, width));
        return new DayViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int pos) {
        long millis = daysInMillis.get(pos);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);
        int day = c.get(Calendar.DAY_OF_MONTH);
        holder.dayNumber.setText(String.valueOf(day));

        // Reset tint
        holder.dayBackground.setBackgroundTintList(
                ColorStateList.valueOf(Color.TRANSPARENT));

        // Green for streak days
        if (streakDays.contains(millis)) {
            holder.dayBackground.setBackgroundTintList(
                    ColorStateList.valueOf(Color.parseColor("#4CAF50")));
        }
        // Red for goal end date
        if (millis == goalEndMillis) {
            holder.dayBackground.setBackgroundTintList(
                    ColorStateList.valueOf(Color.parseColor("#F44336")));
        }
    }

    @Override public int getItemCount() { return daysInMillis.size(); }

    static class DayViewHolder extends RecyclerView.ViewHolder {
        final View dayBackground;
        final TextView dayNumber;
        DayViewHolder(View v) {
            super(v);
            dayBackground = v.findViewById(R.id.day_background);
            dayNumber     = v.findViewById(R.id.day_number);
        }
    }
}

