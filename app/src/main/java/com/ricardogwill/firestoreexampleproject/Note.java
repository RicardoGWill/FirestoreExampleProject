package com.ricardogwill.firestoreexampleproject;

import com.google.firebase.firestore.Exclude;

public class Note {
    private String documentID, title, description;
    private int priority;

    public Note() {
        // This public no-argument / no-parameter constructor is needed so the app won't crash.
        // It is also apparently possible to initialize the Strings as "" so arguments aren't needed.
    }

    // This is the constructor that contains arguments.
    public Note(String title, String description, int priority) {
        this.title = title;
        this.description = description;
        this.priority = priority;
    }

    @Exclude  // "Exclude" makes it so that it doesn't show up in my document.  (Yoku wakannnai kedo.)
    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
