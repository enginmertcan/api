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
                          teacher_id INTEGER NOT NULL,
                          CONSTRAINT fk_teacher_id
                              FOREIGN KEY (teacher_id)
                                  REFERENCES users(id)
);

CREATE TABLE user_lectures (
                               user_id INTEGER NOT NULL,
                               lecture_id INTEGER NOT NULL,
                               CONSTRAINT pk_user_lectures PRIMARY KEY (user_id, lecture_id),
                               CONSTRAINT fk_user_id
                                   FOREIGN KEY (user_id)
                                       REFERENCES users(id),
                               CONSTRAINT fk_lecture_id
                                   FOREIGN KEY (lecture_id)
                                       REFERENCES lectures(id)
);
