DROP TABLE IF EXISTS stats CASCADE;

CREATE TABLE IF NOT EXISTS stats
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    app       text        NOT NULL,
    uri       text        NOT NULL,
    ip        varchar(30) NOT NULL,
    timestamp TIMESTAMP WITHOUT TIME ZONE

);