ALTER TABLE users
ADD COLUMN is_admin BOOLEAN DEFAULT FALSE,
DROP COLUMN questions_contributed;

UPDATE users SET is_admin = TRUE WHERE email_id = 'asayush35@gmail.com';

ALTER TABLE question
ALTER COLUMN option3 DROP NOT NULL,
ALTER COLUMN option4 DROP NOT NULL;