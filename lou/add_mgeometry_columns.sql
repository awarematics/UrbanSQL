
CREATE OR REPLACE FUNCTION public.addmgeometrycolumn(
	character varying,
	character varying,
	character varying,
	integer,
	character varying,
	integer,
	integer)
    RETURNS text
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE STRICT 
AS $BODY$
DECLARE
    f_schema_name     alias for $1;
    f_table_name     alias for $2;
    f_column_name     alias for $3;
    srid        alias for $4;
    new_type     alias for $5;
    dimension     alias for $6;	
    tpseg_size    alias for $7;
    real_schema name;
    sql text;
    table_oid text;
    temp_segtable_name text;
    f_mgeometry_segtable_name text;
    f_sequence_name    text;
    f_segtable_oid    oid;
BEGIN
    --verify SRID
    IF ( f_schema_name IS NOT NULL AND f_schema_name != '' ) THEN
        sql := 'SELECT nspname FROM pg_namespace ' ||
            'WHERE text(nspname) = ' || quote_literal(f_schema_name) ||
            'LIMIT 1';
        RAISE DEBUG '%', sql;
        EXECUTE sql INTO real_schema;

        IF ( real_schema IS NULL ) THEN
            RAISE EXCEPTION 'Schema % is not a valid schemaname', quote_literal(f_schema_name);
            RETURN 'fail';
        END IF;
    END IF;

    IF ( real_schema IS NULL ) THEN
        RAISE DEBUG 'Detecting schema';
        sql := 'SELECT n.nspname AS schemaname ' ||
            'FROM pg_catalog.pg_class c ' ||
              'JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace ' ||
            'WHERE c.relkind = ' || quote_literal('r') ||
            ' AND n.nspname NOT IN (' || quote_literal('pg_catalog') || ', ' || quote_literal('pg_toast') || ')' ||
            ' AND pg_catalog.pg_table_is_visible(c.oid)' ||
            ' AND c.relname = ' || quote_literal(f_table_name);
        RAISE DEBUG '%', sql;
        EXECUTE sql INTO real_schema;

        IF ( real_schema IS NULL ) THEN
            RAISE EXCEPTION 'Table % does not occur in the search_path', quote_literal(f_table_name);
            RETURN 'fail';
        END IF;
    END IF;

    sql := 'select '|| quote_literal(f_table_name) ||'::regclass::oid';
    RAISE DEBUG '%', sql;
    EXECUTE sql INTO table_oid;
