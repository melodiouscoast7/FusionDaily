package com.example.Logic;

import com.example.fusiondailytest.R;
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
    private final Set<Long> goalEndDates;

    /**
     * @param daysInMillis   list of each day in the month (as millis at midnight)
     * @param streakDays     set of millis‐at‐midnight for the user’s daily‐streak days
     * @param goalEndDates   set of millis‐at‐midnight for the selected goal’s end date(s)
     */
    public CalendarAdapter(List<Long> daysInMillis,
                           Set<Long> streakDays,
                           Set<Long> goalEndDates) {
        this.daysInMillis   = daysInMillis;
        this.streakDays     = streakDays;
        this.goalEndDates   = goalEndDates;
    }

    @NonNull @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate our custom day cell
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_calendar_day, parent, false);

        // Make each cell roughly 1/7 of the parent width (7 columns)
        int cellWidth = parent.getMeasuredWidth() / 7;
        RecyclerView.LayoutParams lp =
                new RecyclerView.LayoutParams(cellWidth, cellWidth);
        v.setLayoutParams(lp);

        return new DayViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        long millis = daysInMillis.get(position);

        // Display the day of month
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        holder.dayNumber.setText(String.valueOf(day));

        // Reset background tint
        holder.dayBackground.setBackgroundTintList(
                ColorStateList.valueOf(Color.TRANSPARENT));

        // If this day is in the user's streak => green
        if (streakDays.contains(millis)) {
            holder.dayBackground.setBackgroundTintList(
                    ColorStateList.valueOf(Color.parseColor("#4CAF50")));
        }

        // If this day is a goal end date => red (overrides green)
        if (goalEndDates.contains(millis)) {
            holder.dayBackground.setBackgroundTintList(
                    ColorStateList.valueOf(Color.parseColor("#F44336")));
        }
    }

    @Override
    public int getItemCount() {
        return daysInMillis.size();
    }

    static class DayViewHolder extends RecyclerView.ViewHolder {
        final View dayBackground;
        final TextView dayNumber;

        DayViewHolder(View itemView) {
            super(itemView);
            dayBackground = itemView.findViewById(R.id.day_background);
            dayNumber     = itemView.findViewById(R.id.day_number);
        }
    }
}

