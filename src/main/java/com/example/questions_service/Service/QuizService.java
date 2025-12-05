package com.example.questions_service.Service;
import com.example.questions_service.Cache.QuestionsCache;
import com.example.questions_service.Cache.QuizAnswersCache;
import com.example.questions_service.Cache.QuizCache;
import com.example.questions_service.DTO.AnswerKeyDTO;
import com.example.questions_service.DTO.AnswerResponseDTO;
import com.example.questions_service.DTO.QuestionResponseDTO;
import com.example.questions_service.DTO.QuizDTO;
import com.example.questions_service.Entity.Question;
import com.example.questions_service.Entity.UserStatistics;
import com.example.questions_service.Exception.UserNotAdminException;
import com.example.questions_service.Repository.QuestionRepository;
import com.example.questions_service.Utility.UserHelper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.cache.CaffeineCacheMetrics;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class QuizService {

    @Autowired
    UserService userService;
    @Autowired
    QuestionsCache questionsCache;
    @Autowired
    QuizCache quizCache;
    @Autowired
    QuizAnswersCache quizAnswersCache;
    @Autowired
    QuestionRepository questionRepository;
    private static final Logger logger = LoggerFactory.getLogger(QuizService.class);

    public QuizDTO getPracticeQuestions(List<String> subjects, String difficulty, int numberOfQuestions, String email) {
        logger.info("Service: Generating practice questions for user: {} with subjects: {} and difficulty: {}", email, subjects, difficulty);

        int numberOfEasy = 0;
        int numberOfMedium = 0;
        int numberOfDifficult = 0;

        switch (difficulty) {
            case "EASY" -> numberOfEasy = numberOfQuestions;
            case "MEDIUM" -> numberOfMedium = numberOfQuestions;
            case "HARD" -> numberOfDifficult = numberOfQuestions;
            default -> {
                numberOfEasy = numberOfQuestions / 3;
                numberOfMedium = numberOfQuestions / 3;
                numberOfDifficult = numberOfQuestions - numberOfEasy - numberOfMedium;
            }
        }

        List<Question> result = new ArrayList<>();

        for(int i = 0; i < subjects.size(); i++){
            String subject = subjects.get(i);
            if(i == subjects.size() - 1){
                int remainingEasy = numberOfEasy - (numberOfEasy / subjects.size()) * (subjects.size() - 1);
                int remainingMedium = numberOfMedium - (numberOfMedium / subjects.size()) * (subjects.size() - 1);
                int remainingDifficult = numberOfDifficult - (numberOfDifficult / subjects.size()) * (subjects.size() - 1);
                List<Question> questionsForSubject = getQuestionsBySubject(subject, remainingEasy , remainingMedium, remainingDifficult);
                result.addAll(questionsForSubject);
            } else {
                List<Question> questionsForSubject = getQuestionsBySubject(subject, numberOfEasy / subjects.size() , numberOfMedium / subjects.size(), numberOfDifficult / subjects.size());
                result.addAll(questionsForSubject);
            }
        }

        String quizID = "P" + email + subjects.toString() + quizAnswersCache.estimatedSize() + " " + numberOfDifficult + " " + numberOfMedium + " " + numberOfEasy;
        List<AnswerKeyDTO> answers = new ArrayList<>();
        List<QuestionResponseDTO> questions = new ArrayList<>();
        for(Question q : result){
            answers.add(new AnswerKeyDTO(q.getSolution(), q.getAnswer()));
            questions.add(new QuestionResponseDTO(q.getQuestion(), q.getSubject(), q.getDifficulty(), q.getOption1(), q.getOption2(), q.getOption3(), q.getOption4(), q.getType()));
        }
        AnswerResponseDTO correctAnswers = new AnswerResponseDTO(answers, answers.size());

        quizAnswersCache.put(quizID, correctAnswers);
        logger.info("Service: Questions fetched successfully for quizID: {}", quizID);

        userService.updateQuizzesTaken(email,1);
        return new QuizDTO(questions, quizID);
    }

    private List<Question> getQuestionsBySubject(String subject, int numberOfEasy, int numberOfMedium, int numberOfDifficult) {
        logger.info("Service: Fetching questions for subject: {} with Easy: {}, Medium: {}, Difficult: {}", subject, numberOfEasy, numberOfMedium, numberOfDifficult);
        List<Question> result = new ArrayList<>();
        Map<String, Integer> difficultyCount = Map.of(
                "EASY", numberOfEasy,
                "MEDIUM", numberOfMedium,
                "HARD", numberOfDifficult
        );

        for (Map.Entry<String, Integer> entry : difficultyCount.entrySet()) {
            String difficulty = entry.getKey();
            int count = entry.getValue();
            List<Question> questions = getAllQuestions(subject, difficulty);
            Collections.shuffle(questions);
            result.addAll(questions.subList(0, Math.min(count, questions.size())));
        }
        return result;
    }

    private List<Question> getAllQuestions(String subject, String difficulty) {
        logger.info("Service: Retrieving questions for subject: {} and difficulty: {} from cache", subject, difficulty);
        List<Question> questions = questionsCache.get(subject, difficulty);
        if(questions != null){
            return questions;
        }
        logger.info("Service: Cache miss for subject: {} and difficulty: {}. Fetching from database.", subject, difficulty);
        questions = fetchQuestionsFromDatabase(subject, difficulty);
        logger.info("Service: Storing fetched questions in cache for subject: {} and difficulty: {}", subject, difficulty);
        questionsCache.put(subject, difficulty, questions);
        return questions;
    }

    private List<Question> fetchQuestionsFromDatabase(String subject, String difficulty) {
        try {
            List<Question> questions = questionRepository.findBySubjectAndDifficulty(subject, difficulty);
            return questions != null ? questions : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public AnswerResponseDTO getAnswers(String quizId, List<List<String>> userAnswers, String email, String quizType) {
        logger.info("Service: Fetching answers for quizId: {}", quizId);
        AnswerResponseDTO answers = quizAnswersCache.get(quizId);
        if(quizType.equalsIgnoreCase("PRACTICE")) quizAnswersCache.invalidateKey(quizId);
        logger.info("Service: Answers fetched for quizId: {}", quizId);
        logger.info("Service: Computing results for quizId: {}", quizId);
        return computeResults(answers, userAnswers, email, quizId, quizType);
    }

    private AnswerResponseDTO computeResults(AnswerResponseDTO correctAnswers, List<List<String>> userAnswers, String email, String quizId, String quizType) {
        int correctCount = 0;
        int notAttempted = 0;
        int totalQuestions = correctAnswers.getTotalQuestions();
        List<AnswerKeyDTO> answerKeys = correctAnswers.getAnswers();

        for (int i = 0; i < totalQuestions; i++) {
            List<String> userAnswer = userAnswers.get(i);
            if(userAnswer.isEmpty()){
                notAttempted++;
                continue;
            }
            List<String> correctAnswer = answerKeys.get(i).getAnswerKey();
            if (new HashSet<>(userAnswer).equals(new HashSet<>(correctAnswer))) {
                correctCount++;
            }
        }

        int incorrectCount = totalQuestions - correctCount - notAttempted;
        double percentageScore = (( correctCount - (0.5 * incorrectCount)) / totalQuestions) * 100;

        correctAnswers.setCorrectAnswers(correctCount);
        correctAnswers.setIncorrectAnswers(incorrectCount);
        correctAnswers.setPercentageScore(percentageScore);

        logger.info("Service: Results computed - Correct: {}, Incorrect: {}, Percentage: {}%", correctCount, incorrectCount, percentageScore);

        UserStatistics statistics = new UserStatistics(
                quizId,
                email,
                quizType,
                totalQuestions,
                correctCount,
                incorrectCount,
                percentageScore
        );
        userService.updateStats(email, statistics);

        return correctAnswers;
    }

    public QuizDTO getLiveQuiz(String email) {
        logger.info("Service: Generating live quiz questions for user: {}", email);
        List<Question> quiz = quizCache.getLiveQuiz();

        if (quiz == null) {
            logger.info("No live quiz at the moment");
            return null;
        }

        List<QuestionResponseDTO> questions = new ArrayList<>();
        for (Question q : quiz) {
            questions.add(new QuestionResponseDTO(q.getQuestion(), q.getSubject(), q.getDifficulty(), q.getOption1(), q.getOption2(), q.getOption3(), q.getOption4(), q.getType()));
        }

        userService.updateQuizzesTaken(email, 1);
        return new QuizDTO(questions, "Live");
    }

    public QuizDTO getMockQuiz(String email, String quizId) {
        logger.info("Service: Generating mock quiz questions for user: {} with quizId: {}", email, quizId);
        List<Question> quiz = quizCache.getMockQuiz(quizId);

        if (quiz == null) {
            logger.info("No mock quiz found for quizId: {}", quizId);
            return null;
        }

        List<AnswerKeyDTO> answers = new ArrayList<>();
        List<QuestionResponseDTO> questions = new ArrayList<>();
        for (Question q : quiz) {
            answers.add(new AnswerKeyDTO(q.getSolution(), q.getAnswer()));
            questions.add(new QuestionResponseDTO(q.getQuestion(), q.getSubject(), q.getDifficulty(), q.getOption1(), q.getOption2(), q.getOption3(), q.getOption4(), q.getType()));
        }
        AnswerResponseDTO correctAnswers = new AnswerResponseDTO(answers, answers.size());

        quizAnswersCache.put(quizId, correctAnswers);

        userService.updateQuizzesTaken(email, 1);
        return new QuizDTO(questions, quizId);
    }
}
