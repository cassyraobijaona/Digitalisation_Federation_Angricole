CREATE TABLE city (
                      id     SERIAL PRIMARY KEY,
                      name   VARCHAR(100) NOT NULL,
                      region VARCHAR(100)
);

CREATE TABLE federation (
                            id   SERIAL PRIMARY KEY,
                            name VARCHAR(150) NOT NULL
);

CREATE TABLE opening_authorization (
                                       id                SERIAL PRIMARY KEY,
                                       authorization_date DATE        NOT NULL,
                                       status            VARCHAR(20) NOT NULL CHECK (status IN (
                                                                                                'granted', 'refused', 'pending'
                                           ))
);

CREATE TABLE collectivity (
                              id                  SERIAL PRIMARY KEY,
                              number              VARCHAR(20)  NOT NULL UNIQUE,
                              name                VARCHAR(150) NOT NULL UNIQUE,
                              agricultural_specialty VARCHAR(100) NOT NULL,
                              authorization_id    INT          REFERENCES opening_authorization(id),
                              creation_date       DATE         NOT NULL,
                              city_id             INT          NOT NULL REFERENCES city(id),
                              federation_id       INT          NOT NULL REFERENCES federation(id)
);

CREATE TABLE person (
                        id             SERIAL PRIMARY KEY,
                        last_name      VARCHAR(100) NOT NULL,
                        first_name     VARCHAR(100) NOT NULL,
                        birth_date     DATE         NOT NULL,
                        gender         VARCHAR(10)  NOT NULL CHECK (gender IN ('MALE', 'FEMALE')),
                        address        TEXT         NOT NULL,
                        profession     VARCHAR(100) NOT NULL,
                        phone_number   VARCHAR(20)  NOT NULL,
                        email          VARCHAR(150) NOT NULL UNIQUE
);

CREATE TYPE account_type AS ENUM ('bank', 'mobile_money', 'cash');
CREATE TYPE payment_status AS ENUM ('pending', 'validated', 'rejected');
CREATE TYPE membership_payment_mode AS ENUM ('bank_transfer', 'mobile_money');
CREATE TYPE membership_status AS ENUM ('pending', 'validated', 'refused');
CREATE TYPE contribution_type AS ENUM ('monthly', 'annual', 'one_time');
CREATE TYPE contribution_payment_mode AS ENUM ('cash', 'bank', 'mobile_money');

CREATE TABLE account (
                         id              SERIAL PRIMARY KEY,
                         type            account_type  NOT NULL,
                         collectivity_id INT           REFERENCES collectivity(id),
                         federation_id   INT           REFERENCES federation(id),
                         balance         NUMERIC(12,2) DEFAULT 0,
                         CHECK (
                             (collectivity_id IS NOT NULL AND federation_id IS NULL) OR
                             (federation_id IS NOT NULL AND collectivity_id IS NULL)
                             )
);

CREATE TABLE bank_account (
                              id            SERIAL PRIMARY KEY,
                              account_id    INT         NOT NULL UNIQUE REFERENCES account(id),
                              holder        VARCHAR(150) NOT NULL,
                              bank_name     VARCHAR(50)  NOT NULL CHECK (bank_name IN (
                                                                                       'BRED','MCB','BMOI','BOA','BGFI',
                                                                                       'AFG','ACCES_BANQUE','BAOBAB','SIPEM'
                                  )),
                              bank_code     CHAR(5)  NOT NULL,
                              branch_code   CHAR(5)  NOT NULL,
                              account_number CHAR(11) NOT NULL,
                              rib_key       CHAR(2)  NOT NULL
);

CREATE TABLE mobile_money_account (
                                      id           SERIAL PRIMARY KEY,
                                      account_id   INT         NOT NULL UNIQUE REFERENCES account(id),
                                      holder       VARCHAR(150) NOT NULL,
                                      service      VARCHAR(30)  NOT NULL CHECK (service IN (
                                                                                            'Orange Money', 'Mvola', 'Airtel Money'
                                          )),
                                      phone_number VARCHAR(20)  NOT NULL UNIQUE
);

CREATE TABLE cash_register (
                               id         SERIAL PRIMARY KEY,
                               account_id INT         NOT NULL UNIQUE REFERENCES account(id),
                               holder     VARCHAR(150) NOT NULL
);

CREATE TABLE membership_payment (
                                    id                    SERIAL PRIMARY KEY,
                                    amount                INT  NOT NULL CHECK (amount = 50000),
                                    person_id             INT  NOT NULL REFERENCES person(id) ON DELETE CASCADE,
                                    receiving_account_id  INT  NOT NULL REFERENCES account(id) ON DELETE CASCADE,
                                    payment_mode          membership_payment_mode NOT NULL,
                                    transaction_reference TEXT NOT NULL UNIQUE,
                                    payment_date          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    status                payment_status NOT NULL
);

