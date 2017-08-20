CREATE TABLE document_templates
(
  id serial NOT NULL,
  template_key character varying(64) NOT NULL,
  classification character varying(64) NOT NULL,
  CONSTRAINT document_templates_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);

-- Index: document_templates_template_key_classification_idx

-- DROP INDEX document_templates_template_key_classification_idx;

CREATE UNIQUE INDEX document_templates_template_key_classification_idx
  ON document_templates
  USING btree
  (template_key COLLATE pg_catalog."default", classification COLLATE pg_catalog."default");


CREATE TABLE email_document_templates
(
  id bigint NOT NULL,
  subject character varying(255) NOT NULL,
  body text NOT NULL,
  CONSTRAINT fk_document_templates_id FOREIGN KEY (id)
      REFERENCES document_templates (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);

-- Index: fki_document_templates_id

-- DROP INDEX fki_document_templates_id;

CREATE INDEX fki_document_templates_id
  ON email_document_templates
  USING btree
  (id);

ALTER TABLE email_document_templates
  ADD CONSTRAINT email_document_templates_pkey PRIMARY KEY(id);