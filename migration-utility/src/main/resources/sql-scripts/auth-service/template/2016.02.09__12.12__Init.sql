CREATE TABLE permissions
(
  id serial,
  permission_name character varying(255) NOT NULL,
  CONSTRAINT permission_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);

CREATE UNIQUE INDEX permissions_permission_name_idx
  ON permissions
  USING btree
  (permission_name COLLATE pg_catalog."default");

CREATE TABLE roles
(
  id serial,
  role_name character varying(255) NOT NULL,
  CONSTRAINT roles_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);

CREATE UNIQUE INDEX roles_role_name_idx
  ON roles
  USING btree
  (role_name COLLATE pg_catalog."default");

CREATE TABLE roles_permissions
(
  role_id bigint NOT NULL,
  permission_id bigint NOT NULL,
  CONSTRAINT roles_privileges_pkey PRIMARY KEY (role_id, permission_id),
  CONSTRAINT fk_roles_permissions_role_id FOREIGN KEY (role_id)
      REFERENCES roles (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_roles_permissions_permission_id FOREIGN KEY (permission_id)
      REFERENCES permissions (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);


CREATE TABLE users_roles
(
  user_id bigint NOT NULL,
  role_id bigint NOT NULL,
  id serial NOT NULL,
  CONSTRAINT users_roles_pkey PRIMARY KEY (id),
  CONSTRAINT fk_users_role_id FOREIGN KEY (role_id)
      REFERENCES roles (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);

CREATE UNIQUE INDEX users_roles_user_id_idx
  ON users_roles
  USING btree
  (user_id);

  
  CREATE TABLE users_subscriptions
(
  id serial NOT NULL,
  request_time timestamp without time zone NOT NULL,
  status character varying(64) NOT NULL,
  user_id bigint NOT NULL,
  CONSTRAINT users_subscriptions_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);

-- Index: template.users_subscriptions_user_id_idx

-- DROP INDEX template.users_subscriptions_user_id_idx;

CREATE UNIQUE INDEX users_subscriptions_user_id_idx
  ON users_subscriptions
  USING btree
  (user_id);
  

  
  
  
  CREATE TABLE users_subscriptions_invitations
(
  id serial NOT NULL,
  subject character varying(255) NOT NULL,
  sender character varying(255) NOT NULL,
  recipients text NOT NULL,
  ccs text,
  use_html_body boolean,
  body text NOT NULL,
  sent_time timestamp without time zone,
  CONSTRAINT users_subscriptions_invitations_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);

ALTER TABLE users_subscriptions_invitations ADD COLUMN subscription_id bigint;
ALTER TABLE users_subscriptions_invitations ALTER COLUMN subscription_id SET NOT NULL;

ALTER TABLE users_subscriptions_invitations
  ADD CONSTRAINT fk_users_subscriptions_id FOREIGN KEY (subscription_id)
      REFERENCES users_subscriptions (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
  
  
  
ALTER TABLE users_subscriptions ADD COLUMN confirmation_token character varying(1024);
ALTER TABLE users_subscriptions ALTER COLUMN confirmation_token SET NOT NULL;

