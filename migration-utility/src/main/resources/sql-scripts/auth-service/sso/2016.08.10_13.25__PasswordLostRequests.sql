CREATE TABLE lost_password_requests
(
  id serial NOT NULL,
  requested_for bigint,
  confirmation_token character varying(1024) NOT NULL,
  request_time timestamp without time zone NOT NULL,
  CONSTRAINT lost_password_requests_pkey PRIMARY KEY (id),
  CONSTRAINT lost_password_requests_id_fkey FOREIGN KEY (id)
      REFERENCES users (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);

CREATE INDEX lost_password_requests_requested_for_idx
  ON lost_password_requests
  USING btree
  (requested_for);

