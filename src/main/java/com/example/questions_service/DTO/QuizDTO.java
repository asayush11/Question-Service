package com.example.questions_service.DTO;

import com.example.questions_service.Entity.Question;

import java.util.List;

public class QuizDTO {
    public List<QuestionResponseDTO> questions;
    public String quizId;

    public QuizDTO(List<QuestionResponseDTO> questions, String quizId) {
        this.questions = questions;
        this.quizId = quizId;
    }
}
