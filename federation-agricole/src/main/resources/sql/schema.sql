CREATE TABLE ville (
                       id     SERIAL PRIMARY KEY,
                       nom    VARCHAR(100) NOT NULL,
                       region VARCHAR(100)
);

CREATE TABLE federation (
                            id               SERIAL PRIMARY KEY,
                            nom              VARCHAR(150) NOT NULL,
                            mandat_duree_ans INT NOT NULL DEFAULT 2
);

CREATE TABLE collectivite (
                              id                  SERIAL PRIMARY KEY,
                              numero              VARCHAR(20)  NOT NULL UNIQUE,
                              nom                 VARCHAR(150) NOT NULL UNIQUE,
                              specialite_agricole VARCHAR(100) NOT NULL,
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

CREATE TABLE compte (
                        id         SERIAL PRIMARY KEY,
                        type       VARCHAR(20)   NOT NULL CHECK (type IN ('bancaire', 'mobile_money', 'caisse')),
                        solde      DECIMAL(15,2) NOT NULL DEFAULT 0,
                        date_solde DATE          NOT NULL,
                        personne_id INT          REFERENCES personne(id)
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

CREATE TABLE paiement_ad (
                             id                    SERIAL PRIMARY KEY,
                             montant               DECIMAL(15,2) NOT NULL DEFAULT 50000,
                             personne_id           INT           NOT NULL REFERENCES personne(id),
                             compte_id             INT           NOT NULL REFERENCES compte(id),
                             mode_paiement         VARCHAR(20)   NOT NULL CHECK (mode_paiement IN (
                                                                                                   'mobile_money', 'virement_bancaire'
                                 )),
                             reference_transaction VARCHAR(100)  NOT NULL UNIQUE,
                             date_paiement         DATE          NOT NULL,
                             statut                VARCHAR(20)   NOT NULL CHECK (statut IN (
                                                                                            'en_cours', 'refuse', 'valide'
                                 )),
                             CHECK (montant = 50000)
);

CREATE TABLE adhesion (
                          id              SERIAL PRIMARY KEY,
                          date_adhesion   DATE        NOT NULL,
                          paiement_ad_id  INT         NOT NULL UNIQUE REFERENCES paiement_ad(id),
                          collectivite_id INT         NOT NULL REFERENCES collectivite(id),
                          statut          VARCHAR(20) NOT NULL CHECK (statut IN (
                                                                                 'accepte', 'en_attente', 'rejete'
                              ))
);

CREATE TABLE membre (
                        id          SERIAL PRIMARY KEY,
                        adhesion_id INT NOT NULL UNIQUE REFERENCES adhesion(id),
                        parrain_id  INT REFERENCES membre(id)
);

CREATE TABLE mandat (
                        id              SERIAL PRIMARY KEY,
                        collectivite_id INT         NOT NULL REFERENCES collectivite(id),
                        membre_id       INT         NOT NULL REFERENCES membre(id),
                        poste           VARCHAR(50) NOT NULL CHECK (poste IN (
                                                                              'president', 'president_adjoint', 'tresorier',
                                                                              'secretaire', 'membre_confirme', 'membre_junior'
                            )),
                        annee_civile    INT         NOT NULL,
                        UNIQUE (collectivite_id, poste, annee_civile)
);

CREATE TABLE mandat_federation (
                                   id           SERIAL PRIMARY KEY,
                                   membre_id    INT         NOT NULL REFERENCES membre(id),
                                   poste        VARCHAR(50) NOT NULL CHECK (poste IN (
                                                                                      'president', 'president_adjoint', 'tresorier', 'secretaire'
                                       )),
                                   annee_debut  INT         NOT NULL,
                                   annee_fin    INT         NOT NULL
);

CREATE TABLE autorisation_ouverture (
                                        id                SERIAL PRIMARY KEY,
                                        collectivite_id   INT         NOT NULL UNIQUE REFERENCES collectivite(id),
                                        date_autorisation DATE        NOT NULL,
                                        statut            VARCHAR(20) NOT NULL CHECK (statut IN (
                                                                                                 'accordee', 'refusee', 'en_attente'
                                            ))
);

CREATE TABLE compte_institutionnel (
                                       id              SERIAL PRIMARY KEY,
                                       type            VARCHAR(20)   NOT NULL CHECK (type IN ('caisse', 'bancaire', 'mobile_money')),
                                       solde           DECIMAL(15,2) NOT NULL DEFAULT 0,
                                       date_solde      DATE          NOT NULL,
                                       collectivite_id INT           REFERENCES collectivite(id),
                                       federation_id   INT           REFERENCES federation(id),
                                       CHECK (
                                           (collectivite_id IS NOT NULL AND federation_id IS NULL) OR
                                           (federation_id IS NOT NULL AND collectivite_id IS NULL)
                                           )
);

CREATE TABLE paiement (
                          id                   SERIAL PRIMARY KEY,
                          compte_id_envoyeur   INT           NOT NULL REFERENCES compte(id),
                          compte_id_recepteur  INT           NOT NULL REFERENCES compte_institutionnel(id),
                          montant              DECIMAL(15,2) NOT NULL,
                          date_paiement        DATE          NOT NULL
);

CREATE TABLE cotisation (
                            id              SERIAL PRIMARY KEY,
                            membre_id       INT           NOT NULL REFERENCES membre(id),
                            collectivite_id INT           NOT NULL REFERENCES collectivite(id),
                            periodicite     VARCHAR(20)   NOT NULL CHECK (periodicite IN (
                                                                                          'mensuelle', 'annuelle', 'ponctuelle'
                                )),
                            montant         DECIMAL(15,2) NOT NULL,
                            date_echeance   DATE          NOT NULL
);

CREATE TABLE compte_bancaire (
                                 id           SERIAL PRIMARY KEY,
                                 compte_id    INT          NOT NULL UNIQUE REFERENCES compte(id),
                                 titulaire    VARCHAR(150) NOT NULL,
                                 banque       VARCHAR(50)  NOT NULL CHECK (banque IN (
                                                                                      'BRED','MCB','BMOI','BOA','BGFI',
                                                                                      'AFG','ACCES_BANQUE','BAOBAB','SIPEM'
                                     )),
                                 code_banque  CHAR(5)  NOT NULL,
                                 code_guichet CHAR(5)  NOT NULL,
                                 numero_compte CHAR(11) NOT NULL,
                                 cle_rib      CHAR(2)  NOT NULL
);

CREATE TABLE compte_mobile_money (
                                     id         SERIAL PRIMARY KEY,
                                     compte_id  INT         NOT NULL UNIQUE REFERENCES compte(id),
                                     titulaire  VARCHAR(150) NOT NULL,
                                     service    VARCHAR(30)  NOT NULL CHECK (service IN (
                                                                                         'Orange Money', 'Mvola', 'Airtel Money'
                                         )),
                                     telephone  VARCHAR(20)  NOT NULL UNIQUE
);

CREATE TABLE cotisation (
                            id              SERIAL PRIMARY KEY,
                            membre_id       INT           NOT NULL REFERENCES membre(id),
                            collectivite_id INT           NOT NULL REFERENCES collectivite(id),
                            periodicite     VARCHAR(20)   NOT NULL CHECK (periodicite IN (
                                                                                          'mensuelle', 'annuelle', 'ponctuelle'
                                )),
                            montant         DECIMAL(15,2) NOT NULL,
                            date_echeance   DATE          NOT NULL
);

CREATE TABLE encaissement (
                              id                    SERIAL PRIMARY KEY,
                              cotisation_id         INT           NOT NULL REFERENCES cotisation(id),
                              compte_institutionnel_id INT        NOT NULL REFERENCES compte_institutionnel(id),
                              paiement_id           INT           NOT NULL REFERENCES paiement(id),
                              montant               DECIMAL(15,2) NOT NULL,
                              date_encaissement     DATE          NOT NULL,
                              mode_paiement         VARCHAR(20)   NOT NULL CHECK (mode_paiement IN (
                                                                                                    'espece', 'virement_bancaire', 'mobile_money'
                                  ))
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