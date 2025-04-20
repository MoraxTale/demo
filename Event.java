package com.example.demo1;

import java.util.List;

public class Event {
    private String name;
    private String description;
    private List<EventOption> options;

    public Event(String name, String description, List<EventOption> options) {
        this.name = name;
        this.description = description;
        this.options = options;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<EventOption> getOptions() {
        return options;
    }
}