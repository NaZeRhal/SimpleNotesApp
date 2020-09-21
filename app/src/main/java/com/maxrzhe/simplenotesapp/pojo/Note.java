package com.maxrzhe.simplenotesapp.pojo;

import com.google.firebase.Timestamp;

public class Note {

    private String userId;
    private String text;
    private Timestamp created;
    private boolean isCompleted;

    public Note() {
    }

    public Note(String userId, String text, Timestamp created, boolean isCompleted) {
        this.userId = userId;
        this.text = text;
        this.created = created;
        this.isCompleted = isCompleted;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public boolean getIsCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    @Override
    public String toString() {
        return "Note{" +
                "userId='" + userId + '\'' +
                ", text='" + text + '\'' +
                ", created=" + created +
                ", isCompleted=" + isCompleted +
                '}';
    }
}
