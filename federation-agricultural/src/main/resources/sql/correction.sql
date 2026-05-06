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
------DONNEE 6MAI -------
-- =============================================
-- AJOUT DES COMPTES FINANCIERS POUR COLLECTIVITÉ 3
-- =============================================

-- 1. Insertion des comptes financiers (financial_account)
INSERT INTO financial_account
(id, collectivity_id, owner_type, account_type, amount)
VALUES
    ('C3-A-BANK-1',   'col-3', 'COLLECTIVITY', 'BANK', 0.00),
    ('C3-A-BANK-2',   'col-3', 'COLLECTIVITY', 'BANK', 0.00),
    ('C3-A-MOBILE-1', 'col-3', 'COLLECTIVITY', 'MOBILE_BANKING', 0.00)
    ON CONFLICT (id) DO NOTHING;

-- 2. Insertion des comptes bancaires (bank_account)
-- Note: Le champ 'holder_name' correspond au 'Titulaire' du tableau
INSERT INTO bank_account
(id, holder_name, bank_name, bank_code, bank_branch_code, bank_account_number, bank_account_key)
VALUES
    ('C3-A-BANK-1', 'Koto',  'BMOI', 00004, 00001, 1234567890, 12),
    ('C3-A-BANK-2', 'Naivo', 'BRED', 00008, 00003, 4567890123, 58)
    ON CONFLICT (id) DO NOTHING;

-- 3. Insertion du compte mobile money (mobile_account)
-- Note: 'MVOLA' est une valeur valide dans mobile_service_enum
INSERT INTO mobile_account
(id, holder_name, mobile_service, mobile_number)
VALUES
    ('C3-A-MOBILE-1', 'Kolo', 'MVOLA', '0341889612')
    ON CONFLICT (id) DO NOTHING;

-- =============================================
-- COTISATIONS POUR COLLECTIVITÉ 1
-- =============================================
INSERT INTO membership_fee
(id, collectivity_id, label, status, frequency, eligible_from, amount)
VALUES
    ('cot-1', 'col-1', 'Cotisation annuelle', 'ACTIVE', 'ANNUALLY', '2026-01-01', 200000),
    ('cot-2', 'col-1', 'Famangiana', 'ACTIVE', 'PUNCTUALLY', '2026-04-30', 20000)
    ON CONFLICT (id) DO UPDATE SET
    collectivity_id = EXCLUDED.collectivity_id,
                            label = EXCLUDED.label,
                            status = EXCLUDED.status,
                            frequency = EXCLUDED.frequency,
                            eligible_from = EXCLUDED.eligible_from,
                            amount = EXCLUDED.amount;

-- =============================================
-- COTISATIONS POUR COLLECTIVITÉ 2
-- =============================================
INSERT INTO membership_fee
(id, collectivity_id, label, status, frequency, eligible_from, amount)
VALUES
    ('cot-3', 'col-2', 'Cotisation annuelle', 'ACTIVE', 'ANNUALLY', '2026-01-01', 200000),
    ('cot-4', 'col-2', 'Cotisation 2025', 'INACTIVE', 'ANNUALLY', '2025-01-01', 100000)
    ON CONFLICT (id) DO UPDATE SET
    collectivity_id = EXCLUDED.collectivity_id,
                            label = EXCLUDED.label,
                            status = EXCLUDED.status,
                            frequency = EXCLUDED.frequency,
                            eligible_from = EXCLUDED.eligible_from,
                            amount = EXCLUDED.amount;

-- =============================================
-- COTISATIONS POUR COLLECTIVITÉ 3
-- =============================================
INSERT INTO membership_fee
(id, collectivity_id, label, status, frequency, eligible_from, amount)
VALUES
    ('cot-5', 'col-3', 'Cotisation mensuelle', 'ACTIVE', 'MONTHLY', '2026-04-01', 25000)
    ON CONFLICT (id) DO UPDATE SET
    collectivity_id = EXCLUDED.collectivity_id,
                            label = EXCLUDED.label,
                            status = EXCLUDED.status,
                            frequency = EXCLUDED.frequency,
                            eligible_from = EXCLUDED.eligible_from,
                            amount = EXCLUDED.amount;


-- =============================================
-- SUPPRESSION DES ANCIENNES DONNÉES
-- =============================================

