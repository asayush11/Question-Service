package com.example.questions_service.DTO;

public class QuestionResponseDTO {
    private String question;
    private String subject;
    private String difficulty;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private String type;

    public QuestionResponseDTO(String question, String subject, String difficulty, String option1, String option2, String option3, String option4, String type) {
        this.question = question;
        this.subject = subject;
        this.difficulty = difficulty;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.type = type;
    }

    public String getQuestion() {
        return question;
    }

    public String getSubject() {
        return subject;
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

    public String getType() {
        return type;
    }
}
