package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Task {
    private Long id;
    private String name;
    private String description;
    private Project project;
    private List<String> tags;
    private Priority priority;
    private TaskStatus status;
    private Integer estimationMinutes;
    private LocalDateTime dueDate;
    private Boolean billable;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<TimeEntry> timeEntries;

    public Task()
    {
        this.tags = new ArrayList<>();
        this.timeEntries = new ArrayList<>();
        this.priority = Priority.MEDIUM;
        this.status = TaskStatus.TODO;
        this.billable = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Task(String name, String description)
    {
        this();
        this.name = name;
        this.description = description;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
        this.updatedAt = LocalDateTime.now();
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public Project getProject()
    {
        return project;
    }

    public void setProject(Project project)
    {
        this.project = project;
    }

    public List<String> getTags()
    {
        return tags;
    }

    public void setTags(List<String> tags)
    {
        this.tags = tags;
    }

    public void addTags(String tag)
    {
        this.tags.add(tag);
    }

    public Priority getPriority()
    {
        return priority;
    }

        public void setPriority(Priority priority)
    {
        this.priority = priority;
    }

    public TaskStatus getStatus()
    {
        return status;
    }

    public void setStatus(TaskStatus status)
    {
        this.status = status;
    }

    public Integer getEstimationMinutes()
    {
        return estimationMinutes;
    }

    public void setEstimationMinutes(Integer estimationMinutes)
    {
        this.estimationMinutes = estimationMinutes;
    }

    public LocalDateTime getDueDate()
    {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate)
    {
        this.dueDate = dueDate;
    }

    public boolean getBillable()
    {
        return billable;
    }

    public void setBillable(Boolean billable)
    {
        this.billable = billable;
    }

    public LocalDateTime getCreatedAt()
    {
        return createdAt;
    }

    public LocalDateTime getUpdateAt()
    {
        return updatedAt;
    }

    public List<TimeEntry> getTimeEntries()
    {
        return timeEntries;
    }

    public void setTimeEntries(List<TimeEntry> timeEntries)
    {
        this.timeEntries = timeEntries;
    }

    public void addTimeEntry(TimeEntry entry)
    {
        this.timeEntries.add(entry);
        entry.setTask(this);
    }

    public int getTotalMinutes()
    {
        return timeEntries.stream()
        .filter(e->e.getDurationMinutes() != null)
        .mapToInt(TimeEntry::getDurationMinutes)
        .sum();
    }

    @Override
    public String toString()
    {
        return "Task{id=" + id + ", name='" + name + "', status=" + status + 
                ", priority=" + priority + "}";
    }
}
