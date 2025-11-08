CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       surname VARCHAR(255) NOT NULL,
                       identity_no VARCHAR(11) NOT NULL UNIQUE,
                       gender VARCHAR(11) NOT NULL,
                       urole VARCHAR(16) NOT NULL,
                       created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       created_by VARCHAR(255),
                       updated_by VARCHAR(255)
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
                              grade NUMERIC(4,2),
                              enrolled_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
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
