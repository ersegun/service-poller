CREATE TABLE IF NOT EXISTS service_url (
    id varchar(36) PRIMARY KEY,
    name varchar(256) NOT NULL,
    url varchar(512) NOT NULL,
    status int NOT NULL,
    created varchar(512) NOT NULL,
    updated varchar(512) NOT NULL
);
