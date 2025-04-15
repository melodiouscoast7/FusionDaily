package com.example.Logic;

import java.util.Vector;

public class Task {
    private String name, description;
    private int totalProgress;

    public Task(String _name, String _description)
    {
        name = _name;
        description = _description;
        totalProgress = 0;
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

    public void setDescription(String _description){
        description = _description;
    }

    public int getTotalProgress()
    {
        return totalProgress;
    }

    public void setTotalProgress(int _totalProgress)
    {
        totalProgress = _totalProgress;
    }
}
