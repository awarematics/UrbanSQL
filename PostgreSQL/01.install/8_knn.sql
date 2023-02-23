CREATE TYPE kpq_element AS (
    mpid mpoint,
    double_precision double precision
);

CREATE OR REPLACE TYPE kpq AS (
    heap kpq_element[],
    size integer
);

CREATE OR REPLACE FUNCTION kpq_init()
RETURNS kPQ
AS $$
DECLARE
    pq kPQ;
BEGIN
    pq.heap := NULL;
    pq.size := 0;
    RETURN pq;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION kpq_offer(pq kPQ, elt kpq_element)
RETURNS kPQ
AS $$
DECLARE
    child_pos INTEGER;
    parent_pos INTEGER;
    temp_elt kpq_element;
BEGIN
    -- 添加新元素到堆的末尾
    pq.heap := pq.heap || elt;
    child_pos := array_length(pq.heap, 1);
    pq.size = pq.size+ 1; 
    -- 向上调整新元素，直到其父节点小于该元素
    LOOP
        parent_pos := child_pos / 2;
        IF pq.heap[child_pos].double_precision > pq.heap[parent_pos].double_precision THEN
            temp_elt := pq.heap[child_pos];
            pq.heap[child_pos] := pq.heap[parent_pos];
            pq.heap[parent_pos] := temp_elt;
            child_pos := parent_pos;
        ELSE
            EXIT;
        END IF;
        -- 如果新元素到达堆顶，退出循环
        IF parent_pos <= 1 THEN
            EXIT;
        END IF;
    END LOOP;

    RETURN pq;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION kpq_pop(kpq kPQ)
RETURNS kPQ AS $$
DECLARE
    elem kpq_element;
    i INT;
    j INT;
BEGIN
    elem := kpq.heap[1];
    kpq.heap[1] := kpq.heap[array_length(kpq.heap, 1)];
    i := 1;
    WHILE i*2 <= array_length(kpq.heap, 1) LOOP
        j := i*2;
        IF j+1 <= array_length(kpq.heap, 1) AND kpq.heap[j+1].double_precision > kpq.heap[j].double_precision THEN
            j := j+1;
        END IF;
        IF kpq.heap[j].double_precision > kpq.heap[i].double_precision THEN
            kpq.heap[i] := kpq.heap[j];
            kpq.heap[j] := elem;
            i := j;
        ELSE
            EXIT;
        END IF;
    END LOOP;
    kpq.heap := kpq.heap[1:array_length(kpq.heap, 1)-1];
    kpq.size = kpq.size -1;
    RETURN kpq;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION kpq_peek(pq kPQ)
RETURNS kpq_element
AS $$
BEGIN
    IF pq.heap IS NULL THEN
        RAISE EXCEPTION 'kPQ is empty';
    END IF;
    
    RETURN pq.heap[1];
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION kpq_size(pq kPQ) RETURNS integer AS $$
BEGIN 
    RETURN pq.size;
END;
$$ LANGUAGE plpgsql;



CREATE AGGREGATE m_knn_naive(mpoint,geometry,integer)
(
  SFUNC = m_naive_knn,
  STYPE = kpq,	 
  INITCOND = '("{""(,)""}",1)'
);


CREATE OR REPLACE FUNCTION public.m_naive_knn(
	kpq,
	mpoint,
	geometry,
	integer)
    RETURNS  kpq
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE STRICT 
AS $BODY$
DECLARE
	kpq_			alias for $1;
	f_mpoint		alias for $2;
	f_geo			alias for $3;
	f_k				alias for $4;
	traj			geometry;
	dis			    double precision;
	tempdis         double precision;
	kpq_ele			kpq_element;
BEGIN
	traj := m_spatial(f_mpoint);
	dis := ST_Distance(traj,f_geo);
	IF (kpq_size(kpq_)) < f_k THEN
	  kpq_ := kpq_offer(kpq,f_mpoint,dis);
	ELSE 
	  kpq_ele:= kpq_peek(kpq);
	  tempdis:= kpq_ele.double_precision;
	  IF dis < tempdis THEN
	    kpq_ := kpq_pop(kpq_);
		kpq_ := kpq_offer(kpq_,f_mpoint,dis);
	  END IF;
	END IF;  
   RETURN kpq_;
END
$BODY$;
ALTER FUNCTION public.m_naive_knn(kpq,mpoint, geometry,integer)
    OWNER TO postgres;

CREATE AGGREGATE m_knn_deferred(mpoint,geometry,integer)
(
  SFUNC = m_deferred_knn_sfunc,
  STYPE = mpoint_array,	 
  FINALFUNC = m_deferred_knn_final,
  INITCOND = '({},,)'
);

CREATE TYPE mpoint_array AS (
    mpoint_inside_array mpoint[],
    geo geometry,
    k   integer
);


CREATE OR REPLACE FUNCTION public.m_deferred_knn_sfunc(
	mpoint_array,
	mpoint,
	geometry,
	integer)
    RETURNS  mpoint_array
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE STRICT 
AS $BODY$
DECLARE
   	mpoint_a			    alias for $1;
	mpoint_inside_a         mpoint[];
	mpoint_element			alias for $2;
	geo			            alias for $3;
	kth			            alias for $4;
BEGIN
	 mpoint_inside_a := mpoint_a.mpoint_inside_array;
	 mpoint_inside_a := array_append(mpoint_inside_a,mpoint_element);
	 mpoint_a.mpoint_inside_array := mpoint_inside_a;
	 mpoint_a.geo := geo;
	 mpoint_a.k := k;
   RETURN mpoint_a;
