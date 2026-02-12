-- Migration for Profile and Community Modules

-- PROFILE MODULE TABLES
CREATE TABLE locations (
    id BIGSERIAL PRIMARY KEY,
    city VARCHAR(255),
    country VARCHAR(255)
);

CREATE TABLE employment_statuses (
    id BIGSERIAL PRIMARY KEY,
    status_name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    bio TEXT,
    date_of_birth DATE,
    phone_number VARCHAR(50),
    linkedin_url VARCHAR(255),
    github_url VARCHAR(255),
    portfolio_url VARCHAR(255),
    location_id BIGINT,
    employment_status_id BIGINT,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT fk_profile_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_profile_location FOREIGN KEY (location_id) REFERENCES locations(id),
    CONSTRAINT fk_profile_employment_status FOREIGN KEY (employment_status_id) REFERENCES employment_statuses(id)
);

CREATE TABLE skills (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE profile_skills (
    profile_id BIGINT NOT NULL,
    skill_id BIGINT NOT NULL,
    PRIMARY KEY (profile_id, skill_id),
    CONSTRAINT fk_profile_skills_profile FOREIGN KEY (profile_id) REFERENCES profiles(id),
    CONSTRAINT fk_profile_skills_skill FOREIGN KEY (skill_id) REFERENCES skills(id)
);

-- COMMUNITY MODULE TABLES
CREATE TABLE notices (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    author_id BIGINT NOT NULL,
    is_pinned BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    expires_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT fk_notice_author FOREIGN KEY (author_id) REFERENCES users(id)
);

CREATE TABLE forum_categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    icon_url VARCHAR(255)
);

CREATE TABLE forum_posts (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    author_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT fk_post_author FOREIGN KEY (author_id) REFERENCES users(id),
    CONSTRAINT fk_post_category FOREIGN KEY (category_id) REFERENCES forum_categories(id)
);

CREATE TABLE forum_comments (
    id BIGSERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    author_id BIGINT NOT NULL,
    post_id BIGINT NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT fk_comment_author FOREIGN KEY (author_id) REFERENCES users(id),
    CONSTRAINT fk_comment_post FOREIGN KEY (post_id) REFERENCES forum_posts(id)
);

CREATE TABLE memories (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255),
    description TEXT,
    media_url VARCHAR(255) NOT NULL,
    media_type VARCHAR(50) NOT NULL, -- IMAGE, VIDEO
    uploader_id BIGINT NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT fk_memory_uploader FOREIGN KEY (uploader_id) REFERENCES users(id)
);

-- SEED DATA
INSERT INTO employment_statuses (status_name) VALUES ('Full-time job'), ('Student'), ('Unemployed'), ('Freelancer'), ('Entrepreneur');

-- INDEXES
CREATE INDEX idx_notice_pinned ON notices(is_pinned);
CREATE INDEX idx_post_category ON forum_posts(category_id);
CREATE INDEX idx_comment_post ON forum_comments(post_id);
CREATE INDEX idx_memory_uploader ON memories(uploader_id);
CREATE INDEX idx_profile_dob ON profiles(date_of_birth);
