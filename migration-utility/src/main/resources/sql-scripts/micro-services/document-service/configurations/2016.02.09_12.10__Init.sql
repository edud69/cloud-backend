CREATE TABLE tenant_datasources
(
  tenant_id character varying(64) NOT NULL,
  url character varying(1024),
  username character varying(255),
  password character varying(255),
  driver_class character varying(255),
  max_wait integer,
  max_active integer,
  initial_size integer,
  max_idle integer,
  min_idle integer,
  CONSTRAINT tenant_datasources_pkey PRIMARY KEY (tenant_id)
)
WITH (
  OIDS=FALSE
);