package service;

import dao.ProjectDAO;
import dao.TaskDAO;
import dao.TimeEntryDAO;
import java.sql.SQLException;
import java.util.List;
import model.Project;
import model.ProjectStatus;

public class ProjectService {
    private final ProjectDAO projectDAO;
    private final TaskDAO taskDAO;
    private final TimeEntryDAO timeEntryDAO;

    public ProjectService() {
        this.projectDAO = new ProjectDAO();
        this.taskDAO = new TaskDAO();
        this.timeEntryDAO = new TimeEntryDAO();
    }

    public Project createProject(String name, String description, String client) throws SQLException {
        Project project = new Project(name, description);
        project.setClient(client);
        return projectDAO.save(project);
    }

    public Project getProject(Long id) throws SQLException {
        return projectDAO.findById(id);
    }

    public List<Project> getAllProjects() throws SQLException {
        return projectDAO.findAll();
    }

    public List<Project> getActiveProjects() throws SQLException {
        return projectDAO.findByStatus(ProjectStatus.ACTIVE);
    }

    public List<Project> getProjectByClient(String client) throws SQLException {
        return projectDAO.findByClient(client);
    }

    public void updateProject(Project project) throws SQLException {
        projectDAO.update(project);
    }

    public void deleteProject(Long id) throws SQLException {
        projectDAO.delete(id);
    }

    public void archiveProject(Long id) throws SQLException {
        Project project = projectDAO.findById(id);
        if (project != null) {
            project.setStatus(ProjectStatus.ARCHIVED);
            projectDAO.update(project);
        }
    }

    public int getTaskCount(Long projectId) throws SQLException {
        return projectDAO.countTasks(projectId);
    }

    public int getTotalTimeSpent(Long projectId) throws SQLException {
        return timeEntryDAO.getTotalMinutesByProject(projectId);
    }

    public ProjectSummary getProjectSummary(Long projectId) throws SQLException {
        Project project = projectDAO.findById(projectId);
        if (project == null) {
            return null;
        }

        int taskCount = projectDAO.countTasks(projectId);
        int totalMinutes = timeEntryDAO.getTotalMinutesByProject(projectId);

        return new ProjectSummary(project, taskCount, totalMinutes);
    }

    public static class ProjectSummary {
        private final Project project;
        private final int taskCount;
        private final int totalMinutes;

        public ProjectSummary(Project project, int taskCount, int totalMinutes) {
            this.project = project;
            this.taskCount = taskCount;
            this.totalMinutes = totalMinutes;
        }

        public Project getProject() {
            return project;
        }

        public int getTaskCount() {
            return taskCount;
        }

        public int getTotalMinutes() {
            return totalMinutes;
        }

        public double getTotalHuors() {
            return totalMinutes / 60.0;
        }

        @Override
        public String toString() {
            return String.format("ProjectSummary{project=%s, tasks=%d, time=%dh %dm}",
                project.getName(), taskCount, totalMinutes / 60, totalMinutes % 60);
        }
    }
}