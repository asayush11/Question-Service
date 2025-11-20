-- Drop identity constraints first
ALTER TABLE question ALTER COLUMN question_id DROP IDENTITY IF EXISTS;
ALTER TABLE users ALTER COLUMN user_id DROP IDENTITY IF EXISTS;

-- Now you can change the column types
ALTER TABLE question ALTER COLUMN question_id TYPE BIGINT;
ALTER TABLE users ALTER COLUMN user_id TYPE BIGINT;

-- 1. USER table sequence (existing users = 7)
CREATE SEQUENCE IF NOT EXISTS user_id_seq START WITH 8;

ALTER TABLE users
    ALTER COLUMN user_id
    SET DEFAULT nextval('user_id_seq');

-- 2. QUESTION table sequence (existing questions = 22)
CREATE SEQUENCE IF NOT EXISTS question_id_seq START WITH 23;

ALTER TABLE question
    ALTER COLUMN question_id
    SET DEFAULT nextval('question_id_seq');

-- Link sequences to their columns
ALTER SEQUENCE user_id_seq OWNED BY users.user_id;
ALTER SEQUENCE question_id_seq OWNED BY question.question_id;

-- 3. Align sequences with existing max ids
SELECT setval('user_id_seq', (SELECT COALESCE(MAX(user_id), 1) FROM users));
SELECT setval('question_id_seq', (SELECT COALESCE(MAX(question_id), 1) FROM question));

-- 4. Add question_type column
ALTER TABLE question
    ADD COLUMN IF NOT EXISTS question_type VARCHAR(20) NOT NULL DEFAULT 'SINGLE_CORRECT';

-- 5. Sequence for question_answer (no rows exist yet → start at 1)
CREATE SEQUENCE IF NOT EXISTS question_answer_seq START WITH 1;

-- 6. Create question_answer table for ElementCollection
CREATE TABLE IF NOT EXISTS question_answer (
    id BIGINT NOT NULL DEFAULT nextval('question_answer_seq'),
    question_id BIGINT NOT NULL,
    answer VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (question_id) REFERENCES question(question_id) ON DELETE CASCADE
);

-- Link sequence to its column
ALTER SEQUENCE question_answer_seq OWNED BY question_answer.id;

CREATE INDEX IF NOT EXISTS idx_question_answer_question_id
    ON question_answer(question_id);

-- 7. Migrate existing option1 from question table into question_answer
INSERT INTO question_answer (question_id, answer)
SELECT question_id, option1 FROM question WHERE option1 IS NOT NULL
ON CONFLICT DO NOTHING;