package com.example.questions_service.Entity;
import com.example.questions_service.DTO.QuestionDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Question {
    @Id
    private String questionId;
    private String question;
    private String category;
    private String difficulty;
    private String option1;
    private String option2;
    private String option3;
    private String option4;

    public Question(QuestionDTO questionDTO) {
        this.question = questionDTO.getQuestion();
        this.category = questionDTO.getCategory();
        this.difficulty = questionDTO.getDifficulty();
        this.option1 = questionDTO.getOption1();
        this.option2 = questionDTO.getOption2();
        this.option3 = questionDTO.getOption3();
        this.option4 = questionDTO.getOption4();
    }

    public String getQuestionId() {
        return questionId;
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
}