-------------------------------------------mpoint	
 	IF (new_type = 'mpoint')
    THEN       
    	f_sequence_name = quote_ident(f_table_name) || '_' || quote_ident(f_column_name) || '_mpointid_seq';

    	sql := 'CREATE SEQUENCE ' || quote_ident(f_sequence_name) || ' START 1';
   	 	RAISE DEBUG '%', sql;
   	 	EXECUTE sql;

    	-- Add trajectory column to table
    	sql := 'ALTER TABLE ' || quote_ident(f_table_name) || 
        	' ADD ' || quote_ident(f_column_name) || ' mgeometry';
   		RAISE DEBUG '%', sql;
    	RAISE INFO '%', sql;
   		EXECUTE sql;    

    	-- Delete stale record in geometry_columns (if any)
   		sql := 'DELETE FROM mgeometry_columns WHERE
			f_table_name = ' || quote_literal(f_table_name) ||
        	' AND f_mgeometry_column = ' || quote_literal(f_column_name);
    	RAISE DEBUG '%', sql;
    	EXECUTE sql;

    	sql := 'DELETE FROM mgeometry_columns WHERE
       	 f_table_catalog = ' || quote_literal('') ||
       	 ' AND f_table_schema = ' ||quote_literal(real_schema) ||
       	 ' AND f_table_name = ' || quote_literal(f_table_name) ||
       	 ' AND f_mgeometry_column = ' || quote_literal(f_column_name);
   		RAISE DEBUG '%', sql;
   		EXECUTE sql;
   	 	temp_segtable_name := 'mpoint_' || table_oid || '_' || f_column_name;
	
    	EXECUTE 'CREATE TABLE ' || temp_segtable_name || ' 
        (
            mpid        integer primary key,
            segid       integer,
			mbr			geometry,
			timerange		int8range,
            datetimes    	bigint[],
            geo        point[]
        )';
    	sql := 'select '|| quote_literal(temp_segtable_name) ||'::regclass::oid';
   		RAISE DEBUG '%', sql;
    	EXECUTE sql INTO f_segtable_oid;
   		-- segment table name
    	f_mgeometry_segtable_name := 'mpoint_' || f_segtable_oid ;   
   	 	EXECUTE 'ALTER TABLE ' || quote_ident(temp_segtable_name) || ' RENAME TO ' || quote_ident(f_mgeometry_segtable_name);
		-----index
		sql := ' create index IF NOT EXISTS bl_index_'|| (f_segtable_oid) ||'mpid on '|| quote_ident(f_mgeometry_segtable_name) ||' using btree (mpid) tablespace pg_default ;';
    	EXECUTE sql;
		sql := ' create index IF NOT EXISTS bl_index_'|| (f_segtable_oid) ||'segid on '|| quote_ident(f_mgeometry_segtable_name) ||' using btree (segid) tablespace pg_default ;';
    	EXECUTE sql;
		sql := ' create index IF NOT EXISTS bl_index_'|| (f_segtable_oid) ||'mbr on '|| quote_ident(f_mgeometry_segtable_name) ||' using gist (mbr) tablespace pg_default ;';
    	EXECUTE sql;
		sql := ' create index IF NOT EXISTS bl_index_'|| (f_segtable_oid) ||'imerange on '|| quote_ident(f_mgeometry_segtable_name) ||' using gist (timerange) tablespace pg_default ;';
    	EXECUTE sql;
		sql := ' create index IF NOT EXISTS bl_index_'|| (f_segtable_oid) ||'datetimes on '|| quote_ident(f_mgeometry_segtable_name) ||' using btree (datetimes) tablespace pg_default ;';
    	EXECUTE sql;
		sql := ' create index IF NOT EXISTS bl_index_'|| (f_segtable_oid) ||'geo on '|| quote_ident(f_mgeometry_segtable_name) ||' using gist(ST_MakeLine(geo::geometry[])) tablespace pg_default ;';
    	EXECUTE sql;
	
	
    	-- Add record in geometry_columns 
    	sql := 'INSERT INTO mgeometry_columns (f_table_catalog, f_table_schema, f_table_name, ' ||
            'f_mgeometry_column, f_mgeometry_segtable_name, coord_dimension, srid, type, '|| 
            'f_segtableoid, f_sequence_name, tpseg_size)' ||
        	' VALUES (' ||
       	 	quote_literal('') || ',' ||
        	quote_literal(real_schema) || ',' ||
        	quote_literal(f_table_name) || ',' ||
        	quote_literal(f_column_name) || ',' ||
        	quote_literal(f_mgeometry_segtable_name) || ',' || 
        	dimension::text || ',' ||
        	srid::text || ',' ||
        	quote_literal(new_type) || ', ' ||
        	quote_literal(f_segtable_oid) || ', ' ||
        	quote_literal(f_sequence_name) || ', ' ||
        	tpseg_size || ')';
    	RAISE DEBUG '%', sql;
    	EXECUTE sql;

    	sql := 'UPDATE ' || quote_ident(f_table_name)|| ' SET ' || quote_ident(f_column_name) || '.moid '
     	|| '= NEXTVAL(' || quote_literal(f_sequence_name) ||'), ' || quote_ident(f_column_name) || '.segid = ' || f_segtable_oid;
   		-- sql := 'UPDATE ' || quote_ident(f_table_name)|| ' SET ' || quote_ident(f_column_name) || '.id = NEXTVAL(' || quote_literal(f_sequence_name) ||')';
   		 RAISE DEBUG '%', sql;
   		 EXECUTE sql;
		------trigger for insert and delete 
		EXECUTE 'CREATE TRIGGER insert_mpoint 
		BEFORE INSERT ON ' || quote_ident(f_table_name) || ' FOR EACH ROW EXECUTE PROCEDURE insert_mpoint()';
	
		EXECUTE 'CREATE TRIGGER delete_mpoint 
		AFTER DELETE ON ' || quote_ident(f_table_name) || ' FOR EACH ROW EXECUTE PROCEDURE delete_mpoint()';
    END IF;	
	------------------------------------------------mdouble
	------------------------------------------------mperiod
	------------------------------------------------mduration
	------------------------------------------------minstant
	------------------------------------------------mint
	------------------------------------------------mbool
	------------------------------------------------mstring
	------------------------------------------------mlinestring
	------------------------------------------------mpolygon
	------------------------------------------------mvideo
	IF (new_type = 'mvideo')
    THEN       
    	f_sequence_name = quote_ident(f_table_name) || '_' || quote_ident(f_column_name) || '_mvideoid_seq';

    	sql := 'CREATE SEQUENCE ' || quote_ident(f_sequence_name) || ' START 1';
   	 	RAISE DEBUG '%', sql;
   	 	EXECUTE sql;

    	-- Add trajectory column to table
    	sql := 'ALTER TABLE ' || quote_ident(f_table_name) || 
        	' ADD ' || quote_ident(f_column_name) || ' mgeometry';
   		RAISE DEBUG '%', sql;
    	RAISE INFO '%', sql;
   		EXECUTE sql;    

    	-- Delete stale record in geometry_columns (if any)
   		sql := 'DELETE FROM mgeometry_columns WHERE
			f_table_name = ' || quote_literal(f_table_name) ||
        	' AND f_mgeometry_column = ' || quote_literal(f_column_name);
    	RAISE DEBUG '%', sql;
    	EXECUTE sql;

    	sql := 'DELETE FROM mgeometry_columns WHERE
       	 f_table_catalog = ' || quote_literal('') ||
       	 ' AND f_table_schema = ' ||quote_literal(real_schema) ||
       	 ' AND f_table_name = ' || quote_literal(f_table_name) ||
       	 ' AND f_mgeometry_column = ' || quote_literal(f_column_name);
   		RAISE DEBUG '%', sql;
   		EXECUTE sql;
   	 	temp_segtable_name := 'mvideo_' || table_oid || '_' || f_column_name;
	RAISE INFO '%', temp_segtable_name;
    	EXECUTE 'CREATE TABLE ' || temp_segtable_name || ' 
        (
            mpid        integer primary key,
            segid        integer,
			mbr			geometry,
			timerange		int8range,
			fovs			fov[],
			horizontalAngle double precision[],
			verticalAngle double precision[],
			direction2d double precision[],
			direction3d double precision[],
			distance double precision[],
			uri			character varying[],
            datetimes    	bigint[],
            geo        point[]
        )';
    	sql := 'select '|| quote_literal(temp_segtable_name) ||'::regclass::oid';
    	EXECUTE sql INTO f_segtable_oid;
					-- segment table name
    	f_mgeometry_segtable_name := 'mvideo_' || f_segtable_oid ;   
   	 	EXECUTE 'ALTER TABLE ' || quote_ident(temp_segtable_name) || ' RENAME TO ' || quote_ident(f_mgeometry_segtable_name);
		-----index
		sql := ' create index IF NOT EXISTS bl_index_'|| (f_segtable_oid) ||'mpid on '|| quote_ident(f_mgeometry_segtable_name) ||' using btree (mpid) tablespace pg_default ;';
    	EXECUTE sql;
		sql := ' create index IF NOT EXISTS bl_index_'|| (f_segtable_oid) ||'segid on '|| quote_ident(f_mgeometry_segtable_name) ||' using btree (segid) tablespace pg_default ;';
    	EXECUTE sql;
		sql := ' create index IF NOT EXISTS bl_index_'|| (f_segtable_oid) ||'mbr on '|| quote_ident(f_mgeometry_segtable_name) ||' using gist (mbr) tablespace pg_default ;';
    	EXECUTE sql;
		sql := ' create index IF NOT EXISTS bl_index_'|| (f_segtable_oid) ||'timerange on '|| quote_ident(f_mgeometry_segtable_name) ||' using gist (timerange) tablespace pg_default ;';
    	EXECUTE sql;
		sql := ' create index IF NOT EXISTS bl_index_'|| (f_segtable_oid) ||'horizontalAngle on '|| quote_ident(f_mgeometry_segtable_name) ||' using btree (horizontalAngle) tablespace pg_default ;';
    	EXECUTE sql;
		sql := ' create index IF NOT EXISTS bl_index_'|| (f_segtable_oid) ||'verticalAngle on '|| quote_ident(f_mgeometry_segtable_name) ||' using btree (verticalAngle) tablespace pg_default ;';
    	EXECUTE sql;
		sql := ' create index IF NOT EXISTS bl_index_'|| (f_segtable_oid) ||'direction2d on '|| quote_ident(f_mgeometry_segtable_name) ||' using btree (direction2d) tablespace pg_default ;';
    	EXECUTE sql;
		sql := ' create index IF NOT EXISTS bl_index_'|| (f_segtable_oid) ||'direction3d on '|| quote_ident(f_mgeometry_segtable_name) ||' using btree (direction3d) tablespace pg_default ;';
    	EXECUTE sql;
		sql := ' create index IF NOT EXISTS bl_index_'|| (f_segtable_oid) ||'distance on '|| quote_ident(f_mgeometry_segtable_name) ||' using btree (distance) tablespace pg_default ;';
    	EXECUTE sql;
		sql := ' create index IF NOT EXISTS bl_index_'|| (f_segtable_oid) ||'uri on '|| quote_ident(f_mgeometry_segtable_name) ||' using btree (uri) tablespace pg_default ;';
    	EXECUTE sql;
		sql := ' create index IF NOT EXISTS bl_index_'|| (f_segtable_oid) ||'datetimes on '|| quote_ident(f_mgeometry_segtable_name) ||' using btree (datetimes) tablespace pg_default ;';
    	EXECUTE sql;
		sql := ' create index IF NOT EXISTS bl_index_'|| (f_segtable_oid) ||'geo on '|| quote_ident(f_mgeometry_segtable_name) ||' using gist(ST_MakeLine(geo::geometry[])) tablespace pg_default ;';
    	EXECUTE sql;
		
   	
	
    	-- Add record in mgeometry_columns 
    	sql := 'INSERT INTO mgeometry_columns (f_table_catalog, f_table_schema, f_table_name, ' ||
            'f_mgeometry_column, f_mgeometry_segtable_name, coord_dimension, srid, type, '|| 
            'f_segtableoid, f_sequence_name, tpseg_size)' ||
        	' VALUES (' ||
       	 	quote_literal('') || ',' || 
        	quote_literal(real_schema) || ',' ||
        	quote_literal(f_table_name) || ',' ||
        	quote_literal(f_column_name) || ',' ||
        	quote_literal(f_mgeometry_segtable_name) || ',' || 
        	dimension::text || ',' ||
        	srid::text || ',' ||
        	quote_literal(new_type) || ', ' ||
        	quote_literal(f_segtable_oid) || ', ' ||
        	quote_literal(f_sequence_name) || ', ' ||
        	tpseg_size || ')';
    	RAISE DEBUG '%', sql;
    	EXECUTE sql;
    	sql := 'UPDATE ' || quote_ident(f_table_name)|| ' SET ' || quote_ident(f_column_name) || '.moid '
     	|| '= NEXTVAL(' || quote_literal(f_sequence_name) ||'), ' || quote_ident(f_column_name) || '.segid = ' || f_segtable_oid;
   		-- sql := 'UPDATE ' || quote_ident(f_table_name)|| ' SET ' || quote_ident(f_column_name) || '.id = NEXTVAL(' || quote_literal(f_sequence_name) ||')';
   		 RAISE DEBUG '%', sql;
   		 EXECUTE sql;
		 
		 ------trigger for insert and delete 
		EXECUTE 'CREATE TRIGGER insert_video 
		BEFORE INSERT ON ' || quote_ident(f_table_name) || ' FOR EACH ROW EXECUTE PROCEDURE insert_video()';
	
		EXECUTE 'CREATE TRIGGER delete_video 
		AFTER DELETE ON ' || quote_ident(f_table_name) || ' FOR EACH ROW EXECUTE PROCEDURE delete_video()';
	 END IF;	

    RETURN
        real_schema || '.' ||
        f_table_name || '.' || f_column_name ||
        ' SRID:' || srid::text ||
        ' TYPE:' || new_type ||
        ' DIMS:' || dimension::text || ' ';
