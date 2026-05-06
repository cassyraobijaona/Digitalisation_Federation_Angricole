-- =============================================
-- 1. MEMBER
-- =============================================
CREATE TABLE member (
                        id VARCHAR(50) PRIMARY KEY,
                        first_name VARCHAR(100) NOT NULL,
                        last_name VARCHAR(100) NOT NULL,
                        birth_date DATE NOT NULL,
                        address TEXT,
                        profession VARCHAR(100),
                        phone_number VARCHAR(20),
                        email VARCHAR(150) UNIQUE,
                        occupation occupation_enum NOT NULL,
                        adhesion_date DATE DEFAULT CURRENT_DATE,
                        gender gender_enum NOT NULL
);


-- =============================================
-- 2. COLLECTIVITY
-- =============================================
CREATE TABLE collectivity (
                              id VARCHAR(50) PRIMARY KEY,
                              name VARCHAR(255) UNIQUE,
                              number INTEGER UNIQUE,
                              location VARCHAR(100) NOT NULL,
                              specialization VARCHAR(100),
                              federation_approval BOOLEAN NOT NULL DEFAULT FALSE
);


-- =============================================
-- 3. COLLECTIVITY_MEMBER
-- =============================================
CREATE TABLE collectivity_member (
                                     collectivity_id VARCHAR(50) NOT NULL,
                                     member_id VARCHAR(50) NOT NULL,
                                     PRIMARY KEY (collectivity_id, member_id),
                                     FOREIGN KEY (collectivity_id) REFERENCES collectivity(id) ON DELETE CASCADE,
                                     FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE
);


-- =============================================
-- 4. COLLECTIVITY_STRUCTURE
-- =============================================
CREATE TABLE collectivity_structure (
                                        id SERIAL PRIMARY KEY,
                                        collectivity_id VARCHAR(50) UNIQUE,
                                        president_id VARCHAR(50),
                                        vice_president_id VARCHAR(50),
                                        treasurer_id VARCHAR(50),
                                        secretary_id VARCHAR(50),
                                        FOREIGN KEY (collectivity_id) REFERENCES collectivity(id) ON DELETE CASCADE,
                                        FOREIGN KEY (president_id) REFERENCES member(id) ON DELETE SET NULL,
                                        FOREIGN KEY (vice_president_id) REFERENCES member(id) ON DELETE SET NULL,
                                        FOREIGN KEY (treasurer_id) REFERENCES member(id) ON DELETE SET NULL,
                                        FOREIGN KEY (secretary_id) REFERENCES member(id) ON DELETE SET NULL
);


-- =============================================
-- 5. MEMBERSHIP_FEE
-- =============================================
CREATE TABLE membership_fee (
                                id VARCHAR(50) PRIMARY KEY,
                                collectivity_id VARCHAR(50),
                                label VARCHAR(150),
                                amount NUMERIC,
                                frequency frequency_enum,
                                eligible_from DATE,
                                status activity_status_enum,        -- supposition logique
                                FOREIGN KEY (collectivity_id) REFERENCES collectivity(id) ON DELETE CASCADE
);


-- =============================================
-- 6. MEMBER_PAYMENT
-- =============================================
CREATE TABLE member_payment (
                                id VARCHAR(25) PRIMARY KEY,           -- ou SERIAL si tu préfères
                                member_id VARCHAR(50),
                                membership_fee_id VARCHAR(50),
                                account_id VARCHAR(50),
                                amount NUMERIC,
                                payment_mode payment_mode_enum,
                                creation_date DATE,
                                FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
                                FOREIGN KEY (membership_fee_id) REFERENCES membership_fee(id) ON DELETE SET NULL
);


