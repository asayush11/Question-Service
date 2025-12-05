package com.example.questions_service.Utility;

import com.example.questions_service.Cache.QuizAnswersCache;
import com.example.questions_service.Cache.QuizCache;
import com.example.questions_service.DTO.AnswerKeyDTO;
import com.example.questions_service.DTO.AnswerResponseDTO;
import com.example.questions_service.Entity.Question;
import com.example.questions_service.Repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DBWakeUp {
    @Autowired
    QuestionRepository questionRepository;
    @Autowired
    QuizCache quizCache;
    @Autowired
    QuizAnswersCache quizAnswersCache;
    @Value("${liveQuizSize}")
    private int liveQuizSize;
    @Value("${mockSubjectQuizSize}")
    private int mockSubjectQuizSize;

    @Scheduled(fixedDelay = 270000)
    public void warmUpDb() {
        questionRepository.wakeDbLightly();
    }

    @Scheduled(fixedDelay = 86400000)
    public void clearCaches() {
        List<Question> questions = questionRepository.findRandomQuestions(liveQuizSize);
        quizCache.putLiveQuiz(questions);
        List<AnswerKeyDTO> answers = new ArrayList<>();
        for(Question q : questions) {
            answers.add(new AnswerKeyDTO(q.getSolution(), q.getAnswer()));
        }
        quizAnswersCache.put("Live",  new AnswerResponseDTO(answers, answers.size()));

        quizCache.clearMockQuizCache();

        questions = questionRepository.findRandomQuestions(liveQuizSize);
        quizCache.putMockQuiz("FullMock1", questions);
        answers.clear();
        for(Question q : questions) {
            answers.add(new AnswerKeyDTO(q.getSolution(), q.getAnswer()));
        }
        quizAnswersCache.put("FullMock1",  new AnswerResponseDTO(answers, answers.size()));

        questions = questionRepository.findRandomQuestions(liveQuizSize);
        quizCache.putMockQuiz("FullMock2", questions);
        answers.clear();
        for(Question q : questions) {
            answers.add(new AnswerKeyDTO(q.getSolution(), q.getAnswer()));
        }
        quizAnswersCache.put("FullMock2",  new AnswerResponseDTO(answers, answers.size()));

        List<Question> randomQuestions = questionRepository
                .findRandomQuestionsGroupedBySubject(mockSubjectQuizSize);

        Map<String, List<Question>> questionsBySubject = randomQuestions.stream()
                .collect(Collectors.groupingBy(Question::getSubject));

        questionsBySubject.forEach((subject, subjectQuestions) -> {
            quizCache.putMockQuiz("SubjectMock_" + subject, subjectQuestions);
            List<AnswerKeyDTO> subjectAnswers = new ArrayList<>();
            for(Question q : subjectQuestions) {
                subjectAnswers.add(new AnswerKeyDTO(q.getSolution(), q.getAnswer()));
            }
            quizAnswersCache.put("SubjectMock_" + subject,
                    new AnswerResponseDTO(subjectAnswers, subjectAnswers.size()));
        });
    }
}
