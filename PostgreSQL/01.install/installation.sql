/*
 01. table
*/
CREATE OR REPLACE TABLE mgeometry_columns
(
	f_table_catalog character varying(256) NOT NULL,
	f_table_schema character varying(256) NOT NULL,
	f_table_name character varying(256) NOT NULL,
	f_mgeometry_column character varying(256) NOT NULL,
	f_mgeometry_segtable_name character varying(256) NOT NULL,
	mgeometry_compress character varying(256),
	coord_dimension integer,
	srid integer,
	"type" character varying(30),
	f_segtableoid character varying(256) NOT NULL,
	f_sequence_name character varying(256) NOT NULL,
	tpseg_size	integer
);

/*
 02. types
*/
CREATE TYPE mgeometry AS
(
   moid oid,
   segid text 
);

CREATE TYPE period AS
(
	fromtime bigint,
	totime bigint
);


CREATE TYPE mpoint AS
(
	geo point[],
	t bigint[]
);

CREATE TYPE mperiod AS
(
	fromtime bigint[],
	totime bigint[]
);

CREATE TYPE mdouble AS
(
	doubles double precision[],
	t 	bigint[]
);


CREATE TYPE mbool AS
(
	bools boolean[],
	t 	bigint[]
);

CREATE TYPE mpolygon AS
(
	polygons polygon[],
		t bigint[]
);

CREATE TYPE mduration AS   ----continue time
(
	duration bigint[]
);

CREATE TYPE minstant AS    ----instant
(
	t bigint[]
);

CREATE TYPE mint AS   
(
	ints integer[]
);


CREATE TYPE mstring AS
(
	mstrings text[],
	t bigint[]
);

CREATE TYPE mlinestring AS
(
	mlinestrings text[],
	t bigint[]
);

/*
 03. functions
*/
CREATE OR REPLACE FUNCTION public.m_astext(mgeometry)
    RETURNS text
    LANGUAGE 'plpgsql'	
    COST 100
    VOLATILE STRICT 
AS $BODY$
DECLARE
	f_mgeometry			alias for $1;
	f_mgeometry_segtable_name	char(200);
	sql					text;
	points				text[];
	times				bigint[];
	mpid                integer;
	results				text;
	traj_prefix			text;
	typename			text;
	uritext				text;
	horizontalangle		double precision[];
	verticalangle		double precision[];
	direction2d			double precision[];
	direction3d			double precision[];
	distance			double precision[];
	
BEGIN
	sql := 'select f_mgeometry_segtable_name  from mgeometry_columns where  f_segtableoid = ' ||quote_literal(f_mgeometry.segid);
	EXECUTE sql INTO f_mgeometry_segtable_name;
	sql := 'select type  from mgeometry_columns where f_segtableoid = ' ||quote_literal(f_mgeometry.segid);
	EXECUTE sql INTO typename;
	mpid := f_mgeometry.moid;
	sql := 'select  datetimes from ' || (f_mgeometry_segtable_name) ||' where mpid = ' ||(mpid);
   	EXECUTE sql into times;
	
	IF (typename = 'mvideo' and times is not null) THEN
	sql := 'select geo from ' || (f_mgeometry_segtable_name) ||' where mpid = ' ||(mpid);
    EXECUTE sql into points;
	sql := 'select  uri from ' || (f_mgeometry_segtable_name) ||' where mpid = ' ||(mpid);
    EXECUTE sql into uritext;
	sql := 'select  horizontalangle from ' || (f_mgeometry_segtable_name) ||' where mpid = ' ||(mpid);
    EXECUTE sql into horizontalangle;
	sql := 'select  verticalangle from ' || (f_mgeometry_segtable_name) ||' where mpid = ' ||(mpid);
    EXECUTE sql into verticalangle;
	sql := 'select  direction2d from ' || (f_mgeometry_segtable_name) ||' where mpid = ' ||(mpid);
    EXECUTE sql into direction2d;
	sql := 'select  direction3d from ' || (f_mgeometry_segtable_name) ||' where mpid = ' ||(mpid);
    EXECUTE sql into direction3d;
	sql := 'select  distance from ' || (f_mgeometry_segtable_name) ||' where mpid = ' ||(mpid);
    EXECUTE sql into distance;
	results := m_astext(points, times, uritext, horizontalangle, verticalangle, direction2d, direction3d, distance);
	ELSE
		IF (typename = 'mpoint' and times is not null) THEN
		sql := 'select geo from ' || (f_mgeometry_segtable_name) ||' where mpid = ' ||(mpid);
    	EXECUTE sql into points;
		results := m_astext(points, times);
		END IF;
	END IF;
	return results;
END
$BODY$;
ALTER FUNCTION public.m_astext(mgeometry)
    OWNER TO postgres;
    
    
 
	
-----m_spatial(mgeometry)

	
CREATE OR REPLACE FUNCTION public.m_spatial(
	mgeometry)
	RETURNS geometry
   LANGUAGE 'plpgsql'	
    COST 100
    VOLATILE STRICT 
AS $BODY$
DECLARE
	f_mgeometry			alias for $1;
	f_mgeometry_segtable_name	char(200);
	sql					text;
	points				geometry;
	spatials			geometry;
BEGIN
	sql := 'select f_mgeometry_segtable_name  from mgeometry_columns where  f_segtableoid = ' ||quote_literal(f_mgeometry.segid);
	EXECUTE sql INTO f_mgeometry_segtable_name;
	sql := 'select st_union(geo::geometry[]) from ' || (f_mgeometry_segtable_name) ||' where mpid = ' ||quote_literal(f_mgeometry.moid);
    EXECUTE sql into spatials;
	return spatials;
END;
$BODY$;
ALTER FUNCTION public.m_spatial(mgeometry)
    OWNER TO postgres;	
	
	


----------------------m_time()

CREATE OR REPLACE FUNCTION public.m_time(
	mgeometry)
	RETURNS period
   LANGUAGE 'plpgsql'	
    COST 100
    VOLATILE STRICT 
AS $BODY$
DECLARE
	f_mgeometry			alias for $1;
	f_mgeometry_segtable_name	char(200);
	sql					text;
	times				int8range;
	periods				text;
BEGIN
	sql := 'select f_mgeometry_segtable_name from mgeometry_columns where  f_segtableoid = ' ||quote_literal(f_mgeometry.segid);
	EXECUTE sql INTO f_mgeometry_segtable_name;	
	sql := 'select  timerange from ' || (f_mgeometry_segtable_name) ||' where mpid = ' ||quote_literal(f_mgeometry.moid);
   	EXECUTE sql into times;
	
	periods := '('||lower(times)-1||','||lower(times)||')';
	return periods::period;
END;
$BODY$;
ALTER FUNCTION public.m_time(mgeometry)
    OWNER TO postgres;	
		
		
		
		
-----------------------------m_tintersects_index
		
/*
	m_tintersects_noindex    basic with no index
*/

CREATE OR REPLACE FUNCTION public.m_tintersects_noindex(
	mgeometry,
	int8range)
	RETURNS bool
   LANGUAGE 'plpgsql'	
    COST 100
    VOLATILE STRICT 
AS $BODY$
DECLARE
	f_mgeometry			alias for $1;
	f_period			alias for $2;
	f_mgeometry_segtable_name	char(200);
	sql					text;
	cnt					integer;
