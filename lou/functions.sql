



--m_astext(mpoint) text
	


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
	
	

CREATE OR REPLACE FUNCTION public.m_snapshots(
	mpoint, double precision)
    RETURNS setof geometry
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE STRICT 
AS $BODY$
DECLARE
	f_mgeometry			alias for $1;
	f_double			alias for $2;
	sql					text;
	mpid                integer;
	results				text;
BEGIN
    mpid := f_mgeometry.moid;	
		sql := 'select m_snapshot(wkttraj, ' || (f_double)::bigint ||') from mpoint_120324 where mpid =' ||(mpid)|| ' AND timerange @>'|| (f_double)::bigint;
		EXECUTE sql into results;
    	RETURN QUERY SELECT st_geomfromtext(results);
END
$BODY$;
ALTER FUNCTION public.m_snapshots(mpoint, double precision)
    OWNER TO postgres;	
    
----------------------------------------------------------------------------------------------------------------------------------------------------------------
---Q4: m_sintersects()   



CREATE OR REPLACE FUNCTION public.m_sintersects(
	mpoint, geometry)
    RETURNS setof boolean
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE STRICT 
AS $BODY$
DECLARE
	f_mgeometry			alias for $1;
	f_geometry		alias for $2;
	sql					text;
	mpid                integer;
	res					geometry;
	mbr					geometry;
BEGIN
    mpid := f_mgeometry.moid;		
	sql := 'select mbr from mpoint_120324 where mpid = ' ||(mpid);
	execute sql into mbr;
	IF(ST_Intersects(mbr, f_geometry)) THEN
		sql := 'select trajectory from mpoint_120324 where mpid = ' ||(mpid);
		execute sql into res;
    	RETURN QUERY EXECUTE 'SELECT ' ||ST_Intersects(res, f_geometry);
	END IF;
END
$BODY$;
ALTER FUNCTION public.m_sintersects(mpoint, geometry)
    OWNER TO postgres;	

----------------------------------------------------------------------------------------------------------------------------------------------------------------
---Q5: m_mindistance()   

	
CREATE OR REPLACE FUNCTION public.m_mindistance(
	mpoint,
	mpoint)
    RETURNS setof double precision
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE STRICT 
AS $BODY$
DECLARE
	f_mgeometry1			alias for $1;
	f_mgeometry2			alias for $2;
	f_mgeometry_segtable_name	char(200);
	f_mgeometry_segtable_name2	char(200);
	sql					text;
	mpid                integer;
	mpid2                integer;
	mpoint1				geometry;
	mpoint2				geometry;
BEGIN
    mpid := f_mgeometry1.moid;	
    mpid2 := f_mgeometry2.moid;	
			sql := 'select trajectory from mpoint_120324 where mpid = ' ||(mpid2);
   			EXECUTE sql INTO mpoint2;
			sql := 'select trajectory from mpoint_120324 where mpid = ' ||(mpid);
    		EXECUTE sql INTO mpoint1;
    		RETURN QUERY select MIN(st_distance(mpoint1, mpoint2));
END
$BODY$;
ALTER FUNCTION public.m_mindistance(mpoint, mpoint)
    OWNER TO postgres;
	
----------------------------------------------------------------------------------------------------------------------------------------------------------------
---Q6: m_dwithin() 	



CREATE OR REPLACE FUNCTION public.m_dwithin(mpoint, mpoint, double precision)
RETURNS setof boolean AS 
$BODY$
DECLARE
	f_mgeometry1			alias for $1;
	f_mgeometry2			alias for $2;
	f_mgeometry3			alias for $3;
	sql					text;
	mpid                integer;
	mpid2                integer;
	mpoint1				geometry;
	mpoint2				geometry;
	traj1				geometry;
	traj2				geometry;
BEGIN
    mpid := f_mgeometry1.moid;	
    mpid2 := f_mgeometry2.moid;	
	sql := 'select mbr from mpoint_120324 where mpid = ' ||(mpid2);
   	EXECUTE sql INTO mpoint2;
	sql := 'select mbr from mpoint_120324 where mpid = ' ||(mpid);
    EXECUTE sql INTO mpoint1;
	
	IF(mpoint1 && ST_expand(mpoint2, 10)) THEN	
		sql := 'select trajectory from mpoint_120324 where mpid = ' ||(mpid2);
   		EXECUTE sql INTO traj1;
		sql := 'select trajectory from mpoint_120324 where mpid = ' ||(mpid);
    	EXECUTE sql INTO traj2;
    	RETURN QUERY select traj1 && ST_expand(traj2, 10);
	END IF;
END
$BODY$
	LANGUAGE plpgsql VOLATILE STRICT
	COST 100;

----------------------------------------------------------------------------------------------------------------------------------------------------------------
---Q7: m_eventtime() 	


CREATE OR REPLACE FUNCTION public.m_eventtime(
	mpoint,
	geometry)
    RETURNS setof double precision
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE STRICT 
AS $BODY$
DECLARE
	f_mgeometry1			alias for $1;
	f_mgeometry2			alias for $2;
	sql					text;
	mpid                integer;
	res					text;
BEGIN
    mpid := f_mgeometry1.moid;
	res := st_astext(f_mgeometry2);
	sql := 'select m_eventtime(wkttraj, '||quote_literal(res)||') from mpoint_120324 where mpid = ' ||(mpid);
    RETURN QUERY EXECUTE sql;
END
$BODY$;
ALTER FUNCTION public.m_eventtime(mpoint, geometry)
    OWNER TO postgres;




----------------------------------------------------------------------------------------------------------------------------------------------------------------
---Q8: m_timeatcummulative()  	m_slice()	



CREATE OR REPLACE FUNCTION public.m_timeatcummulative(text)
RETURNS double precision AS 
$BODY$
DECLARE
	f_mgeometry			alias for $1;
	sql			text;
	res		double precision;
BEGIN
  sql:= 'SELECT ' || timeAtCummulativeDistance(f_mgeometry);
   execute sql into res;
   return res;
END
$BODY$
	LANGUAGE plpgsql VOLATILE STRICT
	COST 100;
	

CREATE OR REPLACE FUNCTION public.m_slice(
	mpoint,
	int8range)
    RETURNS setof text
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE STRICT 
AS $BODY$
DECLARE
	f_mgeometry			alias for $1;
	f_period			alias for $2;
	sql					text;
	mpid                integer;
	res					text;
	queryrange			int8range;
BEGIN
    mpid := f_mgeometry.moid;	
	sql := 'select timerange from mpoint_120324 where mpid = ' ||(mpid);
	EXECUTE sql into queryrange;
	IF(queryrange && f_period ) THEN
		IF (queryrange <@ f_period) THEN
			sql := 'select wkttraj from mpoint_120324 where mpid = ' ||(mpid) || ' AND timerange <@ '|| quote_literal(f_period); 
				RETURN QUERY EXECUTE sql;
	--	ELSE 
		--	sql := 'select m_slice(wkttraj, '||quote_literal(f_period)||')  from mpoint_120324 where mpid = ' ||(mpid) || 'AND timerange && '|| quote_literal(f_period);
			-- RETURN QUERY EXECUTE sql;
		END IF;
	END IF;
END
$BODY$;
ALTER FUNCTION public.m_slice(mpoint, int8range)
    OWNER TO postgres;
    
----------------------------------------------------------------------------------------------------------------------------------------------------------------
---Q9: m_timeatcummulative()  	m_slice()	

----------------------------------------------------------------------------------------------------------------------------------------------------------------
---Q10: ()  	()	

	
