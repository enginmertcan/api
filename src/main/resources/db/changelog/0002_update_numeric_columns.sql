ALTER TABLE enrollments
    ALTER COLUMN grade TYPE NUMERIC(5,2)
        USING grade::numeric(5,2);

ALTER TABLE grade_components
    ALTER COLUMN weight TYPE NUMERIC(5,2)
        USING weight::numeric(5,2);

ALTER TABLE grade_components
    ALTER COLUMN max_score TYPE NUMERIC(5,2)
        USING max_score::numeric(5,2);

ALTER TABLE enrollment_grades
    ALTER COLUMN score TYPE NUMERIC(5,2)
        USING score::numeric(5,2);