-- Suppression des anciens paiements pour col-1 et col-2
DELETE FROM member_payment
WHERE member_id IN ('C1-M1', 'C1-M2', 'C1-M3', 'C1-M4', 'C1-M5', 'C1-M6', 'C1-M7', 'C1-M8')
   OR member_id IN ('C2-M1', 'C2-M2', 'C2-M3', 'C2-M4', 'C2-M5', 'C2-M6', 'C2-M7', 'C2-M8');

-- Suppression des anciennes transactions pour col-1 et col-2
DELETE FROM collectivity_transaction
WHERE member_id IN ('C1-M1', 'C1-M2', 'C1-M3', 'C1-M4', 'C1-M5', 'C1-M6', 'C1-M7', 'C1-M8')
   OR member_id IN ('C2-M1', 'C2-M2', 'C2-M3', 'C2-M4', 'C2-M5', 'C2-M6', 'C2-M7', 'C2-M8');

-- Réinitialisation des montants des comptes financiers à 0
UPDATE financial_account SET amount = 0
WHERE id IN ('C1-A-CASH', 'C1-A-MOBILE-1', 'C2-A-CASH', 'C2-A-MOBILE-1');

-- =============================================
-- PAIEMENTS ET TRANSACTIONS - COLLECTIVITÉ 1
-- =============================================

-- Insertion des paiements (member_payment)
INSERT INTO member_payment
(id, member_id, membership_fee_id, account_id, amount, payment_mode, creation_date)
VALUES
    ('pay-c1-m1', 'C1-M1', 'cot-1', 'C1-A-CASH',       200000, 'CASH',           '2026-01-01'),
    ('pay-c1-m2', 'C1-M2', 'cot-1', 'C1-A-CASH',       200000, 'CASH',           '2026-01-01'),
    ('pay-c1-m3', 'C1-M3', 'cot-1', 'C1-A-MOBILE-1',   200000, 'MOBILE_BANKING', '2026-01-01'),
    ('pay-c1-m4', 'C1-M4', 'cot-1', 'C1-A-MOBILE-1',   200000, 'MOBILE_BANKING', '2026-01-01'),
    ('pay-c1-m5', 'C1-M5', 'cot-1', 'C1-A-MOBILE-1',   150000, 'MOBILE_BANKING', '2026-01-01'),
    ('pay-c1-m6', 'C1-M6', 'cot-1', 'C1-A-CASH',       100000, 'CASH',           '2026-05-01'),
    ('pay-c1-m7', 'C1-M7', 'cot-1', 'C1-A-CASH',        60000, 'CASH',           '2026-05-01'),
    ('pay-c1-m8', 'C1-M8', 'cot-1', 'C1-A-CASH',        90000, 'CASH',           '2026-05-01');

-- Insertion des transactions correspondantes (collectivity_transaction)
INSERT INTO collectivity_transaction
(id, collectivity_id, member_id, account_id, amount, payment_mode, creation_date)
VALUES
    ('trans-c1-m1', 'col-1', 'C1-M1', 'C1-A-CASH',       200000, 'CASH',           '2026-01-01'),
    ('trans-c1-m2', 'col-1', 'C1-M2', 'C1-A-CASH',       200000, 'CASH',           '2026-01-01'),
    ('trans-c1-m3', 'col-1', 'C1-M3', 'C1-A-MOBILE-1',   200000, 'MOBILE_BANKING', '2026-01-01'),
    ('trans-c1-m4', 'col-1', 'C1-M4', 'C1-A-MOBILE-1',   200000, 'MOBILE_BANKING', '2026-01-01'),
    ('trans-c1-m5', 'col-1', 'C1-M5', 'C1-A-MOBILE-1',   150000, 'MOBILE_BANKING', '2026-01-01'),
    ('trans-c1-m6', 'col-1', 'C1-M6', 'C1-A-CASH',       100000, 'CASH',           '2026-05-01'),
    ('trans-c1-m7', 'col-1', 'C1-M7', 'C1-A-CASH',        60000, 'CASH',           '2026-05-01'),
    ('trans-c1-m8', 'col-1', 'C1-M8', 'C1-A-CASH',        90000, 'CASH',           '2026-05-01');

