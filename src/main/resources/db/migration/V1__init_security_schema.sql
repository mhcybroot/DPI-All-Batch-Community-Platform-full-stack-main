CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    is_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    is_account_non_locked BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE approval_requests (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    user_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL, -- PENDING, APPROVED, REJECTED
    reviewed_by BIGINT,
    rejection_reason TEXT,
    CONSTRAINT fk_approval_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_approval_reviewer FOREIGN KEY (reviewed_by) REFERENCES users(id)
);

CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    actor_id BIGINT,
    action VARCHAR(255) NOT NULL,
    target_id VARCHAR(255),
    details TEXT,
    ip_address VARCHAR(50)
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Index for performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_approval_status ON approval_requests(status);
CREATE INDEX idx_audit_timestamp ON audit_logs(timestamp);
