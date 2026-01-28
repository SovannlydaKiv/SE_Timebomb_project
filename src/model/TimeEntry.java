package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeEntry {
    private Long id;
    private Task task;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer durationMinutes;
    private String description;
    private String note;
    private Boolean billable;
    private Boolean isRunning;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TimeEntry()
    {
        this.billable = true;
        this.isRunning = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public TimeEntry(Task task, LocalDateTime startTime)
    {
        this();
        this.task = task;
        this.startTime = startTime;
        this.isRunning = true;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Task getTask()
    {
        return task;
    }

    public void setTask(Task task)
    {
        this.task = task;
    }

    public LocalDateTime getStartTime()
    {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime)
    {
        this.startTime = startTime;
        calculateDuration();
    }

    public LocalDateTime getEndTime()
    {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime)
    {
        this.endTime = endTime;
        calculateDuration();
    }

    public Integer getDurationMinutes()
    {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes)
    {
        this.durationMinutes = durationMinutes;
    }

    public String getNotes()
    {
        return note;
    }

    public void setNotes(String note)
    {
        this.note = note;
    }

    public Boolean getBillable()
    {
        return billable;
    }

    public void setBillable(Boolean billable)
    {
        this.billable = billable;
    }

    public Boolean getIsRunning()
    {
        return isRunning;
    }

    public void setIsRunning(Boolean isRunning)
    {
        this.isRunning = isRunning;
    }

    public LocalDateTime getCreatedAt()
    {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt()
    {
        return updatedAt;
    }

    private void calculateDuration()
    {
        if (startTime != null && endTime != null)
        {
            this.durationMinutes = (int) Duration.between(startTime, endTime).toMinutes();
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void stopTimer()
    {
        if (isRunning)
        {
            this.endTime = LocalDateTime.now();
            this.isRunning = false;
            calculateDuration();
        }
    }

    public String getFormattedDuration()
    {
        if(durationMinutes == null) return "Running...";
        int hours = durationMinutes / 60;
        int mins = durationMinutes % 60;
        return String.format("%dh %dm", hours, mins);
    }

    @Override
    public String toString()
    {
        return "TimeEntry{id=" + id + ", task=" + (task != null ? task.getName() : "null") + 
                ", duration=" + getFormattedDuration() + ", running=" + isRunning + "}";
    }
}
