CREATE TABLE candidates
(
    id SERIAL PRIMARY KEY,
    name VARCHAR NOT NULL,
    description VARCHAR NOT NULL,
    created_date TIMESTAMP,
    file_id INT REFERENCES files(id)
);