package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Project
{
    private Long id;
    private String name;
    private String description;
    private String colorCode;
    private String client;
    private ProjectStatus status;
    private Double hourlyRate;
    private Double budget;
    private LocalDateTime deadLine;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Task> tasks;

    public Project()
    {
        this.tasks = new ArrayList<>();
        this.status = ProjectStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Project(String name, String description)
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
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getColorCode()
    {
        return colorCode;
    }

    public void setColorCode(String colorCode)
    {
        this.colorCode = colorCode;
    }

    public String getClient()
    {
        return client;
    }

    public void setClient(String client)
    {
        this.client = client;
    }

    public ProjectStatus getStatus()
    {
        return status;
    }

    public void setStatus(ProjectStatus status)
    {
        this.status = status;
    }

    public Double getHourlyRate()
    {
        return hourlyRate;
    }

    public void setHourlyRate(Double hourlyRate)
    {
        this.hourlyRate = hourlyRate;
    }

    public Double getBudget()
    {
        return budget;
    }

    public void setBudget(Double budget)
    {
        this.budget = budget;
    }

    public LocalDateTime getDeadLine()
    {
        return deadLine;
    }

    public void setDeadLine(LocalDateTime deadLine)
    {
        this.deadLine = deadLine;
    }

    public LocalDateTime getCreatedAt()
    {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt()
    {
        return updatedAt;
    }

    public List<Task> getTasks()
    {
        return tasks;
    }

    public void setTasks(List<Task> tasks)
    {
        this.tasks = tasks;
    }

    public void addTask(Task task)
    {
        this.tasks.add(task);
        task.setProject(this);
    }

    @Override
    public String toString()
    {
        return "Project{id=" + id + ", name='" + name + "', client='" + client + 
                "', status=" + status + "}";
    }
}