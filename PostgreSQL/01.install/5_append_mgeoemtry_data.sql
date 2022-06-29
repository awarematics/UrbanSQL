/*
---mpoint
 			mpid        integer,
            segid       integer,
			mbr			geometry,
			timerange		int8range,
            datetimes    	bigint[],
            geo        point[]
			
---mvideo
 			mpid        integer,
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
    
    
    
    
    
  