-- Mise à jour des montants dans financial_account
UPDATE financial_account SET amount = amount + 200000 WHERE id = 'C1-A-CASH';
UPDATE financial_account SET amount = amount + 200000 WHERE id = 'C1-A-CASH';
UPDATE financial_account SET amount = amount + 200000 WHERE id = 'C1-A-MOBILE-1';
UPDATE financial_account SET amount = amount + 200000 WHERE id = 'C1-A-MOBILE-1';
UPDATE financial_account SET amount = amount + 150000 WHERE id = 'C1-A-MOBILE-1';
UPDATE financial_account SET amount = amount + 100000 WHERE id = 'C1-A-CASH';
UPDATE financial_account SET amount = amount + 60000  WHERE id = 'C1-A-CASH';
UPDATE financial_account SET amount = amount + 90000  WHERE id = 'C1-A-CASH';

-- =============================================
-- PAIEMENTS ET TRANSACTIONS - COLLECTIVITÉ 2
-- =============================================

-- Insertion des paiements (member_payment)
INSERT INTO member_payment
(id, member_id, membership_fee_id, account_id, amount, payment_mode, creation_date)
VALUES
    ('pay-c2-m1', 'C2-M1', 'cot-3', 'C2-A-CASH',       120000, 'CASH',           '2026-01-01'),
    ('pay-c2-m2', 'C2-M2', 'cot-3', 'C2-A-CASH',       180000, 'CASH',           '2026-01-01'),
    ('pay-c2-m3', 'C2-M3', 'cot-3', 'C2-A-CASH',       200000, 'CASH',           '2026-01-01'),
    ('pay-c2-m4', 'C2-M4', 'cot-3', 'C2-A-CASH',       200000, 'CASH',           '2026-01-01'),
    ('pay-c2-m5', 'C2-M5', 'cot-3', 'C2-A-CASH',       200000, 'CASH',           '2026-01-01'),
    ('pay-c2-m6', 'C2-M6', 'cot-3', 'C2-A-CASH',       200000, 'CASH',           '2026-01-01'),
    ('pay-c2-m7', 'C2-M7', 'cot-3', 'C2-A-MOBILE-1',    80000, 'MOBILE_BANKING', '2026-01-01'),
    ('pay-c2-m8', 'C2-M8', 'cot-3', 'C2-A-MOBILE-1',   120000, 'MOBILE_BANKING', '2026-01-01');

-- Insertion des transactions correspondantes (collectivity_transaction)
INSERT INTO collectivity_transaction
(id, collectivity_id, member_id, account_id, amount, payment_mode, creation_date)
VALUES
    ('trans-c2-m1', 'col-2', 'C2-M1', 'C2-A-CASH',       120000, 'CASH',           '2026-01-01'),
    ('trans-c2-m2', 'col-2', 'C2-M2', 'C2-A-CASH',       180000, 'CASH',           '2026-01-01'),
    ('trans-c2-m3', 'col-2', 'C2-M3', 'C2-A-CASH',       200000, 'CASH',           '2026-01-01'),
    ('trans-c2-m4', 'col-2', 'C2-M4', 'C2-A-CASH',       200000, 'CASH',           '2026-01-01'),
    ('trans-c2-m5', 'col-2', 'C2-M5', 'C2-A-CASH',       200000, 'CASH',           '2026-01-01'),
    ('trans-c2-m6', 'col-2', 'C2-M6', 'C2-A-CASH',       200000, 'CASH',           '2026-01-01'),
    ('trans-c2-m7', 'col-2', 'C2-M7', 'C2-A-MOBILE-1',    80000, 'MOBILE_BANKING', '2026-01-01'),
    ('trans-c2-m8', 'col-2', 'C2-M8', 'C2-A-MOBILE-1',   120000, 'MOBILE_BANKING', '2026-01-01');

-- Mise à jour des montants dans financial_account
UPDATE financial_account SET amount = amount + 120000 WHERE id = 'C2-A-CASH';
UPDATE financial_account SET amount = amount + 180000 WHERE id = 'C2-A-CASH';
UPDATE financial_account SET amount = amount + 200000 WHERE id = 'C2-A-CASH';
UPDATE financial_account SET amount = amount + 200000 WHERE id = 'C2-A-CASH';
UPDATE financial_account SET amount = amount + 200000 WHERE id = 'C2-A-CASH';
UPDATE financial_account SET amount = amount + 200000 WHERE id = 'C2-A-CASH';
UPDATE financial_account SET amount = amount + 80000  WHERE id = 'C2-A-MOBILE-1';
UPDATE financial_account SET amount = amount + 120000 WHERE id = 'C2-A-MOBILE-1';

