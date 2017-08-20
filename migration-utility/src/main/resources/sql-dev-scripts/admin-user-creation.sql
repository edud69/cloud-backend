-- Create a user : admin with password asdfasdf for the application (ROLE_ADMIN)
-- assigned to the default tenant which is tenant master

-- 1. Make sure you run a first time the migration utility

-- 2. Create the user on authentication_master
INSERT INTO sso.users(
            account_non_expired, account_non_locked, credentials_non_expired,
            enabled, password, username)
    VALUES (true, true, true,
            true, '$2a$12$f6nWZ94igN7iWajCojqU.eoaIXrb9sbj2uGActFg/2IMPWbAUvRtC', 'admin')
            ON CONFLICT DO NOTHING;

-- 3. Gives your user admin rights on authentication_master
INSERT INTO tenantmaster.users_roles(
            user_id, role_id)
    VALUES ((select id from sso.users where username = 'admin'), (select id from tenantmaster.roles where role_name = 'ROLE_ADMIN'))
    ON CONFLICT DO NOTHING;

