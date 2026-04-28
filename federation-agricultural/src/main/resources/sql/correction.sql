-- =============================================
-- 1. SUPPRIMER TOUTES LES CLÉS ÉTRANGÈRES
-- =============================================
ALTER TABLE public.collectivity_member
DROP CONSTRAINT collectivity_member_collectivity_id_fkey;

ALTER TABLE public.collectivity_structure
DROP CONSTRAINT collectivity_structure_collectivity_id_fkey;

ALTER TABLE public.collectivity_transaction
DROP CONSTRAINT collectivity_transaction_collectivity_id_fkey;

ALTER TABLE public.membership_fee
DROP CONSTRAINT membership_fee_collectivity_id_fkey;

ALTER TABLE public.role_assignment
DROP CONSTRAINT role_assignment_collectivity_id_fkey;


-- =============================================
-- 2. SUPPRIMER LA CLÉ PRIMAIRE
-- =============================================
ALTER TABLE public.collectivity
DROP CONSTRAINT collectivity_pkey;


-- =============================================
-- 3. MODIFIER LE TYPE DES COLONNES
-- =============================================

-- Modifier "id" de integer en varchar dans la table principale
ALTER TABLE public.collectivity
ALTER COLUMN id TYPE VARCHAR(50) USING id::VARCHAR;

-- Modifier "collectivity_id" en varchar dans toutes les tables filles
ALTER TABLE public.collectivity_member
ALTER COLUMN collectivity_id TYPE VARCHAR(50) USING collectivity_id::VARCHAR;

ALTER TABLE public.collectivity_structure
ALTER COLUMN collectivity_id TYPE VARCHAR(50) USING collectivity_id::VARCHAR;

ALTER TABLE public.collectivity_transaction
ALTER COLUMN collectivity_id TYPE VARCHAR(50) USING collectivity_id::VARCHAR;

ALTER TABLE public.membership_fee
ALTER COLUMN collectivity_id TYPE VARCHAR(50) USING collectivity_id::VARCHAR;

ALTER TABLE public.role_assignment
ALTER COLUMN collectivity_id TYPE VARCHAR(50) USING collectivity_id::VARCHAR;

ALTER TABLE public.collectivity ADD COLUMN specialization VARCHAR(100);
ALTER TABLE public.collectivity
    ALTER COLUMN id DROP DEFAULT;
DROP SEQUENCE IF EXISTS collectivity_id_seq;
TRUNCATE TABLE public.collectivity CASCADE;
-- =============================================
-- 4. RECRÉER LA CLÉ PRIMAIRE
-- =============================================
ALTER TABLE public.collectivity
    ADD CONSTRAINT collectivity_pkey PRIMARY KEY (id);


-- =============================================
-- 5. RECRÉER TOUTES LES CLÉS ÉTRANGÈRES
-- =============================================
ALTER TABLE public.collectivity_member
    ADD CONSTRAINT collectivity_member_collectivity_id_fkey
        FOREIGN KEY (collectivity_id) REFERENCES public.collectivity(id);

ALTER TABLE public.collectivity_structure
    ADD CONSTRAINT collectivity_structure_collectivity_id_fkey
        FOREIGN KEY (collectivity_id) REFERENCES public.collectivity(id);

ALTER TABLE public.collectivity_transaction
    ADD CONSTRAINT collectivity_transaction_collectivity_id_fkey
        FOREIGN KEY (collectivity_id) REFERENCES public.collectivity(id);

ALTER TABLE public.membership_fee
    ADD CONSTRAINT membership_fee_collectivity_id_fkey
        FOREIGN KEY (collectivity_id) REFERENCES public.collectivity(id);

ALTER TABLE public.role_assignment
    ADD CONSTRAINT role_assignment_collectivity_id_fkey
        FOREIGN KEY (collectivity_id) REFERENCES public.collectivity(id);


-- =============================================
-- 1. SUPPRIMER TOUTES LES CLÉS ÉTRANGÈRES
-- =============================================
ALTER TABLE public.collectivity_member
DROP CONSTRAINT collectivity_member_member_id_fkey;

ALTER TABLE public.collectivity_structure
DROP CONSTRAINT collectivity_structure_president_id_fkey;

ALTER TABLE public.collectivity_structure
DROP CONSTRAINT collectivity_structure_secretary_id_fkey;

ALTER TABLE public.collectivity_structure
DROP CONSTRAINT collectivity_structure_treasurer_id_fkey;

ALTER TABLE public.collectivity_structure
DROP CONSTRAINT collectivity_structure_vice_president_id_fkey;

ALTER TABLE public.collectivity_transaction
DROP CONSTRAINT collectivity_transaction_member_id_fkey;

ALTER TABLE public.member_payment
DROP CONSTRAINT member_payment_member_id_fkey;

ALTER TABLE public.referee
DROP CONSTRAINT referee_member_id_fkey;

ALTER TABLE public.referee
DROP CONSTRAINT referee_referee_id_fkey;

ALTER TABLE public.role_assignment
DROP CONSTRAINT role_assignment_member_id_fkey;


-- =============================================
-- 2. SUPPRIMER LA CLÉ PRIMAIRE
-- =============================================
ALTER TABLE public.member
DROP CONSTRAINT member_pkey;


-- =============================================
-- 3. MODIFIER LE TYPE DES COLONNES
-- =============================================

-- Table principale
ALTER TABLE public.member
ALTER COLUMN id TYPE VARCHAR(50) USING id::VARCHAR;

