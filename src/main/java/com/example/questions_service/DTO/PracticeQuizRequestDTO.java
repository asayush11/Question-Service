package com.example.questions_service.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class PracticeQuizRequestDTO {
    @NotNull(message = "Select at least one subject")
    @Size(min = 1)
    private List<String> subjects;
    private String difficulty;
    @NotNull(message = "Number of questions must be at least 1")
    @Size(min = 1)
    private int numberOfQuestions;

    public PracticeQuizRequestDTO(List<String> subjects, String difficulty, int numberOfQuestions) {
        this.subjects = subjects;
        this.difficulty = difficulty;
        this.numberOfQuestions = numberOfQuestions;
    }
    public List<String> getSubjects() {
        return subjects;
    }
    public String getDifficulty() {
        return difficulty;
    }
    public int getNumberOfQuestions() {
        return numberOfQuestions;
    }
}