END;
$BODY$;

ALTER FUNCTION public.addmgeometrycolumn(character varying, character varying, character varying, integer, character varying, integer, integer)
    OWNER TO postgres;
	

	
CREATE OR REPLACE FUNCTION delete_video() RETURNS trigger AS $delete_mvideo$
DECLARE		
	delete_trajectory	mgeometry;
	delete_id		integer;
	records			record;
	delete_record		record;

    BEGIN
	execute 'select f_mgeometry_segtable_name, f_mgeometry_column from mgeometry_columns where f_table_name = ' || quote_literal(TG_RELNAME)
	into records;
	delete_record := OLD;

	delete_trajectory := OLD.mvideo;
	delete_id := delete_trajectory.moid;	
	execute 'DELETE FROM ' || quote_ident(records.f_mgeometry_segtable_name) || ' WHERE mpid = ' || delete_id;
	return NULL;
    END;
$delete_mvideo$ LANGUAGE plpgsql;





CREATE OR REPLACE FUNCTION public.insert_video()
  RETURNS trigger AS
$BODY$
DECLARE	
	segtable_oid		text;
	segcolumn_name		text;
	sequence_name		text;
	moid			text;
	
	sql_text		text;
	records			record;
	
 BEGIN	
	sql_text := 'select f_segtableoid, f_mgeometry_column, f_sequence_name from mgeometry_columns where f_table_name = ' || quote_literal(TG_RELNAME);
	execute sql_text into records;
	
	segtable_oid := records.f_segtableoid;
	segcolumn_name := records.f_mgeometry_column;
	sequence_name := records.f_sequence_name;

	sql_text := 'select nextval(' || quote_literal(sequence_name) || ')';
	execute sql_text into moid;
	NEW.mvideo = (moid,segtable_oid);		
	return NEW;
