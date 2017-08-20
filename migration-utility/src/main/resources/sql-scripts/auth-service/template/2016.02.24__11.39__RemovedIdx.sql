DROP INDEX users_roles_user_id_idx;

CREATE INDEX users_roles_user_id_idx
  ON users_roles
  USING btree
  (user_id);