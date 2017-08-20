CREATE TABLE oauth_states
(
  state character varying(255) NOT NULL,
  tenant_id character varying(64),
  CONSTRAINT oauth_states_pkey PRIMARY KEY (state)
)
WITH (
  OIDS=FALSE
);