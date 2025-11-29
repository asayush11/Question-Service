DROP INDEX IF EXISTS idx_question_category_difficulty;

ALTER TABLE question
RENAME COLUMN category TO subject;

CREATE INDEX idx_question_subject_difficulty ON question(subject, difficulty);

CREATE TABLE IF NOT EXISTS user_statistics (
    quiz_id VARCHAR(100) NOT NULL,
    email_id VARCHAR(100) NOT NULL,
    quiz_type VARCHAR(50) NOT NULL,
    total_questions INT DEFAULT 0,
    correct_answers INT DEFAULT 0,
    incorrect_answers INT DEFAULT 0,
    percentage_score DECIMAL DEFAULT 0.0,
    date_taken TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (quiz_id, email_id),
    FOREIGN KEY (email_id) REFERENCES users(email_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_user_statistics_email_id
    ON user_statistics(email_id);

CREATE TABLE IF NOT EXISTS notes (
    topic VARCHAR(255) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    PRIMARY KEY (topic, subject)
);

create index if not exists idx_notes_topic
    on notes(subject);