END
$BODY$;
ALTER FUNCTION public.m_deferred_knn_sfunc(mpoint_array,mpoint, geometry,integer)
    OWNER TO postgres;

CREATE OR REPLACE FUNCTION public.m_deferred_knn_final(
	mpoint_array)
    RETURNS  kpq
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE STRICT 
AS $BODY$
DECLARE
   mpoint_inside_array	 mpoint[];
   geo 					 geometry;
   k					 integer;
   pt                    mpoint;
   kpq_                   kPQ;
   traj			    geometry;
	dis			    double precision;
	tempdis         double precision;
	kpq_ele			kpq_element;
BEGIN
   geo := mpoint_array.geo;
   k := mpoint_array.k;
   FOREACH pt IN ARRAY mpoint_array.mpoint_inside_array
    LOOP
        traj := m_spatial(pt);
	dis := ST_Distance(traj,geo);
	IF (kpq_size(kpq_)) < k THEN
	  kpq_ := kpq_offer(kpq,pt,dis);
	ELSE 
	  kpq_ele:= kpq_peek(kpq);
	  tempdis:= kpq_ele.double_precision;
	  IF dis < tempdis THEN
	    kpq_ := kpq_pop(kpq_);
		kpq_ := kpq_offer(kpq_,pt,dis);
	  END IF;
	END IF;  
    END LOOP;
	RETURN kpq_;
END
$BODY$;
ALTER FUNCTION public.m_deferred_knn_final(mpoint_array)
    OWNER TO postgres;

	
CREATE AGGREGATE m_knn_materialized(mpoint,geometry,integer)
(
  SFUNC = m_materialized_knn_sfunc,
  STYPE = kpq,	 
  INITCOND = '("{""(,)""}",1)'
);


CREATE OR REPLACE FUNCTION public.m_materialized_knn_sfunc(
	kpq,
	mpoint,
	geometry,
	integer)
    RETURNS  kpq
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE STRICT 
AS $BODY$
DECLARE
	kpq_			alias for $1;
	f_mpoint		alias for $2;
	f_geo			alias for $3;
	f_k				alias for $4;
	numofmater      integer;
	sql_cq          text;
	f_range			integer;
	dis			    double precision;
	tempdis         double precision;
	kpq_ele			kpq_element;
	mpid_array		mpoint[];
	mpid_element	mpoint;
	f_mgeometry_segtable_name	char(200);
	traj			    geometry;
	session_key 			text;
	session_value			text;
	tmp_table			text;
	sql_text            text;
BEGIN
	sql_cq := current_query();
	f_range := 100;
	f_mgeometry_segtable_name := temp_mgeometry_table(f_mpoint);
	
	session_key := 'temp.knn.column';
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
		sql_text := sql_text || ' WHERE ST_DWithin($1,trajectory,$2)  ;';
		EXECUTE sql_text USING f_geo,f_range;
	END IF;	
	
	sql_text := 'SELECT COUNT(*) FROM temp_table WHERE mpid = ' || f_mgeometry.moid;
    EXECUTE sql_text INTO numofmater;
	WHILE numofmater < f_k LOOP
	   sql_text := 'UPDATE '|| tmp_table||' as ';
	   sql_text := sql_text || ' SELECT DISTINCT mpid FROM ' || f_mgeometry_segtable_name;
	   sql_text := sql_text || ' WHERE ST_DWithin($1,trajectory,$2 * 1.5)  ;';
	   EXECUTE sql_text INTO mpid_array USING f_geo,f_range;
	   sql_text := 'SELECT COUNT(*) FROM temp_table WHERE mpid = ' || f_mgeometry.moid;
    EXECUTE sql_text INTO numofmater;
	END LOOP;
	
	FOREACH mpid_element IN ARRAY mpid_array
	LOOP
	    traj := m_spatial(pt);
	    dis := ST_Distance(traj,geo);
	    IF (kpq_size(kpq_)) < k THEN
	       kpq_ := kpq_offer(kpq,pt,dis);
	    ELSE 
	       kpq_ele:= kpq_peek(kpq);
	       tempdis:= kpq_ele.double_precision;
	       IF dis < tempdis THEN
	          kpq_ := kpq_pop(kpq_);
		      kpq_ := kpq_offer(kpq_,pt,dis);
	       END IF;
	    END IF;  
	END LOOP;
	RETURN kpq_;
END
$BODY$;
ALTER FUNCTION public.m_materialized_knn_sfunc(kpq,mpoint, geometry,integer)
    OWNER TO postgres;

SELECT * FROM mgeometry_columns

SELECT kpq_init();
SELECT current_query();
	
EXPLAIN ANALYZE  SELECT kpq_init();
CREATE EXTENSION pg_stat_statements;

SELECT *
FROM  pg_stat_statements


SELECT kpq_init();

SELECT kpq_peek(kpq_insert(kpq_insert(kpq_insert(kpq_init(), ROW((1,347620,null,null)::mpoint, 4.0)), ROW((2,347620,null,null)::mpoint, 6.0)), ROW((3,347620,null,null)::mpoint, 3.0)));

SELECT kpq_insert(kpq_init(),  ((1,347620,null,null)::mpoint, 1.0)::kpq_element );

SELECT kpq_pop(kpq_insert(kpq_insert(kpq_insert(kpq_init(), ROW((1,347620,null,null)::mpoint, 1.0)), ROW((2,347620,null,null)::mpoint, 2.0)), ROW((3,347620,null,null)::mpoint, 3.0)));


