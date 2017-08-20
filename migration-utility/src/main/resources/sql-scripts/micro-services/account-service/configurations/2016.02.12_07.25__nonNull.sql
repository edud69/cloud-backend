ALTER TABLE tenant_datasources ALTER COLUMN url SET NOT NULL;
ALTER TABLE tenant_datasources ALTER COLUMN username SET NOT NULL;
ALTER TABLE tenant_datasources ALTER COLUMN password SET NOT NULL;
ALTER TABLE tenant_datasources ALTER COLUMN driver_class SET NOT NULL;