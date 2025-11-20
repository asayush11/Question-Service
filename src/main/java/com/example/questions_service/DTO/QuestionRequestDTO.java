package com.example.questions_service.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class QuestionRequestDTO {
    @NotNull(message = "Enter valid question")
    @Size(min = 1)
    private String question;
    @NotNull(message = "Enter valid category")
    @Size(min = 1)
    private String category;
    private String difficulty;
    @NotNull(message = "Enter valid option 1")
    @Size(min = 1)
    private String option1;
    @NotNull(message = "Enter valid option 2")
    @Size(min = 1)
    private String option2;
    @NotNull(message = "Enter valid option 3")
    @Size(min = 1)
    private String option3;
    @NotNull(message = "Enter valid option 4")
    @Size(min = 1)
    private String option4;

    private String solution;

    @NotNull(message = "Enter valid type")
    @Size(min = 1)
    private String type;

    @NotNull(message = "Enter answers")
    @Size(min = 1)
    private List<String> answer;

    public QuestionRequestDTO() {
    }

    public String getQuestion() {
        return question;
    }

    public String getCategory() {
        return category;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getOption1() {
        return option1;
    }

    public String getOption2() {
        return option2;
    }

    public String getOption3() {
        return option3;
    }

    public String getOption4() {
        return option4;
    }
    public String getSolution() {
        return solution;
    }

    public QuestionRequestDTO(String question, String category, String difficulty, String option1, String option2, String option3, String option4, String solution, String type, List<String> answer) {
        this.question = question;
        this.category = category;
        this.difficulty = difficulty;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.solution = solution;
        this.type = type;
        this.answer = answer;
    }

    public String getType() {
        return type;
    }

    public List<String> getAnswer() {
        return answer;
    }
}
