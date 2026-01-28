package service;

import dao.TaskDAO;
import dao.TimeEntryDAO;
import java.sql.SQLException;
import java.util.List;
import model.*;

public class TaskService {
    private final TaskDAO taskDAO;
    private final TimeEntryDAO timeEntryDAO;

    public TaskService() {
        this.taskDAO = new TaskDAO();
        this.timeEntryDAO = new TimeEntryDAO();
    }

    public Task creatTask(String name, String description, Project project, Priority priority) throws SQLException {
        Task task = new Task();
        task.setName(name);
        task.setProject(project);
        task.setDescription(description);
        task.setPriority(priority);
        return taskDAO.save(task);
    }

    public Task getTask(Long id) throws SQLException {
        return taskDAO.findById(id);
    }

    public List<Task> getAllTasks() throws SQLException {
        return taskDAO.findAll();
    }

    public List<Task> getTaskByProject(Long projectId) throws SQLException {
        return taskDAO.findByProjectId(projectId);
    }

    public List<Task> getTaskByStatus(TaskStatus status) throws SQLException {
        return taskDAO.findByStatus(status);
    }

    public List<Task> getTaskByPriority(Priority priority) throws SQLException {
        return taskDAO.findByPriority(priority);
    }

    public List<Task> getTaskByTag(String tag) throws SQLException {
        return taskDAO.findByTag(tag);
    }

    public void updateTask(Task task) throws SQLException {
        taskDAO.update(task);
    }

    public void deleteTask(Long id) throws SQLException {
        taskDAO.delete(id);
    }

    public void markAsInProgress(Long taskId) throws SQLException {
        Task task = taskDAO.findById(taskId);
        if (task != null) {
            task.setStatus(TaskStatus.IN_PROGRESS);
            taskDAO.update(task);
        }
    }

    public void markAsCompleted(Long taskId) throws SQLException {
        Task task = taskDAO.findById(taskId);
        if (task != null) {
            task.setStatus(TaskStatus.COMPLETED);
            taskDAO.update(task);
        }
    }

    public void addTag(Long taskId, String tag) throws SQLException {
        Task task = taskDAO.findById(taskId);
        if (task != null && !task.getTags().contains(tag)) {
            task.addTags(tag);
            taskDAO.update(task);
        }
    }

    public int getTotalTimeSpent(Long taskId) throws SQLException {
        return timeEntryDAO.getTotalMinutesByTask(taskId);
    }

    public TaskProgress getTaskProgress(Long taskId) throws SQLException {
        Task task = taskDAO.findById(taskId);
        if(task == null) {
            return null;
        }

        int actualMinutes = timeEntryDAO.getTotalMinutesByTask(taskId);
        Integer estimaedMinutes = task.getEstimationMinutes();

        return new TaskProgress(task, actualMinutes, estimaedMinutes);
    }

    public static class TaskProgress {
        private final Task task;
        private final int actualMinutes;
        private final Integer estimatedMinutes;

        public TaskProgress(Task task, int actualMinutes, Integer estimatedMinutes) {
            this.task = task;
            this.actualMinutes = actualMinutes;
            this.estimatedMinutes = estimatedMinutes;
        }

        public Task getTask() {
            return task;
        }

        public int getActualMinutes() {
            return actualMinutes;
        }

        public Integer getEstimatedMinutes() {
            return estimatedMinutes;
        }

        public Double getProgressPercentage() {
            if (estimatedMinutes == null || estimatedMinutes == 0) {
                return null;
            }

            return (actualMinutes * 100.0) / estimatedMinutes;
        }

        @Override
        public String toString() {
            String progress = estimatedMinutes != null
                ? String.format("%.1f%%", getProgressPercentage())
                : "No estimate";
            return String.format("TaskProgress{task=%s, actual=%dh %dm, estimated=%s, progress=%s}",
                task.getName(), actualMinutes / 60, actualMinutes % 60,
                estimatedMinutes != null ? estimatedMinutes + "m" : "N/A", progress);
        }
    }
}
