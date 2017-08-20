ALTER TABLE users_roles DROP CONSTRAINT users_roles_pkey;

ALTER TABLE users_roles DROP COLUMN id;
ALTER TABLE users_roles ADD COLUMN id serial;
ALTER TABLE users_roles ALTER COLUMN id SET NOT NULL;

ALTER TABLE users_roles
  ADD CONSTRAINT users_roles_pkey PRIMARY KEY(id);