BEGIN
	sql := 'select f_mgeometry_segtable_name  from mgeometry_columns where  f_segtableoid = ' ||quote_literal(f_mgeometry.segid);
	EXECUTE sql INTO f_mgeometry_segtable_name;
	sql := 'select  count(mpid) from ' || (f_mgeometry_segtable_name) ||' where mpid = ' ||(f_mgeometry.moid)||
	' AND  ((lower(timerange) <= lower($1) AND upper(timerange) >= lower($1)) 
	OR (lower($1) <= lower(timerange) AND upper($1) >= lower(timerange)))';		
	EXECUTE sql INTO cnt USING f_period;
		IF cnt > 0 THEN
			RETURN true;
		END IF;
	return false;
END;
$BODY$;
ALTER FUNCTION public.m_tintersects_noindex(mgeometry, int8range)
    OWNER TO postgres;	
	
/*
	m_tintersects with index 
*/	

CREATE OR REPLACE FUNCTION public.m_tintersects_index(
	mgeometry,
	int8range)
	RETURNS bool
   LANGUAGE 'plpgsql'	
    COST 100
    VOLATILE STRICT 
AS $BODY$
DECLARE
	f_mgeometry			alias for $1;
	f_period			alias for $2;
	f_mgeometry_segtable_name	char(200);
	sql					text;
	cnt					integer;
	trantext			int8range;
BEGIN

	sql := 'select f_mgeometry_segtable_name from mgeometry_columns where  f_segtableoid = ' ||quote_literal(f_mgeometry.segid);
	EXECUTE sql INTO f_mgeometry_segtable_name;
	--trantext := (f_period::text)::int8range;
	sql := 'select count(*) from ' || (f_mgeometry_segtable_name) ||' where mpid = ' ||(f_mgeometry.moid) ||' AND timerange && $1;';
   	EXECUTE sql into cnt USING f_period;	
	IF (cnt > 0) then 
		RETURN true;
	END IF;
	RETURN false;
END;
$BODY$;
ALTER FUNCTION public.m_tintersects_index(mgeometry, int8range)
    OWNER TO postgres;	
	
/*   
   m_tintersects_materialized with index into temporal table
*/

CREATE OR REPLACE FUNCTION public.temp_mgeometry_table(mgeometry)
	RETURNS text
   LANGUAGE 'plpgsql'	
    COST 100
    VOLATILE STRICT 
AS $BODY$
DECLARE
	f_mgeometry			alias for $1;
	f_mgeometry_segtable_name	char(200);
	meta_key 			text;
	meta_value			text;
	sql_text			varchar;
	table_key			text;
BEGIN
	meta_key := 'temp.mgeometry.column';
	BEGIN
		meta_value := current_setting(meta_key);
	EXCEPTION when undefined_object then
		perform set_config(meta_key, '0', false);	     
		meta_value := current_setting(meta_key);
	END;	
	IF (meta_value = '0') THEN	
		perform set_config(meta_key, '1', false);
		table_key := 'temp_mgeometry_column';
		sql_text := 'CREATE  temporary TABLE '|| table_key || ' as ';
		sql_text := sql_text || ' SELECT * FROM mgeometry_columns;';
		EXECUTE sql_text ;
	END IF;		
	 	sql_text :=  'select f_mgeometry_segtable_name  from temp_mgeometry_column where  f_segtableoid = ' ||quote_literal(f_mgeometry.segid);
		EXECUTE sql_text INTO f_mgeometry_segtable_name;
	RETURN  f_mgeometry_segtable_name;
	END;
$BODY$;
ALTER FUNCTION public.temp_mgeometry_table(mgeometry)
    OWNER TO postgres;	


CREATE OR REPLACE FUNCTION public.m_tintersects_materialized(
	mgeometry,
	int8range)
	RETURNS bool
   LANGUAGE 'plpgsql'	
    COST 100
    VOLATILE STRICT 
AS $BODY$
DECLARE
	f_mgeometry			alias for $1;
	f_period			alias for $2;
	f_mgeometry_segtable_name	char(200);
	cnt					integer;
	sql_text			varchar;
	session_key 			text;
	session_value			text;
	tmp_table			text;
BEGIN
	---------------mgeometry table
	f_mgeometry_segtable_name := temp_mgeometry_table(f_mgeometry);
	-----------temporal query	
	session_key := 'temp.intersects.column';
	BEGIN
		session_value := current_setting(session_key);
	EXCEPTION when undefined_object then
		perform set_config(session_key, '0', false);	     
		session_value := current_setting(session_key);
	END;
	IF (session_value = '0') THEN	
		perform set_config(session_key, '1', false);
		tmp_table := 'temp_table';
		sql_text := 'CREATE temporary TABLE ' ||tmp_table|| ' as ';
		sql_text := sql_text || ' SELECT DISTINCT mpid FROM ' || f_mgeometry_segtable_name;
		sql_text := sql_text || ' WHERE timerange && $1;';
		EXECUTE sql_text USING f_period;
	END IF;		
	
	sql_text := 'SELECT COUNT(*) FROM temp_table WHERE mpid = ' || f_mgeometry.moid;
	EXECUTE sql_text INTO cnt;	
		IF cnt > 0 THEN
			RETURN true;
		END IF;	
	return false;
	END;
$BODY$;
ALTER FUNCTION public.m_tintersects_materialized(mgeometry, int8range)
    OWNER TO postgres;	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
		
-----------------------------m_sintersects_index
		

		
/*
	m_sintersects_noindex    basic with  index
*/

CREATE OR REPLACE FUNCTION public.m_sintersects_index(
	mgeometry,
	geometry)
	RETURNS bool
   LANGUAGE 'plpgsql'	
    COST 100
    VOLATILE STRICT 
AS $BODY$
DECLARE
	f_mgeometry			alias for $1;
	f_geometry			alias for $2;
	f_mgeometry_segtable_name	char(200);
	sql					text;
	res				bool;
	cnt				integer;
BEGIN
	sql := 'select f_mgeometry_segtable_name  from mgeometry_columns where  f_segtableoid = ' ||quote_literal(f_mgeometry.segid);
	EXECUTE sql INTO f_mgeometry_segtable_name;
	sql := 'select count(mpid) from ' || (f_mgeometry_segtable_name) ||' where mpid = ' ||(f_mgeometry.moid) || ' AND st_intersects(st_makeline(geo::geometry[]),$1)';
	EXECUTE sql INTO cnt USING f_geometry; 
	IF( cnt > 0 ) THEN
	return true;
	EXECUTE sql INTO res; 
	END IF;
	RETURN res; 
END;
$BODY$;
ALTER FUNCTION public.m_sintersects_index(mgeometry, geometry)
    OWNER TO postgres;	
	
	
/*
	m_sintersects without index 
*/	


CREATE OR REPLACE FUNCTION public.m_sintersects_noindex(
	mgeometry,
	geometry)
	RETURNS bool
   LANGUAGE 'plpgsql'	
    COST 100
    VOLATILE STRICT 
AS $BODY$
DECLARE
	f_mgeometry			alias for $1;
	f_geometry			alias for $2;
	f_mgeometry_segtable_name	char(200);
	sql					text;
	res					bool;
	geos				geometry;
BEGIN
	sql := 'select f_mgeometry_segtable_name from mgeometry_columns where f_segtableoid = ' ||quote_literal(f_mgeometry.segid);
	EXECUTE sql INTO f_mgeometry_segtable_name;
	sql := 'select st_union(st_makeline(geo::geometry[])) from ' || (f_mgeometry_segtable_name) ||' where mpid = ' ||(f_mgeometry.moid);
	EXECUTE sql INTO geos; 
		IF(geos is not null) THEN
	sql := 'select ' ||m_intersects(st_astext(geos),st_astext(f_geometry));
	EXECUTE sql INTO res USING f_geometry; 
	END IF;
	RETURN res;
END;
$BODY$;
ALTER FUNCTION public.m_sintersects_noindex(mgeometry, geometry)
    OWNER TO postgres;	


	
