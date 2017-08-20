CREATE TABLE users
(
  id serial,
  account_non_expired boolean,
  account_non_locked boolean,
  credentials_non_expired boolean,
  enabled boolean,
  password character varying(255) NOT NULL,
  username character varying(255) NOT NULL,
  CONSTRAINT users_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);

CREATE UNIQUE INDEX users_username_idx
  ON sso.users
  USING btree
  (username COLLATE pg_catalog."default");