END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
	

	
	
CREATE OR REPLACE FUNCTION delete_mpoint() RETURNS trigger AS $delete_mpoint$
DECLARE		
	delete_trajectory	mgeometry;
	delete_id		integer;
	records			record;
	delete_record		record;

    BEGIN
	execute 'select f_mgeometry_segtable_name, f_mgeometry_column from mgeometry_columns where f_table_name = ' || quote_literal(TG_RELNAME)
	into records;
	delete_record := OLD;

	delete_trajectory := OLD.mpoint;
	delete_id := delete_trajectory.moid;	
	execute 'DELETE FROM ' || quote_ident(records.f_mgeometry_segtable_name) || ' WHERE mpid = ' || delete_id;
	return NULL;
    END;
$delete_mpoint$ LANGUAGE plpgsql;





CREATE OR REPLACE FUNCTION public.insert_mpoint()
  RETURNS trigger AS
$BODY$
DECLARE	
	segtable_oid		text;
	segcolumn_name		text;
	sequence_name		text;
	moid			text;
	
	sql_text		text;
	records			record;
	
 BEGIN	
	sql_text := 'select f_segtableoid, f_mgeometry_column, f_sequence_name from mgeometry_columns where f_table_name = ' || quote_literal(TG_RELNAME);
	execute sql_text into records;
	
	segtable_oid := records.f_segtableoid;
	segcolumn_name := records.f_mgeometry_column;
	sequence_name := records.f_sequence_name;

	sql_text := 'select nextval(' || quote_literal(sequence_name) || ')';
	execute sql_text into moid;
	NEW.mpoint = (moid,segtable_oid);		
	return NEW;
END
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
	
