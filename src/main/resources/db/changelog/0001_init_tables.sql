CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       surname VARCHAR(255) NOT NULL,
                       identity_no VARCHAR(11) NOT NULL UNIQUE,
                       gender VARCHAR(11) NOT NULL,
                       urole VARCHAR(16) NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       created_by VARCHAR(255),
                       updated_by VARCHAR(255)
);

CREATE TABLE refresh_tokens (
                                id SERIAL PRIMARY KEY,
                                user_id INTEGER NOT NULL,
                                token VARCHAR(512) NOT NULL UNIQUE,
                                created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                expires_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                                revoked BOOLEAN NOT NULL DEFAULT FALSE,
                                CONSTRAINT fk_refresh_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE lectures (
                          id SERIAL PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          description TEXT,
                          capacity INTEGER NOT NULL CHECK (capacity > 0),
                          teacher_id INTEGER NOT NULL,
                          created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          created_by VARCHAR(255),
                          updated_by VARCHAR(255),
                          CONSTRAINT fk_teacher_id
                              FOREIGN KEY (teacher_id)
                                  REFERENCES users(id)
);

CREATE TABLE enrollments (
                              id SERIAL PRIMARY KEY,
                              lecture_id INTEGER NOT NULL,
                              student_id INTEGER NOT NULL,
                              status VARCHAR(32) NOT NULL,
                              grade NUMERIC(5,2),
                              enrolled_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              approved_at TIMESTAMP WITHOUT TIME ZONE,
                              completed_at TIMESTAMP WITHOUT TIME ZONE,
                              waitlist_position INTEGER,
                              passed BOOLEAN NOT NULL DEFAULT FALSE,
                              created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              created_by VARCHAR(255),
                              updated_by VARCHAR(255),
                              CONSTRAINT uq_enrollment UNIQUE (lecture_id, student_id),
                              CONSTRAINT fk_enrollment_lecture
                                  FOREIGN KEY (lecture_id)
                                      REFERENCES lectures(id),
                              CONSTRAINT fk_enrollment_student
                                  FOREIGN KEY (student_id)
                                      REFERENCES users(id)
);

CREATE INDEX idx_enrollments_student ON enrollments(student_id);

CREATE TABLE classrooms (
                            id SERIAL PRIMARY KEY,
                            name VARCHAR(255) NOT NULL UNIQUE,
                            location VARCHAR(255),
                            capacity INTEGER NOT NULL CHECK (capacity > 0),
                            created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            created_by VARCHAR(255),
                            updated_by VARCHAR(255)
);

CREATE TABLE schedule_slots (
                                id SERIAL PRIMARY KEY,
                                day_of_week VARCHAR(16) NOT NULL,
                                start_time TIME NOT NULL,
                                end_time TIME NOT NULL,
                                created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                created_by VARCHAR(255),
                                updated_by VARCHAR(255),
                                CONSTRAINT chk_slot_time CHECK (start_time < end_time)
);

CREATE TABLE lecture_schedules (
                                   id SERIAL PRIMARY KEY,
                                   lecture_id INTEGER NOT NULL,
                                   classroom_id INTEGER NOT NULL,
                                   schedule_slot_id INTEGER NOT NULL,
                                   start_date DATE,
                                   end_date DATE,
                                   created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   created_by VARCHAR(255),
                                   updated_by VARCHAR(255),
                                   CONSTRAINT fk_schedule_lecture
                                       FOREIGN KEY (lecture_id)
                                           REFERENCES lectures(id),
                                   CONSTRAINT fk_schedule_classroom
                                       FOREIGN KEY (classroom_id)
                                           REFERENCES classrooms(id),
                                   CONSTRAINT fk_schedule_slot
                                       FOREIGN KEY (schedule_slot_id)
                                           REFERENCES schedule_slots(id),
                                   CONSTRAINT uq_lecture_schedule UNIQUE (lecture_id, schedule_slot_id)
);

CREATE INDEX idx_lecture_schedules_classroom ON lecture_schedules(classroom_id);
CREATE INDEX idx_lecture_schedules_slot ON lecture_schedules(schedule_slot_id);

CREATE TABLE grade_components (
                                  id SERIAL PRIMARY KEY,
                                  lecture_id INTEGER NOT NULL,
                                  name VARCHAR(255) NOT NULL,
                                  weight NUMERIC(5,2) NOT NULL,
                                  max_score NUMERIC(5,2) NOT NULL DEFAULT 100,
                                  created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  created_by VARCHAR(255),
                                  updated_by VARCHAR(255),
                                  CONSTRAINT fk_component_lecture
                                      FOREIGN KEY (lecture_id)
                                          REFERENCES lectures(id),
                                  CONSTRAINT chk_component_weight CHECK (weight > 0 AND weight <= 100),
                                  CONSTRAINT uq_component_name UNIQUE (lecture_id, name)
);

CREATE TABLE enrollment_grades (
                                    id SERIAL PRIMARY KEY,
                                    enrollment_id INTEGER NOT NULL,
                                    grade_component_id INTEGER NOT NULL,
                                    score NUMERIC(5,2) NOT NULL,
                                    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    created_by VARCHAR(255),
                                    updated_by VARCHAR(255),
                                    CONSTRAINT fk_grade_enrollment
                                        FOREIGN KEY (enrollment_id)
                                            REFERENCES enrollments(id),
                                    CONSTRAINT fk_grade_component
                                        FOREIGN KEY (grade_component_id)
                                            REFERENCES grade_components(id),
                                    CONSTRAINT uq_enrollment_component UNIQUE (enrollment_id, grade_component_id)
);
