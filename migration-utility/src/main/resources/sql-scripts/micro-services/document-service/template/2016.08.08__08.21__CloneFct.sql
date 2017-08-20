CREATE OR REPLACE FUNCTION clone_schema(source_schema text, dest_schema text)
  RETURNS integer AS
$BODY$

DECLARE
  object text;
  buffer text;
  default_ text;
  column_ text;
  source text;
  _query text;
  _view text;
BEGIN
  EXECUTE 'CREATE SCHEMA ' || dest_schema ;

  FOR object IN
    SELECT sequence_name::text FROM information_schema.SEQUENCES WHERE sequence_schema = source_schema
  LOOP
    EXECUTE 'CREATE SEQUENCE ' || dest_schema || '.' || object;
  END LOOP;


  FOR object IN
    SELECT TABLE_NAME::text FROM information_schema.TABLES WHERE table_schema = source_schema AND table_type = 'BASE TABLE'
  LOOP

    buffer := dest_schema || '.' || object;
    source := source_schema || '.' || object;
    EXECUTE 'CREATE TABLE ' || buffer || ' (LIKE ' || source_schema || '.' || object || ' INCLUDING ALL)';

    FOR column_, default_ IN
      SELECT column_name::text, REPLACE(column_default::text, source_schema, dest_schema) FROM information_schema.COLUMNS WHERE table_schema = dest_schema AND TABLE_NAME = object AND column_default LIKE 'nextval(%' || source_schema || '%::regclass)'
    LOOP
      EXECUTE 'ALTER TABLE ' || buffer || ' ALTER COLUMN ' || column_ || ' SET DEFAULT ' || default_;
    END LOOP;

  END LOOP;

    FOR object IN
    SELECT TABLE_NAME::text FROM information_schema.TABLES WHERE table_schema = source_schema AND table_type = 'BASE TABLE'
  LOOP

    buffer := dest_schema || '.' || object;
    source := source_schema || '.' || object;
       for _query in
        select
            format (
            'alter table %s add constraint %s %s',
            buffer,
            replace(conname, source, buffer),
            replace(pg_get_constraintdef(oid), source_schema, dest_schema))
        from pg_constraint
        where contype = 'f' and conrelid = source::regclass
        loop
        execute _query;
        end loop;

  END LOOP;

   FOR object IN
    SELECT table_name::text FROM information_schema.TABLES WHERE table_schema = source_schema AND table_type = 'VIEW'
  LOOP
    EXECUTE 'select pg_get_viewdef(''' || source_schema || '.' || object || ''', true)' into _view;
    EXECUTE 'CREATE OR REPLACE VIEW ' || dest_schema || '.' || object || ' AS ' || replace(_view, source_schema, dest_schema);
  END LOOP;



   FOR object IN
    SELECT routine_name::text FROM information_schema.ROUTINES WHERE routine_schema = source_schema AND routine_type = 'FUNCTION'
  LOOP
    EXECUTE 'select pg_get_functiondef(pg_proc.oid) from pg_proc, pg_namespace where pg_proc.proname = ''' || object || ''' and pg_namespace.oid = pg_proc.pronamespace and pg_namespace.nspname = ''' || source_schema || '''' into _view;
    EXECUTE replace(_view, source_schema, dest_schema);
  END LOOP;


  FOR object IN
    SELECT t.oid::text FROM pg_trigger t
    WHERE t.tgname not ilike '%ConstraintTrigger%' AND tgrelid::regclass::text like source_schema||'.%'
  LOOP
    EXECUTE 'select pg_get_triggerdef(pg_trigger.oid) from pg_trigger, pg_namespace where pg_trigger.oid::text = ''' || object || ''' and pg_namespace.nspname = ''' || source_schema || '''' into _view;
    EXECUTE replace(_view, source_schema, dest_schema);
  END LOOP;

  EXECUTE 'INSERT INTO ' || dest_schema || '.schema_version (select * from ' || source_schema || '.schema_version)';

  return 0;
END;

$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;