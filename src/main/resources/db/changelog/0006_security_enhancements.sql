--liquibase formatted sql
--changeset gpt-5:security_enhancements
ALTER TABLE lecture_api.users
    ADD COLUMN IF NOT EXISTS mfa_enabled BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE lecture_api.users
    ADD COLUMN IF NOT EXISTS preferred_mfa_channel VARCHAR(32) DEFAULT 'EMAIL';

CREATE TABLE IF NOT EXISTS lecture_api.activity_logs
(
    id BIGSERIAL PRIMARY KEY,
    entity_name VARCHAR(255) NOT NULL,
    entity_id VARCHAR(255),
    action VARCHAR(64) NOT NULL,
    performed_by VARCHAR(255),
    performed_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    details TEXT
);

