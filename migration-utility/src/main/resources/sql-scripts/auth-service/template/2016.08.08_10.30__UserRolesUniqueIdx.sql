ALTER TABLE users_roles
  ADD CONSTRAINT users_roles_user_id_role_id_key UNIQUE(user_id, role_id);