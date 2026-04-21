CREATE TABLE ville (
                       id     SERIAL PRIMARY KEY,
                       nom    VARCHAR(100) NOT NULL,
                       region VARCHAR(100)
);

CREATE TABLE federation (
                            id               SERIAL PRIMARY KEY,
                            nom              VARCHAR(150) NOT NULL
                            /*mandat_duree_ans INT NOT NULL DEFAULT 2*/
);

CREATE TABLE autorisation_ouverture (
                                        id                SERIAL PRIMARY KEY,
                                        date_autorisation DATE        NOT NULL,
                                        statut            VARCHAR(20) NOT NULL CHECK (statut IN (
                                                                                                 'accordee', 'refusee', 'en_attente'
                                            ))
);

CREATE TABLE collectivite (
                              id                  SERIAL PRIMARY KEY,
                              numero              VARCHAR(20)  NOT NULL UNIQUE,
                              nom                 VARCHAR(150) NOT NULL UNIQUE,
                              specialite_agricole VARCHAR(100) NOT NULL,
                              autorisation_id    int references  autorisation_ouverture(id),
                              date_creation       DATE         NOT NULL,
                              ville_id            INT          NOT NULL REFERENCES ville(id),
                              federation_id       INT          NOT NULL REFERENCES federation(id)
);

CREATE TABLE personne (
                          id             SERIAL PRIMARY KEY,
                          nom            VARCHAR(100) NOT NULL,
                          prenom         VARCHAR(100) NOT NULL,
                          date_naissance DATE         NOT NULL,
                          genre          VARCHAR(10)  NOT NULL CHECK (genre IN ('masculin', 'feminin')),
                          adresse        TEXT         NOT NULL,
                          metier         VARCHAR(100) NOT NULL,
                          telephone      VARCHAR(20)  NOT NULL,
                          email          VARCHAR(150) NOT NULL UNIQUE
);