-- =============================================
-- VÉRIFICATION DES TOTAUX
-- =============================================

-- Collectivité 1:
-- C1-A-CASH: 200K + 200K + 100K + 60K + 90K = 650 000 Ar
-- C1-A-MOBILE-1: 200K + 200K + 150K = 550 000 Ar
-- TOTAL col-1 = 1 200 000 Ar

-- Collectivité 2:
-- C2-A-CASH: 120K + 180K + 200K + 200K + 200K + 200K = 1 100 000 Ar
-- C2-A-MOBILE-1: 80K + 120K = 200 000 Ar
-- TOTAL col-2 = 1 300 000 Ar

-- =============================================
-- SUPPRESSION DES ANCIENNES DONNÉES POUR COL-3
-- =============================================

-- Suppression des anciens paiements pour col-3
DELETE FROM member_payment
WHERE member_id IN ('C3-M1', 'C3-M2', 'C3-M3', 'C3-M4', 'C3-M5', 'C3-M6', 'C3-M7', 'C3-M8');

-- Suppression des anciennes transactions pour col-3
DELETE FROM collectivity_transaction
WHERE member_id IN ('C3-M1', 'C3-M2', 'C3-M3', 'C3-M4', 'C3-M5', 'C3-M6', 'C3-M7', 'C3-M8');

-- Réinitialisation des montants des comptes financiers de col-3 à 0
UPDATE financial_account SET amount = 0
WHERE id IN ('C3-A-CASH', 'C3-A-BANK-1', 'C3-A-BANK-2', 'C3-A-MOBILE-1');

-- =============================================
-- PAIEMENTS ET TRANSACTIONS - COLLECTIVITÉ 3
-- =============================================

-- Insertion des paiements (member_payment)
-- Note: La cotisation utilisée est 'cot-5' (Cotisation mensuelle à 25 000 Ar)
INSERT INTO member_payment
(id, member_id, membership_fee_id, account_id, amount, payment_mode, creation_date)
VALUES
    -- Paiements du 01/04/2026
    ('pay-c3-m1-avr', 'C3-M1', 'cot-5', 'C3-A-BANK-1',    25000, 'BANK_TRANSFER', '2026-04-01'),
    ('pay-c3-m2-avr', 'C3-M2', 'cot-5', 'C3-A-BANK-1',    25000, 'BANK_TRANSFER', '2026-04-01'),
    ('pay-c3-m3-avr', 'C3-M3', 'cot-5', 'C3-A-BANK-1',    25000, 'BANK_TRANSFER', '2026-04-01'),
    ('pay-c3-m4-avr', 'C3-M4', 'cot-5', 'C3-A-BANK-1',    25000, 'BANK_TRANSFER', '2026-04-01'),
    ('pay-c3-m5-avr', 'C3-M5', 'cot-5', 'C3-A-BANK-2',    25000, 'BANK_TRANSFER', '2026-04-01'),
    ('pay-c3-m6-avr', 'C3-M6', 'cot-5', 'C3-A-BANK-2',    25000, 'BANK_TRANSFER', '2026-04-01'),
    ('pay-c3-m7-avr', 'C3-M7', 'cot-5', 'C3-A-CASH',      25000, 'CASH',           '2026-04-01'),
    ('pay-c3-m8-avr', 'C3-M8', 'cot-5', 'C3-A-CASH',      25000, 'CASH',           '2026-04-01'),

    -- Paiements du 01/05/2026
    ('pay-c3-m1-mai', 'C3-M1', 'cot-5', 'C3-A-BANK-1',    25000, 'BANK_TRANSFER', '2026-05-01'),
    ('pay-c3-m2-mai', 'C3-M2', 'cot-5', 'C3-A-BANK-1',    25000, 'BANK_TRANSFER', '2026-05-01'),
    ('pay-c3-m3-mai', 'C3-M3', 'cot-5', 'C3-A-MOBILE-1',  15000, 'MOBILE_BANKING', '2026-05-01'),
    ('pay-c3-m4-mai', 'C3-M4', 'cot-5', 'C3-A-MOBILE-1',  15000, 'MOBILE_BANKING', '2026-05-01'),
    ('pay-c3-m5-mai', 'C3-M5', 'cot-5', 'C3-A-BANK-2',    20000, 'BANK_TRANSFER', '2026-05-01'),
    ('pay-c3-m6-mai', 'C3-M6', 'cot-5', 'C3-A-BANK-2',    25000, 'BANK_TRANSFER', '2026-05-01'),
    ('pay-c3-m7-mai', 'C3-M7', 'cot-5', 'C3-A-CASH',       5000, 'CASH',           '2026-05-01'),
    ('pay-c3-m8-mai', 'C3-M8', 'cot-5', 'C3-A-CASH',       5000, 'CASH',           '2026-05-01');

