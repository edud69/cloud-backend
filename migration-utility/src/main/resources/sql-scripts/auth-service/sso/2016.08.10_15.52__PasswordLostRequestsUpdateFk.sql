ALTER TABLE lost_password_requests DROP CONSTRAINT lost_password_requests_id_fkey;

ALTER TABLE lost_password_requests
  ADD CONSTRAINT lost_password_requests_id_fkey FOREIGN KEY (requested_for)
      REFERENCES users (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;