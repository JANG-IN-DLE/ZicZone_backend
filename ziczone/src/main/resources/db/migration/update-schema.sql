CREATE TABLE company_user
(
    company_id             BIGINT       NOT NULL,
    company_num            VARCHAR(100) NOT NULL,
    company_addr           VARCHAR(100) NOT NULL,
    company_year           date         NOT NULL,
    company_logo_url       TEXT NULL,
    company_logo_uuid      TEXT NULL,
    company_logo_file_name TEXT NULL,
    company_ceo            VARCHAR(255) NOT NULL,
    user_id                BIGINT NULL,
    CONSTRAINT pk_companyuser PRIMARY KEY (company_id)
);

ALTER TABLE company_user
    ADD CONSTRAINT uc_companyuser_user UNIQUE (user_id);

ALTER TABLE company_user
    ADD CONSTRAINT FK_COMPANYUSER_ON_USER FOREIGN KEY (user_id) REFERENCES user (user_id);