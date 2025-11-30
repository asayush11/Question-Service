package com.example.questions_service.Entity;

import java.io.Serializable;
import java.util.Objects;

public class NotesId implements Serializable {

    private String topic;
    private String subject;

    public NotesId() {
    }

    public NotesId(String topic, String subject) {
        this.topic = topic;
        this.subject = subject;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    // MUST implement equals() and hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotesId notesId = (NotesId) o;
        return Objects.equals(topic, notesId.getTopic()) &&
                Objects.equals(subject, notesId.getSubject());
    }

    @Override
    public int hashCode() {
        return Objects.hash(topic, subject);
    }
}