-- Insertion des transactions correspondantes (collectivity_transaction)
INSERT INTO collectivity_transaction
(id, collectivity_id, member_id, account_id, amount, payment_mode, creation_date)
VALUES
    -- Transactions du 01/04/2026
    ('trans-c3-m1-avr', 'col-3', 'C3-M1', 'C3-A-BANK-1',    25000, 'BANK_TRANSFER', '2026-04-01'),
    ('trans-c3-m2-avr', 'col-3', 'C3-M2', 'C3-A-BANK-1',    25000, 'BANK_TRANSFER', '2026-04-01'),
    ('trans-c3-m3-avr', 'col-3', 'C3-M3', 'C3-A-BANK-1',    25000, 'BANK_TRANSFER', '2026-04-01'),
    ('trans-c3-m4-avr', 'col-3', 'C3-M4', 'C3-A-BANK-1',    25000, 'BANK_TRANSFER', '2026-04-01'),
    ('trans-c3-m5-avr', 'col-3', 'C3-M5', 'C3-A-BANK-2',    25000, 'BANK_TRANSFER', '2026-04-01'),
    ('trans-c3-m6-avr', 'col-3', 'C3-M6', 'C3-A-BANK-2',    25000, 'BANK_TRANSFER', '2026-04-01'),
    ('trans-c3-m7-avr', 'col-3', 'C3-M7', 'C3-A-CASH',      25000, 'CASH',           '2026-04-01'),
    ('trans-c3-m8-avr', 'col-3', 'C3-M8', 'C3-A-CASH',      25000, 'CASH',           '2026-04-01'),

    -- Transactions du 01/05/2026
    ('trans-c3-m1-mai', 'col-3', 'C3-M1', 'C3-A-BANK-1',    25000, 'BANK_TRANSFER', '2026-05-01'),
    ('trans-c3-m2-mai', 'col-3', 'C3-M2', 'C3-A-BANK-1',    25000, 'BANK_TRANSFER', '2026-05-01'),
    ('trans-c3-m3-mai', 'col-3', 'C3-M3', 'C3-A-MOBILE-1',  15000, 'MOBILE_BANKING', '2026-05-01'),
    ('trans-c3-m4-mai', 'col-3', 'C3-M4', 'C3-A-MOBILE-1',  15000, 'MOBILE_BANKING', '2026-05-01'),
    ('trans-c3-m5-mai', 'col-3', 'C3-M5', 'C3-A-BANK-2',    20000, 'BANK_TRANSFER', '2026-05-01'),
    ('trans-c3-m6-mai', 'col-3', 'C3-M6', 'C3-A-BANK-2',    25000, 'BANK_TRANSFER', '2026-05-01'),
    ('trans-c3-m7-mai', 'col-3', 'C3-M7', 'C3-A-CASH',       5000, 'CASH',           '2026-05-01'),
    ('trans-c3-m8-mai', 'col-3', 'C3-M8', 'C3-A-CASH',       5000, 'CASH',           '2026-05-01');

-- =============================================
-- MISE À JOUR DES MONTANTS DES COMPTES
-- =============================================

