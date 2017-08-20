CREATE TABLE accounts
(
  id serial,
  user_id INTEGER NOT NULL CONSTRAINT account_unique_user_id UNIQUE,
  first_name character varying(64) NOT NULL,
  last_name character varying(64) NOT NULL,
  gender character varying(16) NOT NULL default 'NOT_KNOWN',
  birthday date,
  CONSTRAINT accounts_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
