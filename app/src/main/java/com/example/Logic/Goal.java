package com.example.Logic;
import java.util.Vector;
public class Goal {

    private String name, description, completionDate, startDate, docID;
    private int totalProgress, dailyStreak;
    private Vector<Task> dailyTasks = new Vector<Task>();

    public Goal(String _name, String _description, String _startDate, String _completionDate) {
        startDate = _startDate;
        completionDate = _completionDate;
        name = _name;
        description = _description;
        totalProgress = 0;
        dailyStreak = 0;
        docID = "";
    }

    public Goal(String _name, String _description, String _startDate, String _completionDate, int _totalProgress, int _dailyStreak, String _docID) {
        startDate = _startDate;
        completionDate = _completionDate;
        name = _name;
        description = _description;
        totalProgress = _totalProgress;
        dailyStreak = _dailyStreak;
        docID = _docID;
    }

    public String getName() {
        return name;
    }

    public void setName(String _name) {
        name = _name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String _description) {
        description = _description;
    }

    public String getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(String _completionDate) {
        completionDate = _completionDate;
    }

    public int getTotalProgress() {
        return totalProgress;
    }

    public void setTotalProgress(int _totalProgress) {
        totalProgress = _totalProgress;
    }

    public int getDailyStreak() {
        return dailyStreak;
    }

    public void setDailyStreak(int _dailyStreak) {
        dailyStreak = _dailyStreak;
    }

    public void createTask(String _name, String _description) {
        dailyTasks.add(new Task(_name, _description));
    }

    public void createTask(String _name, String _description, boolean _isComplete) {
        dailyTasks.add(new Task(_name, _description, _isComplete));
    }

    public int getTaskAmount() {
        return dailyTasks.size();
    }

    public Task getTask(int _index) {
        return dailyTasks.get(_index);
    }

    public void deleteTask(int _index) {
        dailyTasks.remove(_index);
    }

    public String getDocID()
    {
        return docID;
    }

    public void setDocID(String _docID)
    {
        docID = _docID;
    }

    public String getStartDate()
    {
        return startDate;
    }
}
