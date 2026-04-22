CREATE TYPE gender_enum AS ENUM (
    'MALE',
    'FEMALE'
);
CREATE TYPE member_occupation_enum AS ENUM (
    'JUNIOR',
    'SENIOR',
    'SECRETARY',
    'TREASURER',
    'VICE_PRESIDENT',
    'PRESIDENT'
);
CREATE TYPE frequency_enum AS ENUM (
    'WEEKLY',
    'MONTHLY',
    'ANNUALLY',
    'PUNCTUALLY'
);
CREATE TYPE activity_status_enum AS ENUM (
    'ACTIVE',
    'INACTIVE'
);
CREATE TYPE payment_mode_enum AS ENUM (
    'CASH',
    'MOBILE_BANKING',
    'BANK_TRANSFER'
);
CREATE TYPE bank_enum AS ENUM (
    'BRED',
    'MCB',
    'BMOI',
    'BOA',
    'BGFI',
    'AFG',
    'ACCES_BAQUE',
    'BAOBAB',
    'SIPEM'
);
CREATE TYPE mobile_service_enum AS ENUM (
    'AIRTEL_MONEY',
    'MVOLA',
    'ORANGE_MONEY'
);


CREATE TABLE member (
                        id SERIAL PRIMARY KEY,

                        first_name VARCHAR(100),
                        last_name VARCHAR(100),
                        birth_date DATE,

                        gender gender_enum,

                        address TEXT,
                        profession VARCHAR(100),
                        phone_number BIGINT,
                        email VARCHAR(150),

                        occupation member_occupation_enum
);
CREATE TABLE collectivity (
                              id SERIAL PRIMARY KEY,

                              name VARCHAR(150) UNIQUE,
                              number INT UNIQUE,
                              location VARCHAR(150),

                              federation_approval BOOLEAN NOT NULL
);
CREATE TABLE collectivity_member (
                                     collectivity_id INT,
                                     member_id INT,

                                     PRIMARY KEY (collectivity_id, member_id),

                                     FOREIGN KEY (collectivity_id) REFERENCES collectivity(id),
                                     FOREIGN KEY (member_id) REFERENCES member(id)
);
CREATE TABLE collectivity_structure (
                                        id SERIAL PRIMARY KEY,
                                        collectivity_id INT UNIQUE,

                                        president_id INT,
                                        vice_president_id INT,
                                        treasurer_id INT,
                                        secretary_id INT,

                                        FOREIGN KEY (collectivity_id) REFERENCES collectivity(id),
                                        FOREIGN KEY (president_id) REFERENCES member(id),
                                        FOREIGN KEY (vice_president_id) REFERENCES member(id),
                                        FOREIGN KEY (treasurer_id) REFERENCES member(id),
                                        FOREIGN KEY (secretary_id) REFERENCES member(id)
);
CREATE TABLE membership_fee (
                                id SERIAL PRIMARY KEY,

                                collectivity_id INT,

                                eligible_from DATE,
                                frequency frequency_enum,
                                amount NUMERIC(12,2),
                                label VARCHAR(150),

                                status activity_status_enum,

                                FOREIGN KEY (collectivity_id) REFERENCES collectivity(id)
);
CREATE TABLE member_payment (
                                id SERIAL PRIMARY KEY,

                                member_id INT,
                                membership_fee_id INT,

                                amount NUMERIC(12,2),

                                payment_mode payment_mode_enum,

                                creation_date DATE,

                                FOREIGN KEY (member_id) REFERENCES member(id),
                                FOREIGN KEY (membership_fee_id) REFERENCES membership_fee(id)
);
CREATE TABLE financial_account (
                                   id SERIAL PRIMARY KEY,

                                   owner_type VARCHAR(20), -- COLLECTIVITY / FEDERATION
                                   owner_id INT,

                                   account_type VARCHAR(20), -- CASH / BANK / MOBILE

                                   amount NUMERIC(12,2)
);
CREATE TABLE bank_account (
                              financial_account_id INT PRIMARY KEY,

                              holder_name VARCHAR(150),

                              bank_name bank_enum,

                              bank_code INT,
                              bank_branch_code INT,
                              bank_account_number BIGINT,
                              bank_account_key INT,

                              FOREIGN KEY (financial_account_id)
                                  REFERENCES financial_account(id)
);
CREATE TABLE mobile_account (
                                financial_account_id INT PRIMARY KEY,

                                holder_name VARCHAR(150),

                                mobile_service mobile_service_enum,

                                mobile_number BIGINT,

                                FOREIGN KEY (financial_account_id)
                                    REFERENCES financial_account(id)
);
CREATE TABLE collectivity_transaction (
                                          id SERIAL PRIMARY KEY,

                                          collectivity_id INT,
                                          member_id INT,
                                          financial_account_id INT,

                                          amount NUMERIC(12,2),

                                          payment_mode payment_mode_enum,

                                          creation_date DATE,

                                          FOREIGN KEY (collectivity_id) REFERENCES collectivity(id),
                                          FOREIGN KEY (member_id) REFERENCES member(id),
                                          FOREIGN KEY (financial_account_id) REFERENCES financial_account(id)
);