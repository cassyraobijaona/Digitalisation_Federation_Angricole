CREATE TYPE gender_enum AS ENUM (
    'M',
    'F'
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
CREATE TYPE account_type AS ENUM (
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
                               id            VARCHAR(50)          NOT NULL,
                               first_name    VARCHAR(100)         NOT NULL,
                               last_name     VARCHAR(100)         NOT NULL,
                               birth_date    DATE                 NOT NULL,
                               gender        gender_enum          NOT NULL,
                               address       TEXT,
                               profession    VARCHAR(100),
                               phone_number  VARCHAR(20),
                               email         VARCHAR(150)         UNIQUE,
                               occupation    occupation_enum      NOT NULL,
                               adhesion_date DATE                 DEFAULT CURRENT_DATE,

                               CONSTRAINT member_pkey PRIMARY KEY (id)
);
CREATE TABLE collectivity (
                                     id                  VARCHAR(50)         NOT NULL,
                                     number              INTEGER             UNIQUE,
                                     name                VARCHAR(255)        UNIQUE,
                                     location            VARCHAR(100)        NOT NULL,
                                     specialization      VARCHAR(100),
                                     federation_approval BOOLEAN             NOT NULL DEFAULT false,

                                     CONSTRAINT collectivity_pkey PRIMARY KEY (id)
);
INSERT INTO collectivity (id, number, name, location, specialization, federation_approval) VALUES
                                                                                                      ('col-1', 1, 'Mpanorina',      'Ambatondrazaka', 'Riziculture', false),
                                                                                                      ('col-2', 2, 'Dobo voalohany', 'Ambatondrazaka', 'Pisciculture', false),
                                                                                                      ('col-3', 3, 'Tantely mamy',   'Brickaville',    'Apiculture',  false);
CREATE TABLE collectivity_member (
                                     collectivity_id VARCHAR(50),
                                     member_id VARCHAR(50),

                                     PRIMARY KEY (collectivity_id, member_id),

                                     FOREIGN KEY (collectivity_id) REFERENCES collectivity(id),
                                     FOREIGN KEY (member_id) REFERENCES member(id)
);
CREATE TABLE collectivity_structure (
                                        id SERIAL PRIMARY KEY,
                                        collectivity_id VARCHAR(50) UNIQUE,
                                        president_id VARCHAR(50),
                                        vice_president_id VARCHAR(50),
                                        treasurer_id VARCHAR(50),
                                        secretary_id VARCHAR(50),

                                        FOREIGN KEY (collectivity_id) REFERENCES collectivity(id),
                                        FOREIGN KEY (president_id) REFERENCES member(id),
                                        FOREIGN KEY (vice_president_id) REFERENCES member(id),
                                        FOREIGN KEY (treasurer_id) REFERENCES member(id),
                                        FOREIGN KEY (secretary_id) REFERENCES member(id)
);
CREATE TABLE referee (
                                member_id   VARCHAR(50)  NOT NULL,
                                referee_id  VARCHAR(50)  NOT NULL,
                                relation    VARCHAR(50)  NOT NULL,
                                CONSTRAINT referee_pkey PRIMARY KEY (member_id, referee_id),

                                CONSTRAINT referee_member_id_fkey
                                    FOREIGN KEY (member_id) REFERENCES public.member(id),

                                CONSTRAINT referee_referee_id_fkey
                                    FOREIGN KEY (referee_id) REFERENCES public.member(id)
);.
-- =============================================
-- COLLECTIVITÉ 1 (col-1)
-- =============================================
INSERT INTO public.member (id, last_name, first_name, birth_date, gender, address, profession, phone_number, email, occupation, adhesion_date) VALUES
('C1-M1', 'Nom membre 1', 'Prénom membre 1', '1980-02-01', 'M', 'Lot II V M Ambato.', 'Riziculteur',  '0341234567', 'member.1@fed-agri.mg', 'PRESIDENT',      '2025-01-01'),
('C1-M2', 'Nom membre 2', 'Prénom membre 2', '1982-03-05', 'M', 'Lot II F Ambato.',   'Agriculteur',  '0321234567', 'member.2@fed-agri.mg', 'VICE_PRESIDENT', '2025-01-01'),
('C1-M3', 'Nom membre 3', 'Prénom membre 3', '1992-03-10', 'M', 'Lot II J Ambato.',   'Collecteur',   '0331234567', 'member.3@fed-agrimg',  'SECRETARY',      '2025-01-01'),
('C1-M4', 'Nom membre 4', 'Prénom membre 4', '1988-05-22', 'F', 'Lot A K 50 Ambato.','Distributeur', '0381234567', 'member.4@fed-agri.mg', 'TREASURER',      '2025-01-01'),
('C1-M5', 'Nom membre 5', 'Prénom membre 5', '1999-08-21', 'M', 'Lot UV 80 Ambato.', 'Riziculteur',  '0373434567', 'member.5@fed-agri.mg', 'SENIOR',         '2025-01-01'),
('C1-M6', 'Nom membre 6', 'Prénom membre 6', '1998-08-22', 'F', 'Lot UV 6 Ambato.',  'Riziculteur',  '0372234567', 'member.6@fed-agri.mg', 'SENIOR',         '2025-01-01'),
('C1-M7', 'Nom membre 7', 'Prénom membre 7', '1998-01-31', 'M', 'Lot UV 7 Ambato.',  'Riziculteur',  '0374234567', 'member.7@fed-agri.mg', 'SENIOR',         '2025-01-01'),
('C1-M8', 'Nom membre 8', 'Prénom membre 6', '1975-08-20', 'M', 'Lot UV 8 Ambato.',  'Riziculteur',  '0370234567', 'member.8@fed-agri.mg', 'SENIOR',         '2025-01-01');


-- =============================================
-- COLLECTIVITÉ 2 (col-2)
-- =============================================
INSERT INTO public.member (id, last_name, first_name, birth_date, gender, address, profession, phone_number, email, occupation, adhesion_date) VALUES
                                                                                                                                                   ('C2-M1', 'Nom membre 1', 'Prénom membre 1', '1980-02-01', 'M', 'Lot II V M Ambato.', 'Riziculteur',  '0341234567', 'member.c2.1@fed-agri.mg', 'SENIOR',         '2025-01-01'),
                                                                                                                                                   ('C2-M2', 'Nom membre 2', 'Prénom membre 2', '1982-03-05', 'M', 'Lot II F Ambato.',   'Agriculteur',  '0321234567', 'member.c2.2@fed-agri.mg', 'SENIOR',         '2025-01-01'),
                                                                                                                                                   ('C2-M3', 'Nom membre 3', 'Prénom membre 3', '1992-03-10', 'M', 'Lot II J Ambato.',   'Collecteur',   '0331234567', 'member.c2.3@fed-agrimg',  'SENIOR',         '2025-01-01'),
                                                                                                                                                   ('C2-M4', 'Nom membre 4', 'Prénom membre 4', '1988-05-22', 'F', 'Lot A K 50 Ambato.','Distributeur', '0381234567', 'member.c2.4@fed-agri.mg', 'SENIOR',         '2025-01-01'),
                                                                                                                                                   ('C2-M5', 'Nom membre 5', 'Prénom membre 5', '1999-08-21', 'M', 'Lot UV 80 Ambato.', 'Riziculteur',  '0373434567', 'member.c2.5@fed-agri.mg', 'PRESIDENT',      '2025-01-01'),
                                                                                                                                                   ('C2-M6', 'Nom membre 6', 'Prénom membre 6', '1998-08-22', 'F', 'Lot UV 6 Ambato.',  'Riziculteur',  '0372234567', 'member.c2.6@fed-agri.mg', 'VICE_PRESIDENT', '2025-01-01'),
                                                                                                                                                   ('C2-M7', 'Nom membre 7', 'Prénom membre 7', '1998-01-31', 'M', 'Lot UV 7 Ambato.',  'Riziculteur',  '0374234567', 'member.c2.7@fed-agri.mg', 'SECRETARY',      '2025-01-01'),
                                                                                                                                                   ('C2-M8', 'Nom membre 8', 'Prénom membre 6', '1975-08-20', 'M', 'Lot UV 8 Ambato.',  'Riziculteur',  '0370234567', 'member.c2.8@fed-agri.mg', 'TREASURER',      '2025-01-01');


-- =============================================
-- COLLECTIVITÉ 3 (col-3)
-- =============================================
INSERT INTO public.member (id, last_name, first_name, birth_date, gender, address, profession, phone_number, email, occupation, adhesion_date) VALUES
                                                                                                                                                   ('C3-M1', 'Nom membre 9',  'Prénom membre 9',  '1988-01-02', 'M', 'Lot 33 J Antsirabe',   'Apiculteur',   '034034567',  'member.9@fed-agri.mg',  'PRESIDENT',      '2025-01-01'),
                                                                                                                                                   ('C3-M2', 'Nom membre 10', 'Prénom membre 10', '1982-03-05', 'M', 'Lot 2 J Antsirabe',    'Agriculteur',  '0338634567', 'member.10@fed-agri.mg', 'VICE_PRESIDENT', '2025-01-01'),
                                                                                                                                                   ('C3-M3', 'Nom membre 11', 'Prénom membre 11', '1992-03-12', 'M', 'Lot 8 KM Antsirabe',   'Collecteur',   '0338234567', 'member.11@fed-agrimg',  'SECRETARY',      '2025-01-01'),
                                                                                                                                                   ('C3-M4', 'Nom membre 12', 'Prénom membre 12', '1988-05-10', 'F', 'Lot A K 50 Antsirabe', 'Distributeur', '0382334567', 'member.12@fed-agri.mg', 'TREASURER',      '2025-01-01'),
                                                                                                                                                   ('C3-M5', 'Nom membre 13', 'Prénom membre 13', '1999-08-11', 'M', 'Lot UV 80 Antsirabe.', 'Apiculteur',   '0373365567', 'member.13@fed-agri.mg', 'SENIOR',         '2025-01-01'),
                                                                                                                                                   ('C3-M6', 'Nom membre 14', 'Prénom membre 14', '1998-08-09', 'F', 'Lot UV 6 Antsirabe.',  'Apiculteur',   '0378234567', 'member.14@fed-agri.mg', 'SENIOR',         '2025-01-01'),
                                                                                                                                                   ('C3-M7', 'Nom membre 15', 'Prénom membre 15', '1998-01-13', 'M', 'Lot UV 7 Antsirabe',   'Apiculteur',   '0374914567', 'member.15@fed-agri.mg', 'SENIOR',         '2025-01-01'),
                                                                                                                                                   ('C3-M8', 'Nom membre 16', 'Prénom membre 16', '1975-08-02', 'M', 'Lot UV 8 Antsirabe',   'Apiculteur',   '0370634567', 'member.16@fed-agri.mg', 'SENIOR',         '2025-01-01');


-- =============================================
-- REFEREE (membres référents)
-- =============================================

-- Collectivité 1
INSERT INTO public.referee (member_id, referee_id,relation) VALUES
                                                       ('C1-M3', 'C1-M1','famille'), ('C1-M3', 'C1-M2','famille'),
                                                       ('C1-M4', 'C1-M1','famille'), ('C1-M4', 'C1-M2','famille'),
                                                       ('C1-M5', 'C1-M1','famille'), ('C1-M5', 'C1-M2','famille'),
                                                       ('C1-M6', 'C1-M1','famille'), ('C1-M6', 'C1-M2','famille'),
                                                       ('C1-M7', 'C1-M1','famille'), ('C1-M7', 'C1-M2','famille'),
                                                       ('C1-M8', 'C1-M6','famille'), ('C1-M8', 'C1-M7','famille');

-- Collectivité 2
INSERT INTO public.referee (member_id, referee_id) VALUES
                                                       ('C2-M3', 'C1-M1'), ('C2-M3', 'C1-M2'),
                                                       ('C2-M4', 'C1-M1'), ('C2-M4', 'C1-M2'),
                                                       ('C2-M5', 'C1-M1'), ('C2-M5', 'C1-M2'),
                                                       ('C2-M6', 'C1-M1'), ('C2-M6', 'C1-M2'),
                                                       ('C2-M7', 'C1-M1'), ('C2-M7', 'C1-M2'),
                                                       ('C2-M8', 'C1-M6'), ('C2-M8', 'C1-M7');

-- Collectivité 3
INSERT INTO public.referee (member_id, referee_id) VALUES
                                                       ('C3-M1', 'C1-M1'), ('C3-M1', 'C1-M2'),
                                                       ('C3-M2', 'C1-M1'), ('C3-M2', 'C1-M2'),
                                                       ('C3-M3', 'C3-M1'), ('C3-M3', 'C3-M2'),
                                                       ('C3-M4', 'C3-M1'), ('C3-M4', 'C3-M2'),
                                                       ('C3-M5', 'C3-M1'), ('C3-M5', 'C3-M2'),
                                                       ('C3-M6', 'C3-M1'), ('C3-M6', 'C3-M2'),
                                                       ('C3-M7', 'C3-M1'), ('C3-M7', 'C3-M2'),
                                                       ('C3-M8', 'C3-M1'), ('C3-M8', 'C3-M2');



CREATE TABLE membership_fee (
                                id VARCHAR(50) PRIMARY KEY,
                                collectivity_id VARCHAR(50),
                                eligible_from DATE,
                                frequency frequency_enum,
                                amount NUMERIC(12,2),
                                label VARCHAR(150),
                                status activity_status_enum,
                                FOREIGN KEY (collectivity_id) REFERENCES collectivity(id)
);
INSERT INTO public.membership_fee (id, collectivity_id, label, status, frequency, eligible_from, amount) VALUES
                                                                                                             ('cot-1', 'col-1', 'Cotisation annuelle', 'ACTIVE', 'ANNUALLY', '2026-01-01', 100000.00),
                                                                                                             ('cot-2', 'col-2', 'Cotisation annuelle', 'ACTIVE', 'ANNUALLY', '2026-01-01', 100000.00),
                                                                                                             ('cot-3', 'col-3', 'Cotisation annuelle', 'ACTIVE', 'ANNUALLY', '2026-01-01',  50000.00);
CREATE TABLE member_payment (
                                id SERIAL PRIMARY KEY,
                                member_id VARCHAR(50),
                                membership_fee_id VARCHAR(50),
                                account_id INT,
                                amount NUMERIC(12,2),
                                payment_mode payment_mode_enum,
                                creation_date DATE,
                                FOREIGN KEY (member_id) REFERENCES member(id),
                                FOREIGN KEY (membership_fee_id) REFERENCES membership_fee(id),
                                FOREIGN KEY (account_id) REFERENCES financial_account(id)
);
CREATE TYPE owner_type_enum AS ENUM ('COLLECTIVITY','FEDERATION');
CREATE TABLE financial_account (
                                   id           VARCHAR(50)       NOT NULL,
                                   owner_type   owner_type_enum,
                                   collectivity_id    VARCHAR(50)
                                    REFERENCES collectivity(id),
                                   account_type account_type_enum,
                                   amount       NUMERIC(12,2)     DEFAULT 0.00,

                                   CONSTRAINT financial_account_pkey PRIMARY KEY (id)
);

CREATE TABLE cash_account (
                              id VARCHAR(50) NOT NULL,

                              CONSTRAINT cash_account_pkey PRIMARY KEY (id),
                              CONSTRAINT cash_account_id_fkey
                                  FOREIGN KEY (id) REFERENCES financial_account(id)
);

CREATE TABLE bank_account (
                              id                  VARCHAR(50)   NOT NULL,
                              holder_name         VARCHAR(150),
                              bank_name           bank_enum,
                              bank_code           INT,
                              bank_branch_code    INT,
                              bank_account_number BIGINT,
                              bank_account_key    INT,

                              CONSTRAINT bank_account_pkey PRIMARY KEY (id),
                              CONSTRAINT bank_account_id_fkey
                                  FOREIGN KEY (id) REFERENCES financial_account(id)
);

CREATE TABLE mobile_account (
                                id             VARCHAR(50)        NOT NULL,
                                holder_name    VARCHAR(150),
                                mobile_service mobile_service_enum,
                                mobile_number  VARCHAR(20),

                                CONSTRAINT mobile_account_pkey PRIMARY KEY (id),
                                CONSTRAINT mobile_account_id_fkey
                                    FOREIGN KEY (id) REFERENCES financial_account(id)
);

-- =============================================
-- financial_account
-- =============================================
INSERT INTO public.financial_account (id, owner_type, collectivity_id, account_type, amount) VALUES
                                                                                                 ('C1-A-CASH',     'COLLECTIVITY', 'col-1', 'CASH',           0.00),
                                                                                                 ('C1-A-MOBILE-1', 'COLLECTIVITY', 'col-1', 'MOBILE_BANKING', 0.00),
                                                                                                 ('C2-A-CASH',     'COLLECTIVITY', 'col-2', 'CASH',           0.00),
                                                                                                 ('C2-A-MOBILE-1', 'COLLECTIVITY', 'col-2', 'MOBILE_BANKING', 0.00),
                                                                                                 ('C3-A-CASH',     'COLLECTIVITY', 'col-3', 'CASH',           0.00);


-- =============================================
-- cash_account
-- =============================================
INSERT INTO public.cash_account (id) VALUES
                                         ('C1-A-CASH'),
                                         ('C2-A-CASH'),
                                         ('C3-A-CASH');


-- =============================================
-- mobile_account
-- =============================================
INSERT INTO public.mobile_account (id, holder_name, mobile_service, mobile_number) VALUES
                                                                                       ('C1-A-MOBILE-1', 'Mpanorina',      'ORANGE_MONEY', '0370489612'),
                                                                                       ('C2-A-MOBILE-1', 'Dobo voalohany', 'ORANGE_MONEY', '0320489612');

-- =============================================
-- CORRECTION DE LA TABLE
-- =============================================


CREATE TABLE public.collectivity_transaction (
                                                 id               VARCHAR(50)       NOT NULL,
                                                 collectivity_id  VARCHAR(50),
                                                 member_id        VARCHAR(50),
                                                 account_id       VARCHAR(50),
                                                 amount           NUMERIC(12,2),
                                                 payment_mode     payment_mode_enum,
                                                 creation_date    DATE,

                                                 CONSTRAINT collectivity_transaction_pkey PRIMARY KEY (id),
                                                 CONSTRAINT collectivity_transaction_collectivity_id_fkey
                                                     FOREIGN KEY (collectivity_id) REFERENCES public.collectivity(id),
                                                 CONSTRAINT collectivity_transaction_member_id_fkey
                                                     FOREIGN KEY (member_id) REFERENCES public.member(id),
                                                 CONSTRAINT collectivity_transaction_account_id_fkey
                                                     FOREIGN KEY (account_id) REFERENCES public.financial_account(id)
);


-- =============================================
-- COLLECTIVITÉ 1 - Transactions
-- =============================================
INSERT INTO public.collectivity_transaction (id, collectivity_id, member_id, account_id, amount, payment_mode, creation_date) VALUES
                                                                                                                                  ('CT-C1-M1', 'col-1', 'C1-M1', 'C1-A-CASH', 100000.00, 'CASH', '2026-01-01'),
                                                                                                                                  ('CT-C1-M2', 'col-1', 'C1-M2', 'C1-A-CASH', 100000.00, 'CASH', '2026-01-01'),
                                                                                                                                  ('CT-C1-M3', 'col-1', 'C1-M3', 'C1-A-CASH', 100000.00, 'CASH', '2026-01-01'),
                                                                                                                                  ('CT-C1-M4', 'col-1', 'C1-M4', 'C1-A-CASH', 100000.00, 'CASH', '2026-01-01'),
                                                                                                                                  ('CT-C1-M5', 'col-1', 'C1-M5', 'C1-A-CASH', 100000.00, 'CASH', '2026-01-01'),
                                                                                                                                  ('CT-C1-M6', 'col-1', 'C1-M6', 'C1-A-CASH', 100000.00, 'CASH', '2026-01-01'),
                                                                                                                                  ('CT-C1-M7', 'col-1', 'C1-M7', 'C1-A-CASH',  60000.00, 'CASH', '2026-01-01'),
                                                                                                                                  ('CT-C1-M8', 'col-1', 'C1-M8', 'C1-A-CASH',  90000.00, 'CASH', '2026-01-01');


-- =============================================
-- COLLECTIVITÉ 2 - Transactions
-- =============================================
INSERT INTO public.collectivity_transaction (id, collectivity_id, member_id, account_id, amount, payment_mode, creation_date) VALUES
                                                                                                                                  ('CT-C2-M1', 'col-2', 'C2-M1', 'C2-A-CASH',    60000.00, 'CASH',           '2026-01-01'),
                                                                                                                                  ('CT-C2-M2', 'col-2', 'C2-M2', 'C2-A-CASH',    90000.00, 'CASH',           '2026-01-01'),
                                                                                                                                  ('CT-C2-M3', 'col-2', 'C2-M3', 'C2-A-CASH',   100000.00, 'CASH',           '2026-01-01'),
                                                                                                                                  ('CT-C2-M4', 'col-2', 'C2-M4', 'C2-A-CASH',   100000.00, 'CASH',           '2026-01-01'),
                                                                                                                                  ('CT-C2-M5', 'col-2', 'C2-M5', 'C2-A-CASH',   100000.00, 'CASH',           '2026-01-01'),
                                                                                                                                  ('CT-C2-M6', 'col-2', 'C2-M6', 'C2-A-CASH',   100000.00, 'CASH',           '2026-01-01'),
                                                                                                                                  ('CT-C2-M7', 'col-2', 'C2-M7', 'C2-A-MOBILE-1', 40000.00, 'MOBILE_BANKING','2026-01-01'),
                                                                                                                                  ('CT-C2-M8', 'col-2', 'C2-M8', 'C2-A-MOBILE-1', 60000.00, 'MOBILE_BANKING','2026-01-01');