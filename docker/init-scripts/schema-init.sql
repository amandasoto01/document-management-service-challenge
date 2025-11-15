--The script to initialize the schema was sourced from the Spring Batch Core dependency: org.springframework.batch.core.

CREATE SCHEMA IF NOT EXISTS document_schema;
SET SCHEMA 'document_schema';

CREATE TABLE IF NOT EXISTS documents (
    document_id SERIAL PRIMARY KEY,
    user_name VARCHAR(255) NOT NULL,
    document_name VARCHAR(255) NOT NULL,a
    tag VARCHAR(255)[],
    file_path VARCHAR(512) NOT NULL,
    file_size BIGINT,
    file_type VARCHAR(100),
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    UNIQUE (user_name, document_name)
);

CREATE TABLE IF NOT EXISTS document_tags (
  document_id BIGINT NOT NULL,
  tag VARCHAR(255) NOT NULL,
  CONSTRAINT fk_document FOREIGN KEY (document_id) REFERENCES documents(document_id),
  PRIMARY KEY (document_id, tag)
);

GRANT ALL PRIVILEGES ON SCHEMA document_schema TO admin;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA document_schema TO admin;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA document_tags TO admin;