package com.example.questions_service.Utility;

public record LoginValidationResult(String token, String username, Integer numberOfQuizzes, Integer numberOfQuestions) {}
