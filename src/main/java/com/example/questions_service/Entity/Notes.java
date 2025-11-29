package com.example.questions_service.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
public class Notes {

    @NotNull(message = "Enter valid topic")
    @Size(min = 1)
    private String topic;
    @NotNull(message = "Enter valid subject")
    @Size(min = 1)
    private String subject;
    @NotNull(message = "Enter valid content")
    @Size(min = 1)
    private String content;

    public Notes() {
    }

    public Notes(String subject, String topic, String content) {
        this.topic = topic;
        this.content = content;
        this.subject = subject;
    }

    public String getTopic() {
        return topic;
    }

    public String getContent() {
        return content;
    }

    public String getSubject() {
        return subject;
    }
}
