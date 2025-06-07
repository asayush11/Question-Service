CREATE TABLE IF NOT EXISTS question (
    question_id VARCHAR(10) PRIMARY KEY,
    question VARCHAR(500) NOT NULL,
    category VARCHAR(50) NOT NULL,
    difficulty VARCHAR(10) NOT NULL,
    option1 VARCHAR(100) NOT NULL,
    option2 VARCHAR(100) NOT NULL,
    option3 VARCHAR(100) NOT NULL,
    option4 VARCHAR(100) NOT NULL,
    solution VARCHAR(500)
);

CREATE INDEX idx_question_category_difficulty ON question(category, difficulty);