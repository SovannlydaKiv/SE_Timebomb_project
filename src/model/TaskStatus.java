package model;

public enum TaskStatus {
    TODO("To Do"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed");

    private final String displayName;

    TaskStatus(String displayName)
    {
        this.displayName = displayName;
    }

    public String getDisplayName()
    {
        return displayName;
    }
}