CREATE TABLE membership (
                            id               SERIAL PRIMARY KEY,
                            collectivity_id  INT NOT NULL REFERENCES collectivity(id) ON DELETE CASCADE,
                            payment_id       INT UNIQUE   REFERENCES membership_payment(id),
                            membership_date  DATE         DEFAULT CURRENT_DATE,
                            status           membership_status NOT NULL
);

CREATE TABLE member (
                        id               SERIAL PRIMARY KEY,
                        collectivity_id  INT NOT NULL REFERENCES collectivity(id) ON DELETE CASCADE,
                        membership_id    INT NOT NULL UNIQUE REFERENCES membership(id) ON DELETE CASCADE
);

CREATE TABLE sponsorship (
                             id               SERIAL PRIMARY KEY,
                             membership_id    INT         NOT NULL REFERENCES membership(id),
                             sponsor_id       INT         NOT NULL REFERENCES member(id),
                             relationship     VARCHAR(50) NOT NULL,
                             UNIQUE (membership_id, sponsor_id)
);

CREATE TABLE role (
                      id         SERIAL PRIMARY KEY,
                      name       TEXT    NOT NULL UNIQUE,
                      is_unique  BOOLEAN DEFAULT FALSE
);

CREATE TABLE mandate (
                         id               SERIAL PRIMARY KEY,
                         collectivity_id  INT REFERENCES collectivity(id),
                         federation_id    INT REFERENCES federation(id),
                         start_date       DATE NOT NULL,
                         end_date         DATE NOT NULL,
                         CHECK (
                             (collectivity_id IS NOT NULL AND federation_id IS NULL) OR
                             (collectivity_id IS NULL AND federation_id IS NOT NULL)
                             )
);

CREATE TABLE position_assignment (
                                     id           SERIAL PRIMARY KEY,
                                     member_id    INT NOT NULL REFERENCES member(id),
                                     role_id      INT NOT NULL REFERENCES role(id),
                                     mandate_id   INT NOT NULL REFERENCES mandate(id)
);

CREATE TABLE contribution (
                              id               SERIAL PRIMARY KEY,
                              collectivity_id  INT REFERENCES collectivity(id),
                              federation_id    INT REFERENCES federation(id),
                              type             contribution_type NOT NULL,
                              amount           INT  NOT NULL,
                              start_date       DATE,
                              end_date         DATE,
                              description      TEXT,
                              CHECK (
                                  (collectivity_id IS NOT NULL AND federation_id IS NULL) OR
                                  (federation_id IS NOT NULL AND collectivity_id IS NULL)
                                  )
);

CREATE TABLE contribution_payment (
                                      id                    SERIAL PRIMARY KEY,
                                      member_id             INT NOT NULL REFERENCES member(id),
                                      contribution_id       INT NOT NULL REFERENCES contribution(id),
                                      amount                INT NOT NULL,
                                      payment_mode          contribution_payment_mode NOT NULL,
                                      receiving_account_id  INT NOT NULL REFERENCES account(id),
                                      transaction_reference VARCHAR(55),
                                      payment_date          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                      status                payment_status NOT NULL
);

CREATE TABLE federation_payment (
                                    id                    SERIAL PRIMARY KEY,
                                    collectivity_id       INT NOT NULL REFERENCES collectivity(id),
                                    federation_id         INT NOT NULL REFERENCES federation(id),
                                    contribution_id       INT NOT NULL REFERENCES contribution(id),
                                    total_amount          INT NOT NULL,
                                    payment_mode          contribution_payment_mode NOT NULL,
                                    transaction_reference TEXT UNIQUE,
                                    payment_date          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    source_account_id     INT REFERENCES account(id),
                                    destination_account_id INT REFERENCES account(id)
);

CREATE TABLE activity (
                          id               SERIAL PRIMARY KEY,
                          title            VARCHAR(200) NOT NULL,
                          type             VARCHAR(30)  NOT NULL CHECK (type IN (
                                                                                 'general_assembly', 'junior_training',
                                                                                 'exceptional', 'federation'
                              )),
                          activity_date    DATE    NOT NULL,
                          mandatory        BOOLEAN NOT NULL DEFAULT TRUE,
                          target           VARCHAR(50) DEFAULT 'all',
                          collectivity_id  INT REFERENCES collectivity(id),
                          federation_id    INT REFERENCES federation(id)
);

CREATE TABLE attendance (
                            id            SERIAL PRIMARY KEY,
                            activity_id   INT     NOT NULL REFERENCES activity(id),
                            member_id     INT     NOT NULL REFERENCES member(id),
                            present       BOOLEAN NOT NULL DEFAULT FALSE,
                            excused       BOOLEAN NOT NULL DEFAULT FALSE,
                            absence_reason TEXT,
                            UNIQUE (activity_id, member_id)
);