ALTER TABLE enrollments
    ADD COLUMN IF NOT EXISTS absence_count INTEGER NOT NULL DEFAULT 0;

CREATE TABLE IF NOT EXISTS enrollment_attendance (
    id SERIAL PRIMARY KEY,
    enrollment_id INTEGER NOT NULL,
    week_of DATE NOT NULL,
    attended BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT fk_attendance_enrollment FOREIGN KEY (enrollment_id) REFERENCES enrollments(id) ON DELETE CASCADE,
    CONSTRAINT uq_attendance_week UNIQUE (enrollment_id, week_of)
);

CREATE INDEX IF NOT EXISTS idx_attendance_enrollment ON enrollment_attendance(enrollment_id);
