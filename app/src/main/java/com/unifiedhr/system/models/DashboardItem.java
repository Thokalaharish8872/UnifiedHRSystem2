package com.unifiedhr.system.models;

public class DashboardItem {
    private String title;
    private String description;

    public DashboardItem(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}

