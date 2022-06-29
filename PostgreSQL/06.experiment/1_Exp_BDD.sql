
CREATE TABLE mgeometry_columns
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


-----basic temporal query no index  
explain analyze
select carid, mpoint, m_time(mpoint)
from bdd10k 
where m_tintersects_noindex(mpoint, '(1504010956999,1504012995999)'::int8range)
and carid <2000;
---Execution Time: 789.214 ms

-----no temporary table with index
explain analyze
select carid, mpoint, m_time(mpoint)
from bdd10k
where m_tintersects_index(mpoint, '(1504010956999,1504012995999)'::int8range )
and carid <2000;
---Execution Time: 789.214 ms


-----temporal table with index
explain analyze
select carid, mpoint, m_time(mpoint)
from bdd10k 
where m_tintersects_materialized(mpoint, '(1504010956999,1504012995999)'::int8range)
and carid <2000;
---Execution Time: 476.073 ms



-----basic spatial query no index  
explain analyze
select carid, mpoint, m_spatial(mpoint)
from bdd10k 
where m_intersects_noindex(mpoint, 'LINESTRING(40 -73,40.7416693959765 -73.9897693321798,40.7416693959765 -73.9897693321798)'::geometry,  '(1414010956999,1504012995999)'::int8range)
and carid <8000;
---Execution Time: 789.214 ms

-----no spatial table with index
explain analyze
select carid, mpoint, m_spatial(mpoint)
from bdd10k
where m_intersects_index(mpoint, 'LINESTRING(40 -73,40.7416693959765 -73.9897693321798,40.7416693959765 -73.9897693321798)'::geometry, '(1414010956999,1504012995999)'::int8range)
and carid <8000;
---Execution Time: 789.214 ms


-----spatial table with index
explain analyze
select carid, mpoint, m_spatial(mpoint)
from bdd10k 
where m_intersects_materialized(mpoint, 'LINESTRING(40 -73,40.7416693959765 -73.9897693321798,40.7416693959765 -73.9897693321798)'::geometry, '(1414010956999,1504012995999)'::int8range)
and carid <8000;
---Execution Time: 476.073 ms






-----basic spatial query no index  
explain analyze
select carid, mpoint, m_spatial(mpoint)
from bdd10k 
where m_sintersects_noindex(mpoint, 'LINESTRING(40 -73,40.7416693959765 -73.9897693321798,40.7416693959765 -73.9897693321798)'::geometry)
and carid <2000;
---Execution Time: 789.214 ms

-----no spatial table with index
explain analyze
select carid, mpoint, m_spatial(mpoint)
from bdd10k
where m_sintersects_index(mpoint, 'LINESTRING(40 -73,40.7416693959765 -73.9897693321798,40.7416693959765 -73.9897693321798)'::geometry)
and carid <2000;
---Execution Time: 789.214 ms


-----spatial table with index
explain analyze
select carid, mpoint, m_spatial(mpoint)
from bdd10k 
where m_sintersects_materialized(mpoint, 'LINESTRING(40 -73,40.7416693959765 -73.9897693321798,40.7416693959765 -73.9897693321798)'::geometry)
and carid <10000;
---Execution Time: 476.073 ms






---------------------------------------------distance join query




explain analyze
select carid from bdd100k_seg where m_mindistance_noindex(mpoint,'POINT (-73.9917157777343 40.7424697420008)'::geometry, 100.0) and carid<2000;

-----no spatial table with index
explain analyze
select carid from bdd100k_seg where  carid<2000 and m_mindistance_index(mpoint,'POINT (-73.9917157777343 40.7424697420008)'::geometry, 100.0);
---Execution Time: 789.214 ms


-----spatial table with index
explain analyze
select carid from bdd100k_seg where carid<2000 and  m_mindistance_materialized(mpoint,'POINT (-73.9917157777343 40.7424697420008)'::geometry, 100.0);
---Execution Time: 476.073 ms



explain analyze
select a.carid, b.carid from bdd100k_seg a, bdd100k b where m_mindistance_noindex(a.mpoint, b.mpoint, 100.0) and a.carid<100 and b.carid>9800;
---Execution Time: 74472.477 ms


explain analyze
select a.carid, b.carid from bdd100k_seg a, bdd100k b where m_mindistance_index(a.mpoint, b.mpoint, 100.0) and a.carid<100 and b.carid>9800;

----Execution Time: 10742.828 ms

explain analyze
select a.carid, b.carid from bdd100k_seg a, bdd100k b 
where m_mindistance_materialized(a.mpoint, b.mpoint, 100.0, '[1, 100]','[9800, 10000]') and a.carid<100 and b.carid>9800;