CREATE TYPE compte_type as enum('bancaire', 'mobile_money', 'caisse');
CREATE TABLE compte (
                        id SERIAL PRIMARY KEY,
                        type compte_type NOT NULL ,
                        collectivite_id INT,
                        federation_id INT,
                        solde NUMERIC(12,2) DEFAULT 0,

                        CHECK (
                            @@ -61,28 +60,28 @@

                        FOREIGN KEY (collectivite_id) REFERENCES collectivite(id),
    FOREIGN KEY (federation_id) REFERENCES federation(id),
    FOREIGN KEY (titulaire_id) REFERENCES personne(id)
);

CREATE TYPE status_paiement as enum('en_cours','valide','rejete');
CREATE TYPE mode_paiement_adhesion as enum('virement','mobile_money');

CREATE TABLE paiement_adhesion (
                                   id SERIAL PRIMARY KEY,
                                   montant INT NOT NULL CHECK (montant = 50000),
                                   personne_id INT NOT NULL,
                                   compte_reception_id INT NOT NULL,/*compte du collectivite*/
                                   mode_paiement mode_paiement_adhesion NOT NULL,
                                   reference_transaction TEXT UNIQUE NOT NULL ,
                                   date_paiement TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                   statut status_paiement,
                                   FOREIGN KEY (personne_id) REFERENCES personne(id) ON DELETE CASCADE,
                                   FOREIGN KEY (compte_reception_id) REFERENCES compte(id) ON DELETE CASCADE
);

/* créer le tableau adhesion*/
/* créer le type de status adhesion*/
CREATE TYPE adhesion_status as enum('en_attente', 'valide', 'refuse');
CREATE TABLE adhesion (
                          id SERIAL PRIMARY KEY,
                          collectivite_id INT NOT NULL,
                          paiement_id INT UNIQUE,
                          date_adhesion DATE DEFAULT CURRENT_DATE,
                          statut adhesion_status not null ,
                          FOREIGN KEY (collectivite_id) REFERENCES collectivite(id) ON DELETE CASCADE,
                          FOREIGN KEY (paiement_id) REFERENCES paiement_adhesion(id)
);

CREATE TABLE membre (
                        id SERIAL PRIMARY KEY,
                        collectivite_id INT NOT NULL,
                        parrain_id INT NOT NULL
                        adhesion_id INT UNIQUE NOT NULL,
                        FOREIGN KEY (parrain_id) REFERENCES membre(id) ON DELETE CASCADE,
                        FOREIGN KEY (collectivite_id) REFERENCES collectivite(id) ON DELETE CASCADE,
                        FOREIGN KEY (adhesion_id) REFERENCES adhesion(id) ON DELETE CASCADE
);

/* creer le tableau cotisation*/
CREATE TYPE  cotisation_type as enum('mensuelle','annuelle','ponctuelle');
CREATE TABLE cotisation (
                            id SERIAL PRIMARY KEY,
                            collectivite_id INT NOT NULL,
                            federation_id int references federation(id),
                            type cotisation_type not null ,
                            montant INT NOT NULL,
                            date_debut DATE,
                            date_fin DATE,
                            description TEXT,
                            CHECK (
                                (collectivite_id IS NOT NULL AND federation_id IS NULL) OR
                                (federation_id IS NOT NULL AND collectivite_id IS NULL)
                                ),
                            FOREIGN KEY (collectivite_id) REFERENCES collectivite(id)
);

/* tableau paiement-cotisation*/
CREATE TYPE mode_paiement_cotisation as enum ('espece', 'bancaire', 'mobile_money');
CREATE TABLE paiement_cotisation (
                                     id SERIAL PRIMARY KEY,
                                     membre_id INT NOT NULL,
                                     cotisation_id INT NOT NULL,
                                     montant INT NOT NULL,
                                     mode_paiement mode_paiement_cotisation NOT NULL,
                                     compte_id_recepteur INT NOT NULL ,
                                     reference_transaction VARCHAR(55),
                                     date_paiement TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     statut status_paiement not null ,
                                     FOREIGN KEY (membre_id) REFERENCES membre(id),
                                     FOREIGN KEY (cotisation_id) REFERENCES cotisation(id),
                                     FOREIGN KEY (compte_id_recepteur) REFERENCES compte(id)
);

/* creer la table pour gerer la paiement vers la federation,*/
CREATE TABLE paiement_federation (
                                     id SERIAL PRIMARY KEY,
                                     collectivite_id INT NOT NULL,
                                     federation_id INT NOT NULL,
                                     cotisation_id INT NOT NULL,
                                     montant_total INT NOT NULL,         -- total collecté
                                     mode_paiement mode_paiement_cotisation NOT NULL,
                                     reference_transaction TEXT UNIQUE,
                                     date_paiement TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     compte_source_id INT,
                                     compte_destination_id INT,
                                     FOREIGN KEY (collectivite_id) REFERENCES collectivite(id),
                                     FOREIGN KEY (federation_id) REFERENCES federation(id),
                                     FOREIGN KEY (cotisation_id) REFERENCES cotisation(id)FOREIGN KEY (compte_source_id) REFERENCES compte(id),
                                     FOREIGN KEY (compte_destination_id) REFERENCES compte(id),
                                     FOREIGN KEY (compte_source_id) REFERENCES compte(id)
);

CREATE TABLE role (
                      id SERIAL PRIMARY KEY,
                      nom TEXT UNIQUE NOT NULL,
                      est_unique BOOLEAN DEFAULT FALSE
);
CREATE TABLE mandat (
                        id SERIAL PRIMARY KEY,
                        collectivite_id INT,
                        federation_id INT,
                        date_debut DATE NOT NULL,
                        date_fin DATE NOT NULL,

                        CHECK (
                            (collectivite_id IS NOT NULL AND federation_id IS NULL)
                                OR
                            (collectivite_id IS NULL AND federation_id IS NOT NULL)
                            ),

                        FOREIGN KEY (collectivite_id) REFERENCES collectivite(id),
                        FOREIGN KEY (federation_id) REFERENCES federation(id)
);
CREATE TABLE affectation_poste (
                                   id SERIAL PRIMARY KEY,
                                   membre_id INT NOT NULL,
                                   role_id INT NOT NULL,
                                   mandat_id INT NOT NULL,

                                   FOREIGN KEY (membre_id) REFERENCES membre(id),
                                   FOREIGN KEY (role_id) REFERENCES role(id),
                                   FOREIGN KEY (mandat_id) REFERENCES mandat(id)
);

CREATE UNIQUE INDEX unique_poste_unique
    ON affectation_poste(role_id, mandat_id)
    WHERE role_id IN (
    SELECT id FROM role WHERE est_unique = TRUE
);

CREATE TABLE compte_bancaire (
                                 id            SERIAL PRIMARY KEY,
                                 compte_id     INT          NOT NULL UNIQUE REFERENCES compte(id),
                                 titulaire     VARCHAR(150) NOT NULL,
                                 banque        VARCHAR(50)  NOT NULL CHECK (banque IN (
                                                                                       'BRED','MCB','BMOI','BOA','BGFI',
                                                                                       'AFG','ACCES_BANQUE','BAOBAB','SIPEM'
                                     )),
                                 code_banque   CHAR(5)  NOT NULL,
                                 code_guichet  CHAR(5)  NOT NULL,
                                 numero_compte CHAR(11) NOT NULL,
                                 cle_rib       CHAR(2)  NOT NULL
);

CREATE TABLE compte_mobile_money (
                                     id        SERIAL PRIMARY KEY,
                                     compte_id INT          NOT NULL UNIQUE REFERENCES compte(id),
                                     titulaire VARCHAR(150) NOT NULL,
                                     service   VARCHAR(30)  NOT NULL CHECK (service IN (
                                                                                        'Orange Money', 'Mvola', 'Airtel Money'
                                         )),
                                     telephone VARCHAR(20)  NOT NULL UNIQUE
);

CREATE TABLE caisse (
                        id        SERIAL PRIMARY KEY,
                        compte_id INT          NOT NULL UNIQUE REFERENCES compte(id),
                        titulaire VARCHAR(150) NOT NULL
);

CREATE TABLE activite (
                          id              SERIAL PRIMARY KEY,
                          titre           VARCHAR(200) NOT NULL,
                          type            VARCHAR(30)  NOT NULL CHECK (type IN (
                                                                                'assemblee_generale', 'formation_junior',
                                                                                'exceptionnelle', 'federation'
                              )),
                          date_activite   DATE         NOT NULL,
                          obligatoire     BOOLEAN      NOT NULL DEFAULT TRUE,
                          cible           VARCHAR(50)  DEFAULT 'tous',
                          collectivite_id INT          REFERENCES collectivite(id),
                          federation_id   INT          REFERENCES federation(id)
);

CREATE TABLE presence (
                          id            SERIAL PRIMARY KEY,
                          activite_id   INT     NOT NULL REFERENCES activite(id),
                          membre_id     INT     NOT NULL REFERENCES membre(id),
                          present       BOOLEAN NOT NULL DEFAULT FALSE,
                          excusee       BOOLEAN NOT NULL DEFAULT FALSE,
                          motif_absence TEXT,
                          UNIQUE (activite_id, membre_id)
);