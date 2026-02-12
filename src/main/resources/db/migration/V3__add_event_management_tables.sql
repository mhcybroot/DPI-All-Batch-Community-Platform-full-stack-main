-- EVENT MANAGEMENT MODULE TABLES

-- Events Table
CREATE TABLE events (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    event_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    venue VARCHAR(255),
    venue_address TEXT,
    organizer_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'UPCOMING',
    max_attendees INT,
    registration_deadline TIMESTAMP WITHOUT TIME ZONE,
    cover_image_url VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT fk_event_organizer FOREIGN KEY (organizer_id) REFERENCES users(id),
    CONSTRAINT chk_event_status CHECK (status IN ('DRAFT', 'UPCOMING', 'ONGOING', 'COMPLETED', 'CANCELLED'))
);

-- Registrations Table
CREATE TABLE registrations (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    rejection_reason TEXT,
    reviewed_by BIGINT,
    reviewed_at TIMESTAMP WITHOUT TIME ZONE,
    notes TEXT,
    registered_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT fk_registration_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    CONSTRAINT fk_registration_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_registration_reviewer FOREIGN KEY (reviewed_by) REFERENCES users(id),
    CONSTRAINT uq_event_user UNIQUE (event_id, user_id),
    CONSTRAINT chk_registration_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'WAITLISTED', 'CANCELLED', 'ATTENDED'))
);

-- Polls Table
CREATE TABLE polls (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT,
    question VARCHAR(500) NOT NULL,
    created_by BIGINT NOT NULL,
    is_multiple_choice BOOLEAN DEFAULT FALSE,
    is_anonymous BOOLEAN DEFAULT FALSE,
    deadline TIMESTAMP WITHOUT TIME ZONE,
    is_closed BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT fk_poll_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    CONSTRAINT fk_poll_creator FOREIGN KEY (created_by) REFERENCES users(id)
);

-- Poll Options Table
CREATE TABLE poll_options (
    id BIGSERIAL PRIMARY KEY,
    poll_id BIGINT NOT NULL,
    option_text VARCHAR(255) NOT NULL,
    vote_count INT DEFAULT 0,
    CONSTRAINT fk_option_poll FOREIGN KEY (poll_id) REFERENCES polls(id) ON DELETE CASCADE
);

-- Votes Table
CREATE TABLE votes (
    id BIGSERIAL PRIMARY KEY,
    poll_option_id BIGINT NOT NULL,
    voter_id BIGINT NOT NULL,
    voted_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT fk_vote_option FOREIGN KEY (poll_option_id) REFERENCES poll_options(id) ON DELETE CASCADE,
    CONSTRAINT fk_vote_voter FOREIGN KEY (voter_id) REFERENCES users(id),
    CONSTRAINT uq_voter_option UNIQUE (poll_option_id, voter_id)
);

-- INDEXES
CREATE INDEX idx_event_date ON events(event_date);
CREATE INDEX idx_event_status ON events(status);
CREATE INDEX idx_event_organizer ON events(organizer_id);
CREATE INDEX idx_registration_event ON registrations(event_id);
CREATE INDEX idx_registration_user ON registrations(user_id);
CREATE INDEX idx_poll_event ON polls(event_id);
CREATE INDEX idx_poll_deadline ON polls(deadline);
CREATE INDEX idx_vote_option ON votes(poll_option_id);