-- Mise à jour de C3-A-BANK-1
UPDATE financial_account SET amount = amount + 25000 WHERE id = 'C3-A-BANK-1';  -- C3-M1 avril
UPDATE financial_account SET amount = amount + 25000 WHERE id = 'C3-A-BANK-1';  -- C3-M2 avril
UPDATE financial_account SET amount = amount + 25000 WHERE id = 'C3-A-BANK-1';  -- C3-M3 avril
UPDATE financial_account SET amount = amount + 25000 WHERE id = 'C3-A-BANK-1';  -- C3-M4 avril
UPDATE financial_account SET amount = amount + 25000 WHERE id = 'C3-A-BANK-1';  -- C3-M1 mai
UPDATE financial_account SET amount = amount + 25000 WHERE id = 'C3-A-BANK-1';  -- C3-M2 mai
-- Total C3-A-BANK-1 = 150 000 Ar

-- Mise à jour de C3-A-BANK-2
UPDATE financial_account SET amount = amount + 25000 WHERE id = 'C3-A-BANK-2';  -- C3-M5 avril
UPDATE financial_account SET amount = amount + 25000 WHERE id = 'C3-A-BANK-2';  -- C3-M6 avril
UPDATE financial_account SET amount = amount + 20000 WHERE id = 'C3-A-BANK-2';  -- C3-M5 mai
UPDATE financial_account SET amount = amount + 25000 WHERE id = 'C3-A-BANK-2';  -- C3-M6 mai
-- Total C3-A-BANK-2 = 95 000 Ar

-- Mise à jour de C3-A-MOBILE-1
UPDATE financial_account SET amount = amount + 15000 WHERE id = 'C3-A-MOBILE-1'; -- C3-M3 mai
UPDATE financial_account SET amount = amount + 15000 WHERE id = 'C3-A-MOBILE-1'; -- C3-M4 mai
-- Total C3-A-MOBILE-1 = 30 000 Ar

-- Mise à jour de C3-A-CASH
UPDATE financial_account SET amount = amount + 25000 WHERE id = 'C3-A-CASH';     -- C3-M7 avril
UPDATE financial_account SET amount = amount + 25000 WHERE id = 'C3-A-CASH';     -- C3-M8 avril
UPDATE financial_account SET amount = amount + 5000  WHERE id = 'C3-A-CASH';     -- C3-M7 mai
UPDATE financial_account SET amount = amount + 5000  WHERE id = 'C3-A-CASH';     -- C3-M8 mai
-- Total C3-A-CASH = 60 000 Ar

-- =============================================
-- RÉCAPITULATIF DES MONTANTS POUR COL-3
-- =============================================

-- C3-A-BANK-1:     150 000 Ar
-- C3-A-BANK-2:      95 000 Ar
-- C3-A-MOBILE-1:    30 000 Ar
-- C3-A-CASH:        60 000 Ar
-- TOTAL COL-3:     335 000 Ar

-- =============================================
-- MISE À JOUR DES DATES D'ADHÉSION DES ANCIENS MEMBRES
-- =============================================

-- Tous les anciens membres des trois collectivités voient leur date d'adhésion fixée au 01/01/2026
UPDATE member SET adhesion_date = '2026-01-01'
WHERE id IN (
    -- Anciens membres collectivité 1
             'C1-M1', 'C1-M2', 'C1-M3', 'C1-M4', 'C1-M5', 'C1-M6', 'C1-M7', 'C1-M8',
    -- Anciens membres collectivité 2
             'C2-M1', 'C2-M2', 'C2-M3', 'C2-M4', 'C2-M5', 'C2-M6', 'C2-M7', 'C2-M8',
    -- Anciens membres collectivité 3
             'C3-M1', 'C3-M2', 'C3-M3', 'C3-M4', 'C3-M5', 'C3-M6', 'C3-M7', 'C3-M8'
    );

-- =============================================
-- NOUVEAUX MEMBRES - COLLECTIVITÉ 1
-- =============================================

-- Insertion des nouveaux membres
INSERT INTO member
(id, first_name, last_name, birth_date, gender, address, profession, phone_number, email, occupation, adhesion_date)
VALUES
    ('C1-M9',  'Rakoto',   'Jean',     '1995-03-15', 'M', 'Lot 123 Ambatondrazaka', 'Agriculteur', '0341000001', 'rakoto.jean@email.mg', 'JUNIOR', '2026-04-01'),
    ('C1-M10', 'Rabe',     'Marie',    '1996-05-20', 'F', 'Lot 124 Ambatondrazaka', 'Éleveur',     '0341000002', 'rabe.marie@email.mg',  'JUNIOR', '2026-04-01'),
    ('C1-M11', 'Randria',  'Paul',     '1997-07-10', 'M', 'Lot 125 Ambatondrazaka', 'Commerçant',  '0341000003', 'randria.paul@email.mg', 'JUNIOR', '2026-05-01'),
    ('C1-M12', 'Razafy',   'Sophie',   '1998-09-25', 'F', 'Lot 126 Ambatondrazaka', 'Artisan',     '0341000004', 'razafy.sophie@email.mg','JUNIOR', '2026-06-01');