ALTER TABLE public.member
    ALTER COLUMN id DROP DEFAULT;

DROP SEQUENCE IF EXISTS member_id_seq;

-- Tables filles
ALTER TABLE public.collectivity_member
ALTER COLUMN member_id TYPE VARCHAR(50) USING member_id::VARCHAR;

ALTER TABLE public.collectivity_structure
ALTER COLUMN president_id TYPE VARCHAR(50) USING president_id::VARCHAR;

ALTER TABLE public.collectivity_structure
ALTER COLUMN secretary_id TYPE VARCHAR(50) USING secretary_id::VARCHAR;

ALTER TABLE public.collectivity_structure
ALTER COLUMN treasurer_id TYPE VARCHAR(50) USING treasurer_id::VARCHAR;

ALTER TABLE public.collectivity_structure
ALTER COLUMN vice_president_id TYPE VARCHAR(50) USING vice_president_id::VARCHAR;

ALTER TABLE public.collectivity_transaction
ALTER COLUMN member_id TYPE VARCHAR(50) USING member_id::VARCHAR;

ALTER TABLE public.member_payment
ALTER COLUMN member_id TYPE VARCHAR(50) USING member_id::VARCHAR;

ALTER TABLE public.referee
ALTER COLUMN member_id TYPE VARCHAR(50) USING member_id::VARCHAR;

ALTER TABLE public.referee
ALTER COLUMN referee_id TYPE VARCHAR(50) USING referee_id::VARCHAR;

ALTER TABLE public.role_assignment
ALTER COLUMN member_id TYPE VARCHAR(50) USING member_id::VARCHAR;


-- =============================================
-- 4. RECRÉER LA CLÉ PRIMAIRE
-- =============================================
ALTER TABLE public.member
    ADD CONSTRAINT member_pkey PRIMARY KEY (id);


-- =============================================
-- 5. RECRÉER TOUTES LES CLÉS ÉTRANGÈRES
-- =============================================
ALTER TABLE public.collectivity_member
    ADD CONSTRAINT collectivity_member_member_id_fkey
        FOREIGN KEY (member_id) REFERENCES public.member(id);

ALTER TABLE public.collectivity_structure
    ADD CONSTRAINT collectivity_structure_president_id_fkey
        FOREIGN KEY (president_id) REFERENCES public.member(id);

ALTER TABLE public.collectivity_structure
    ADD CONSTRAINT collectivity_structure_secretary_id_fkey
        FOREIGN KEY (secretary_id) REFERENCES public.member(id);

ALTER TABLE public.collectivity_structure
    ADD CONSTRAINT collectivity_structure_treasurer_id_fkey
        FOREIGN KEY (treasurer_id) REFERENCES public.member(id);

ALTER TABLE public.collectivity_structure
    ADD CONSTRAINT collectivity_structure_vice_president_id_fkey
        FOREIGN KEY (vice_president_id) REFERENCES public.member(id);

ALTER TABLE public.collectivity_transaction
    ADD CONSTRAINT collectivity_transaction_member_id_fkey
        FOREIGN KEY (member_id) REFERENCES public.member(id);

ALTER TABLE public.member_payment
    ADD CONSTRAINT member_payment_member_id_fkey
        FOREIGN KEY (member_id) REFERENCES public.member(id);

ALTER TABLE public.referee
    ADD CONSTRAINT referee_member_id_fkey
        FOREIGN KEY (member_id) REFERENCES public.member(id);

ALTER TABLE public.referee
    ADD CONSTRAINT referee_referee_id_fkey
        FOREIGN KEY (referee_id) REFERENCES public.member(id);

ALTER TABLE public.role_assignment
    ADD CONSTRAINT role_assignment_member_id_fkey
        FOREIGN KEY (member_id) REFERENCES public.member(id);

TRUNCATE TABLE public.member CASCADE;

-- =============================================
-- 1. SUPPRIMER LA CLÉ ÉTRANGÈRE (table fille)
-- =============================================
ALTER TABLE public.member_payment
DROP CONSTRAINT member_payment_membership_fee_id_fkey;


-- =============================================
-- 2. SUPPRIMER LA CLÉ PRIMAIRE
-- =============================================
ALTER TABLE public.membership_fee
DROP CONSTRAINT membership_fee_pkey;


-- =============================================
-- 3. MODIFIER LE TYPE DES COLONNES
-- =============================================

-- Table principale
ALTER TABLE public.membership_fee
ALTER COLUMN id TYPE VARCHAR(50) USING id::VARCHAR;

ALTER TABLE public.membership_fee
    ALTER COLUMN id DROP DEFAULT;

DROP SEQUENCE IF EXISTS membership_fee_id_seq;

-- Table fille
ALTER TABLE public.member_payment
ALTER COLUMN membership_fee_id TYPE VARCHAR(50) USING membership_fee_id::VARCHAR;


-- =============================================
-- 4. RECRÉER LA CLÉ PRIMAIRE
-- =============================================
ALTER TABLE public.membership_fee
    ADD CONSTRAINT membership_fee_pkey PRIMARY KEY (id);


-- =============================================
-- 5. RECRÉER LA CLÉ ÉTRANGÈRE
-- =============================================
ALTER TABLE public.member_payment
    ADD CONSTRAINT member_payment_membership_fee_id_fkey
        FOREIGN KEY (membership_fee_id) REFERENCES public.membership_fee(id);