CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       surname VARCHAR(255) NOT NULL,
                       identity_no VARCHAR(11) NOT NULL UNIQUE,
                       gender VARCHAR(11) NOT NULL,
                       urole VARCHAR(16) NOT NULL
);

CREATE TABLE lectures (
                          id SERIAL PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          description TEXT,
                          capacity INTEGER NOT NULL CHECK (capacity > 0),
                          teacher_id INTEGER NOT NULL,
                          CONSTRAINT fk_teacher_id
                              FOREIGN KEY (teacher_id)
                                  REFERENCES users(id)
);

CREATE TABLE enrollments (
                             id SERIAL PRIMARY KEY,
                             lecture_id INTEGER NOT NULL,
                             student_id INTEGER NOT NULL,
                             status VARCHAR(32) NOT NULL,
                             grade NUMERIC(4,2),
                             enrolled_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             CONSTRAINT uq_enrollment UNIQUE (lecture_id, student_id),
                             CONSTRAINT fk_enrollment_lecture
                                 FOREIGN KEY (lecture_id)
                                     REFERENCES lectures(id),
                             CONSTRAINT fk_enrollment_student
                                 FOREIGN KEY (student_id)
                                     REFERENCES users(id)
);

CREATE INDEX idx_enrollments_student ON enrollments(student_id);
