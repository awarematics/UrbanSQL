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
 
 -- TEST AGGREGATE FUNCTION  --
 
SELECT m_knn(t.mt , ST_GeomFromText('POINT (11 1111)',4326), 3)
FROM trips t

SELECT m_knn(t.mt , p.geom, 3)
FROM trips t,querypoints p

SELECT m_knn(t1.mt , t2.mt, 3)
FROM trips t1,trips t2

SELECT m_knn_distance(t.mt , ST_GeomFromText('POINT (11 1111)',4326), 3)
FROM trips t

SELECT unnest(m_knn_distance(t.mt ,p.geom, 3))
FROM trips t,querypoint p

SELECT unnest(m_knn_distance(t1.mt , t2.mt, 3))
FROM trips t1,trips t2

SELECT m_knn(t.mt,'LINESTRING(0 0, 1 1)',3)
FROM Trips t

SELECT m_knn_distance(t.mt,'LINESTRING(0 0, 1 1)',3)
FROM Trips t

SELECT m_knn(t.mt,'POLYGON((75 29, 77 29, 77 29, 75 29))',3)
FROM Trips t

SELECT m_knn_distance(t.mt,'POLYGON((75 29, 77 29, 77 29, 75 29))',3)
FROM Trips t
