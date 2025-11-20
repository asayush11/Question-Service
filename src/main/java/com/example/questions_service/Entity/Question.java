package com.example.questions_service.Entity;
import com.example.questions_service.DTO.QuestionRequestDTO;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "question_id_seq")
    @SequenceGenerator(name = "question_id_seq", sequenceName = "question_id_seq", allocationSize = 1)
    private Long questionId;
    private String question;
    private String category;
    private String difficulty;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private String solution;
    private String type;
    @ElementCollection
    @CollectionTable(
            name = "question_answer",
            joinColumns = @JoinColumn(name = "question_id")
    )
    private List<String> answer;

    public Question() {
    }

    public Question(Long questionId, String question, String category, String difficulty, String option1, String option2, String option3, String option4, String solution, String type, List<String> answer) {
        this.questionId = questionId;
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

    public Question(QuestionRequestDTO questionDTO) {
        this.question = questionDTO.getQuestion();
        this.category = questionDTO.getCategory();
        this.difficulty = questionDTO.getDifficulty();
        this.option1 = questionDTO.getOption1();
        this.option2 = questionDTO.getOption2();
        this.option3 = questionDTO.getOption3();
        this.option4 = questionDTO.getOption4();
        this.solution = questionDTO.getSolution();
        this.type = questionDTO.getType();
        this.answer = questionDTO.getAnswer();
    }

    public Long getQuestionId() {
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

    public String getSolution() {
        return solution;
    }

    public String getType() {
        return type;
    }

    public List<String> getAnswer() {
        return answer;
    }
}
