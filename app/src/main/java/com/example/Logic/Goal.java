package com.example.Logic;
import java.util.Vector;
public class Goal {

    private String name, description, completionDate;
    private int totalProgress, dailyStreak;
    private Vector<Task> dailyTasks = new Vector<Task>();

    public Goal(String _name, String _description, String _completionDate)
    {
        completionDate = _completionDate;
        name = _name;
        description = _description;
        totalProgress = 0;
        dailyStreak = 0;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String _name)
    {
        name = _name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String _description)
    {
        description = _description;
    }

    public String getCompletionDate()
    {
        return completionDate;
    }

    public void setCompletionDate(String _completionDate)
    {
        completionDate = _completionDate;
    }

    public int getTotalProgress()
    {
        return totalProgress;
    }

    public void setTotalProgress(int _totalProgress)
    {
        totalProgress = _totalProgress;
    }

    public int getDailyStreak()
    {
        return dailyStreak;
    }

    public void setDailyStreak(int _dailyStreak)
    {
        dailyStreak = _dailyStreak;
    }

    public void createTask(String _name, String _description)
    {
        dailyTasks.add(new Task(_name, _description));
    }

    public int getTaskAmount()
    {
        return dailyTasks.size();
    }

    public Task getTask(int _index)
    {
        return dailyTasks.get(_index);
    }

    public void deleteTask(int _index)
    {
        dailyTasks.remove(_index);
    }
}