-- Liaison des nouveaux membres à la collectivité 1
INSERT INTO collectivity_member (collectivity_id, member_id)
VALUES
    ('col-1', 'C1-M9'),
    ('col-1', 'C1-M10'),
    ('col-1', 'C1-M11'),
    ('col-1', 'C1-M12');

-- Ajout des membres référents pour la collectivité 1
INSERT INTO referee (member_id, referee_id, relation)
VALUES
    ('C1-M9',  'C1-M1', 'famille'),
    ('C1-M9',  'C1-M2', 'famille'),
    ('C1-M10', 'C1-M1', 'famille'),
    ('C1-M10', 'C1-M2', 'famille'),
    ('C1-M11', 'C1-M1', 'famille'),
    ('C1-M11', 'C1-M2', 'famille'),
    ('C1-M12', 'C1-M1', 'famille'),
    ('C1-M12', 'C1-M2', 'famille');

-- =============================================
-- NOUVEAUX MEMBRES - COLLECTIVITÉ 2
-- =============================================

-- Insertion des nouveaux membres
INSERT INTO member
(id, first_name, last_name, birth_date, gender, address, profession, phone_number, email, occupation, adhesion_date)
VALUES
    ('C2-M9',  'Rakotomalala', 'Hery',     '1994-02-10', 'M', 'Lot 223 Ambatondrazaka', 'Pisciculteur', '0342000001', 'rakotomalala.hery@email.mg', 'JUNIOR', '2026-03-01'),
    ('C2-M10', 'Andrian',      'Tiana',    '1995-04-18', 'F', 'Lot 224 Ambatondrazaka', 'Agricultrice', '0342000002', 'andrian.tiana@email.mg',    'JUNIOR', '2026-03-01'),
    ('C2-M11', 'Rakotobe',     'Fidy',     '1996-06-22', 'M', 'Lot 225 Ambatondrazaka', 'Commerçant',   '0342000003', 'rakotobe.fidy@email.mg',    'JUNIOR', '2026-03-01');

-- Liaison des nouveaux membres à la collectivité 2
INSERT INTO collectivity_member (collectivity_id, member_id)
VALUES
    ('col-2', 'C2-M9'),
    ('col-2', 'C2-M10'),
    ('col-2', 'C2-M11');

-- Ajout des membres référents pour la collectivité 2
INSERT INTO referee (member_id, referee_id, relation)
VALUES
    ('C2-M9',  'C2-M1', 'famille'),
    ('C2-M9',  'C2-M2', 'famille'),
    ('C2-M10', 'C2-M1', 'famille'),
    ('C2-M10', 'C2-M2', 'famille'),
    ('C2-M11', 'C2-M1', 'famille'),
    ('C2-M11', 'C2-M2', 'famille');

-- =============================================
-- NOUVEAUX MEMBRES - COLLECTIVITÉ 3
-- =============================================

-- Insertion des nouveaux membres
INSERT INTO member
(id, first_name, last_name, birth_date, gender, address, profession, phone_number, email, occupation, adhesion_date)
VALUES
    ('C3-M9',  'Ravelo',     'Mamy',     '1993-01-05', 'M', 'Lot 323 Brickaville', 'Apiculteur', '0343000001', 'ravelo.mamy@email.mg',   'JUNIOR', '2026-01-01'),
    ('C3-M10', 'Rakotoarisoa','Lanto',   '1994-02-14', 'M', 'Lot 324 Brickaville', 'Apiculteur', '0343000002', 'rakotoarisoa.lanto@email.mg','JUNIOR', '2026-02-01'),
    ('C3-M11', 'Randrian',    'Noro',     '1995-03-20', 'F', 'Lot 325 Brickaville', 'Apicultrice','0343000003', 'randrian.noro@email.mg', 'JUNIOR', '2026-02-01'),
    ('C3-M12', 'Razafindrabe','Tovo',     '1996-04-25', 'M', 'Lot 326 Brickaville', 'Apiculteur', '0343000004', 'razafindrabe.tovo@email.mg','JUNIOR', '2026-03-01'),
    ('C3-M13', 'Rakotondra',  'Naina',    '1997-05-30', 'F', 'Lot 327 Brickaville', 'Apicultrice','0343000005', 'rakotondra.naina@email.mg','JUNIOR', '2026-03-01'),
    ('C3-M14', 'Andriatsi',   'Fano',     '1998-06-12', 'M', 'Lot 328 Brickaville', 'Apiculteur', '0343000006', 'andriatsi.fano@email.mg', 'JUNIOR', '2026-03-01');

