package com.example.Logic;

import java.util.Vector;

public class Task {
    private String name, description;
    private boolean isComplete;

    public Task(String _name, String _description)
    {
        name = _name;
        description = _description;
        isComplete = false;
    }

    public Task(String _name, String _description, boolean _icComplete)
    {
        name = _name;
        description = _description;
        isComplete = _icComplete;
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

    public boolean isComplete()
    {
        return isComplete;
    }

    public void setCompletion(boolean _isComplete)
    {
        isComplete = _isComplete;
    }
}