/*   
   m_sintersects_materialized with index into temporal table
*/

CREATE OR REPLACE FUNCTION public.m_sintersects_materialized(
	mgeometry,
	geometry)
	RETURNS bool
   LANGUAGE 'plpgsql'	
    COST 100
    VOLATILE STRICT 
AS $BODY$
DECLARE
	f_mgeometry			alias for $1;
	f_geometry			alias for $2;
	f_mgeometry_segtable_name	char(200);
	sql					text;
	cnt					integer;
	meta_key 			text;
	meta_value			text;
	sql_text			varchar;
	table_key			text;
	session_key 			text;
	session_value			text;
	tmp_table			text;
BEGIN
	f_mgeometry_segtable_name := temp_mgeometry_table(f_mgeometry);
	session_key := 'temp.intersects.spatial';
	BEGIN
		session_value := current_setting(session_key);
	EXCEPTION when undefined_object then
		perform set_config(session_key, '0', false);	     
		session_value := current_setting(session_key);
	END;
	IF (session_value = '0') THEN	
		perform set_config(session_key, '1', false);
		tmp_table := 'temp_table_spatial';`
		sql_text := 'CREATE temporary TABLE ' ||tmp_table|| ' as ';
		sql_text := sql_text || ' SELECT DISTINCT mpid FROM ' || f_mgeometry_segtable_name;
		sql_text := sql_text || ' WHERE st_intersects(st_makeline(geo::geometry[]),$1);';
		EXECUTE sql_text USING f_geometry;
	END IF;		
	
	sql_text := 'SELECT COUNT(*) FROM temp_table_spatial WHERE mpid = ' || f_mgeometry.moid;
	EXECUTE sql_text INTO cnt;	
		IF cnt > 0 THEN
			RETURN true;
		END IF;	
	return false;
	END;
$BODY$;
ALTER FUNCTION public.m_sintersects_materialized(mgeometry, geometry)
    OWNER TO postgres;	
	
	
	
	
	
	
	
	
	
	
	--------------------------------------------
		
		
/*
	m_intersects_noindex    basic with no index
*/
CREATE OR REPLACE FUNCTION public.m_intersects_index(
	mgeometry,
	geometry, int8range)
	RETURNS bool
   LANGUAGE 'plpgsql'	
    COST 100
    VOLATILE STRICT 
AS $BODY$
DECLARE
	f_mgeometry			alias for $1;
	f_geometry			alias for $2;
	f_period			alias for $3;
	f_mgeometry_segtable_name	char(200);
	sql					text;
	cnt				integer;
	res				bool;
BEGIN
	sql := 'select f_mgeometry_segtable_name  from mgeometry_columns where  f_segtableoid = ' ||quote_literal(f_mgeometry.segid);
	EXECUTE sql INTO f_mgeometry_segtable_name;
	sql := 'select count(*) from ' || (f_mgeometry_segtable_name) ||' where mpid = ' ||(f_mgeometry.moid) ||
	' AND  (timerange && $1) AND st_intersects(st_makeline(geo::geometry[]), $2)';	
	EXECUTE sql INTO cnt USING f_period, f_geometry; 
	IF(cnt>0) THEN
	return true;
	END IF;
	return res;
END;
$BODY$;
ALTER FUNCTION public.m_intersects_index(mgeometry, geometry, int8range)
    OWNER TO postgres;	
	
/*
	m_intersects with index 
*/	
CREATE OR REPLACE FUNCTION public.m_intersects_noindex(
	mgeometry,
	geometry, int8range)
	RETURNS bool
   LANGUAGE 'plpgsql'	
    COST 100
    VOLATILE STRICT 
AS $BODY$
DECLARE
	f_mgeometry			alias for $1;
	f_geometry			alias for $2;
	f_period			alias for $3;
	f_mgeometry_segtable_name	char(200);
	sql					text;
	cnt					integer;
	geos				geometry;
	res					bool;
BEGIN
	sql := 'select f_mgeometry_segtable_name from mgeometry_columns where f_segtableoid = ' ||quote_literal(f_mgeometry.segid);
	EXECUTE sql INTO f_mgeometry_segtable_name;
	
	sql := 'select st_union(st_makeline(geo::geometry[])) from ' || (f_mgeometry_segtable_name) ||' where mpid = ' ||(f_mgeometry.moid) ||
	' AND   ((lower(timerange) <= lower($1) AND upper(timerange) >= lower($1)) OR (lower($1) <= lower(timerange) AND upper($1) >= lower(timerange)))';	
	EXECUTE sql INTO geos USING f_period; 
	IF(geos is not null) THEN
	sql := 'select ' || m_intersects(st_astext(geos), st_astext(f_geometry));
	EXECUTE sql INTO res; 
	END IF;
	return res;
END;
$BODY$;
ALTER FUNCTION public.m_intersects_noindex(mgeometry, geometry, int8range)
    OWNER TO postgres;	
	
	
/*   
   m_intersects_materialized with index into temporal table
*/
CREATE OR REPLACE FUNCTION public.m_intersects_materialized(
	mgeometry,
	geometry, int8range)
	RETURNS bool
   LANGUAGE 'plpgsql'	
    COST 100
    VOLATILE STRICT 
AS $BODY$
DECLARE
	f_mgeometry			alias for $1;
	f_geometry			alias for $2;
	f_period			alias for $3;
	f_mgeometry_segtable_name	char(200);
	sql					text;
	cnt					integer;
	meta_key 			text;
	meta_value			text;
	sql_text			varchar;
	table_key			text;
	session_key 			text;
	session_value			text;
	tmp_table			text;
BEGIN
	---------------mgeometry table
	f_mgeometry_segtable_name := temp_mgeometry_table(f_mgeometry);
	-----------temporal query	
	session_key := 'temp.intersects.st';
	BEGIN
		session_value := current_setting(session_key);
	EXCEPTION when undefined_object then
		perform set_config(session_key, '0', false);	     
		session_value := current_setting(session_key);
	END;
	IF (session_value = '0') THEN	
		perform set_config(session_key, '1', false);
		tmp_table := 'temp_table_st';
		sql_text := 'CREATE temporary TABLE ' ||tmp_table|| ' as ';
		sql_text := sql_text || ' SELECT DISTINCT mpid FROM ' || f_mgeometry_segtable_name;
		sql_text := sql_text || ' WHERE timerange && $1 AND st_intersects(st_makeline(geo::geometry[]),$2);';
		EXECUTE sql_text USING f_period, f_geometry;
	END IF;		
	
	sql_text := 'SELECT COUNT(*) FROM temp_table_st WHERE mpid = ' || f_mgeometry.moid;
	EXECUTE sql_text INTO cnt;	
		IF cnt > 0 THEN
			RETURN true;
		END IF;	
	return false;
	END;
$BODY$;
ALTER FUNCTION public.m_intersects_materialized(mgeometry, geometry, int8range)
    OWNER TO postgres;	
	
	
	
	
	
	
	-------------------------------------
CREATE OR REPLACE FUNCTION public.m_mindistance_noindex(
	mgeometry,
	geometry, double precision)
	RETURNS bool
   LANGUAGE 'plpgsql'	
    COST 100
    VOLATILE STRICT 
AS $BODY$
DECLARE
	f_mgeometry			alias for $1;
	f_geometry			alias for $2;
	f_double			alias for $3;
	f_mgeometry_segtable_name	char(200);
	sql					text;
	geos				geometry;
BEGIN
	sql := 'select f_mgeometry_segtable_name from mgeometry_columns where f_segtableoid = ' ||quote_literal(f_mgeometry.segid);
	EXECUTE sql INTO f_mgeometry_segtable_name;		
	sql := 'select st_union(ST_Collect(geo::geometry[])) from ' || (f_mgeometry_segtable_name) ||' where mpid = ' ||(f_mgeometry.moid);	
	EXECUTE sql INTO geos; 
	IF  st_distance(geos::geography, f_geometry::geography)< f_double THEN
		RETURN true;
	END IF;
	return false;
END;
$BODY$;
ALTER FUNCTION public.m_mindistance_noindex(mgeometry, geometry, double precision)
    OWNER TO postgres;	
	
	
	
	
	
CREATE OR REPLACE FUNCTION public.m_mindistance_index(
	mgeometry,
	geometry, double precision)
	RETURNS bool
   LANGUAGE 'plpgsql'	
    COST 100
    VOLATILE STRICT 
AS $BODY$
DECLARE
	f_mgeometry			alias for $1;
	f_geometry			alias for $2;
	f_double			alias for $3;
	f_mgeometry_segtable_name	char(200);
	sql					text;
	cnn					integer;
BEGIN
	sql := 'select f_mgeometry_segtable_name from mgeometry_columns where f_segtableoid = ' ||quote_literal(f_mgeometry.segid);
	EXECUTE sql INTO f_mgeometry_segtable_name;		
	
	sql := 'select count(mpid) from ' || (f_mgeometry_segtable_name) ||' where mpid = ' ||(f_mgeometry.moid)|| 
	' AND st_distance($1::geography, mbr::geography)< $2 AND st_distance($1::geography, ST_Collect(geo::geometry[])::geography)< $2';	
	EXECUTE sql INTO cnn USING f_geometry, f_double; 
	IF cnn >0 THEN
		RETURN true;
	END IF;
	return false;
END;
$BODY$;
ALTER FUNCTION public.m_mindistance_index(mgeometry, geometry, double precision)
    OWNER TO postgres;	
	
	
	
	
	
CREATE OR REPLACE FUNCTION public.m_mindistance_materialized(
	mgeometry,
	geometry, double precision)
	RETURNS bool
   LANGUAGE 'plpgsql'	
    COST 100
    VOLATILE STRICT 
AS $BODY$
DECLARE
	f_mgeometry			alias for $1;
	f_geometry			alias for $2;
	f_double			alias for $3;
	f_mgeometry_segtable_name	char(200);
	sql_text					text;
	cnt					integer;
	cnn					integer;
	geos				geometry;
	res					bool;
	session_key 			text;
	session_value			text;
	tmp_table			text;
BEGIN
	f_mgeometry_segtable_name := temp_mgeometry_table(f_mgeometry);	
	session_key := 'temp.mindis.st';
	BEGIN
		session_value := current_setting(session_key);
	EXCEPTION when undefined_object then
		perform set_config(session_key, '0', false);	     
		session_value := current_setting(session_key);
	END;
	tmp_table := 'temp_table_mindis';
	IF (session_value = '0') THEN	
		perform set_config(session_key, '1', false);
		sql_text := 'CREATE temporary TABLE ' ||tmp_table|| ' as ';
		sql_text := sql_text || ' SELECT DISTINCT mpid FROM ' || f_mgeometry_segtable_name;
		sql_text := sql_text || ' WHERE st_distance($1::geography, mbr::geography)< $2 AND st_distance($1::geography, ST_Collect(geo::geometry[])::geography)< $2';	
		EXECUTE sql_text USING f_geometry, f_double; 
	END IF;		
	
	sql_text := 'SELECT COUNT(*) FROM temp_table_mindis WHERE mpid = ' || f_mgeometry.moid;
	EXECUTE sql_text INTO cnt;	
		IF cnt > 0 THEN
			RETURN true;
		END IF;	
	return false;
END;
$BODY$;
ALTER FUNCTION public.m_mindistance_materialized(mgeometry, geometry, double precision)
    OWNER TO postgres;	
	
	
	------------------------------------mgeometry to megeometgry distance join query
	
	
CREATE OR REPLACE FUNCTION public.m_mindistance_noindex(
	mgeometry,
	mgeometry, double precision)
	RETURNS bool
   LANGUAGE 'plpgsql'	
    COST 100
    VOLATILE STRICT 
AS $BODY$
DECLARE
	f_mgeometry			alias for $1;
	f_mgeometry2			alias for $2;
	f_double			alias for $3;
	f_mgeometry_segtable_name	char(200);
	f_mgeometry_segtable_name2	char(200);
	sql					text;
	geos				geometry;
	geo2				geometry;
BEGIN
	sql := 'select f_mgeometry_segtable_name from mgeometry_columns where f_segtableoid = ' ||quote_literal(f_mgeometry.segid);
	EXECUTE sql INTO f_mgeometry_segtable_name;		
	sql := 'select f_mgeometry_segtable_name from mgeometry_columns where f_segtableoid = ' ||quote_literal(f_mgeometry2.segid);
	EXECUTE sql INTO f_mgeometry_segtable_name2;		
	
	sql := 'select st_union(ST_Collect(geo::geometry[])) from ' || (f_mgeometry_segtable_name) ||' where mpid = ' ||(f_mgeometry.moid);	
	EXECUTE sql INTO geos; 
	sql := 'select st_union(ST_Collect(geo::geometry[])) from ' || (f_mgeometry_segtable_name2) ||' where mpid = ' ||(f_mgeometry2.moid);	
	EXECUTE sql INTO geo2; 
	
	IF  st_distance(geos::geography, geo2::geography)< f_double THEN
		RETURN true;
	END IF;
	return false;
END;
$BODY$;
ALTER FUNCTION public.m_mindistance_noindex(mgeometry, mgeometry, double precision)
    OWNER TO postgres;	
	
	
	
	
	
CREATE OR REPLACE FUNCTION public.m_mindistance_index(
	mgeometry,
	mgeometry, double precision)
	RETURNS bool
   LANGUAGE 'plpgsql'	
    COST 100
    VOLATILE STRICT 
AS $BODY$
DECLARE
	f_mgeometry			alias for $1;
	f_mgeometry2			alias for $2;
	f_double			alias for $3;
	f_mgeometry_segtable_name	char(200);
	f_mgeometry_segtable_name2	char(200);
	sql					text;
	cnn					integer;
BEGIN
	sql := 'select f_mgeometry_segtable_name from mgeometry_columns where f_segtableoid = ' ||quote_literal(f_mgeometry.segid);
	EXECUTE sql INTO f_mgeometry_segtable_name;	
	sql := 'select f_mgeometry_segtable_name from mgeometry_columns where f_segtableoid = ' ||quote_literal(f_mgeometry2.segid);
	EXECUTE sql INTO f_mgeometry_segtable_name2;	
	
	sql := 'select count(a.mpid) from ' || (f_mgeometry_segtable_name) ||' a, '|| (f_mgeometry_segtable_name2) ||' b where a.mpid = ' ||(f_mgeometry.moid)|| ' AND b.mpid = ' ||(f_mgeometry2.moid)|| 
	' AND st_distance( b.mbr::geography, a.mbr::geography)< $1 AND st_distance(ST_Collect(a.geo::geometry[])::geography, ST_Collect(b.geo::geometry[])::geography)< $1';	
	EXECUTE sql INTO cnn USING f_double; 
	IF cnn >0 THEN
		RETURN true;
	END IF;
	return false;
END;
$BODY$;
ALTER FUNCTION public.m_mindistance_index(mgeometry, mgeometry, double precision)
    OWNER TO postgres;	
	
	
	
	
	
CREATE OR REPLACE FUNCTION public.m_mindistance_materialized(
	mgeometry,
	mgeometry, double precision)
	RETURNS bool
   LANGUAGE 'plpgsql'	
    COST 100
    VOLATILE STRICT 
AS $BODY$
DECLARE
	f_mgeometry			alias for $1;
	f_mgeometry2			alias for $2;
	f_double			alias for $3;
	f_mgeometry_segtable_name	char(200);
	f_mgeometry_segtable_name2	char(200);
	sql_text					text;
	cnt					integer;
	session_key 			text;
	session_value			text;
	tmp_table			text;
BEGIN
	f_mgeometry_segtable_name := temp_mgeometry_table(f_mgeometry);	
	f_mgeometry_segtable_name2 := temp_mgeometry_table(f_mgeometry2);	
	session_key := 'temp.mindis.st';
	BEGIN
		session_value := current_setting(session_key);
	EXCEPTION when undefined_object then
		perform set_config(session_key, '0', false);	     
		session_value := current_setting(session_key);
	END;
	tmp_table := 'temp_table_mindismm';
	IF (session_value = '0') THEN	
		perform set_config(session_key, '1', false);
		----mpid   one   b
		sql_text := 'CREATE temporary TABLE ' ||tmp_table|| ' as ';
		sql_text := sql_text || ' SELECT DISTINCT a.mpid as ampid, b.mpid as bmpid FROM ' || f_mgeometry_segtable_name ||' a, '|| (f_mgeometry_segtable_name2) ||' b ';
		sql_text := sql_text || ' WHERE st_distance( a.mbr::geography, b.mbr::geography)< $1  
		AND st_distance( ST_Collect(b.geo::geometry[])::geography, ST_Collect(a.geo::geometry[])::geography)< $1;';	
		EXECUTE sql_text USING f_double; 
	END IF;		
	
	sql_text := 'SELECT COUNT(*) FROM temp_table_mindismm WHERE ampid = ' || f_mgeometry.moid|| ' AND bmpid ='|| f_mgeometry2.moid;
	EXECUTE sql_text INTO cnt;	
		IF cnt > 0 THEN
			RETURN true;
		END IF;	
	return false;
END;
$BODY$;
ALTER FUNCTION public.m_mindistance_materialized(mgeometry, mgeometry, double precision)
    OWNER TO postgres;	
	
	
	
	
	
CREATE OR REPLACE FUNCTION public.m_mindistance_materialized(
	mgeometry,
	mgeometry, double precision, text, text)
	RETURNS bool
   LANGUAGE 'plpgsql'	
    COST 100
    VOLATILE STRICT 
AS $BODY$
DECLARE
	f_mgeometry			alias for $1;
	f_mgeometry2			alias for $2;
	f_double			alias for $3;
	f_1			alias for $4;
	f_2			alias for $5;
	f_mgeometry_segtable_name	char(200);
	f_mgeometry_segtable_name2	char(200);
	sql_text					text;
	cnt					integer;
	session_key 			text;
	session_value			text;
	tmp_table			text;
BEGIN
	f_mgeometry_segtable_name := temp_mgeometry_table(f_mgeometry);	
	f_mgeometry_segtable_name2 := temp_mgeometry_table(f_mgeometry2);	
	session_key := 'temp.mindis.st';
	BEGIN
		session_value := current_setting(session_key);
	EXCEPTION when undefined_object then
		perform set_config(session_key, '0', false);	     
		session_value := current_setting(session_key);
	END;
	tmp_table := 'temp_table_mindismm';
	IF (session_value = '0') THEN	
		perform set_config(session_key, '1', false);
		----mpid   one   b
		sql_text := 'CREATE temporary TABLE ' ||tmp_table|| ' as ';
		sql_text := sql_text || ' SELECT DISTINCT a.mpid as ampid, b.mpid as bmpid FROM ' || f_mgeometry_segtable_name ||' a, '|| (f_mgeometry_segtable_name2) ||' b ';
		sql_text := sql_text || ' WHERE $2::int4range @> a.mpid AND $3::int4range @> b.mpid AND st_distance( a.mbr::geography, b.mbr::geography)< $1  
		AND st_distance( ST_Collect(b.geo::geometry[])::geography, ST_Collect(a.geo::geometry[])::geography)< $1;';	
		EXECUTE sql_text USING f_double, f_1, f_2; 
	END IF;		
	raise notice 'sql : %', sql_text;		
	sql_text := 'SELECT COUNT(*) FROM temp_table_mindismm WHERE ampid = ' || f_mgeometry.moid|| ' AND bmpid ='|| f_mgeometry2.moid;
	EXECUTE sql_text INTO cnt;	
		IF cnt > 0 THEN
			RETURN true;
		END IF;	
	return false;
END;
$BODY$;
ALTER FUNCTION public.m_mindistance_materialized(mgeometry, mgeometry, double precision, text, text)
    OWNER TO postgres;	
	
	
	
CREATE OR REPLACE FUNCTION public.m_mindistance_index(
	mgeometry,
	geometry, double precision, int8range)
	RETURNS bool
   LANGUAGE 'plpgsql'	
    COST 100
    VOLATILE STRICT 
AS $BODY$
DECLARE
	f_mgeometry			alias for $1;
	f_geometry			alias for $2;
	f_double			alias for $3;
	f_range				alias for $4;
	f_mgeometry_segtable_name	char(200);
	sql					text;
	cnn					integer;
BEGIN
	sql := 'select f_mgeometry_segtable_name from mgeometry_columns where f_segtableoid = ' ||quote_literal(f_mgeometry.segid);
	EXECUTE sql INTO f_mgeometry_segtable_name;		
	
	sql := 'select count(mpid) from ' || (f_mgeometry_segtable_name) ||' where mpid = ' ||(f_mgeometry.moid)|| 
	' AND timerange && $1 AND st_distance($2::geography, mbr::geography)< $3 AND st_distance($2::geography, ST_Collect(geo::geometry[])::geography)< $3';	
	EXECUTE sql INTO cnn USING f_range, f_geometry, f_double; --30m
	IF cnn >0 THEN
		RETURN true;
	END IF;
	return false;
END;
$BODY$;
ALTER FUNCTION public.m_mindistance_index(mgeometry, geometry, double precision, int8range)
    OWNER TO postgres;	
	
CREATE OR REPLACE FUNCTION public.m_mindistance_index(
	mgeometry,
	geometry, double precision, bigint)
	RETURNS bool
   LANGUAGE 'plpgsql'	
    COST 100
    VOLATILE STRICT 
AS $BODY$
DECLARE
	f_mgeometry			alias for $1;
	f_geometry			alias for $2;
	f_double			alias for $3;
	f_range				alias for $4;
	f_mgeometry_segtable_name	char(200);
	sql					text;
	cnn					integer;
BEGIN
	sql := 'select f_mgeometry_segtable_name from mgeometry_columns where f_segtableoid = ' ||quote_literal(f_mgeometry.segid);
	EXECUTE sql INTO f_mgeometry_segtable_name;		
	
	sql := 'select count(mpid) from ' || (f_mgeometry_segtable_name) ||' where mpid = ' ||(f_mgeometry.moid)|| 
	' AND timerange @> $1 AND st_distance($2::geography, mbr::geography)< $3 AND st_distance($2::geography, ST_Collect(geo::geometry[])::geography)< $3';	
	EXECUTE sql INTO cnn USING f_range, f_geometry, f_double; --30m
	IF cnn >0 THEN
		RETURN true;
	END IF;
	return false;
END;
$BODY$;
ALTER FUNCTION public.m_mindistance_index(mgeometry, geometry, double precision, bigint)
    OWNER TO postgres;	

/*
  04.add_mgeometry_columns
*/

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

/*
  05.append_mgeoemtry_data
*/

CREATE OR REPLACE FUNCTION public.append(
	mgeometry,
	point,
	bigint)
    RETURNS mgeometry
AS $BODY$
DECLARE
    f_mgeometry            	alias for $1;
    tp                		alias for $2;
 	timeline                alias for $3;
	segid                	integer;
    f_mgeometry_segtable_name    char(200);
    mpid               		integer;
    sql                		text;
	new_segid				integer;
	traj_prefix				text;
	cnt_mpid				integer;
	max_tpseg_count			integer;
	tp_seg_size					integer;
BEGIN   
	traj_prefix := 'mpoint_' ;		
	f_mgeometry_segtable_name := traj_prefix || f_mgeometry.segid ;
	mpid := f_mgeometry.moid;
	----count number of points
	sql := 'SELECT COUNT(*) FROM ' || quote_ident(f_mgeometry_segtable_name) || 
		' WHERE mpid = ' || f_mgeometry.moid;
	RAISE DEBUG '%', sql;
	EXECUTE sql INTO cnt_mpid;
	----for indexing seg_table 
	sql := 'select tpseg_size from mgeometry_columns where f_mgeometry_segtable_name  = ' || quote_literal(f_mgeometry_segtable_name);
	RAISE DEBUG '%', sql;
	EXECUTE sql INTO tp_seg_size;	
	IF (cnt_mpid < 1) THEN
		---mpid, segid, mbr, datetimes, geo
		EXECUTE 'INSERT INTO ' || quote_ident(f_mgeometry_segtable_name) || '(mpid, segid, mbr, datetimes, geo) 
			VALUES($1, 1, st_geomfromtext(st_astext(st_makebox2d($3::geometry, $3::geometry))), ARRAY[$2]::bigint[], ARRAY[$3]::Point[])'
		USING mpid, timeline, tp;
	END IF;
    ----have points in mpoint
	IF(cnt_mpid > 0) THEN
		sql := 'select max(segid) from ' || quote_ident(f_mgeometry_segtable_name) ||
				' where mpid = ' || f_mgeometry.moid;
		EXECUTE sql INTO segid;
		sql := 'select array_upper((select geo from ' || quote_ident(f_mgeometry_segtable_name) || 
			' where mpid = ' || f_mgeometry.moid || ' and segid = ' || segid || '), 1)';
		RAISE DEBUG '%', sql;
		EXECUTE sql INTO max_tpseg_count;
		----add array points and times	
		IF( segid IS NOT NULL AND max_tpseg_count < tp_seg_size) THEN
			EXECUTE 'UPDATE ' || quote_ident(f_mgeometry_segtable_name) || 
				' set datetimes = array_append(datetimes, $1), mbr = st_geomfromtext(st_astext(st_combinebbox( Box2D(mbr), $2::geometry))), geo = array_append(geo, $2)
				where mpid = $3 and segid = $4'
			USING timeline, tp, mpid, segid;
		ELSE 
			---split segment mpoint
			new_segid := segid+1;
			EXECUTE 'INSERT INTO ' || quote_ident(f_mgeometry_segtable_name) ||'(mpid, segid, mbr, datetimes, geo) 
				VALUES( $1, $2, st_geomfromtext(st_astext(st_makebox2d($3::geometry, $3::geometry))), ARRAY[$4]::bigint[], ARRAY[$5]::Point[])'
			USING f_mgeometry.moid, new_segid, tp, timeline, tp;
				
		END IF;
	END IF;
	RETURN f_mgeometry;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE STRICT
  COST 100;
ALTER FUNCTION public.append(mgeometry, point, bigint)
    OWNER TO postgres;
    
    
    
    
    
    
CREATE OR REPLACE FUNCTION public.append(mgeometry, point, bigint, double precision, double precision, double precision, double precision, double precision, character varying)
    RETURNS mgeometry
AS $BODY$
DECLARE
    f_mgeometry            	alias for $1;
    tp                		alias for $2;
 	timeline                alias for $3;
	vangle                alias for $4;
	hangle                alias for $5;
	dir2d                alias for $6;
	dir3d                alias for $7;
	dist                alias for $8;
	uris                alias for $9;
	segid                	integer;
    f_mgeometry_segtable_name    char(200);
    mpid               		integer;
    sql                		text;
	new_segid				integer;
	traj_prefix				text;
	cnt_mpid				integer;
	max_tpseg_count			integer;
	tp_seg_size					integer;
BEGIN   
	traj_prefix := 'mvideo_' ;		
	f_mgeometry_segtable_name := traj_prefix || f_mgeometry.segid ;
	mpid := f_mgeometry.moid;
	----count number of mvideos
	sql := 'SELECT COUNT(*) FROM ' || quote_ident(f_mgeometry_segtable_name) || 
		' WHERE mpid = ' || f_mgeometry.moid;
	RAISE DEBUG '%', sql;
	EXECUTE sql INTO cnt_mpid;
	----for indexing seg_table 
	sql := 'select tpseg_size from mgeometry_columns where f_mgeometry_segtable_name  = ' || quote_literal(f_mgeometry_segtable_name);
	RAISE DEBUG '%', sql;
	EXECUTE sql INTO tp_seg_size;	
	IF (cnt_mpid < 1) THEN
		---mpid, segid, mbr, datetimes, geo
		EXECUTE 'INSERT INTO ' || quote_ident(f_mgeometry_segtable_name) || '(mpid, segid, mbr, datetimes, geo, horizontalAngle, verticalAngle, direction2d, direction3d, distance, uri) 
			VALUES($1, 1, st_geomfromtext(st_astext(st_makebox2d($3::geometry, $3::geometry))), ARRAY[$2]::bigint[], ARRAY[$3]::Point[], 
			ARRAY[$4]::double precision[], ARRAY[$5]::double precision[], ARRAY[$6]::double precision[], ARRAY[$7]::double precision[], ARRAY[$8]::double precision[], ARRAY[$9]::character varying[])'
		USING mpid, timeline, tp, vangle, hangle, dir2d, dir3d, dist, uris;
	END IF;
    ----have points in mvideo
	IF(cnt_mpid > 0) THEN
		sql := 'select max(segid) from ' || quote_ident(f_mgeometry_segtable_name) ||
				' where mpid = ' || f_mgeometry.moid;
		EXECUTE sql INTO segid;
		sql := 'select array_upper((select geo from ' || quote_ident(f_mgeometry_segtable_name) || 
			' where mpid = ' || f_mgeometry.moid || ' and segid = ' || segid || '), 1)';
		RAISE DEBUG '%', sql;
		EXECUTE sql INTO max_tpseg_count;
		----add array videos and times	
		IF( segid IS NOT NULL AND max_tpseg_count < tp_seg_size) THEN
			EXECUTE 'UPDATE ' || quote_ident(f_mgeometry_segtable_name) || 
				' set datetimes = array_append(datetimes, $1), mbr = st_geomfromtext(st_astext(st_combinebbox( Box2D(mbr), $2::geometry))), geo = array_append(geo, $2),
				horizontalAngle = array_append(horizontalAngle, $3), verticalAngle = array_append(verticalAngle, $4), direction2d = array_append(direction2d, $5), 
				direction3d = array_append(direction3d, $6), distance = array_append(distance, $7), uri = array_append(uri, $8) 
				where mpid = $9 and segid = $10'
			USING timeline, tp, vangle, hangle, dir2d, dir3d, dist, uris, mpid, segid;
		ELSE 
			---split segment mvideo
			new_segid := segid+1;
			EXECUTE 'INSERT INTO ' || quote_ident(f_mgeometry_segtable_name) ||'(mpid, segid, mbr, datetimes, geo, horizontalAngle, verticalAngle, direction2d, direction3d, distance, uri) 
				VALUES( $1, $2, st_geomfromtext(st_astext(st_makebox2d($3::geometry, $3::geometry))), ARRAY[$4]::bigint[], ARRAY[$5]::Point[],
				ARRAY[$6]::double precision[], ARRAY[$7]::double precision[], ARRAY[$8]::double precision[], ARRAY[$9]::double precision[], ARRAY[$10]::double precision[], ARRAY[$11]::character varying[])'
			USING f_mgeometry.moid, new_segid, tp, timeline, tp, vangle, hangle, dir2d, dir3d, dist, uris;				
		END IF;
	END IF;
	RETURN f_mgeometry;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE STRICT
  COST 100;
ALTER FUNCTION public.append(mgeometry, point, bigint, double precision, double precision, double precision, double precision, double precision, character varying)
    OWNER TO postgres;
    
 /*
   06. kNN
 */
 
 ----- AGGREGATE FUNCTION -----

CREATE AGGREGATE m_knn(mpoint,geometry,integer)
(
  SFUNC = m_knn1,
  STYPE = setof text[],	 //setof is optional
  FINALFUNC = results,
  INITCOND = '{}'
);

CREATE AGGREGATE m_knn_distance(mpoint,geometry,integer)
(
  SFUNC = m_knn1_distance,
  STYPE = setof text[],	//setof is optional
  FINALFUNC = results,
  INITCOND = '{}'
);

CREATE AGGREGATE m_knn(mpoint,mpoint,integer)
(
  SFUNC = m_knn1,
  STYPE = text[],	
  FINALFUNC = results,
  INITCOND = '{}'
);

CREATE AGGREGATE m_knn_distance(mpoint,mpoint,integer)
(
  SFUNC = m_knn1_distance,
  STYPE = text[],	
  FINALFUNC = results,
  INITCOND = '{}'
);

CREATE AGGREGATE m_knn(mpoint,text,integer)
(
  SFUNC = m_knn1,
  STYPE = text[],	 
  FINALFUNC = results,
  INITCOND = '{}'
);
DROP AGGREGATE m_knn(mpoint,text,integer)

CREATE AGGREGATE m_knn_distance(mpoint,text,integer)
(
  SFUNC = m_knn1_distance,
  STYPE = text[],	 
  FINALFUNC = results,
  INITCOND = '{}'
);
DROP AGGREGATE m_knn_distance(mpoint,text,integer)

----- AGGREGATE SFUNC FUNCTIONs -----

CREATE OR REPLACE FUNCTION public.m_knn1(
	text[],
	mpoint,
	geometry,
	integer)
    RETURNS  text[]
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE STRICT 
AS $BODY$
DECLARE
	f_mgeometry			alias for $2;
	f_double			alias for $3;
	f_k					alias for $4;
	agg					alias for $1;
	f_mgeometry_segtable_name	char(200);
	results				text;
	sql					text;
	cnt				    integer;
	trajid				integer;
	mpid                integer;
BEGIN
	
	sql := 'SELECT f_segtableoid  FROM mgeometry_columns WHERE  f_segtableoid = ' ||quote_literal(f_mgeometry.segid);
	EXECUTE sql INTO trajid;
	sql := 'SELECT f_mgeometry_segtable_name  FROM mgeometry_columns WHERE f_segtableoid = ' ||quote_literal(trajid );
	EXECUTE sql INTO f_mgeometry_segtable_name;

	sql :='SELECT count(*) FROM querypoints p WHERE ST_equals($1,p.geom)';
	EXECUTE sql INTO cnt USING f_double;
		raise notice 'SQL: , param1 is %',cnt;
	IF (cnt = 0 ) THEN
			sql := 'WITH results AS(SELECT  mp.segid as segid, $1 <-> mp.trajectory  as min FROM ' || (f_mgeometry_segtable_name) ||' mp  '||' ORDER BY min LIMIT '||(f_k) ||') SELECT '||quote_literal('{') ||' || array_agg(segid)::text  ||'
         	 || quote_literal('}') ||' FROM results';	
			EXECUTE sql INTO results USING f_double;
			IF ( cardinality(agg) = 0) THEN
				RETURN array_append(agg,results);
			ELSE
				IF (array_position(agg,results) IS NOT NULL) THEN
				 	RETURN agg;
				ELSE
					RETURN array_append(agg,results);
				END IF;
			END IF;
	ELSE
			sql := 'WITH results AS(SELECT  qp.pointid as pointid, qp.geom <-> mp.trajectory  as min FROM ' || (f_mgeometry_segtable_name) ||' mp  '||',querypoints qp WHERE segid ='||(f_mgeometry.moid)|| ' ORDER BY min LIMIT '||(f_k) ||') SELECT '||quote_literal('{') ||' || array_agg(pointid)::text  ||'
         	       || quote_literal('}') ||' FROM results';
			raise notice 'SQL: , param1 is %',sql;
			EXECUTE sql INTO results USING f_mgeometry_segtable_name,f_mgeometry.moid,f_k ;
			RETURN array_append(agg,results);
	END IF;	
END
$BODY$;
ALTER FUNCTION public.m_knn1(text[],mpoint, geometry,integer)
    OWNER TO postgres;
    
CREATE OR REPLACE FUNCTION public.m_knn1_distance(
	text[],
	mpoint,
	geometry,
	integer)
    RETURNS  text[]
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE STRICT 
AS $BODY$
DECLARE
	f_mgeometry			alias for $2;
	f_double			alias for $3;
	f_k					alias for $4;
	agg					alias for $1;
	f_mgeometry_segtable_name	char(200);
	results				text;
	sql					text;
	cnt				    integer;
	trajid				integer;
	mpid                integer;
BEGIN
	
	sql := 'SELECT f_segtableoid  FROM mgeometry_columns WHERE  f_segtableoid = ' ||quote_literal(f_mgeometry.segid);
	EXECUTE sql INTO trajid;
	sql := 'SELECT f_mgeometry_segtable_name  FROM mgeometry_columns WHERE f_segtableoid = ' ||quote_literal(trajid );
	EXECUTE sql INTO f_mgeometry_segtable_name;

	sql :='SELECT count(*) FROM querypoints p WHERE ST_equals($1,p.geom)';
	EXECUTE sql INTO cnt USING f_double;
		raise notice 'SQL: , param1 is %',cnt;
	IF (cnt = 0 ) THEN
			sql := 'WITH results AS(SELECT  mp.segid as segid, $1 <-> mp.trajectory  as min FROM ' || (f_mgeometry_segtable_name) ||' mp  '||' ORDER BY min LIMIT '||(f_k) ||') SELECT '||quote_literal('{') ||' || array_agg(segid)::text  ||'
         	 || quote_literal(',')|| '||  array_agg(min)::text ||'|| quote_literal('}') ||' FROM results';	
			EXECUTE sql INTO results USING f_double;
			IF ( cardinality(agg) = 0) THEN
				RETURN array_append(agg,results);
			ELSE
				IF (array_position(agg,results) IS NOT NULL) THEN
				 	RETURN agg;
				ELSE
					RETURN array_append(agg,results);
				END IF;
			END IF;
	ELSE
	        sql := 'WITH results AS(SELECT  qp.pointid as pointid, qp.geom <-> mp.trajectory  as min FROM ' || (f_mgeometry_segtable_name) ||' mp  '||',querypoints qp WHERE segid ='||(f_mgeometry.moid)|| ' ORDER BY min LIMIT '||(f_k) ||') SELECT '||quote_literal('{') ||' || array_agg(pointid)::text  ||'
         	 || quote_literal(',')|| '||  array_agg(min)::text ||'|| quote_literal('}') ||' FROM results';	
			raise notice 'SQL: , param1 is %',sql;
			EXECUTE sql INTO results ;
			RETURN array_append(agg,results);
	END IF;	
END
$BODY$;
ALTER FUNCTION public.m_knn1_distance(text[],mpoint, geometry,integer)
    OWNER TO postgres;

CREATE OR REPLACE FUNCTION public.m_knn1(
	text[],
	mpoint,
	mpoint,
	integer)
    RETURNS  text[]
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE STRICT 
AS $BODY$
DECLARE
	f_agg				alias for $1;
	f_mpoint			alias for $2;
	f_mpoint2			alias for $3;
	f_k					alias for $4;
	f_mpoint_segtable_name	char(200);
	f_mpoint_segtable_name2	char(200);
	tmp_table				text;
	sql_text				text;
	session_key 			text;
	session_value			text;
	results					text;
	aanull					text;
BEGIN
	f_mpoint_segtable_name := temp_mgeometry_table(f_mpoint);	
	f_mpoint_segtable_name2 := temp_mgeometry_table(f_mpoint2);
	IF (f_mpoint2.moid > 1 ) THEN
	RETURN f_agg;
	END IF;

	sql_text := 'WITH results AS(SELECT b.mpid,a.trajectory <-> b.trajectory as min FROM '|| (f_mpoint_segtable_name) ||' a, ' || (f_mpoint_segtable_name2)|| ' b WHERE a.mpid < b.mpid  AND  a.timerange && b.timerange AND a.segid = '||(f_mpoint.moid) ||' ORDER BY min LIMIT '|| (f_k) || ') SELECT array_agg(mpid)::text FROM results';
	EXECUTE sql_text INTO results;
	raise notice 'SQL: , results is %',results;
	RETURN array_append(f_agg,results);	
END
$BODY$;
ALTER FUNCTION public.m_knn1(text[],mpoint, mpoint,integer)
    OWNER TO postgres;

CREATE OR REPLACE FUNCTION public.m_knn1_distance(
	text[],
	mpoint,
	mpoint,
	integer)
    RETURNS  text[]
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE STRICT 
AS $BODY$
DECLARE
	f_agg				alias for $1;
	f_mpoint			alias for $2;
	f_mpoint2			alias for $3;
	f_k					alias for $4;
	f_mpoint_segtable_name	char(200);
	f_mpoint_segtable_name2	char(200);
	tmp_table				text;
	sql_text				text;
	session_key 			text;
	session_value			text;
	results					text;
	aanull					text;
BEGIN
	f_mpoint_segtable_name := temp_mgeometry_table(f_mpoint);	
	f_mpoint_segtable_name2 := temp_mgeometry_table(f_mpoint2);
	IF (f_mpoint2.moid > 1 ) THEN
	RETURN f_agg;
	END IF;

	sql_text := 'WITH results AS(SELECT b.mpid,a.trajectory <-> b.trajectory as min FROM '|| (f_mpoint_segtable_name) ||' a, ' || (f_mpoint_segtable_name2)|| ' b WHERE a.mpid < b.mpid  AND  a.timerange && b.timerange AND a.segid = '||(f_mpoint.moid) ||' ORDER BY min LIMIT '|| (f_k) || ') SELECT '||quote_literal('{') ||' || array_agg(mpid)::text  ||'
         	 || quote_literal(',')|| '||  array_agg(min)::text ||'|| quote_literal('}') ||' FROM results';
	EXECUTE sql_text INTO results;
	raise notice 'SQL: , results is %',results;
	RETURN array_append(f_agg,results);	
END
$BODY$;
ALTER FUNCTION public.m_knn1_distance(text[],mpoint, mpoint,integer)
    OWNER TO postgres;

CREATE OR REPLACE FUNCTION public.m_knn1(
	text[],
	mpoint,
	text,
	integer)
    RETURNS  text[]
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE STRICT 
AS $BODY$
DECLARE
	f_mgeometry			alias for $2;
	f_text			alias for $3;
	f_k					alias for $4;
	agg					alias for $1;
	ff_text				geometry;
	f_mgeometry_segtable_name	char(200);
	results				text;
	sql					text;
	cnt				    integer;
	trajid				integer;
	mpid                integer;
BEGIN
	
	sql := 'SELECT f_segtableoid  FROM mgeometry_columns WHERE  f_segtableoid = ' ||quote_literal(f_mgeometry.segid);
	EXECUTE sql INTO trajid;
	sql := 'SELECT f_mgeometry_segtable_name  FROM mgeometry_columns WHERE f_segtableoid = ' ||quote_literal(trajid );
	EXECUTE sql INTO f_mgeometry_segtable_name;
	raise notice '%',f_text;
	IF (f_mgeometry.moid > 1 ) THEN
		RETURN agg;
	END IF;
	sql := 'WITH results AS(SELECT  mp.segid as segid, ST_GeomFromText($1,4326) <-> mp.trajectory  as min FROM ' || (f_mgeometry_segtable_name) ||' mp  '|| ' ORDER BY min LIMIT '||(f_k) ||') SELECT '||quote_literal('{') ||' || array_agg(segid)::text  ||'
         	       || quote_literal('}') ||' FROM results';
	EXECUTE sql INTO results USING f_text ;	
	raise notice '%',results;
	RETURN array_append(agg,results);

END
$BODY$;
ALTER FUNCTION public.m_knn1(text[],mpoint, text,integer)
    OWNER TO postgres;
	
CREATE OR REPLACE FUNCTION public.m_knn1_distance(
	text[],
	mpoint,
	text,
	integer)
    RETURNS  text[]
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE STRICT 
AS $BODY$
DECLARE
	f_mgeometry			alias for $2;
	f_text			alias for $3;
	f_k					alias for $4;
	agg					alias for $1;
	ff_text				geometry;
	f_mgeometry_segtable_name	char(200);
	results				text;
	sql					text;
	cnt				    integer;
	trajid				integer;
	mpid                integer;
BEGIN
	
	sql := 'SELECT f_segtableoid  FROM mgeometry_columns WHERE  f_segtableoid = ' ||quote_literal(f_mgeometry.segid);
	EXECUTE sql INTO trajid;
	sql := 'SELECT f_mgeometry_segtable_name  FROM mgeometry_columns WHERE f_segtableoid = ' ||quote_literal(trajid );
	EXECUTE sql INTO f_mgeometry_segtable_name;
	raise notice '%',f_text;
	IF (f_mgeometry.moid > 1 ) THEN
		RETURN agg;
	END IF;
	sql := 'WITH results AS(SELECT  mp.segid as segid, ST_GeomFromText($1,4326) <-> mp.trajectory  as min FROM ' || (f_mgeometry_segtable_name) ||' mp  '|| ' ORDER BY min LIMIT '||(f_k) ||')SELECT '||quote_literal('{') ||' || array_agg(segid)::text  ||'
         	 || quote_literal(',')|| '||  array_agg(min)::text ||'|| quote_literal('}') ||' FROM results';	
	EXECUTE sql INTO results USING f_text ;	
	raise notice '%',results;
	RETURN array_append(agg,results);

END
$BODY$;
ALTER FUNCTION public.m_knn1_distance(text[],mpoint, text,integer)
    OWNER TO postgres;

----- AGGREGATE FINALFUNC FUNCTION -----

CREATE OR REPLACE FUNCTION results(aa text[])
RETURNS  text[] 
AS $BODY$
BEGIN
      RETURN  unnest(aa);
END;
$BODY$
 LANGUAGE 'plpgsql' STRICT;
 
 DROP FUNCTION results(text[])