-- Liaison des nouveaux membres à la collectivité 3
INSERT INTO collectivity_member (collectivity_id, member_id)
VALUES
    ('col-3', 'C3-M9'),
    ('col-3', 'C3-M10'),
    ('col-3', 'C3-M11'),
    ('col-3', 'C3-M12'),
    ('col-3', 'C3-M13'),
    ('col-3', 'C3-M14');

-- Ajout des membres référents pour la collectivité 3
INSERT INTO referee (member_id, referee_id, relation)
VALUES
    ('C3-M9',  'C3-M1', 'famille'),
    ('C3-M9',  'C3-M2', 'famille'),
    ('C3-M10', 'C3-M1', 'famille'),
    ('C3-M10', 'C3-M2', 'famille'),
    ('C3-M11', 'C3-M1', 'famille'),
    ('C3-M11', 'C3-M2', 'famille'),
    ('C3-M12', 'C3-M1', 'famille'),
    ('C3-M12', 'C3-M2', 'famille'),
    ('C3-M13', 'C3-M1', 'famille'),
    ('C3-M13', 'C3-M2', 'famille'),
    ('C3-M14', 'C3-M1', 'famille'),
    ('C3-M14', 'C3-M2', 'famille');

-- =============================================
-- RÉCAPITULATIF DES NOUVEAUX MEMBRES
-- =============================================

-- Collectivité 1: 4 nouveaux membres (C1-M9 à C1-M12)
-- Collectivité 2: 3 nouveaux membres (C2-M9 à C2-M11)
-- Collectivité 3: 6 nouveaux membres (C3-M9 à C3-M14)
-- TOTAL: 13 nouveaux membres

-- Tous ces membres ont l'occupation 'JUNIOR'
-- Aucun paiement n'est enregistré pour ces nouveaux membres


CREATE TYPE activity_type_enum AS ENUM ('MEETING', 'TRAINING', 'OTHER');
CREATE TYPE day_of_week_enum AS ENUM ('MO', 'TU', 'WE', 'TH', 'FR', 'SA', 'SU');
CREATE TYPE attendance_status_enum AS ENUM ('ATTENDED', 'MISSING', 'UNDEFINED');

CREATE TABLE collectivity_activity (
                                       id VARCHAR(50) PRIMARY KEY,
                                       collectivity_id VARCHAR(50) NOT NULL,
                                       label VARCHAR(150),
                                       activity_type activity_type_enum,
                                       executive_date DATE,
                                       week_ordinal INTEGER,
                                       day_of_week day_of_week_enum,
                                       FOREIGN KEY (collectivity_id) REFERENCES collectivity(id) ON DELETE CASCADE
);

CREATE TABLE activity_occupation_concerned (
                                               activity_id VARCHAR(50) NOT NULL,
                                               occupation occupation_enum NOT NULL,
                                               PRIMARY KEY (activity_id, occupation),
                                               FOREIGN KEY (activity_id) REFERENCES collectivity_activity(id) ON DELETE CASCADE
);

CREATE TABLE activity_attendance (
                                     id VARCHAR(50) PRIMARY KEY,
                                     activity_id VARCHAR(50) NOT NULL,
                                     member_id VARCHAR(50) NOT NULL,
                                     attendance_status attendance_status_enum NOT NULL DEFAULT 'UNDEFINED',
                                     FOREIGN KEY (activity_id) REFERENCES collectivity_activity(id) ON DELETE CASCADE,
                                     FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE
);
