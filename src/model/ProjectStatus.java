package model;

public enum ProjectStatus {

    ACTIVE("Active"),
    ARCHIVED("Archived"),
    COMPLETED("Completed");

    private final String displayName;

    ProjectStatus(String displayName)
    {
        this.displayName = displayName;
    }

    public String getDisplayName()
    {
        return displayName;
    }
}