-- =============================================
-- 7. ROLE_ASSIGNMENT (comme tu l'as demandé)
-- =============================================
CREATE TABLE role_assignment (
                                 id SERIAL PRIMARY KEY,
                                 collectivity_id VARCHAR(50) NOT NULL,
                                 member_id VARCHAR(50) NOT NULL,
                                 role occupation_enum NOT NULL,           -- comme tu as indiqué
                                 year INTEGER NOT NULL,
                                 UNIQUE (collectivity_id, role, year),
                                 FOREIGN KEY (collectivity_id) REFERENCES collectivity(id) ON DELETE CASCADE,
                                 FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE
);


-- =============================================
-- 8. FINANCIAL_ACCOUNT
-- =============================================
CREATE TABLE financial_account (
                                   id VARCHAR(50) PRIMARY KEY,
                                   owner_type owner_type_enum,
                                   collectivity_id VARCHAR(50),
                                   account_type account_type_enum,
                                   amount NUMERIC DEFAULT 0.00,
                                   FOREIGN KEY (collectivity_id) REFERENCES collectivity(id) ON DELETE CASCADE
);


-- =============================================
-- 9. BANK_ACCOUNT
-- =============================================
CREATE TABLE bank_account (
                              id VARCHAR(50) PRIMARY KEY,
                              holder_name VARCHAR(150),
                              bank_name bank_enum,
                              bank_code INTEGER,
                              bank_branch_code INTEGER,
                              bank_account_number BIGINT,
                              bank_account_key INTEGER
);


-- =============================================
-- 10. MOBILE_ACCOUNT
-- =============================================
CREATE TABLE mobile_account (
                                id VARCHAR(50) PRIMARY KEY,
                                holder_name VARCHAR(150),
                                mobile_service mobile_service_enum,
                                mobile_number VARCHAR(20)
);


-- =============================================
-- 11. CASH_ACCOUNT
-- =============================================
CREATE TABLE cash_account (
                              id VARCHAR(50) PRIMARY KEY
    -- Tu peux ajouter d'autres colonnes plus tard si besoin
);


-- =============================================
-- 12. COLLECTIVITY_TRANSACTION
-- =============================================
CREATE TABLE collectivity_transaction (
                                          id VARCHAR(50) PRIMARY KEY,
                                          collectivity_id VARCHAR(50),
                                          member_id VARCHAR(50),
                                          account_id VARCHAR(50),
                                          amount NUMERIC,
                                          payment_mode payment_mode_enum,
                                          creation_date DATE,
                                          FOREIGN KEY (collectivity_id) REFERENCES collectivity(id) ON DELETE CASCADE,
                                          FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE SET NULL,
                                          FOREIGN KEY (account_id) REFERENCES financial_account(id) ON DELETE SET NULL
);


-- =============================================
-- 13. REFEREE
-- =============================================
CREATE TABLE referee (
                         member_id VARCHAR(50) NOT NULL,
                         referee_id VARCHAR(50) NOT NULL,
                         relation VARCHAR(50) NOT NULL,
                         PRIMARY KEY (member_id, referee_id),
                         FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
                         FOREIGN KEY (referee_id) REFERENCES member(id) ON DELETE CASCADE
);


-- Insertion des Collectivités
INSERT INTO collectivity
(id, number, name, location, specialization, federation_approval)
VALUES
    ('col-1', 1, 'Mpanorina',       'Ambatondrazaka', 'Riziculture',   TRUE),
    ('col-2', 2, 'Dobo voalohany',  'Ambatondrazaka', 'Pisciculture',  TRUE),
    ('col-3', 3, 'Tantely mamy',    'Brickaville',   'Apiculture',    TRUE)
    ON CONFLICT (id) DO NOTHING;        -- Sécurité si tu relances le script

INSERT INTO member
(id, first_name, last_name, birth_date, gender, address, profession, phone_number, email, occupation, adhesion_date)
VALUES
    ('C1-M1', 'Prénom membre 1', 'Nom membre 1', '1980-02-01', 'M', 'Lot II V M Ambatondrazaka', 'Riziculteur', '0341234567', 'member.1@ed-agri.mg', 'PRESIDENT', '2026-04-23'),

    ('C1-M2', 'Prénom membre 2', 'Nom membre 2', '1982-03-05', 'M', 'Lot II F Ambatondrazaka', 'Agriculteur', '0321234567', 'member.2@ed-agri.mg', 'VICE_PRESIDENT', '2026-04-23'),

    ('C1-M3', 'Prénom membre 3', 'Nom membre 3', '1992-10-03', 'M', 'Lot II J Ambatondrazaka', 'Collecteur', '0331234567', 'member.3@ed-agri.mg', 'SECRETARY', '2026-04-23'),

    ('C1-M4', 'Prénom membre 4', 'Nom membre 4', '1988-02-22', 'F', 'Lot A K 50 Ambatondrazaka', 'Distributeur', '0381234567', 'member.4@ed-agri.mg', 'TREASURER', '2026-04-23'),

    ('C1-M5', 'Prénom membre 5', 'Nom membre 5', '1999-01-21', 'M', 'Lot UV 80 Ambatondrazaka', 'Riziculteur', '0373434567', 'member.5@ed-agri.mg', 'SENIOR', '2026-04-23'),

    ('C1-M6', 'Prénom membre 6', 'Nom membre 6', '1998-02-22', 'F', 'Lot UV 6 Ambatondrazaka', 'Riziculteur', '0372234567', 'member.6@ed-agri.mg', 'SENIOR', '2026-04-23'),

    ('C1-M7', 'Prénom membre 7', 'Nom membre 7', '1998-01-31', 'M', 'Lot UV 7 Ambatondrazaka', 'Riziculteur', '0374234567', 'member.7@ed-agri.mg', 'SENIOR', '2026-04-23'),

    ('C1-M8', 'Prénom membre 8', 'Nom membre 6', '1975-08-20', 'M', 'Lot UV 8 Ambatondrazaka', 'Riziculteur', '0370234567', 'member.8@ed-agri.mg', 'SENIOR', '2026-04-23')
    ON CONFLICT (id) DO NOTHING;

INSERT INTO collectivity_member (collectivity_id, member_id)
VALUES
    ('col-1', 'C1-M1'),
    ('col-1', 'C1-M2'),
    ('col-1', 'C1-M3'),
    ('col-1', 'C1-M4'),
    ('col-1', 'C1-M5'),
    ('col-1', 'C1-M6'),
    ('col-1', 'C1-M7'),
    ('col-1', 'C1-M8')
    ON CONFLICT DO NOTHING;

INSERT INTO referee (member_id, referee_id, relation)
VALUES
    -- Membres référents pour C1-M3
    ('C1-M3', 'C1-M1', 'famille'),
    ('C1-M3', 'C1-M2', 'famille'),

    -- Membres référents pour C1-M4
    ('C1-M4', 'C1-M1', 'famille'),
    ('C1-M4', 'C1-M2', 'famille'),

    -- Membres référents pour C1-M5
    ('C1-M5', 'C1-M1', 'famille'),
    ('C1-M5', 'C1-M2', 'famille'),

    -- Membres référents pour C1-M6
    ('C1-M6', 'C1-M1', 'famille'),
    ('C1-M6', 'C1-M2', 'famille'),

    -- Membres référents pour C1-M7
    ('C1-M7', 'C1-M1', 'famille'),
    ('C1-M7', 'C1-M2', 'famille'),

    -- Membres référents pour C1-M8
    ('C1-M8', 'C1-M6', 'famille'),
    ('C1-M8', 'C1-M7', 'famille')
    ON CONFLICT DO NOTHING;
-- Supprimer la contrainte UNIQUE sur email
ALTER TABLE member DROP CONSTRAINT member_email_key;
INSERT INTO member
(id, first_name, last_name, birth_date, gender, address, profession, phone_number, email, occupation, adhesion_date)
VALUES
    ('C2-M1', 'Prénom membre 1', 'Nom membre 1', '1980-02-01', 'M', 'Lot II V M Ambatondrazaka', 'Riziculteur', '0341234567', 'member.1@ed-agri.mg', 'SENIOR', '2026-04-23'),
    ('C2-M2', 'Prénom membre 2', 'Nom membre 2', '1982-03-05', 'M', 'Lot II F Ambatondrazaka', 'Agriculteur', '0321234567', 'member.2@ed-agri.mg', 'SENIOR', '2026-04-23'),
    ('C2-M3', 'Prénom membre 3', 'Nom membre 3', '1992-10-03', 'M', 'Lot II J Ambatondrazaka', 'Collecteur', '0331234567', 'member.3@ed-agri.mg', 'SENIOR', '2026-04-23'),
    ('C2-M4', 'Prénom membre 4', 'Nom membre 4', '1988-02-22', 'F', 'Lot A K 50 Ambatondrazaka', 'Distributeur', '0381234567', 'member.4@ed-agri.mg', 'SENIOR', '2026-04-23'),
    ('C2-M5', 'Prénom membre 5', 'Nom membre 5', '1999-01-21', 'M', 'Lot UV 80 Ambatondrazaka', 'Riziculteur', '0373434567', 'member.5@ed-agri.mg', 'PRESIDENT', '2026-04-23'),
    ('C2-M6', 'Prénom membre 6', 'Nom membre 6', '1998-02-22', 'F', 'Lot UV 6 Ambatondrazaka', 'Riziculteur', '0372234567', 'member.6@ed-agri.mg', 'VICE_PRESIDENT', '2026-04-23'),
    ('C2-M7', 'Prénom membre 7', 'Nom membre 7', '1998-01-31', 'M', 'Lot UV 7 Ambatondrazaka', 'Riziculteur', '0374234567', 'member.7@ed-agri.mg', 'SECRETARY', '2026-04-23'),
    ('C2-M8', 'Prénom membre 8', 'Nom membre 6', '1975-08-20', 'M', 'Lot UV 8 Ambatondrazaka', 'Riziculteur', '0370234567', 'member.8@ed-agri.mg', 'TREASURER', '2026-04-23')
    ON CONFLICT (id) DO NOTHING;

-- 2. Relier tous les membres à la collectivité 2
INSERT INTO collectivity_member (collectivity_id, member_id)
VALUES
    ('col-2', 'C2-M1'),('col-2', 'C2-M2'),('col-2', 'C2-M3'),('col-2', 'C2-M4'),
    ('col-2', 'C2-M5'),('col-2', 'C2-M6'),('col-2', 'C2-M7'),('col-2', 'C2-M8')
    ON CONFLICT DO NOTHING;
-- 3. Insertion des référents (relation = famille)
INSERT INTO referee (member_id, referee_id, relation)
VALUES
    ('C2-M3', 'C2-M1', 'famille'),
    ('C2-M3', 'C2-M2', 'famille'),
    ('C2-M4', 'C2-M1', 'famille'),
    ('C2-M4', 'C2-M2', 'famille'),
    ('C2-M5', 'C2-M1', 'famille'),
    ('C2-M5', 'C2-M2', 'famille'),
    ('C2-M6', 'C2-M1', 'famille'),
    ('C2-M6', 'C2-M2', 'famille'),
    ('C2-M7', 'C2-M1', 'famille'),
    ('C2-M7', 'C2-M2', 'famille'),
    ('C2-M8', 'C2-M6', 'famille'),
    ('C2-M8', 'C2-M7', 'famille')
    ON CONFLICT DO NOTHING;
INSERT INTO member
(id, first_name, last_name, birth_date, gender, address, profession, phone_number, email, occupation, adhesion_date)
VALUES
    ('C3-M1', 'Prénom membre 9',  'Nom membre 9',  '1988-01-02', 'M', 'Lot 33 J Antisrabe', 'Apiculteur', '034034567', 'member.9@fed-agri.mg',   'PRESIDENT',     '2026-04-23'),
    ('C3-M2', 'Prénom membre 10', 'Nom membre 10', '1982-03-05', 'M', 'Lot 2 J Antisrabe',  'Agriculteur', '0338634567','member.10@fed-agri.mg',  'VICE_PRESIDENT','2026-04-23'),
    ('C3-M3', 'Prénom membre 11', 'Nom membre 11', '1992-03-12', 'M', 'Lot 8 KM Antisrabe', 'Collecteur',  '0338234567','member.11@fed-agri.mg',  'SECRETARY',     '2026-04-23'),
    ('C3-M4', 'Prénom membre 12', 'Nom membre 12', '1988-05-10', 'F', 'Lot A K 50 Antisrabe','Distributeur','0338234567','member.12@fed-agri.mg',  'TREASURER',     '2026-04-23'),
    ('C3-M5', 'Prénom membre 13', 'Nom membre 13', '1999-08-11', 'M', 'Lot UV 80 Antisrabe', 'Apiculteur',  '0373365567','member.13@fed-agri.mg',  'SENIOR',        '2026-04-23'),
    ('C3-M6', 'Prénom membre 14', 'Nom membre 14', '1998-08-09', 'F', 'Lot UV 6 Antisrabe',  'Apiculteur',  '0378234567','member.14@fed-agri.mg',  'SENIOR',        '2026-04-23'),
    ('C3-M7', 'Prénom membre 15', 'Nom membre 15', '1998-01-13', 'M', 'Lot UV 7 Antisrabe',  'Apiculteur',  '0374914567','member.15@fed-agri.mg',  'SENIOR',        '2026-04-23'),
    ('C3-M8', 'Prénom membre 16', 'Nom membre 16', '1975-08-02', 'M', 'Lot UV 8 Antisrabe',  'Apiculteur',  '0370634567','member.16@fed-agri.mg',  'SENIOR',        '2026-04-23')
    ON CONFLICT (id) DO NOTHING;
INSERT INTO collectivity_member (collectivity_id, member_id)
VALUES
    ('col-3', 'C3-M1'),('col-3', 'C3-M2'),('col-3', 'C3-M3'),('col-3', 'C3-M4'),
    ('col-3', 'C3-M5'),('col-3', 'C3-M6'),('col-3', 'C3-M7'),('col-3', 'C3-M8')
    ON CONFLICT DO NOTHING;
INSERT INTO referee (member_id, referee_id, relation)
VALUES
    -- C3-M3
    ('C3-M3', 'C3-M1', 'famille'),
    ('C3-M3', 'C3-M2', 'famille'),

    -- C3-M4
    ('C3-M4', 'C3-M1', 'famille'),
    ('C3-M4', 'C3-M2', 'famille'),

    -- C3-M5
    ('C3-M5', 'C3-M1', 'famille'),
    ('C3-M5', 'C3-M2', 'famille'),

    -- C3-M6
    ('C3-M6', 'C3-M1', 'famille'),
    ('C3-M6', 'C3-M2', 'famille'),

    -- C3-M7
    ('C3-M7', 'C3-M1', 'famille'),
    ('C3-M7', 'C3-M2', 'famille'),

    -- C3-M8
    ('C3-M8', 'C3-M1', 'famille'),
    ('C3-M8', 'C3-M2', 'famille')
    ON CONFLICT DO NOTHING;

INSERT INTO membership_fee
(id, collectivity_id, label, status, frequency, eligible_from, amount)
VALUES
    -- Collectivité 1
    ('cot-1', 'col-1', 'Cotisation annuelle', 'ACTIVE', 'ANNUALLY', '2026-01-01', 100000),

    -- Collectivité 2
    ('cot-2', 'col-2', 'Cotisation annuelle', 'ACTIVE', 'ANNUALLY', '2026-01-01', 100000),

    -- Collectivité 3
    ('cot-3', 'col-3', 'Cotisation annuelle', 'ACTIVE', 'ANNUALLY', '2026-01-01', 50000)
    ON CONFLICT (id) DO NOTHING;
INSERT INTO financial_account
(id, collectivity_id, owner_type, account_type, amount)
VALUES
    -- Collectivité 1
    ('C1-A-CASH',     'col-1', 'COLLECTIVITY', 'CASH',          0.00),
    ('C1-A-MOBILE-1', 'col-1', 'COLLECTIVITY', 'MOBILE_BANKING', 0.00),

    -- Collectivité 2
    ('C2-A-CASH',     'col-2', 'COLLECTIVITY', 'CASH',          0.00),
    ('C2-A-MOBILE-1', 'col-2', 'COLLECTIVITY', 'MOBILE_BANKING', 0.00),

    -- Collectivité 3
    ('C3-A-CASH',     'col-3', 'COLLECTIVITY', 'CASH',          0.00)
    ON CONFLICT (id) DO NOTHING;
INSERT INTO cash_account (id)
VALUES
    ('C1-A-CASH'),
    ('C2-A-CASH'),
    ('C3-A-CASH')
    ON CONFLICT (id) DO NOTHING;
INSERT INTO mobile_account
(id, holder_name, mobile_service, mobile_number)
VALUES
    -- Collectivité 1
    ('C1-A-MOBILE-1', 'Mpanorina',    'ORANGE_MONEY', '037049612'),

    -- Collectivité 2
    ('C2-A-MOBILE-1', 'Dobo voalohany','ORANGE_MONEY', '0320489612')
    ON CONFLICT (id) DO NOTHING;
-- ========================
-- PAIEMENTS MEMBRES (member_payment)
-- ========================

INSERT INTO member_payment
(id, member_id, membership_fee_id, account_id, amount, payment_mode, creation_date)
VALUES
    -- Collectivité 1
    ('pay-c1-m1', 'C1-M1', 'cot-1', 'C1-A-CASH', 100000, 'CASH', '2026-01-01'),
    ('pay-c1-m2', 'C1-M2', 'cot-1', 'C1-A-CASH', 100000, 'CASH', '2026-01-01'),
    ('pay-c1-m3', 'C1-M3', 'cot-1', 'C1-A-CASH', 100000, 'CASH', '2026-01-01'),
    ('pay-c1-m4', 'C1-M4', 'cot-1', 'C1-A-CASH', 100000, 'CASH', '2026-01-01'),
    ('pay-c1-m5', 'C1-M5', 'cot-1', 'C1-A-CASH', 100000, 'CASH', '2026-01-01'),
    ('pay-c1-m6', 'C1-M6', 'cot-1', 'C1-A-CASH', 100000, 'CASH', '2026-01-01'),
    ('pay-c1-m7', 'C1-M7', 'cot-1', 'C1-A-CASH',  60000, 'CASH', '2026-01-01'),
    ('pay-c1-m8', 'C1-M8', 'cot-1', 'C1-A-CASH',  90000, 'CASH', '2026-01-01'),

    -- Collectivité 2
    ('pay-c2-m1', 'C2-M1', 'cot-2', 'C2-A-CASH',     60000, 'CASH',         '2026-01-01'),
    ('pay-c2-m2', 'C2-M2', 'cot-2', 'C2-A-CASH',     90000, 'CASH',         '2026-01-01'),
    ('pay-c2-m3', 'C2-M3', 'cot-2', 'C2-A-CASH',    100000, 'CASH',         '2026-01-01'),
    ('pay-c2-m4', 'C2-M4', 'cot-2', 'C2-A-CASH',    100000, 'CASH',         '2026-01-01'),
    ('pay-c2-m5', 'C2-M5', 'cot-2', 'C2-A-CASH',    100000, 'CASH',         '2026-01-01'),
    ('pay-c2-m6', 'C2-M6', 'cot-2', 'C2-A-CASH',    100000, 'CASH',         '2026-01-01'),
    ('pay-c2-m7', 'C2-M7', 'cot-2', 'C2-A-MOBILE-1', 40000, 'MOBILE_BANKING','2026-01-01'),
    ('pay-c2-m8', 'C2-M8', 'cot-2', 'C2-A-MOBILE-1', 60000, 'MOBILE_BANKING','2026-01-01')
    ON CONFLICT (id) DO NOTHING;

-- ========================
-- TRANSACTIONS COLLECTIVITÉ
-- ========================

INSERT INTO collectivity_transaction
(id, collectivity_id, member_id, account_id, amount, payment_mode, creation_date)
VALUES
    -- Collectivité 1
    ('trans-c1-m1', 'col-1', 'C1-M1', 'C1-A-CASH', 100000, 'CASH', '2026-01-01'),
    ('trans-c1-m2', 'col-1', 'C1-M2', 'C1-A-CASH', 100000, 'CASH', '2026-01-01'),
    ('trans-c1-m3', 'col-1', 'C1-M3', 'C1-A-CASH', 100000, 'CASH', '2026-01-01'),
    ('trans-c1-m4', 'col-1', 'C1-M4', 'C1-A-CASH', 100000, 'CASH', '2026-01-01'),
    ('trans-c1-m5', 'col-1', 'C1-M5', 'C1-A-CASH', 100000, 'CASH', '2026-01-01'),
    ('trans-c1-m6', 'col-1', 'C1-M6', 'C1-A-CASH', 100000, 'CASH', '2026-01-01'),
    ('trans-c1-m7', 'col-1', 'C1-M7', 'C1-A-CASH',  60000, 'CASH', '2026-01-01'),
    ('trans-c1-m8', 'col-1', 'C1-M8', 'C1-A-CASH',  90000, 'CASH', '2026-01-01'),

    -- Collectivité 2
    ('trans-c2-m1', 'col-2', 'C2-M1', 'C2-A-CASH',     60000, 'CASH',         '2026-01-01'),
    ('trans-c2-m2', 'col-2', 'C2-M2', 'C2-A-CASH',     90000, 'CASH',         '2026-01-01'),
    ('trans-c2-m3', 'col-2', 'C2-M3', 'C2-A-CASH',    100000, 'CASH',         '2026-01-01'),
    ('trans-c2-m4', 'col-2', 'C2-M4', 'C2-A-CASH',    100000, 'CASH',         '2026-01-01'),
    ('trans-c2-m5', 'col-2', 'C2-M5', 'C2-A-CASH',    100000, 'CASH',         '2026-01-01'),
    ('trans-c2-m6', 'col-2', 'C2-M6', 'C2-A-CASH',    100000, 'CASH',         '2026-01-01'),
    ('trans-c2-m7', 'col-2', 'C2-M7', 'C2-A-MOBILE-1', 40000, 'MOBILE_BANKING','2026-01-01'),
    ('trans-c2-m8', 'col-2', 'C2-M8', 'C2-A-MOBILE-1', 60000, 'MOBILE_BANKING','2026-01-01')
    ON CONFLICT (id) DO NOTHING;

