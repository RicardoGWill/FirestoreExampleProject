package com.ricardogwill.firestoreexampleproject;

public class Note {
    private String title, description;

    public Note() {
        // This public no-argument / no-parameter constructor is needed so the app won't crash.
        // It is also apparently possible to initialize the Strings as "" so arguments aren't needed.
    }

    // This is the constructor that contains arguments.
    public Note(String title, String description) {
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
