CREATE TABLE users (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(255)        NOT NULL,
    email           VARCHAR(255)        NOT NULL UNIQUE,
    password_hash   VARCHAR(255),
    role            VARCHAR(20)         NOT NULL DEFAULT 'USER',
    auth_provider   VARCHAR(20)         NOT NULL DEFAULT 'LOCAL',
    email_verified  BOOLEAN             NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP           NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP           NOT NULL DEFAULT now()
);

CREATE INDEX idx_users_email ON users (email);
