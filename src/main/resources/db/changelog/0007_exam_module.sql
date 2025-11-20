CREATE TABLE exams (
                       id SERIAL PRIMARY KEY,
                       lecture_id INTEGER NOT NULL,
                       title VARCHAR(255) NOT NULL,
                       description TEXT,
                       status VARCHAR(24) NOT NULL,
                       opens_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                       closes_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                       time_limit_minutes INTEGER,
                       total_score NUMERIC(6,2) NOT NULL DEFAULT 0,
                       created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       created_by VARCHAR(255),
                       updated_by VARCHAR(255),
                       CONSTRAINT fk_exam_lecture
                           FOREIGN KEY (lecture_id)
                               REFERENCES lectures(id)
                               ON DELETE CASCADE,
                       CONSTRAINT chk_exam_window CHECK (opens_at < closes_at),
                       CONSTRAINT chk_exam_time_limit CHECK (time_limit_minutes IS NULL OR time_limit_minutes > 0),
                       CONSTRAINT chk_exam_total_score CHECK (total_score >= 0)
);

CREATE INDEX idx_exams_lecture ON exams(lecture_id);

CREATE TABLE exam_questions (
                                id SERIAL PRIMARY KEY,
                                exam_id INTEGER NOT NULL,
                                prompt TEXT NOT NULL,
                                question_type VARCHAR(32) NOT NULL,
                                question_order INTEGER NOT NULL,
                                points NUMERIC(6,2) NOT NULL DEFAULT 1,
                                correct_answer TEXT,
                                created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                created_by VARCHAR(255),
                                updated_by VARCHAR(255),
                                CONSTRAINT fk_question_exam
                                    FOREIGN KEY (exam_id)
                                        REFERENCES exams(id)
                                        ON DELETE CASCADE,
                                CONSTRAINT chk_question_points CHECK (points >= 0),
                                CONSTRAINT uq_exam_question_order UNIQUE (exam_id, question_order)
);

CREATE INDEX idx_exam_questions_exam ON exam_questions(exam_id);

CREATE TABLE exam_question_options (
                                       id SERIAL PRIMARY KEY,
                                       question_id INTEGER NOT NULL,
                                       label VARCHAR(16),
                                       display_order INTEGER,
                                       content TEXT NOT NULL,
                                       is_correct BOOLEAN NOT NULL DEFAULT FALSE,
                                       created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       created_by VARCHAR(255),
                                       updated_by VARCHAR(255),
                                       CONSTRAINT fk_option_question
                                           FOREIGN KEY (question_id)
                                               REFERENCES exam_questions(id)
                                               ON DELETE CASCADE
);

CREATE INDEX idx_exam_question_options_question ON exam_question_options(question_id);

CREATE TABLE exam_attempts (
                               id SERIAL PRIMARY KEY,
                               exam_id INTEGER NOT NULL,
                               student_id INTEGER NOT NULL,
                               status VARCHAR(32) NOT NULL,
                               started_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               submitted_at TIMESTAMP WITHOUT TIME ZONE,
                               duration_seconds INTEGER,
                               score NUMERIC(6,2),
                               created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               created_by VARCHAR(255),
                               updated_by VARCHAR(255),
                               CONSTRAINT fk_attempt_exam
                                   FOREIGN KEY (exam_id)
                                       REFERENCES exams(id)
                                       ON DELETE CASCADE,
                               CONSTRAINT fk_attempt_student
                                   FOREIGN KEY (student_id)
                                       REFERENCES users(id)
                                       ON DELETE CASCADE,
                               CONSTRAINT uq_exam_attempt UNIQUE (exam_id, student_id)
);

CREATE INDEX idx_exam_attempts_exam ON exam_attempts(exam_id);
CREATE INDEX idx_exam_attempts_student ON exam_attempts(student_id);

CREATE TABLE exam_answers (
                              id SERIAL PRIMARY KEY,
                              attempt_id INTEGER NOT NULL,
                              question_id INTEGER NOT NULL,
                              selected_option_id INTEGER,
                              answer_text TEXT,
                              is_correct BOOLEAN,
                              score_awarded NUMERIC(6,2),
                              created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              created_by VARCHAR(255),
                              updated_by VARCHAR(255),
                              CONSTRAINT fk_answer_attempt
                                  FOREIGN KEY (attempt_id)
                                      REFERENCES exam_attempts(id)
                                      ON DELETE CASCADE,
                              CONSTRAINT fk_answer_question
                                  FOREIGN KEY (question_id)
                                      REFERENCES exam_questions(id)
                                      ON DELETE CASCADE,
                              CONSTRAINT fk_answer_option
                                  FOREIGN KEY (selected_option_id)
                                      REFERENCES exam_question_options(id)
                                      ON DELETE SET NULL,
                              CONSTRAINT uq_attempt_question UNIQUE (attempt_id, question_id)
);

CREATE INDEX idx_exam_answers_attempt ON exam_answers(attempt_id);
CREATE INDEX idx_exam_answers_question ON exam_answers(question_id);

