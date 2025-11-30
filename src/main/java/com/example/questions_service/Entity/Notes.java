package com.example.questions_service.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@IdClass(NotesId.class)
public class Notes {

    @Id
    @NotNull(message = "Enter valid topic")
    @Size(min = 1)
    private String topic;

    @Id
    @NotNull(message = "Enter valid subject")
    @Size(min = 1)
    private String subject;

    @NotNull(message = "Enter valid content")
    @Size(min = 1)
    private String content;

    public Notes() {
    }

    public Notes(String topic, String subject, String content) {
        this.topic = topic;
        this.subject = subject;
        this.content = content;
    }

    // Getters and Setters
    public String getTopic() {
        return topic;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }
}