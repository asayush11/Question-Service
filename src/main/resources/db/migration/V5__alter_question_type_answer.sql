ALTER TABLE question ALTER COLUMN question_id TYPE BIGINT;
ALTER TABLE users ALTER COLUMN user_id TYPE BIGINT;

-- 1. USER table sequence (existing users = 7)
CREATE SEQUENCE user_id_seq START WITH 8;

ALTER TABLE "users"
    ALTER COLUMN user_id
    SET DEFAULT nextval('user_id_seq');

-- 2. QUESTION table sequence (existing questions = 22)
CREATE SEQUENCE question_id_seq START WITH 23;

ALTER TABLE question
    ALTER COLUMN question_id
    SET DEFAULT nextval('question_id_seq');


-- 3. Add question_type column
ALTER TABLE question
    ADD COLUMN question_type VARCHAR(20) NOT NULL DEFAULT 'SINGLE_CORRECT';


-- 4. Sequence for question_answer (no rows exist yet → start at 1)
CREATE SEQUENCE question_answer_seq START WITH 1;


-- 5. Create question_answer table for ElementCollection
CREATE TABLE question_answer (
    id BIGINT NOT NULL DEFAULT nextval('question_answer_seq'),
    question_id BIGINT NOT NULL,
    answer VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (question_id) REFERENCES question(question_id) ON DELETE CASCADE
);

CREATE INDEX idx_question_answer_question_id
    ON question_answer(question_id);


-- 6. Migrate existing option1 from question table into question_answer
INSERT INTO question_answer (question_id, answer)
SELECT question_id, option1 FROM question WHERE option1 IS NOT NULL;
